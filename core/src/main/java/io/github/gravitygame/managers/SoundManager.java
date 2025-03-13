package io.github.gravitygame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;


public class SoundManager implements Disposable {
    private static final String TAG = "SoundManager";
    
    private static SoundManager instance;
    
    // Sound assets
    private Sound clickSound;
    private Array<Music> gameTracks;
    private Sound smallCollisionSound;
    private Sound mediumCollisionSound;
    private Sound largeCollisionSound;
    
    // Runtime state
    private float masterVolume = 0.7f;
    private int currentTrackIndex = 0;
    private boolean initialized = false;

    /**
     * Private constructor for singleton pattern
     */
    private SoundManager() {
        // Empty constructor
    }
    
    /**
     * Initializes sound resources. Call this after Gdx has been fully initialized.
     */
    public void initialize() {
        if (initialized) return;
        
        try {
            // Load sound effects
            clickSound = Gdx.audio.newSound(Gdx.files.internal("sounds/clicksound.wav"));
            
            // Load collision sounds
            smallCollisionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/collision_small.mp3"));
            mediumCollisionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/collision_medium.mp3"));
            largeCollisionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/collision_large.mp3"));
    
            // Load music tracks
            gameTracks = new Array<>();
            Music track1 = Gdx.audio.newMusic(Gdx.files.internal("sounds/music/18Track3A.mp3"));
            track1.setVolume(masterVolume);
            gameTracks.add(track1);
            
            Music track2 = Gdx.audio.newMusic(Gdx.files.internal("sounds/music/19Track3B.mp3"));
            track2.setVolume(masterVolume);
            gameTracks.add(track2);
    
            initialized = true;
            Gdx.app.log(TAG, "Sound system initialized successfully");
        } catch (Exception e) {
            Gdx.app.error(TAG, "Failed to initialize sound system", e);
        }
    }

    /**
     * Plays UI click sound effect
     */
    public void playClickSound() {
        if (initialized && clickSound != null) {
            clickSound.play(masterVolume);
        }
    }

    /**
     * Starts playing game music with track cycling
     */
    public void startGameMusic() {
        if (!initialized || gameTracks.size == 0) {
            return;
        }
        
        stopAllMusic();
        playNextGameTrack();
        Gdx.app.log(TAG, "Starting game music");
    }

    public void setVolume(float volume) {
        if (volume < 0f) volume = 0f;
        if (volume > 1f) volume = 1f;
        
        this.masterVolume = volume;
        
        // Apply volume to all current music tracks
        if (initialized && gameTracks != null) {
            for (Music track : gameTracks) {
                if (track != null) {
                    track.setVolume(masterVolume);
                }
            }
        }
        
        Gdx.app.log(TAG, "Master volume set to: " + masterVolume);
    }

    public float getVolume() {
        return masterVolume;
    }

    public void playCollisionSound(String size) {
        if (!initialized) return;

        Sound soundToPlay = null;
        
        switch(size.toLowerCase()) {
            case "small":
                soundToPlay = smallCollisionSound;
                break;
            case "medium":
                soundToPlay = mediumCollisionSound;
                break;
            case "large":
                soundToPlay = largeCollisionSound;
                break;
            default:
                Gdx.app.error(TAG, "Unknown collision size: " + size);
                return;
        }

        if (soundToPlay != null) {
            // Add slight pitch variation for natural sound
            float pitchVariation = MathUtils.random(0.9f, 1.1f);
            soundToPlay.play(masterVolume, pitchVariation, 0f);
        }
    }

    /**
     * Plays the next game track in sequence
     */
    private void playNextGameTrack() {
        if (!initialized || gameTracks.size == 0) {
            return;
        }
        
        final Music currentTrack = gameTracks.get(currentTrackIndex);
        currentTrackIndex = (currentTrackIndex + 1) % gameTracks.size;
        
        currentTrack.setLooping(false);
        currentTrack.setVolume(masterVolume);
        currentTrack.setOnCompletionListener(music -> {
            if (initialized) {
                Music nextTrack = gameTracks.get(currentTrackIndex);
                if (nextTrack != null) {
                    nextTrack.setVolume(masterVolume);
                    nextTrack.play();
                }
            }
        });
        
        currentTrack.play();
    }

    /**
     * Stops all currently playing music
     */
    public void stopAllMusic() {
        if (!initialized) {
            return;
        }
        
        if (gameTracks != null) {
            for(Music track : gameTracks) {
                if (track != null) {
                    track.stop();
                }
            }
        }
    }

    /**
     * Disposes all sound resources
     */
    @Override
    public void dispose() {
        if (!initialized) {
            return;
        }
        
        if (clickSound != null) {
            clickSound.dispose();
            clickSound = null;
        }
        
        if (gameTracks != null) {
            for(Music track : gameTracks) {
                if (track != null) {
                    track.dispose();
                }
            }
            gameTracks.clear();
        }

        if (smallCollisionSound != null) {
            smallCollisionSound.dispose();
            smallCollisionSound = null;
        }
        if (mediumCollisionSound != null) {
            mediumCollisionSound.dispose();
            mediumCollisionSound = null;
        }
        if (largeCollisionSound != null) {
            largeCollisionSound.dispose();
            largeCollisionSound = null;
        }
        initialized = false;
        Gdx.app.log(TAG, "Sound resources disposed");
    }

    /**
     * Gets the singleton instance
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
}