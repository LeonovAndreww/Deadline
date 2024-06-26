//package com.deadline;
//
//import com.badlogic.gdx.physics.box2d.Body;
//import com.badlogic.gdx.physics.box2d.BodyDef;
//import com.badlogic.gdx.physics.box2d.FixtureDef;
//import com.badlogic.gdx.physics.box2d.PolygonShape;
//import com.badlogic.gdx.physics.box2d.World;
//import com.badlogic.gdx.utils.TimeUtils;
//
//public class MeleeRegion {
//    private final Body body;
//    private float vx = 0, vy = 0;
//    private final float speed;
//    private long createTime;
//    private final int damage;
//    private float width, height;
//
//    public MeleeRegion(World world, float x, float y, float width, float height, float speed, char direction, long createTime, int damage) {
//        this.speed = speed;
//        this.createTime = createTime;
//        this.damage = damage;
//        this.width = width;
//        this.height = height;
//
//        switch (direction) {
//            case 'u':
//                vy = height/2;
//                break;
//            case 'd':
//                vy = -height/2;
//                break;
//            case 'l':
//                vx = -width/2;
//                break;
//            case 'r':
//                vx = width/2;
//                break;
//            default:
//                vx = 0;
//                vy = 0;
//        }
//
//        BodyDef bodyDef = new BodyDef();
//        bodyDef.type = BodyDef.BodyType.DynamicBody;
//        bodyDef.position.set(x+vx, y+vy);
//        bodyDef.linearDamping = 0; // No damping
//        bodyDef.angularDamping = 0; // No damping
//        bodyDef.gravityScale = 0; // No gravity
//        body = world.createBody(bodyDef);
//        body.setUserData("meleeRegionFalse");
//
//        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.density = 1.0f; // Set the density to a non-zero value
//        fixtureDef.friction = 0; // No friction
//        fixtureDef.restitution = 0; // No restitution
//        fixtureDef.isSensor = true;
//
//        PolygonShape shape = new PolygonShape();
//        shape.setAsBox(this.width / 2, this.height / 2);
//        fixtureDef.shape = shape;
//
//        body.createFixture(fixtureDef);
//
//        shape.dispose();
//    }
//
//    public void update(char direction, float parentX, float parentY) {
//        switch (direction) {
//            case 'u':
//                vy = height/2;
//                vx = width/4;
//                break;
//            case 'd':
//                vy = -height/2;
//                vx = width/4;
//                break;
//            case 'l':
//                vy = height/4;
//                vx = -width/2;
//                break;
//            case 'r':
//                vy = height/4;
//                vx = width/2;
//                break;
//        }
//
//        body.setTransform(parentX+vx, parentY+vy, body.getAngle());
//    }
//
//    public Body getBody() {
//        return body;
//    }
//
//    public float getSpeed() {
//        return speed;
//    }
//
//    public long getCreateTime() {
//        return createTime;
//    }
//
//    public void resetCreateTime() {
//        createTime = TimeUtils.millis();
//    }
//
//    public float getX() {
//        return getBody().getPosition().x;
//    }
//
//    public float getY() {
//        return getBody().getPosition().y;
//    }
//
//    public int getDamage() {
//        return damage;
//    }
//}
