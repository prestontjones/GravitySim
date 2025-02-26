package io.github.gravitygame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.gravitygame.Main;
import io.github.gravitygame.entities.BodyCreator;
import io.github.gravitygame.managers.PredictionManager;
import io.github.gravitygame.managers.SimulationManager;
import io.github.gravitygame.managers.StarsManager;
import io.github.gravitygame.managers.UICreationManager;
import io.github.gravitygame.utils.CameraController;
import io.github.gravitygame.utils.GameRenderer;

public class GameScreen implements Screen {
    private final Main main;
    private OrthographicCamera camera;
    private CameraController cameraController;
    private SimulationManager simulationManager;
    private BodyCreator bodyCreator;
    private UICreationManager uiManager;
    private GameRenderer gameRenderer;
    private PredictionManager predictionManager;

    public GameScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        simulationManager = new SimulationManager();
        bodyCreator = new BodyCreator(simulationManager, new ShapeRenderer(), simulationManager.getWorld(), camera);
        cameraController = new CameraController(camera);

        Stage stage = new Stage(new ScreenViewport());
        uiManager = new UICreationManager(stage, simulationManager, bodyCreator);
        uiManager.setupUI();

        StarsManager starfield = new StarsManager(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 200);

        gameRenderer = new GameRenderer(camera, simulationManager, bodyCreator, uiManager, starfield);
        predictionManager = new PredictionManager(simulationManager, gameRenderer);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiManager.getStage());
        multiplexer.addProcessor(cameraController);
        multiplexer.addProcessor(bodyCreator.getInputProcessor());
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        Vector3 mousePos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(mousePos);
        bodyCreator.updateInput(new Vector2(mousePos.x, mousePos.y));

        simulationManager.update(delta);
        bodyCreator.update();
        predictionManager.update(delta);
        gameRenderer.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
        uiManager.getStage().getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        gameRenderer.dispose();
        uiManager.dispose();
        simulationManager.dispose();
        predictionManager.dispose();
    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resume'");
    }
}