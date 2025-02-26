package io.github.gravitygame.utils;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CameraController implements InputProcessor {
    public enum CameraMode {
        PAN, FOLLOW
    }

    private final OrthographicCamera camera;
    private final Vector3 lastTouch = new Vector3();
    private CameraMode mode = CameraMode.PAN;
    private Vector2 followTarget;
    private float followSpeed = 5f;

    public CameraController(OrthographicCamera camera) {
        this.camera = camera;
    }

    public void update(float delta) {
        if (mode == CameraMode.FOLLOW && followTarget != null) {
            Vector3 targetPos = new Vector3(followTarget.x, followTarget.y, 0);
            camera.position.lerp(targetPos, delta * followSpeed);
            camera.update();
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (mode == CameraMode.FOLLOW) {
            setMode(CameraMode.PAN); // Switch to pan mode on touch
        }
        lastTouch.set(screenX, screenY, 0);
        camera.unproject(lastTouch);
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        Vector3 newTouch = new Vector3(screenX, screenY, 0);
        camera.unproject(newTouch);
        Vector3 delta = lastTouch.cpy().sub(newTouch);
        camera.translate(delta);
        lastTouch.set(newTouch);
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        camera.zoom += amountY * 0.1f;
        camera.zoom = Math.max(0.1f, Math.min(camera.zoom, 10f));
        return true;
    }

    // Getters and Setters
    public CameraMode getMode() { return mode; }
    public void setMode(CameraMode mode) { this.mode = mode; }
    public void setFollowTarget(Vector2 target) { this.followTarget = target; }
    public void setFollowSpeed(float speed) { this.followSpeed = speed; }
    
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
    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'touchCancelled'");
    }
}