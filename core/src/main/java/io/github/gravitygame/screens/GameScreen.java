package io.github.gravitygame.screens;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
import io.github.gravitygame.physics.PredictionWorker;
import io.github.gravitygame.utils.CameraController;

public class GameScreen implements Screen {
    private final Main main;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private UICreationManager uiManager;
    private CameraController cameraController;
    private SimulationManager simulationManager;
    private BodyCreator bodyCreator;

    private PredictionWorker predictionWorker;
    private ExecutorService executor;
    private Map<UUID, List<Vector2>> currentPredictions = new HashMap<>();
    private float predictionAccumulator;

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
        
        //initialize body creator
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

        predictionWorker = new PredictionWorker(1/60f, 5);
        executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "PredictionWorker");
            t.setDaemon(true);
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        });
        executor.execute(predictionWorker);
    }

     @Override
    public void render(float delta) {

        if (currentPredictions == null) {
            currentPredictions = new HashMap<>();
        }
    
        // Prediction updates
        if (simulationManager.isPredictionsEnabled()) {
            predictionAccumulator += delta;
            while (predictionAccumulator >= 0.016f) {
                if (!simulationManager.getBodies().isEmpty()) {
                    predictionWorker.updateWorld(simulationManager.getBodies());
                }
                predictionAccumulator -= 0.016f;
            }
            currentPredictions = new HashMap<>(predictionWorker.getPredictions());
        } else {
            currentPredictions.clear(); // Now safe to call
        }

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
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (PhysicsBody body : simulationManager.getBodies()) {
            body.render(shapeRenderer);
        }
        shapeRenderer.end();

        // Render creation preview
        bodyCreator.renderPreview();
    
        // Render UI
        uiManager.getStage().act(delta);
        uiManager.getStage().draw();

        //Render Prediction Paths
        if (uiManager.isPredictionsEnabled()) {
            renderPredictionPaths();
        }
    }

    private void renderPredictionPaths() {
        if (!uiManager.arePredictionsEnabled()) return;
        
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED); // Use visible color
        
        for (PhysicsBody body : simulationManager.getBodies()) {
            List<Vector2> path = currentPredictions.get(body.getId());
            if (path != null && path.size() > 1) {
                Gdx.gl.glLineWidth(2f); // Make lines visible
                for (int i = 1; i < path.size(); i++) {
                    Vector2 start = path.get(i-1);
                    Vector2 end = path.get(i);
                    shapeRenderer.line(start.x, start.y, end.x, end.y);
                }
            }
        }
        
        shapeRenderer.end();
        Gdx.gl.glLineWidth(1f); // Reset
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
        predictionWorker.shutdown();
        try {
            executor.shutdown();
            if(!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        if (currentPredictions != null) {
            currentPredictions.clear();
        }
    }
}