package com.deadline;

import com.badlogic.gdx.graphics.Texture;

public class Weapon {
    private final float range;
    private final boolean isMelee;
    private final Texture texture;
    private final float reloadTime;

    public Weapon(Texture texture, boolean isMelee, float range,  float reloadTime) {
        this.range = range;
        this.isMelee = isMelee;
        this.texture = texture;
        this.reloadTime = reloadTime;
    }

    public float getRange() {
        return range;
    }

    public boolean isMelee() {
        return isMelee;
    }

    public Texture getTexture() {
        return texture;
    }

    public float getReloadTime() {
        return reloadTime;
    }
}
