package edu.cmu.lti.dialogos.sphinx.client;

import com.clt.dialogos.plugin.AudioPlugin;
import com.clt.dialogos.plugin.PluginManager;
import edu.cmu.sphinx.api.AbstractSpeechRecognizer;
import edu.cmu.sphinx.api.Context;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.frontend.util.StreamDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;

import java.io.IOException;
import java.io.InputStream;

/**
 * takes a SphinxContext and configures the speech recognizer accordingly
 *
 * Created by timo on 30.10.17.
 */
public class ConfigurableSpeechRecognizer extends AbstractSpeechRecognizer {

    private Microphone microphone;
    AudioPlugin audioInputPlugin;

    public ConfigurableSpeechRecognizer(Context context, InputStream audioSource) throws IOException {
        super(context);
        recognizer.allocate();

        /*recognizer.addStateListener(new StateListener() {
            @Override public void statusChanged(edu.cmu.sphinx.recognizer.Recognizer.State status) {
                System.err.println("Sphinx recognition listener defined in ConfigurableSpeechRecognizer.java: " + status);
            }
            @Override public void newProperties(PropertySheet ps) throws PropertyException { }
        });*/

        try {
            audioInputPlugin = PluginManager.getActiveAudioInputPlugin();
        } catch (Exception e) {
            e.printStackTrace();
        }
        StreamDataSource sds = context.getInstance(StreamDataSource.class);
        if (audioSource != null) {
            sds.setInputStream(audioSource);
        } else {
            // TODO die stelle mit dem LocalAudioInputPlugin verändern
            // sds.setInputStream(audioInputPlugin.getAudioInput());
            microphone = context.getInstance(Microphone.class);
            microphone.initialize();
            sds.setPredecessor(microphone);
        }
    }

    public synchronized void startRecognition() {
        if (recognizer.getState() == Recognizer.State.DEALLOCATED)
            recognizer.allocate();
        if (audioInputPlugin != null)
            audioInputPlugin.startRecording();
    }

    public synchronized void stopRecognition() {
        if (audioInputPlugin != null)
            audioInputPlugin.stopRecording();
    }

    public synchronized void resetRecognition() {
        stopRecognition();
        if (recognizer.getState() != Recognizer.State.DEALLOCATED)
            recognizer.deallocate();
    }

    /**
     * Returns result of the recognition.
     *
     * @return recognition result or {@code null} if there is no result, e.g., because the
     * 			microphone or input stream has been closed
     */
    @Override
    public SpeechResult getResult() {
        Result result = recognizer.recognize();
        return null == result ? null : new SpeechResult(result);
    }


}
