package com.deadline;

//import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.deadline.buttons.Button;

import java.util.List;

public class UiInput implements InputProcessor {
    private final OrthographicCamera camera;
    private final List<Button> buttons;
    private final Vector3 temp = new Vector3();

    public UiInput(OrthographicCamera camera, List<Button> buttons) {
        this.camera = camera;
        this.buttons = buttons;
    }

    private Vector3 toWorld(int screenX, int screenY) {
        temp.set(screenX, screenY, 0);
        camera.unproject(temp);
        return temp;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int buttonCode) {
        Vector3 touch = toWorld(screenX, screenY);
        for (Button button : buttons) {
            if (!button.isClickable()) continue;
            if (button.hit(touch.x, touch.y)) {
                button.touchDown(pointer, touch.x, touch.y);
                //Gdx.app.log("UI", "touchDown world=" + screenX + "," + screenY+ " pointer=" + pointer);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int buttonCode) {
        Vector3 world = toWorld(screenX, screenY);
        boolean consumed = false;
        for (Button button : buttons) {
            if (!button.isClickable()) continue;
            if (button.getPressedPointer() == pointer) {
                button.touchUp(pointer, world.x, world.y);
                consumed = true;
                //Gdx.app.log("UI", "touchUp world=" + screenX + "," + screenY+ " pointer=" + pointer);
            }
        }
        return consumed;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
