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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Collections;

public class ScreenGame implements Screen {
    DdlnGame game;

    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;

    World world;
    MyContactListener contactListener;
    Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    BitmapFont font, fontUi;

    OnScreenJoystick joystick;

    Texture imgRoom;
    Texture imgHorWall, imgVerWall;
    Texture imgHorDoor, imgVerDoor;
    Texture imgJstBase, imgJstKnob;
    Texture imgPaperWad;
    Texture imgPlayerAtlas;
    Texture imgGhostAtlas;
    TextureRegion[][] imgPlayerIdle = new TextureRegion[4][6];
    TextureRegion[][] imgPlayerRun = new TextureRegion[4][6];
    TextureRegion[] imgGhost = new TextureRegion[4];

    ArrayList<Room> rooms = new ArrayList<>();

    Player player;
    Weapon paperWad;

    ArrayList<Ghost> ghosts = new ArrayList<>();

    MyButton btnAttack;

    String txtCord = "Empty";
    boolean actJoystick = false;
    boolean actAttack = false;

    public ScreenGame(DdlnGame game) {
        this.game = game;
        batch = game.batch;
        camera = game.camera;
        touch = game.touch;
        font = game.font;
        fontUi = game.fontUi;

        world = new World(new Vector2(0, 0), true);
        MyContactListener contactListener = new MyContactListener(world);
        world.setContactListener(contactListener);

        glyphLayout = new GlyphLayout();

        imgRoom = new Texture("room.png");

        imgHorWall = new Texture("horizontalWall.png");
        imgVerWall = new Texture("verticalWall.png");

        imgHorDoor = new Texture("horizontalDoor.png");
        imgVerDoor = new Texture("verticalDoor.png");

        imgJstBase = new Texture("joystickBase.png");
        imgJstKnob = new Texture("joystickKnob.png");

        imgPlayerAtlas = new Texture("playerAtlas.png");
        imgGhostAtlas = new Texture("ghostAtlas.png");

        imgPaperWad = new Texture("paperWad.png");

        btnAttack = new MyButton(SCR_WIDTH / 3, SCR_HEIGHT / 3, SCR_WIDTH / 20);

        int iter = 0;
        for (int i = 0; i < imgPlayerIdle.length; i++) {
            for (int j = 0; j < imgPlayerIdle[0].length; j++) {
                imgPlayerIdle[i][j] = new TextureRegion(imgPlayerAtlas, iter * 16, 0, 16, 32);
                imgPlayerRun[i][j] = new TextureRegion(imgPlayerAtlas, iter * 16, 32, 16, 32);
                iter++;
            }
        }
        for (int i = 0; i < imgGhost.length; i++) {
            imgGhost[i] = new TextureRegion(imgGhostAtlas, i * 32, 0, 32, 32);
        }

        paperWad = new Weapon(imgPaperWad, false, 35, 1250, 950, 1);

        player = new Player(world, 14, 18, 50, 50, 6, 6, 450, paperWad);

        joystick = new OnScreenJoystick(SCR_HEIGHT / 6, SCR_HEIGHT / 12);

        generateMap(7);
        generateRooms();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // касания

        touchHandler();

        // события

        player.changePhase();
        ghostsChangePhase();
        player.updateProjectiles();
        ghostsUpdate();
        btnAttack.update(player.getX() + SCR_WIDTH / 3, player.getY() - SCR_HEIGHT / 3);

        // отрисовка
        ScreenUtils.clear(0, 0, 0, 0);
        debugRenderer.render(world, camera.combined);
        batch.setProjectionMatrix(camera.combined);
        camera.position.set(player.getX(), player.getY(), 0);
        camera.update();
        batch.begin();


        wallBatch();
        projectileBatch();
        doorPreBatch();
        playerBatch();
        ghostsBatch();
        doorPostBatch();


//        for (int i = 0; i < player.getProjectiles().size(); i++) {
//            txtCord = String.valueOf(player.getProjectiles().get(i).getCreateTime());
//        }

        // ghost roomnum in constructor
        txtCord = "" + rooms.get(0).getX() + " " + rooms.get(0).getY();
        font.draw(batch, txtCord, player.getX() - SCR_WIDTH / 2, player.getY() + SCR_HEIGHT / 2);

        joystick.render(batch, imgJstBase, imgJstKnob, player.getX() - SCR_WIDTH / 2.75f, player.getY() - SCR_HEIGHT / 4);
        batch.draw(imgJstBase, btnAttack.x, btnAttack.y, btnAttack.width, btnAttack.height);

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
        imgHorWall.dispose();
        imgVerWall.dispose();
        imgHorDoor.dispose();
        imgVerDoor.dispose();
        imgRoom.dispose();
        imgPlayerAtlas.dispose();
        imgJstBase.dispose();
        imgJstKnob.dispose();
        player.dispose();
        paperWad.dispose();
        batch.dispose();
    }

    private void touchHandler() { // if touch.i == using touchaattack same or == null
        actJoystick = false;
        actAttack = false;
        ArrayList<Vector3> touches = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            touches.add(new Vector3());
        }

        for (int i = 0; i < 3; i++) {
            if (Gdx.input.isTouched(i)) {
                touches.get(i).set(Gdx.input.getX(i), Gdx.input.getY(i), 0);
                camera.unproject(touches.get(i));

                if (touches.get(i).x < player.getX()) {
                    actJoystick = true;
                    joystick.updateKnob(touches.get(i));
                } else if (touches.get(i).x > player.getX()) {
                    if (btnAttack.hit(touches.get(i).x, touches.get(i).y)) {
                        actAttack = true;
                    }
                }
            }
        }

        if (actJoystick) {
            Vector2 directionVector = joystick.getDirectionVector();
            player.getBody().setLinearVelocity(
                    directionVector.x * player.getSpeed(),
                    directionVector.y * player.getSpeed()
            );
            // Update player direction based on joystick direction
            if (Math.abs(directionVector.x) > Math.abs(directionVector.y)) {
                if (directionVector.x > 0) player.setDirection('r');
                else player.setDirection('l');
            } else {
                if (directionVector.y > 0) player.setDirection('u');
                else player.setDirection('d');
            }
        } else {
            joystick.resetKnob();
            player.getBody().setLinearVelocity(0, 0);
        }

        if (actAttack) {
//            player.changeBattleState(!player.getBattleState());
            player.attack();
        }
    }

    private void playerBatch() {
        int phase = player.getPhase();
        float x = player.getX() - imgPlayerIdle[0][0].getRegionWidth() / 2f; // centralized image x
        float y = player.getY() - imgPlayerIdle[0][0].getRegionHeight() / 3.5f; // centralized image y
        boolean isMoving = player.getBody().isAwake(); // реагировать не на сон, а на джойстик! w

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

    private void ghostsBatch() {
        if (!ghosts.isEmpty()) {
            for (int i = 0; i < ghosts.size(); i++) {
                int phase = ghosts.get(i).getPhase();
                float x = ghosts.get(i).getX() - imgGhost[0].getRegionWidth() / 2f; // centralized image x
                float y = ghosts.get(i).getY() - imgGhost[0].getRegionHeight() / 2.25f; // centralized image y
                boolean isMoving = ghosts.get(0).getBody().isAwake();
                if (isMoving) batch.draw(imgGhost[phase], x, y);
                else batch.draw(imgGhost[1], x, y);
            }
        }
    }

    private void ghostsChangePhase() {
        for (int i = 0; i < ghosts.size(); i++) {
            ghosts.get(i).changePhase();
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
                switch (j % 3) { // %3 cuz there may be two doors
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
                switch (j % 3) {
                    case 0:
                        batch.draw(imgHorWall, room.getX() + 10, wall.getPosition().y - 5, room.getWidth() / 3, 10);
                        break;
                    case 2:
                        batch.draw(imgHorWall, room.getX() + room.getWidth() * (2f / 3) - 10, wall.getPosition().y - 5, room.getWidth() / 3, 10);
                }
            }
        }
    }

    private void projectileBatch() {
        for (int i = 0; i < player.getProjectiles().size(); i++) {
            Projectile projectile = player.getProjectiles().get(i);
            if (projectile.getBody().isActive())
                batch.draw(imgPaperWad, projectile.getX()-projectile.getRadius()*2, projectile.getY()-projectile.getRadius()*2);
        }
    }

//    private void ghostHitCheck() {
//        if (!player.getProjectiles().isEmpty() && !ghosts.isEmpty()) {
//        for (int i = 0; i < ghosts.size(); i++) {
//            for (int j = 0; j < player.getProjectiles().size(); j++) {
//                Ghost ghost = ghosts.get(i);
//                Projectile projectile = player.getProjectiles().get(j);
//                if ((projectile.getX()>ghost.getX()-ghost.getWidth()/2 && projectile.getX()<ghost.getX()+ghost.getWidth()/2)
//                        &&(projectile.getY()>ghost.getY()-ghost.getHeight()/2 && projectile.getY()<ghost.getY()+ghost.getHeight()/2)) {
//                    ghosts.get(i).hit(player.getProjectiles().get(j).getDamage());
//                    player.getProjectiles().get(j).getBody().setActive(false);
//                    world.destroyBody(player.getProjectiles().get(j).getBody());
//                }
//            }
//        }
//        }
//    }

    private void ghostsUpdate() {
        if (ghosts != null) {
            for (int i = 0; i < ghosts.size(); i++) {
                if (ghosts.get(i).isAlive()) {
                    if (!player.getProjectiles().isEmpty()) {
                        if (ghosts.get(i).getBody().getUserData() == "hit") {
                            ghosts.get(i).hit(player.getWeapon().getDamage());
                            player.getProjectiles().get(player.getProjectiles().size() - 1).getBody().setActive(false);
                            world.destroyBody(player.getProjectiles().get(player.getProjectiles().size() - 1).getBody());
                            player.getProjectiles().remove(player.getProjectiles().size() - 1);
                            ghosts.get(i).getBody().setUserData("ghost");
                        }
                    }
                }
                if (!ghosts.get(i).isAlive()) {
                    ghosts.get(i).getBody().setActive(false);
                    world.destroyBody(ghosts.get(i).getBody());
                    ghosts.remove(i);
                    break;
                }
            }
        }
    }

    private void generateMap(int maxRooms) {
        float roomX = 0, roomY = 0;
        float roomWidth = 200, roomHeight = 200;
        char roomType = 'd';
        ArrayList<Character> doors = new ArrayList<>();


        for (int i = 0; i < maxRooms; i++) {
            if (i == 0) roomType = 's';
            else if (i == maxRooms - 1) roomType = 'q';
            else roomType = 'd';

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

            ArrayList<Room> tempRooms = new ArrayList<>(rooms);
            Room room = new Room(world, roomX, roomY, roomWidth, roomHeight, doors, tempRooms, roomType);
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


    private ArrayList<Character> generateDir(float x, float y, float width, float height) {
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

    private void setDoors() {
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

    private boolean hasFreeCell(float x, float y) {
        float roomX, roomY; // init

        for (int i = 0; i < rooms.size(); i++) {
            roomX = rooms.get(i).getX();
            roomY = rooms.get(i).getY();

            if (roomX == x && roomY == y) return false;
        }
        return true;
    }

    private void generateRooms() {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            switch (room.getType()) {
                case 's':
                    continue;
                case 'q':
                    continue;
                case 'd':
                    for (int j = 0; j < MathUtils.random(3) + 1; j++) {
//                        float randomFloat = a + new Random().nextFloat() * (b - a);
                        float spawnX = MathUtils.random(room.getX() + 10, room.getX() + room.getWidth() - 10);
                        float spawnY = MathUtils.random(room.getY() + 10, room.getY() + room.getHeight() - 10);
                        Ghost ghost = new Ghost(world, 20, 24, spawnX, spawnY, 4, 4, 350, null);
                        ghosts.add(ghost);
                    }
            }
        }
    }
}
