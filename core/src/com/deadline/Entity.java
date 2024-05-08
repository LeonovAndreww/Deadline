package com.deadline;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

public class Entity {
    private Body body;
    private float width, height;
    private float speed = 50f;
    private int health, maxHealth;
    private char direction = 'd';
    private int phase, nPhases;
    private long timeLastPhase, timePhaseInterval;
    private boolean isAlive;

    public Entity(World world, float width, float height, float x, float y, int maxHealth, int nPhases, long timePhaseInterval) {
        phase = 0;
        this.nPhases = nPhases;
        this.timePhaseInterval = timePhaseInterval; // except running animation!
        this.width = width;
        this.height = height;
        this.health = this.maxHealth = maxHealth;
        this.isAlive = true;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;

        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(this.width / 2, this.height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.4f;

        body.createFixture(fixtureDef);

        shape.dispose();
    }

    void changePhase(){
        long interval = timePhaseInterval;
        if(TimeUtils.millis() > timeLastPhase+interval) {
            if (++phase == nPhases) phase = 0;
            timeLastPhase = TimeUtils.millis();
        }
    }

    public void hit(int damage) {
        health-=damage;
        if (health<=0) {
            isAlive = false;
            health = 0;
        }
    }

    public Body getBody() {
        return body;
    }

    public float getX() {
        return body.getPosition().x;
    }
    public float getY() {
        return body.getPosition().y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public char getDirection() {
        return direction;
    }

    public void setDirection(char dir) {
        direction = dir;
    }

    int getPhase() {
        return phase;
    }

    int getHealth() {
        return health;
    }

    int getMaxHealth() {
        return maxHealth;
    }

    public boolean isAlive() {
        return isAlive;
    }
}
