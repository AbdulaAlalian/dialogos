document.addEventListener("DOMContentLoaded", function () {
    const startButton = document.getElementById("startRecording");
    const stopButton = document.getElementById("stopRecording");

    startButton.addEventListener("click", function () {
        console.log("Start Recording clicked");
        startRecording();
        stopButton.disabled = false;
        startButton.disabled = true;
    });

    stopButton.addEventListener("click", function () {
        console.log("Stop Recording clicked");
        stopRecording();
        stopButton.disabled = true;
        startButton.disabled = false;
    });
});

let mediaRecorder;
let audioChunks = [];

function startRecording() {
    navigator.mediaDevices.getUserMedia({ audio: true })
        .then(function(stream) {
            mediaRecorder = new MediaRecorder(stream);
            mediaRecorder.ondataavailable = function(event) {
                audioChunks.push(event.data);
            };
            mediaRecorder.onstop = function() {
                const audioBlob = new Blob(audioChunks, { type: 'audio/wav' });
                // sendAudioToServer(audioBlob);
                audioChunks = [];
            };
            mediaRecorder.start();
            document.getElementById('stopRecording').disabled = false;
            document.getElementById('startRecording').disabled = true;
        });
}

function stopRecording() {
    mediaRecorder.stop();
    document.getElementById('startRecording').disabled = false;
    document.getElementById('stopRecording').disabled = true;
}

function sendAudioToServer(audioBlob) {
    const socket = new WebSocket("ws://localhost:8080/audio-stream");
    socket.onopen = function() {
        socket.send(audioBlob);
    };
}