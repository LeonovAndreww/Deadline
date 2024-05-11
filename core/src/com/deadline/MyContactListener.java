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
//        Fixture fixtureA = contact.getFixtureA();
//        Fixture fixtureB = contact.getFixtureB();
//
//        Body bodyA = fixtureA.getBody();
//        Body bodyB = fixtureB.getBody();
//

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

        if (bodyA.getUserData()=="projectile" && (bodyB.getUserData()=="wall" || bodyB.getUserData()=="closeDoor")) {
            bodyA.setActive(false);
            sndPaperBump.play();
        }
        else if (bodyB.getUserData()=="projectile" && (bodyA.getUserData()=="wall" || bodyA.getUserData()=="closeDoor")) {
            bodyB.setActive(false);
            sndPaperBump.play();
        }
        else if (bodyA.getUserData()=="projectile" && bodyB.getUserData()=="openDoor") {
            contact.setEnabled(false);
        }
        else if (bodyB.getUserData()=="projectile" && bodyA.getUserData()=="openDoor") {
            contact.setEnabled(false);
        }

        else if (bodyA.getType() == BodyDef.BodyType.DynamicBody && bodyB.getType() == BodyDef.BodyType.DynamicBody && bodyA.getUserData()=="projectile" && bodyB.getUserData()=="player") {
            contact.setEnabled(false);
        }
        else if (bodyA.getType() == BodyDef.BodyType.DynamicBody && bodyB.getType() == BodyDef.BodyType.DynamicBody && bodyB.getUserData()=="projectile" && bodyA.getUserData()=="player") {
            contact.setEnabled(false);
        }

        else if (bodyA.getType() == BodyDef.BodyType.DynamicBody && bodyB.getType() == BodyDef.BodyType.DynamicBody && bodyB.getUserData()=="projectile" && bodyA.getUserData()=="ghost") {
            bodyA.setUserData("hit");
            sndPaperBump.play();
        }
        else if (bodyA.getType() == BodyDef.BodyType.DynamicBody && bodyB.getType() == BodyDef.BodyType.DynamicBody && bodyA.getUserData()=="projectile" && bodyB.getUserData()=="ghost") {
            bodyB.setUserData("hit");
            sndPaperBump.play();
        }

        else if (bodyA.getType() == BodyDef.BodyType.DynamicBody && bodyB.getType() == BodyDef.BodyType.DynamicBody && bodyB.getUserData()=="projectile" && bodyA.getUserData()=="projectile") {
            contact.setEnabled(false);
        }

        else if (bodyA.getType() == BodyDef.BodyType.DynamicBody && bodyB.getType() == BodyDef.BodyType.StaticBody && bodyA.getUserData()=="player" && bodyB.getUserData()=="openDoor") {
            contact.setEnabled(false);
        }
        else if (bodyA.getType() == BodyDef.BodyType.StaticBody && bodyB.getType() == BodyDef.BodyType.DynamicBody && bodyB.getUserData()=="player" && bodyA.getUserData()=="openDoor") {
            contact.setEnabled(false);
        }


    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
