package com.deadline.buttons;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.TimeUtils;

public abstract class Button {
    private float x, y;
    private final boolean isSticky;
    private final TextureRegion[] buttonAtlas;
    private boolean isPressed = false;
    private boolean isDown = false;
    private boolean isClickable = true;
    private long timePressed = 0;
    private int pressedPointer = -1;
    private final Runnable onUp;
    private final Runnable onDown;

    public Button(float x, float y, TextureRegion[] buttonAtlas, boolean isSticky, Runnable onDown, Runnable onUp) {
        this.x = x;
        this.y = y;
        this.buttonAtlas = buttonAtlas;
        this.isSticky = isSticky;
        this.onDown = onDown;
        this.onUp = onUp;
    }

    public abstract boolean hit(float touchX, float touchY);

    public void touchDown(int pointer, float worldX, float worldY) {
        if (!isClickable) return;
        if (!hit(worldX, worldY)) return;
        if (pressedPointer != -1 && pressedPointer != pointer) return;

        isDown = true;
        pressedPointer = pointer;
        timePressed = TimeUtils.millis();

        if (onDown != null) onDown.run();
    }

    public void touchUp(int pointer, float worldX, float worldY) {
        if (!isClickable) return;
        if (pressedPointer != pointer) return;

        boolean releasedOver = hit(worldX, worldY);

        if (isSticky) { if (releasedOver) isPressed = !isPressed; }
        else isPressed = false;
        isDown = false;
        pressedPointer = -1;

        if (releasedOver && onUp != null) onUp.run();
    }

    public void touchCancel(int pointer) {
        if (pressedPointer == pointer) {
            isDown = false;
            pressedPointer = -1;
        }
    }

    public void update(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void draw(com.badlogic.gdx.graphics.g2d.SpriteBatch batch) {
        TextureRegion texture = buttonAtlas[0];
        if (isPressed || isDown) {
            texture = buttonAtlas[1];
        }

        batch.draw(texture, x, y, texture.getRegionWidth(), texture.getRegionHeight());
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isPressed() { return isPressed; }
    public boolean isDown() { return isDown; }

    public boolean isClickable() { return isClickable; }
    public void setClickable(boolean clickable) { isClickable = clickable; }

    public long getTimePressed() { return timePressed; }
    public void setTimePressed(long timePressed) { this.timePressed = timePressed; }

    public int getPressedPointer() {
        return pressedPointer;
    }

    public void setPressedPointer(int pointer) {
        pressedPointer = pointer;
    }

    public TextureRegion[] getButtonAtlas() {
        return buttonAtlas;
    }
}
