package com.github.rccookie.greenfoot.game.physics2d;

import com.github.rccookie.common.geometry.Ray2D;
import com.github.rccookie.common.geometry.Vector;
import com.github.rccookie.common.geometry.Vector2D;
import com.github.rccookie.greenfoot.game.raycast.Raycast;

public class Corner {

    private Vector location;
    private Vector movement;
    // Rotation not needed because it's just a point

    private double mass = 1;

    private final CollidingActor host;



    public Corner(CollidingActor host) {
        this(host, new Vector2D());
    }

    public Corner(CollidingActor host, Vector location) {
        this(host, location, new Vector2D());
    }

    public Corner(CollidingActor host, Vector location, Vector movement) {
        this.location = location;
        this.movement = movement;
        this.host = host;
    }



    public void update(double deltaTime) {
        applyForces(deltaTime);
        handleMovement(deltaTime);
    }

    private void applyForces(double deltaTime) {

    }

    private void handleMovement(double deltaTime) {
        Vector targetMove = movement.scaled(deltaTime);
        final Vector collisionNorm;
        switch (CollisionDetection.current()) {
            case CONTINUOUS: {
                Raycast raycast = Raycast.raycast(new Ray2D(location.get2D(), targetMove.get2D()), host.getWorld(), CollidingActor.class, targetMove.abs(), host);
                if(!raycast.collided) {
                    location.add(targetMove);
                    collisionNorm = null;
                    break;
                }

                location.set(raycast.hitLoc);

                collisionNorm = raycast.raw.hitEdge.edge().rotated(-90);
                if(Vector2D.smallestAngle(targetMove.get2D(), collisionNorm.get2D()) > 90) collisionNorm.invert();

                break;
            }
            case DISTINCT: {
                Vector initialLoc = new Vector2D(location);
                location.add(targetMove);
                if(host.getWorld().getObjectsAt((int)location.x(), (int)location.y(), CollidingActor.class).size() == 0) {
                    collisionNorm = null;
                    break;
                }
                Raycast raycast = Raycast.raycast(new Ray2D(initialLoc.get2D(), targetMove.get2D()), host.getWorld(), CollidingActor.class, targetMove.abs(), host);
                location.set(raycast.hitLoc);

                collisionNorm = raycast.raw.hitEdge.edge().rotated(-90);
                if(Vector2D.smallestAngle(targetMove.get2D(), collisionNorm.get2D()) > 90) collisionNorm.invert();

                break;
            }
            default: throw new RuntimeException("Unexpected collision detection: " + CollisionDetection.current().toString());
        }

        if(collisionNorm == null) return;
        if(collisionNorm != null) return;
    }

    @SuppressWarnings("unused")
    private boolean intersects() {
        
        return false;
    }



    public Vector getLocation() {
        return location;
    }

    public Vector getMovement() {
        return movement;
    }

    public double getMass() {
        return mass;
    }

    public void setLocation(Vector location) {
        this.location = location;
    }

    public void setMovement(Vector movement) {
        this.movement = movement;
    }

    public void setMass(double mass) {
        if(mass <= 0) throw new IllegalArgumentException("Mass cannot be set to " + mass);
        this.mass = mass;
    }
}
