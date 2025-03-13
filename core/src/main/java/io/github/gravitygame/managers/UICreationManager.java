package io.github.gravitygame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import io.github.gravitygame.entities.BodyCreationController;
import io.github.gravitygame.utils.PerformanceMonitor;

public class UICreationManager implements Disposable {
    private static final String TAG = "UIManager";
    
    // UI components
    private final Stage stage;
    private final Skin skin;
    
    // Game controllers
    private final SimulationManager simulationManager;
    private final BodyCreationController bodyCreationController;
    private final CameraController cameraController;
    
    // Performance monitoring
    private final PerformanceMonitor performanceMonitor;
    private Label fpsLabel;
    private Label frameTimeLabel;
    
    // UI tables
    private Table controlsTable;
    private Table performanceTable;
    private Table soundControlsTable;


    /**
     * Constructor for UICreationManager
     */
    public UICreationManager(Stage stage, SimulationManager simulationManager, 
                             BodyCreationController bodyCreationController, CameraController cameraController) {
        this.stage = stage;
        this.simulationManager = simulationManager;
        this.bodyCreationController = bodyCreationController;
        this.cameraController = cameraController;
        this.skin = new Skin(Gdx.files.internal("skin/neon-ui.json"));
        this.performanceMonitor = new PerformanceMonitor();
        
        // Initialize the SoundManager - this should be done early
        SoundManager.getInstance().initialize();
    }

    /**
     * Initialize and setup all UI elements
     */
    public void setupUI() {
        Gdx.app.log(TAG, "Setting up UI elements");
        
        // Initialize tables
        setupTables();
        
        // Setup UI components
        setupPerformanceUI();
        setupControlButtons();
        
        Gdx.app.log(TAG, "UI setup complete");
    }
    
    /**
     * Create and configure UI tables
     */
    private void setupTables() {
        // Main controls table (right side)
        controlsTable = new Table();
        controlsTable.setFillParent(true);
        controlsTable.top().right();
        controlsTable.pad(10);
        stage.addActor(controlsTable);
        
        // Performance metrics table (left side)
        performanceTable = new Table();
        performanceTable.setFillParent(true);
        performanceTable.top().left();
        performanceTable.pad(10);
        stage.addActor(performanceTable);
        
        // Sound controls table (bottom right)
        soundControlsTable = new Table();
        soundControlsTable.setFillParent(true);
        soundControlsTable.bottom().right();
        soundControlsTable.pad(10);
        stage.addActor(soundControlsTable);
    }
    
    /**
     * Setup performance monitoring UI components
     */
    private void setupPerformanceUI() {
        // Create labels for FPS and Frame Time
        fpsLabel = new Label("FPS: 0", skin);
        frameTimeLabel = new Label("Frame Time: 0.0 ms", skin);
        
        // Style the labels
        fpsLabel.setAlignment(Align.left);
        frameTimeLabel.setAlignment(Align.left);
        
        // Add labels to the performance table
        performanceTable.add(fpsLabel).padTop(10).padLeft(10).left();
        performanceTable.row();
        performanceTable.add(frameTimeLabel).padTop(5).padLeft(10).left();
    }

    /**
     * Setup all control buttons
     */
    private void setupControlButtons() {
        // Create main control buttons
        TextButton pauseButton = createPauseButton();
        TextButton cameraModeButton = createCameraModeButton();
        TextButton createBodyButton = createCreateBodyButton();
        
        // Standard button configuration
        int buttonWidth = 200;
        int buttonHeight = 70;
        int buttonPadding = 10;
        
        // Add buttons to controls table
        controlsTable.add(pauseButton).width(buttonWidth).height(buttonHeight).pad(buttonPadding);
        controlsTable.row();
        controlsTable.add(cameraModeButton).width(buttonWidth).height(buttonHeight).pad(buttonPadding);
        controlsTable.row();
        controlsTable.add(createBodyButton).width(buttonWidth).height(buttonHeight).pad(buttonPadding);
        controlsTable.row();
        
        // Set up sound controls separately
        setupSoundControls(buttonWidth, buttonHeight, buttonPadding);
    }

    private void setupSoundControls(int buttonWidth, int buttonHeight, int buttonPadding) {
        // Create sound control buttons
        TextButton toggleMusicButton = createToggleMusicButton();
        
        // Create volume slider
        Label volumeLabel = new Label("Volume:", skin);
        Slider volumeSlider = createVolumeSlider();
        
        // Create a sub-table for volume controls
        Table volumeControlTable = new Table();
        volumeControlTable.add(volumeLabel).padRight(10);
        volumeControlTable.add(volumeSlider).width(buttonWidth - 70);
        
        // Add elements to sound controls table
        soundControlsTable.add(volumeControlTable).width(buttonWidth).height(buttonHeight).pad(buttonPadding);
        soundControlsTable.row();
        soundControlsTable.add(toggleMusicButton).width(buttonWidth).height(buttonHeight).pad(buttonPadding);
    }

    private Slider createVolumeSlider() {
        Slider slider = new Slider(0f, 1f, 0.01f, false, skin);
        slider.setValue(0.7f); // Set default volume
        
        // Set initial volume
        SoundManager.getInstance().setVolume(slider.getValue());
        
        slider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = slider.getValue();
                SoundManager.getInstance().setVolume(volume);
                Gdx.app.log("UI", "Volume changed to: " + volume);
            }
        });
        
        return slider;
    }

    /**
     * Create a button to toggle game music
     */
    private TextButton createToggleMusicButton() {
        TextButton button = new TextButton("Toggle Music", skin);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.getInstance().playClickSound();
                // Start or stop music
                SoundManager soundManager = SoundManager.getInstance();
                soundManager.stopAllMusic();
                soundManager.startGameMusic();
            }
        });
        return button;
    }

    /**
     * Create the Pause/Resume button
     */
    private TextButton createPauseButton() {
        TextButton pauseButton = new TextButton("Pause", skin);
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.getInstance().playClickSound();
                simulationManager.togglePause();
                Gdx.app.log("UI", "Pause Button Clicked");
                pauseButton.setText(simulationManager.isPaused() ? "Resume" : "Pause");
            }
        });
        return pauseButton;
    }

    /**
     * Create the Camera Mode toggle button
     */
    private TextButton createCameraModeButton() {
        TextButton cameraModeButton = new TextButton("Camera: Follow", skin);
        cameraModeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.getInstance().playClickSound();
                CameraController.CameraMode newMode = (cameraController.getMode() == CameraController.CameraMode.FOLLOW)
                        ? CameraController.CameraMode.PAN
                        : CameraController.CameraMode.FOLLOW;
                cameraController.setMode(newMode);
                cameraModeButton.setText("Camera: " + (newMode == CameraController.CameraMode.PAN ? "Pan" : "Follow"));
            }
        });
        return cameraModeButton;
    }

    /**
     * Create the Planet Creation toggle button
     */
    private TextButton createCreateBodyButton() {
        TextButton createBodyButton = new TextButton("Create Planets", skin);
        createBodyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.getInstance().playClickSound();
                if (!bodyCreationController.isActive()) {
                    bodyCreationController.startCreation();
                    createBodyButton.setText("Cancel Creation Mode");
                } else {
                    bodyCreationController.cancelCreation();
                    createBodyButton.setText("Create Planets");
                }
            }
        });
        return createBodyButton;
    }

    /**
     * Update performance metrics display
     */
    private void updatePerformanceMetrics() {
        performanceMonitor.update();
        
        // Update labels with current metrics
        fpsLabel.setText(String.format("FPS: %.1f", performanceMonitor.getFPS()));
        frameTimeLabel.setText(String.format("Frame Time: %.2f ms", performanceMonitor.getAverageFrameTime()));
    }

    /**
     * Update and render the UI
     */
    public void render(float delta) {
        updatePerformanceMetrics();
        stage.act(delta);
        stage.draw();
    }

    /**
     * Getter for the stage
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Dispose resources
     */
    @Override
    public void dispose() {
        skin.dispose();
        SoundManager.getInstance().dispose();
    }
}