package com.deadline;

import com.badlogic.gdx.physics.box2d.World;

public class Player extends Entity{
    public Player(World world, float width, float height, float x, float y, int nPhases, long timePhaseInterval) {
        super(world, width, height, x, y, nPhases, timePhaseInterval);
    }
}
