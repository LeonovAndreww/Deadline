package com.deadline;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;

public class Room {
    World world;
    private float x, y;
    private float width, height;
    private char type;
    public Body topWall, bottomWall, leftWall, rightWall;
    private boolean hasTopWall, hasBottomWall, hasLeftWall, hasRightWall;
    private ArrayList<Character> doors = new ArrayList<>();
    private ArrayList<Body> doorHorBodies = new ArrayList<>();
    private ArrayList<Body> doorVerBodies = new ArrayList<>();
//    private ArrayList<Character>

    boolean built;

    public Room(World world, float x, float y, float width, float height, ArrayList<Character> doorDir, ArrayList<Room> rooms, char type) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.type = type;
        built = false;

//         door check

        if ((doorDir.contains('u') && (isRoom(this.x, this.y + this.height, rooms) || rooms.isEmpty())) || getRoomDir(this.x, this.y + this.height, rooms).contains('d'))
            this.doors.add('u');
        if ((doorDir.contains('d') && (isRoom(this.x, this.y - this.height, rooms) || rooms.isEmpty())) || getRoomDir(this.x, this.y - this.height, rooms).contains('u'))
            this.doors.add('d');
        if ((doorDir.contains('l') && (isRoom(this.x - this.width, this.y, rooms) || rooms.isEmpty())) || getRoomDir(this.x - this.width, this.y, rooms).contains('r'))
            this.doors.add('l');
        if ((doorDir.contains('r') && (isRoom(this.x + this.width, this.y, rooms) || rooms.isEmpty())) || getRoomDir(this.x + this.width, this.y, rooms).contains('l'))
            this.doors.add('r');

        // wall and door create
        topWall = createWall(this.x, this.y + this.height - 10, this.width, 10);

        bottomWall = createWall(this.x, this.y, this.width, 10);

        leftWall = createWall(this.x, this.y, 10, this.height);

        rightWall = createWall(this.x + this.width - 10, this.y, 10, this.height);

        hasTopWall = hasBottomWall = hasLeftWall = hasRightWall = true;
    }

    boolean isRoom(float x, float y, ArrayList<Room> rooms) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).x == x && rooms.get(i).y == y) return true;
        }
        return false;
    }

    ArrayList<Character> getRoomDir(float x, float y, ArrayList<Room> rooms) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).x == x && rooms.get(i).y == y) return rooms.get(i).doors;
        }
        ArrayList<Character> nullDir = new ArrayList<>();
//        nullDir.add('n');
        return nullDir;
    }

    private Body createWall(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x + width / 2, y + height / 2);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);
        body.setUserData("wall");

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    private void createHorDoor(float x, float y, float width, float height) {
        BodyDef bodyDef1 = new BodyDef();
        bodyDef1.position.set(x + width*(1f/3)-23, y + height / 2);
        bodyDef1.type = BodyDef.BodyType.StaticBody;
        Body body1 = world.createBody(bodyDef1);

        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.position.set(x+ width*(2f/3)-33, y + height/2);
        bodyDef2.type = BodyDef.BodyType.StaticBody;
        Body body2 = world.createBody(bodyDef2);

        BodyDef bodyDef3 = new BodyDef();
        bodyDef3.position.set(x + width - 43, y + height/2);
        bodyDef3.type = BodyDef.BodyType.StaticBody;
        Body body3 = world.createBody(bodyDef3);

        PolygonShape wallShape = new PolygonShape();
        wallShape.setAsBox(width/3/2, height/2);

        PolygonShape doorShape = new PolygonShape();
        doorShape.setAsBox(width/3/2-10, height/2);

        FixtureDef fixtureDef1 = new FixtureDef();
        fixtureDef1.shape = wallShape;

        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = doorShape;
        fixtureDef2.filter.maskBits = 1;
        fixtureDef2.filter.categoryBits = 1;
//        fixtureDef2.isSensor = true;
        fixtureDef2.filter.groupIndex = 1;

        FixtureDef fixtureDef3 = new FixtureDef();
        fixtureDef3.shape = wallShape;

        body1.createFixture(fixtureDef1);
        body1.setUserData("wall");
        doorHorBodies.add(body1);

        body2.createFixture(fixtureDef2);
        body2.setUserData("openDoor");
        doorHorBodies.add(body2);

        body3.createFixture(fixtureDef3);
        body3.setUserData("wall");
        doorHorBodies.add(body3);

        wallShape.dispose();
    }
    private void createVerDoor (float x, float y, float width, float height) {
        BodyDef bodyDef1 = new BodyDef();
        bodyDef1.position.set(x + width / 2, y + (height)*(1f/3)-23);
        bodyDef1.type = BodyDef.BodyType.StaticBody;
        Body body1 = world.createBody(bodyDef1);

        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.position.set(x + width / 2, y + (height)*(2f/3)-33);
        bodyDef2.type = BodyDef.BodyType.StaticBody;
        Body body2 = world.createBody(bodyDef2);

        BodyDef bodyDef3 = new BodyDef();
        bodyDef3.position.set(x + width / 2, y + (height)- 43);
        bodyDef3.type = BodyDef.BodyType.StaticBody;
        Body body3 = world.createBody(bodyDef3);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, (height)/3/2);

        PolygonShape doorShape = new PolygonShape();
        doorShape.setAsBox(width/2, height/3/2-10);

        FixtureDef fixtureDef1 = new FixtureDef();
        fixtureDef1.shape = shape;

        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = doorShape;
//        fixtureDef2.isSensor = true;

        FixtureDef fixtureDef3 = new FixtureDef();
        fixtureDef3.shape = shape;

        body1.createFixture(fixtureDef1);
        body1.setUserData("wall");
        doorVerBodies.add(body1);

        body2.createFixture(fixtureDef2);
        body2.setUserData("openDoor");
        doorVerBodies.add(body2);

        body3.createFixture(fixtureDef3);
        body3.setUserData("wall");
        doorVerBodies.add(body3);

        shape.dispose();
    }

    public void removeTopWall() {
        if (hasTopWall) {
            doors.add('u');
            world.destroyBody(topWall);
            hasTopWall = false;
            createHorDoor(this.x, this.y + this.height - 10, this.width, 10);
        }
    }

    public void removeBottomWall() {
        if (hasBottomWall) {
            doors.add('d');
            world.destroyBody(bottomWall);
            hasBottomWall = false;
            createHorDoor(this.x, this.y, this.width, 10);
        }
    }

    public void removeLeftWall() {
        if (hasLeftWall) {
            doors.add('l');
            world.destroyBody(leftWall);
            hasLeftWall = false;
            createVerDoor(this.x, this.y, 10, this.height);
        }
    }

    public void removeRightWall() {
        if (hasRightWall) {
            doors.add('r');
            world.destroyBody(rightWall);
            hasRightWall = false;
            createVerDoor(this.x + this.width - 10, this.y, 10, this.height);
        }
    }

    public ArrayList<Character> getDoors() {
        if (!doors.isEmpty()) return doors;
        else {
            System.out.println("getDoors костыль активирован!");
            return new ArrayList<>('d');
        }
    }

    public void setDoors(ArrayList<Character> doors) {
        this.doors = doors;
    }

    public void addDoor(char door) {
        doors.add(door);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public ArrayList<Body> getDoorHorBodies() {
        return doorHorBodies;
    }

    public ArrayList<Body> getDoorVerBodies() {
        return doorVerBodies;
    }

    public char getType() {
        return type;
    }
}
