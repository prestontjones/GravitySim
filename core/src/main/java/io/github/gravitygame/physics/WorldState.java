package io.github.gravitygame.physics;

import java.util.UUID;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class WorldState {
    private final Array<BodySnapshot> snapshots = new Array<>();

    public void addBodyState(UUID id, Vector2 position, Vector2 velocity) {
        snapshots.add(new BodySnapshot(id, position, velocity));
    }

    public Array<BodySnapshot> getSnapshots() {
        return new Array<>(snapshots);
    }

    public static class BodySnapshot {
        public final UUID id;
        public final Vector2 position;
        public final Vector2 velocity;

        public BodySnapshot(UUID id, Vector2 position, Vector2 velocity) {
            this.id = id;
            this.position = position.cpy();
            this.velocity = velocity.cpy();
        }
    }
}