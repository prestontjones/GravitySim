package io.github.gravitygame.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
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
        if (!enabled) return;

        // Retrieve the entire history of world states.
        List<WorldState> history = worldStateManager.getHistory();

        // Map each body's id (as a String) to a list of positions.
        Map<String, List<Vector2>> trajectories = new HashMap<>();

        // Iterate over the history in chronological order.
        for (WorldState state : history) {
            for (BodyState bodyState : state.getBodyStates()) {
                String id = bodyState.getId().toString();
                if (!trajectories.containsKey(id)) {
                    trajectories.put(id, new ArrayList<>());
                }
                trajectories.get(id).add(bodyState.getPosition());
            }
        }

        // Draw each trajectory as a line connecting the saved positions.
        renderer.begin(ShapeRenderer.ShapeType.Line);
        // Here we use yellow for all trajectories, or you could use a per-body color.
        renderer.setColor(Color.YELLOW);
        for (List<Vector2> path : trajectories.values()) {
            if (path.size() < 2) continue;
            for (int i = 0; i < path.size() - 1; i++) {
                Vector2 p1 = path.get(i);
                Vector2 p2 = path.get(i + 1);
                renderer.line(p1.x, p1.y, p2.x, p2.y);
            }
        }
        renderer.end();
    }
}
