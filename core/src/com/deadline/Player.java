package com.deadline;

import com.badlogic.gdx.physics.box2d.World;

public class Player extends Entity{
    private boolean isBattle = false;
    public Player(World world, float width, float height, float x, float y, int nPhases, long timePhaseInterval) {
        super(world, width, height, x, y, nPhases, timePhaseInterval);
    }

    void changeBattleState(boolean isBattle) {
        this.isBattle = isBattle;
        if (this.isBattle) super.setSpeed(75f);
        else super.setSpeed(50f);
    }

    public boolean getBattleState() {
        return isBattle;
    }
}
