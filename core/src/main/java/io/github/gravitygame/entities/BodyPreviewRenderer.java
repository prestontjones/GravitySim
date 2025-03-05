package io.github.gravitygame.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class BodyPreviewRenderer {
    private static final Color PREVIEW_COLOR = Color.GRAY;
    private static final float POSITION_MARKER_RADIUS = 3f;
    private static final float ARROW_HEAD_SIZE = 10f;

    public void render(BodyCreationController controller,
                      OrthographicCamera camera,
                      ShapeRenderer shapeRenderer) {
        if (!controller.isActive()) return;  // Check if creation is active

        shapeRenderer.setColor(PREVIEW_COLOR);
        BodyCreationData data = controller.getCurrentData();

        drawPositionMarker(data, shapeRenderer);
        drawRadiusPreview(controller, data, shapeRenderer);
        drawVelocityArrow(controller, data, shapeRenderer);
    }

    private void drawPositionMarker(BodyCreationData data, ShapeRenderer shapeRenderer) {
        if (data.getPosition() != null) {
            shapeRenderer.circle(data.getPosition().x, data.getPosition().y, POSITION_MARKER_RADIUS);
        }
    }

    private void drawRadiusPreview(BodyCreationController controller, BodyCreationData data, ShapeRenderer shapeRenderer) {
        if (data.getPosition() == null) return;

        switch (controller.getCurrentState()) {
            case "SETTING_RADIUS":
                float rawDistance = data.getPosition().dst(data.getCurrentMouse());
                float liveRadius = controller.capMaximum(rawDistance);
                shapeRenderer.circle(data.getPosition().x, data.getPosition().y, liveRadius);
                break;
            case "SETTING_VELOCITY":
                float rawFixedDistance = data.getPosition().dst(data.getRadiusEnd());
                float fixedRadius = controller.capMaximum(rawFixedDistance);
                shapeRenderer.circle(data.getPosition().x, data.getPosition().y, fixedRadius);
                break;
        }
    }

    private void drawVelocityArrow(BodyCreationController controller, BodyCreationData data, ShapeRenderer shapeRenderer) {
        if (!controller.getCurrentState().equals("SETTING_VELOCITY")) return;
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
