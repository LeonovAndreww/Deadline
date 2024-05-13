package com.deadline;

import com.badlogic.gdx.graphics.Texture;

public class Weapon {
    private Texture texture;
    private float speed;
    private final long duration;
    private final float reloadTime;
    private final int damage;
    private boolean isMelee;

    public Weapon(Texture texture, float speed, long duration, float reloadTime, int damage) {
        this.texture = texture;
        this.speed = speed;
        this.duration = duration;
        this.reloadTime = reloadTime;
        this.damage = damage;
        isMelee = false;
    }

    public Weapon (long duration, float reloadTime, int damage) {
        this.duration = duration;
        this.reloadTime = reloadTime;
        this.damage = damage;
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

    public int getDamage() {
        return damage;
    }

    public void dispose(){
        texture.dispose();
    }
}
