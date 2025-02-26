package io.github.gravitygame.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import io.github.gravitygame.entities.PhysicsBody;
import io.github.gravitygame.entities.WorldCloner;

public class PredictionWorker implements Runnable {
    private final BlockingQueue<World> inputQueue = new LinkedBlockingQueue<>(1);
    private final Map<UUID, List<Vector2>> predictions = new ConcurrentHashMap<>();
    private volatile boolean running = true;
    private final float timeStep;
    private volatile int stepsToPredict;
    private volatile boolean enabled = true;

    
    // Track original PhysicsBody UUIDs instead of Box2D bodies
    private final Map<Body, UUID> bodyMap = new ConcurrentHashMap<>();

    public PredictionWorker(float timeStep, float predictionSeconds) {
        this.timeStep = timeStep;
        this.stepsToPredict = (int)(predictionSeconds / timeStep);
    }

    public void updateWorld(List<PhysicsBody> bodies) {
        inputQueue.clear();
        if (!bodies.isEmpty()) {
            // Clear old mappings and update with current bodies
            bodyMap.clear();
            bodies.forEach(b -> bodyMap.put(b.getBody(), b.getId()));
            inputQueue.offer(WorldCloner.cloneWorld(bodies.get(0).getWorld()));
        }
    }

    @Override
    public void run() {
        while(running && !Thread.currentThread().isInterrupted()) {
            try {
                World world = inputQueue.poll(100, TimeUnit.MILLISECONDS);
                if(world != null) {
                    simulateWorld(world);
                    world.dispose();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void simulateWorld(World world) {
        Map<UUID, List<Vector2>> newPredictions = new HashMap<>();
        Array<Body> clonedBodies = new Array<>();
        world.getBodies(clonedBodies);

        // Initialize paths with starting positions
        for (Body body : clonedBodies) {
            UUID originalId = findOriginalId(body);
            if (originalId != null) {
                List<Vector2> path = new ArrayList<>();
                path.add(body.getPosition().cpy());
                newPredictions.put(originalId, path);
            }
        }

        // Simulate with gravity
        for (int i = 0; i < stepsToPredict; i++) {
            applyGravity(clonedBodies);
            world.step(timeStep, 6, 2);

            // Update paths
            for (Body body : clonedBodies) {
                UUID originalId = findOriginalId(body);
                if (originalId != null && newPredictions.containsKey(originalId)) {
                    newPredictions.get(originalId).add(body.getPosition().cpy());
                }
                
            }
            
        }

        // Update predictions
        predictions.clear();
        predictions.putAll(newPredictions);
    }

    private UUID findOriginalId(Body clonedBody) {
        // Find the original body with matching UUID
        return bodyMap.entrySet().stream()
            .filter(entry -> entry.getValue().equals(clonedBody.getUserData()))
            .findFirst()
            .map(Map.Entry::getValue)
            .orElse(null);
    }

    private void applyGravity(Array<Body> bodies) {
        // Use the Array's direct access methods
        int size = bodies.size;
        
        for(int i = 0; i < size; i++) {
            Body body1 = bodies.get(i);
            if(body1 == null) continue;
            
            for(int j = i + 1; j < size; j++) {
                Body body2 = bodies.get(j);
                if(body2 == null) continue;
    
                Vector2 pos1 = body1.getPosition();
                Vector2 pos2 = body2.getPosition();
                Vector2 delta = pos2.cpy().sub(pos1);
                
                float distanceSq = delta.len2();
                if(distanceSq < 0.1f) continue;
    
                float forceMagnitude = 100f * body1.getMass() * body2.getMass() / distanceSq;
                Vector2 force = delta.nor().scl(forceMagnitude);
                
                body1.applyForceToCenter(force, true);
                body2.applyForceToCenter(force.scl(-1), true); 
            }
        }
        
    }

    public Map<UUID, List<Vector2>> getPredictions() {
        return new ConcurrentHashMap<>(predictions);
    }

    public enum QualityLevel { HIGH, MEDIUM, LOW }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            predictions.clear();
        }
    }

    public void shutdown() {
        running = false;
        predictions.clear();
    }
    
    public void clearPredictions() {
        predictions.clear();
    }
    
    public void setPredictionQuality(QualityLevel level) {
        switch(level) {
            case HIGH: stepsToPredict = 300; break;
            case MEDIUM: stepsToPredict = 150; break;
            case LOW: stepsToPredict = 75; break;
        }
    }
}