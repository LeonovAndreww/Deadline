package com.deadline;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import java.util.Random;

public class MyContactListener implements ContactListener {
    Random random = new Random();

    @Override
    public void beginContact(Contact contact) {

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

        if (bodyA.getUserData()=="projectile" && (bodyB.getUserData()=="wall" || bodyB.getUserData()=="closeDoor" || bodyA.getUserData()=="elevator" || bodyA.getUserData()=="vending" || bodyA.getUserData()=="obstacle")) {
            bodyA.setActive(false);
        }
        else if (bodyB.getUserData()=="projectile" && (bodyA.getUserData()=="wall" || bodyA.getUserData()=="closeDoor" || bodyA.getUserData()=="elevator" || bodyA.getUserData()=="vending" || bodyA.getUserData()=="obstacle")) {
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

        else if ((bodyB.getUserData()=="projectile" || bodyB.getUserData()=="meleeRegionTrue") && bodyA.getUserData()=="ghost") {
            bodyA.setUserData("hit");
        }
        else if ((bodyA.getUserData()=="projectile" || bodyA.getUserData()=="meleeRegionTrue") && bodyB.getUserData()=="ghost") {
            bodyB.setUserData("hit");
        }

        else if ((bodyB.getUserData()=="projectile" || bodyB.getUserData()=="meleeRegionTrue") && bodyA.getUserData()=="zombie") {
            bodyA.setUserData("hit");
        }
        else if ((bodyA.getUserData()=="projectile" || bodyA.getUserData()=="meleeRegionTrue") && bodyB.getUserData()=="zombie") {
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

        else if (bodyA.getUserData()=="coin" && bodyB.getUserData()=="coin") {
//            bodyA.setLinearVelocity(random.nextInt(20)-10, random.nextInt(20)-10);
//            bodyB.setLinearVelocity(random.nextInt(20)-10, random.nextInt(20)-10);
        }

        else if (bodyB.getUserData()=="player" && bodyA.getUserData()=="ghost") {
            bodyB.setUserData("hit1");
        }
        else if (bodyA.getUserData()=="player" && bodyB.getUserData()=="ghost") {
            bodyA.setUserData("hit1");
        }

        else if (bodyB.getUserData()=="player" && bodyA.getUserData()=="zombie") {
            bodyB.setUserData("hit2");
        }
        else if (bodyA.getUserData()=="player" && bodyB.getUserData()=="zombie") {
            bodyA.setUserData("hit2");
        }

        else if (bodyB.getUserData()=="player" && bodyA.getUserData()=="coin") {
            bodyA.setUserData("got");
        }
        else if (bodyA.getUserData()=="player" && bodyB.getUserData()=="coin") {
            bodyB.setUserData("got");
        }

        else if (bodyB.getUserData()=="coin" && bodyA.getUserData()=="ghost") {
            contact.setEnabled(false);
        }
        else if (bodyA.getUserData()=="coin" && bodyB.getUserData()=="ghost") {
            contact.setEnabled(false);
        }

        else if ((bodyB.getUserData()=="ghost" || bodyB.getUserData()=="coin" || bodyB.getUserData()=="zombie") && (bodyB.getUserData()=="zombie" || bodyA.getUserData()=="coin" || bodyA.getUserData()=="ghost")) {
            bodyA.setLinearVelocity(random.nextInt(40)-20, random.nextInt(40)-20);
            bodyB.setLinearVelocity(random.nextInt(40)-20, random.nextInt(40)-20);
        }

        else if (bodyB.getUserData()=="obstacle" && bodyA.getUserData()=="ghost") {
            contact.setEnabled(false);
        }
        else if (bodyA.getUserData()=="obstacle" && bodyB.getUserData()=="ghost") {
            contact.setEnabled(false);
        }

        else if (bodyA.getUserData()=="player" && bodyB.getUserData()=="elevatorOn") {
            bodyA.setUserData("moved");
        }
        else if (bodyB.getUserData()=="player" && bodyA.getUserData()=="elevatorOn") {
            bodyB.setUserData("moved");
        }

        else if (bodyA.getUserData()=="player" && bodyB.getUserData()=="vending") {
            bodyA.setUserData("shopping");
        }
        else if (bodyB.getUserData()=="player" && bodyA.getUserData()=="vending") {
            bodyB.setUserData("shopping");
        }
        else if (bodyA.getUserData()=="shopping" && bodyB.getUserData()!="vending") {
            bodyA.setUserData("player");
        }
        else if (bodyB.getUserData()=="shopping" && bodyA.getUserData()!="vending") {
            bodyB.setUserData("player");
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
