package com.deadline;

import com.badlogic.gdx.physics.box2d.World;

public class Vending extends Obstacle {
    public Vending(World world, float width, float height, float x, float y) {
        super(world, width, height, x, y);
        body.setUserData("vending");
    }
}
