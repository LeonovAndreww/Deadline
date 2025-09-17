package com.deadline.buttons;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class CircleButton extends Button {
    private final float radius;

    public CircleButton(float x, float y, float radius, TextureRegion[] buttonAtlas, boolean isSticky, Runnable onDown, Runnable onUp) {
        super(x, y, buttonAtlas, isSticky, onDown, onUp);
        this.radius = radius;
    }

    @Override
    public boolean hit(float touchX, float touchY) {
        float dx = touchX - super.getX();
        float dy = touchY - super.getY();
        return (dx * dx + dy * dy <= radius * radius);
    }

//    @Override
//    public void draw(SpriteBatch batch) {
//        TextureRegion texture = buttonAtlas;
//    }
}
