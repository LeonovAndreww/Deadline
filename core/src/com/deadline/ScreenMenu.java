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

public class ScreenMenu implements Screen {
    DdlnGame game;

    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;
    BitmapFont font, fontUi;

    Texture imgBg;


    public ScreenMenu(DdlnGame game){
        this.game = game;
        batch = game.batch;
        camera = game.camera;
        touch = game.touch;
        font = game.font;
        fontUi = game.fontUi;

        glyphLayout = new GlyphLayout();

        imgBg = new Texture("backgroundMenu.jpg");
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
            if (touch.x<SCR_WIDTH/2) game.setScreen(game.screenGame);
        }

        // события

        //  отрисовка
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(imgBg, 0, 0);
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
    }
}
