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

import io.github.gravitygame.entities.BodyCreationController;
import io.github.gravitygame.utils.PerformanceMonitor;

public class UICreationManager {
    private final Stage stage;
    private final Skin skin;
    private final SimulationManager simulationManager;
    private final BodyCreationController bodyCreationController;
    private final CameraController cameraController;
    private final PerformanceMonitor performanceMonitor;
    
    private Label fpsLabel;
    private Label frameTimeLabel;

    // Constructor
    public UICreationManager(Stage stage, SimulationManager simulationManager, 
                             BodyCreationController bodyCreationController, CameraController cameraController) {
        this.stage = stage;
        this.simulationManager = simulationManager;
        this.bodyCreationController = bodyCreationController;
        this.cameraController = cameraController;
        this.skin = new Skin(Gdx.files.internal("skin/neon-ui.json"));
        this.performanceMonitor = new PerformanceMonitor();
    }

    // Setup UI elements and listeners
    public void setupUI() {
        Gdx.app.log("UI", "Setting up UI elements");

        // Create a table for layout
        Table table = new Table();
        table.setFillParent(true);
        table.top().right();
        stage.addActor(table);

        // Create performance monitor labels
        setupPerformanceUI();

        // Create UI buttons
        TextButton pauseButton = createPauseButton();
        TextButton cameraModeButton = createCameraModeButton();
        TextButton createBodyButton = createCreateBodyButton();

        // Add buttons to table with layout specifications
        table.add(pauseButton).width(200).height(100).padTop(10).padRight(10);
        table.row();
        table.add(cameraModeButton).width(200).height(100).padTop(10).padRight(10);
        table.row();
        table.add(createBodyButton).width(200).height(100).padTop(10).padRight(10);

        Gdx.app.log("UI", "UI setup complete");
    }
    
    // Setup performance monitoring UI
    private void setupPerformanceUI() {
        Table perfTable = new Table();
        perfTable.setFillParent(true);
        perfTable.top().left();
        stage.addActor(perfTable);
        
        // Create labels for FPS and Frame Time
        fpsLabel = new Label("FPS: 0", skin);
        frameTimeLabel = new Label("Frame Time: 0.0 ms", skin);
        
        // Style the labels
        fpsLabel.setAlignment(Align.left);
        frameTimeLabel.setAlignment(Align.left);
        
        // Add labels to the performance table
        perfTable.add(fpsLabel).padTop(10).padLeft(10).left();
        perfTable.row();
        perfTable.add(frameTimeLabel).padTop(5).padLeft(10).left();
    }

    // Create the Pause Button and add listener
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

    // Create the Camera Mode Button and add listener
    private TextButton createCameraModeButton() {
        TextButton cameraModeButton = new TextButton("Camera: Follow", skin);
        cameraModeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleCameraMode(cameraModeButton);
            }
        });
        return cameraModeButton;
    }

    // Create the Create Body Button and add listener
    private TextButton createCreateBodyButton() {
        TextButton createBodyButton = new TextButton("Create Planets", skin);
        createBodyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleBodyCreationMode(createBodyButton);
            }
        });
        return createBodyButton;
    }

    // Toggle between Follow and Pan camera modes
    private void toggleCameraMode(TextButton cameraModeButton) {
        CameraController.CameraMode newMode = (cameraController.getMode() == CameraController.CameraMode.FOLLOW)
                ? CameraController.CameraMode.PAN
                : CameraController.CameraMode.FOLLOW;
        cameraController.setMode(newMode);
        cameraModeButton.setText("Camera: " + (newMode == CameraController.CameraMode.PAN ? "Pan" : "Follow"));
    }

    // Toggle body creation mode
    private void toggleBodyCreationMode(TextButton createBodyButton) {
        if (!bodyCreationController.isActive()) {
            bodyCreationController.startCreation();
            createBodyButton.setText("Cancel Creation Mode");
        } else {
            bodyCreationController.cancelCreation();
            createBodyButton.setText("Create Planets");
        }
    }

    // Update performance metrics
    private void updatePerformanceMetrics() {
        performanceMonitor.update();
        
        // Update labels with current metrics
        fpsLabel.setText(String.format("FPS: %.1f", performanceMonitor.getFPS()));
        frameTimeLabel.setText(String.format("Frame Time: %.2f ms", performanceMonitor.getAverageFrameTime()));
    }

    // Render the UI
    public void render(float delta) {
        updatePerformanceMetrics();
        stage.act(delta);
        stage.draw();
    }

    // Getter for the stage
    public Stage getStage() {
        return stage;
    }

    // Dispose resources
    public void dispose() {
        skin.dispose();
    }
}