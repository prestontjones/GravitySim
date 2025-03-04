package io.github.gravitygame.managers;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class ViewportManager {
    private final OrthographicCamera gameCamera;
    private final Stage uiStage;
    private final Viewport uiViewport;

    public ViewportManager(OrthographicCamera camera, Stage stage) {
        this.gameCamera = camera;
        this.uiStage = stage;
        this.uiViewport = new ScreenViewport(); // Adapts UI to screen size
        uiStage.setViewport(uiViewport);
    }

    public void resize(int width, int height) {
        uiViewport.update(width, height, true);
    }
}
