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
    private ArrayList<Character> doors = new ArrayList<>();
    private ArrayList<Room> rooms = new ArrayList<>();

    public Room(World world, float x, float y, float width, float height, ArrayList<Character> doorDir, ArrayList<Room> rooms) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // door check
        if ((doorDir.contains('u') && isRoom(this.x, this.y + this.height, rooms)) || getRoomDir(this.x, this.y, rooms).contains('d'))
            this.doors.add('u');
        if ((doorDir.contains('d') && isRoom(this.x, this.y - this.height, rooms)) || getRoomDir(this.x, this.y, rooms).contains('u'))
            this.doors.add('d');
        if ((doorDir.contains('l') && isRoom(this.x, this.y - this.width, rooms)) || getRoomDir(this.x, this.y, rooms).contains('r'))
            this.doors.add('l');
        if ((doorDir.contains('r') && isRoom(this.x, this.y + this.width, rooms)) || getRoomDir(this.x, this.y, rooms).contains('l'))
            this.doors.add('r');

        // door create
        if (doors.contains('u')) createDoubleWall(this.x, this.y, this.width, 10, this.width/4);
        else createWall(this.x, this.y, this.width, 10);

        if (doors.contains('d')) createDoubleWall(this.x, this.y, 10, this.height, this.height/4);
        else createWall(this.x, this.y, 10, this.height);

        if (doors.contains('l')) createDoubleWall(this.width + this.x, this.y, 10, this.height, this.height/4);
        else createWall(this.width + this.x, this.y, 10, this.height);

        if (doors.contains('r')) createDoubleWall(this.x, this.height + this.y, this.width, 10, this.width/4);
        else createWall(this.x, this.height + this.y, this.width, 10);
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

    private void createWall(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x + width / 2, y + height / 2);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    private void createDoubleWall(float x, float y, float wallWidth, float wallHeight, float slitWidth) {
        createWall(x, y, wallWidth - slitWidth / 2, wallHeight);
        createWall(x + wallWidth + slitWidth, y, wallWidth - slitWidth / 2, wallHeight);
    }

    public ArrayList<Character> getDoors() {
        return doors;
    }

}
