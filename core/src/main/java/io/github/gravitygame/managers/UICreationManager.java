package io.github.gravitygame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import io.github.gravitygame.Main;
import io.github.gravitygame.entities.BodyCreationController;
import io.github.gravitygame.physics.TrajectoryRenderer;
import io.github.gravitygame.screens.GameScreen;
import io.github.gravitygame.utils.PerformanceMonitor;

public class UICreationManager {
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
    }

    /**
     * Initialize and setup all UI elements
     */
    public void setupUI() {
        Gdx.app.log("UI", "Setting up UI elements");
        
        // Initialize tables
        setupTables();
        
        // Setup UI components
        setupPerformanceUI();
        setupControlButtons();
        
        Gdx.app.log("UI", "UI setup complete");
    }
    
    /**
     * Create and configure UI tables
     */
    private void setupTables() {
        // Main controls table (right side)
        controlsTable = new Table();
        controlsTable.setFillParent(true);
        controlsTable.top().right();
        stage.addActor(controlsTable);
        
        // Performance metrics table (left side)
        performanceTable = new Table();
        performanceTable.setFillParent(true);
        performanceTable.top().left();
        stage.addActor(performanceTable);
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
        // Create buttons
        TextButton pauseButton = createPauseButton();
        TextButton cameraModeButton = createCameraModeButton();
        TextButton createBodyButton = createCreateBodyButton();
        TextButton trajectoryModeButton = createTrajectoryModeButton();
        
        // Standard button configuration
        int buttonWidth = 200;
        int buttonHeight = 100;
        int buttonPadding = 10;
        
        // Add buttons to controls table
        controlsTable.add(pauseButton).width(buttonWidth).height(buttonHeight).pad(buttonPadding);
        controlsTable.row();
        controlsTable.add(cameraModeButton).width(buttonWidth).height(buttonHeight).pad(buttonPadding);
        controlsTable.row();
        controlsTable.add(createBodyButton).width(buttonWidth).height(buttonHeight).pad(buttonPadding);
        controlsTable.row();
        controlsTable.add(trajectoryModeButton).width(buttonWidth).height(buttonHeight).pad(buttonPadding);
    }

    /**
     * Create the Pause/Resume button
     */
    private TextButton createPauseButton() {
        TextButton pauseButton = new TextButton("Pause", skin);
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
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
     * Create the Trajectory Mode toggle button
     */
    private TextButton createTrajectoryModeButton() {
        TextButton trajectoryModeBtn = new TextButton("Trajectory: Historical", skin);
        trajectoryModeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                GameScreen gameScreen = (GameScreen)((Main)Gdx.app.getApplicationListener()).getScreen();
                TrajectoryRenderer.PredictionMode currentMode = gameScreen.trajectoryRenderer.getPredictionMode();
                
                // Toggle between modes
                if (currentMode == TrajectoryRenderer.PredictionMode.HISTORICAL) {
                    gameScreen.setTrajectoryMode(TrajectoryRenderer.PredictionMode.ESTIMATED);
                    trajectoryModeBtn.setText("Trajectory: Estimated");
                } else {
                    gameScreen.setTrajectoryMode(TrajectoryRenderer.PredictionMode.HISTORICAL);
                    trajectoryModeBtn.setText("Trajectory: Historical");
                }
            }
        });
        return trajectoryModeBtn;
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
    public void dispose() {
        skin.dispose();
    }
}