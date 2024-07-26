package com.deadline;

import static com.deadline.DdlnGame.soundVolume;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Random;

public class Warden extends Entity {
    private final World world;
    protected long timePhaseInterval;
    private Weapon weapon;
    private long timeLastAttack, timeLastPhase;
    private int phase, nPhases;
    private int room;
    private Sound sndPaperSwing, sndPaperBump;
    private ArrayList<Projectile> projectiles = new ArrayList<>();
    Random random = new Random();

    public Warden(World world, float width, float height, float x, float y, int maxHealth, int nPhases, long timePhaseInterval, Weapon weapon) {
        super(world, width, height, x, y, maxHealth, nPhases, timePhaseInterval);
        this.world = world;
        this.nPhases = nPhases;
        this.timePhaseInterval = timePhaseInterval+random.nextInt(101);
        this.weapon = weapon;
        isBattle = false;
        this.phase = random.nextInt(4);
        getBody().setUserData("warden");

        sndPaperSwing = Gdx.audio.newSound(Gdx.files.internal("sounds/paperSwing.mp3"));
        sndPaperBump =  Gdx.audio.newSound(Gdx.files.internal("sounds/paperBump.mp3"));
    }

    @Override
    public void changePhase() {
        if (isBattle) {
            long interval = timePhaseInterval;
            if (TimeUtils.millis() > timeLastPhase + interval) {
                if (++phase == nPhases) phase = 0;
                timeLastPhase = TimeUtils.millis();
            }
        }
    }


//    public void update() {
//        if (isAlive()) {
//            if (Math.abs(getBody().getLinearVelocity().x) > Math.abs(getBody().getLinearVelocity().y)) {
//                if (getBody().getLinearVelocity().x > 0) setDirection('r');
//                else setDirection('l');
//            } else {
//                if (getBody().getLinearVelocity().y > 0) setDirection('u');
//                else setDirection('d');
//            }
//        }
//    }

    public void attack(Vector2 playerPos) {
        if (isBattle) {
            if (TimeUtils.millis() - timeLastAttack > weapon.getReloadTime()+random.nextInt(2550)) {
                Character[] directions = {'u', 'd', 'l', 'r'};
                for (int i = 0; i < 4; i++) {
                    Projectile projectile = new Projectile(world, getX() - getWidth() / 4, getY(), 1.5f, weapon.getSpeed() + getSpeed(), directions[i], TimeUtils.millis(), weapon.getDamage());
                    projectile.getBody().setUserData("projectileWarden");
                    projectiles.add(projectile);
                }
//                getBody().setLinearVelocity(getBody().getLinearVelocity().x+random.nextInt(20)-10, getBody().getLinearVelocity().y+random.nextInt(20)-10);

                Vector2 directionToPlayer = playerPos.sub(getPosition()).nor();
                float randomSpeed = 15 + random.nextInt(35);
                float randomXOffset = random.nextInt(30) - 15;
                float randomYOffset = random.nextInt(30) - 15;
                getBody().setLinearVelocity(directionToPlayer.scl(randomSpeed).add(randomXOffset, randomYOffset));
//                getBody().setLinearVelocity(playerPos.sub(getPosition()).nor().scl(15+random.nextInt(35)).x+random.nextInt(30)-15, playerPos.sub(getPosition()).nor().scl(15+random.nextInt(35)).y+random.nextInt(30)-15);
                timeLastAttack = TimeUtils.millis();
                sndPaperSwing.play(0.3f*soundVolume);
            }
        }
    }

    public void updateProjectiles() {
        for (int i = 0; i < projectiles.size(); i++) {
            if (!projectiles.get(i).getBody().isActive()) {
                projectiles.get(i).resetCreateTime();
                projectiles.get(i).resetCreateTime();
                world.destroyBody(projectiles.get(i).getBody());
                projectiles.remove(i);
//                sndPaperBump.play(0.25f*soundVolume);
                break;
//                i--;
            } else if (projectiles.get(i).getCreateTime() + getWeapon().getDuration() <= TimeUtils.millis()){
                projectiles.get(i).resetCreateTime();
                projectiles.get(i).getBody().setActive(false);
                world.destroyBody(projectiles.get(i).getBody());
                projectiles.remove(i);
//                sndPaperBump.play(0.25f*soundVolume);
                break;
            }
        }
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public int getRoom() {
        return room;
    }

    public void setRoomNum(int room) {
        this.room = room;
    }

    public void setBattle(boolean battle) {
        this.isBattle = battle;
    }

    @Override
    public int getPhase() {
        return phase;
    }

    public void dispose() {
        sndPaperBump.dispose();
        sndPaperSwing.dispose();
    }
}
