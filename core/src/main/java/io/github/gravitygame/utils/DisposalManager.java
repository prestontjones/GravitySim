package io.github.gravitygame.utils;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class DisposalManager {
    private final Array<Disposable> resources = new Array<>();

    public void register(Disposable resource) {
        resources.add(resource);
    }

    public void disposeAll() {
        for(Disposable resource : resources) {
            resource.dispose();
        }
        resources.clear();
    }
}