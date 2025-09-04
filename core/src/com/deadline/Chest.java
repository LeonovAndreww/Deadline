package com.deadline;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

public class Chest extends AnimatedObstacle {
    int tPhase = 0;
    private boolean opened = false;

    public Chest(World world, float width, float height, float x, float y, int nPhases, long timePhaseInterval) {
        super(world, width, height, x, y, nPhases, timePhaseInterval);
        body.setUserData("chestClosed");
        phase = 0;
        tPhase = 0;
        opened = false;
        timeLastPhase = TimeUtils.millis();
    }

    @Override
    public void changePhase() {
        if (TimeUtils.millis() > timeLastPhase + timePhaseInterval) {
            timeLastPhase = TimeUtils.millis();
            tPhase++;
            if (tPhase >= nPhases) {
                phase = nPhases;
                opened = true;
            } else {
                phase = tPhase;
            }
        }
    }

    public void updateChest(boolean isBattle) {
        if (body != null) {
            Object ud = body.getUserData();
            if ("chestOpen".equals(ud) && !isBattle) {
                changePhase();
            } else {
                if (!"chestOpen".equals(ud)) {
                    tPhase = 0;
                    phase = 0;
                    opened = false;
                    body.setUserData("chestClosed");
                }
            }
        }
    }

    public boolean dropLoot() {
        return opened && phase >= nPhases;
    }
}
