package io.github.gravitygame.entities;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import io.github.gravitygame.managers.SimulationManager;
import io.github.gravitygame.physics.GravityManager;

public class BodyCreationController extends InputAdapter {
    public enum CreationState { INACTIVE, SETTING_POSITION, SETTING_RADIUS, SETTING_VELOCITY, SELECTING_PRIMARY }

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

    public void startOrbiterCreation() {
        if (simulationManager.getBodies().size == 0) {
            // Cannot create an orbiter if there are no primary bodies
            return;
        }
        
        currentState = CreationState.SELECTING_PRIMARY;
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

    private PhysicsBody findNearestSignificantBody(Vector2 position) {
        PhysicsBody nearest = null;
        float minDistance = Float.MAX_VALUE;
        Array<PhysicsBody> bodies = simulationManager.getBodies();
        
        for (PhysicsBody body : bodies) {
            float distance = body.getPosition().dst(position);
            // Consider both distance and mass when finding "significant" bodies
            float significance = distance / (body.getMass() * 0.1f);
            
            if (significance < minDistance) {
                minDistance = significance;
                nearest = body;
            }
        }
        
        return nearest;
    }

    private void createOrbiter(PhysicsBody primaryBody) {
        // Validate primary body exists
        if (primaryBody == null || primaryBody.getBody() == null) return;
    
        // Calculate safe spawn distance
        float safeDistance = primaryBody.getRadius() * 2 + 50f; // 2x radius + buffer
        Vector2 spawnPos = primaryBody.getPosition().cpy()
            .add(new Vector2(safeDistance, 0).rotateDeg((float)Math.random() * 360f));
    
        // Create body with initial velocity
        simulationManager.addBody(
            spawnPos.x, 
            spawnPos.y, 
            primaryBody.getRadius() * 0.2f, 
            new Vector2(0, 0) // Will be set by setupDynamicOrbit
        );
    
        // Get reference safely
        PhysicsBody newBody = simulationManager.getBodies()
            .peek(); // Get last added body
    
        if (newBody != null) {
            GravityManager.setupDynamicOrbit(newBody, primaryBody);
            // Ensure valid physics state
            newBody.getBody().setTransform(spawnPos, 0);
            newBody.getBody().setAwake(true);
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Only process left clicks (button 0) for body creation
        if (button != 0) return false;
        
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
                
            case SELECTING_PRIMARY:
                // Find the nearest significant body to the click position
                PhysicsBody primaryBody = findNearestSignificantBody(mouseWorldPos);
                if (primaryBody != null) {
                    createOrbiter(primaryBody);
                    currentState = CreationState.INACTIVE;
                }
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