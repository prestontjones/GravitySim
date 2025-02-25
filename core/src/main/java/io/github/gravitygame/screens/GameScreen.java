package io.github.gravitygame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.gravitygame.Main;
import io.github.gravitygame.entities.PhysicsBody;
import io.github.gravitygame.managers.SimulationManager;
import io.github.gravitygame.utils.CameraController;

public class GameScreen implements Screen {
    private final Main main;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private CameraController cameraController;
    private Stage stage;
    private Skin skin;
    private SimulationManager simulationManager;

    public GameScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        // Initialize rendering tools
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Initialize camera controller
        cameraController = new CameraController(camera);
        Gdx.input.setInputProcessor(cameraController);

        // Initialize UI
        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("skin/neon-ui.json"));

        // Create UI table
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Add UI elements
        TextButton pauseButton = new TextButton("Pause", skin);
        Slider timestepSlider = new Slider(0.1f, 2f, 0.01f, false, skin);
        timestepSlider.setValue(1f); // Default timestep

        // Add button and slider to the table
        table.add(pauseButton).width(100).height(50).padBottom(10).top().left();
        table.row();
        table.add(timestepSlider).width(200).height(20).padBottom(10).top().left();

        // Add button listener
        pauseButton.addListener(event -> {
            if (event.isHandled()) {
                simulationManager.togglePause();
                pauseButton.setText(simulationManager.isPaused() ? "Resume" : "Pause");
            }
            return true;
        });

        // Add slider listener
        timestepSlider.addListener(event -> {
            if (event.isHandled()) {
                simulationManager.setTimestepScale(timestepSlider.getValue());
            }
            return true;
        });

        // Initialize simulation manager
        simulationManager = new SimulationManager();
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update simulation
        simulationManager.update(delta);

        // Render simulation
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (PhysicsBody body : simulationManager.getBodies()) {
            body.render(shapeRenderer);
        }
        shapeRenderer.end();

        // Render UI
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null); // Clear input processor
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        stage.dispose();
        skin.dispose();
        simulationManager.dispose();
    }
}