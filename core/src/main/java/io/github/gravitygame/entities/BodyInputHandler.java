package io.github.gravitygame.entities;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class BodyInputHandler extends InputAdapter {
    private final BodyCreationStateMachine stateMachine;
    private final BodyCreationLogic creationLogic;
    private final OrthographicCamera camera;
    private final BodyCreationData data;

    public BodyInputHandler(BodyCreationStateMachine stateMachine,
                          BodyCreationLogic creationLogic,
                          OrthographicCamera camera,
                          BodyCreationData data) {
        this.stateMachine = stateMachine;
        this.creationLogic = creationLogic;
        this.camera = camera;
        this.data = data;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!stateMachine.isActive()) return false;
        
        Vector3 worldPos = camera.unproject(new Vector3(screenX, screenY, 0));
        handleInput(new Vector2(worldPos.x, worldPos.y));
        return true;
    }

    public void handleInput(Vector2 mouseWorldPos) {
        data.setCurrentMouse(mouseWorldPos);
        
        switch (stateMachine.getCurrentState()) {
            case SETTING_POSITION:
                stateMachine.advanceState(mouseWorldPos);
                break;
                
            case SETTING_RADIUS:
                if (data.getPosition() != null) {
                    stateMachine.advanceState(mouseWorldPos);
                }
                break;
                
            case SETTING_VELOCITY:
                stateMachine.advanceState(mouseWorldPos);
                if (!stateMachine.isActive()) {
                    creationLogic.finalizeCreation(data);
                }
                break;
        }
    }

    public InputProcessor getInputProcessor() {
        return this;
    }
}