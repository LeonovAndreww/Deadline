package com.deadline;

import static com.deadline.DdlnGame.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class ScreenMenu implements Screen {
    DdlnGame game;

    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;
    BitmapFont font, fontUi;

    Texture imgBg;
    Texture imgBtnShutdown;

    Sound sndClick;
    Sound sndShutdown;

    MyButton btnShutdown;

    public ScreenMenu(DdlnGame game){
        this.game = game;
        batch = game.batch;
        camera = game.camera;
        touch = game.touch;
        font = game.font;
        fontUi = game.fontUi;

        glyphLayout = new GlyphLayout();

        imgBg = new Texture("menu.png");
        imgBtnShutdown = new Texture("shutdown.png");

        sndClick = Gdx.audio.newSound(Gdx.files.internal("click.mp3"));
        sndShutdown = Gdx.audio.newSound(Gdx.files.internal("shutdown.mp3"));

        btnShutdown = new MyButton(5, 10, 8);
    }

    @Override
    public void show() {

    }


    @Override
    public void render(float delta) {
        // касания
        if (Gdx.input.justTouched()) {
            touch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touch);
            if (btnShutdown.hit(touch.x, touch.y) && !btnShutdown.isPressed) {
                btnShutdown.isPressed = true;
                sndClick.play();
                sndShutdown.play();
                if (btnShutdown.timePressed == 0) {
                    btnShutdown.timePressed = TimeUtils.millis();
                }
            }
        }

        // события

        if (btnShutdown.isPressed) {
            if (TimeUtils.millis() > btnShutdown.timePressed+2150) {
                btnShutdown.isPressed = false;
                btnShutdown.timePressed = 0;
                game.setScreen(game.screenGame);
            }
        }

        //  отрисовка
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(imgBg, -45, 0);
        batch.draw(imgBtnShutdown, btnShutdown.x, btnShutdown.y, btnShutdown.width, btnShutdown.height);

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
        imgBtnShutdown.dispose();
    }
}
