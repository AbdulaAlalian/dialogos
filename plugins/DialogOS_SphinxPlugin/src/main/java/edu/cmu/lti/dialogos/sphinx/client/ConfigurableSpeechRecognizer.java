package edu.cmu.lti.dialogos.sphinx.client;

import com.clt.dialogos.plugin.AudioPlugin;
import com.clt.dialogos.plugin.PluginManager;
import edu.cmu.sphinx.api.AbstractSpeechRecognizer;
import edu.cmu.sphinx.api.Context;
import edu.cmu.sphinx.api.SpeechResult;
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
            if (audioInputPlugin == null) {
                throw new Exception("AudioInputPlugin not set!");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        StreamDataSource sds = context.getInstance(StreamDataSource.class);
        if (audioSource != null) {
            sds.setInputStream(audioSource);
        } else {
            sds.setInputStream(audioInputPlugin.setupAndGetAudioInput());
        }
    }

    public synchronized void startRecognition() {
        if (recognizer.getState() == Recognizer.State.DEALLOCATED)
            recognizer.allocate();
        if (audioInputPlugin != null)
            audioInputPlugin.startRecording();
    }

    public synchronized void stopRecognition() {
        if (audioInputPlugin != null && audioInputPlugin.isRecording())
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
