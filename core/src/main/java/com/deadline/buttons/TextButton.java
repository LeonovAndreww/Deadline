package com.deadline.buttons;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextButton extends RectangleButton{
    BitmapFont font;
    String text;
    private final float padding = 1.5f;

    public TextButton(float x, float y, float width, float height, String text, BitmapFont font, TextureRegion[] buttonAtlas, boolean isSticky, Runnable onDown, Runnable onUp) {
        super(x, y, width, height, buttonAtlas, isSticky, onDown, onUp);
        this.text = text;
        this.font = font;
    }

    @Override
    public void draw(SpriteBatch batch) {
        TextureRegion texture = super.getButtonAtlas()[0];
        if (isPressed() || isDown()) texture = getButtonAtlas()[1];
        batch.draw(texture, getX(), getY(), getWidth(), getHeight());

        float targetWidth = getWidth() - 2 * padding;
        float targetHeight = getHeight() - 2 * padding;

        GlyphLayout glyphLayout = new GlyphLayout(font, text);
        float textWidth = glyphLayout.width;
        float textHeight = glyphLayout.height;
        float scaleX = targetWidth / textWidth;
        float scaleY = targetHeight / textHeight;
        float scale = Math.min(scaleX, scaleY);

        font.getData().setScale(scale);
        glyphLayout.setText(font, text);

        float textX = getX() + (getWidth() - glyphLayout.width) / 2f;
        float textY = getY() + (getHeight() + glyphLayout.height) / 1.75f;

        font.draw(batch, glyphLayout, textX, textY);

        font.getData().setScale(1f);
    }
}
