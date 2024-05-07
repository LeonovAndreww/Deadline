package com.deadline;

import com.badlogic.gdx.physics.box2d.World;

public class Ghost extends Entity{
    private final World world;
    private boolean isBattle = false;
    private Weapon weapon;
    private int health, maxHealth;
    private long timeLastAttack;

    public Ghost(World world, float width, float height, float x, float y, int maxHealth, int nPhases, long timePhaseInterval) {
        super(world, width, height, x, y, maxHealth, nPhases, timePhaseInterval);
        this.world = world;
        getBody().setUserData("ghost");
    }
}
