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

public class Player extends Entity{
    private final World world;
    private boolean isBattle = false;
    private Weapon weapon;
    private long  timeLastAttack;
    private MyContactListener myContactListener;
    private ArrayList<Body> projectiles = new ArrayList<>();
    private Sound paperSwing;

    public Player(World world, float width, float height, float x, float y, int nPhases, long timePhaseInterval, Weapon weapon) {
        super(world, width, height, x, y, nPhases, timePhaseInterval);
        getBody().setUserData("player");
        this.weapon = weapon;
        this.world = world;
        paperSwing = Gdx.audio.newSound(Gdx.files.internal("paperSwing.mp3"));
        world.setContactListener(new MyContactListener(world));
    }

    void attack(){
        if (weapon == null) return;

        if (TimeUtils.millis()-timeLastAttack> weapon.getReloadTime()) {
            if (weapon.isMelee()) {
                meleeAttack();
            } else {
                rangedAttack();
            }
            paperSwing.play();
            timeLastAttack = TimeUtils.millis();
        }
    }

    private void meleeAttack() {
        // Melee attack
    }

    private void rangedAttack() {
        float angle = 0;
        switch (getDirection()) {
            case 'u':
                angle = MathUtils.PI / 2;
                break;
            case 'd':
                angle = -MathUtils.PI / 2;
                break;
            case 'l':
                angle = -MathUtils.PI;
                break;
            case 'r':
                angle = 0;
                break;
        }

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(getX()-getWidth()/4, getY());
        bodyDef.linearDamping = 0; // No damping
        bodyDef.angularDamping = 0; // No damping
        bodyDef.gravityScale = 0; // No gravity
        Body body = world.createBody(bodyDef);
        body.setUserData("projectile");

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f; // Set the density to a non-zero value
        fixtureDef.friction = 0; // No friction
        fixtureDef.restitution = 0; // No restitution

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(1.5f);
        fixtureDef.shape = circleShape;

        body.createFixture(fixtureDef);

        projectiles.add(body);
        circleShape.dispose();

        body.setLinearVelocity(weapon.getRange() * 2 * (float) Math.cos(angle), weapon.getRange() * 2 * (float) Math.sin(angle));
    }

    void changeBattleState(boolean isBattle) {
        this.isBattle = isBattle;
        if (this.isBattle) super.setSpeed(75f);
        else super.setSpeed(50f);
    }

    public boolean getBattleState() {
        return isBattle;
    }

    public MyContactListener getProjectileListener() {
        return myContactListener;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public ArrayList<Body> getProjectiles() {
        return projectiles;
    }
}
