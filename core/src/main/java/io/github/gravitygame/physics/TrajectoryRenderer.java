package io.github.gravitygame.physics;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

import io.github.gravitygame.entities.BodyState;
import io.github.gravitygame.managers.WorldStateManager;
import io.github.gravitygame.utils.WorldState;

public class TrajectoryRenderer {
    // Toggle to enable/disable trajectory rendering.
    private boolean enabled = false;
    private final WorldStateManager worldStateManager;
    private static final float TRAJECTORY_THICKNESS = 2.5f; // Thicker lines for trajectories
    
    // Colors for gradient effect
    private static final float[] START_COLOR = {1.0f, 1.0f, 0.4f, 0.9f}; // Bright yellow
    private static final float[] END_COLOR = {1.0f, 0.6f, 0.0f, 0.7f};   // Orange-ish

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

        // Set line width for thicker trajectories
        Gdx.gl.glLineWidth(TRAJECTORY_THICKNESS);
        
        // Enable blending for smoother lines
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        renderer.begin(ShapeRenderer.ShapeType.Line);

        // Get all states from the queue
        Queue<WorldState> history = worldStateManager.getHistoryQueue();
        if (history.size() < 2) {
            renderer.end();
            Gdx.gl.glLineWidth(1.0f); // Reset line width
            return;
        }
        
        WorldState[] historyArray = history.toArray(new WorldState[0]);
        
        // Create maps to track body positions across states
        Map<UUID, Vector2[]> bodyPositions = new HashMap<>();
        Map<UUID, float[]> bodyColors = new HashMap<>();
        
        // First, gather all positions and colors for each body
        for (int i = 0; i < historyArray.length; i++) {
            for (BodyState body : historyArray[i].getBodyStates()) {
                UUID id = body.getId();
                
                // Initialize the arrays for this body if they don't exist
                if (!bodyPositions.containsKey(id)) {
                    bodyPositions.put(id, new Vector2[historyArray.length]);
                    bodyColors.put(id, new float[]{
                        body.getColor().r,
                        body.getColor().g,
                        body.getColor().b,
                        0.8f
                    });
                }
                
                // Store the position
                bodyPositions.get(id)[i] = body.getPosition().cpy();
            }
        }
        
        // Now draw trajectories for each body
        for (UUID id : bodyPositions.keySet()) {
            Vector2[] positions = bodyPositions.get(id);
            float[] color = bodyColors.get(id);
            
            // Draw a line between each consecutive non-null position with gradient effect
            for (int i = 0; i < positions.length - 1; i++) {
                if (positions[i] != null && positions[i+1] != null) {
                    // Create gradient effect - older segments fade out
                    float segmentProgress = (float)i / (positions.length - 2);
                    
                    // Custom color with gradient effect based on body's color
                    renderer.setColor(
                        color[0],
                        color[1],
                        color[2],
                        0.9f - (0.4f * segmentProgress) // Fade out older segments
                    );
                    
                    renderer.line(positions[i].x, positions[i].y, positions[i+1].x, positions[i+1].y);
                }
            }
        }

        renderer.end();
        
        // Reset line width to default
        Gdx.gl.glLineWidth(1.0f);
    }
}