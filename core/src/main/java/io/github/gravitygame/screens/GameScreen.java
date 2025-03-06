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
import io.github.gravitygame.managers.CameraController;
import io.github.gravitygame.managers.SimulationManager;
import io.github.gravitygame.managers.StarsManager;
import io.github.gravitygame.managers.UICreationManager;
import io.github.gravitygame.managers.WorldStateManager;
import io.github.gravitygame.physics.PhysicsRenderer;
import io.github.gravitygame.physics.TrajectoryRenderer;

public class GameScreen implements Screen {

    private final Main main;
    private Stage stage;
    private OrthographicCamera camera;
    private ShapeRenderer shapeRenderer;
    private SimulationManager simulationManager;
    private CameraController cameraController;
    private BodyCreationController bodyCreationController;
    private UICreationManager uiCreationManager;
    private StarsManager starsManager;
    private Stage uiStage;
    private TrajectoryRenderer trajectoryRenderer;

    // Added WorldStateQueue for state tracking
    private WorldStateManager worldStateManager;
    private PhysicsRenderer physicsRenderer;

    public GameScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        initializeCoreSystems();
        initializeUI();
        initializeStars();
        setupInput();
    }

    private void initializeCoreSystems() {
        // Initialize camera
        camera = new OrthographicCamera();
        stage = new Stage(new ScreenViewport());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // Initialize simulation, and WorldStateManager
        simulationManager = new SimulationManager();
        worldStateManager = new WorldStateManager();
    
        // Connect them
        simulationManager.setWorldStateManager(worldStateManager);
        worldStateManager.setSimulationManager(simulationManager);

        trajectoryRenderer = new TrajectoryRenderer(worldStateManager);
        trajectoryRenderer.setEnabled(true);

        // Initialize body creation system
        bodyCreationController = new BodyCreationController(
            simulationManager,
            simulationManager.getWorld(),
            camera,
            1.0f
        );

        // Initialize rendering
        shapeRenderer = new ShapeRenderer();
        physicsRenderer = new PhysicsRenderer(worldStateManager); // Pass world state queue

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

    private void initializeStars() {
        // Create a StarsManager with a number of stars based on screen size
        starsManager = new StarsManager(3000, 3000, 3000, camera);
    }

    private void setupInput() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);                     // UI first
        multiplexer.addProcessor(bodyCreationController.getInputProcessor()); // Body creation
        multiplexer.addProcessor(cameraController);            // Camera controls
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        update(delta);
        renderWorld();
        starsManager.render(shapeRenderer, camera);
        uiCreationManager.render(Gdx.graphics.getDeltaTime());
    }

    private void update(float delta) {
        // Update camera and simulation
        cameraController.update(delta);
        simulationManager.update(delta);
        worldStateManager.update(delta);
        physicsRenderer.update(delta);
        camera.update();
    
        // Update body creation input
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        bodyCreationController.updateInput(new Vector2(mousePos.x, mousePos.y));
    }

    private void renderWorld() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    
        shapeRenderer.setProjectionMatrix(camera.combined);
        
        // Render historical physics state from WorldStateQueue
        physicsRenderer.renderBodies(shapeRenderer, simulationManager.getBodies());

        // Render body preview
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        bodyCreationController.renderPreview(shapeRenderer);
        shapeRenderer.end();

        trajectoryRenderer.renderTrajectories(shapeRenderer);
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
        stage.dispose();
    }

    // Required empty implementations
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
