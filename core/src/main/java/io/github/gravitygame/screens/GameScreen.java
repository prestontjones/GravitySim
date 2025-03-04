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
import io.github.gravitygame.entities.BodyCreationController;
import io.github.gravitygame.entities.PhysicsBody;
import io.github.gravitygame.managers.CameraController;
import io.github.gravitygame.managers.SimulationManager;
import io.github.gravitygame.managers.UICreationManager;

public class GameScreen implements Screen {

    private final Main main;
    private Stage stage;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SimulationManager simulationManager;
    private CameraController cameraController;
    private BodyCreationController bodyCreationController;
    private UICreationManager uiCreationManager;
    private Stage uiStage;

    public GameScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        initializeCoreSystems();
        initializeUI();
        setupInput();
    }

    private void initializeCoreSystems() {
        // Initialize camera
        camera = new OrthographicCamera();
        stage = new Stage(new ScreenViewport());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Initialize simulation
        simulationManager = new SimulationManager();

        // Initialize body creation system
        bodyCreationController = new BodyCreationController(
            simulationManager,
            simulationManager.getWorld(),
            camera,
            1.0f
        );

        // Initialize rendering
        shapeRenderer = new ShapeRenderer();

        // Initialize camera controller
        cameraController = new CameraController(camera, simulationManager, stage);
    }

    private void initializeUI() {
        // Create UI stage
        uiStage = new Stage(new ScreenViewport());
        
        // Initialize UI manager
        uiCreationManager = new UICreationManager(
            uiStage,
            simulationManager,
            bodyCreationController,
            cameraController
        );
        
        // Setup UI components
        uiCreationManager.setupUI();
    }

    private void setupInput() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);                     // UI first
        multiplexer.addProcessor(cameraController);            // Camera controls
        multiplexer.addProcessor(bodyCreationController.getInputProcessor()); // Body creation
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        update(delta);
        renderWorld();
        renderUI();
    }

    private void update(float delta) {
        // Update camera and simulation
        cameraController.update(delta);
        simulationManager.update(delta);
        camera.update();

        // Update body creation input
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        bodyCreationController.updateInput(new Vector2(mousePos.x, mousePos.y));
    }

    private void renderWorld() {
        // Clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Render physics bodies
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (PhysicsBody body : simulationManager.getBodies()) {
            body.render(shapeRenderer);
        }
        
        // Render creation preview
        bodyCreationController.renderPreview(camera, shapeRenderer);
        shapeRenderer.end();
    }

    private void renderUI() {
        // Render UI elements
        uiCreationManager.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        simulationManager.dispose();
        uiCreationManager.dispose();
        uiStage.dispose();
    }

    // Required empty implementations
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}