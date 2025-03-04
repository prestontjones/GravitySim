package io.github.gravitygame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import io.github.gravitygame.entities.BodyCreationController;

public class UICreationManager {
    private final Stage stage;
    private final Skin skin;
    private final SimulationManager simulationManager;
    private final BodyCreationController bodyCreationController;
    private final CameraController cameraController;

    // Constructor
    public UICreationManager(Stage stage, SimulationManager simulationManager, 
                             BodyCreationController bodyCreationController, CameraController cameraController) {
        this.stage = stage;
        this.simulationManager = simulationManager;
        this.bodyCreationController = bodyCreationController;
        this.cameraController = cameraController;
        this.skin = new Skin(Gdx.files.internal("skin/neon-ui.json"));
    }

    // Setup UI elements and listeners
    public void setupUI() {
        Gdx.app.log("UI", "Setting up UI elements");

        // Create a table for layout
        Table table = new Table();
        table.setFillParent(true);
        table.top().right();
        stage.addActor(table);

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

    // Render the UI
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    // Getter for predictionsEnabled flag
    // public boolean isPredictionsEnabled() {
    //     return predictionsEnabled;
    // }

    // Getter for the stage
    public Stage getStage() {
        return stage;
    }

    // Dispose resources
    public void dispose() {
        skin.dispose();
    }
}