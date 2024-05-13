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

import java.util.Random;

public class MyContactListener implements ContactListener {
    private final World world;
    Random random = new Random();


    public MyContactListener(World world) {
        this.world = world;
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
        }
        else if (bodyB.getUserData()=="projectile" && (bodyA.getUserData()=="wall" || bodyA.getUserData()=="closeDoor")) {
            bodyB.setActive(false);
        }
        else if (bodyA.getUserData()=="projectile" && bodyB.getUserData()=="openDoor") {
            contact.setEnabled(false);
        }
        else if (bodyB.getUserData()=="projectile" && bodyA.getUserData()=="openDoor") {
            contact.setEnabled(false);
        }

        else if (bodyA.getUserData()=="projectile" && bodyB.getUserData()=="player") {
            contact.setEnabled(false);
        }
        else if (bodyB.getUserData()=="projectile" && bodyA.getUserData()=="player") {
            contact.setEnabled(false);
        }

        else if (bodyB.getUserData()=="projectile" && bodyA.getUserData()=="ghost") {
//            bodyB.setActive(false);
            bodyA.setUserData("hit");
        }
        else if (bodyA.getUserData()=="projectile" && bodyB.getUserData()=="ghost") {
//            bodyA.setActive(false);
            bodyB.setUserData("hit");
        }

        else if (bodyB.getUserData()=="projectile" && bodyA.getUserData()=="projectile") {
            contact.setEnabled(false);
        }

        else if (bodyA.getUserData()=="player" && bodyB.getUserData()=="openDoor") {
            contact.setEnabled(false);
        }
        else if (bodyB.getUserData()=="player" && bodyA.getUserData()=="openDoor") {
            contact.setEnabled(false);
        }

        else if (bodyB.getUserData()=="player" && bodyA.getUserData()=="coin") {
            bodyA.setActive(false);
        }
        else if (bodyA.getUserData()=="player" && bodyB.getUserData()=="coin") {
            bodyB.setActive(false);
        }

        else if (bodyA.getUserData()=="coin" && bodyB.getUserData()=="coin") {
            bodyA.setLinearVelocity(random.nextInt(20)-10, random.nextInt(20)-10);
            bodyB.setLinearVelocity(random.nextInt(20)-10, random.nextInt(20)-10);
        }

        else if (bodyB.getUserData()=="player" && bodyA.getUserData()=="ghost") {
            bodyB.setUserData("hit");
        }
        else if (bodyA.getUserData()=="player" && bodyB.getUserData()=="ghost") {
            bodyA.setUserData("hit");
        }

        else if (bodyB.getUserData()=="ghost" && bodyA.getUserData()=="ghost") {
//            contact.setEnabled(false);
            bodyA.setLinearVelocity(random.nextInt(20)-10, random.nextInt(20)-10);
            bodyB.setLinearVelocity(random.nextInt(20)-10, random.nextInt(20)-10);
        }


    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
