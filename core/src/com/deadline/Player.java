package com.deadline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;

public class Player extends Entity {
    private static final float BASIC_SPEED = 50f, BATTLE_SPEED = 75f;
    private final World world;
    private Weapon weapon;
    private int health, maxHealth;
    private long timeLastAttack, timeLastDamaged, timeLastStep;
    private ArrayList<Projectile> projectiles = new ArrayList<>();
    private final Sound sndPaperSwing, sndPaperBump;
    private final Sound sndStep;

    public Player(World world, float width, float height, float x, float y, int maxHealth, int nPhases, long timeBasePhaseInterval, Weapon weapon) {
        super(world, width, height, x, y, maxHealth, nPhases, timeBasePhaseInterval);
        getBody().setUserData("player");
        this.timeBasePhaseInterval = timeBasePhaseInterval;
        timePhaseInterval = timeBasePhaseInterval;
        this.weapon = weapon;
        this.health = health;
        this.maxHealth = maxHealth;
        this.world = world;
        sndPaperSwing = Gdx.audio.newSound(Gdx.files.internal("paperSwing.mp3"));
        sndPaperBump =  Gdx.audio.newSound(Gdx.files.internal("paperBump.mp3"));
        sndStep = Gdx.audio.newSound(Gdx.files.internal("step.mp3"));
        world.setContactListener(new MyContactListener(world));
    }

    void attack() {
        if (weapon == null) return;

        if (TimeUtils.millis() - timeLastAttack > weapon.getReloadTime()) {
            if (weapon.isMelee()) {
                meleeAttack();
            } else {
                rangedAttack();
            }
            sndPaperSwing.play();
            timeLastAttack = TimeUtils.millis();
        }
    }

    public void update(int damage) { // безобидный костыль
        if (getBody().getUserData()=="hit") {
            if (TimeUtils.millis() - timeLastDamaged > 2650) {
                hit(damage);
                timeLastDamaged = TimeUtils.millis();
            }
            getBody().setUserData("player");
        }
    }

    public void step(boolean joystickAct) {
        if (joystickAct) {
            timePhaseInterval = timeBasePhaseInterval-150;
            if (TimeUtils.millis() - timeLastStep > timePhaseInterval*2) {
                if (isBattle) {
                    if (getPhase()%2==0) {
                        sndStep.play(0.35f * DdlnGame.soundVolume);
                        timeLastStep = TimeUtils.millis();
                    }
                }
                else if(getPhase()==2 || getPhase()==5) {
                    sndStep.play(0.4f * DdlnGame.soundVolume);
                    timeLastStep = TimeUtils.millis();
                }
            }
        }
    }

    private void meleeAttack() {
        // Melee attack
    }

    private void rangedAttack() {
        Projectile projectile = new Projectile(world, getX() - getWidth() / 4, getY(), 1.5f, weapon.getSpeed()+getSpeed(), getDirection(), TimeUtils.millis(), weapon.getDamage());
        projectiles.add(projectile);
    }
    void setBattleState(boolean isBattle) {
        this.isBattle = isBattle;
        if (this.isBattle) super.setSpeed(BATTLE_SPEED);
        else super.setSpeed(BASIC_SPEED);
    }

    public boolean getBattleState() {
        return isBattle;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
    }

    public void setProjectiles(ArrayList<Projectile> projectiles) {
        this.projectiles=projectiles;
    }

    public void updateProjectiles() {
        for (int i = 0; i < projectiles.size(); i++) {
            if (!projectiles.get(i).getBody().isActive()) {
                projectiles.get(i).resetCreateTime();
                projectiles.get(i).resetCreateTime();
                world.destroyBody(projectiles.get(i).getBody());
                projectiles.remove(i);
                sndPaperBump.play();
                break;
//                i--;
            } else if (projectiles.get(i).getCreateTime() + getWeapon().getDuration() <= TimeUtils.millis()){
                projectiles.get(i).resetCreateTime();
                projectiles.get(i).getBody().setActive(false);
                world.destroyBody(projectiles.get(i).getBody());
                projectiles.remove(i);
                sndPaperBump.play();
                break;
            }
        }
    }

    public void dispose() {
        sndPaperSwing.dispose();
        sndPaperBump.dispose();
        sndStep.dispose();
    }
}
