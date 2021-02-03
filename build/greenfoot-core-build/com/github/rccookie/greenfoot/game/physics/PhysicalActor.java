package com.github.rccookie.greenfoot.game.physics;

import greenfoot.GreenfootImage;
import com.github.rccookie.common.geometry.Transform2D;
import com.github.rccookie.common.geometry.Vector2D;
import com.github.rccookie.greenfoot.core.CoreActor;

/**
 * The physical actor is a type of actor that has physical properties like a density, drag, bounce and inertia
 * and collides with any collider objects. It is a powerful base for any game objects that are meant to be functioning
 * physically.
 * <p>Extend from this to give your objects physics and implement custom behavior.
 * 
 * @author RcCookie
 * @version 2.0
 */
public abstract class PhysicalActor extends CoreActor {

    /**
     * A vector representing the acceleration by gravity.
     */
    public static final Vector2D GRAVITY = new Vector2D(0, 9.81);

    /**
     * The length of one meter, in pixels.
     */
    public static double UNIT = 10;
    
    /**
     * The scale of the wind resistance.
     */
    public static final double CW_SCALE = .0001;



    /**
     * Every actor will collide with the edges of the world according to this value. Change it to adjust the default behavior.
     */
    public static boolean DEFAULT_BORDER_COLLISION = false;

    /**
     * The minimum grip, no matter how fast the object is sliding.
     */
    static final double MIN_GRIP = 0.3;

    /**
     * The factor that effects how quick grip is lost when sliding.
     */
	static final double SLIDE_DELAY = 8;
    



    /**
     * Effects weather physics like movement and collisions are calculated for this actor.
     * <p>This is {@code true} by default.
     */
    public boolean simulated = true;

    /**
     * Effects weather the actor bounces of world borders.
     * <p>The default value is {@code DEFAULT_BORDER_COLLISION}.
     */
    public boolean collideWithBorder = DEFAULT_BORDER_COLLISION;

    /**
     * The scale of the affect of gravity for this object. 1 is normal gravity, 0 is no gravity, negavive values will make objects
     * "fall up".
     * <p>For top-down games this should be 0, for side-view games with gravity this should be positive.
     */
    public double gravityScale = 1;

    /**
     * The density of this actor. The density corresponds to the mass of each pixel of the collider or, if no used, of the actors image.
     * Because of that the mass scales with the size of the object.
     */
    public double density;

    /**
     * The drag coefficient (wind resistance) of this object. 0 as value will disable wind resistance.
     */
    public double cw;

    /**
     * The drag on the ground of this object.
     * <p>For games with gravity this should be 0.
     */
    public double groundDrag;

    /**
     * The bounciness of the object. if the value is 1, the object will bounce back as quick as it collided. Values smaller
     * than 1 will lead to some momentum being absorbed during collision. Values above 1 and below 0 are not reccomended.
     * <p>The defualt value is 0.6.
     */
    public double bounce = .6;
    




    /**
     * The movement of this actor in x and y direction and the rotational speed.
     * <p>The movement describes the speed in units per second. This means that the object - when not effected in any way - 
     * will move exactly the current speed scaled by the current unit size pixels in one second, independent of:
     * <ul>
     * <li>The current framerate
     * <li>The current mass
     * <li>Changes in framerate
     * </ul>
     */
    private Transform2D movement;
    











    //Constructors




    /**
     * Constructs a new physical actor with a density of 1 and without gravity effecting it.
     */
    public PhysicalActor(){
        this(1);
    }

    /**
     * Constructs a new physical actor with the specified density and without gravity effecting it.
     * 
     * @param density The density of the actor
     */
    public PhysicalActor(double density){
        this(density, 0.5, 0.5, 0);
    }

    /**
     * Constructs a new physical actor with the specified density and the given gravity effect.
     * 
     * @param density The density of the actor
     * @param gravityScale The effect of gravity on this actor
     */
    public PhysicalActor(double density, double gravityScale){
        this(density, 0.5, gravityScale==1 ? 0.5 : 0, gravityScale);
    }

    /**
     * Constructs a new physical actor with the given physical properties.
     * 
     * @param density The density of the actor
     * @param cw The drag coefficient of the actor (wind resistance)
     * @param groundDrag The ground drag coefficient of the actor
     * @param gravityScale The effect of gravity on this actor
     */
    public PhysicalActor(double density, double cw, double groundDrag, double gravityScale){
        this.density = density;
        this.cw = cw;
        this.groundDrag = groundDrag;
        this.gravityScale = gravityScale;
        movement = new Transform2D();
    }
    
    

















    //Implementation of inertia and regular forces



    /**
     * Runs the physics of the actor to make it physical if the simulation is enabled.
     * <p>DO NOT OVERRIDE if you don't know what you're doing! This will disable any regular physics like inertia and similar!
     */
    protected void physicsUpdate() {
        super.physicsUpdate();
        if(!simulated) return;

        applyGravity();
        applyAirDrag();
        applyRegularForces();
        //Has to be last to be able to stop movement completly
        applyGroundDrag();
        
        //actually moving
        inertia(movement);
        collisionPhysics();
    }

    /**
     * Applies gravity to the actor.
     * <p>Override this method to change the behavior of gravity.
     */
    protected void applyGravity() {
        fixedAccelerate(GRAVITY.scaled(gravityScale));
    }

    /**
     * Applies air drag to the actor.
     * <p>Override this method to change the behavior of air drag.
     */
    protected void applyAirDrag(){
        addFixedForce(Vector2D.angledVector(getMovementDirection() + 180, cw * CW_SCALE * getSpeed() * getSpeed()));
    }

    /**
     * Applies ground drag to the actor. The drag is actually an acceleration rather than a force because heavier objects create more friction.
     * <p>Override this method to change the behavior of ground drag.
     */
    protected void applyGroundDrag(){
        double drag = groundDrag * gripLevel(velocity());
        fixedAccelerate(Vector2D.angledVector(getMovementDirection() + 180, drag));
    }

    /**
     * Calculates a value between {@code MIN_GRIP} and 1 that describes the amount of grip the object currently has. On higher sliding speeds the
     * grip will become lower.
     * 
     * @param slide The sliding speed of the object
     * @return The grip level
     * @see #MIN_GRIP
     * @see #SLIDE_DELAY
     */
	public static double gripLevel(Vector2D slide){
		return (1 - MIN_GRIP) * (1 / Math.exp(Math.pow(slide.abs() / SLIDE_DELAY, 2))) + MIN_GRIP;
	}

    /**
     * Executed once per frame, this method is intendet to be overridden to apply forces or accelerations regularly.
     * <p>The default Implementation does nothing.
     */
    protected void applyRegularForces(){}


    /**
     * Moves the actor the current amount of inertia.
     */
    protected void inertia(Transform2D movement){
        fixedMove(new Transform2D(movement).scale(UNIT));
    }













    //Collisions


    /**
     * Manages collisions with other objects and with the world borders.
     */
    protected void collisionPhysics() {
        collisions();
        worldCollisions();
    }
    
    /**
     * Calculates collisions
     */
    protected void collisions() {

        PhysicalActor hit = (PhysicalActor)getOneIntersectingObject(PhysicalActor.class);
        
        //The direction of the collidion pointing at the other collider
        Vector2D vCollisionNorm = new Vector2D(hit.getLocation()).add(getLocation().inverted()).normed();
        
        /*Collider myCollider = getCollider();
        if(myCollider instanceof BoxCollider && collider instanceof CircleCollider){
            vCollisionNorm = BoxCollider.getCollNorm((BoxCollider)myCollider, (CircleCollider)collider).invert();
        }
        else if(myCollider instanceof CircleCollider && collider instanceof BoxCollider){
            vCollisionNorm = BoxCollider.getCollNorm((BoxCollider)collider, (CircleCollider)myCollider);
        }
        else if(myCollider instanceof BoxCollider && collider instanceof BoxCollider){*/
            /*vCollisionNorm = Vector.angledVector(
                (myCollider.getEdgeTowards(collider).angle() + collider.getEdgeTowards(myCollider).angle()) / 2,
                1
            );
        }*/
        //The speed difference between the two objects
        Vector2D vRelativeVelocity = new Vector2D(velocity()).add(hit.velocity().inverted());
        
        double speed = Vector2D.dot(vRelativeVelocity, vCollisionNorm);
        
        if(speed < 0) return;
        
        double impuls = (1 + (bounce + hit.bounce) / 2) * speed / (mass() + hit.mass());
        
        movement.location.add(new Vector2D(vCollisionNorm).scale(-impuls * hit.mass()));
        hit.movement.location.add(new Vector2D(vCollisionNorm).scale(impuls * mass()));
        /*}
        else{
            Vector2D edge = collider.getEdgeTowards(getCollider());
            for(int i=0; i<100&&collider.intersects(getCollider()); i++)
                move(movement.location.inverted().normed());
            movement.location = reflect(edge, movement.location, bounce);
        }*/
    }

    /**
     * A helper method that calculates a reflected vector that takes bounce into consideration when reflecting.
     * 
     * @param base The base vector to reflect from
     * @param reflect The vector to be reflected
     * @param bounce The amount of bounce the reflection should have
     * @return The reflected vector
     */
    protected static Vector2D reflect(Vector2D base, Vector2D reflect, double bounce){
        Vector2D reflected = Vector2D.reflect(base, reflect);
        Vector2D out = Vector2D.project(reflected, base);
        out.add(Vector2D.project(reflected, base.rotated(90)).scale(bounce));
        return out;
    }

    /**
     * Calculates collisions with world border.
     */
    private void worldCollisions(){
        /*if(!collideWithBorder) return;
        if(getCollider() != null && getCollider().getWorldEdge() != null){
            Vector2D edge = getCollider().getWorldEdge();
            movement.location = reflect(edge, movement.location, bounce);
            for(int i=0; i<100&&edge.equals(getCollider().getWorldEdge()); i++){
                move(movement.location.normed());
                if(i == 99) move(movement.location.normed().invert().scale(100));
            }
        }*/
    }










    
    















    
    
    //Modifying movement


    //One-time appliements
    

    /**
     * Overrides the movement of this object.
     * <p>The object WILL NOT be moved instantly. This will simply change the inertia of the object. The movement
     * represents the movement of the object per second.
     * <p>Special cases:
     * <ul>
     * <li>Any type of the argument that is {@code null} will be ignored.
     * <li>Any type of the argument that has the value of NaN will be ignored.
     * </ul>
     * 
     * @param newMovement The new movement of this object
     * @see #movement
     */
    public void setMovement(Transform2D newMovement){
        if(newMovement == null) return;
        if(newMovement.location == null) newMovement.location = getMovement().location;

        if(Double.isNaN(newMovement.location.x())) newMovement.location.setX(getMovement().location.x());
        if(Double.isNaN(newMovement.location.y())) newMovement.location.setY(getMovement().location.y());
        if(Double.isNaN(newMovement.rotation)) newMovement.rotation = getMovement().rotation;

        movement = newMovement;
    }


    /**
     * Overrides the velocity of this object. This does not effect its spin.
     * 
     * @param newVelocity The new velocity for the object it units per seconds
     * @see #setMovement(Transform2D)
     */
    public void setVelocity(Vector2D newVelocity){
        setMovement(new Transform2D(newVelocity, getAngularSpeed()));
    }


    /**
     * Overrides the spinning speed of the object. This will not effect its velocity.
     * 
     * @param newRotationalSpeed The new rotational speed for the object in degrees per second
     * @see #setMovement(Transform2D)
     */
    public void setSpin(double newRotationalSpeed){
        setMovement(new Transform2D(velocity(), newRotationalSpeed));
    }


    /**
     * Adds both a velocity and a rotational speed onto the objects current movement.
     * 
     * @param addedMovement The movement that should be added
     * @see #setMovement(Transform2D)
     */
    public void addMovement(Transform2D addedMovement){
        Transform2D newMovement = getMovement();
        newMovement.add(addedMovement);
        setMovement(newMovement);
    }


    /**
     * Adds a velocity onto the objects current movement.
     * 
     * @param addedVelocity The velocity that should be added
     * @see #addMovement(Transform2D)
     */
    public void addVelocity(Vector2D addedVelocity){
        addMovement(new Transform2D(addedVelocity));
    }


    /**
     * Adds a rotational speed onto the objects current movement.
     * <p>This means that a rotational speed of 1 (°) will - independent of the mass of the object - make the object
     * spin 1° per second clockwise faster.
     * <p>Positive values rotate the object clockwise, negative values counterclockwise.
     * 
     * @param addedRotationalSpeed The rotational speed that should be added(in degrees per second)
     * @see #addMovement(Transform2D)
     */
    public void addSpin(double addedRotationalSpeed){
        addMovement(new Transform2D(addedRotationalSpeed));
    }



    /**
     * Puts a force onto the object at its center of mass. This means the 'created' momentum is always the
     * same, however, heavier objects will be accelerated less.
     * <p>Generally speaking, a force with a magnitude of 1 put onto an object with the mass 1 (for example a square with the edge length of a unit 
     * and the density 1) will be 1 unit per second faster in the direction of the force.
     * 
     * @param addedForce The force that should be added
     * @see #addVelocity(Vector2D)
     */
    public void addForce(Vector2D addedForce){
        addVelocity(addedForce.divided(mass()));
    }


    /**
     * Puts a force onto the actor at the specified location.
     * <p>This will make the actor move the specified velocity divided through its mass faster at the given location. This added velocity will partially be created by actually accelerating the
     * object, and partially by turning it. The further away from the center of mass the force is applied, the more of it will turn the object. It DOES NOT matter if the
     * location at which the force is added is actually inside of the boundaries of this object.
     * <p><b>Example:</b> 
     * <li>A stationary object with an square image with an edge length of a unit is at the location (0|0). The force [1|0] is put onto the object at the
     * location (0|-0.5) (The center of the upper edge of the square). Half of the force will accelerate the object, making it move with a velocity of [0.5|0]
     * (units per second). The other half of the force will spin the object with a speed of 22.5° per second. After 1 second, the objects will have traveled the
     * given force resulting in an acceleration of [1|0] at the location (0|-0.5).
     * <p>The location is given in world coordinates. Use {@code worldLoc(Vector)} to generate a vector from one that is relative to the objects location and rotation.
     * 
     * @param forceLocation The location at which the force should be applied, in world coordinates
     * @param force The force to put onto the object
     * @see #worldLocOf(Vector2D)
     * @see #addForce(Vector2D)
     * @see #addAccAt(Vector2D, Vector2D)
     */
    public void addForceAt(Vector2D forceLocation, Vector2D force){
        addAccAt(forceLocation, force.divide(mass()));
    }


    /**
     * Accelerates the object at its center of mass.
     * <p>This means that - independent of the mass of the object, an object accelerated with an acceleration of the magnitude 1 will be moving 1 unit
     * per second faster in the direction of the acceleration.
     * <p>This does actually exactly the same as {@code addVelocity(Vector)}.
     * 
     * @param acceleration The added acceleration
     * @see #addVelocity(Vector2D)
     */
    public void accelerate(Vector2D acceleration){
        addVelocity(acceleration);
    }


    /**
     * Accelerates the actor at the specified location.
     * <p>This will make the actor move the specified velocity faster at the given location. This added velocity will partially be created by actually accelerating the
     * object, and partially by turning it. The further away from the center of mass the force is applied, the more of it will turn the object. It DOES NOT matter if the
     * location at which the force is added is actually inside of the boundaries of this object.
     * <p><b>Example:</b> 
     * <li>A stationary object with an square image with an edge length of a unit is at the location (0|0). The accelaration [1|0] is put onto the object at the
     * location (0|-0.5) (The center of the upper edge of the square). Half of the acceleration will accelerate the object, making it move with a velocity of [0.5|0]
     * (units per second). The other half of the acceleration will spin the object with a speed of 22.5° per second. After 1 second, the objects will have traveled the
     * given acceleration of [1|0] at the location (0|-0.5).
     * <p>The location is given in world coordinates. Use {@code worldLoc(Vector)} to generate a vector from one that is relative to the objects location and rotation.
     * 
     * @param forceLocation The location at which the force should be applied, in world coordinates
     * @param acceleration The acceleration for the object
     * @see #worldLocOf(Vector2D)
     */
    public void addAccAt(Vector2D forceLocation, Vector2D acceleration){
        Vector2D overallAcc = acceleration;
        Vector2D applyLocToCenter = Vector2D.between(forceLocation, getLocation());
        double lengthOfMass = (getImage().getWidth() + getImage().getHeight()) / 2;

        double directlyAppliedForcePercentage = applyLocToCenter.abs() / lengthOfMass;
        double restForcePercentage = 1 - directlyAppliedForcePercentage;

        Vector2D directlyAppliedForce = overallAcc.scaled(directlyAppliedForcePercentage);
        Vector2D restForce = overallAcc.scaled(restForcePercentage);


        Vector2D additionalMovement = Vector2D.project(restForce, applyLocToCenter);
        Vector2D spinMovement = Vector2D.project(restForce, applyLocToCenter.rotated(90));


        Vector2D spinTarget = forceLocation.added(spinMovement);
        double spin = Vector2D.angleBetween(Vector2D.between(spinTarget, getLocation()), applyLocToCenter);

        if(spin > 180) spin -= 360;

        addVelocity(directlyAppliedForce);
        addVelocity(additionalMovement);
        addSpin(spin);
    }


    /**
     * Puts a torque onto the object. This makes the object spin.
     * <p>Generally speaking, a torque of 1 put onto an object with the mass 1 (for example a square with the edge length of a unit 
     * and the density 1) will make it spin 1° per second faster clockwise.
     * <p>Positive values rotate the object clockwise, negative values counterclockwise.
     * 
     * @param addedTorque Teh torque that should be added
     * @see #addSpin(double)
     */
    public void addTorque(double addedTorque){
        addSpin(addedTorque / mass());
    }







    //Regular appliements


    /**
     * Adds both a velocity and a rotational speed onto the objects current movement.
     * <p>Executing this each frame will have the exactly same overall effect as executing {@code addMovement(Transform)} once per second with
     * the same argument. However, this will have a smoother, constant effect.
     * 
     * @param addedMovement The movement that should be added
     * @see #addMovement(Transform2D)
     */
    public void addFixedMovement(Transform2D addedMovement){
        Transform2D newMovement = getMovement();
        newMovement.add(new Transform2D(addedMovement).scale(time.deltaTime()));
        setMovement(newMovement);
    }


    /**
     * Adds a velocity onto the objects current movement.
     * <p>Executing this each frame will have the exactly same overall effect as executing {@code addVelocity(Transform)} once per second with
     * the same argument. However, this will have a smoother, constant effect.
     * 
     * @param addedVelocity The velocity that should be added
     * @see #adFixeddMovement(Transform2D)
     */
    public void addFixedVelocity(Vector2D addedVelocity){
        addFixedMovement(new Transform2D(addedVelocity));
    }


    /**
     * Adds a rotational speed onto the objects current movement.
     * <p>This means that a rotational speed of 1 (°) will - independent of the mass of the object - make the object
     * spin 1° per second clockwise faster.
     * <p>Positive values rotate the object clockwise, negative values counterclockwise.
     * <p>Executing this each frame will have the exactly same overall effect as executing {@code addVelocity(Transform)} once per second with
     * the same argument. However, this will have a smoother, constant effect.
     * 
     * @param addedRotationalSpeed The rotational speed that should be added(in degrees per second)
     * @see #addFixedMovement(Transform2D)
     */
    public void addFixedSpin(double addedRotationalSpeed){
        addFixedMovement(new Transform2D(addedRotationalSpeed));
    }



    /**
     * Puts a force onto the object at its center of mass. This means the 'created' momentum is always the
     * same, however, heavier objects will be accelerated less.
     * <p>Generally speaking, a force with a magnitude of 1 put onto an object with the mass 1 (for example a square with the edge length of a unit 
     * and the density 1) will be 1 unit per second faster in the direction of the force.
     * 
     * @param addedForce The force that should be added
     * @see #addFixedVelocity(Vector2D)
     */
    public void addFixedForce(Vector2D addedForce){
        addFixedVelocity(addedForce.divided(mass()));
    }


    /**
     * Puts a force onto the actor at the specified location.
     * <p>This will make the actor move the specified velocity divided through its mass faster at the given location. This added velocity will partially be created by actually accelerating the
     * object, and partially by turning it. The further away from the center of mass the force is applied, the more of it will turn the object. It DOES NOT matter if the
     * location at which the force is added is actually inside of the boundaries of this object.
     * <p><b>Example:</b> 
     * <li>A stationary object with an square image with an edge length of a unit is at the location (0|0). The force [1|0] is put onto the object at the
     * location (0|-0.5) (The center of the upper edge of the square). Half of the force will accelerate the object, making it move with a velocity of [0.5|0]
     * (units per second). The other half of the force will spin the object with a speed of 22.5° per second. After 1 second, the objects will have traveled the
     * given force resulting in an acceleration of [1|0] at the location (0|-0.5).
     * <p>The location is given in world coordinates. Use {@code worldLoc(Vector)} to generate a vector from one that is relative to the objects location and rotation.
     * 
     * @param forceLocation The location at which the force should be applied, in world coordinates
     * @param force The force to put onto the object
     * @see #worldLocOf(Vector2D)
     * @see #addFixedForce(Vector2D)
     * @see #addForceAt(Vector2D, Vector2D)
     */
    public void addFixedForceAt(Vector2D forceLocation, Vector2D force){
        addAccAt(forceLocation, force.divided(mass()).scale(time.deltaTime()));
    }


    /**
     * Accelerates the object at its center of mass.
     * <p>This means that - independent of the mass of the object, an object accelerated with an acceleration of the magnitude 1 will be moving 1 unit
     * per second faster in the direction of the acceleration.
     * <p>This does actually exactly the same as {@code addFixedVelocity(Vector)}.
     * 
     * @param acceleration The added acceleration
     * @see #addFixedVelocity(Vector2D)
     */
    public void fixedAccelerate(Vector2D acceleration){
        addFixedVelocity(acceleration);
    }


    /**
     * Accelerates the actor at the specified location.
     * <p>This will make the actor move the specified velocity faster at the given location. This added velocity will partially be created by actually accelerating the
     * object, and partially by turning it. The further away from the center of mass the force is applied, the more of it will turn the object. It DOES NOT matter if the
     * location at which the force is added is actually inside of the boundaries of this object.
     * <p><b>Example:</b> 
     * <li>A stationary object with an square image with an edge length of a unit is at the location (0|0). The accelaration [1|0] is put onto the object at the
     * location (0|-0.5) (The center of the upper edge of the square). Half of the acceleration will accelerate the object, making it move with a velocity of [0.5|0]
     * (units per second). The other half of the acceleration will spin the object with a speed of 22.5° per second. After 1 second, the objects will have traveled the
     * given acceleration of [1|0] at the location (0|-0.5).
     * <p>The location is given in world coordinates. Use {@code worldLoc(Vector)} to generate a vector from one that is relative to the objects location and rotation.
     * 
     * @param forceLocation The location at which the force should be applied, in world coordinates
     * @param acceleration The acceleration for the object
     * @see #addAccAt(Vector2D)
     * @see #fixedAccelerate(Vector2D)
     */
    public void addFixedAccAt(Vector2D loc, Vector2D overallAcc){
        addAccAt(loc, overallAcc.scaled(time.deltaTime()));
    }


    /**
     * Puts a torque onto the object. This makes the object spin.
     * <p>Generally speaking, a torque of 1 put onto an object with the mass 1 (for example a square with the edge length of a unit 
     * and the density 1) will make it spin 1° per second faster clockwise.
     * <p>Positive values rotate the object clockwise, negative values counterclockwise.
     * 
     * @param addedTorque Teh torque that should be added
     * @see #addFixedSpin(double)
     */
    public void addFixedTorque(double addedTorque){
        addFixedSpin(addedTorque / mass());
    }

    
    














    //Getters


    /**
     * Calculates the current momentum of the actor. This takes both movement speed and mass into consideration. The higher the mass,
     * the higher the momentum.
     * <p>Generally speaking, a object moving with a speed of 1 and with the mass 1 (for example a square with the edge length of a unit 
     * and the density 1) has a momentum of 1.
     * 
     * @return The actors momentum
     */
    public Vector2D momentum(){
        return new Vector2D(movement.location).scale(mass());
    }

    /**
     * Calculates the mass of the actor. The mass is based on the colliders/actors size mutiplied by the actors density. The result
     * is divided by the square of the unit length.
     * <p>This means that an object has the same mass, even if it is scaled up (image size and the unit size): an square object with the edge length of a
     * unit and a density of 1 will always have a mass of 1, no matter what value {@code UNIT} actually has.
     * 
     * @return The actors mass
     */
    public double mass(){
        return density * getImage().getWidth() * getImage().getHeight() / Math.pow(UNIT, 2);
    }


    /**
     * Returns the current movement speed of the actor in units per second.
     * 
     * @return The actors speed
     */
    public double getSpeed(){
        return velocity().abs();
    }


    /**
     * Returns the rotating speed of the actor in degrees per second.
     * 
     * @return The actors angular speed
     */
    public double getAngularSpeed(){
        return movement.rotation;
    }


    /**
     * Returns the actors current velocity as a vector in units per second.
     * 
     * @return The actors velocity
     */
    public Vector2D velocity(){
        return new Vector2D(movement.location);
    }


    /**
     * Returns the actors current movement (both velocity and angular speed).
     * 
     * @return The actors movement
     */
    public Transform2D getMovement(){
        return new Transform2D(movement);
    }


    /**
     * Returns the angle the actor is currently moving in degrees between 0 and 360 (exclusive).
     * 
     * @return The actors movement direction
     */
    public double getMovementDirection(){
        return velocity().angle();
    }

    















    //Other stuff




    /**
     * Convertes the given location that is relative to the objects location and rotation into a vector
     * that points at the same point in world coordinates.
     * 
     * @param relativeLoc The location relative to the object
     * @return The location relative to the world
     */
    public Vector2D worldLocOf(Vector2D relativeLoc){
        return getLocation().add(Vector2D.angledVector(getAngle() + relativeLoc.angle(), relativeLoc.abs()));
    }


    /**
     * Set the image for this actor to the specified image. If the default collider object is used, the scale of that collider will be set to the new bounds.
     * <p><b>Warning:</b> If no collider is used, this will effect the mass of the object!
     * 
     * @param image The new image
    */
    @Override
    public void setImage(GreenfootImage image){
        super.setImage(image);
    }
}