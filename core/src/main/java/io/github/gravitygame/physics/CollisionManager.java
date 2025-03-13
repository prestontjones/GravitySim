package io.github.gravitygame.physics;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import io.github.gravitygame.entities.BodyState;
import io.github.gravitygame.managers.SoundManager;
import io.github.gravitygame.managers.WorldStateManager;
import io.github.gravitygame.utils.WorldState;

public class CollisionManager {
    private final WorldStateManager worldStateManager;
    private final SoundManager soundManager;
    private final Set<CollisionPair> processedCollisions = new HashSet<>();

    public CollisionManager(WorldStateManager worldStateManager) {
        this.worldStateManager = worldStateManager;
        this.soundManager = SoundManager.getInstance();
    }

    public void update() {
        WorldState displayedState = worldStateManager.getOldestState();
        if (displayedState == null) return;

        // Changed from Array<BodyState> to List<BodyState> to match the returned type
        List<BodyState> bodies = displayedState.getBodyStates();
        
        Set<CollisionPair> currentCollisions = new HashSet<>();

        // Check all pairs in displayed state
        for (int i = 0; i < bodies.size(); i++) {
            BodyState a = bodies.get(i);
            for (int j = i + 1; j < bodies.size(); j++) {
                BodyState b = bodies.get(j);
                
                if (isColliding(a, b)) {
                    CollisionPair pair = new CollisionPair(a.getId(), b.getId());
                    currentCollisions.add(pair);

                    if (!processedCollisions.contains(pair)) {
                        handleNewCollision(a, b);
                        processedCollisions.add(pair);
                    }
                }
            }
        }

        // Cleanup old collisions that are no longer happening
        processedCollisions.retainAll(currentCollisions);
    }

    private boolean isColliding(BodyState a, BodyState b) {
        float distance = a.getPosition().dst(b.getPosition());
        return distance < (a.getRadius() + b.getRadius());
    }

    private void handleNewCollision(BodyState a, BodyState b) {
        // Calculate intensity using historical velocity data
        float intensity = a.getMass() * b.getMass() * a.getVelocity().dst(b.getVelocity());
        
        String size = intensity > 5000 ? "large" : 
                     intensity > 1000 ? "medium" : "small";
        
        soundManager.playCollisionSound(size);
    }

    private static class CollisionPair {
        final UUID id1, id2;

        CollisionPair(UUID id1, UUID id2) {
            // Ensure consistent ordering using compareTo
            if (id1.compareTo(id2) < 0) {
                this.id1 = id1;
                this.id2 = id2;
            } else {
                this.id1 = id2;
                this.id2 = id1;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CollisionPair)) return false;
            CollisionPair that = (CollisionPair) o;
            return id1.equals(that.id1) && id2.equals(that.id2);
        }

        @Override
        public int hashCode() {
            return id1.hashCode() ^ id2.hashCode();
        }
    }
}
