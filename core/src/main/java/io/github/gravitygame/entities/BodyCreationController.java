package io.github.gravitygame.entities;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import io.github.gravitygame.managers.SimulationManager;

public class BodyCreationController {
    private final BodyCreationStateMachine stateMachine;
    private final BodyPreviewRenderer previewRenderer;
    private final BodyInputHandler inputHandler;
    private final BodyCreationLogic creationLogic;

    public BodyCreationController(SimulationManager simulationManager, World physicsWorld, OrthographicCamera camera, float velocityScale) {
    this.stateMachine = new BodyCreationStateMachine();
    this.previewRenderer = new BodyPreviewRenderer();
    this.creationLogic = new BodyCreationLogic(simulationManager, physicsWorld, velocityScale);
    this.inputHandler = new BodyInputHandler(
            stateMachine, 
            creationLogic,
            camera,
            stateMachine.getCurrentData()
        );
    }

    public void updateInput(Vector2 mouseWorldPos) {
        inputHandler.handleInput(mouseWorldPos);
    }

    public void renderPreview(OrthographicCamera camera, ShapeRenderer shapeRenderer) {
        previewRenderer.render(stateMachine.getCurrentState(), 
                             stateMachine.getCurrentData(), 
                             camera, 
                             shapeRenderer);
    }

    public void startCreation() {
        stateMachine.startNewCreation();
    }

    public void cancelCreation() {
        stateMachine.cancelCreation();
    }

    public boolean isActive() {
        return stateMachine.isActive();
    }

    public InputProcessor getInputProcessor() {
        return inputHandler;
    }
}