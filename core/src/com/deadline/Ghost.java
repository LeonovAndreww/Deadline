package com.deadline;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

public class Ghost extends Entity{
    private final World world;
    private long timePhaseInterval;
    private boolean isBattle = false;
    private Weapon weapon;
    private int health, maxHealth;
    private long timeLastAttack, timeLastPhase;
    private int phase, nPhases;
    private int room;

    public Ghost(World world, float width, float height, float x, float y, int maxHealth, int nPhases, long timePhaseInterval, Weapon weapon) {
        super(world, width, height, x, y, maxHealth, nPhases, timePhaseInterval);
        this.world = world;
        this.nPhases = nPhases;
        this.timePhaseInterval = timePhaseInterval;
        this.weapon = weapon;
        phase = 0;
        getBody().setUserData("ghost");
    }

    @Override
    public void changePhase() {
        if (getBody().isAwake()) {
            long interval = timePhaseInterval;
            if (TimeUtils.millis() > timeLastPhase + interval) {
                if (++phase == nPhases) phase = 0;
                timeLastPhase = TimeUtils.millis();
            }
        }
    }

    public void update(int damage) {
        if (isAlive()) {
            if (getBody().getUserData()=="hit") {
                hit(damage);
                getBody().setUserData("ghost");
            }
        }
    }

    public void attack(float playerX, float playerY) {

    }

    public int getRoom() {
        return room;
    }

    public void setRoomNum(int room) {
        this.room = room;
    }
}
