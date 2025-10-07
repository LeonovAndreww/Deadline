package com.deadline;

import static com.deadline.DdlnGame.SCR_HEIGHT;
import static com.deadline.DdlnGame.SCR_WIDTH;
import static com.deadline.DdlnGame.THICKNESS;
import static com.deadline.DdlnGame.ambientLight;
import static com.deadline.DdlnGame.isMusicOn;
import static com.deadline.DdlnGame.musicVolume;
import static com.deadline.DdlnGame.playerLightDistance;
import static com.deadline.DdlnGame.soundVolume;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.deadline.buttons.Button;
import com.deadline.buttons.RectangleButton;
import com.deadline.buttons.TextButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class ScreenGame implements Screen {
    private final DdlnGame game;

    Random random = new Random();

    SpriteBatch batch;
    OrthographicCamera camera;
    Vector3 touch;

    World world;
    //    Box2DDebugRenderer debugRenderer = new Box2DDebugRenderer();
    Vector2 position = new Vector2(0, 0);
    private final ArrayList<Body> bodiesToDestroy = new ArrayList<>();

    RayHandler rayHandler;
    PointLight playerLight;
    PointLight vendingPointLight;

    BitmapFont font, fontUi, fontButton;
    GlyphLayout glyphLayout;

    OnScreenJoystick joystick;

    List<Button> hudButtons = new ArrayList<>();
    List<Button> menuButtons = new ArrayList<>();
    List<Button> vendingButtons = new ArrayList<>();

    UiInput uiInput;

    Texture imgJstBase, imgJstKnob;
    Texture imgButtonMenuAtlas, imgButtonCloseAtlas, imgButtonLongAtlas, imgButtonSquareAtlas;
    Texture imgGameMenu;
    Texture imgVendingUi;
    Texture imgMinimapBackground;
    Texture imgHeal, imgDamageUp, imgSpeedUp;
    Texture[] imgRoom = new Texture[9];
    Texture imgHorWall, imgVerWall;
    Texture imgPaperWad;
    Texture imgPoisonBall;
    Texture imgRouble;
    Texture imgVendingMachine;
    Texture imgHorDoorAtlas, imgVerDoorAtlas;
    Texture imgHealthAtlas, imgWalletAtlas;
    Texture imgCutterAtlas;
    Texture imgPlayerAtlas;
    Texture imgGhostAtlas;
    Texture imgZombieAtlas;
    Texture imgWardenAtlas;
    Texture imgStaticObstacleAtlas, imgAnimatedObstacleAtlas;
    Texture imgChestAtlas;
    Texture imgElevatorAtlas;
    Texture imgMinimapRoomAtlas;
    Texture imgBlankAtlas;
    TextureRegion[] imgHorDoor = new TextureRegion[2];
    TextureRegion[] imgVerDoor = new TextureRegion[2];
    TextureRegion[] imgHealth = new TextureRegion[7];
    TextureRegion[] imgWallet = new TextureRegion[4];
    TextureRegion[] imgCutter = new TextureRegion[4];
    TextureRegion[][] imgPlayerIdle = new TextureRegion[4][6];
    TextureRegion[][] imgPlayerRun = new TextureRegion[4][6];
    TextureRegion[] imgGhost = new TextureRegion[4];
    TextureRegion[][] imgZombie = new TextureRegion[4][10];
    TextureRegion[] imgWarden = new TextureRegion[8];
    TextureRegion[] imgStaticObstacle = new TextureRegion[6];
    TextureRegion[][] imgAnimatedObstacle = new TextureRegion[1][2];
    TextureRegion[] imgChest = new TextureRegion[6];
    TextureRegion[] imgElevator = new TextureRegion[2];
    TextureRegion[][] imgMinimapRoom = new TextureRegion[2][3];
    TextureRegion[] imgBlank = new TextureRegion[3];
    TextureRegion[] imgButtonMenu = new TextureRegion[2];
    TextureRegion[] imgButtonClose = new TextureRegion[2];
    TextureRegion[] imgButtonLong = new TextureRegion[2];
    TextureRegion[] imgButtonSquare = new TextureRegion[2];

    Sound sndClick;
    Sound sndError;
    Sound sndPowerUp;
    Sound sndPaperBump;
    Sound sndCoinUp;
    Sound sndMonsterDeath;
    Sound sndElevatorUse;
    Sound sndPlayerDeath;
    Sound sndPhew;
    Sound sndChestOpen;

    Music[] musBackground = new Music[4];

    Player player;
    Vending vending;

    Weapon paperWad;
    Weapon ghostOrb;
    Weapon cutter;

    ArrayList<Room> rooms = new ArrayList<>();
    ArrayList<Elevator> elevators = new ArrayList<>();
    ArrayList<Ghost> ghosts = new ArrayList<>();
    ArrayList<Zombie> zombies = new ArrayList<>();
    ArrayList<Warden> wardens = new ArrayList<>();
    ArrayList<Coin> coins = new ArrayList<>();
    ArrayList<Obstacle> obstacles = new ArrayList<>();
    ArrayList<AnimatedObstacle> animatedObstacles = new ArrayList<>();
    ArrayList<Chest> chests = new ArrayList<>();

    MyButton btnAttack;

    RectangleButton btnMenu, btnMenuClose;
    TextButton btnMenuResume, btnMenuSettings, btnMenuDesktop;

    RectangleButton btnVendingClose, btnVendingBuyHeal, btnVendingBuyDamageUp, btnVendingBuySpeedUp;

    boolean actJoystick;
    boolean actAttack;
    boolean actMenu;
    boolean actVending;
    boolean up, down, left, right, attack, menu;

    boolean isPlayerDeathSoundOn;
    boolean tempBattleMode;

    long deathTime = 0;
    long elevatorUseTime = 0;
    long vendingCloseTime = 0;

    float menuX, menuY, menuWidth, menuHeight;
    float menuButtonWidth, menuButtonHeight;
    float vendingX, vendingY, vendingWidth, vendingHeight;

    public int wallet = 0;
    public int level = 0;
    public int musicNumber = 0;

    public int healCost = 5, damageUpCost = 10, speedUpCost = 8;

    public ScreenGame(DdlnGame game) {
        this.game = game;
        batch = game.batch;
        camera = game.camera;
        touch = game.touch;
        font = game.font;
        fontUi = game.fontUi;
        fontButton = game.fontButton;
        glyphLayout = DdlnGame.glyphLayout;

        actJoystick = false;
        actAttack = false;
        actMenu = false;

        menuX = menuY = 0;
        vendingX = vendingY = 0;

        isPlayerDeathSoundOn = false;
        tempBattleMode = false;
        isMusicOn = false;

        world = new World(new Vector2(0, 0), true);
        MyContactListener contactListener = new MyContactListener();
        world.setContactListener(contactListener);
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(ambientLight - 0.025f * level);

        glyphLayout = new GlyphLayout();

        imgJstBase = new Texture("textures/joystickBase.png");
        imgJstKnob = new Texture("textures/joystickKnob.png");

        imgButtonMenuAtlas = new Texture("textures/buttonMenuAtlas.png");
        imgButtonCloseAtlas = new Texture("textures/buttonCloseAtlas.png");
        imgButtonLongAtlas = new Texture("textures/buttonLongAtlas.png");
        imgButtonSquareAtlas = new Texture("textures/buttonSquareAtlas.png");

        imgGameMenu = new Texture("textures/gameMenu.png");

        imgVendingMachine = new Texture("textures/vendingMachine.png");
        imgVendingUi = new Texture("textures/vendingUi.png");

        imgMinimapBackground = new Texture("textures/minimapBackground.png");

        imgHeal = new Texture("textures/heal.png");
        imgDamageUp = new Texture("textures/damageUp.png");
        imgSpeedUp = new Texture("textures/speedUp.png");

        imgRoom[0] = new Texture("textures/room0.png");
        imgRoom[1] = new Texture("textures/room1.png");
        imgRoom[2] = new Texture("textures/room2.png");
        imgRoom[3] = new Texture("textures/room3.png");
        imgRoom[4] = new Texture("textures/room4.png");
        imgRoom[5] = new Texture("textures/room5.png");
        imgRoom[6] = new Texture("textures/room6.png");
        imgRoom[7] = new Texture("textures/room7.png");
        imgRoom[8] = new Texture("textures/room8.png"); // I should turn it into a texture region.
        // turn hor wall into texture region
        imgHorWall = new Texture("textures/horizontalWall.png");
        imgVerWall = new Texture("textures/verticalWall.png");

        imgHorDoorAtlas = new Texture("textures/horizontalDoorAtlas.png");
        imgVerDoorAtlas = new Texture("textures/verticalDoorAtlas.png");
        imgHealthAtlas = new Texture("textures/healthbarAtlas.png");
        imgWalletAtlas = new Texture("textures/walletAtlas.png");
        imgCutterAtlas = new Texture("textures/cutterAtlas.png");
        imgPlayerAtlas = new Texture("textures/playerAtlas.png");
        imgGhostAtlas = new Texture("textures/ghostAtlas.png");
        imgZombieAtlas = new Texture("textures/zombieAtlas.png");
        imgWardenAtlas = new Texture("textures/wardenAtlas.png");
        imgStaticObstacleAtlas = new Texture("textures/staticObstacleAtlas.png");
        imgAnimatedObstacleAtlas = new Texture("textures/animatedObstacleAtlas.png");
        imgChestAtlas = new Texture("textures/chestAtlas.png");
        imgElevatorAtlas = new Texture("textures/elevatorAtlas.png");
        imgMinimapRoomAtlas = new Texture("textures/minimapRoomAtlas.png");
        imgBlankAtlas = new Texture("textures/blankAtlas.png");

        imgPaperWad = new Texture("textures/paperWad.png");
        imgPoisonBall = new Texture("textures/poisonBall.png");
        imgRouble = new Texture("textures/rouble.png");

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

        for (int i = 0; i < imgZombie.length; i++) {
            for (int j = 0; j < imgZombie[0].length; j++) {
                imgZombie[i][j] = new TextureRegion(imgZombieAtlas, j * 32, i * 32, 32, 32);
            }
        }

        for (int i = 0; i < imgWarden.length; i++) {
            imgWarden[i] = new TextureRegion(imgWardenAtlas, i * 41, 0, 41, 32);
        }

        for (int i = 0; i < imgStaticObstacle.length; i++) {
            imgStaticObstacle[i] = new TextureRegion(imgStaticObstacleAtlas, i * 16, 0, 16, 32);
        }

        for (int i = 0; i < imgAnimatedObstacle.length; i++) {
            for (int j = 0; j < imgAnimatedObstacle[0].length; j++) {
                imgAnimatedObstacle[i][j] = new TextureRegion(imgAnimatedObstacleAtlas, j * 21, i * 32, 21, 32);
            }
        }

        for (int i = 0; i < imgChest.length; i++) {
            imgChest[i] = new TextureRegion(imgChestAtlas, i * 16, 0, 16, 32);
        }

        for (int i = 0; i < imgElevator.length; i++) {
            imgElevator[i] = new TextureRegion(imgElevatorAtlas, 0, i * 12, 64, 12);
        }

        for (int i = 0; i < imgHealth.length; i++) {
            imgHealth[i] = new TextureRegion(imgHealthAtlas, 0, (imgHealth.length - 1 - i) * 16, 64, 16);
        }

        for (int i = 0; i < imgWallet.length; i++) {
            imgWallet[i] = new TextureRegion(imgWalletAtlas, 0, (imgWallet.length - 1 - i) * 16, 16, 16);
        }

        for (int i = 0; i < imgCutter.length; i++) {
            imgCutter[i] = new TextureRegion(imgCutterAtlas, i * 16, 0, 16, 16);
        }

        for (int i = 0; i < imgMinimapRoom.length; i++) {
            for (int j = 0; j < imgMinimapRoom[i].length; j++) {
                imgMinimapRoom[i][j] = new TextureRegion(imgMinimapRoomAtlas, j * 8, i * 8, 8, 8);
            }
        }

        for (int i = 0; i < imgBlank.length; i++) {
            imgBlank[i] = new TextureRegion(imgBlankAtlas, i, 0, 1, 1);
        }

        for (int i = 0; i < imgButtonMenu.length; i++) {
            imgButtonMenu[i] = new TextureRegion(imgButtonMenuAtlas, 0, i * 32, 32, 32);
        }
        for (int i = 0; i < imgButtonClose.length; i++) {
            imgButtonClose[i] = new TextureRegion(imgButtonCloseAtlas, 0, i * 32, 32, 32);
        }
        for (int i = 0; i < imgButtonLong.length; i++) {
            imgButtonLong[i] = new TextureRegion(imgButtonLongAtlas, 0, i * 67, 315, 67);
        }
        for (int i = 0; i < imgButtonSquare.length; i++) {
            imgButtonSquare[i] = new TextureRegion(imgButtonSquareAtlas, 0, i * 100, 100, 100);
        }

        imgHorDoor[0] = new TextureRegion(imgHorDoorAtlas, 0, 0, 64, 16);
        imgHorDoor[1] = new TextureRegion(imgHorDoorAtlas, 0, 16, 64, 16);
        imgVerDoor[0] = new TextureRegion(imgVerDoorAtlas, 0, 0, 16, 64);
        imgVerDoor[1] = new TextureRegion(imgVerDoorAtlas, 16, 0, 16, 64);

        sndClick = Gdx.audio.newSound(Gdx.files.internal("sounds/click.ogg"));
        sndError = Gdx.audio.newSound(Gdx.files.internal("sounds/error.ogg"));
        sndPowerUp = Gdx.audio.newSound(Gdx.files.internal("sounds/powerUp.ogg"));
        sndPaperBump = Gdx.audio.newSound(Gdx.files.internal("sounds/paperBump.ogg"));
        sndCoinUp = Gdx.audio.newSound(Gdx.files.internal("sounds/coinUp.ogg"));
        sndMonsterDeath = Gdx.audio.newSound(Gdx.files.internal("sounds/monsterDeath.ogg"));
        sndElevatorUse = Gdx.audio.newSound(Gdx.files.internal("sounds/elevatorUse.ogg"));
        sndPlayerDeath = Gdx.audio.newSound(Gdx.files.internal("sounds/playerDeath.ogg"));
        sndPhew = Gdx.audio.newSound(Gdx.files.internal("sounds/phew.ogg"));
        sndChestOpen = Gdx.audio.newSound(Gdx.files.internal("sounds/chestOpen.ogg"));

        musBackground[0] = Gdx.audio.newMusic(Gdx.files.internal("music/Bye-bye - qklmv.ogg"));
        musBackground[1] = Gdx.audio.newMusic(Gdx.files.internal("music/Dangerous - qklmv.ogg"));
        musBackground[2] = Gdx.audio.newMusic(Gdx.files.internal("music/Faded - qklmv.ogg"));
        musBackground[3] = Gdx.audio.newMusic(Gdx.files.internal("music/Opulence - qklmv.ogg"));

        btnAttack = new MyButton(SCR_WIDTH / 3, SCR_HEIGHT / 3, SCR_WIDTH / 20);

        paperWad = new Weapon(imgPaperWad, "Paper wad", 60, 450, 950, 1);
        cutter = new Weapon(imgCutter[0], "Cutter", 70, 500, 650, 2);
        ghostOrb = new Weapon("Ghost orb", 1250, 2500, 1);

        player = new Player(world, 14, 18, 75, 75, 6, 6, 350, paperWad, game.screenGame);
        playerLight = new PointLight(rayHandler, 512, new Color(1, 1, 1, 0.475f), playerLightDistance, player.getX(), player.getY());
        playerLight.setSoftnessLength(25);

        joystick = new OnScreenJoystick(SCR_HEIGHT / 6, SCR_HEIGHT / 12);

        //        musicNumber = random.nextInt(musBackground.length);
        //        musBackground[musicNumber].setVolume(0.75f*musicVolume);
        //        musBackground[musicNumber].play();

        menuWidth = SCR_WIDTH / 2;
        menuHeight = SCR_HEIGHT * 0.8f;
        menuButtonWidth = menuWidth * 0.75f;
        menuButtonHeight = menuHeight * 0.155f;
        vendingWidth = SCR_WIDTH / 2;
        vendingHeight = SCR_HEIGHT / 2f;

        createButtons();

        generateMap(random.nextInt(level / 3 + 1) + 6);
        generateRooms();
        generateElevators();
        vending = new Vending(world, 32, 24, rooms.get(0).getX() + rooms.get(0).getWidth() - 35, rooms.get(0).getY() + rooms.get(0).getHeight() - 35);
        vendingPointLight = new PointLight(rayHandler, 512, new Color(0.1f, 0.5f, 0.85f, 0.695f), 95, vending.getX() + vending.getWidth() / 3, vending.getY() + 20);
        vendingPointLight.setSoftnessLength(50);
        uiInput = new UiInput(camera, hudButtons);
    }

    @Override
    public void show() {
        resetWorld();
        Gdx.input.setInputProcessor(uiInput);
        for (Button button : hudButtons) button.setTimePressed(0);
        for (Button button : menuButtons) button.setTimePressed(0);
        for (Button button : vendingButtons) button.setTimePressed(0);
    }

    @Override
    public void render(float delta) {
        if (Gdx.app.getType() != Application.ApplicationType.WebGL) {
            if (Gdx.graphics.getDisplayMode().refreshRate <= 60) {
                world.step(delta, 6, 2);
            }
        }

        updateGameLogic();
        renderGame();
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
        imgJstBase.dispose();
        imgJstKnob.dispose();
        imgGameMenu.dispose();
        imgButtonMenuAtlas.dispose();
        imgVendingUi.dispose();
        imgHeal.dispose();
        imgDamageUp.dispose();
        imgSpeedUp.dispose();
        imgVendingMachine.dispose();
        imgHorWall.dispose();
        imgVerWall.dispose();
        imgHorDoorAtlas.dispose();
        imgVerDoorAtlas.dispose();
        imgElevatorAtlas.dispose();
        imgPlayerAtlas.dispose();
        imgGhostAtlas.dispose();
        imgZombieAtlas.dispose();
        imgWardenAtlas.dispose();
        imgStaticObstacleAtlas.dispose();
        imgAnimatedObstacleAtlas.dispose();
        imgChestAtlas.dispose();
        imgPaperWad.dispose();
        imgPoisonBall.dispose();
        imgRouble.dispose();
        player.dispose();
        paperWad.dispose();
        sndClick.dispose();
        sndError.dispose();
        sndCoinUp.dispose();
        sndPaperBump.dispose();
        sndMonsterDeath.dispose();
        sndElevatorUse.dispose();
        sndPlayerDeath.dispose();
        imgMinimapBackground.dispose();
        imgMinimapRoomAtlas.dispose();
        for (int i = 0; i < ghosts.size(); i++) {
            ghosts.get(i).dispose();
        }
        for (int i = 0; i < zombies.size(); i++) {
            zombies.get(i).dispose();
        }
        for (int i = 0; i < wardens.size(); i++) {
            wardens.get(i).dispose();
        }
        for (Texture texture : imgRoom) {
            texture.dispose();
        }
    }

    private void touchHandler() { // if touch.i == using touchaattack same or == null
        actJoystick = false;
        actAttack = false;

        float playerSpeed = player.getSpeed();
        float playerDiagSpeed = playerSpeed / (float) Math.sqrt(2);

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
                    if (player.isShopping()) {
                        player.setShopping(false);
                    }
                } else if (touches.get(i).x > player.getX()) {
                    if (btnAttack.hit(touches.get(i).x, touches.get(i).y)) {
                        actAttack = true;
                    }
                }
            }
        }

        if (elevatorUseTime != 0 || actMenu || actVending || deathTime != 0) {
            actJoystick = false;
            actAttack = false;

            up = false;
            down = false;
            left = false;
            right = false;
            attack = false;
            menu = false;
        }

        if (actJoystick) {
            Vector2 directionVector = joystick.getDirectionVector();
            player.getBody().setLinearVelocity(
                directionVector.x * player.getSpeed(),
                directionVector.y * player.getSpeed()
            );

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

        if (actAttack || attack) player.attack();
        if (menu) {
            sndClick.play(0.9f * soundVolume);
            if (actVending) {
                actVending = false;
                player.setShopping(false);
                uiInput.setButtons(hudButtons);
                vendingCloseTime = TimeUtils.millis();
            }
            else {
                actMenu = !actMenu;
                uiInput.setButtons(hudButtons);
                if (actMenu) uiInput.setButtons(menuButtons);
            }
        }

        if (up) {
            player.setDirection('u');
            player.getBody().setLinearVelocity(
                player.getBody().getLinearVelocity().x,
                playerSpeed
            );
        } else if (down) {
            player.setDirection('d');
            player.getBody().setLinearVelocity(
                player.getBody().getLinearVelocity().x,
                -playerSpeed
            );
        } else if (right) {
            player.setDirection('r');
            player.getBody().setLinearVelocity(
                playerSpeed,
                player.getBody().getLinearVelocity().y
            );
        } else if (left) {
            player.setDirection('l');
            player.getBody().setLinearVelocity(
                -playerSpeed,
                player.getBody().getLinearVelocity().y
            );
        }
        if (up && right) {
            player.setDirection('r');
            player.getBody().setLinearVelocity(
                playerDiagSpeed,
                playerDiagSpeed
            );
        } else if (up && left) {
            player.setDirection('l');
            player.getBody().setLinearVelocity(
                -playerDiagSpeed,
                playerDiagSpeed
            );
        } else if (down && right) {
            player.setDirection('r');
            player.getBody().setLinearVelocity(
                playerDiagSpeed,
                -playerDiagSpeed
            );
        } else if (down && left) {
            player.setDirection('l');
            player.getBody().setLinearVelocity(
                -playerDiagSpeed,
                -playerDiagSpeed
            );
        }
    }

    public void resetProgress() {
        deathTime = 0;
        wallet = 0;
        level = 0;
        player.setSpeedUp(0);
        player.setDamageUp(0);
        player.setHealth(player.getMaxHealth());
        healCost = 5;
        damageUpCost = 10;
        speedUpCost = 8;
        musBackground[musicNumber].stop();
    }

    public void resetWorld() {
        //        world = new World(new Vector2(0, 0), true);
        //        world.setContactListener(contactListener);
        Array<Body> bodies = new Array<>();
        world.getBodies(bodies);
        for (int i = 0; i < bodies.size; i++) {
            if (!world.isLocked())
                world.destroyBody(bodies.get(i));
        }

        rooms.clear();
        elevators.clear();
        ghosts.clear();
        zombies.clear();
        wardens.clear();
        coins.clear();
        obstacles.clear();
        animatedObstacles.clear();
        chests.clear();

        actJoystick = false;
        actAttack = false;
        actMenu = false;
        isPlayerDeathSoundOn = false;
        tempBattleMode = false;
        isMusicOn = false;
        elevatorUseTime = 0;
        vendingCloseTime = 0;
        menuX = menuY = 0;
        vendingX = vendingY = 0;
        rayHandler.setAmbientLight(ambientLight - 0.025f * level);

        int tempDamageUp = player.getDamageUp();
        int tempSpeedUp = player.getSpeedUp();
        int tempHealth = player.getHealth();
        Weapon tWeapon = player.getWeapon();
        player = new Player(world, 14, 18, 75, 75, 6, 6, 350, tWeapon, game.screenGame);
        player.setDamageUp(tempDamageUp);
        player.setSpeedUp(tempSpeedUp);
        player.setHealth(tempHealth);

        musBackground[musicNumber].stop();
        musicNumber = random.nextInt(musBackground.length);
        musBackground[musicNumber].setVolume(0.65f * musicVolume);
        musBackground[musicNumber].setLooping(true);
        musBackground[musicNumber].play();

        if (level > imgRoom.length) {
            // Game end screen will be here some sunny day
            level = 0;
        }
        generateMap(random.nextInt(level / 3 + 1) + 6);
        generateRooms();
        generateElevators();
        vending = new Vending(world, 32, 24, rooms.get(0).getX() + rooms.get(0).getWidth() - 35, rooms.get(0).getY() + rooms.get(0).getHeight() - 35);
    }


    private void batchPlayer() {
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

    private void batchGhosts() {
        if (!ghosts.isEmpty()) {
            for (int i = 0; i < ghosts.size(); i++) {
                Ghost ghost = ghosts.get(i);
                int phase = ghost.getPhase();
                float x = ghost.getX() - imgGhost[0].getRegionWidth() / 2f; // centralized image x
                float y = ghost.getY() - imgGhost[0].getRegionHeight() / 2.25f; // centralized image y
                batch.draw(imgGhost[phase], x, y);
            }
        }
    }

    private void batchWardens() {
        if (!wardens.isEmpty()) {
            for (int i = 0; i < wardens.size(); i++) {
                Warden warden = wardens.get(i);
                int phase = warden.getPhase();
                float x = warden.getX() - imgWarden[0].getRegionWidth() / 2f; // centralized image x
                float y = warden.getY() - imgWarden[0].getRegionHeight() / 2.25f; // centralized image y
                batch.draw(imgWarden[phase], x, y);
            }
        }
    }

    private void batchZombies() {
        if (!zombies.isEmpty()) {
            for (int i = 0; i < zombies.size(); i++) {
                Zombie zombie = zombies.get(i);
                int phase = zombie.getPhase();
                float x = zombie.getX() - imgZombie[0][0].getRegionWidth() / 2f; // centralized image x
                float y = zombie.getY() - imgZombie[0][0].getRegionHeight() / 2.25f; // centralized image y
                switch (zombie.getDirection()) {
                    case 'r': {
                        batch.draw(imgZombie[2][phase], x, y);
                    }
                    break;
                    case 'u': {
                        batch.draw(imgZombie[1][phase], x, y);
                    }
                    break;
                    case 'l': {
                        batch.draw(imgZombie[3][phase], x, y);
                    }
                    break;
                    default: {
                        batch.draw(imgZombie[0][phase], x, y);
                    }
                }
            }
        }
    }

    private void batchObstacles() {
        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle obstacle = obstacles.get(i);
            batch.draw(imgStaticObstacle[obstacle.getImgNumber()], obstacle.getX(), obstacle.getY(), obstacle.getWidth() * 1.115f, obstacle.getHeight() * 1.75f);
        }
        for (int i = 0; i < animatedObstacles.size(); i++) {
            AnimatedObstacle animatedObstacle = animatedObstacles.get(i);
            batch.draw(imgAnimatedObstacle[animatedObstacle.getImgNumber()][animatedObstacle.getPhase()], animatedObstacle.getX(), animatedObstacle.getY(), animatedObstacle.getWidth() * 1.115f, animatedObstacle.getHeight() * 1.75f);
        }
    }

    private void batchChest() {
        for (int i = 0; i < chests.size(); i++) {
            Chest chest = chests.get(i);
            batch.draw(imgChest[chest.getPhase()], chest.getX(), chest.getY(), chest.getWidth() * 1.115f, chest.getHeight() * 1.75f);
        }
    }

    private void batchVending() {
        batch.draw(imgVendingMachine, vending.getX(), vending.getY(), 24, 36);
    }

    private void changePhaseGhosts() {
        for (int i = 0; i < ghosts.size(); i++) {
            ghosts.get(i).changePhase();
        }
    }

    private void changePhaseZombies() {
        for (int i = 0; i < zombies.size(); i++) {
            zombies.get(i).changePhase();
        }
    }

    private void changePhaseWardens() {
        for (int i = 0; i < wardens.size(); i++) {
            wardens.get(i).changePhase();
        }
    }

    private void changePhaseAnimatedObstacles() {
        for (int i = 0; i < animatedObstacles.size(); i++) {
            animatedObstacles.get(i).changePhase();
        }
    }

    private void updateProjectilesWardens() {
        for (int i = 0; i < wardens.size(); i++) {
            wardens.get(i).updateProjectiles();
        }
    }


    private void batchWall() {
        int lvl = level;
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (level >= imgRoom.length) lvl = level % imgRoom.length;
            batch.draw(imgRoom[lvl], room.getX(), room.getY(), room.getWidth(), room.getHeight());
        }
    }

    private void batchDoorPost() {
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

    private void batchDoorPre() {
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

    private void batchElevators() {
        for (int i = 0; i < elevators.size(); i++) {
            Elevator elevator = elevators.get(i);
            float scale = 2;
            float originX = elevator.getWidth() * scale / 2;
            float originY = elevator.getHeight() * scale / 2;
            if (elevator.getBody().getUserData() == "elevatorOn") {
                batch.draw(imgElevator[0],
                    elevator.getX() - originX,
                    elevator.getY() - originY,
                    originX,
                    originY,
                    elevator.getWidth() * scale,
                    elevator.getHeight() * scale,
                    1,
                    1,
                    elevator.getRotation());
            } else {
                batch.draw(imgElevator[1],
                    elevator.getX() - originX,
                    elevator.getY() - originY,
                    originX,
                    originY,
                    elevator.getWidth() * scale,
                    elevator.getHeight() * scale,
                    1,
                    1,
                    elevator.getRotation());
            }
        }
    }

    private void batchProjectile() {
        for (int i = 0; i < player.getProjectiles().size(); i++) {
            Projectile projectile = player.getProjectiles().get(i);
            if (projectile.getBody().isActive()) {

                if (player.getWeapon().getName().equals("Cutter")) {
                    float scale = 6;
                    float originX = projectile.getRadius() * scale / 2;
                    float originY = projectile.getRadius() * scale / 2;
                    batch.draw(imgCutter[0],
                        projectile.getX() - originX,
                        projectile.getY() - originY,
                        originX,
                        originY,
                        projectile.getRadius() * scale,
                        projectile.getRadius() * scale,
                        1,
                        1,
                        projectile.getRotation());
                } else {
                    batch.draw(imgPaperWad, projectile.getX() - projectile.getRadius() * 2, projectile.getY() - projectile.getRadius() * 2);
                }
            }
        }

        for (int i = 0; i < wardens.size(); i++) {
            Warden warden = wardens.get(i);
            for (int j = 0; j < warden.getProjectiles().size(); j++) {
                Projectile projectile = warden.getProjectiles().get(j);
                batch.draw(imgPoisonBall, projectile.getX() - projectile.getRadius() * 2, projectile.getY() - projectile.getRadius() * 2, 8, 8);
            }

        }
    }

    //    private void batchMeleeRegion() {
    //        MeleeRegion meleeRegion = player.getMeleeRegion();
    //        if (meleeRegion!=null) {
    //            if (meleeRegion.getBody().getUserData() == "meleeRegionTrue") {
    //                batch.draw(imgCutter, meleeRegion.getX(), meleeRegion.getY(), 8, 8); // заменить на текстуру с ударом
    //            }
    //            else{
    //                batch.draw(imgCutter, meleeRegion.getX(), meleeRegion.getY(), 8, 8);
    //            }
    //        }
    //    }

    private void batchCoin() {
        for (int i = 0; i < coins.size(); i++) {
            if (coins.get(i).getBody().isActive()) {
                Coin coin = coins.get(i);
                batch.draw(imgRouble, coin.getX() - coin.getRadius(), coin.getY() - coin.getRadius(), coin.getRadius() * 2, coin.getRadius() * 2);
            }
        }
    }

    private void batchHud() {
        float roomSize = 8;
        float mapSize = imgMinimapBackground.getWidth() * (3 / 4f);
        float mapX, mapY;
        //        float shiftX, shiftY;
        //        shiftX = shiftY = 0;
        mapX = position.x + SCR_WIDTH / 2 - mapSize - 8;
        mapY = position.y + SCR_HEIGHT / 2 - mapSize - (imgButtonMenu[0].getRegionHeight() + 8);

        if (player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());
        if (player.getHealth() < 0) player.setHealth(0);

        batch.draw(imgHealth[player.getHealth()], position.x - SCR_WIDTH / 2 + 2, position.y + SCR_HEIGHT / 2.5f - 4);
        if (wallet == 0)
            batch.draw(imgWallet[0], position.x - SCR_WIDTH / 2 + 2, position.y + SCR_HEIGHT / 2.5f - 22);
        else if (wallet < 5)
            batch.draw(imgWallet[1], position.x - SCR_WIDTH / 2 + 2, position.y + SCR_HEIGHT / 2.5f - 22);
        else if (wallet < 10)
            batch.draw(imgWallet[2], position.x - SCR_WIDTH / 2 + 2, position.y + SCR_HEIGHT / 2.5f - 22);
        else
            batch.draw(imgWallet[3], position.x - SCR_WIDTH / 2 + 2, position.y + SCR_HEIGHT / 2.5f - 22);

        //        batch.draw(imgMinimapBackground, mapX, mapY, mapSize, mapSize);
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);

            float roomX = mapX + mapSize / 2;
            float roomY = mapY + mapSize / 2;

            roomX += ((int) room.getX() / room.getWidth()) * roomSize - roomSize / 2f;
            roomY += ((int) room.getY() / room.getHeight()) * roomSize - roomSize / 2f;

            //            if (roomX<0) shiftX+=roomSize/2f;
            //            else if (roomX>0) shiftX-=roomSize/2f;
            //            if (roomY<0) shiftY+=roomSize/2f;
            //            else if (roomY>0) shiftY-=roomSize/2f;
            if (room.isActive()) {
                if (i == 0) batch.draw(imgMinimapRoom[1][1], roomX, roomY, roomSize, roomSize);
                else if (i == rooms.size() - 1)
                    batch.draw(imgMinimapRoom[1][2], roomX, roomY, roomSize, roomSize);
                else batch.draw(imgMinimapRoom[1][0], roomX, roomY, roomSize, roomSize);
            } else {
                if (i == 0) batch.draw(imgMinimapRoom[0][1], roomX, roomY, roomSize, roomSize);
                else if (i == rooms.size() - 1)
                    batch.draw(imgMinimapRoom[0][2], roomX, roomY, roomSize, roomSize);
                else batch.draw(imgMinimapRoom[0][0], roomX, roomY, roomSize, roomSize);
            }
        }
        btnMenu.draw(batch);

        joystick.render(batch, imgJstBase, imgJstKnob, position.x - SCR_WIDTH / 2.75f, position.y - SCR_HEIGHT / 4);
        batch.draw(imgJstBase, btnAttack.x, btnAttack.y, btnAttack.width, btnAttack.height);
        if (player.getWeapon().getTexture() == null) {
            batch.draw(imgCutter[0], btnAttack.x + player.getWeapon().getTextureRegion().getRegionWidth() / 2.5f, btnAttack.y + player.getWeapon().getTextureRegion().getRegionHeight() / 2.5f, btnAttack.width / 2, btnAttack.height / 2);
        } else {
            batch.draw(player.getWeapon().getTexture(), btnAttack.x + player.getWeapon().getTexture().getWidth() / 1.5f, btnAttack.y + player.getWeapon().getTexture().getHeight() / 1.5f, btnAttack.width / 2, btnAttack.height / 2);
        }
    }

    private void batchVendingUi() {
        if (actVending) {
            batch.draw(imgBlank[1], position.x - SCR_WIDTH, position.y - SCR_HEIGHT, Gdx.graphics.getWidth() * 2, Gdx.graphics.getHeight() * 2);
            batch.draw(imgVendingUi, vendingX, vendingY, vendingWidth, vendingHeight);

            btnVendingClose.draw(batch);

            btnVendingBuyHeal.draw(batch);
            batch.draw(imgHeal, btnVendingBuyHeal.getX(), btnVendingBuyHeal.getY(), btnVendingBuyHeal.getWidth(), btnVendingBuyHeal.getHeight());
            font.draw(batch, healCost + "", btnVendingBuyHeal.getX() + 4, btnVendingBuyHeal.getY() + 37);

            btnVendingBuyDamageUp.draw(batch);
            batch.draw(imgDamageUp, btnVendingBuyDamageUp.getX(), btnVendingBuyDamageUp.getY(), btnVendingBuyDamageUp.getWidth(), btnVendingBuyDamageUp.getHeight());
            font.draw(batch, damageUpCost + "", btnVendingBuyDamageUp.getX() + 4, btnVendingBuyDamageUp.getY() + 37);

            btnVendingBuySpeedUp.draw(batch);
            batch.draw(imgSpeedUp, btnVendingBuySpeedUp.getX(), btnVendingBuySpeedUp.getY(), btnVendingBuySpeedUp.getWidth(), btnVendingBuySpeedUp.getHeight());
            font.draw(batch, speedUpCost + "", btnVendingBuySpeedUp.getX() + 4, btnVendingBuySpeedUp.getY() + 37);

            font.draw(batch, wallet + "", btnVendingBuyDamageUp.getX() + 4, btnVendingBuyDamageUp.getY() - 5.5f);
        }
    }


    private void batchElevatorBlank() {
        if (elevatorUseTime != 0) {
            if (elevatorUseTime < TimeUtils.millis() - 2500) {
                batch.draw(imgBlank[2], position.x - SCR_WIDTH, position.y - SCR_HEIGHT, Gdx.graphics.getWidth() * 2, Gdx.graphics.getHeight() * 2);
            } else if (elevatorUseTime < TimeUtils.millis() - 1250) {
                batch.draw(imgBlank[1], position.x - SCR_WIDTH, position.y - SCR_HEIGHT, Gdx.graphics.getWidth() * 2, Gdx.graphics.getHeight() * 2);
            } else if (elevatorUseTime < TimeUtils.millis() - 500) {
                batch.draw(imgBlank[0], position.x - SCR_WIDTH, position.y - SCR_HEIGHT, Gdx.graphics.getWidth() * 2, Gdx.graphics.getHeight() * 2);
            }
        }
    }

    private void batchGameMenu() {
        if (actMenu) {
            batch.draw(imgBlank[1], position.x - SCR_WIDTH, position.y - SCR_HEIGHT, Gdx.graphics.getWidth() * 2, Gdx.graphics.getHeight() * 2);
            batch.draw(imgGameMenu, menuX, menuY, menuWidth, menuHeight);
            btnMenu.draw(batch);
            btnMenuClose.draw(batch);
            btnMenuDesktop.draw(batch);
            btnMenuSettings.draw(batch);
            btnMenuResume.draw(batch);
        }
    }

    private void batchDeathScreen() {
        if (player.getHealth() > 0 && !actMenu) {
            world.step(1 / 60f, 6, 2);
        } else if (player.getHealth() <= 0) {
            batch.draw(imgBlank[1], position.x - SCR_WIDTH, position.y - SCR_HEIGHT, Gdx.graphics.getWidth() * 2, Gdx.graphics.getHeight() * 2);
            musBackground[musicNumber].stop();
            glyphLayout.setText(fontUi, "I'm not ready to die yet");
            fontUi.draw(batch, "I'm not ready to die yet", position.x - glyphLayout.width / 2, position.y);
            if (deathTime == 0) {
                deathTime = TimeUtils.millis();
            } else if (deathTime + 1000 < TimeUtils.millis() && !isPlayerDeathSoundOn) {
                sndPlayerDeath.play(0.85f * soundVolume);
                isPlayerDeathSoundOn = true;
            } else if (deathTime + 4000 < TimeUtils.millis()) {
                resetProgress();
                musBackground[musicNumber].stop();
                game.setScreen(game.screenMenu);
            }
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
    //                    scheduleBodyDestroy(player.getProjectiles().get(j).getBody());
    //                }
    //            }
    //        }
    //        }
    //    }

    private void updateGhosts() {
        if (ghosts != null) {
            for (int i = 0; i < ghosts.size(); i++) {
                Ghost ghost = ghosts.get(i);
                if (ghost.isBattle) ghost.attack(player.getPosition());
                if (ghost.isAlive()) {
                    if (!player.getProjectiles().isEmpty()) {
                        if (ghost.getBody().getUserData() == "hit") {
                            ghosts.get(i).hit(player.getWeapon().getDamage() + player.getDamageUp());
                            player.getProjectiles().get(player.getProjectiles().size() - 1).getBody().setActive(false);
                            scheduleBodyDestroy(player.getProjectiles().get(player.getProjectiles().size() - 1).getBody());
                            player.getProjectiles().remove(player.getProjectiles().size() - 1);
                            ghost.getBody().setUserData("ghost");
                            sndPaperBump.play(0.65f * soundVolume);
                        }
                    }
                }
                if (!ghost.isAlive()) {
                    ghost.getBody().setActive(false);
                    scheduleBodyDestroy(ghost.getBody());
                    ghosts.remove(i);
                    sndMonsterDeath.play(0.15f * soundVolume);
                    for (int j = 0; j < random.nextInt(3) - 1; j++) {
                        Coin coin = new Coin(world, ghost.getX() + (random.nextInt(10) + 5) * j, ghost.getY() + (random.nextInt(10) + 5) * j, 4.5f, 1);
                        coins.add(coin);
                    }
                    break;
                }
            }
        }
    }

    private void updateZombies() {
        if (zombies != null) {
            for (int i = 0; i < zombies.size(); i++) {
                Zombie zombie = zombies.get(i);
                if (zombie.isBattle) {
                    zombie.attack(player.getPosition());
                    zombies.get(i).update();
                }
                if (zombie.isAlive()) {
                    if (!player.getProjectiles().isEmpty()) {
                        if (zombie.getBody().getUserData() == "hit") {
                            zombies.get(i).hit(player.getWeapon().getDamage() + player.getDamageUp());
                            player.getProjectiles().get(player.getProjectiles().size() - 1).getBody().setActive(false);
                            scheduleBodyDestroy(player.getProjectiles().get(player.getProjectiles().size() - 1).getBody());
                            player.getProjectiles().remove(player.getProjectiles().size() - 1);
                            zombie.getBody().setUserData("zombie");
                            sndPaperBump.play(0.65f * soundVolume);
                        }
                    }
                }
                if (!zombie.isAlive()) {
                    zombie.getBody().setActive(false);
                    scheduleBodyDestroy(zombie.getBody());
                    zombies.remove(i);
                    sndMonsterDeath.play(0.15f * soundVolume);
                    for (int j = 0; j < random.nextInt(4) - 1; j++) {
                        Coin coin = new Coin(world, zombie.getX() + (random.nextInt(10) + 5) * j, zombie.getY() + (random.nextInt(10) + 5) * j, 4.5f, 1);
                        coins.add(coin);
                    }
                    break;
                }
            }
        }
    }

    private void updateWardens() {
        if (wardens != null) {
            for (int i = 0; i < wardens.size(); i++) {
                Warden warden = wardens.get(i);
                if (warden.isBattle) {
                    warden.attack(player.getPosition());
                    //                    wardens.get(i).update();
                }
                if (warden.isAlive()) {
                    if (!player.getProjectiles().isEmpty()) {
                        if (warden.getBody().getUserData() == "hit") {
                            wardens.get(i).hit(player.getWeapon().getDamage() + player.getDamageUp());
                            player.getProjectiles().get(player.getProjectiles().size() - 1).getBody().setActive(false);
                            scheduleBodyDestroy(player.getProjectiles().get(player.getProjectiles().size() - 1).getBody());
                            player.getProjectiles().remove(player.getProjectiles().size() - 1);
                            warden.getBody().setUserData("warden");
                            sndPaperBump.play(0.65f * soundVolume);
                        }
                    }
                }
                if (!warden.isAlive()) {
                    warden.getBody().setActive(false);
                    scheduleBodyDestroy(warden.getBody());
                    wardens.remove(i);
                    sndMonsterDeath.play(0.15f * soundVolume);
                    for (int j = 0; j < random.nextInt(4) - 1; j++) {
                        Coin coin = new Coin(world, warden.getX() + (random.nextInt(10) + 5) * j, warden.getY() + (random.nextInt(10) + 5) * j, 4.5f, 1);
                        coins.add(coin);
                    }
                    break;
                }
            }
        }
    }

    private void updateDoors() {
        Room room = rooms.get(getPlayerRoom());
        tempBattleMode = player.isBattle;

        int ghostNum = 0;
        for (int i = 0; i < ghosts.size(); i++) {
            if (ghosts.get(i).getRoom() == getPlayerRoom()) {
                ghostNum++;
                ghosts.get(i).setBattle(true);
            }
        }

        int zombieNum = 0;
        for (int i = 0; i < zombies.size(); i++) {
            if (zombies.get(i).getRoom() == getPlayerRoom()) {
                zombieNum++;
                zombies.get(i).setBattle(true);
            }
        }

        int wardenNum = 0;
        for (int i = 0; i < wardens.size(); i++) {
            if (wardens.get(i).getRoom() == getPlayerRoom()) {
                wardenNum++;
                wardens.get(i).setBattle(true);
            }
        }

        for (int i = 1; i < room.getDoorVerBodies().size(); i += 3) {
            if (ghostNum + zombieNum + wardenNum > 0) {
                room.getDoorVerBodies().get(i).setUserData("closeDoor");
                player.setBattleState(true);
            } else {
                room.getDoorVerBodies().get(i).setUserData("openDoor");
                player.setBattleState(false);
            }
        }
        for (int i = 1; i < room.getDoorHorBodies().size(); i += 3) {
            if (ghostNum + zombieNum + wardenNum > 0) {
                room.getDoorHorBodies().get(i).setUserData("closeDoor");
                player.setBattleState(true);
            } else {
                room.getDoorHorBodies().get(i).setUserData("openDoor");
                player.setBattleState(false);
            }
        }

        if (player.isBattle != tempBattleMode) {
            if (player.isBattle) {
                musBackground[musicNumber].setVolume(0.45f * musicVolume);
            } else {
                musBackground[musicNumber].setVolume(0.35f * musicVolume);
            }
        }
    }

    private void updateCoins() {
        for (int i = 0; i < coins.size(); i++) {
            if (coins.get(i) != null) {
                Coin coin = coins.get(i);
                if (coin.getBody().getUserData() == "got") {
                    scheduleBodyDestroy(coin.getBody());
                    wallet += coin.getValue();
                    sndCoinUp.play(0.65f * soundVolume);
                    coins.remove(i);
                    break;
                }
            }
        }
    }

    private void updateChests() {
        if (!player.isBattle) {
            int lootNum;
            for (int i = 0; i < chests.size(); i++) {
                Chest chest = chests.get(i);
                chest.updateChest(player.isBattle);
                if (chest.dropLoot()) {
                    lootNum = random.nextInt(3);
                    sndChestOpen.play(0.55f * soundVolume);
                    switch (lootNum) {
                        case 0: {
                            for (int j = 0; j < random.nextInt(3) + 1; j++) {
                                Coin coin = new Coin(world, chest.getX() + (random.nextInt(10) - 5), chest.getY() + (random.nextInt(10) - 5), 4.5f, 1);
                                coins.add(coin);
                            }
                            break;
                        }
                        case 1: {
                            if (player.getWeapon() == cutter) {
                                for (int j = 0; j < random.nextInt(3) + 1; j++) {
                                    Coin coin = new Coin(world, chest.getX() + (random.nextInt(10) - 5), chest.getY() + (random.nextInt(10) - 5), 4.5f, 1);
                                    coins.add(coin);
                                }
                            } else {
                                player.setWeapon(cutter);
                            }
                            break;
                        }
                        default: {
                            sndPhew.play(0.75f * soundVolume);
                            break;
                        }
                    }
                    chest.getBody().setActive(false);
                    scheduleBodyDestroy(chest.getBody());
                    chests.remove(i);
                    break;
                }
            }
        }
    }

    public void updateLevel() {
        if (player.isMoved()) {
            if (elevatorUseTime == 0) {
                elevatorUseTime = TimeUtils.millis();
                musBackground[musicNumber].setVolume(0.35f * musicVolume);
                sndElevatorUse.play(0.9f * soundVolume);
            }
            if (elevatorUseTime < TimeUtils.millis() - 2750) {
                resetWorld();
                level++;
                if (level == imgRoom.length) {
                    game.setScreen(game.screenEnding);
                }
            }
        }
    }

    public void updateVending() {
        if (player.isShopping() && !actVending && vendingCloseTime + 1250 < TimeUtils.millis()) {
            actVending = true;
            uiInput.setButtons(vendingButtons);
            player.setShopping(false);
            vendingCloseTime = TimeUtils.millis();
        } else if (player.isShopping() && !actVending) player.setShopping(false);
    }

    //    public void updateMeleeRegion() {
    //        if (player.getMeleeRegion()!=null) {
    //            if (player.getWeapon().getName().equals("Cutter")) {
    //                player.getMeleeRegion().update(player.direction, player.getX(), player.getY());
    //            }
    //
    //            for (int i = 0; i < ghosts.size(); i++) {
    //                Ghost ghost = ghosts.get(i);
    //                MeleeRegion meleeRegion = player.getMeleeRegion();
    //
    //                if (ghost.getRoom()==getPlayerRoom()) {
    //                    if (meleeRegion.getX()>ghost.getX() && meleeRegion.getX()<ghost.getX()+ghost.getWidth()/2 && meleeRegion.getY()>ghost.getY() && meleeRegion.getY()<ghost.getY()+ghost.getHeight()/2) {
    //                        ghost.getBody().setUserData("hit");
    //                        System.out.println("HITTED BY CUTTER");
    //                    }
    //                }
    //            }
    //        }
    //    }

    private void generateMap(int maxRooms) {
        float roomX = 0, roomY = 0;
        float roomWidth = 200, roomHeight = 200;
        char roomType;
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

            Room room = new Room(world, roomX, roomY, roomWidth, roomHeight, doors, rooms, roomType);
            setDoors();
            rooms.add(room);
            room.setDoors(doors);
            doors.clear();
        }

        Room lastRoom = rooms.get(rooms.size() - 1);
        Room preLastRoom = rooms.get(rooms.size() - 2);

        if (lastRoom.getX() == preLastRoom.getX() && lastRoom.getY() + lastRoom.getHeight() == preLastRoom.getY()) {
            if (lastRoom.getY() + roomHeight == preLastRoom.getY()) {
                lastRoom.addDoor('u');
                lastRoom.removeTopWall();
                preLastRoom.addDoor('d');
                preLastRoom.removeBottomWall();
            }
        } else if (lastRoom.getX() == preLastRoom.getX() && lastRoom.getY() - lastRoom.getHeight() == preLastRoom.getY()) {
            if (lastRoom.getY() - roomHeight == preLastRoom.getY()) {
                lastRoom.addDoor('d');
                lastRoom.removeBottomWall();
                preLastRoom.addDoor('u');
                preLastRoom.removeTopWall();
            }
        } else if (lastRoom.getY() == preLastRoom.getY() && lastRoom.getX() - lastRoom.getWidth() == preLastRoom.getX()) {
            if (lastRoom.getX() - roomWidth == preLastRoom.getX()) {
                lastRoom.addDoor('l');
                lastRoom.removeLeftWall();
                preLastRoom.addDoor('r');
                preLastRoom.removeRightWall();
            }
        } else if (lastRoom.getY() == preLastRoom.getY() && lastRoom.getX() + lastRoom.getWidth() == preLastRoom.getX()) {
            if (lastRoom.getX() + roomWidth == preLastRoom.getX()) {
                lastRoom.addDoor('r');
                lastRoom.removeRightWall();
                preLastRoom.addDoor('l');
                preLastRoom.removeLeftWall();
            }
        }
    }


    private ArrayList<Character> generateDir(float x, float y, float width, float height) {
        ArrayList<Character> freeCell = new ArrayList<>();
        float testX, testY;

        for (int i = 0; i < rooms.size(); i++) {
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
        float roomX, roomY;

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
                    float spawnX;
                    float spawnY;
                    int size;
                    for (int j = 0; j < 3 - MathUtils.random(1) - level / 4; j++) {
                        size = MathUtils.random(10) - 5;
                        spawnX = MathUtils.random(room.getX() + THICKNESS * 3, room.getX() + room.getWidth() - THICKNESS * 3);
                        spawnY = MathUtils.random(room.getY() + THICKNESS * 3, room.getY() + room.getHeight() - THICKNESS * 3);

                        Ghost ghost = new Ghost(world, 14 + size, 17 + size, spawnX, spawnY, 3, 4, 250, ghostOrb);
                        ghost.setRoomNum(i);
                        ghosts.add(ghost);
                    }

                    if (level >= 2) {
                        for (int j = 0; j < random.nextInt(2) + 1; j++) {
                            size = MathUtils.random(10) - 5;
                            spawnX = MathUtils.random(room.getX() + THICKNESS * 3, room.getX() + room.getWidth() - THICKNESS * 3);
                            spawnY = MathUtils.random(room.getY() + THICKNESS * 3, room.getY() + room.getHeight() - THICKNESS * 3);

                            Zombie zombie = new Zombie(world, 9 + size, 15 + size, spawnX, spawnY, 6, 10, 175, ghostOrb);
                            zombie.setRoomNum(i);
                            zombies.add(zombie);
                        }
                    }

                    if (level >= 4) {
                        for (int j = 0; j < random.nextInt(2); j++) {
                            size = MathUtils.random(10) - 5;
                            spawnX = MathUtils.random(room.getX() + THICKNESS * 3, room.getX() + room.getWidth() - THICKNESS * 3);
                            spawnY = MathUtils.random(room.getY() + THICKNESS * 3, room.getY() + room.getHeight() - THICKNESS * 3);

                            Warden warden = new Warden(world, 12 + size, 13.5f + size, spawnX, spawnY, 8, 8, 175, paperWad, game.screenGame);
                            warden.setRoomNum(i);
                            wardens.add(warden);
                        }
                    }

                    for (int j = 0; j < MathUtils.random(2) + 1; j++) {
                        spawnX = MathUtils.random(room.getX() + THICKNESS * 3, room.getX() + room.getWidth() - THICKNESS * 3);
                        spawnY = MathUtils.random(room.getY() + THICKNESS * 3, room.getY() + room.getHeight() - THICKNESS * 3);
                        size = MathUtils.random(5) + 10;

                        if (MathUtils.random(5) == 0) {
                            AnimatedObstacle animatedObstacle = new AnimatedObstacle(world, size, size, spawnX, spawnY, 2, 125);
                            animatedObstacle.setRoomNum(i);
                            animatedObstacles.add(animatedObstacle);
                            animatedObstacle.setImgNumber(random.nextInt(imgAnimatedObstacle.length));
                        } else {
                            Obstacle obstacle = new Obstacle(world, size, size, spawnX, spawnY);
                            obstacle.setRoomNum(i);
                            obstacles.add(obstacle);
                            obstacle.setImgNumber(random.nextInt(imgStaticObstacle.length));
                        }
                    }

                    if (random.nextInt(6) == 1) {
                        spawnX = MathUtils.random(room.getX() + THICKNESS * 3, room.getX() + room.getWidth() - THICKNESS * 3);
                        spawnY = MathUtils.random(room.getY() + THICKNESS * 3, room.getY() + room.getHeight() - THICKNESS * 3);
                        size = MathUtils.random(5) + 10;

                        Chest chest = new Chest(world, size, size, spawnX, spawnY, 6, 250);
                        chest.setRoomNum(i);
                        chests.add(chest);
                        //                        animatedObstacle.setImgNumber(random.nextInt(imgObstacle.length));
                    }
            }
        }
    }

    private void generateElevators() {
        Elevator elevatorLast = new Elevator(world, rooms.get(rooms.size() - 1).getX(), rooms.get(rooms.size() - 1).getY(), rooms.get(rooms.size() - 1).getWidth(), rooms.get(rooms.size() - 1).getHeight(), rooms.get(rooms.size() - 1).getDoors(), true);
        elevators.add(elevatorLast);
        if (level != 0) {
            Room room = rooms.get(0);
            Elevator elevator = new Elevator(world, room.getX(), room.getY(), room.getWidth(), room.getHeight(), room.getDoors(), false);
            elevators.add(elevator); // to one str
        }
    }

    public int getPlayerRoom() {
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (player.getX() > room.getX() + THICKNESS && player.getX() < room.getX() + room.getWidth() - THICKNESS && player.getY() > room.getY() + THICKNESS && player.getY() < room.getY() + room.getHeight() - THICKNESS) {
                room.setDiscovered(true);
                room.setActive(true);
                return i;
            } else room.setActive(false);
        }
        return 0;
    }

    private void createButtons() {
        btnMenu = new RectangleButton(0, 0, 14, 14, imgButtonMenu, false,
            () -> sndClick.play(0.9f * soundVolume),
            () -> {
                actMenu = !actMenu;
                uiInput.setButtons(hudButtons);
                if (actMenu) uiInput.setButtons(menuButtons);
            });
        hudButtons.add(btnMenu);
        menuButtons.add(btnMenu);

        btnMenuClose = new RectangleButton(0, 0, 10, 10, imgButtonClose, false, () -> {
            sndClick.play(0.9f * soundVolume);
        }, () -> {
            actMenu = false;
            uiInput.setButtons(hudButtons);
        });
        menuButtons.add(btnMenuClose);

        btnMenuDesktop = new TextButton(0, 0, menuButtonWidth, menuButtonHeight, "To Desktop", fontButton, imgButtonLong, false, () -> {
            sndClick.play(0.9f * soundVolume);
        }, () -> {
            actMenu = false;
            uiInput.setButtons(hudButtons);
            musBackground[musicNumber].stop();
            game.setScreen(game.screenMenu);
        });
        menuButtons.add(btnMenuDesktop);

        btnMenuSettings = new TextButton(0, 0, menuButtonWidth, menuButtonHeight, "Settings", fontButton, imgButtonLong, false, () -> {
            sndClick.play(0.9f * soundVolume);
        }, () -> {
            sndError.play(0.85f * soundVolume);
        });
        menuButtons.add(btnMenuSettings);

        btnMenuResume = new TextButton(0, 0, menuButtonWidth, menuButtonHeight, "Resume", fontButton, imgButtonLong, false, () -> {
            sndClick.play(0.9f * soundVolume);
        }, () -> {
            actMenu = false;
            uiInput.setButtons(hudButtons);
        });
        menuButtons.add(btnMenuResume);

        btnVendingClose = new RectangleButton(0, 0, 10, 10, imgButtonClose, false, () -> {
            sndClick.play(0.9f * soundVolume);
        }, () -> {
            actVending = false;
            player.setShopping(false);
            uiInput.setButtons(hudButtons);
            vendingCloseTime = TimeUtils.millis();
        });
        vendingButtons.add(btnVendingClose);

        btnVendingBuyHeal = new RectangleButton(0, 0, 26, 26, imgButtonSquare, false, () -> {
            sndClick.play(0.9f * soundVolume);
        }, () -> {
            if (player.getHealth() < player.getMaxHealth() && wallet >= healCost) {
                wallet -= healCost;
                healCost += 1;
                player.setHealth(player.getMaxHealth());
                sndPowerUp.play(0.65f * soundVolume);
            } else {
                sndError.play(0.85f * soundVolume);
            }
        });
        vendingButtons.add(btnVendingBuyHeal);

        btnVendingBuyDamageUp = new RectangleButton(0, 0, 26, 26, imgButtonSquare, false, () -> {
            sndClick.play(0.9f * soundVolume);
        }, () -> {
            if (wallet >= damageUpCost) {
                wallet -= damageUpCost;
                damageUpCost *= 1.45f;
                player.setDamageUp(player.getDamageUp() + 1);
                sndPowerUp.play(0.65f * soundVolume);
            } else {
                sndError.play(0.85f * soundVolume);
            }


        });
        vendingButtons.add(btnVendingBuyDamageUp);

        btnVendingBuySpeedUp = new RectangleButton(0, 0, 26, 26, imgButtonSquare, false, () -> {
            sndClick.play(0.9f * soundVolume);
        }, () -> {
            if (wallet >= speedUpCost) {
                wallet -= speedUpCost;
                speedUpCost *= 1.25f;
                player.setSpeedUp(player.getSpeedUp() + 2);
                sndPowerUp.play(0.65f * soundVolume);
            } else {
                sndError.play(0.85f * soundVolume);
            }
        });
        vendingButtons.add(btnVendingBuySpeedUp);
    }

    private void updateButtons() {
        btnAttack.update(position.x + SCR_WIDTH / 3, position.y - SCR_HEIGHT / 3);

        //batch.draw(imgBlank[1], position.x - SCR_WIDTH, position.y - SCR_HEIGHT, Gdx.graphics.getWidth() * 2, Gdx.graphics.getHeight() * 2);

        btnMenu.update(position.x + SCR_WIDTH / 2 - 16f, position.y + SCR_HEIGHT / 2 - 16f);
        btnMenuClose.update(menuX + menuWidth - 12, menuY + menuHeight - 12.75f);
        btnMenuDesktop.update(menuX + (menuWidth - menuButtonWidth) / 2f, menuY + menuButtonHeight - menuButtonHeight / 2);
        btnMenuSettings.update(menuX + (menuWidth - menuButtonWidth) / 2f, menuY + menuButtonHeight * 2);
        btnMenuResume.update(menuX + (menuWidth - menuButtonWidth) / 2f, menuY + menuButtonHeight * 3 + menuButtonHeight / 2);

        btnVendingClose.update(vendingX + vendingWidth - 11, vendingY + vendingHeight - 12);
        btnVendingBuyHeal.update(vendingX + 9, vendingY + 17.5f); // some day I'll deal with that hardcode
        btnVendingBuyDamageUp.update(vendingX + 47, vendingY + 17.5f);
        btnVendingBuySpeedUp.update(vendingX + 85, vendingY + 17.5f);
    }

    public void scheduleBodyDestroy(Body body) {
        if (!bodiesToDestroy.contains(body)) bodiesToDestroy.add(body);
    }

    private void destroyScheduledBodies() {
        for (Body body : bodiesToDestroy) {
            if (body != null) world.destroyBody(body);
        }
        bodiesToDestroy.clear();
    }

    private void updateGameLogic() {
        if (!actMenu && !actVending)
            position.lerp(new Vector2(player.getX(), player.getY()), 0.1f); // 0.1f - camera smoothing coefficient
        camera.position.set(position, 0);
        camera.update();

        up = Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP);
        down = Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN);
        left = Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT);
        right = Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT);

        attack = Gdx.input.isKeyPressed(Input.Keys.SPACE) || Gdx.input.isKeyPressed(Input.Keys.ENTER);
        menu = Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.BACK);

        menuX = position.x - SCR_WIDTH / 4;
        menuY = position.y - SCR_HEIGHT / 3;

        vendingX = position.x - SCR_WIDTH / 4;
        vendingY = position.y - SCR_HEIGHT / 4;

        // touch handler

        touchHandler();

        // events

        if (deathTime == 0 && !actMenu && !actVending) {
            player.changePhase();
            changePhaseGhosts();
            changePhaseZombies();
            changePhaseWardens();
            changePhaseAnimatedObstacles();
            player.step(actJoystick);
            player.updateProjectiles();
            updateProjectilesWardens();
            //            player.updateMeleeRegion();
            //            meleeRegionUpdate();
            updateGhosts();
            updateZombies();
            updateWardens();
            updateDoors();
            updateCoins();
            updateChests();
            updateLevel();
            updateVending();
            updateButtons();
        }
    }

    private void renderGame() {
        // draw
        ScreenUtils.clear(0, 0, 0, 0);
        //        debugRenderer.render(world, camera.combined);
        batch.setProjectionMatrix(camera.combined);
        rayHandler.setCombinedMatrix(camera.combined, camera.position.x, camera.position.y, camera.viewportWidth, camera.viewportHeight);
        camera.update();

        batch.begin();

        batchWall();
        batchProjectile();
        // batchMeleeRegion();
        batchDoorPre();
        batchElevators();
        batchVending();
        batchCoin();
        batchZombies();
        batchPlayer();
        batchObstacles();
        batchChest();
        batchGhosts();
        batchWardens();
        batchDoorPost();
        batchHud();
        batchVendingUi();
        batchElevatorBlank();
        batchGameMenu();
        batchDeathScreen();

        // Debug on-screen messages:
        //        fontUi.draw(batch, "Floor:" + (imgRoom.length-level), position.x - SCR_WIDTH / 2, position.y + SCR_HEIGHT / 2);
        //        fontUi.draw(batch, musBackground[musicNumber].getVolume()+"", position.x - SCR_WIDTH / 2, position.y + SCR_HEIGHT / 2);
        //        if (player.getMeleeRegion()!=null) {
        //            if (player.getMeleeRegion().getBody().getUserData().equals("meleeRegionTrue")) tPunch++;
        //            fontUi.draw(batch, tPunch+"", position.x - SCR_WIDTH / 2, position.y + SCR_HEIGHT / 2);
        //        }
        //        fontUi.draw(batch, player.getMeleeRegion().getBody().getUserData()+"", position.x - SCR_WIDTH / 2, position.y + SCR_HEIGHT / 2);
        //        fontUi.draw(batch, wardens.size() + "", position.x - SCR_WIDTH / 2, position.y + SCR_HEIGHT / 2);
        //        fontUi.draw(batch, "direction: " + joystick.getDirectionVector(), position.x - SCR_WIDTH / 2, position.y + SCR_HEIGHT / 2);
        //            fontUi.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond() + "\nDelta: " + delta + "\nSystem Delta: " + Gdx.graphics.getDeltaTime(), position.x - SCR_WIDTH / 2, position.y + SCR_HEIGHT / 2);
        //               if (TimeUtils.millis() % 1000 < 16) {
        //                Gdx.app.log("perf", "FPS=" + Gdx.graphics.getFramesPerSecond() +
        //                    " delta=" + delta +
        //                    " SystemDelta=" + Gdx.graphics.getDeltaTime());
        //            }

        batch.end();

        playerLight.attachToBody(player.getBody());
        rayHandler.updateAndRender();
        destroyScheduledBodies();
    }
}
