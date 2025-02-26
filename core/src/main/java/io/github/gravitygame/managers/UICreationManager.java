package io.github.gravitygame.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import io.github.gravitygame.entities.BodyCreator;
import io.github.gravitygame.utils.CameraController;

public class UICreationManager {
    private final Stage stage;
    private final Skin skin;
    private final SimulationManager simulationManager;
    private final BodyCreator bodyCreator;
    private boolean predictionsEnabled = false;
    private final CameraController cameraController;

    public UICreationManager(Stage stage, SimulationManager simulationManager, BodyCreator bodyCreator, CameraController cameraController) {
        this.stage = stage;
        this.simulationManager = simulationManager;
        this.bodyCreator = bodyCreator;
        this.cameraController = cameraController;
        this.skin = new Skin(Gdx.files.internal("skin/neon-ui.json"));
    }

    public void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Create UI elements
        TextButton pauseButton = new TextButton("Pause", skin);
        TextButton createBodyButton = new TextButton("Create Planets", skin);
        TextButton predictionToggleButton = new TextButton("Predictions: OFF", skin);
        TextButton cameraModeButton  = new TextButton("Camera: Follow", skin);

        Slider timestepSlider = new Slider(0.1f, 2f, 0.01f, false, skin);
        timestepSlider.setValue(1f); // Default timestep

        // Layout UI elements
        table.top().right();
        table.add(pauseButton).width(200).height(100).padTop(10).padRight(10);
        table.row();
        table.add(cameraModeButton).width(200).height(100).padTop(10).padRight(10);
        table.row();
        table.add(predictionToggleButton).width(200).height(100).padTop(10).padRight(10);
        table.row();
        table.add(createBodyButton).width(200).height(100).padTop(10).padRight(10);
        table.row();
        table.add(timestepSlider).width(200).height(20).padTop(10).padRight(10);

        // Pause Button listener
        pauseButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                simulationManager.togglePause();
                pauseButton.setText(simulationManager.isPaused() ? "Resume" : "Pause");
            }
        });

        // Camera Controller Button Listener
        cameraModeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CameraController.CameraMode newMode = 
                    (cameraController.getMode() == CameraController.CameraMode.FOLLOW) 
                        ? CameraController.CameraMode.PAN 
                        : CameraController.CameraMode.FOLLOW;
                
                cameraController.setMode(newMode);
                cameraModeButton.setText("Camera: " + (newMode == CameraController.CameraMode.FOLLOW ? "Pan" : "Follow"));
            }
        });

        // Create Body Button listener
        createBodyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!bodyCreator.isActive()) {
                    bodyCreator.startCreation();
                    createBodyButton.setText("Cancel Creation Mode");
                } else {
                    bodyCreator.cancelCreation();
                    createBodyButton.setText("Create Planets");
                }
            }
        });

        // Timestep Slider Listener
        timestepSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                simulationManager.setTimestepScale(timestepSlider.getValue());
            }
        });

        predictionToggleButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                predictionsEnabled = !predictionsEnabled;
                Gdx.app.log("UI", "Predictions toggled: " + predictionsEnabled);
                predictionToggleButton.setText("Predictions: " + (predictionsEnabled ? "ON" : "OFF"));
                
                // Notify the prediction worker through proper channels
                if (simulationManager != null) {
                    simulationManager.setPredictionsEnabled(predictionsEnabled);
                }
            }
        });
    }

    public boolean arePredictionsEnabled() {
        return predictionsEnabled;
    }

    public Stage getStage() {
        return stage;
    }

    public boolean isPredictionsEnabled() {
        return predictionsEnabled;
    }

    public void dispose() {
        skin.dispose();
    }
}
