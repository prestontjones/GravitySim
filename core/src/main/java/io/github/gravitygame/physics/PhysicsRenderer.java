package io.github.gravitygame.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import io.github.gravitygame.entities.BodyState;
import io.github.gravitygame.entities.PhysicsBody;
import io.github.gravitygame.managers.WorldStateManager;
import io.github.gravitygame.utils.WorldState;

public class PhysicsRenderer {
    private final WorldStateManager stateManager;
    private boolean debugRenderCurrentState = false;
    private float timeSinceLastCycle = 0;
    private static final float CYCLE_INTERVAL = 0.05f; // Match the capture interval for smooth animation
    private static final float LINE_THICKNESS = 2.0f; // Thicker lines for body outlines

    public PhysicsRenderer(WorldStateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void setDebugRender(boolean debug) {
        this.debugRenderCurrentState = debug;
    }

    public void update(float delta) {
        // Only cycle states when not in stabilization period
        if (!stateManager.isStabilizing()) {
            timeSinceLastCycle += delta;
            if (timeSinceLastCycle >= CYCLE_INTERVAL) {
                if (stateManager.cycleStates()) {
                    timeSinceLastCycle = 0;
                }
            }
        }
    }

    public void renderBodies(ShapeRenderer renderer, Array<PhysicsBody> currentBodies) {
        // Set line width for thicker borders
        Gdx.gl.glLineWidth(LINE_THICKNESS);
        
        // Enable blending for smoother lines
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderPastState(renderer);
        
        if (debugRenderCurrentState) {
            renderCurrentState(renderer, currentBodies);
        }
        renderer.end();
        
        // Optional: Render filled circles under the outlines for a more polished look
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderFilledBodies(renderer);
        renderer.end();
        
        // Reset line width
        Gdx.gl.glLineWidth(1.0f);
    }

    private void renderPastState(ShapeRenderer renderer) {
        // Render the oldest state in the queue
        WorldState pastState = stateManager.getOldestState();
        if (pastState != null) {
            for (BodyState pastBody : pastState.getBodyStates()) {
                // Enhanced color with slight glow effect (brighter)
                float r = Math.min(1.0f, pastBody.getColor().r * 1.2f);
                float g = Math.min(1.0f, pastBody.getColor().g * 1.2f);
                float b = Math.min(1.0f, pastBody.getColor().b * 1.2f);
                renderer.setColor(r, g, b, 1.0f);
                renderer.circle(pastBody.getPosition().x, pastBody.getPosition().y, pastBody.getRadius());
            }
        }
    }
    
    private void renderFilledBodies(ShapeRenderer renderer) {
        // Render filled circles with lower alpha for a subtle effect
        WorldState pastState = stateManager.getOldestState();
        if (pastState != null) {
            for (BodyState pastBody : pastState.getBodyStates()) {
                // Use a slightly darker shade for the fill
                float r = pastBody.getColor().r * 0.7f;
                float g = pastBody.getColor().g * 0.7f;
                float b = pastBody.getColor().b * 0.7f;
                renderer.setColor(r, g, b, 0.3f); // Low alpha for subtle fill
                renderer.circle(pastBody.getPosition().x, pastBody.getPosition().y, pastBody.getRadius() * 0.9f);
            }
        }
    }

    private void renderCurrentState(ShapeRenderer renderer, Array<PhysicsBody> currentBodies) {
        for (PhysicsBody body : currentBodies) {
            // Semi-transparent red for current bodies in debug mode
            renderBody(renderer, body.getPosition().x, body.getPosition().y, body.getRadius(), 1, 0, 0, 0.5f);
        }
    }

    private void renderBody(ShapeRenderer renderer, float x, float y, float radius, float r, float g, float b, float a) {
        renderer.setColor(r, g, b, a);
        renderer.circle(x, y, radius);
    }
}