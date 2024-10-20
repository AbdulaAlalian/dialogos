package edu.cmu.lti.dialogos.sphinx.client;

import com.clt.speech.Language;
import com.clt.speech.SpeechException;
import com.clt.speech.recognition.*;
import com.clt.speech.recognition.simpleresult.SimpleRecognizerResult;
import com.clt.srgf.Grammar;

import java.util.*;

import edu.cmu.sphinx.api.*;
import edu.cmu.sphinx.frontend.Data;
import edu.cmu.sphinx.frontend.endpoint.SpeechClassifiedData;

/**
 * @author koller, timo
 *
 * List of TODOs:
 * handle multiple languages (configurable for more than DE/EN?),
 *
 */

// TODO Change for Plugin-Architecture
public class Sphinx extends SphinxBaseRecognizer {
    private Map<Language, SphinxLanguageSettings> languageSettings;
    private SphinxContext context;
    ConfigurableSpeechRecognizer csr;
    private boolean vadInSpeech = false;
    private boolean stopping = false;

    public Sphinx() {
        languageSettings = SphinxLanguageSettings.createDefault();
        /* addRecognizerListener(evt -> {
            System.err.println("DialogOS recognizer listener defined in Sphinx.java: " + evt.toString());
        }); */
    }

    public SphinxLanguageSettings getLanguageSettings(Language l) {
        return languageSettings.get(l);
    }

    @Override protected RecognitionResult startImpl() throws SpeechException {
        fireRecognizerEvent(RecognizerEvent.RECOGNIZER_LOADING);
        assert context != null : "cannot start recognition without a context";
        csr = context.getRecognizer();
        context.getVadListener().setRecognizer(this);
        vadInSpeech = false;
        SpeechResult speechResult;
        boolean isMatch;
        stopping = false;
        csr.startRecognition();
        do {
            fireRecognizerEvent(RecognizerEvent.RECOGNIZER_READY);
            speechResult = csr.getResult();
            if (speechResult == null)
                break;
            isMatch = isMatch(speechResult);
            if (!isMatch)
                fireRecognizerEvent(new RecognizerEvent(this, RecognizerEvent.INVALID_RESULT, sphinx2DOSResult(speechResult)));
        } while (!isMatch && !stopping && isActive());
        csr.stopRecognition();
        if (speechResult != null && !stopping) {
            RecognitionResult sphinxResult = sphinx2DOSResult(speechResult);
            fireRecognizerEvent(sphinxResult);
            return sphinxResult;
        } else {
            return null;
        }
    }

    private boolean isMatch(SpeechResult speechResult) {
        Grammar gr = context.getGrammar();
        String result = speechResult.getHypothesis().replaceAll("<PHONE_.*?> ?", "");
        return gr.match(result, gr.getRoot()) != null;
    }

    private RecognitionResult sphinx2DOSResult(SpeechResult sphinx) {
        return new SimpleRecognizerResult(sphinx.getHypothesis().replaceAll("<PHONE_.*?> ?", ""));
    }

    @Override protected void stopImpl() {
        stopping = true;
        if (csr != null)
            csr.stopRecognition();
        vadInSpeech = false;
    }

    /** Return an array of supported languages */
    @Override public Language[] getLanguages() {
        Collection<Language> langs = languageSettings.keySet();
        return langs.toArray(new Language[langs.size()]);
    }

    @Override public void setContext(RecognitionContext context) {
        assert context instanceof SphinxContext : "you're feeding a context that I do not understand";
        this.context = (SphinxContext) context;
    }

    @Override public RecognitionContext getContext() {
        return this.context;
    }

    @Override public SphinxContext createTemporaryContext(Grammar g, Domain domain) {
        return createContext("temp", g, domain, System.currentTimeMillis());
    }

    Map<Language, SphinxContext> contextCache = new HashMap<>();

    @Override protected SphinxContext createContext(String name, Grammar g, Domain domain, long timestamp) {
        assert g.getLanguage() != null;

        Language l = new Language(Language.findLocale(g.getLanguage()));
        SphinxLanguageSettings ls = languageSettings.get(l);

	// Fallback if no language could be found
        if( ls == null ) {
            ls = languageSettings.values().iterator().next();
        }


        assert l != null;
        if (!contextCache.containsKey(l)) {
            contextCache.put(l, new SphinxContext(name, g, ls));
        }
        SphinxContext sc = contextCache.get(l);
        sc.setGrammar(g);
        return sc;
    }

    void evesdropOnFrontend(Data d) {
        if (d instanceof SpeechClassifiedData) {
            SpeechClassifiedData scd = (SpeechClassifiedData) d;
            if (scd.isSpeech() != vadInSpeech) {
                vadInSpeech = scd.isSpeech();
                fireRecognizerEvent(vadInSpeech ? RecognizerEvent.START_OF_SPEECH : RecognizerEvent.END_OF_SPEECH);
            }
            informAudioListeners(scd.getValues());
        }
    }

}
