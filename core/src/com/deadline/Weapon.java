package com.deadline;

import com.badlogic.gdx.graphics.Texture;

public class Weapon {
    private final float speed;
    private long duration;
    private final boolean isMelee;
    private final Texture texture;
    private final float reloadTime;

    public Weapon(Texture texture, boolean isMelee, float speed, long duration, float reloadTime) {
        this.texture = texture;
        this.isMelee = isMelee;
        this.speed = speed;
        this.duration = duration;
        this.reloadTime = reloadTime;
    }

    public Texture getTexture() {
        return texture;
    }

    public boolean isMelee() {
        return isMelee;
    }

    public float getSpeed() {
        return speed;
    }

    public long getDuration() {
        return duration;
    }

    public float getReloadTime() {
        return reloadTime;
    }

    public void dispose(){
        texture.dispose();
    }
}