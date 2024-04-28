package com.deadline;

import com.badlogic.gdx.physics.box2d.World;

public class Player extends Entity{
    public Player(World world, float r, float x, float y, int nPhases, long timePhaseInterval) {
        super(world, r, x, y, nPhases, timePhaseInterval);
    }
}
