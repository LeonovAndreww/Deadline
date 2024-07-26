package com.deadline;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;


public class Chest extends AnimatedObstacle {
    int tPhase = 0;
    public Chest(World world, float width, float height, float x, float y, int nPhases, long timePhaseInterval) {
        super(world, width, height, x, y, nPhases, timePhaseInterval);
        body.setUserData("chestClosed");
    }

    public void changePhase(){
        tPhase++;
        if (tPhase>=5) phase=5;
        if(TimeUtils.millis() > timeLastPhase+timePhaseInterval) {
            timeLastPhase = TimeUtils.millis();
        }
    }

    public void updateChest(boolean isBattle) {
        if (body!=null) {
            if (body.getUserData() == "chestOpen" && isBattle) {
                changePhase();
            }
            else {
                body.setUserData("chestClosed");
            }
        }
    }

    public boolean dropLoot() {
        return tPhase>nPhases+4;
    }
}
