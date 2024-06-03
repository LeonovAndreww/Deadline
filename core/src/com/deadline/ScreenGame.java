package com.deadline;

import static com.deadline.DdlnGame.*;

import static sun.jvm.hotspot.debugger.win32.coff.DebugVC50X86RegisterEnums.TAG;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
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
    Vector2 position = new Vector2(0, 0);

    RayHandler rayHandler;
    PointLight playerLight;
    PointLight vendingPointLight;

    BitmapFont font, fontUi;
    GlyphLayout glyphLayout;

    OnScreenJoystick joystick;

    Texture imgJstBase, imgJstKnob;
    Texture imgButtonMenu;
    Texture imgGameMenu;
    Texture imgVendingUi;
    Texture imgMinimapBackground;
    Texture imgHeal, imgDamageUp, imgSpeedUp;
    Texture[] imgRoom = new Texture[9];
    Texture imgHorWall, imgVerWall;
    Texture imgPaperWad;
    Texture imgRouble;
    Texture imgVendingMachine;
    Texture imgHorDoorAtlas, imgVerDoorAtlas;
    Texture imgHealthAtlas, imgWalletAtlas;
    Texture imgPlayerAtlas;
    Texture imgGhostAtlas;
    Texture imgZombieAtlas;
    Texture imgObstacleAtlas;
    Texture imgElevatorAtlas;
    Texture imgMinimapRoomAtlas;
    Texture imgBlankAtlas;
    TextureRegion[] imgHorDoor = new TextureRegion[2];
    TextureRegion[] imgVerDoor = new TextureRegion[2];
    TextureRegion[] imgHealth = new TextureRegion[7];
    TextureRegion[] imgWallet = new TextureRegion[4];
    TextureRegion[][] imgPlayerIdle = new TextureRegion[4][6];
    TextureRegion[][] imgPlayerRun = new TextureRegion[4][6];
    TextureRegion[] imgGhost = new TextureRegion[4];
    TextureRegion[][] imgZombie = new TextureRegion[4][10];
    TextureRegion[] imgObstacle = new TextureRegion[6];
    TextureRegion[] imgElevator = new TextureRegion[2];
    TextureRegion[][] imgMinimapRoom = new TextureRegion[2][3];
    TextureRegion[] imgBlank = new TextureRegion[3];

    Sound sndClick;
    Sound sndError;
    Sound sndPowerUp;
    Sound sndPaperBump;
    Sound sndCoinUp;
    Sound sndMonsterDeath;
    Sound sndElevatorUse;
    Sound sndPlayerDeath;

    Music[] musBackground = new Music[4];

    Player player;
    Vending vending;

    Weapon paperWad;
    Weapon ghostOrb;

    ArrayList<Room> rooms = new ArrayList<>();
    ArrayList<Elevator> elevators = new ArrayList<>();
    ArrayList<Ghost> ghosts = new ArrayList<>();
    ArrayList<Zombie> zombies = new ArrayList<>();
    ArrayList<Coin> coins = new ArrayList<>();
    ArrayList<Obstacle> obstacles = new ArrayList<>();

    MyButton btnAttack;
    MyButton btnMenu, btnMenuClose, btnMenuResume, btnMenuMain;
    MyButton btnVendingClose, btnVendingBuyHeal, btnVendingBuyDamageUp, btnVendingBuySpeedUp;

    boolean actJoystick;
    boolean actAttack;
    boolean actMenu;
    boolean actVending;

    boolean isPlayerDeathSoundOn;

    long deathTime = 0;
    long elevatorUseTime = 0;
    long vendingCloseTime = 0;
    long buyHealTime = 0;
    long buyDamageUpTime = 0;
    long buySpeedUpTime = 0;

    float menuX, menuY, menuWidth, menuHeight;
    float vendingX, vendingY, vendingWidth, vendingHeight;

    public int wallet = 0;
    public int level = 0;
    public int musicNumber = 0;

    public int healCost = 5, damageUpCost = 10, speedUpCost = 8;

    String txtCord = "Empty";

    public ScreenGame(DdlnGame game) {
        this.game = game;
        batch = game.batch;
        camera = game.camera;
        touch = game.touch;
        font = game.font;
        fontUi = game.fontUi;
        glyphLayout = DdlnGame.glyphLayout;

        actJoystick = false;
        actAttack = false;
        actMenu = false;

        menuX = menuY = menuWidth = menuHeight = 0;
        vendingX = vendingY = vendingWidth = vendingHeight = 0;

        isPlayerDeathSoundOn = false;
        isMusicOn = false;

        world = new World(new Vector2(0, 0), true);
        MyContactListener contactListener = new MyContactListener();
        world.setContactListener(contactListener);
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(ambientLight-0.025f*level);
//        Vector2 position = new Vector2(0, 0);

        glyphLayout = new GlyphLayout();

        imgJstBase = new Texture("textures/joystickBase.png");
        imgJstKnob = new Texture("textures/joystickKnob.png");

        imgButtonMenu = new Texture("textures/menuButton.png");
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
        imgRoom[8] = new Texture("textures/room8.png"); // потом это в текстур-регион переделать! Ибо выглядит страшно

        imgHorWall = new Texture("textures/horizontalWall.png");
        imgVerWall = new Texture("textures/verticalWall.png");

        imgHorDoorAtlas = new Texture("textures/horizontalDoorAtlas.png");
        imgVerDoorAtlas = new Texture("textures/verticalDoorAtlas.png");
        imgHealthAtlas = new Texture("textures/healthbarAtlas.png");
        imgWalletAtlas = new Texture("textures/walletAtlas.png");
        imgPlayerAtlas = new Texture("textures/playerAtlas.png");
        imgGhostAtlas = new Texture("textures/ghostAtlas.png");
        imgZombieAtlas = new Texture("textures/zombieAtlas.png");
        imgObstacleAtlas = new Texture("textures/obstacleAtlas.png");
        imgElevatorAtlas = new Texture("textures/elevatorAtlas.png");
        imgMinimapRoomAtlas = new Texture("textures/minimapRoomAtlas.png");
        imgBlankAtlas = new Texture("textures/blankAtlas.png");

        imgPaperWad = new Texture("textures/paperWad.png");
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
            imgGhost[i] = new TextureRegion(imgGhostAtlas, i*32, 0, 32, 32);
        }

        for (int i = 0; i < imgZombie.length; i++) {
            for (int j = 0; j < imgZombie[0].length; j++) {
                imgZombie[i][j] = new TextureRegion(imgZombieAtlas, j*32, i*32, 32, 32);
            }
        }

        for (int i = 0; i < imgObstacle.length; i++) {
            imgObstacle[i] = new TextureRegion(imgObstacleAtlas, i*21, 0, 21, 32);
        }

        for (int i = 0; i < imgElevator.length; i++) {
            imgElevator[i] = new TextureRegion(imgElevatorAtlas, 0, i*12, 64, 12);
        }

        for (int i = 0; i < imgHealth.length; i++) {
            imgHealth[i] = new TextureRegion(imgHealthAtlas, 0,(imgHealth.length-1-i)*16,64, 16);
        }

        for (int i = 0; i < imgWallet.length; i++) {
            imgWallet[i] = new TextureRegion(imgWalletAtlas, 0, (imgWallet.length-1-i)*16, 16, 16);
        }

        for (int i = 0; i < imgMinimapRoom.length; i++) {
            for (int j = 0; j < imgMinimapRoom[i].length; j++) {
                imgMinimapRoom[i][j] = new TextureRegion(imgMinimapRoomAtlas, j*8, i*8, 8, 8);
            }
        }

        for (int i = 0; i < imgBlank.length; i++) {
            imgBlank[i] = new TextureRegion(imgBlankAtlas, i, 0, 1, 1);
        }

        imgHorDoor[0] = new TextureRegion(imgHorDoorAtlas, 0, 0, 64, 16);
        imgHorDoor[1] = new TextureRegion(imgHorDoorAtlas, 0, 16, 64, 16);
        imgVerDoor[0] = new TextureRegion(imgVerDoorAtlas, 0, 0, 16, 64);
        imgVerDoor[1] = new TextureRegion(imgVerDoorAtlas, 16, 0, 16, 64);

        sndClick = Gdx.audio.newSound(Gdx.files.internal("sounds/click.mp3"));
        sndError = Gdx.audio.newSound(Gdx.files.internal("sounds/error.mp3"));
        sndPowerUp = Gdx.audio.newSound(Gdx.files.internal("sounds/powerUp.mp3"));
        sndPaperBump = Gdx.audio.newSound(Gdx.files.internal("sounds/paperBump.mp3"));
        sndCoinUp = Gdx.audio.newSound(Gdx.files.internal("sounds/coinUp.mp3"));
        sndMonsterDeath = Gdx.audio.newSound(Gdx.files.internal("sounds/monsterDeath.mp3"));
        sndElevatorUse = Gdx.audio.newSound(Gdx.files.internal("sounds/elevatorUse.mp3"));
        sndPlayerDeath = Gdx.audio.newSound(Gdx.files.internal("sounds/playerDeath.mp3"));

        musBackground[0] = Gdx.audio.newMusic(Gdx.files.internal("music/Bye-bye - qklmv.mp3"));
        musBackground[1] = Gdx.audio.newMusic(Gdx.files.internal("music/Dangerous - qklmv.mp3"));
        musBackground[2] = Gdx.audio.newMusic(Gdx.files.internal("music/Faded - qklmv.mp3"));
        musBackground[3] = Gdx.audio.newMusic(Gdx.files.internal("music/Opulence - qklmv.mp3"));

        btnAttack = new MyButton(SCR_WIDTH / 3, SCR_HEIGHT / 3, SCR_WIDTH / 20);

        btnMenu = new MyButton(0, 0, 15, 15);
        btnMenuClose = new MyButton(0, 0,12, 12);
        btnMenuMain = new MyButton(0, 0,80, 22);
        btnMenuResume = new MyButton(0, 0,80, 22);

        btnVendingClose = new MyButton(0, 0, 15,15);
        btnVendingBuyHeal = new MyButton(0, 0, 26, 26);
        btnVendingBuyDamageUp = new MyButton(0, 0, 26, 26);
        btnVendingBuySpeedUp = new MyButton(0, 0, 26, 26);

        paperWad = new Weapon(imgPaperWad, "Paper wad", 35, 1250, 950, 1);
        ghostOrb = new Weapon(1250, 2500, 1);

        player = new Player(world, 14, 18, 75, 75, 6, 6, 450, paperWad);
        playerLight =  new PointLight(rayHandler, 512, new Color(1,1,1,0.475f), playerLightDistance, player.getX(), player.getY());
        playerLight.setSoftnessLength(25);

        joystick = new OnScreenJoystick(SCR_HEIGHT / 6, SCR_HEIGHT / 12);

//        musicNumber = random.nextInt(musBackground.length);
//        musBackground[musicNumber].setVolume(0.75f*musicVolume);
//        musBackground[musicNumber].play();

        generateMap(7);
        generateRooms();
        generateElevators();
        vending = new Vending(world, 32, 24, rooms.get(0).getX()+rooms.get(0).getWidth()-35, rooms.get(0).getY()+rooms.get(0).getHeight()-35);
        vendingPointLight = new PointLight(rayHandler, 512, new Color(0.1f, 0.5f, 0.85f, 0.695f), 95, vending.getX()+vending.getWidth()/3, vending.getY()+20);
        vendingPointLight.setSoftnessLength(50);
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

        menuX = position.x-SCR_WIDTH/4;
        menuY = position.y-SCR_HEIGHT/3;
        menuWidth = SCR_WIDTH/2;
        menuHeight = SCR_HEIGHT*0.8f;

        vendingX = position.x-SCR_WIDTH/4;
        vendingY = position.y-SCR_HEIGHT/4;
        vendingWidth = SCR_WIDTH/2;
        vendingHeight = SCR_HEIGHT/2f;

        // касания

        touchHandler();

        // события

        if (deathTime==0) {
            player.changePhase();
            ghostsChangePhase();
            zombiesChangePhase();
            player.updateProjectiles();
            player.hit();
            player.step(actJoystick);
            ghostsUpdate();
            zombiesUpdate();
            doorsUpdate();
            coinsUpdate();
            levelUpdate();
            vendingUpdate();

            btnAttack.update(position.x + SCR_WIDTH / 3, position.y - SCR_HEIGHT / 3);

            btnMenu.update(position.x + SCR_WIDTH/2 - 17.5f, position.y + SCR_HEIGHT/2 - 17.5f);
            btnMenuClose.update(menuX+menuWidth-12, menuY+menuHeight-12);
            btnMenuMain.update(menuX+13, menuY+52.5f);
            btnMenuResume.update(menuX+13, menuY+5);

            btnVendingClose.update(vendingX+vendingWidth-12, vendingY+vendingHeight-12);
            btnVendingBuyHeal.update(vendingX+9, vendingY+20);
            btnVendingBuyDamageUp.update(vendingX+47, vendingY+20);
            btnVendingBuySpeedUp.update(vendingX+85, vendingY+20);

        }

        // draw
        ScreenUtils.clear(0, 0, 0, 0);
        debugRenderer.render(world, camera.combined);
        batch.setProjectionMatrix(camera.combined);
        rayHandler.setCombinedMatrix(camera.combined, camera.position.x, camera.position.y, camera.viewportWidth, camera.viewportHeight);
        camera.update();

        batch.begin();

        wallBatch();
        projectileBatch();
        doorPreBatch();
        elevatorsBatch();
        vendingBatch();
        coinBatch();
        zombiesBatch();
        playerBatch();
        obstaclesBatch();
        ghostsBatch();
        doorPostBatch();
        hudBatch();
        vendingUiBatch();
        elevatorBlankBatch();
        gameMenuBatch();

//        font.draw(batch, level+"", position.x - SCR_WIDTH / 2, position.y + SCR_HEIGHT / 2);

        joystick.render(batch, imgJstBase, imgJstKnob, position.x - SCR_WIDTH / 2.75f, position.y - SCR_HEIGHT / 4);
        batch.draw(imgJstBase, btnAttack.x, btnAttack.y, btnAttack.width, btnAttack.height);
        batch.draw(player.getWeapon().getTexture(), btnAttack.x+player.getWeapon().getTexture().getWidth()/1.5f, btnAttack.y+player.getWeapon().getTexture().getHeight()/1.5f, btnAttack.width/2, btnAttack.height/2);
        batch.draw(imgButtonMenu, btnMenu.x, btnMenu.y, btnMenu.width, btnMenu.height);

        if (player.getHealth() > 0 && !actMenu) {
            world.step(1 / 60f, 6, 2);
        } else if (player.getHealth() <= 0){
            batch.draw(imgBlank[1], position.x-SCR_WIDTH, position.y-SCR_HEIGHT, Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2);
            glyphLayout.setText(fontUi, "I'm not ready to die yet");
            fontUi.draw(batch, "I'm not ready to die yet", position.x - glyphLayout.width/2, position.y);
            if (deathTime == 0) {
                deathTime = TimeUtils.millis();
            } else if (deathTime + 1000 < TimeUtils.millis() && !isPlayerDeathSoundOn) {
                sndPlayerDeath.play(0.85f * soundVolume);
                isPlayerDeathSoundOn = true;
            } else if (deathTime + 4000 < TimeUtils.millis()) {
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
        imgJstBase.dispose();
        imgJstKnob.dispose();
        imgGameMenu.dispose();
        imgButtonMenu.dispose();
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
        imgObstacleAtlas.dispose();
        imgPaperWad.dispose();
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
        for (int i = 0; i < ghosts.size(); i++) {
            ghosts.get(i).dispose();
        }
        for (int i = 0; i < zombies.size(); i++) {
            zombies.get(i).dispose();
        }
        for (Texture texture : imgRoom) {
            texture.dispose();
        }
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
                    if (player.getBody().getUserData() == "shopping") { // reset  shopping
                        player.getBody().setUserData("player");
                    }
                } else if (touches.get(i).x > player.getX()) {
                    if (btnAttack.hit(touches.get(i).x, touches.get(i).y)) {
                        actAttack = true;
                    }
                    if (btnMenu.hit(touches.get(i).x, touches.get(i).y)) {
                        if (!actMenu) {
                            actMenu = true;
                            sndClick.play(0.9f*soundVolume);
                        }
//                        actMenu = !actMenu;
                    }
                }
                if (actMenu) {
                    if (btnMenuClose.hit(touches.get(i).x, touches.get(i).y) || btnMenuResume.hit(touches.get(i).x, touches.get(i).y)) {
                        sndClick.play(0.9f*soundVolume);
                        actMenu = false;
                    }
                    if (btnMenuMain.hit(touches.get(i).x, touches.get(i).y)) {
                        sndClick.play(0.9f*soundVolume);
                        resetProgress();
                        game.setScreen(game.screenMenu);
                    }
                }

                if (actVending) {
                    if (btnVendingClose.hit(touches.get(i).x, touches.get(i).y)) {
                        sndClick.play(0.9f*soundVolume);
                        actVending = false;
                        player.getBody().setUserData("player");
                        vendingCloseTime = TimeUtils.millis();
                    }
                    else if (btnVendingBuyHeal.hit(touches.get(i).x, touches.get(i).y)) {
                        if (TimeUtils.millis() > buyHealTime + 1000) {
                            if (player.getHealth() < player.getMaxHealth() && wallet >= healCost) {
                                sndClick.play(0.9f * soundVolume);
                                wallet-=healCost;
                                healCost += 1;
                                player.setHealth(player.getMaxHealth());
                                buyHealTime = TimeUtils.millis();
                                sndPowerUp.play(0.65f * soundVolume);
                            } else {
                                sndError.play(0.75f * soundVolume);
                                buyHealTime = TimeUtils.millis();
                            }
                        }

                    }
                    else if (btnVendingBuyDamageUp.hit(touches.get(i).x, touches.get(i).y)) {
                        if (TimeUtils.millis() > buyDamageUpTime + 1000) {
                            if (wallet >= damageUpCost) {
                                sndClick.play(0.9f * soundVolume);
                                wallet-=damageUpCost;
                                damageUpCost *= 1.45f;
                                player.setDamageUp(player.getDamageUp()+1);
                                buyDamageUpTime = TimeUtils.millis();
                                sndPowerUp.play(0.65f * soundVolume);
                            } else {
                                sndError.play(0.75f * soundVolume);
                                buyDamageUpTime = TimeUtils.millis();
                            }
                        }
                    }
                    else if (btnVendingBuySpeedUp.hit(touches.get(i).x, touches.get(i).y)) {
                        if (TimeUtils.millis() > buySpeedUpTime + 1000) {
                            if (wallet >= speedUpCost) {
                                sndClick.play(0.9f * soundVolume);
                                wallet-=speedUpCost;
                                speedUpCost *= 1.25f;
                                player.setSpeedUp(player.getSpeedUp()+2);
                                buySpeedUpTime = TimeUtils.millis();
                                sndPowerUp.play(0.65f * soundVolume);
                            } else {
                                sndError.play(0.75f * soundVolume);
                                buySpeedUpTime = TimeUtils.millis();
                            }
                        }
                    }
                }
            }
        }

        if (elevatorUseTime!=0 || actMenu || actVending) {
            actJoystick = false;
            actAttack = false;
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
        musBackground[musicNumber].stop();
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
        zombies.clear();
        coins.clear();
        obstacles.clear();

        txtCord = "Empty";
        actJoystick = false;
        actAttack = false;
        actMenu = false;
        isPlayerDeathSoundOn = false;
        isMusicOn = false;
        elevatorUseTime = 0;
        vendingCloseTime = 0;
        menuX = menuY = menuWidth = menuHeight = 0;
        rayHandler.setAmbientLight(ambientLight-0.025f*level);

        int tempDamageUp = player.getDamageUp();
        int tempSpeedUp = player.getSpeedUp();
        player = new Player(world, 14, 18, 75, 75, 6, 6, 350, paperWad);
        player.setDamageUp(tempDamageUp);
        player.setSpeedUp(tempSpeedUp);

        musBackground[musicNumber].stop();
        musicNumber = random.nextInt(musBackground.length);
        musBackground[musicNumber].setVolume(0.35f*musicVolume);
        musBackground[musicNumber].setLooping(true);
        musBackground[musicNumber].play();

        if (level>imgRoom.length) {
            // Game end screen will be here some sunny day
            level = 0;
        }
        generateMap(7);
        generateRooms();
        generateElevators();
        vending = new Vending(world, 32, 24, rooms.get(0).getX()+rooms.get(0).getWidth()-35, rooms.get(0).getY()+rooms.get(0).getHeight()-35);
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
                float x = ghost.getX() - imgGhost[0].getRegionWidth() / 2f; // centralized image x
                float y = ghost.getY() - imgGhost[0].getRegionHeight() / 2.25f; // centralized image y
                batch.draw(imgGhost[phase], x, y);
            }
        }
    }

    private void zombiesBatch() {
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

    private void obstaclesBatch() {
        for (int i = 0; i < obstacles.size(); i++) {
            Obstacle obstacle = obstacles.get(i);
            batch.draw(imgObstacle[obstacle.getImgNumber()], obstacle.getX(), obstacle.getY(), obstacle.getWidth()*1.115f, obstacle.getHeight()*1.75f);
        }
    }

    private void vendingBatch() {
        batch.draw(imgVendingMachine, vending.getX(), vending.getY(), 24, 36);
    }

    private void ghostsChangePhase() {
        for (int i = 0; i < ghosts.size(); i++) {
            ghosts.get(i).changePhase();
        }
    }

    private void zombiesChangePhase() {
        for (int i = 0; i < zombies.size(); i++) {
            zombies.get(i).changePhase();
        }
    }

    private void wallBatch() {
        int lvl = level;
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);
            if (level>imgRoom.length) lvl = level%imgRoom.length;
            batch.draw(imgRoom[lvl], room.getX(), room.getY(), room.getWidth(), room.getHeight());
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

    private void elevatorsBatch(){
        for (int i = 0; i < elevators.size(); i++) {
            Elevator elevator = elevators.get(i);
            float scale = 2;
            float originX = elevator.getWidth() * scale / 2;
            float originY = elevator.getHeight() * scale / 2;
            if (elevator.getBody().getUserData()=="elevatorOn") {
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
            }
            else {
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
        float roomSize = 8;
        float mapSize = imgMinimapBackground.getWidth()*(3/4f);
        float mapX, mapY;
//        float shiftX, shiftY;
//        shiftX = shiftY = 0;
        mapX = position.x + SCR_WIDTH/2 - mapSize;
        mapY = position.y + SCR_HEIGHT/2 - mapSize - (imgButtonMenu.getHeight()+4);

        if (player.getHealth()>player.getMaxHealth()) player.setHealth(player.getMaxHealth());
        if (player.getHealth()<0) player.setHealth(0);

        batch.draw(imgHealth[player.getHealth()], position.x - SCR_WIDTH / 2+2, position.y  + SCR_HEIGHT / 2.5f - 4);
        if (wallet==0) batch.draw(imgWallet[0], position.x - SCR_WIDTH / 2 + 2, position.y  + SCR_HEIGHT / 2.5f - 22);
        else if (wallet<5) batch.draw(imgWallet[1], position.x - SCR_WIDTH / 2 + 2, position.y  + SCR_HEIGHT / 2.5f - 22);
        else if (wallet<10) batch.draw(imgWallet[2], position.x - SCR_WIDTH / 2 + 2, position.y  + SCR_HEIGHT / 2.5f - 22);
        else batch.draw(imgWallet[3], position.x - SCR_WIDTH / 2 + 2, position.y  + SCR_HEIGHT / 2.5f - 22);

//        batch.draw(imgMinimapBackground, mapX, mapY, mapSize, mapSize);
        for (int i = 0; i < rooms.size(); i++) {
            Room room = rooms.get(i);

            float roomX = mapX + mapSize/2;
            float roomY = mapY + mapSize/2;

            roomX += ((int)room.getX()/room.getWidth())*roomSize -  roomSize/2f;
            roomY += ((int)room.getY()/room.getHeight())*roomSize -  roomSize/2f;

//            if (roomX<0) shiftX+=roomSize/2f;
//            else if (roomX>0) shiftX-=roomSize/2f;
//            if (roomY<0) shiftY+=roomSize/2f;
//            else if (roomY>0) shiftY-=roomSize/2f;
            if (room.isActive()) {
                if (i==0) batch.draw(imgMinimapRoom[1][1], roomX, roomY, roomSize, roomSize);
                else if (i==rooms.size()-1) batch.draw(imgMinimapRoom[1][2], roomX, roomY, roomSize, roomSize);
                else batch.draw(imgMinimapRoom[1][0], roomX, roomY, roomSize, roomSize);
            }
            else {
                if (i==0) batch.draw(imgMinimapRoom[0][1], roomX, roomY, roomSize, roomSize);
                else if (i==rooms.size()-1) batch.draw(imgMinimapRoom[0][2], roomX, roomY, roomSize, roomSize);
                else batch.draw(imgMinimapRoom[0][0], roomX, roomY, roomSize, roomSize);
            }

        }
    }

    private void vendingUiBatch() {
        if (actVending) {
            batch.draw(imgBlank[1], position.x-SCR_WIDTH, position.y-SCR_HEIGHT, Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2);
            batch.draw(imgVendingUi, vendingX, vendingY, vendingWidth, vendingHeight);
            batch.draw(imgHeal, btnVendingBuyHeal.x, btnVendingBuyHeal.y, btnVendingBuyHeal.width, btnVendingBuyHeal.height);
            font.draw(batch, healCost+"", btnVendingBuyHeal.x+3, btnVendingBuyHeal.y+33);
            batch.draw(imgDamageUp, btnVendingBuyDamageUp.x, btnVendingBuyDamageUp.y, btnVendingBuyDamageUp.width, btnVendingBuyDamageUp.height);
            font.draw(batch, damageUpCost+"", btnVendingBuyDamageUp.x+3, btnVendingBuyDamageUp.y+33);
            batch.draw(imgSpeedUp, btnVendingBuySpeedUp.x, btnVendingBuySpeedUp.y, btnVendingBuySpeedUp.width, btnVendingBuySpeedUp.height);
            font.draw(batch, speedUpCost+"", btnVendingBuySpeedUp.x+3, btnVendingBuySpeedUp.y+33);
            font.draw(batch, wallet+"", btnVendingBuyDamageUp.x+3, btnVendingBuyDamageUp.y-9);
        }
    }

    private void gameMenuBatch() {
        if (actMenu) {
            batch.draw(imgBlank[1], position.x-SCR_WIDTH, position.y-SCR_HEIGHT, Gdx.graphics.getWidth()*2, Gdx.graphics.getHeight()*2);
            batch.draw(imgGameMenu, menuX, menuY, menuWidth, menuHeight);
        }
    }

    private void elevatorBlankBatch() {
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
                            ghosts.get(i).hit(player.getWeapon().getDamage()+player.getDamageUp());
                            player.getProjectiles().get(player.getProjectiles().size() - 1).getBody().setActive(false);
                            world.destroyBody(player.getProjectiles().get(player.getProjectiles().size() - 1).getBody());
                            player.getProjectiles().remove(player.getProjectiles().size() - 1);
                            ghost.getBody().setUserData("ghost");
                            sndPaperBump.play(0.65f*soundVolume);
                        }
                    }
                }
                if (!ghost.isAlive()) {
                    ghost.getBody().setActive(false);
                    world.destroyBody(ghost.getBody());
                    ghosts.remove(i);
                    sndMonsterDeath.play(0.25f*soundVolume);
                    for (int j = 0; j < random.nextInt(3)-1; j++) {
                        Coin coin = new Coin(world, ghost.getX() + (random.nextInt(10)+5)*j, ghost.getY() + (random.nextInt(10)+5)*j, 4.5f, 1);
                        coins.add(coin);
                    }
                    break;
                }
            }
        }
    }

    private void zombiesUpdate() {
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
                            zombies.get(i).hit(player.getWeapon().getDamage()+player.getDamageUp());
                            player.getProjectiles().get(player.getProjectiles().size() - 1).getBody().setActive(false);
                            world.destroyBody(player.getProjectiles().get(player.getProjectiles().size() - 1).getBody());
                            player.getProjectiles().remove(player.getProjectiles().size() - 1);
                            zombie.getBody().setUserData("zombie");
                            sndPaperBump.play(0.65f*soundVolume);
                        }
                    }
                }
                if (!zombie.isAlive()) {
                    zombie.getBody().setActive(false);
                    world.destroyBody(zombie.getBody());
                    zombies.remove(i);
                    sndMonsterDeath.play(0.25f*soundVolume);
                    for (int j = 0; j < random.nextInt(4)-1; j++) {
                        Coin coin = new Coin(world, zombie.getX() + (random.nextInt(10)+5)*j, zombie.getY() + (random.nextInt(10)+5)*j, 4.5f, 1);
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

        int zombieNum = 0;
        for (int i = 0; i < zombies.size(); i++) {
            if (zombies.get(i).getRoom() == getPlayerRoom()) {
                zombieNum++;
                zombies.get(i).setBattle(true);
            }
        }

        for (int i = 1; i < room.getDoorVerBodies().size(); i += 3) {
            if (ghostNum > 0 || zombieNum > 0) {
                if (!player.isBattle) {
                    room.getDoorVerBodies().get(i).setUserData("closeDoor");
                    player.setBattleState(true);
                    musBackground[musicNumber].setVolume(0.5f * musicVolume);
                }
            } else {
                if (player.isBattle) {
                    room.getDoorVerBodies().get(i).setUserData("openDoor");
                    musBackground[musicNumber].setVolume(0.35f * musicVolume);
                    player.setBattleState(false);
                }
            }
        }
        for (int i = 1; i < room.getDoorHorBodies().size(); i += 3) {
            if (ghostNum > 0 || zombieNum > 0) {
                if (!player.isBattle) {
                    room.getDoorVerBodies().get(i).setUserData("closeDoor");
                    player.setBattleState(true);
                    musBackground[musicNumber].setVolume(0.5f * musicVolume);
                }
            } else {
                if (player.isBattle) {
                    room.getDoorVerBodies().get(i).setUserData("openDoor");
                    musBackground[musicNumber].setVolume(0.35f * musicVolume);
                    player.setBattleState(false);
                }
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
                    sndCoinUp.play(0.65f*soundVolume);
                    coins.remove(i);
                    break;
                }
            }
        }
    }

    public void levelUpdate() {
        if (player.getBody().getUserData()=="moved") {
            if (elevatorUseTime==0) {
                elevatorUseTime = TimeUtils.millis();
                musBackground[musicNumber].setVolume(0.35f);
                sndElevatorUse.play(0.9f*soundVolume);
            }
            if (elevatorUseTime < TimeUtils.millis() - 2750) {
                resetWorld();
                level++;
            }
        }
    }

    public void vendingUpdate() {
        if (player.getBody().getUserData()=="shopping" && !actVending && vendingCloseTime + 4250 < TimeUtils.millis()) {
            actVending = true;
            player.getBody().setUserData("player");
            vendingCloseTime = TimeUtils.millis();
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

            ArrayList<Room> tempRooms = new ArrayList<>();
            Room room = new Room(world, roomX, roomY, roomWidth, roomHeight, doors, rooms, roomType);
            setDoors();
            rooms.add(room);
            room.setDoors(doors);
            doors.clear();
        }

        Room lastRoom = rooms.get(rooms.size() - 1);
        Room preLastRoom = rooms.get(rooms.size() - 2);

        if (lastRoom.getX()==preLastRoom.getX() && lastRoom.getY()+lastRoom.getHeight()==preLastRoom.getY()) {
            if (lastRoom.getY() + roomHeight == preLastRoom.getY()) {
                lastRoom.addDoor('u');
                lastRoom.removeTopWall();
                preLastRoom.addDoor('d');
                preLastRoom.removeBottomWall();
            }
        }
        else if (lastRoom.getX()==preLastRoom.getX() && lastRoom.getY()-lastRoom.getHeight()==preLastRoom.getY()) {
            if (lastRoom.getY() - roomHeight == preLastRoom.getY()) {
                lastRoom.addDoor('d');
                lastRoom.removeBottomWall();
                preLastRoom.addDoor('u');
                preLastRoom.removeTopWall();
            }
        }
        else if (lastRoom.getY()==preLastRoom.getY() && lastRoom.getX()-lastRoom.getWidth()==preLastRoom.getX()) {
            if (lastRoom.getX() - roomWidth == preLastRoom.getX()) {
                lastRoom.addDoor('l');
                lastRoom.removeLeftWall();
                preLastRoom.addDoor('r');
                preLastRoom.removeRightWall();
            }
        }
       else  if (lastRoom.getY()==preLastRoom.getY() && lastRoom.getX()+lastRoom.getWidth()==preLastRoom.getX()) {
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
                    for (int j = 0; j < MathUtils.random(4-level/2)+1; j++) {
                        size = MathUtils.random(5)-5;
                        spawnX = MathUtils.random(room.getX() + THICKNESS*2, room.getX() + room.getWidth() - THICKNESS*2);
                        spawnY = MathUtils.random(room.getY() + THICKNESS*2, room.getY() + room.getHeight() - THICKNESS*2);

                        Ghost ghost = new Ghost(world, 20+size, 24+size, spawnX, spawnY, 3, 4, 250, ghostOrb);
                        ghost.setRoomNum(i);
                        ghosts.add(ghost);
                    }

                    for (int j = 0; j < MathUtils.random(level)-1; j++) {
                        size = MathUtils.random(5)-5;
                        spawnX = MathUtils.random(room.getX() + THICKNESS*2, room.getX() + room.getWidth() - THICKNESS*2);
                        spawnY = MathUtils.random(room.getY() + THICKNESS*2, room.getY() + room.getHeight() - THICKNESS*2);

                        Zombie zombie = new Zombie(world, 12.5f+size, 21+size, spawnX, spawnY, 6, 10, 175, ghostOrb);
                        zombie.setRoomNum(i);
                        zombies.add(zombie);
                    }

                    for (int j = 0; j < MathUtils.random(2)+1; j++) {
                        spawnX = MathUtils.random(room.getX() + THICKNESS*2, room.getX() + room.getWidth() - THICKNESS*2);
                        spawnY = MathUtils.random(room.getY() + THICKNESS*2, room.getY() + room.getHeight() - THICKNESS*2);
                        size = MathUtils.random(5)+10;

                        Obstacle obstacle = new Obstacle(world, size, size, spawnX, spawnY);
                        obstacle.setRoomNum(i);
                        obstacles.add(obstacle);
                        obstacle.setImgNumber(random.nextInt(imgObstacle.length));
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
            }
            else room.setActive(false);
        }
        return 0;
    }
}
