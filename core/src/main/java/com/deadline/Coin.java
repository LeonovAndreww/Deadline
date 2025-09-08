package com.deadline;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class Coin {
    private final Body body;
    private final float radius;
    private final int value;

    public Coin(World world, float x, float y, float radius, int value) {
        this.radius = radius;
        this.value = value;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.linearDamping = 0.25f;
        bodyDef.angularDamping = 0.25f;
        bodyDef.gravityScale = 0;
        body = world.createBody(bodyDef);
        body.setUserData("coin");

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0.45f;
        fixtureDef.friction = 0.75f;
        fixtureDef.restitution = 1.0f;

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius);
        fixtureDef.shape = circleShape;

        body.createFixture(fixtureDef);

        circleShape.dispose();
    }

    public Body getBody() {
        return body;
    }

    public float getX() {
        return getBody().getPosition().x;
    }

    public float getY() {
        return getBody().getPosition().y;
    }

    public float getRadius() {
        return radius;
    }

    public int getValue() {
        return value;
    }
}
