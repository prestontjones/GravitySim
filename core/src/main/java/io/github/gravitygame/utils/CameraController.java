package io.github.gravitygame.utils;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class CameraController implements InputProcessor {
    private final OrthographicCamera camera;
    private final Vector3 lastTouch = new Vector3(); // For panning

    public CameraController(OrthographicCamera camera) {
        this.camera = camera;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        lastTouch.set(screenX, screenY, 0);
        camera.unproject(lastTouch); // Convert screen coordinates to world coordinates
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 newTouch = new Vector3(screenX, screenY, 0);
        camera.unproject(newTouch);

        // Pan the camera
        Vector3 delta = lastTouch.cpy().sub(newTouch);
        camera.translate(delta);
        lastTouch.set(newTouch);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        // Zoom the camera
        camera.zoom += amountY * 0.1f;
        camera.zoom = Math.max(0.1f, Math.min(camera.zoom, 10f)); // Clamp zoom
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    // Unused InputProcessor methods
    @Override
    public boolean keyDown(int keycode) { return false; }
    @Override
    public boolean keyUp(int keycode) { return false; }
    @Override
    public boolean keyTyped(char character) { return false; }
    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }
    @Override
    public boolean mouseMoved(int screenX, int screenY) { return false; }
}