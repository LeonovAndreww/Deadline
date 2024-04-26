package com.deadline;

import static com.deadline.DdlnGame.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class ScreenGame implements Screen {
    DdlnGame game;

    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;
    BitmapFont font, fontUi;

    OnScreenJoystick joystick;

    Texture imgBg;
    Texture imgPlayer;
    Texture imgJstBase, imgJstKnob;

    float playerSpeed = 5.0f; // ПЕРЕНЕСТИ ВСЁ В ОТДЕЛЬНЫЙ КЛАСС ИГРОКА! НЕ ЗАБЫТЬ
    float px = 0, py = 0;

    String txtCord = "Empty";

    public ScreenGame(DdlnGame game) {
        this.game = game;
        batch = game.batch;
        camera = game.camera;
        touch = game.touch;
        font = game.font;
        fontUi = game.fontUi;

        glyphLayout = new GlyphLayout();

        imgPlayer = new Texture("badlogic.jpg");
        imgJstBase = new Texture("joystickBase.png");
        imgJstKnob = new Texture("joystickKnob.png");

        joystick = new OnScreenJoystick(200, 200, 150, 75);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // касания
        if (Gdx.input.isTouched()) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            txtCord = "X "+touch.x + "\nY "+touch.y;
            joystick.updateKnob(touch);
        }
        else joystick.resetKnob();

        // события

        px += joystick.getDirectionVector().x * playerSpeed;
        py += joystick.getDirectionVector().y * playerSpeed;

        // отрисовка
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        font.draw(batch, txtCord, 0, SCR_HEIGHT);

        batch.draw(imgPlayer, px, py);

        joystick.render(batch, imgJstBase, imgJstKnob);

        batch.end();


    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        imgBg.dispose();
        imgPlayer.dispose();
        imgJstBase.dispose();
        imgJstKnob.dispose();
    }
}
