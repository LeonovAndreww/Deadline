package com.deadline;

import static com.deadline.DdlnGame.SCR_HEIGHT;
import static com.deadline.DdlnGame.SCR_WIDTH;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

public class ScreenEnding implements Screen {
    private final DdlnGame game;
    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;
    Texture imgStreet;
    long timeStart;

    BitmapFont font, fontUi;

    Texture imgPlayerAtlas;
    Texture imgBlankAtlas;

    TextureRegion[][] imgPlayerIdle = new TextureRegion[4][6];
    TextureRegion[] imgBlank = new TextureRegion[3];

    long timeLastPhase=0;
    int phase = 0;

    public ScreenEnding(DdlnGame game) {
        this.game = game;
        batch = game.batch;
        camera = game.camera;
        touch = game.touch;
        font = game.font;
        fontUi = game.fontUi;

        imgStreet = new Texture("textures/street.jpeg");
        imgPlayerAtlas = new Texture("textures/playerAtlas.png");
        imgBlankAtlas = new Texture("textures/blankAtlas.png");

        int iter = 0;
        for (int i = 0; i < imgPlayerIdle.length; i++) {
            for (int j = 0; j < imgPlayerIdle[0].length; j++) {
                imgPlayerIdle[i][j] = new TextureRegion(imgPlayerAtlas, iter * 16, 0, 16, 32);
                iter++;
            }
        }

        for (int i = 0; i < imgBlank.length; i++) {
            imgBlank[i] = new TextureRegion(imgBlankAtlas, i, 0, 1, 1);
        }
    }

    @Override
    public void show() {
        timeStart = TimeUtils.millis();
        System.out.println("screen ending showed");
    }

    @Override
    public void render(float delta) {
        if (timeLastPhase+400<TimeUtils.millis()) {
            if (++phase == imgPlayerIdle[0].length) phase = 0;
            timeLastPhase = TimeUtils.millis();
        }

        batch.begin();
        camera.position.set(SCR_WIDTH / 2, SCR_HEIGHT / 2, 0);
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.draw(imgStreet, 0, 0, SCR_WIDTH, SCR_HEIGHT);
        batch.draw(imgPlayerIdle[2][phase], SCR_WIDTH/1.3f, SCR_HEIGHT/4.75f);

        if (TimeUtils.millis()-timeStart>17500) {
            batch.draw(imgBlank[2], 0, 0, SCR_WIDTH, SCR_HEIGHT);
            fontUi.draw(batch, "Вы успешно вернулись домой с работы!" +
                "\nСпасибо Вам за то, что дошли до конца)" +
                "\n" +
                "\nОтдельная благодарность:" +
                "\n-Моему младшему брату" +
                "\n   за тестирование игры и мотивацию" +
                "\n" +
                "\nМарку (@mrkkleem)" +
                "\n   за музыкальное сопровождение" +
                "\n" +
                "\nЖдите обновлений!!", SCR_WIDTH*0.1f, SCR_HEIGHT*0.9f);
        }
        else if (TimeUtils.millis()-timeStart>15000) {
            if (TimeUtils.millis() - timeStart > 17500) {
                batch.draw(imgBlank[2], 0, 0, SCR_WIDTH, SCR_HEIGHT);
            }
            else if (TimeUtils.millis() - timeStart > 16250) {
                batch.draw(imgBlank[1], 0, 0, SCR_WIDTH, SCR_HEIGHT);
            }
            else if (TimeUtils.millis() - timeStart > 15000) {
                batch.draw(imgBlank[0], 0, 0, SCR_WIDTH, SCR_HEIGHT);
            }
        }
        else if (TimeUtils.millis()-timeStart<3750) {
            if (TimeUtils.millis() - timeStart > 2500) {
                batch.draw(imgBlank[0], 0, 0, SCR_WIDTH, SCR_HEIGHT);
            } else if (TimeUtils.millis() - timeStart > 1250) {
                batch.draw(imgBlank[1], 0, 0, SCR_WIDTH, SCR_HEIGHT);
            } else if (TimeUtils.millis() - timeStart > 0) {
                batch.draw(imgBlank[2], 0, 0, SCR_WIDTH, SCR_HEIGHT);
            }
        }

        if (TimeUtils.millis()-timeStart>30500) {
            game.setScreen(game.screenMenu);
        }

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
        imgStreet.dispose();
        imgPlayerAtlas.dispose();
        imgBlankAtlas.dispose();
    }
}
