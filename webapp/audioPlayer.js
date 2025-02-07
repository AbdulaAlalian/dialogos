//TODO Fix problem when reloading the page, where audio context does not play audio until the tab is reopened
document.addEventListener("DOMContentLoaded", function () {
    const ws = new WebSocket("ws://localhost:8080/audio-stream");

    ws.binaryType = "arraybuffer";

    ws.onmessage = function(event) {
        const audioData = event.data;
        console.log("Received audio data ", audioData);
        playAudio(audioData);
    };

    let audioContext;
    let audioSource;

    function playAudio(audioData) {
        if (!audioContext) {
            audioContext = new AudioContext({sampleRate: 16000});
        }

        audioContext.decodeAudioData(audioData, function(buffer) {
            if (audioSource) {
                audioSource.stop();
            }

            audioSource = audioContext.createBufferSource();
            audioSource.buffer = buffer;
            audioSource.connect(audioContext.destination);
            audioSource.start(0);
        });
    }
});
