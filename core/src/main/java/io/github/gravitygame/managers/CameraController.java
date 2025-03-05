package io.github.gravitygame.managers;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import io.github.gravitygame.entities.PhysicsBody; // For checking UI interaction

public class CameraController extends InputAdapter {

    public enum CameraMode { FOLLOW, PAN }

    private final OrthographicCamera camera;
    private final SimulationManager simulationManager;
    private final Stage stage; // To check if the input is hitting a UI element
    private CameraMode mode = CameraMode.PAN;
    private float parallaxFactor = .1f; // Background moves at 50% speed
    private boolean parallaxEnabled = true;

    // For panning mode:
    private Vector3 lastTouch = new Vector3();
    private boolean panningActive = false;

    // Zoom boundaries
    private final float MIN_ZOOM = 0.5f;
    private final float MAX_ZOOM = 5f;

    public CameraController(OrthographicCamera camera, SimulationManager simulationManager, Stage stage) {
        this.camera = camera;
        this.simulationManager = simulationManager;
        this.stage = stage;
    }

    public void setMode(CameraMode mode) {
        this.mode = mode;
    }

    public CameraMode getMode() {
        return mode;
    }

    public void setParallaxFactor(float factor) {
        this.parallaxFactor = factor;
    }

    public float getParallaxFactor() {
        return parallaxFactor;
    }

    /**
     * Update the camera position. In FOLLOW mode, center on the simulation's center of mass.
     */
    public void update(float delta) {
        if (mode == CameraMode.FOLLOW) {
            Array<PhysicsBody> bodies = simulationManager.getBodies();
            if (!bodies.isEmpty()) {
                Vector2 com = calculateSmoothCenter(bodies, delta);
                camera.position.lerp(new Vector3(com, 0), 5 * delta); // Smooth follow
            }
        }
        camera.update();
    }

    /**
     * Returns the parallax offset for the background.
     * The background should use this offset so that it moves slower than the simulation.
     */
    public Vector2 getParallaxOffset() {
        return new Vector2(camera.position.x * -parallaxFactor, camera.position.y * -parallaxFactor);
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (stage.hit(screenX, screenY, true) == null) { // Check if the touch is outside the UI
            if (mode == CameraMode.PAN) {
                lastTouch.set(screenX, screenY, 0);
                panningActive = true;
            }
        }
        return false; // Return false so UI and other processors can still receive the event
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (stage.hit(screenX, screenY, true) == null && mode == CameraMode.PAN && panningActive) {
            Vector3 currentTouch = new Vector3(screenX, screenY, 0);
            // Calculate difference in screen coordinates:
            Vector3 delta = new Vector3(currentTouch).sub(lastTouch);
            // Adjust camera position.
            // Multiply by camera.zoom to account for scaling differences.
            camera.position.add(-delta.x * camera.zoom, delta.y * camera.zoom, 0);
            lastTouch.set(currentTouch);
            camera.update();
        }
        return false; // Return false so UI and other processors can still receive the event
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        panningActive = false;
        return false; // Return false so UI and other processors can still receive the event
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // Adjust zoom level based on scroll (amountY) and clamp between MIN_ZOOM and MAX_ZOOM.
        camera.zoom += amountY * 0.1f;
        if (camera.zoom < MIN_ZOOM) {
            camera.zoom = MIN_ZOOM;
        }
        if (camera.zoom > MAX_ZOOM) {
            camera.zoom = MAX_ZOOM;
        }
        camera.update();
        return true;
    }

    private Vector2 calculateSmoothCenter(Array<PhysicsBody> bodies, float delta) {
        Vector2 center = new Vector2();
        for (PhysicsBody body : bodies) {
            center.add(body.getPosition());
        }
        center.scl(1f / bodies.size);
        return center;
    }

    public void setParallaxEnabled(boolean enabled) {
        this.parallaxEnabled = enabled;
    }

    public boolean isParallaxEnabled() {
        return parallaxEnabled;
    }
}
