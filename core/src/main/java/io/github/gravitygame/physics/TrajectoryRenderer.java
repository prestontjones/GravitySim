package io.github.gravitygame.physics;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import io.github.gravitygame.entities.BodyState;
import io.github.gravitygame.managers.WorldStateManager;
import io.github.gravitygame.utils.WorldState;

public class TrajectoryRenderer {
    // Toggle to enable/disable trajectory rendering.
    private boolean enabled = false;
    private final WorldStateManager worldStateManager;

    public TrajectoryRenderer(WorldStateManager manager) {
        this.worldStateManager = manager;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Renders trajectories for each body using the saved world state history.
     * For each unique body (by id), it draws a line through the historical positions.
     *
     * @param renderer The ShapeRenderer used for drawing.
     */
    public void renderTrajectories(ShapeRenderer renderer) {
        if (!enabled || worldStateManager.isStabilizing()) return;

        renderer.begin(ShapeRenderer.ShapeType.Line);
        
        // Use a stronger color for visibility
        renderer.setColor(1.0f, 1.0f, 0, 0.8f); // Bright yellow

        // Get all states from the queue
        Queue<WorldState> history = worldStateManager.getHistoryQueue();
        if (history.size() < 2) {
            renderer.end();
            return;
        }
        
        WorldState[] historyArray = history.toArray(new WorldState[0]);
        
        // Create maps to track body positions across states
        Map<UUID, Vector2[]> bodyPositions = new HashMap<>();
        
        // First, gather all positions for each body
        for (int i = 0; i < historyArray.length; i++) {
            for (BodyState body : historyArray[i].getBodyStates()) {
                UUID id = body.getId();
                
                // Initialize the array for this body if it doesn't exist
                if (!bodyPositions.containsKey(id)) {
                    bodyPositions.put(id, new Vector2[historyArray.length]);
                }
                
                // Store the position
                bodyPositions.get(id)[i] = body.getPosition().cpy();
            }
        }
        
        // Now draw trajectories for each body
        for (UUID id : bodyPositions.keySet()) {
            Vector2[] positions = bodyPositions.get(id);
            
            // Draw a line between each consecutive non-null position
            for (int i = 0; i < positions.length - 1; i++) {
                if (positions[i] != null && positions[i+1] != null) {
                    renderer.line(positions[i].x, positions[i].y, positions[i+1].x, positions[i+1].y);
                }
            }
        }

        renderer.end();
    }
}