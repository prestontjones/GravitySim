package io.github.gravitygame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.gravitygame.Main;
import io.github.gravitygame.entities.BodyCreator;
import io.github.gravitygame.entities.PhysicsBody;
import io.github.gravitygame.managers.SimulationManager;
import io.github.gravitygame.managers.UICreationManager;
import io.github.gravitygame.utils.CameraController;

public class GameScreen implements Screen {
    private final Main main;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private UICreationManager uiManager;
    private CameraController cameraController;
    private SimulationManager simulationManager;
    private BodyCreator bodyCreator;

    public GameScreen(Main main) {
        this.main = main;
    }

     @Override
    public void show() {
        // Initialize core systems FIRST
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        
        // Initialize simulation BEFORE body creator
        simulationManager = new SimulationManager();
        
        // THEN initialize body creator
        bodyCreator = new BodyCreator(simulationManager, shapeRenderer, simulationManager.getWorld(), camera);

        // Initialize camera controller
        cameraController = new CameraController(camera);

        // Initialize UI Manager
        Stage stage = new Stage(new ScreenViewport());
        uiManager = new UICreationManager(stage, simulationManager, bodyCreator);
        uiManager.setupUI();

        // Set up input multiplexer
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiManager.getStage()); // UI first
        multiplexer.addProcessor(cameraController); // Then camera
        multiplexer.addProcessor(bodyCreator.getInputProcessor()); // Then body creation
        Gdx.input.setInputProcessor(multiplexer);
    }

     @Override
    public void render(float delta) {
        // Get mouse position in world coordinates
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        bodyCreator.updateInput(new Vector2(mousePos.x, mousePos.y));

        // Clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update systems
        simulationManager.update(delta);
        bodyCreator.update();

        // Render simulation
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (PhysicsBody body : simulationManager.getBodies()) {
            body.render(shapeRenderer);
        }
        shapeRenderer.end();

        // Render creation preview
        bodyCreator.renderPreview();

        // Render UI
        uiManager.getStage().act(delta);
        uiManager.getStage().draw();
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        uiManager.getStage().getViewport().update(width, height, true);
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
        uiManager.dispose();
        simulationManager.dispose();
    }
}