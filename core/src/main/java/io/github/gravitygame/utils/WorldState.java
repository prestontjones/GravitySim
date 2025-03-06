package io.github.gravitygame.utils;

import java.util.ArrayList;
import java.util.List;

import io.github.gravitygame.entities.BodyState;

public class WorldState {
    private final List<BodyState> bodyStates = new ArrayList<>();

    public WorldState() {
        // Default constructor
    }

    // Copy constructor
    public WorldState(WorldState other) {
        for (BodyState bodyState : other.getBodyStates()) {
            this.bodyStates.add(new BodyState(bodyState)); // Using the BodyState copy constructor
        }
    }

    public void addBodyState(BodyState bodyState) {
        bodyStates.add(bodyState);
    }

    public List<BodyState> getBodyStates() {
        return bodyStates;
    }
}
