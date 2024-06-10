package com.deadline;

import com.badlogic.gdx.graphics.Texture;

public class Weapon {
    private Texture texture;
    private String name;
    private float speed;
    private final long duration;
    private final float reloadTime;
    private final int damage;
    private boolean isMelee;

    public Weapon(Texture texture, String name, float speed, long duration, float reloadTime, int damage) {
        this.texture = texture;
        this.name = name;
        this.speed = speed;
        this.duration = duration;
        this.reloadTime = reloadTime;
        this.damage = damage;
        isMelee = false;
    }

    public Weapon (Texture texture, String name, long duration, float reloadTime, int damage) {
        this.texture = texture;
        this.name = name;
        this.duration = duration;
        this.reloadTime = reloadTime;
        this.damage = damage;
    }

    public Weapon (String name, long duration, float reloadTime, int damage) {
        this.duration = duration;
        this.reloadTime = reloadTime;
        this.damage = damage;
    }

    public Texture getTexture() {
        return texture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
