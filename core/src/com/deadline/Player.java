package com.deadline;

import static com.deadline.DdlnGame.soundVolume;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.ArrayList;
import java.util.Objects;

public class Player extends Entity {
    private static final float BASIC_SPEED = 50f, BATTLE_SPEED = 75f;
    private final World world;
    private Weapon weapon;
    private int damageUp = 0, speedUp = 0;
    private int health, maxHealth;
    private boolean isAlive;
    private long timeLastAttack, timeLastDamaged, timeLastStep;
    private ArrayList<Projectile> projectiles = new ArrayList<>();

    private final Sound sndPaperSwing, sndPaperBump;
    private final Sound sndStep;
    private final Sound sndPlayerHurt;

    public Player(World world, float width, float height, float x, float y, int maxHealth, int nPhases, long timeBasePhaseInterval, Weapon weapon) {
        super(world, width, height, x, y, maxHealth, nPhases, timeBasePhaseInterval);
        getBody().setUserData("player");
        this.timeBasePhaseInterval = timeBasePhaseInterval;
        this.timePhaseInterval = timeBasePhaseInterval;
        this.weapon = weapon;
        this.health = maxHealth;
        this.isAlive = true;
        this.maxHealth = maxHealth;
        this.world = world;

        sndPaperSwing = Gdx.audio.newSound(Gdx.files.internal("paperSwing.mp3"));
        sndPaperBump =  Gdx.audio.newSound(Gdx.files.internal("paperBump.mp3"));
        sndStep = Gdx.audio.newSound(Gdx.files.internal("step.mp3"));
        sndPlayerHurt = Gdx.audio.newSound(Gdx.files.internal("playerHurt.mp3"));

//        world.setContactListener(new MyContactListener(world));
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

    public void hit() {
        String hit = this.getBody().getUserData().toString();
        if (TimeUtils.millis() - this.timeLastDamaged > 2650) {
            if (!(Objects.equals(hit, "player") || Objects.equals(hit, "moved") || Objects.equals(hit, "shopping"))) {
                char charHit = hit.charAt(hit.length() - 1);
                int value = Character.getNumericValue(charHit);
                System.out.println(value);
                System.out.println(this.health);
                this.health -= value;
                sndPlayerHurt.play(0.65f*soundVolume);
                this.timeLastDamaged = TimeUtils.millis();
            }
        }
        else this.getBody().setUserData("player");
        if (this.health <= 0) {
            this.isAlive = false;
            this.health = 0;
        }
    }

    public void step(boolean joystickAct) {
        if (joystickAct) {
            timePhaseInterval = timeBasePhaseInterval-150;
            if (TimeUtils.millis() - timeLastStep > timePhaseInterval*2) {
                if (isBattle) {
                    if (getPhase()%2==0) {
                        sndStep.play(0.75f * soundVolume);
                        timeLastStep = TimeUtils.millis();
                    }
                }
                else if(getPhase()==2 || getPhase()==5) {
                    sndStep.play(0.8f * soundVolume);
                    timeLastStep = TimeUtils.millis();
                }
            }
        }
    }

    private void meleeAttack() {
        // Melee attack (когда-нибудь)
    }

    private void rangedAttack() {
        Projectile projectile = new Projectile(world, getX() - getWidth() / 4, getY(), 1.5f, weapon.getSpeed()+getSpeed()+speedUp*2, getDirection(), TimeUtils.millis(), weapon.getDamage());
        projectiles.add(projectile);
    }
    void setBattleState(boolean isBattle) {
        this.isBattle = isBattle;
        if (this.isBattle) super.setSpeed(BATTLE_SPEED+speedUp*2);
        else super.setSpeed(BASIC_SPEED+speedUp*2);
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public ArrayList<Projectile> getProjectiles() {
        return projectiles;
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
        sndPlayerHurt.dispose();
    }

    public int getHealth() {
        return this.health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public int getDamageUp() {
        return damageUp;
    }

    public void setDamageUp(int damageUp) {
        this.damageUp = damageUp;
    }

    public int getSpeedUp() {
        return speedUp;
    }

    public void setSpeedUp(int speedUp) {
        this.speedUp = speedUp;
    }
}
