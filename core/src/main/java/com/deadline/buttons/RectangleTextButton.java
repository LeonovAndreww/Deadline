package com.deadline.buttons;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class RectangleTextButton extends RectangleButton {
    BitmapFont font;
    String text;

    public RectangleTextButton(float x, float y, String text, BitmapFont font, float width, float height, TextureRegion[] buttonAtlas, boolean isSticky, Runnable onDown, Runnable onUp) {
        super(x, y, width, height, buttonAtlas, isSticky, onDown, onUp);
        this.text = text;
        this.font = font;
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion texture = getButtonAtlas()[0];
        if (isPressed() || isDown()) {
            texture = getButtonAtlas()[1];
        }
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());

        float fontScale = 0.15f;
        font.getData().setScale(fontScale);

        GlyphLayout layout = new GlyphLayout(font, text);

        float textX = getX() + (getWidth() - layout.width) / 2f;
        float textY = getY() + (getHeight() + layout.height) / 2f - getHeight() / 1.9f;

        font.draw(batch, layout, textX, textY);
    }

}
