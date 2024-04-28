package com.deadline;

import static com.deadline.DdlnGame.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

public class ScreenGame implements Screen {
    DdlnGame game;

    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;

    World world = new World(new Vector2(0, 0), true);
    Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    BitmapFont font, fontUi;

    OnScreenJoystick joystick;

    Texture imgBg;
    Texture imgPlayer;
    Texture imgJstBase, imgJstKnob;

    Player player;

    String txtCord = "Empty";

    public ScreenGame(DdlnGame game) {
        this.game = game;
        batch = game.batch;
        camera = game.camera;
        touch = game.touch;
        font = game.font;
        fontUi = game.fontUi;

        glyphLayout = new GlyphLayout();

        imgBg = new Texture("map.png");
        imgPlayer = new Texture("bob.png");
        TextureRegion bobRegion = new TextureRegion(imgPlayer, 0, 0, 16, 32);
        imgJstBase = new Texture("joystickBase.png");
        imgJstKnob = new Texture("joystickKnob.png");

        player = new Player(world, bobRegion, 30, SCR_WIDTH/2, SCR_HEIGHT/2);

        joystick = new OnScreenJoystick(SCR_HEIGHT/6, SCR_HEIGHT/12);
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
            if (touch.x < player.getX())
            {
                joystick.updateKnob(touch);
                player.getBody().setLinearVelocity(
                        joystick.getDirectionVector().x * player.getSpeed(),
                        joystick.getDirectionVector().y * player.getSpeed()
                );
            }
        }
        else
        {
            joystick.resetKnob();
            player.getBody().setLinearVelocity(0, 0);
        }

        // события

        // отрисовка
        ScreenUtils.clear(0.2f, 0, 0.3f, 1);
        debugRenderer.render(world, camera.combined);
        batch.setProjectionMatrix(camera.combined);
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();
        batch.begin();

        batch.draw(imgPlayer, player.getX(), player.getY());
        batch.draw(imgBg, 0, 0);
        batch.draw(imgPlayer, player.getX(), player.getY());
        font.draw(batch, txtCord, 0, SCR_HEIGHT);

        joystick.render(batch, imgJstBase, imgJstKnob, player.getX()-SCR_WIDTH/2.75f, player.getY()-SCR_HEIGHT/4);

        batch.end();

        world.step(1/60f, 6, 2);
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
