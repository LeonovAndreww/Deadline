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
    private final World world;
    private boolean isBattle = false;
    private Weapon weapon;
    private int health, maxHealth;
    private long timeLastAttack;
    private ArrayList<Projectile> projectiles = new ArrayList<>();
    private final Sound sndPaperSwing, sndPaperBump;

    public Player(World world, float width, float height, float x, float y, int maxHealth, int nPhases, long timePhaseInterval, Weapon weapon) {
        super(world, width, height, x, y, maxHealth, nPhases, timePhaseInterval);
        getBody().setUserData("player");
        this.weapon = weapon;
        this.health = health;
        this.maxHealth = maxHealth;
        this.world = world;
        sndPaperSwing = Gdx.audio.newSound(Gdx.files.internal("paperSwing.mp3"));
        sndPaperBump =  Gdx.audio.newSound(Gdx.files.internal("paperBump.mp3"));
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

    private void meleeAttack() {
        // Melee attack
    }

    private void rangedAttack() {
        Projectile projectile = new Projectile(world, getX() - getWidth() / 4, getY(), weapon.getSpeed(), getDirection(), TimeUtils.millis(), weapon.getDamage());
        projectiles.add(projectile);
    }
    void changeBattleState(boolean isBattle) {
        this.isBattle = isBattle;
        if (this.isBattle) super.setSpeed(75f);
        else super.setSpeed(50f);
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
                projectiles.remove(i);
//                i--;
            } else if (projectiles.get(i).getCreateTime() + getWeapon().getDuration() < TimeUtils.millis()){
                projectiles.get(i).getBody().setActive(false);
                world.destroyBody(projectiles.get(i).getBody());
                projectiles.remove(i);
                sndPaperBump.play();
            }
        }
    }

    public void dispose() {
        sndPaperSwing.dispose();
        sndPaperBump.dispose();
    }
}
