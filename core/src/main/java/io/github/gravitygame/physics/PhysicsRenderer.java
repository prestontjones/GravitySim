package io.github.gravitygame.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import io.github.gravitygame.entities.BodyState;
import io.github.gravitygame.entities.PhysicsBody;
import io.github.gravitygame.managers.WorldStateManager;
import io.github.gravitygame.utils.WorldState;

/**
 * Enhanced renderer for physics bodies with professional visual effects.
 * Implements glow effects, smooth transitions, and depth perception.
 */
public class PhysicsRenderer {
    private final WorldStateManager stateManager;
    private boolean debugRenderEnabled = false;
    private float timeSinceLastCycle = 0;
    
    // Rendering constants
    private static final float CYCLE_INTERVAL = 0.05f;
    private static final float GLOW_INTENSITY = 1.5f;
    private static final float BASE_OUTLINE_THICKNESS = 2.5f;
    private static final float INNER_SHADOW_ALPHA = 0.35f;
    private static final float OUTER_GLOW_ALPHA = 0.4f;
    private static final int GLOW_SEGMENTS = 36;
    
    // Visual effect parameters
    private final Vector2 lightSource = new Vector2(1.0f, 1.0f).nor();
    private float effectTime = 0f;
    
    public PhysicsRenderer(WorldStateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void setDebugRender(boolean debug) {
        this.debugRenderEnabled = debug;
    }

    public void update(float delta) {
        effectTime += delta;
        
        // Only cycle states when not in stabilization period
        if (!stateManager.isStabilizing()) {
            timeSinceLastCycle += delta;
            if (timeSinceLastCycle >= CYCLE_INTERVAL) {
                if (stateManager.cycleStates()) {
                    timeSinceLastCycle = 0;
                }
            }
        }
    }

    public void renderBodies(ShapeRenderer renderer, Array<PhysicsBody> currentBodies) {
        // Enable blending for all rendering
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        // Render body glow effects
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderGlowEffects(renderer);
        
        // Render filled bodies
        renderFilledBodies(renderer);
        
        // Render inner shadows to create depth
        renderInnerShadows(renderer);
        renderer.end();
        
        // Render outlines
        Gdx.gl.glLineWidth(BASE_OUTLINE_THICKNESS);
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderOutlines(renderer);
        renderer.end();
        
        // Debug rendering if enabled
        if (debugRenderEnabled) {
            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderCurrentState(renderer, currentBodies);
            renderer.end();
        }
        
        // Reset line width
        Gdx.gl.glLineWidth(1.0f);
    }
    
    private void renderGlowEffects(ShapeRenderer renderer) {
        WorldState state = stateManager.getOldestState();
        if (state != null) {
            for (BodyState body : state.getBodyStates()) {
                Color glowColor = body.getColor().cpy();
                float baseRadius = body.getRadius();
                
                // Multi-layered glow for depth
                for (int i = 0; i < 3; i++) {
                    float layerRadius = baseRadius * (1.15f + i * 0.15f);
                    float alpha = OUTER_GLOW_ALPHA * (3 - i) / 3f;
                    glowColor.a = alpha;
                    renderer.setColor(glowColor);
                    renderer.circle(body.getPosition().x, body.getPosition().y, layerRadius, GLOW_SEGMENTS);
                }
            }
        }
    }
    
    private void renderFilledBodies(ShapeRenderer renderer) {
        WorldState state = stateManager.getOldestState();
        if (state != null) {
            for (BodyState body : state.getBodyStates()) {
                // Base color with slight adjustment for better visual appearance
                Color fillColor = enhanceColor(body.getColor(), 0.9f);
                fillColor.a = 0.9f;
                renderer.setColor(fillColor);
                renderer.circle(body.getPosition().x, body.getPosition().y, body.getRadius(), GLOW_SEGMENTS);
            }
        }
    }
    
    private void renderInnerShadows(ShapeRenderer renderer) {
        WorldState state = stateManager.getOldestState();
        if (state != null) {
            for (BodyState body : state.getBodyStates()) {
                // Calculate shadow offset based on light direction
                float shadowOffsetX = -lightSource.x * body.getRadius() * 0.2f;
                float shadowOffsetY = -lightSource.y * body.getRadius() * 0.2f;
                
                // Draw shadow as partially transparent black
                renderer.setColor(0, 0, 0, INNER_SHADOW_ALPHA);
                renderer.circle(
                    body.getPosition().x + shadowOffsetX,
                    body.getPosition().y + shadowOffsetY,
                    body.getRadius() * 0.85f,
                    GLOW_SEGMENTS
                );
            }
        }
    }
    
    private void renderOutlines(ShapeRenderer renderer) {
        WorldState state = stateManager.getOldestState();
        if (state != null) {
            for (BodyState body : state.getBodyStates()) {
                // Enhanced outline color (slightly brighter than base)
                Color outlineColor = enhanceColor(body.getColor(), GLOW_INTENSITY);
                renderer.setColor(outlineColor);
                
                // Pulse effect for outlines
                float pulseAmount = 0.05f * MathUtils.sin(effectTime * 3f);
                float radiusWithPulse = body.getRadius() * (1.0f + pulseAmount);
                
                renderer.circle(body.getPosition().x, body.getPosition().y, radiusWithPulse, GLOW_SEGMENTS);
            }
        }
    }
    
    private void renderCurrentState(ShapeRenderer renderer, Array<PhysicsBody> currentBodies) {
        renderer.setColor(1, 0.3f, 0.3f, 0.6f);
        for (PhysicsBody body : currentBodies) {
            renderer.circle(body.getPosition().x, body.getPosition().y, body.getRadius(), GLOW_SEGMENTS);
        }
    }
    
    /**
     * Enhances a color by increasing its brightness while preserving hue.
     * 
     * @param original The original color
     * @param factor Brightness multiplier (>1 brightens, <1 darkens)
     * @return Enhanced color
     */
    private Color enhanceColor(Color original, float factor) {
        Color enhanced = original.cpy();
        enhanced.r = MathUtils.clamp(enhanced.r * factor, 0, 1);
        enhanced.g = MathUtils.clamp(enhanced.g * factor, 0, 1);
        enhanced.b = MathUtils.clamp(enhanced.b * factor, 0, 1);
        return enhanced;
    }
}