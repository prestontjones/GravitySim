package io.github.gravitygame.entities;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

import io.github.gravitygame.managers.SimulationManager;

public class BodyCreationController extends InputAdapter {
    public enum CreationState { INACTIVE, SETTING_POSITION, SETTING_RADIUS, SETTING_VELOCITY }

    private final SimulationManager simulationManager;
    private final BodyPreviewRenderer previewRenderer;
    private final OrthographicCamera camera;
    private final float velocityScale;

    private CreationState currentState = CreationState.INACTIVE;
    private final BodyCreationData data = new BodyCreationData();

    public BodyCreationController(SimulationManager simulationManager, World physicsWorld, OrthographicCamera camera, float velocityScale) {
        this.simulationManager = simulationManager;
        this.camera = camera;
        this.velocityScale = velocityScale;
        this.previewRenderer = new BodyPreviewRenderer();
    }

    public void startCreation() {
        currentState = CreationState.SETTING_POSITION;
        data.reset();
    }

    public void cancelCreation() {
        currentState = CreationState.INACTIVE;
    }

    public boolean isActive() {
        return currentState != CreationState.INACTIVE;
    }

    public void renderPreview(ShapeRenderer shapeRenderer) {
        previewRenderer.render(this, camera, shapeRenderer);
    }

    private void finalizeCreation() {
        float rawDistance = data.getPosition().dst(data.getRadiusEnd());
        float radius = capMaximum(rawDistance);
        Vector2 velocity = data.getCurrentMouse().cpy().sub(data.getPosition()).scl(velocityScale);

        simulationManager.addBody(data.getPosition().x, data.getPosition().y, radius, velocity);
        startCreation();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!isActive()) return false;

        Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
        Vector2 mouseWorldPos = new Vector2(worldPos.x, worldPos.y);

        switch (currentState) { 
            case SETTING_POSITION:
                data.setPosition(mouseWorldPos);
                currentState = CreationState.SETTING_RADIUS;
                break;

            case SETTING_RADIUS:
                data.setRadiusEnd(mouseWorldPos);
                currentState = CreationState.SETTING_VELOCITY;
                break;

            case SETTING_VELOCITY:
                data.setCurrentMouse(mouseWorldPos);
                finalizeCreation();
                break;
                default:
                break;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (currentState == CreationState.SETTING_RADIUS) {
            Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
            data.setCurrentMouse(new Vector2(worldPos.x, worldPos.y)); 
            return true;
        }
        return false;
    }

    public void updateInput(Vector2 mouseWorldPos) {
        // Update preview position if we are in radius or velocity setting mode
        if (currentState == CreationState.SETTING_RADIUS || currentState == CreationState.SETTING_VELOCITY) {
            data.setCurrentMouse(mouseWorldPos);
        }
    }

    public float capMaximum(float value) {
        float maxValue = 500f;
        return Math.min(value, maxValue);
    }
    

    public InputProcessor getInputProcessor() {
        return this;
    }

    public String getCurrentState() {
        return currentState.name();
    }
    
    public BodyCreationData getCurrentData() {
        return data;
    }
}
