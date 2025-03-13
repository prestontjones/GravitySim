package io.github.gravitygame.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.gravitygame.Main;
import io.github.gravitygame.entities.Planet;
import io.github.gravitygame.entities.Star;
import io.github.gravitygame.managers.SoundManager;

public class MainMenuScreen implements Screen {
    private static final int STAR_COUNT = 150;
    private static final int PLANET_COUNT = 4;
    private static final float CAMERA_ROTATION_SPEED = .05f;

    private final Main main;
    private Stage stage;
    private Skin skin;
    private ShapeRenderer shapeRenderer;
    private List<Star> stars;
    private List<Planet> planets;
    private OrthographicCamera backgroundCamera;

    public MainMenuScreen(Main main) {
        this.main = main;
    }

    @Override
    public void show() {
        initializeStage();
        setupCamera();
        initializeBackground();
        setupUI();
    }

    private void initializeStage() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        shapeRenderer = new ShapeRenderer();
    }

    private void setupCamera() {
        backgroundCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        backgroundCamera.position.set(backgroundCamera.viewportWidth / 2, backgroundCamera.viewportHeight / 2, 0);
        backgroundCamera.update();
    }

    private void initializeBackground() {
        stars = createStars();
        planets = createPlanets();
    }

    private List<Star> createStars() {
        List<Star> stars = new ArrayList<>(STAR_COUNT);
        for (int i = 0; i < STAR_COUNT; i++) {
            float x = MathUtils.random(0, Gdx.graphics.getWidth());
            float y = MathUtils.random(0, Gdx.graphics.getHeight());
            float distance = MathUtils.random(50, 200);
            stars.add(new Star(x, y, distance));
        }
        return stars;
    }

    private List<Planet> createPlanets() {
        List<Planet> planets = new ArrayList<>(PLANET_COUNT);
        for (int i = 0; i < PLANET_COUNT; i++) {
            float x = MathUtils.random(Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getWidth() * 0.9f);
            float y = MathUtils.random(Gdx.graphics.getHeight() * 0.1f, Gdx.graphics.getHeight() * 0.9f);
            float radius = MathUtils.random(20, 50);
            Color color = new Color(MathUtils.random(0.2f, 0.8f), MathUtils.random(0.2f, 0.8f), MathUtils.random(0.2f, 0.8f), 1);
            boolean hasMoon = MathUtils.randomBoolean(0.6f);
            planets.add(new Planet(x, y, radius, color, hasMoon));
        }
        return planets;
    }

    private void setupUI() {
        skin = new Skin(Gdx.files.internal("skin/neon-ui.json"));
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        TextButton startButton = createButton("Start Game", () -> {
            SoundManager.getInstance().startGameMusic(); // Start the music
            main.setScreen(new GameScreen(main));
        });
        TextButton githubButton = createButton("GitHub", () -> {
            Gdx.net.openURI("https://github.com/prestontjones"); 
            Gdx.app.log("MainMenuScreen", "GitHub link opened");
        });

        table.add(startButton).width(400).height(100).padBottom(20);
        table.row();
        table.add(githubButton).width(400).height(100);
    }

    private TextButton createButton(String text, Runnable action) {
        TextButton button = new TextButton(text, skin);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SoundManager.getInstance().playClickSound();
                action.run();
            }
        });
        return button;
    }

    @Override
    public void render(float delta) {
        clearScreen();
        updateBackground(delta);
        renderBackground();
        drawUI(delta);
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0.02f, 0.02f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void updateBackground(float delta) {
        backgroundCamera.rotate(CAMERA_ROTATION_SPEED * delta);
        backgroundCamera.update();
        planets.forEach(planet -> planet.update(delta));
    }

    private void renderBackground() {
        shapeRenderer.setProjectionMatrix(backgroundCamera.combined);
        renderStars();
        renderPlanets();
    }

    private void renderStars() {
        shapeRenderer.begin(ShapeType.Filled);
        stars.forEach(star -> star.render(shapeRenderer, 0, 0));
        shapeRenderer.end();
    }

    private void renderPlanets() {
        planets.forEach(planet -> {
            renderPlanet(planet);
            if (planet.hasMoon) {
                renderMoon(planet);
            }
        });
    }

    private void renderPlanet(Planet planet) {
        shapeRenderer.begin(ShapeType.Filled);
        
        // Main planet using getters
        shapeRenderer.setColor(planet.getColor());
        shapeRenderer.circle(planet.getX(), planet.getY(), planet.getRadius());

        shapeRenderer.end();
    }

    private void renderMoon(Planet planet) {
        shapeRenderer.begin(ShapeType.Filled);
        
        // Moon position using getters
        float moonX = planet.getX() + planet.getMoonOrbitRadius() * MathUtils.cosDeg(planet.getMoonAngle());
        float moonY = planet.getY() + planet.getMoonOrbitRadius() * MathUtils.sinDeg(planet.getMoonAngle());
        
        // Moon body
        shapeRenderer.setColor(0.85f, 0.85f, 0.85f, 1f);
        shapeRenderer.circle(moonX, moonY, planet.getRadius() * 0.2f);
        
        // Crater detail
        shapeRenderer.setColor(0.5f, 0.5f, 0.5f, 1f);
        shapeRenderer.circle(
            moonX + planet.getRadius() * 0.05f,
            moonY - planet.getRadius() * 0.05f,
            planet.getRadius() * 0.07f
        );
        
        shapeRenderer.end();
    }

    private void drawUI(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        backgroundCamera.viewportWidth = width;
        backgroundCamera.viewportHeight = height;
        backgroundCamera.position.set(width / 2, height / 2, 0);
        backgroundCamera.update();
        initializeBackground();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        shapeRenderer.dispose();
    }
}