package com.deadline;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class OnScreenJoystick {
    private final Vector2 position; // позже сделать двигаемый джойстик
    private final float baseRadius;
    private final float knobRadius;
    private final Vector2 knobPos;

    public OnScreenJoystick(float baseRadius, float knobRadius) {
        position = new Vector2(0, 0);
        this.baseRadius = baseRadius;
        this.knobRadius = knobRadius;
        knobPos = new Vector2(position.x, position.y);
    }

    public void render(SpriteBatch batch, Texture base, Texture knob, float x, float y) {
        position.x = x;
        position.y = y;
        batch.draw(base, position.x - baseRadius, position.y - baseRadius, baseRadius * 2, baseRadius * 2);
        batch.draw(knob, knobPos.x - knobRadius, knobPos.y - knobRadius, knobRadius * 2, knobRadius * 2);
    }

    public void updateKnob(Vector3 touch) {
            knobPos.x += touch.x - knobPos.x;
            knobPos.y += touch.y - knobPos.y;

            if (knobPos.dst(position) > baseRadius - knobRadius) {
                knobPos.sub(position).nor().scl(baseRadius - knobRadius).add(position);
            }
    }

    public void resetKnob() {
        knobPos.set(position.x, position.y);
    }

    // получение вектора движения в виде нормализованного вектора
    public Vector2 getDirectionVector() {
        float deltaX = knobPos.x - position.x;
        float deltaY = knobPos.y - position.y;
        return new Vector2(deltaX / baseRadius, deltaY / baseRadius).nor();
    }
}
