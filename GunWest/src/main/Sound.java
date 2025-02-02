package main;

import javax.sound.sampled.*;
import java.net.URL;

public class Sound {
    private Clip clip;
    private FloatControl volumeControl;
    private URL soundURL[] = new URL[30];

    // Constructor to load the sound files
    public Sound() {
        soundURL[0] = getClass().getResource("/sounds/MenuTheme.wav");
        soundURL[1] = getClass().getResource("/sounds/RevolverSound.wav");
        soundURL[2] = getClass().getResource("/sounds/ShotgunSound.wav");
        soundURL[3] = getClass().getResource("/sounds/SniperSound.wav");
        soundURL[4] = getClass().getResource("/sounds/CoinSound.wav");
    }

    // Set the sound file to be played
    public void setFile(int i) {
        try {
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL[i]);
            clip = AudioSystem.getClip();
            clip.open(ais);

            // Set up volume control if supported
            if (clip.isControlSupported(FloatControl.Type.VOLUME)) {
                volumeControl = (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Play the sound once
    public void play() {
        if (clip != null) {
            clip.start();
        }
    }

    // Loop the sound continuously
    public void loop() {
        if (clip != null) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    // Stop the sound
    public void stop() {
        if (clip != null) {
            clip.stop();
        }
    }

    // Set the volume of the sound (from 0.0f to 1.0f)
    public void setVolume(float volume) {
        if (volumeControl != null) {
            volumeControl.setValue(volume);  // Adjust the volume
        }
    }

    // Play sound asynchronously to prevent blocking the game thread
    public void playAsync() {
        new Thread(() -> {
            play();
        }).start();
    }
}
