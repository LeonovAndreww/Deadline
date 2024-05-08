package com.deadline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Projectile {
    private final Body body;
    private float x, y;
    private final float speed;
    private final long createTime;
    private final int damage;

    public Projectile(World world, float x, float y, float speed, char direction, long createTime, int damage) {
        this.speed = speed;
        this.createTime = createTime;
        this.damage = damage;

        float vx = 0, vy = 0;
        switch (direction) {
            case 'u':
                vy = 2;
                break;
            case 'd':
                vy = -2;
                break;
            case 'l':
                vx = -2;
                break;
            case 'r':
                vx = 2;
                break;
        }

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.linearDamping = 0; // No damping
        bodyDef.angularDamping = 0; // No damping
        bodyDef.gravityScale = 0; // No gravity
        body = world.createBody(bodyDef);
        body.setUserData("projectile");

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 1.0f; // Set the density to a non-zero value
        fixtureDef.friction = 0; // No friction
        fixtureDef.restitution = 0; // No restitution

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(1.5f);
        fixtureDef.shape = circleShape;

        body.createFixture(fixtureDef);

        circleShape.dispose();
        body.setLinearVelocity(vx*speed, vy*speed);
    }

    public Body getBody() {
        return body;
    }

    public float getSpeed() {
        return speed;
    }

    public long getCreateTime() {
        return createTime;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getDamage() {
        return damage;
    }
}
