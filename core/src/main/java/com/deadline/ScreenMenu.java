package com.deadline;

import static com.deadline.DdlnGame.SCR_HEIGHT;
import static com.deadline.DdlnGame.SCR_WIDTH;
import static com.deadline.DdlnGame.glyphLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.deadline.buttons.Button;
import com.deadline.buttons.RectangleTextButton;

import java.util.ArrayList;
import java.util.List;

public class ScreenMenu implements Screen {
    private final DdlnGame game;

    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;
    BitmapFont fontIcon;

    List<Button> buttons = new ArrayList<>();
    UiInput uiInput;

    Texture imgBg;
    Texture imgBtnShutdownAtlas, imgBtnInternetAtlas, imgBtnComputerAtlas;
    Texture imgNote;
    TextureRegion[] imgBtnShutdown = new TextureRegion[2];
    TextureRegion[] imgBtnInternet = new TextureRegion[2];
    TextureRegion[] imgBtnComputer = new TextureRegion[2];

    Sound sndClick, sndError;
    Sound sndShutdown;
    Sound sndStartup;
    Music musNoise;

    RectangleTextButton btnShutdown, btnInternet, btnComputer;

    public ScreenMenu(DdlnGame game){
        this.game = game;
        batch = game.batch;
        camera = game.camera;
        touch = game.touch;
        fontIcon = game.fontIcon;

        glyphLayout = new GlyphLayout();

        imgBg = new Texture("textures/menu.png");
        imgNote = new Texture("textures/note.png");
        imgBtnShutdownAtlas = new Texture("textures/buttonShutdownAtlas.png");
        imgBtnInternetAtlas = new Texture("textures/buttonInternetAtlas.png");
        imgBtnComputerAtlas = new Texture("textures/buttonComputerAtlas.png");

        for (int i = 0; i < imgBtnShutdown.length; i++) {
            imgBtnShutdown[i] = new TextureRegion(imgBtnShutdownAtlas, 0, i*16, 16, 16);
        }
        for (int i = 0; i < imgBtnInternet.length; i++) {
            imgBtnInternet[i] = new TextureRegion(imgBtnInternetAtlas, 0, i*16, 16, 16);
        }
        for (int i = 0; i < imgBtnComputer.length; i++) {
            imgBtnComputer[i] = new TextureRegion(imgBtnComputerAtlas, 0, i*16, 16, 16);
        }

        sndClick = Gdx.audio.newSound(Gdx.files.internal("sounds/click.ogg"));
        sndShutdown = Gdx.audio.newSound(Gdx.files.internal("sounds/shutdown.ogg"));
        sndStartup = Gdx.audio.newSound(Gdx.files.internal("sounds/startup.ogg"));
        sndError = Gdx.audio.newSound(Gdx.files.internal("sounds/error.ogg"));
        musNoise = Gdx.audio.newMusic(Gdx.files.internal("sounds/noise.ogg"));

        musNoise.setLooping(true);

        btnShutdown = new RectangleTextButton(10, 10, "Shutdown", fontIcon, 16, 16, imgBtnShutdown, false, () -> sndClick.play(), () -> {
            sndShutdown.play();
            btnShutdown.setClickable(false);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    musNoise.stop();
                    game.setScreen(game.screenGame);
                }
            }, 1f);
        });
        buttons.add(btnShutdown);

        btnInternet = new RectangleTextButton(10, 95, "Internet Discover", fontIcon, 16, 16, imgBtnInternet, false, () -> sndClick.play(), () -> {
            sndError.play();
        });
        buttons.add(btnInternet);

        btnComputer = new RectangleTextButton(10, 116, "My computer", fontIcon, 16, 16, imgBtnComputer, false, () -> sndClick.play(), () -> {
            sndError.play();
        });
        buttons.add(btnComputer);

        uiInput = new UiInput(camera, buttons);
    }

    @Override
    public void show() {
        resetMenu();
        Gdx.input.setInputProcessor(uiInput);
        sndStartup.play();
        musNoise.play();
    }


    @Override
    public void render(float delta) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(imgBg, -45, 0);
        for (Button button: buttons) button.draw(batch);
        batch.draw(imgNote, 75, 45);

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
        imgBtnInternetAtlas.dispose();
        imgBtnComputerAtlas.dispose();
        imgNote.dispose();
        sndClick.dispose();
        sndError.dispose();
        sndShutdown.dispose();
        sndStartup.dispose();
        musNoise.dispose();
    }

    public void resetMenu() {
        for (Button button: buttons) button.setTimePressed(0);
        btnShutdown.setClickable(true);
        camera.setToOrtho(false, SCR_WIDTH, SCR_HEIGHT);
    }
}
