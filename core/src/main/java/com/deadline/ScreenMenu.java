package com.deadline;

import static com.deadline.DdlnGame.SCR_HEIGHT;
import static com.deadline.DdlnGame.SCR_WIDTH;
import static com.deadline.DdlnGame.glyphLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.deadline.buttons.Button;
import com.deadline.buttons.RectangleButton;

import java.util.ArrayList;
import java.util.List;

public class ScreenMenu implements Screen {
    private final DdlnGame game;

    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;
    BitmapFont font, fontUi;

    List<Button> buttons = new ArrayList<>();
    UiInput uiInput;

    Texture imgBg;
    Texture imgBtnShutdownAtlas;
    Texture imgNote;
    TextureRegion[] imgBtnShutdown = new TextureRegion[2];

    Sound sndClick;
    Sound sndShutdown;
    Sound sndStartup;

    RectangleButton btnShutdown;

    String dialogue = "<-- It's time to turn off the computer and go home!";

    public ScreenMenu(DdlnGame game){
        this.game = game;
        batch = game.batch;
        camera = game.camera;
        touch = game.touch;
        font = game.font;
        fontUi = game.fontUi;

        glyphLayout = new GlyphLayout();

        imgBg = new Texture("textures/menu.png");
        imgBtnShutdownAtlas = new Texture("textures/buttonShutdownAtlas.png");
        imgNote = new Texture("textures/note.png");

        for (int i = 0; i < imgBtnShutdown.length; i++) {
            imgBtnShutdown[i] = new TextureRegion(imgBtnShutdownAtlas, 0, i*16, 16, 16);
        }

        sndClick = Gdx.audio.newSound(Gdx.files.internal("sounds/click.ogg"));
        sndShutdown = Gdx.audio.newSound(Gdx.files.internal("sounds/shutdown.ogg"));
        sndStartup = Gdx.audio.newSound(Gdx.files.internal("sounds/startup.ogg"));

        btnShutdown = new RectangleButton(5, 10, 16, 16, imgBtnShutdown, false, () -> sndClick.play(), () -> {
            sndShutdown.play();
            btnShutdown.setClickable(false);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    game.setScreen(game.screenGame);
                }
            }, 1f);
        });
        buttons.add(btnShutdown);

        uiInput = new UiInput(camera, buttons);
    }

    @Override
    public void show() {
        resetMenu();
        Gdx.input.setInputProcessor(uiInput);
        sndStartup.play();
    }


    @Override
    public void render(float delta) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(imgBg, -45, 0);
        for (Button button: buttons) button.draw(batch);
        batch.draw(imgNote, 150, 45);
        font.draw(batch, dialogue, 22, 22);

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
        imgBtnShutdownAtlas.dispose();
        imgNote.dispose();
        sndClick.dispose();
        sndShutdown.dispose();
        sndStartup.dispose();
    }

    public void resetMenu() {
        for (Button button: buttons) button.setTimePressed(0);
        btnShutdown.setClickable(true);
        camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);
    }
}
