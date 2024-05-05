package com.deadline;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class MyButton {

    float x, y;
    float width, height;
    BitmapFont font;
    String text;
    private boolean isTextButton = false;
    private boolean isCircleButton = false;

    public MyButton(float x, float y, float width, float height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public MyButton(String text, BitmapFont font, float x, float y){
        this.x = x;
        this.y = y;
        this.text = text;
        this.font = font;
        GlyphLayout glyphLayout = new GlyphLayout(font, text);
        width = glyphLayout.width;
        height = glyphLayout.height;
        this.x -= width/2;
        isTextButton = true;
    }

    public MyButton(float x, float y, float radius){
        this.x = x;
        this.y = y;
        this.width = this.height = radius * 2;
        isCircleButton = true;
    }

    boolean hit(float touchX, float touchY){
        if (isTextButton) {
            return (x < touchX && touchX < x + width && y > touchY && touchY > y - height);
        }
        else if (isCircleButton) {
            float centerX = x + width / 2;
            float centerY = y - height / 2;
            return Math.sqrt(Math.pow(centerX - touchX, 2) + Math.pow(centerY - touchY, 2)) <= width/2;
        }
        else {
            return (x < touchX && touchX < x + width) && (y < touchY && touchY < y + height);
        }
    }
}
