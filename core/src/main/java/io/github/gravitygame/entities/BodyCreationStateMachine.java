package io.github.gravitygame.entities;

import com.badlogic.gdx.math.Vector2;

public class BodyCreationStateMachine {
    public enum CreationState { INACTIVE, SETTING_POSITION, SETTING_RADIUS, SETTING_VELOCITY }
    
    private CreationState currentState = CreationState.INACTIVE;
    private BodyCreationData currentData = new BodyCreationData();

    public void startNewCreation() {
        currentState = CreationState.SETTING_POSITION;
        currentData.reset();
    }

    public void cancelCreation() {
        currentState = CreationState.INACTIVE;
    }

    public void advanceState(Vector2 position) {
        switch (currentState) {
            case SETTING_POSITION:
                currentData.setPosition(position);
                currentState = CreationState.SETTING_RADIUS;
                break;
            case SETTING_RADIUS:
                currentData.setRadiusEnd(position);
                currentState = CreationState.SETTING_VELOCITY;
                break;
            case SETTING_VELOCITY:
                currentData.setCurrentMouse(position);
                currentState = CreationState.INACTIVE;
                break;
        }
    }

    public boolean isActive() {
        return currentState != CreationState.INACTIVE;
    }

    public CreationState getCurrentState() {
        return currentState;
    }

    public BodyCreationData getCurrentData() {
        return currentData;
    }
}