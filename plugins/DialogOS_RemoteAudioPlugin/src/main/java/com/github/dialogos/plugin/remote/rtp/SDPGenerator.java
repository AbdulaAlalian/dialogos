package com.github.dialogos.plugin.remote.rtp;


/**
 * Creates a Session Descritption Protocol (SDP) file, which is used to describe the audio stream to the receiver of an RTP stream
 * The file generated here has the following content:
 * <ul>
 *     <li>v = version of the protocol (as of 2025 it is '0')</li>
 *     <li>o = originator and session identifier consisting of: username, id, version number, and ip-address</li>
 *     <li>s = session title</li>
 *     <li>c = connection information</li>
 *     <li>t = time the session is active (0 in our case)</li>
 *     <li>m = media name and transport address</li>
 *     <li>a = media attribute lines indicating information like, format, sampleRate, sampleSize, channels etc.</li>
 * </ul>
 *
 * For more information  look into the official document, RFC 4566
 */
public class SDPGenerator {

    /**
     * Generates an SDP file for the speech synthesizer (currently only MaryTTS)
     * @param ipAddress ip address of the receiver of the stream
     * @param port port on which the receiver listens (for example: RTP with the port 5004)
     * @return String containing the SDP information
     */
    public static String generateSynthesizerSDPDescription(String ipAddress, int port) {
        return "v=0\n" +
                "o=- 0 0 IN IP4 " + ipAddress + "\n" +
                "s=RTP Audio Stream\n" +
                "c=IN IP4 " + ipAddress + "\n" +
                "t=0 0\n" +
                "m=audio " + port + " RTP/AVP 96\n" +
                "a=rtpmap:96 L16/48000/1\n" +
                "a=recvonly\n";
    }
}
