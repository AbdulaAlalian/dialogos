package com.github.dialogos.plugin.input.util;

import edu.cmu.sphinx.frontend.*;
import edu.cmu.sphinx.frontend.util.DataUtil;
import edu.cmu.sphinx.frontend.util.Utterance;

import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MicrophoneInputStream {
    private AudioFormat finalFormat;
    private AudioInputStream audioStream;
    private TargetDataLine audioLine;
    private BlockingQueue<Data> audioList;
    private Utterance currentUtterance;
    private boolean doConversion;
    private volatile boolean recording;
    private volatile boolean utteranceEndReached = true;
    private MicrophoneInputStream.RecordingThread recorder;

    // Configuration data

    private AudioFormat desiredFormat;
    private boolean closeBetweenUtterances;
    private boolean keepDataReference;
    private boolean signed;
    private boolean bigEndian;
    private int frameSizeInBytes;
    private int msecPerRead;
    private int selectedChannel;
    private String selectedMixerIndex;
    private String stereoToMono;
    private int sampleRate;
    private int audioBufferSize;

    /**
     * @param sampleRate sample rate of the data
     * @param bitsPerSample number of bits per value.
     * @param channels number of channels.
     * @param bigEndian the endianness of the data
     * @param signed whether the data is signed.
     * @param closeBetweenUtterances whether or not the microphone will release the audio between utterances.  On
     * certain systems (Linux for one), closing and reopening the audio does not work too well. The default is false for
     * Linux systems, true for others
     * @param msecPerRead the number of milliseconds of audio data to read each time from the underlying
     * Java Sound audio device.
     * @param keepLastAudio whether to keep the audio data of an utterance around until the next utterance
     * is recorded.
     * @param stereoToMono how to convert stereo audio to mono. Currently, the possible values are
     * "average", which averages the samples from at each channel, or "selectChannel", which chooses audio only from
     * that channel. If you choose "selectChannel", you should also specify which channel to use with the
     * "selectChannel" property.
     * @param selectedChannel the channel to use if the audio is stereo
     * @param selectedMixerIndex the mixer to use.  The value can be "default," (which means let the
     * AudioSystem decide), "last," (which means select the last Mixer supported by the AudioSystem), which appears to
     * be what is often used for USB headsets, or an integer value which represents the index of the Mixer.Info that is
     * returned by AudioSystem.getMixerInfo(). To get the list of Mixer.Info objects, run the AudioTool application with
     * a command line argument of "-dumpMixers".
     * @param audioBufferSize buffer size
     */
    public MicrophoneInputStream(int sampleRate, int bitsPerSample, int channels,
                      boolean bigEndian, boolean signed, boolean closeBetweenUtterances, int msecPerRead, boolean keepLastAudio,
                      String stereoToMono, int selectedChannel, String selectedMixerIndex, int audioBufferSize) {

        this.sampleRate = sampleRate;
        this.bigEndian = bigEndian;
        this.signed = signed;

        this.desiredFormat = new AudioFormat
                (sampleRate, bitsPerSample, channels, signed, bigEndian);

        this.closeBetweenUtterances = closeBetweenUtterances;
        this.msecPerRead = msecPerRead;
        this.keepDataReference = keepLastAudio;
        this.stereoToMono = stereoToMono;
        this.selectedChannel = selectedChannel;
        this.selectedMixerIndex = selectedMixerIndex;
        this.audioBufferSize = audioBufferSize;
    }

    public MicrophoneInputStream() {
        this.sampleRate = MicrophoneDefaultConfig.SAMPLE_RATE;
        this.bigEndian = MicrophoneDefaultConfig.BIG_ENDIAN;
        this.signed = MicrophoneDefaultConfig.SIGNED;

        this.desiredFormat = new AudioFormat(sampleRate, MicrophoneDefaultConfig.BITS_PER_SAMPLE,
                MicrophoneDefaultConfig.CHANNELS, signed, bigEndian);

        this.closeBetweenUtterances = MicrophoneDefaultConfig.CLOSE_BETWEEN_UTTERANCES;
        this.msecPerRead = MicrophoneDefaultConfig.MSEC_PER_READ;
        this.keepDataReference = MicrophoneDefaultConfig.KEEP_LAST_AUDIO;
        this.stereoToMono = MicrophoneDefaultConfig.STEREO_TO_MONO;
        this.selectedChannel = MicrophoneDefaultConfig.SELECT_CHANNEL;
        this.selectedMixerIndex = MicrophoneDefaultConfig.SELECT_MIXER;
        this.audioBufferSize = MicrophoneDefaultConfig.BUFFER_SIZE;
    }

    /**
     * Constructs a Microphone with the given InputStream.
     */
    public void initialize() {
        audioList = new LinkedBlockingQueue<>();

        DataLine.Info info
                = new DataLine.Info(TargetDataLine.class, desiredFormat);

        /* If we cannot get an audio line that matches the desired
         * characteristics, shoot for one that matches almost
         * everything we want, but has a higher sample rate.
         */
        if (!AudioSystem.isLineSupported(info)) {
            System.out.println(desiredFormat + " not supported");
            AudioFormat nativeFormat
                    = DataUtil.getNativeAudioFormat(desiredFormat,
                    getSelectedMixer());
            if (nativeFormat == null) {
                System.err.println("couldn't find suitable target audio format");
            } else {
                finalFormat = nativeFormat;

                /* convert from native to the desired format if supported */
                doConversion = AudioSystem.isConversionSupported
                        (desiredFormat, nativeFormat);

                if (doConversion) {
                    System.out.println
                            ("Converting from " + finalFormat.getSampleRate()
                                    + "Hz to " + desiredFormat.getSampleRate() + "Hz");
                } else {
                    System.out.println
                            ("Using native format: Cannot convert from " +
                                    finalFormat.getSampleRate() + "Hz to " +
                                    desiredFormat.getSampleRate() + "Hz");
                }
            }
        } else {
            System.out.println("Desired format: " + desiredFormat + " supported.");
            finalFormat = desiredFormat;
        }
    }


    /**
     * Gets the Mixer to use.  Depends upon selectedMixerIndex being defined.
     *
     *
     */
    private Mixer getSelectedMixer() {
        if (selectedMixerIndex.equals("default")) {
            return null;
        } else {
            Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
            if (selectedMixerIndex.equals("last")) {
                return AudioSystem.getMixer(mixerInfo[mixerInfo.length - 1]);
            } else {
                int index = Integer.parseInt(selectedMixerIndex);
                return AudioSystem.getMixer(mixerInfo[index]);
            }
        }
    }


    private TargetDataLine getAudioLine() {
        if (audioLine != null) {
            return audioLine;
        }

        /* Obtain and open the line and stream.
         */
        try {
            /* The finalFormat was decided in the initialize() method
             * and is based upon the capabilities of the underlying
             * audio system.  The final format will have all the
             * desired audio characteristics, but may have a sample
             * rate that is higher than desired.  The idea here is
             * that we'll let the processors in the front end (e.g.,
             * the FFT) handle some form of downsampling for us.
             */
            System.out.println("Final format: " + finalFormat);

            DataLine.Info info = new DataLine.Info(TargetDataLine.class,
                    finalFormat);

            /* We either get the audio from the AudioSystem (our
             * default choice), or use a specific Mixer if the
             * selectedMixerIndex property has been set.
             */
            Mixer selectedMixer = getSelectedMixer();
            if (selectedMixer == null) {
                audioLine = (TargetDataLine) AudioSystem.getLine(info);
            } else {
                audioLine = (TargetDataLine) selectedMixer.getLine(info);
            }

            /* Add a line listener that just traces
             * the line states.
             */
            audioLine.addLineListener(new LineListener() {
                public void update(LineEvent event) {
                    System.out.println("line listener " + event);
                }
            });
        } catch (LineUnavailableException e) {
            System.out.println("microphone unavailable " + e.getMessage());
        }

        return audioLine;
    }


    /**
     * Opens the audio capturing device so that it will be ready for capturing audio. Attempts to create a converter if
     * the requested audio format is not directly available.
     *
     * @return true if the audio capturing device is opened successfully; false otherwise
     */
    private boolean open() {
        TargetDataLine audioLine = getAudioLine();
        if (audioLine != null) {
            if (!audioLine.isOpen()) {
                System.out.println("open");
                try {
                    audioLine.open(finalFormat, audioBufferSize);
                } catch (LineUnavailableException e) {
                    System.err.println("Can't open microphone " + e.getMessage());
                    return false;
                }

                audioStream = new AudioInputStream(audioLine);
                if (doConversion) {
                    audioStream = AudioSystem.getAudioInputStream
                            (desiredFormat, audioStream);
                    assert (audioStream != null);
                }

                /* Set the frame size depending on the sample rate.
                 */
                float sec = msecPerRead / 1000.f;
                frameSizeInBytes =
                        (audioStream.getFormat().getSampleSizeInBits() / 8) *
                                (int) (sec * audioStream.getFormat().getSampleRate()) *
                                desiredFormat.getChannels();

                System.out.println("Frame size: " + frameSizeInBytes + " bytes");
            }
            return true;
        } else {
            System.err.println("Can't find microphone");
            return false;
        }
    }

    /**
     * Initializes and opens the microphone, used for the first time setup
     */
    public void initializeAndOpen() {
        this.initialize();
        this.open();
    }


    /**
     * Returns the format of the audio recorded by this Microphone. Note that this might be different from the
     * configured format.
     *
     * @return the current AudioFormat
     */
    public AudioFormat getAudioFormat() {
        return finalFormat;
    }


    /**
     * Returns the current Utterance.
     *
     * @return the current Utterance
     */
    public Utterance getUtterance() {
        return currentUtterance;
    }


    /**
     * Returns true if this Microphone is recording.
     *
     * @return true if this Microphone is recording, false otherwise
     */
    public boolean isRecording() {
        return recording;
    }


    /**
     * Starts recording audio. This method will return only when a START event is received, meaning that this Microphone
     * has started capturing audio.
     *
     * @return true if the recording started successfully; false otherwise
     */

    public synchronized boolean startRecording() {
        if (recording) {
            return false;
        }
        if (!open()) {
            return false;
        }
        utteranceEndReached = false;
        if (audioLine.isRunning()) {
            System.out.println("Whoops: audio line is running");
        }
        assert (recorder == null);
        audioList.clear();
        recorder = new MicrophoneInputStream.RecordingThread("Microphone");
        recorder.start();
        recording = true;
        return true;
    }

    /**
     * Stops recording audio. This method does not return until recording has been stopped and all data has been read
     * from the audio line.
     */
    public synchronized void stopRecording() {
        if (audioLine != null) {
            if (recorder != null) {
                recorder.stopRecording();
                recorder = null;
            }
            recording = false;
        }
    }

    /**
     * This Thread records audio, and caches them in an audio buffer.
     */
    class RecordingThread extends Thread {

        private boolean done;
        private volatile boolean started;
        private long totalSamplesRead;
        private final Object lock = new Object();


        /**
         * Creates the thread with the given name
         *
         * @param name the name of the thread
         */
        public RecordingThread(String name) {
            super(name);
        }


        /**
         * Starts the thread, and waits for recorder to be ready
         */
        @Override
        public void start() {
            started = false;
            super.start();
            waitForStart();
        }


        /**
         * Stops the thread. This method does not return until recording has actually stopped, and all the data has been
         * read from the audio line.
         */
        public void stopRecording() {
            audioLine.stop();
            try {
                synchronized (lock) {
                    while (!done) {
                        lock.wait();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // flush can not be called here because the audio-line might has been set to  null already by the mic-thread
//    	    audioLine.flush();
        }


        /**
         * Implements the run() method of the Thread class. Records audio, and cache them in the audio buffer.
         */
        @Override
        public void run() {
            totalSamplesRead = 0;
            System.out.println("started recording");

            if (keepDataReference) {
                currentUtterance = new Utterance
                        ("Microphone", audioStream.getFormat());
            }

            audioList.add(new DataStartSignal(sampleRate));
            System.out.println("DataStartSignal added");
            try {
                audioLine.start();
                while (!done) {
                    Data data = readData(currentUtterance);
                    if (data == null) {
                        done = true;
                        break;
                    }
                    audioList.add(data);
                }
                audioLine.flush();
                if (closeBetweenUtterances) {
                    /* Closing the audio stream *should* (we think)
                     * also close the audio line, but it doesn't
                     * appear to do this on the Mac.  In addition,
                     * once the audio line is closed, re-opening it
                     * on the Mac causes some issues.  The Java sound
                     * spec is also kind of ambiguous about whether a
                     * closed line can be re-opened.  So...we'll go
                     * for the conservative route and never attempt
                     * to re-open a closed line.
                     */
                    audioStream.close();
                    audioLine.close();
                    System.err.println("set to null");
                    audioLine = null;
                }
            } catch (IOException ioe) {
                System.out.println("IO Exception " + ioe.getMessage());
                ioe.printStackTrace();
            }
            long duration = (long)
                    (((double) totalSamplesRead /
                            (double) audioStream.getFormat().getSampleRate()) * 1000.0);

            audioList.add(new DataEndSignal(duration));
            System.out.println("DataEndSignal ended");
            System.out.println("stopped recording");

            synchronized (lock) {
                lock.notify();
            }
        }


        /**
         * Waits for the recorder to start
         */
        private synchronized void waitForStart() {
            // note that in theory we could use a LineEvent START
            // to tell us when the microphone is ready, but we have
            // found that some javasound implementations do not always
            // issue this event when a line  is opened, so this is a
            // WORKAROUND.

            try {
                while (!started) {
                    wait();
                }
            } catch (InterruptedException ie) {
                System.out.println("wait was interrupted");
            }
        }


        /**
         * Reads one frame of audio data, and adds it to the given Utterance.
         *
         * @param utterance
         * @return an Data object containing the audio data
         * @throws java.io.IOException
         */
        private Data readData(Utterance utterance) throws IOException {

            // Read the next chunk of data from the TargetDataLine.
            byte[] data = new byte[frameSizeInBytes];

            int channels = audioStream.getFormat().getChannels();
            long firstSampleNumber = totalSamplesRead / channels;

            int numBytesRead = audioStream.read(data, 0, data.length);

            //  notify the waiters upon start
            if (!started) {
                synchronized (this) {
                    started = true;
                    notifyAll();
                }
            }

            if (numBytesRead <= 0) {
                return null;
            }
            int sampleSizeInBytes =
                    audioStream.getFormat().getSampleSizeInBits() / 8;
            totalSamplesRead += (numBytesRead / sampleSizeInBytes);

            if (numBytesRead != frameSizeInBytes) {

                if (numBytesRead % sampleSizeInBytes != 0) {
                    throw new Error("Incomplete sample read.");
                }

                data = Arrays.copyOf(data, numBytesRead);
            }

            if (keepDataReference) {
                utterance.add(data);
            }

            double[] samples;

            if (bigEndian) {
                samples = DataUtil.bytesToValues
                        (data, 0, data.length, sampleSizeInBytes, signed);
            } else {
                samples = DataUtil.littleEndianBytesToValues
                        (data, 0, data.length, sampleSizeInBytes, signed);
            }

            if (channels > 1) {
                samples = convertStereoToMono(samples, channels);
            }

            return (new DoubleData
                    (samples, (int) audioStream.getFormat().getSampleRate(),
                            firstSampleNumber));
        }
    }


    /**
     * Converts stereo audio to mono.
     *
     * @param samples  the audio samples, each double in the array is one sample
     * @param channels the number of channels in the stereo audio
     */
    private double[] convertStereoToMono(double[] samples, int channels) {
        assert (samples.length % channels == 0);
        double[] finalSamples = new double[samples.length / channels];
        if (stereoToMono.equals("average")) {
            for (int i = 0, j = 0; i < samples.length; j++) {
                double sum = samples[i++];
                for (int c = 1; c < channels; c++) {
                    sum += samples[i++];
                }
                finalSamples[j] = sum / channels;
            }
        } else if (stereoToMono.equals("selectChannel")) {
            for (int i = selectedChannel, j = 0; i < samples.length;
                 i += channels, j++) {
                finalSamples[j] = samples[i];
            }
        } else {
            throw new Error("Unsupported stereo to mono conversion: " +
                    stereoToMono);
        }
        return finalSamples;
    }

    public InputStream getAudioInputStream() {
        return audioStream;
    }


    /**
     * Clears all cached audio data.
     */
    public void clear() {
        audioList.clear();
    }

}
