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
import java.util.Collections;
import java.util.Random;

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

    Random random = new Random();

    String txtCord = "Empty";
    int roomsCreated = 0;

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
                imgPlayerIdle[i][j] = new TextureRegion(imgPlayerAtlas, iter * 16, 0, 16, 32);
                imgPlayerRun[i][j] = new TextureRegion(imgPlayerAtlas, iter * 16, 32, 16, 32);
                iter++;
            }
        }
        player = new Player(world, 16, 12, 50, 50, 6, 450);

        joystick = new OnScreenJoystick(SCR_HEIGHT / 6, SCR_HEIGHT / 12);

//        ArrayList<Character> temp = new ArrayList<>();
//        temp.add('r');
//        Room room0 = new Room(world, -150, 0, 150, 150,temp, rooms);
//        rooms.add(room0);
//        temp.clear();
//        Room room1 = new Room(world, 0, 0, 150, 150,temp, rooms);
//        rooms.add(room1);
//        temp.add('l');
//        Room room2 = new Room(world, 150, 0, 150, 150,temp, rooms);
//        rooms.add(room2);
        generateMap(7);
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
//            txtCord = "x0 "+rooms.get(0).getX()+"\ny0 "+rooms.get(0).getY()+"\nx1 "+rooms.get(1).getX()+"\ny1 "+rooms.get(1).getY();
//            txtCord = "0 "+rooms.get(0).getDoors()+"\n1 "+rooms.get(1).getDoors();
            if (touch.x < player.getX()) {
                joystick.updateKnob(touch);
                player.getBody().setLinearVelocity(
                        joystick.getDirectionVector().x * player.getSpeed(),
                        joystick.getDirectionVector().y * player.getSpeed()
                );
                if (Math.abs(joystick.getDirectionVector().x) > Math.abs(joystick.getDirectionVector().y)) {
                    if (joystick.getDirectionVector().x > 0) player.setDirection('r');
                    else player.setDirection('l');
                } else {
                    if (joystick.getDirectionVector().y > 0) player.setDirection('u');
                    else player.setDirection('d');
                }
            }
        } else {
            joystick.resetKnob();
            player.getBody().setLinearVelocity(0, 0);
        }
//        if (Gdx.input.justTouched()) {
//            if (touch.x > player.getX())player.changeBattleState(!player.getBattleState());
//        }

        // события

        player.changePhase();

        // отрисовка
        ScreenUtils.clear(0, 0, 0, 0);
        debugRenderer.render(world, camera.combined);
        batch.setProjectionMatrix(camera.combined);
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();
        batch.begin();

//        batch.draw(imgBg, 100, 100);

        txtCord="";
        for (int i = 0; i < rooms.size(); i++) {
            txtCord+="\nX " + rooms.get(i).getX()+"\nY "+ rooms.get(i).getY()+"\n"+rooms.get(i).getDoors()+"\n"+(hasDir('r', rooms.get(i).getX()+150 , rooms.get(i).getY()) != -1);
        }
        font.draw(batch, txtCord, player.getX() - SCR_WIDTH / 2, player.getY() + SCR_HEIGHT / 2);

        playerBatch();

        joystick.render(batch, imgJstBase, imgJstKnob, player.getX() - SCR_WIDTH / 2.75f, player.getY() - SCR_HEIGHT / 4);

        batch.end();

        world.step(1 / 60f, 6, 2);
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
        float x = player.getX() - imgPlayerIdle[0][0].getRegionWidth() / 2f; // centralized image x
        float y = player.getY() - imgPlayerIdle[0][0].getRegionHeight() / 5f; // centralized image y
        boolean isMoving = player.getBody().isAwake();

        switch (player.getDirection()) {
            case 'r': {
                if (isMoving) batch.draw(imgPlayerRun[0][phase], x, y);
                else batch.draw(imgPlayerIdle[0][phase], x, y);
            }
            break;
            case 'u': {
                if (isMoving) batch.draw(imgPlayerRun[1][phase], x, y);
                else batch.draw(imgPlayerIdle[1][phase], x, y);
            }
            break;
            case 'l': {
                if (isMoving) batch.draw(imgPlayerRun[2][phase], x, y);
                else batch.draw(imgPlayerIdle[2][phase], x, y);
            }
            break;
            default: {
                if (isMoving) batch.draw(imgPlayerRun[3][phase], x, y);
                else batch.draw(imgPlayerIdle[3][phase], x, y);
            }
        }
    }

    void generateMap(int maxRooms) {
        float roomX = 0, roomY = 0;
        float roomWidth = 150, roomHeight = 150;
        ArrayList<Character> doors = new ArrayList<>();
        ArrayList<Character> temp = new ArrayList<>();

        for (int i = 0; i < maxRooms; i++) {
            doors.clear();
            doors = generateDir(roomX, roomY, roomWidth, roomHeight);

            for (int j = 0; j < doors.size(); j++) {
                if (doors.get(j) == 'u' && hasFreeCell(roomX, roomY + roomHeight)) {
                    roomY += roomHeight;
                    break;
                }
                if (doors.get(j) == 'd' && hasFreeCell(roomX, roomY - roomHeight)) {
                    roomY -= roomHeight;
                    break;
                }
                if (doors.get(j) == 'l' && hasFreeCell(roomX - roomWidth, roomY)) {
                    roomX -= roomWidth;
                    break;
                }
                if (doors.get(j) == 'r' && hasFreeCell(roomX + roomWidth, roomY)) {
                    roomX += roomWidth;
                    break;
                }
            }

            roomsCreated++; // to delete
            ArrayList<Room> tempRooms = new ArrayList<>(rooms);
            Room room = new Room(world, roomX, roomY, roomWidth, roomHeight, doors, tempRooms);
            setDoors();
            rooms.add(room);
            doors.clear();
        }

        ArrayList<Character> lastDir = new ArrayList<>();
        lastDir = rooms.get(rooms.size()-1).getDoors();

        ArrayList<Character> preLastDir = new ArrayList<>();
        preLastDir = rooms.get(rooms.size()-2).getDoors();

        if (lastDir.contains('u'))
        {
            rooms.get(rooms.size()-1).removeTopWall();
            rooms.get(rooms.size()-2).removeBottomWall();
        }
        else if (lastDir.contains('d')) {
            rooms.get(rooms.size()-1).removeBottomWall();
            rooms.get(rooms.size()-2).removeTopWall();
        }
        else if (lastDir.contains('l')) {
            rooms.get(rooms.size()-1).removeLeftWall();
            rooms.get(rooms.size()-2).removeRightWall();
        }
        else if (lastDir.contains('r')) {
            rooms.get(rooms.size()-1).removeRightWall();
            rooms.get(rooms.size()-2).removeLeftWall();
        }
    }


    ArrayList<Character> generateDir(float x, float y, float width, float height) {
        ArrayList<Character> freeCell = new ArrayList<>();
        float testX, testY;

        for (int i = 0; i < rooms.size(); i++) {
            rooms.get(i).setRooms(rooms);
            testX = rooms.get(i).getX();
            testY = rooms.get(i).getY();

            if (testX+width != x) freeCell.add('r');
            if (testX-width != x) freeCell.add('l');
            if (testY+height != y) freeCell.add('u');
            if (testY-height != y) freeCell.add('d');
        }

        if (freeCell.isEmpty()) freeCell.add('n'); // cuz it can be empty, leading to crash
        Collections.shuffle(freeCell);
        return freeCell;
    }

    void setDoors() {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            for (int j = 0; j < rooms.size(); j++) {
                if (i != j) {
                    Room otherRoom = rooms.get(j);
                    if (room.getX() == otherRoom.getX() && room.getY() + room.getHeight() == otherRoom.getY()) {
                        room.removeTopWall();
                        otherRoom.removeBottomWall();
                    } else if (room.getX() == otherRoom.getX() && room.getY() - room.getHeight() == otherRoom.getY()) {
                        room.removeBottomWall();
                        otherRoom.removeTopWall();
                    } else if (room.getX() + room.getWidth() == otherRoom.getX() && room.getY() == otherRoom.getY()) {
                        room.removeRightWall();
                        otherRoom.removeLeftWall();
                    } else if (room.getX() - room.getWidth() == otherRoom.getX() && room.getY() == otherRoom.getY()) {
                        room.removeLeftWall();
                        otherRoom.removeRightWall();
                    }
                }
            }
        }
    }

    boolean hasFreeCell(float x, float y){
        float roomX , roomY; // init

        for (int i = 0; i < rooms.size(); i++) {
            roomX = rooms.get(i).getX();
            roomY = rooms.get(i).getY();

            if (roomX == x && roomY == y) return false;
        }
        return true;
    }

    int hasDir(char direction, float x, float y) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getDoors().contains(direction) && rooms.get(i).getX() == x && rooms.get(i).getY() == y) return i;
        }
        return -1;
    }

//    int hasDirNum(char direction, float x, float y) {
//        for (int i = 0; i < rooms.size(); i++) {
//            if (rooms.get(i).getDoors().contains(direction) && rooms.get(i).getX() == x && rooms.get(i).getY() == y) return i;
//        }
//        return -1;
//    }
}
