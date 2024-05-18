package com.deadline;

import static com.deadline.DdlnGame.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

public class ScreenGame implements Screen {
    DdlnGame game;
    Random random = new Random();

    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;

    World world;
    MyContactListener contactListener;
    Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();

    RayHandler rayHandler;
    PointLight playerLight;

    BitmapFont font, fontUi;
    GlyphLayout glyphLayout;

    OnScreenJoystick joystick;

    Texture[] imgRoom = new Texture[2];
    Texture imgHorWall, imgVerWall;
    Texture imgJstBase, imgJstKnob;
    Texture imgElevator;
    Texture imgPaperWad;
    Texture imgRouble;
    Texture imgHorDoorAtlas, imgVerDoorAtlas;
    Texture imgHealthAtlas, imgWalletAtlas;
    Texture imgPlayerAtlas;
    Texture imgGhostAtlas;
    Texture imgBlank;
    TextureRegion[] imgHorDoor = new TextureRegion[2];
    TextureRegion[] imgVerDoor = new TextureRegion[2];
    TextureRegion[] imgHealth = new TextureRegion[7];
    TextureRegion[] imgWallet = new TextureRegion[4];
    TextureRegion[][] imgPlayerIdle = new TextureRegion[4][6];
    TextureRegion[][] imgPlayerRun = new TextureRegion[4][6];
    TextureRegion[] imgGhost = new TextureRegion[4];

    Sound sndPaperBump;

    Player player;
    Weapon paperWad;
    Weapon ghostOrb;

    ArrayList<Room> rooms = new ArrayList<>();
    ArrayList<Body> elevators = new ArrayList<>();
    ArrayList<Ghost> ghosts = new ArrayList<>();
    ArrayList<Coin> coins = new ArrayList<>();

    MyButton btnAttack;

    String txtCord = "Empty";
    boolean actJoystick = false;
    boolean actAttack = false;
    boolean toReset = false;
    Vector2 position = new Vector2(0, 0);
    long deathTime = 0;
    public int wallet = 0;
    public int level = 0;

    static final int THICKNESS = 10;

    public ScreenGame(DdlnGame game) {
        this.game = game;
        batch = game.batch;
        camera = game.camera;
        touch = game.touch;
        font = game.font;
        fontUi = game.fontUi;
        glyphLayout = DdlnGame.glyphLayout;

        world = new World(new Vector2(0, 0), true);
        MyContactListener contactListener = new MyContactListener(world);
        world.setContactListener(contactListener);
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(ambientLight);
        Vector2 position = new Vector2(0, 0);

        glyphLayout = new GlyphLayout();

        imgRoom[0] = new Texture("room0.png");
        imgRoom[1] = new Texture("room1.png");

        imgHorWall = new Texture("horizontalWall.png");
        imgVerWall = new Texture("verticalWall.png");

        imgElevator = new Texture("elevator.png");

        imgJstBase = new Texture("joystickBase.png");
        imgJstKnob = new Texture("joystickKnob.png");

        imgHorDoorAtlas = new Texture("horizontalDoorAtlas.png");
        imgVerDoorAtlas = new Texture("verticalDoorAtlas.png");
        imgHealthAtlas = new Texture("healthbarAtlas.png");
        imgWalletAtlas = new Texture("walletAtlas.png");
        imgPlayerAtlas = new Texture("playerAtlas.png");
        imgGhostAtlas = new Texture("ghostAtlas.png");

        imgPaperWad = new Texture("paperWad.png");
        imgRouble = new Texture("rouble.png");

        imgBlank = new Texture("blank.png");

        sndPaperBump = Gdx.audio.newSound(Gdx.files.internal("paperBump.mp3"));

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
        for (int i = 0; i < imgHealth.length; i++) {
            imgHealth[i] = new TextureRegion(imgHealthAtlas, 0,(imgHealth.length-1-i)*16,64, 16);
        }
        for (int i = 0; i < imgWallet.length; i++) {
            imgWallet[i] = new TextureRegion(imgWalletAtlas, 0, (imgWallet.length-1-i)*16, 16, 16);
        }
        imgHorDoor[0] = new TextureRegion(imgHorDoorAtlas, 0, 0, 64, 16);
        imgHorDoor[1] = new TextureRegion(imgHorDoorAtlas, 0, 16, 64, 16);
        imgVerDoor[0] = new TextureRegion(imgVerDoorAtlas, 0, 0, 16, 64);
        imgVerDoor[1] = new TextureRegion(imgVerDoorAtlas, 16, 0, 16, 64);


        paperWad = new Weapon(imgPaperWad, 35, 1250, 950, 2);
        ghostOrb = new Weapon(1250, 2500, 1);

        player = new Player(world, 14, 18, 50, 50, 6, 6, 450, paperWad);
        playerLight =  new PointLight(rayHandler, 256, new Color(1,1,1,0.475f), playerLightDistance, player.getX(), player.getY());
        playerLight.setSoftnessLength(25);

        Filter filter = new Filter();
        filter.categoryBits = 1;
        filter.maskBits = 1;
        playerLight.setContactFilter((short)1,(short)1,(short)1);

        joystick = new OnScreenJoystick(SCR_HEIGHT / 6, SCR_HEIGHT / 12);

        generateMap(7);
        generateRooms();
    }

    @Override
    public void show() {
        resetWorld();
    }

    @Override
    public void render(float delta) {
        position.lerp(new Vector2(player.getX(), player.getY()), 0.1f); // 0.1f - это коэффициент сглаживания
        camera.position.set(position, 0);
        camera.update();

        // касания

        touchHandler();

        // события

        player.changePhase();
        ghostsChangePhase();
        player.updateProjectiles();
        player.update(1);
        ghostsUpdate();
        doorsUpdate();
        coinsUpdate();
        levelUpdate();
        btnAttack.update(position.x + SCR_WIDTH / 3, position.y - SCR_HEIGHT / 3);

        // отрисовка
        ScreenUtils.clear(0, 0, 0, 0);
        debugRenderer.render(world, camera.combined);
        batch.setProjectionMatrix(camera.combined);
        rayHandler.setCombinedMatrix(camera.combined, camera.position.x, camera.position.y, camera.viewportWidth, camera.viewportHeight);
        camera.update();

        batch.begin();
        wallBatch();
        projectileBatch();
        coinBatch();
        doorPreBatch();
        playerBatch();
        ghostsBatch();
        doorPostBatch();
        elevatorBatch();
        hudBatch();

//        txtCord = "HP "+player.getHealth()+"\nMoney "+wallet;
//        font.draw(batch, txtCord, position.x - SCR_WIDTH / 2, position.y + SCR_HEIGHT / 2);

        joystick.render(batch, imgJstBase, imgJstKnob, position.x - SCR_WIDTH / 2.75f, position.y - SCR_HEIGHT / 4);
        batch.draw(imgJstBase, btnAttack.x, btnAttack.y, btnAttack.width, btnAttack.height);

        if (player.getHealth() > 0) {
            world.step(1 / 60f, 6, 2);
        } else {
            batch.draw(imgBlank, position.x-SCR_WIDTH, position.y-SCR_HEIGHT, Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2);
            glyphLayout.setText(font, "I'm not ready to die yet");
            font.draw(batch, "I'm not ready to die yet", position.x - glyphLayout.width/2, position.y);
            if (deathTime == 0) {
                deathTime = TimeUtils.millis();
            }
            else if (deathTime + 4000 < TimeUtils.millis()) {
                resetProgress();
                game.setScreen(game.screenMenu);
            }
        }

        batch.end();

        playerLight.attachToBody(player.getBody());
        rayHandler.updateAndRender();
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
        rayHandler.dispose();
        imgRoom[0].dispose();
        imgRoom[1].dispose();
        imgHorWall.dispose();
        imgVerWall.dispose();
        imgHorDoorAtlas.dispose();
        imgVerDoorAtlas.dispose();
        imgPlayerAtlas.dispose();
        imgGhostAtlas.dispose();
        imgJstBase.dispose();
        imgJstKnob.dispose();
        imgPaperWad.dispose();
        imgRouble.dispose();
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

    public void resetProgress() {
        deathTime = 0;
        wallet = 0;
        level = 0;
    }

    public void resetWorld() {
//        world = new World(new Vector2(0, 0), true);
//        world.setContactListener(contactListener);
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        for(int i = 0; i < bodies.size; i++)
        {
            if(!world.isLocked())
                world.destroyBody(bodies.get(i));
        }

        rooms.clear();
        elevators.clear();
        ghosts.clear();
        coins.clear();

        txtCord = "Empty";
        actJoystick = false;
        actAttack = false;
        player = new Player(world, 14, 18, 50, 50, 6, 6, 450, paperWad);

        generateMap(7);
        generateRooms();
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
                Ghost ghost = ghosts.get(i);
                int phase = ghost.getPhase();
                float x = ghosts.get(i).getX() - imgGhost[0].getRegionWidth() / 2f; // centralized image x
                float y = ghosts.get(i).getY() - imgGhost[0].getRegionHeight() / 2.25f; // centralized image y
                batch.draw(imgGhost[phase], x, y);
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
            batch.draw(imgRoom[level], room.getX(), room.getY(), room.getWidth(), room.getHeight());
        }
    }

    private void doorPostBatch() {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            for (int j = 0; j < room.getDoorHorBodies().size(); j++) {
                Body wall = room.getDoorHorBodies().get(j);
                if (j % 3 == 1) {
                    if (wall.getUserData() == "closeDoor") {
                        batch.draw(imgHorDoor[1], room.getX() + room.getWidth() * (1f / 3) + THICKNESS, wall.getPosition().y - 5, room.getWidth() - ((room.getWidth() / 3) * 2) - 20, THICKNESS);
                    } else {
                        batch.draw(imgHorDoor[0], room.getX() + room.getWidth() * (1f / 3) + THICKNESS, wall.getPosition().y - 5, room.getWidth() - ((room.getWidth() / 3) * 2) - 20, THICKNESS);

                    }
                }
            }

            for (int j = 0; j < room.getDoorVerBodies().size(); j++) {
                Body wall = room.getDoorVerBodies().get(j);
                switch (j % 3) { // %3 cuz there may be two doors
                    case 0:
                        batch.draw(imgVerWall, wall.getPosition().x - 5, room.getY() + THICKNESS, THICKNESS, room.getHeight() / 3);
                        break;
                    case 1:
                        if (wall.getUserData() == "closeDoor") {
                            batch.draw(imgVerDoor[1], wall.getPosition().x - 5, room.getY() + room.getHeight() * (1f / 3) + THICKNESS, THICKNESS, room.getHeight() - ((room.getHeight() / 3) * 2) - 20);
                        } else {
                            batch.draw(imgVerDoor[0], wall.getPosition().x - 5, room.getY() + room.getHeight() * (1f / 3) + THICKNESS, THICKNESS, room.getHeight() - ((room.getHeight() / 3) * 2) - 20);
                        }
                        break;
                    case 2:
                        batch.draw(imgVerWall, wall.getPosition().x - 5, room.getY() + room.getHeight() * (2f / 3) - THICKNESS, THICKNESS, room.getHeight() / 3);
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
                        batch.draw(imgHorWall, room.getX() + THICKNESS, wall.getPosition().y - 5, room.getWidth() / 3, THICKNESS);
                        break;
                    case 2:
                        batch.draw(imgHorWall, room.getX() + room.getWidth() * (2f / 3) - THICKNESS, wall.getPosition().y - 5, room.getWidth() / 3, THICKNESS);
                }
            }
        }
    }

    private void elevatorBatch(){
        for (int i = 0; i < elevators.size(); i++) {
            Body elevator = elevators.get(i);
            batch.draw(imgElevator, elevator.getPosition().x-rooms.get(0).getHeight()/8, elevator.getPosition().y-rooms.get(0).getHeight()/8, rooms.get(0).getWidth()/4, rooms.get(0).getHeight()/4);
        }
    }

    private void projectileBatch() {
        for (int i = 0; i < player.getProjectiles().size(); i++) {
            Projectile projectile = player.getProjectiles().get(i);
            if (projectile.getBody().isActive())
                batch.draw(imgPaperWad, projectile.getX() - projectile.getRadius() * 2, projectile.getY() - projectile.getRadius() * 2);
        }
    }

    private void coinBatch() {
        for (int i = 0; i < coins.size(); i++) {
            if (coins.get(i).getBody().isActive()) {
                Coin coin = coins.get(i);
                batch.draw(imgRouble, coin.getX() - coin.getRadius(), coin.getY() - coin.getRadius(), coin.getRadius() * 2, coin.getRadius() * 2);
            }
        }
    }

    private void hudBatch() {
        batch.draw(imgHealth[player.getHealth()], position.x - SCR_WIDTH / 2+2, position.y  + SCR_HEIGHT / 2.5f - 4);
        if (wallet==0) batch.draw(imgWallet[0], position.x - SCR_WIDTH / 2 + 2, position.y  + SCR_HEIGHT / 2.5f - 22);
        else if (wallet<5) batch.draw(imgWallet[1], position.x - SCR_WIDTH / 2 + 2, position.y  + SCR_HEIGHT / 2.5f - 22);
        else if (wallet<10) batch.draw(imgWallet[2], position.x - SCR_WIDTH / 2 + 2, position.y  + SCR_HEIGHT / 2.5f - 22);
        else batch.draw(imgWallet[3], position.x - SCR_WIDTH / 2 + 2, position.y  + SCR_HEIGHT / 2.5f - 22);
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
                Ghost ghost = ghosts.get(i);
                if (ghost.isBattle) ghost.attack(player.getPosition());
                if (ghost.isAlive()) {
                    if (!player.getProjectiles().isEmpty()) {
                        if (ghost.getBody().getUserData() == "hit") {
                            ghosts.get(i).hit(player.getWeapon().getDamage());
                            player.getProjectiles().get(player.getProjectiles().size() - 1).getBody().setActive(false);
                            world.destroyBody(player.getProjectiles().get(player.getProjectiles().size() - 1).getBody());
                            player.getProjectiles().remove(player.getProjectiles().size() - 1);
                            ghost.getBody().setUserData("ghost");
                            sndPaperBump.play();
                        }
                    }
                }
                if (!ghost.isAlive()) {
                    ghost.getBody().setActive(false);
                    world.destroyBody(ghost.getBody());
                    ghosts.remove(i);
                    for (int j = 0; j < random.nextInt(2)+1; j++) {
                        Coin coin = new Coin(world, ghost.getX() + (random.nextInt(10)+5)*j, ghost.getY() + (random.nextInt(10)+5)*j, 4.5f, 1);
                        coins.add(coin);
                    }
                    break;
                }
            }
        }
    }

    private void doorsUpdate() {
        Room room = rooms.get(getPlayerRoom());
        int ghostNum = 0;
        for (int i = 0; i < ghosts.size(); i++) {
            if (ghosts.get(i).getRoom() == getPlayerRoom()) {
                ghostNum++;
                ghosts.get(i).setBattle(true);
            }
        }

        for (int i = 1; i < room.getDoorVerBodies().size(); i += 3) {
            if (ghostNum > 0) {
                room.getDoorVerBodies().get(i).setUserData("closeDoor");
                player.setBattleState(true);
            } else {
                room.getDoorVerBodies().get(i).setUserData("openDoor");
                player.setBattleState(false);
            }
        }
        for (int i = 1; i < room.getDoorHorBodies().size(); i += 3) {
            if (ghostNum > 0) {
                room.getDoorHorBodies().get(i).setUserData("closeDoor");
                player.setBattleState(true);
            } else {
                room.getDoorHorBodies().get(i).setUserData("openDoor");
                player.setBattleState(false);
            }
        }
    }

    private void coinsUpdate() {
        for (int i = 0; i < coins.size(); i++) {
            if (coins.get(i) != null) {
                Coin coin = coins.get(i);
                if (coin.getBody().getUserData()=="got") {
                    world.destroyBody(coin.getBody());
                    wallet++;
                    coins.remove(i);
                    break;
                }
            }
        }
    }

    public void levelUpdate() {
        if (player.getBody().getUserData()=="moved") {
            resetWorld();
            level++;
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
        elevators.add(rooms.get(rooms.size() - 1).setElevator());
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
                    for (int j = 0; j < MathUtils.random(2) + 1; j++) {
//                        float randomFloat = a + new Random().nextFloat() * (b - a);
                        float spawnX = MathUtils.random(room.getX() + THICKNESS, room.getX() + room.getWidth() - THICKNESS);
                        float spawnY = MathUtils.random(room.getY() + THICKNESS, room.getY() + room.getHeight() - THICKNESS);
                        Ghost ghost = new Ghost(world, 20, 24, spawnX, spawnY, 5, 4, 350, ghostOrb);
                        ghost.setRoomNum(i);
                        ghosts.add(ghost);
                    }
            }
        }
    }

    public int getPlayerRoom() {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (player.getX() > room.getX() + THICKNESS && player.getX() < room.getX() + room.getWidth() - THICKNESS && player.getY() > room.getY() + THICKNESS && player.getY() < room.getY() + room.getHeight() - THICKNESS) {
                return i;
            }
        }
        return 0;
    }
}
