package io.github.gravitygame.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

import io.github.gravitygame.managers.SimulationManager;

public class BodyCreator {
    public enum CreationState {
        INACTIVE, SETTING_POSITION, SETTING_RADIUS, SETTING_VELOCITY
    }

    private CreationState state = CreationState.INACTIVE;
    private Vector2 position;
    private Vector2 radiusEnd;
    private Vector2 currentMouse;
    private final SimulationManager simulationManager;
    private final ShapeRenderer shapeRenderer;
    private final World physicsWorld;
    private final OrthographicCamera camera;

    // Velocity scaling factor to reduce the magnitude of the velocity vector.
    private float velocityScale = 0.5f; // Adjust this value as needed.

    public BodyCreator(SimulationManager simulationManager, ShapeRenderer shapeRenderer, World physicsWorld, OrthographicCamera camera) {
        this.simulationManager = simulationManager;
        this.shapeRenderer = shapeRenderer;
        this.physicsWorld = physicsWorld;
        this.camera = camera;
    }

    public void startCreation() {
        state = CreationState.SETTING_POSITION;
        position = null;
        radiusEnd = null;
    }

    public void cancelCreation() {
        state = CreationState.INACTIVE;
    }

    public void updateInput(Vector2 mouseWorldPos) {
        currentMouse = mouseWorldPos;
        
        if (state == CreationState.INACTIVE) return;

        if (Gdx.input.justTouched()) {
            switch (state) {
                case SETTING_POSITION:
                    position = new Vector2(mouseWorldPos);
                    Gdx.app.log("BodyCreator", "Position set to: " + position);
                    state = CreationState.SETTING_RADIUS;
                    break;
                    
                case SETTING_RADIUS:
                    radiusEnd = new Vector2(mouseWorldPos);
                    Gdx.app.log("BodyCreator", "Radius end set to: " + radiusEnd);
                    state = CreationState.SETTING_VELOCITY;
                    break;
                    
                case SETTING_VELOCITY:
                    completeCreation(mouseWorldPos);
                    break;
            }
        }
    }

    private void completeCreation(Vector2 velocityEnd) {
        float radius = position.dst(radiusEnd);
        // Calculate raw velocity vector from the difference between velocityEnd and position.
        Vector2 rawVelocity = new Vector2(velocityEnd).sub(position);
        // Scale the velocity to reduce its magnitude.
        Vector2 scaledVelocity = rawVelocity.scl(velocityScale);
        
        // Debug logs to trace the creation process.
        Gdx.app.log("BodyCreator", "Creating body with:");
        Gdx.app.log("BodyCreator", " - Position: " + position);
        Gdx.app.log("BodyCreator", " - Radius: " + radius);
        Gdx.app.log("BodyCreator", " - Raw Velocity: " + rawVelocity);
        Gdx.app.log("BodyCreator", " - Scaled Velocity: " + scaledVelocity);
        
        simulationManager.addBody(
            position.x, 
            position.y, 
            radius, 
            scaledVelocity
        );

        // Reset creation state for continuous body creation mode.
        state = CreationState.SETTING_POSITION;
        position = null;
        radiusEnd = null;
    }

    public void renderPreview() {
        if (state == CreationState.INACTIVE) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GRAY);

        // Draw position marker
        if (position != null) {
            shapeRenderer.circle(position.x, position.y, 3);
        }

        // Draw radius preview
        if (position != null && (state == CreationState.SETTING_RADIUS || state == CreationState.SETTING_VELOCITY)) {
            float previewRadius = (state == CreationState.SETTING_RADIUS) ? position.dst(currentMouse) : position.dst(radiusEnd);
            shapeRenderer.circle(position.x, position.y, previewRadius);
        }

        // Draw velocity arrow
        if (state == CreationState.SETTING_VELOCITY && position != null) {
            shapeRenderer.line(position, currentMouse);
            drawArrowHead(position, currentMouse);
        }

        shapeRenderer.end();
    }

    private void drawArrowHead(Vector2 start, Vector2 end) {
        Vector2 direction = new Vector2(end).sub(start).nor();
        float arrowSize = 10f;
        Vector2 arrowTip = new Vector2(end);
        
        Vector2 left = new Vector2(direction).rotateDeg(45).scl(-arrowSize);
        Vector2 right = new Vector2(direction).rotateDeg(-45).scl(-arrowSize);
        
        shapeRenderer.line(arrowTip, arrowTip.cpy().add(left));
        shapeRenderer.line(arrowTip, arrowTip.cpy().add(right));
    }

    public boolean isActive() {
        return state != CreationState.INACTIVE;
    }

    public InputProcessor getInputProcessor() {
        return new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (state != CreationState.INACTIVE) {
                    Vector3 screenPos = new Vector3(screenX, screenY, 0);
                    camera.unproject(screenPos); // Convert screen to world coordinates

                    Vector2 worldPos = new Vector2(screenPos.x, screenPos.y);
                    updateInput(worldPos);
                    return true;
                }
                return false;
            }
        };
    }

    public void update() {
        // Additional update logic if needed
    }
    
    // Optional: Setter to adjust the velocity scaling externally.
    public void setVelocityScale(float scale) {
        this.velocityScale = scale;
    }
}
