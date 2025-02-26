package io.github.gravitygame.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import io.github.gravitygame.entities.BodyCreator;
import io.github.gravitygame.entities.PhysicsBody;
import io.github.gravitygame.managers.SimulationManager;
import io.github.gravitygame.managers.StarsManager;
import io.github.gravitygame.managers.UICreationManager;

public class GameRenderer {
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private final StarsManager starfield;
    private final SimulationManager simulationManager;
    private final BodyCreator bodyCreator;
    private final UICreationManager uiManager;
    private Map<UUID, List<Vector2>> currentPredictions = new HashMap<>();

    public GameRenderer(OrthographicCamera camera, SimulationManager simulationManager, 
                        BodyCreator bodyCreator, UICreationManager uiManager, StarsManager starfield) {
        this.camera = camera;
        this.simulationManager = simulationManager;
        this.bodyCreator = bodyCreator;
        this.uiManager = uiManager;
        this.starfield = starfield;
        this.shapeRenderer = new ShapeRenderer();
    }

    public void render(float delta) {
        // Clear screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Render starfield
        starfield.render(shapeRenderer, camera);

        // Render physics bodies
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (PhysicsBody body : simulationManager.getBodies()) {
            body.render(shapeRenderer);
        }
        shapeRenderer.end();

        // Render UI
        uiManager.getStage().act(delta);
        uiManager.getStage().draw();

        // Render predictions
        if (uiManager.isPredictionsEnabled()) {
            renderPredictionPaths();
        }
    }

    private void renderPredictionPaths() {
        if (!uiManager.isPredictionsEnabled()) return;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        for (PhysicsBody body : simulationManager.getBodies()) {
            List<Vector2> path = currentPredictions.get(body.getId());
            if (path != null && path.size() > 1) {
                Gdx.gl.glLineWidth(2f);
                for (int i = 1; i < path.size(); i++) {
                    Vector2 start = path.get(i - 1);
                    Vector2 end = path.get(i);
                    shapeRenderer.line(start.x, start.y, end.x, end.y);
                }
            }
        }

        shapeRenderer.end();
        Gdx.gl.glLineWidth(1f);
    }

    public void dispose() {
        shapeRenderer.dispose();
    }

    public void updatePredictions(Map<UUID, List<Vector2>> predictions) {
        this.currentPredictions = new HashMap<>(predictions);
    }
}
