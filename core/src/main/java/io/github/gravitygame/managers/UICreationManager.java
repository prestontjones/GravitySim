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

public class UICreationManager {
    private final Stage stage;
    private final Skin skin;
    private final SimulationManager simulationManager;
    private final BodyCreator bodyCreator;

    public UICreationManager(Stage stage, SimulationManager simulationManager, BodyCreator bodyCreator) {
        this.stage = stage;
        this.simulationManager = simulationManager;
        this.bodyCreator = bodyCreator;
        this.skin = new Skin(Gdx.files.internal("skin/neon-ui.json"));
    }

    public void setupUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Create UI elements
        TextButton pauseButton = new TextButton("Pause", skin);
        TextButton toggleWireFrame = new TextButton("Toggle Wire Frame", skin);
        TextButton createBodyButton = new TextButton("Create Body", skin);
        TextButton collisionToggle = new TextButton("Toggle collision", skin);
        TextButton pathPrediction = new TextButton("Path Prediction", skin);
        TextButton showDirection = new TextButton("Show Direction", skin);
        TextButton deleteBody = new TextButton("Delete Body", skin);

        Slider timestepSlider = new Slider(0.1f, 2f, 0.01f, false, skin);
        timestepSlider.setValue(1f); // Default timestep

        // Layout UI elements
        table.top().right();
        table.add(pauseButton).width(200).height(100).padTop(10).padRight(10);
        table.row();
        table.add(deleteBody).width(200).height(100).padTop(10).padRight(10);
        table.row();
        table.add(showDirection).width(200).height(100).padTop(10).padRight(10);
        table.row();
        table.add(pathPrediction).width(200).height(100).padTop(10).padRight(10);
        table.row();
        table.add(toggleWireFrame).width(200).height(100).padTop(10).padRight(10);
        table.row();
        table.add(createBodyButton).width(200).height(100).padTop(10).padRight(10);
        table.row();
        table.add(collisionToggle).width(200).height(100).padTop(10).padRight(10);
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

        // Create Body Button listener
        createBodyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!bodyCreator.isActive()) {
                    bodyCreator.startCreation();
                    createBodyButton.setText("Cancel Creation");
                } else {
                    bodyCreator.cancelCreation();
                    createBodyButton.setText("Create Body");
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
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        skin.dispose();
    }
}
