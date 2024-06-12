package com.deadline;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

public class AnimatedObstacle extends Obstacle {
    protected int phase, nPhases;
    protected long timeLastPhase, timePhaseInterval;

    public AnimatedObstacle(World world, float width, float height, float x, float y, int nPhases, long timePhaseInterval) {
        super(world, width, height, x, y);
        phase = 0;
        this.nPhases = nPhases;
        this.timePhaseInterval = timePhaseInterval; // except running animation!
        body.setUserData("animatedObstacle");
    }

    public void changePhase(){
        if(TimeUtils.millis() > timeLastPhase+timePhaseInterval) {
            if (++phase == nPhases) phase = 0;
            timeLastPhase = TimeUtils.millis();
        }
    }

    public int getPhase() {
        return phase;
    }
}
