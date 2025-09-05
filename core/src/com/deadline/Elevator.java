package com.deadline;

import static com.deadline.DdlnGame.THICKNESS;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

public class Elevator {
    private final World world;
    private Body body;
    private float x, y;
    private float width, height;
    private float rotation;
    private boolean isWorking;

    public Elevator(World world, float tempX, float tempY, float tempWidth, float tempHeight, ArrayList<Character> walls, boolean isWorking) {
        this.world = world;
        x = tempX;
        y = tempY;
        this.width = tempWidth-THICKNESS*2;
        this.height = tempHeight-THICKNESS*2;

        ArrayList<Character> doors = new ArrayList<>();
        doors.add('u');
        doors.add('d');
        doors.add('l');
        doors.add('r');

        if (walls.contains('u')) doors.set(0, '0');
        if (walls.contains('d')) doors.set(1, '0');
        if (walls.contains('l')) doors.set(2, '0');
        if (walls.contains('r')) doors.set(3, '0');


        BodyDef bodyDef = new BodyDef();

        PolygonShape shape = new PolygonShape();

          if (doors.contains('l')) {
            x += THICKNESS/2f;
            y += tempHeight/2f;
            width /= 30;
            height /= 3.75f;
            shape.setAsBox(width, height);
            rotation = 270;
        } else if (doors.contains('r')) {
            x += tempWidth-THICKNESS/2F;
            y += tempHeight/2;
            width /= 30;
            height /= 3.75f;
            shape.setAsBox(width, height);
            rotation = 90;
        }
        else if (doors.contains('u')) {
            x += tempWidth/2;
            y += tempHeight-THICKNESS/2f;
            width /= 3.75f;
            height /= 30;
            shape.setAsBox(width, height);
            rotation = 180;
        } else if (doors.contains('d')) {
            x += tempWidth/2;
            y += THICKNESS/2f;
            width /= 3.75f;
            height /= 30;
            shape.setAsBox(width, height);
            rotation = 0;
        }else {
            shape.setAsBox(width / 8, height / 8);
            rotation = 0;
            System.out.println("Invalid elevator direction!");
        }
        bodyDef.position.set(x, y);

        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        this.body.createFixture(fixtureDef);
        if (isWorking) {
            body.setUserData("elevatorOn");
        } else {
            body.setUserData("elevatorOff");
        }

        shape.dispose();
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        if (rotation==90 || rotation == 270) {
            return height;
        }
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        if (rotation==90 || rotation == 270) {
            return width;
        }
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }
}
