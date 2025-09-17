package com.deadline.buttons;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class RectangleButton extends Button {
    private float width, height;

    public RectangleButton(float x, float y, float width, float height, TextureRegion[] buttonAtlas, boolean isSticky, Runnable onDown, Runnable onUp) {
        super(x, y, buttonAtlas, isSticky, onDown, onUp);
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean hit(float touchX, float touchY) {
        float x = super.getX();
        float y = super.getY();
        return (touchX >= x && touchX <= x + width && touchY >= y && touchY <= y + height);
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion texture = getButtonAtlas()[0];
        if (isPressed() || isDown()) {
            texture = getButtonAtlas()[1];
        }

        batch.draw(texture, getX(), getY(),width, height);
    }

    public float getWidth() { return width; }
    public float getHeight() { return height; }
}
