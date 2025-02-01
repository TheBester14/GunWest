package network;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import javax.sound.sampled.*;

public class AudioHandler {
    private static final int SAMPLE_RATE = 48000;
    private static final int SAMPLE_SIZE_IN_BITS = 16;
    private static final int CHANNELS = 2;
    private static final boolean SIGNED = true;
    private static final boolean BIG_ENDIAN = false;
    private OutputStream audioOutputStream;
    private InputStream audioInputStream;
    private TargetDataLine microphone;
    private SourceDataLine speakers;

    public void startCapture() {
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();
            new Thread(() -> {
                byte[] buffer = new byte[1024 * CHANNELS];
                while (true) {
                    int bytesRead = microphone.read(buffer, 0, buffer.length);
                    if (bytesRead > 0) {
                        sendAudioData(buffer, bytesRead);
                    }
                }
            }).start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    
    public void startPlayback() {
        try {
            AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE_IN_BITS, CHANNELS, SIGNED, BIG_ENDIAN);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            speakers = (SourceDataLine) AudioSystem.getLine(info);
            speakers.open(format);
            speakers.start();
            new Thread(() -> {
                byte[] buffer = new byte[1024 * CHANNELS];
                while (true) {
                    int bytesRead = receiveAudioData(buffer);
                    if (bytesRead > 0) {
                        speakers.write(buffer, 0, bytesRead);
                    }
                }
            }).start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public AudioHandler(OutputStream audioOutputStream) {
        this.audioOutputStream = audioOutputStream;
    }
    
    public AudioHandler(InputStream audioInputStream) {
        this.audioInputStream = audioInputStream;
    }
    
    private void sendAudioData(byte[] data, int length) {
    	 try {
             audioOutputStream.write((length >> 24) & 0xFF);
             audioOutputStream.write((length >> 16) & 0xFF);
             audioOutputStream.write((length >> 8) & 0xFF);
             audioOutputStream.write(length & 0xFF);
             audioOutputStream.write(data, 0, length);
             audioOutputStream.flush();
         } catch (IOException e) {
             e.printStackTrace();
         }
    }

    private int receiveAudioData(byte[] buffer) {
        try {
            byte[] lengthBytes = new byte[4];
            int bytesRead = audioInputStream.read(lengthBytes);
            if (bytesRead != 4) {
                return -1;
            }
            int length = (lengthBytes[0] << 24) | (lengthBytes[1] << 16) | (lengthBytes[2] << 8) | lengthBytes[3];
            int totalBytesRead = 0;
            while (totalBytesRead < length) {
                bytesRead = audioInputStream.read(buffer, totalBytesRead, length - totalBytesRead);
                if (bytesRead == -1) {
                    return -1;
                }
                totalBytesRead += bytesRead;
            }

            return totalBytesRead;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}

