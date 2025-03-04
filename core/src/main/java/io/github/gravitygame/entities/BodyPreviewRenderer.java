package io.github.gravitygame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class BodyPreviewRenderer {
    private static final Color PREVIEW_COLOR = Color.GRAY;
    private static final float POSITION_MARKER_RADIUS = 3f;
    private static final float ARROW_HEAD_SIZE = 10f;

    public void render(BodyCreationStateMachine.CreationState state, 
                      BodyCreationData data,
                      OrthographicCamera camera,
                      ShapeRenderer shapeRenderer) {
        if (state == BodyCreationStateMachine.CreationState.INACTIVE) return;

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(PREVIEW_COLOR);

        drawPositionMarker(data, shapeRenderer);
        drawRadiusPreview(state, data, shapeRenderer);
        drawVelocityArrow(state, data, shapeRenderer);

        shapeRenderer.end();
    }

    private void drawPositionMarker(BodyCreationData data, ShapeRenderer shapeRenderer) {
        if (data.getPosition() != null) {
            shapeRenderer.circle(data.getPosition().x, 
                               data.getPosition().y, 
                               POSITION_MARKER_RADIUS);
        }
    }

    private void drawRadiusPreview(BodyCreationStateMachine.CreationState state, 
                                 BodyCreationData data,
                                 ShapeRenderer shapeRenderer) {
        if (data.getPosition() == null) return;

        switch (state) {
            case SETTING_RADIUS:
                float liveRadius = data.getPosition().dst(data.getCurrentMouse());
                shapeRenderer.circle(data.getPosition().x, data.getPosition().y, liveRadius);
                break;
            case SETTING_VELOCITY:
                float fixedRadius = data.getPosition().dst(data.getRadiusEnd());
                shapeRenderer.circle(data.getPosition().x, data.getPosition().y, fixedRadius);
                break;
        }
    }

    private void drawVelocityArrow(BodyCreationStateMachine.CreationState state,
                                 BodyCreationData data,
                                 ShapeRenderer shapeRenderer) {
        if (state != BodyCreationStateMachine.CreationState.SETTING_VELOCITY) return;
        if (data.getPosition() == null || data.getCurrentMouse() == null) return;

        shapeRenderer.line(data.getPosition(), data.getCurrentMouse());
        drawArrowHead(data.getPosition(), data.getCurrentMouse(), shapeRenderer);
    }

    private void drawArrowHead(Vector2 start, Vector2 end, ShapeRenderer shapeRenderer) {
        Vector2 direction = end.cpy().sub(start).nor();
        Vector2 arrowTip = end.cpy();
        
        Vector2 left = direction.cpy().rotateDeg(45).scl(-ARROW_HEAD_SIZE);
        Vector2 right = direction.cpy().rotateDeg(-45).scl(-ARROW_HEAD_SIZE);
        
        shapeRenderer.line(arrowTip, arrowTip.cpy().add(left));
        shapeRenderer.line(arrowTip, arrowTip.cpy().add(right));
    }
}