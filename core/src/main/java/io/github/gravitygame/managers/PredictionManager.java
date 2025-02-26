package io.github.gravitygame.managers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.github.gravitygame.physics.PredictionWorker;
import io.github.gravitygame.utils.GameRenderer;

public class PredictionManager {
    private final PredictionWorker predictionWorker;
    private final ExecutorService executor;
    private final SimulationManager simulationManager;
    private final GameRenderer gameRenderer;
    private float predictionAccumulator;

    public PredictionManager(SimulationManager simulationManager, GameRenderer gameRenderer) {
        this.simulationManager = simulationManager;
        this.gameRenderer = gameRenderer;
        this.predictionWorker = new PredictionWorker(1 / 60f, 5);
        this.executor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "PredictionWorker");
            t.setDaemon(true);
            t.setPriority(Thread.MIN_PRIORITY);
            return t;
        });
        executor.execute(predictionWorker);
    }

    public void update(float delta) {
        if (simulationManager.isPredictionsEnabled()) {
            predictionAccumulator += delta;
            while (predictionAccumulator >= 0.016f) {
                if (!simulationManager.getBodies().isEmpty()) {
                    predictionWorker.updateWorld(simulationManager.getBodies());
                }
                predictionAccumulator -= 0.016f;
            }
            gameRenderer.updatePredictions(predictionWorker.getPredictions());
        }
    }

    public void dispose() {
        predictionWorker.shutdown();
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
