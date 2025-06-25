package GraphicalTicTacToeFinal;

import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Fixed SoundEffect enum - removed incorrect package reference
 * This enum encapsulates all the sound effects of a game, so as to separate the sound playing
 * codes from the game codes.
 */
public enum SoundEffect {
    EAT_FOOD("audio/eatfood.wav"),
    EXPLODE("audio/explode.wav"),
    DIE("audio/die.wav");

    /** Nested enumeration for specifying volume */
    public static enum Volume {
        MUTE, LOW, MEDIUM, HIGH
    }

    // Fixed: Removed incorrect package reference
    public static Volume volume = Volume.LOW;

    /** Each sound effect has its own clip, loaded with its own sound file. */
    private Clip clip;

    /** Private Constructor to construct each element of the enum with its own sound file. */
    private SoundEffect(String soundFileName) {
        try {
            // Use URL (instead of File) to read from disk and JAR.
            URL url = this.getClass().getClassLoader().getResource(soundFileName);
            if (url != null) {
                // Set up an audio input stream piped from the sound file.
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
                // Get a clip resource.
                clip = AudioSystem.getClip();
                // Open audio clip and load samples from the audio input stream.
                clip.open(audioInputStream);
            } else {
                System.err.println("Could not find audio file: " + soundFileName);
            }
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio file: " + soundFileName);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO error loading audio file: " + soundFileName);
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable for: " + soundFileName);
            e.printStackTrace();
        }
    }

    /** Play or Re-play the sound effect from the beginning, by rewinding. */
    public void play() {
        if (volume != Volume.MUTE && clip != null) {
            if (clip.isRunning())
                clip.stop();   // Stop the player if it is still running
            clip.setFramePosition(0); // rewind to the beginning
            clip.start();     // Start playing
        }
    }

    /** Optional static method to pre-load all the sound files. */
    static void initGame() {
        values(); // calls the constructor for all the elements
    }
}
