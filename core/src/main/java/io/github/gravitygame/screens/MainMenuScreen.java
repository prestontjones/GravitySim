package io.github.gravitygame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.gravitygame.Main;

public class MainMenuScreen implements Screen {
    private final Main main;
    private Stage stage;
    private Skin skin;

    public MainMenuScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage); // Set the stage to handle input

        skin = new Skin(Gdx.files.internal("skin/neon-ui.json")); // Load Neon skin
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Create buttons
        TextButton startButton = new TextButton("Start Game", skin);
        TextButton settingsButton = new TextButton("Settings", skin);
        TextButton githubButton = new TextButton("GitHub", skin);

        // Add buttons to the table
        table.add(startButton).width(200).height(50).padBottom(10);
        table.row();
        table.add(settingsButton).width(200).height(50).padBottom(10);
        table.row();
        table.add(githubButton).width(200).height(50).padBottom(10);

        // Add button listeners
        startButton.addListener(event -> {
            if (event.isHandled()) {
                main.setScreen(new GameScreen(main)); // Switch to the game screen
            }
            return true;
        });

        settingsButton.addListener(event -> {
            if (event.isHandled()) {
                // TODO: Add logic to open the settings screen
                Gdx.app.log("MainMenuScreen", "Settings button clicked");
            }
            return true;
        });

        githubButton.addListener(event -> {
            if (event.isHandled()) {
                // Open GitHub link in the default browser
                Gdx.net.openURI("https://github.com/prestontjones");
            }
            return true;
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null); // Clear the input processor
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
