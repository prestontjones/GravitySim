package io.github.gravitygame.utils;

import java.util.ArrayList;
import java.util.List;

import io.github.gravitygame.entities.BodyState;

public class WorldState {
    private final List<BodyState> bodyStates = new ArrayList<>();

    public void addBodyState(BodyState bodyState) {
        bodyStates.add(bodyState);
    }

    public List<BodyState> getBodyStates() {
        return bodyStates;
    }
}