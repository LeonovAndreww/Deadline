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

import java.util.ArrayList;

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
    Texture imgWall;
    Texture imgJstBase, imgJstKnob;
    Texture imgPlayerAtlas;
    TextureRegion[][] imgPlayerIdle = new TextureRegion[4][6];
    TextureRegion[][] imgPlayerRun = new TextureRegion[4][6];

    ArrayList<Room> rooms = new ArrayList<>();
    Player player;

    ArrayList<Character> doors = new ArrayList<>(); // перенести в отдельный класс или функцию
    String txtCord = "Empty";

    public ScreenGame(DdlnGame game) {
        this.game = game;
        batch = game.batch;
        camera = game.camera;
        touch = game.touch;
        font = game.font;
        fontUi = game.fontUi;

        glyphLayout = new GlyphLayout();

        imgBg = new Texture("grass.png");
        imgWall = new Texture("wall.png");
        imgJstBase = new Texture("joystickBase.png");
        imgJstKnob = new Texture("joystickKnob.png");
        imgPlayerAtlas = new Texture("playerAtlas.png");

        int iter = 0;
        for (int i = 0; i < imgPlayerIdle.length; i++) {
            for (int j = 0; j < imgPlayerIdle[0].length; j++) {
                imgPlayerIdle[i][j] = new TextureRegion(imgPlayerAtlas, iter*16, 0, 16, 32);
                imgPlayerRun[i][j] = new TextureRegion(imgPlayerAtlas, iter*16, 32, 16, 32);
                iter++;
            }
        }
        player = new Player(world, 16, 12, SCR_WIDTH/2, SCR_HEIGHT/2, 6, 450);

        joystick = new OnScreenJoystick(SCR_HEIGHT/6, SCR_HEIGHT/12);

        doors.add('l');
        Room room0 = new Room(world, -100, 0, 100, 100,doors, rooms);
        rooms.add(room0);
        Room room1 = new Room(world, 0, 0, 100, 100,doors, rooms);
        rooms.add(room1);

        txtCord = "x: "+player.getX()+"\ny: "+player.getY();
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
            if (touch.x < player.getX())
            {
                joystick.updateKnob(touch);
                player.getBody().setLinearVelocity(
                        joystick.getDirectionVector().x * player.getSpeed(),
                        joystick.getDirectionVector().y * player.getSpeed()
                );
                if (Math.abs(joystick.getDirectionVector().x) > Math.abs(joystick.getDirectionVector().y)) {
                    if (joystick.getDirectionVector().x>0) player.setDirection('r');
                    else player.setDirection('l');
                }
                else {
                    if (joystick.getDirectionVector().y>0) player.setDirection('u');
                    else player.setDirection('d');
                }
            }
        }
        else
        {
            joystick.resetKnob();
            player.getBody().setLinearVelocity(0, 0);
        }

        // события

        player.changePhase();

        // отрисовка
        ScreenUtils.clear(0.2f, 0, 0.3f, 1);
        debugRenderer.render(world, camera.combined);
        batch.setProjectionMatrix(camera.combined);
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();
        batch.begin();

//        batch.draw(imgBg, 100, 100);

//        txtCord = String.valueOf(player.getBody().isAwake());
        font.draw(batch, txtCord, player.getX()-SCR_WIDTH/3, player.getY()+SCR_HEIGHT/3);

        playerBatch();

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
        imgPlayerAtlas.dispose();
        imgJstBase.dispose();
        imgJstKnob.dispose();
        batch.dispose();
    }

    public void playerBatch() { // separated it cuz too big
        int phase = player.getPhase();
        float x = player.getX()-imgPlayerIdle[0][0].getRegionWidth()/2f; // centralized image x
        float y = player.getY()-imgPlayerIdle[0][0].getRegionHeight()/5f; // centralized image y
        boolean isMoving = player.getBody().isAwake();

        switch(player.getDirection()) {
            case 'r': {
                if (isMoving) batch.draw(imgPlayerRun[0][phase], x, y);
                else batch.draw(imgPlayerIdle[0][phase], x, y); } break;
            case 'u': {
                if (isMoving) batch.draw(imgPlayerRun[1][phase], x, y);
                else batch.draw(imgPlayerIdle[1][phase], x, y); } break;
            case 'l': {
                if (isMoving) batch.draw(imgPlayerRun[2][phase], x, y);
                else batch.draw(imgPlayerIdle[2][phase], x, y); } break;
            default: {
                if (isMoving) batch.draw(imgPlayerRun[3][phase], x, y);
                else batch.draw(imgPlayerIdle[3][phase], x, y); }
        }
    }
}
