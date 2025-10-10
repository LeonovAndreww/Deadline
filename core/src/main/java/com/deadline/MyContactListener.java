package com.deadline;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import java.util.Random;

public class MyContactListener implements ContactListener {
    private final ScreenGame screen;
    Random random = new Random();

    public MyContactListener(ScreenGame screen) {
        this.screen = screen;
    }

    private boolean isUD(Body b, String s) {
        if (b == null) return false;
        Object ud = b.getUserData();
        return s.equals(ud);
    }

    private boolean isPlayer(Body b) {
        if (b == null) return false;
        return b.getUserData() instanceof Player;
    }

    private boolean isMobileType(Body b) {
        return isUD(b, "ghost") || isUD(b, "coin") || isUD(b, "zombie");
    }

    @Override
    public void beginContact(Contact contact) { }

    @Override
    public void endContact(Contact contact) { }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        final Fixture fixtureA = contact.getFixtureA();
        final Fixture fixtureB = contact.getFixtureB();

        final Body bodyA = fixtureA.getBody();
        final Body bodyB = fixtureB.getBody();

        if ((isUD(bodyA, "projectile") || isUD(bodyA, "projectileWarden")) && (isUD(bodyB, "wall") || isUD(bodyB, "closeDoor") || isUD(bodyB, "elevatorOn") || isUD(bodyB, "elevatorOff") || isUD(bodyB, "vending") || isUD(bodyB, "obstacle")  || isUD(bodyB, "animatedObstacle" ) || isUD(bodyB, "chestOpen") || isUD(bodyB, "chestClosed"))) {
            screen.scheduleAction(() -> bodyA.setActive(false));
        }
        else if ((isUD(bodyB, "projectile") || isUD(bodyB, "projectileWarden")) && (isUD(bodyA, "wall") || isUD(bodyA, "closeDoor") || isUD(bodyA, "elevatorOn") || isUD(bodyA, "elevatorOff") || isUD(bodyA, "vending") || isUD(bodyA, "obstacle") || isUD(bodyA, "animatedObstacle") || isUD(bodyA, "chestOpen") || isUD(bodyA, "chestClosed"))) {
            screen.scheduleAction(() -> bodyB.setActive(false));
        }
        else if (isUD(bodyA, "projectile") && isUD(bodyB, "openDoor")) {
            contact.setEnabled(false);
        }
        else if (isUD(bodyB, "projectile") && isUD(bodyA, "openDoor")) {
            contact.setEnabled(false);
        }

        else if (isUD(bodyA, "projectile") && isPlayer(bodyB)) {
            contact.setEnabled(false);
        }
        else if (isUD(bodyB, "projectile") && isPlayer(bodyA)) {
            contact.setEnabled(false);
        }

        else if (isUD(bodyB, "projectile") && isUD(bodyA, "ghost")) {
            screen.scheduleAction(() -> bodyA.setUserData("hit"));
        }
        else if (isUD(bodyA, "projectile") && isUD(bodyB, "ghost")) {
            screen.scheduleAction(() -> bodyB.setUserData("hit"));
        }

        else if (isUD(bodyB, "projectile") && isUD(bodyA, "zombie")) {
            screen.scheduleAction(() -> bodyA.setUserData("hit"));
        }
        else if (isUD(bodyA, "projectile") && isUD(bodyB, "zombie")) {
            screen.scheduleAction(() -> bodyB.setUserData("hit"));
        }

        else if (isUD(bodyB, "projectile") && isUD(bodyA, "warden")) {
            screen.scheduleAction(() -> bodyA.setUserData("hit"));
        }
        else if (isUD(bodyA, "projectile") && isUD(bodyB, "warden")) {
            screen.scheduleAction(() -> bodyB.setUserData("hit"));
        }

        else if (isUD(bodyB, "projectile") && isUD(bodyA, "projectile")) {
            contact.setEnabled(false);
        }

        else if (isUD(bodyB, "projectileWarden") && (isUD(bodyA, "projectile") || isUD(bodyA, "projectileWarden") || isUD(bodyA, "obstacle") || isUD(bodyA, "animatedObstacle"))) {
            contact.setEnabled(false);
        }
        else if (isUD(bodyA, "projectileWarden") && (isUD(bodyB, "projectile") || isUD(bodyB, "projectileWarden") || isUD(bodyB, "obstacle") || isUD(bodyB, "animatedObstacle"))) {
            contact.setEnabled(false);
        }

        else if (isPlayer(bodyA) && isUD(bodyB, "openDoor")) {
            contact.setEnabled(false);
        }
        else if (isPlayer(bodyB) && isUD(bodyA, "openDoor")) {
            contact.setEnabled(false);
        }

        else if (isPlayer(bodyB) && isUD(bodyA, "ghost")) {
            Player p = (Player) bodyB.getUserData();
            screen.scheduleAction(() -> p.receiveDamage(1));

        }
        else if (isPlayer(bodyA) && isUD(bodyB, "ghost")) {
            Player p = (Player) bodyA.getUserData();
            screen.scheduleAction(() -> p.receiveDamage(1));
        }

        else if (isPlayer(bodyB) && isUD(bodyA, "zombie")) {
            Player p = (Player) bodyB.getUserData();
            screen.scheduleAction(() -> p.receiveDamage(2));
        }
        else if (isPlayer(bodyA) && isUD(bodyB, "zombie")) {
            Player p = (Player) bodyA.getUserData();
            screen.scheduleAction(() -> p.receiveDamage(2));
        }

        else if (isPlayer(bodyB) && isUD(bodyA, "projectileWarden")) {
            Player p = (Player) bodyB.getUserData();
            screen.scheduleAction(() -> p.receiveDamage(2));
        }
        else if (isPlayer(bodyA) && isUD(bodyB, "projectileWarden")) {
            Player p = (Player) bodyA.getUserData();
            screen.scheduleAction(() -> p.receiveDamage(2));
        }

        else if (isPlayer(bodyB) && isUD(bodyA, "coin")) {
            screen.scheduleAction(() -> bodyA.setUserData("got"));
        }
        else if (isPlayer(bodyA) && isUD(bodyB, "coin")) {
            screen.scheduleAction(() -> bodyB.setUserData("got"));
        }

        else if (isPlayer(bodyA) && isUD(bodyB, "chestClosed")) {
            Player p = (Player) bodyA.getUserData();
            if (!p.isBattle) screen.scheduleAction(() -> bodyB.setUserData("chestOpen"));
        }
        else if (isPlayer(bodyB) && isUD(bodyA, "chestClosed")) {
            Player p = (Player) bodyB.getUserData();
            if (!p.isBattle) screen.scheduleAction(() -> bodyA.setUserData("chestOpen"));
        }

        else if (isUD(bodyB, "coin") && isUD(bodyA, "ghost")) {
            contact.setEnabled(false);
        }
        else if (isUD(bodyA, "coin") && isUD(bodyB, "ghost")) {
            contact.setEnabled(false);
        }

        else if (isUD(bodyB, "zombie") && isUD(bodyA, "ghost")) {
            contact.setEnabled(false);
        }
        else if (isUD(bodyA, "zombie") && isUD(bodyB, "ghost")) {
            contact.setEnabled(false);
        }

        else if (isUD(bodyB, "warden") && isUD(bodyA, "ghost")) {
            contact.setEnabled(false);
        }
        else if (isUD(bodyA, "warden") && isUD(bodyB, "ghost")) {
            contact.setEnabled(false);
        }

        else if (isUD(bodyB, "projectileWarden") && isUD(bodyA, "ghost")) {
            contact.setEnabled(false);
        }
        else if (isUD(bodyA, "projectileWarden") && isUD(bodyB, "ghost")) {
            contact.setEnabled(false);
        }

        else if (isMobileType(bodyA) && isMobileType(bodyB)) {
            final float vxA = random.nextInt(40)-20;
            final float vyA = random.nextInt(40)-20;
            final float vxB = random.nextInt(40)-20;
            final float vyB = random.nextInt(40)-20;
            screen.scheduleAction(() -> {
                bodyA.setLinearVelocity(vxA, vyA);
                bodyB.setLinearVelocity(vxB, vyB);
            });
        }

        else if ((isUD(bodyB, "obstacle") || isUD(bodyB, "animatedObstacle") || isUD(bodyB, "chestClosed") || isUD(bodyB, "chestOpen")) && (isUD(bodyA, "ghost") || isUD(bodyA, "warden"))) {
            contact.setEnabled(false);
        }
        else if ((isUD(bodyA, "obstacle") || isUD(bodyA, "animatedObstacle") || isUD(bodyA, "chestClosed") || isUD(bodyA, "chestOpen")) && (isUD(bodyB, "ghost") || isUD(bodyB, "warden"))) {
            contact.setEnabled(false);
        }

        else if (isPlayer(bodyA) && isUD(bodyB, "elevatorOn")) {
            Player p = (Player) bodyA.getUserData();
            screen.scheduleAction(() -> p.setMoved(true));
        }
        else if (isPlayer(bodyB) && isUD(bodyA, "elevatorOn")) {
            Player p = (Player) bodyB.getUserData();
            screen.scheduleAction(() -> p.setMoved(true));
        }

        else if (isPlayer(bodyA) && isUD(bodyB, "vending")) {
            Player p = (Player) bodyA.getUserData();
            screen.scheduleAction(() -> p.setShopping(true));
        }
        else if (isPlayer(bodyB) && isUD(bodyA, "vending")) {
            Player p = (Player) bodyB.getUserData();
            screen.scheduleAction(() -> p.setShopping(true));
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) { }
}
