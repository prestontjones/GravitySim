package io.github.gravitygame.physics;

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
        renderer.begin(ShapeRenderer.ShapeType.Line);

        renderPastState(renderer);
        
        if (debugRenderCurrentState) {
            renderCurrentState(renderer, currentBodies);
        }

        renderer.end();
    }

    private void renderPastState(ShapeRenderer renderer) {
        // Render the oldest state in the queue
        WorldState pastState = stateManager.getOldestState();
        if (pastState != null) {
            for (BodyState pastBody : pastState.getBodyStates()) {
                renderer.setColor(pastBody.getColor());
                renderer.circle(pastBody.getPosition().x, pastBody.getPosition().y, pastBody.getRadius());
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