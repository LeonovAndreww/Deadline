package com.deadline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

public class MyContactListener implements ContactListener {
    private final World world;
    Sound sndPaperBump;


    public MyContactListener(World world) {
        this.world = world;
        sndPaperBump = Gdx.audio.newSound(Gdx.files.internal("paperBump.mp3"));
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();


    }

    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Body bodyA = fixtureA.getBody();
        Body bodyB = fixtureB.getBody();

        if (bodyA.getType() == BodyDef.BodyType.DynamicBody && bodyB.getType() == BodyDef.BodyType.StaticBody && bodyA.getUserData()=="projectile") {
            bodyA.setActive(false);
            world.destroyBody(bodyA);
            sndPaperBump.play();
        }
        else if (bodyA.getType() == BodyDef.BodyType.StaticBody && bodyB.getType() == BodyDef.BodyType.DynamicBody && bodyB.getUserData()=="projectile") {
            bodyB.setActive(false);
            world.destroyBody(bodyB);
            sndPaperBump.play();
        }
        else if (bodyA.getType() == BodyDef.BodyType.DynamicBody && bodyB.getType() == BodyDef.BodyType.DynamicBody && bodyA.getUserData()=="projectile" && bodyB.getUserData()=="player") {
            contact.setEnabled(false);
        }
        else if (bodyA.getType() == BodyDef.BodyType.DynamicBody && bodyB.getType() == BodyDef.BodyType.DynamicBody && bodyB.getUserData()=="projectile" && bodyA.getUserData()=="player") {
            contact.setEnabled(false);
        }

//        if (bodyA.getUserData().equals("player") && bodyB.getUserData().equals("projectile")) {
//            contact.setEnabled(false);
//        } else if (bodyB.getUserData().equals("player") && bodyA.getUserData().equals("projectile")) {
//            contact.setEnabled(false);
//        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
