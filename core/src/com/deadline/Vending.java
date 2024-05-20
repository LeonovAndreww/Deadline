package com.deadline;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class Vending extends Obstacle{
    private World world;
    private Body body;
    private int room;
    private float x, y;
    private float width, height;
    private int imgNumber;

    public Vending(World world, float width, float height, float x, float y) {
        super(world, width, height, x, y);
        getBody().setUserData("vending");
    }
}
