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
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Collections;

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
    Texture imgRoom;
    Texture imgHorWall, imgVerWall;
    Texture imgHorDoor, imgVerDoor;
    Texture imgJstBase, imgJstKnob;
    Texture imgPlayerAtlas;
    TextureRegion[][] imgPlayerIdle = new TextureRegion[4][6];
    TextureRegion[][] imgPlayerRun = new TextureRegion[4][6];

    ArrayList<Room> rooms = new ArrayList<>();
    Player player;

//    String txtCord = "Empty";
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

        imgRoom = new Texture("room.png");

        imgHorWall = new Texture("horizontalWall.png");
        imgVerWall = new Texture("verticalWall.png");

        imgHorDoor = new Texture("horizontalDoor.png");
        imgVerDoor = new Texture("verticalDoor.png");

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
        player = new Player(world, 14, 16, 50, 50, 6, 450);

        joystick = new OnScreenJoystick(SCR_HEIGHT / 6, SCR_HEIGHT / 12);

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

//        txtCord = "";
//        for (int i = 0; i < rooms.size(); i++) {
//            txtCord += "\nX " + rooms.get(i).getX() + "\nY " + rooms.get(i).getY() + "\n" + rooms.get(i).getDoors() + "\n" + (hasDir('r', rooms.get(i).getX() + 150, rooms.get(i).getY()) != -1);
//        }
//        font.draw(batch, txtCord, player.getX() - SCR_WIDTH / 2, player.getY() + SCR_HEIGHT / 2);

        wallBatch();

        doorPreBatch();

        playerBatch();

        doorPostBatch();

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
        imgHorWall.dispose();
        imgVerWall.dispose();
        imgRoom.dispose();
        imgPlayerAtlas.dispose();
        imgJstBase.dispose();
        imgJstKnob.dispose();
        batch.dispose();
    }

    private void playerBatch() { // separated it cuz too big
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

    private void wallBatch() {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            batch.draw(imgRoom, room.getX(), room.getY(), room.getWidth(), room.getHeight());
        }
    }

    private void doorPostBatch() {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            for (int j = 0; j < room.getDoorHorBodies().size(); j++) {
                Body wall = room.getDoorHorBodies().get(j);
                if (j % 3 == 1) {
                    batch.draw(imgHorDoor, room.getX() + room.getWidth() * (1f / 3) + 10, wall.getPosition().y - 5, room.getWidth() - ((room.getWidth() / 3) * 2) - 20, 10);
                }
            }

            for (int j = 0; j < room.getDoorVerBodies().size(); j++) {
                Body wall = room.getDoorVerBodies().get(j);
                switch (j%3) { // %3 cuz there may be two doors
                    case 0:
                        batch.draw(imgVerWall, wall.getPosition().x - 5, room.getY() + 10, 10, room.getHeight() / 3);
                        break;
                    case 1:
                        batch.draw(imgVerDoor, wall.getPosition().x - 5, room.getY() + room.getHeight() * (1f / 3) + 10, 10, room.getHeight() - ((room.getHeight() / 3) * 2) - 20);
                        break;
                    case 2:
                        batch.draw(imgVerWall, wall.getPosition().x - 5, room.getY() + room.getHeight() * (2f / 3) - 10, 10, room.getHeight() / 3);
                }
            }
        }
    }

    private void doorPreBatch() {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            for (int j = 0; j < room.getDoorHorBodies().size(); j++) {
                Body wall = room.getDoorHorBodies().get(j);
                switch (j%3) {
                    case 0: batch.draw(imgHorWall, room.getX()+10, wall.getPosition().y-5, room.getWidth()/ 3, 10); break;
                    case 2: batch.draw(imgHorWall, room.getX() + room.getWidth()*(2f/3)-10, wall.getPosition().y-5, room.getWidth()/ 3, 10);
                }
            }
        }
    }

    void generateMap(int maxRooms) {
        float roomX = 0, roomY = 0;
        float roomWidth = 200, roomHeight = 200;
        ArrayList<Character> doors = new ArrayList<>();

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

        ArrayList<Character> lastDir = rooms.get(rooms.size() - 1).getDoors();

        Room lastRoom = rooms.get(rooms.size() - 1);
        Room preLastRoom = rooms.get(rooms.size() - 2);

        if (lastDir.contains('u')) {
            if (lastRoom.getY() + roomHeight == preLastRoom.getY()) {
                lastRoom.removeTopWall();
                preLastRoom.removeBottomWall();
            }
        }
        if (lastDir.contains('d')) {
            if (lastRoom.getY() - roomHeight == preLastRoom.getY()) {
                lastRoom.removeBottomWall();
                preLastRoom.removeTopWall();
            }
        }
        if (lastDir.contains('l')) {
            if (lastRoom.getX() - roomWidth == preLastRoom.getX()) {
                lastRoom.removeLeftWall();
                preLastRoom.removeRightWall();
            }
        }
        if (lastDir.contains('r')) {
            if (lastRoom.getX() + roomWidth == preLastRoom.getX()) {
                lastRoom.removeRightWall();
                preLastRoom.removeLeftWall();
            }
        }
    }


    ArrayList<Character> generateDir(float x, float y, float width, float height) {
        ArrayList<Character> freeCell = new ArrayList<>();
        float testX, testY;

        for (int i = 0; i < rooms.size(); i++) {
//            rooms.get(i).setRooms(rooms);
            testX = rooms.get(i).getX();
            testY = rooms.get(i).getY();

            if (testX + width != x) freeCell.add('r');
            if (testX - width != x) freeCell.add('l');
            if (testY + height != y) freeCell.add('u');
            if (testY - height != y) freeCell.add('d');
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

    boolean hasFreeCell(float x, float y) {
        float roomX, roomY; // init

        for (int i = 0; i < rooms.size(); i++) {
            roomX = rooms.get(i).getX();
            roomY = rooms.get(i).getY();

            if (roomX == x && roomY == y) return false;
        }
        return true;
    }
//    int hasDirNum(char direction, float x, float y) {
//        for (int i = 0; i < rooms.size(); i++) {
//            if (rooms.get(i).getDoors().contains(direction) && rooms.get(i).getX() == x && rooms.get(i).getY() == y) return i;
//        }
//        return -1;
//    }
}
