package com.github.rccookie.common.geometry;

import static java.lang.Math.PI;


public final class Physics {

    /**
     * Lowest possible temperature in °C
     */
    public static final double T0 = -273.15;

    /**
     * Atomic mass unit
     */
    public static final double U = 1.660539E-27;

    /**
     * Avogadro constant
     */
    public static final double N_A = 6.0221E23;

    /**
     * Bohr's radius
     */
    public static final double A0 = 5.2917721E-11;

    /**
     * Bolzmann constant
     */
    public static final double K = 1.3807E-23;

    /**
     * Compton wavelength
     */
    public static final double GAMMA_C = 2.426310215E-12;

    /**
     * Elementary charge (charge of an electron)
     */
    public static final double E = 1.6022E-19;

    /**
     * Gravitational acceleration on earth (Europe)
     */
    public static final double G_EARTH = 9.81;

    /**
     * A vector representation of the acceleration on earth
     * @see #G_EARTH
     */
    public static final Vector3D G_EARTH_VEC = new Vector3D(0, 0, -G_EARTH);

    /**
     * Absolute permittivity in vacuum
     */
    public static final double EPSILON_0 = 8.85418782E-12;

    /**
     * Absolute permeability in vacuum
     */
    public static final double MI_0 = 4 * PI * 1E-7;

    /**
     * Gravitation constant
     */
    public static final double G = 6.6743E-11;

    /**
     * Speed of light
     */
    public static final double C = 2.99792458E8;

    /**
     * Molar norm volume of ideal gases
     */
    public static final double V_MN = 22.3232;

    /**
     * Planck constant
     */
    public static final double H = 6.6261E-34;

    /**
     * Reduced planck constant
     */
    public static final double H_RED = H / (4 * PI);

    /**
     * Resting mass of an alpha particle
     */
    public static final double M_ALPHA = 6.6442E-27;

    /**
     * Rydberg frequency for h-atom
     */
    public static final double R_H = 3.28984196E15;

    /**
     * Rydberg constant
     */
    public static final double R_INFITINE = 1.09737316E7;

    /**
     * Solar constant
     */
    public static final double S = 1.367;

    /**
     * Stephan-Boltzmann constant
     */
    public static final double DELTA = 5.6704E-8;

    /**
     * Universal gas constant
     */
    public static final double R = 8.3145;

    /**
     * Wien's displancement constant
     */
    public static final double B = 2.89777E-3;


    /**
     * Mass of an electron
     */
    public static final double M_E = 9.10939E-31;

    /**
     * Mass of a neuron
     */
    public static final double M_N = 1.67493E-27;

    /**
     * Mass of a proton
     */
    public static final double M_P = 1.67262E-27;



    /**
     * Mass of the sun
     */
    public static final double M_SUN = 1.989E30;

    /**
     * Mass of the earth
     */
    public static final double M_EARTH = 5.975E24;





    public static final class Mechanics {

        /**
         * Calculates the traveled distance when moving with the given velocity for the specified time.
         * @param velocity
         * @param time
         * @return The distance traveled
         */
        public static final double distTraveled(double velocity, double time) {
            return velocity * time;
        }

        /**
         * Calculates the traveled distance when moving with the given velocity for the specified time.
         * @param velocity
         * @param time
         * @return The distance traveled
         */
        public static final Vector3D distTraveled(Vector3D velocity, double time) {
            return velocity.scaled(time);
        }

        /**
         * Calculates the average velocity when moving the specified distance in the given amount of time.
         * @param distance
         * @param time
         * @return The average velocity
         */
        public static final double velocity(double distance, double time) {
            return distance / time;
        }

        /**
         * Calculates the average velocity when moving the specified distance in the given amount of time.
         * @param distance
         * @param time
         * @return The average velocity
         */
        public static final Vector3D velocity(Vector3D distance, double time) {
            return distance.divided(time);
        }


        /**
         * Calculates the traveled distance when moving with an accelerating velocity for the specified amount of time.
         * @param acceleration
         * @param time
         * @param velocity0 The initial velocity
         * @return The distance traveled
         */
        public static final double distTraveled(double acceleration, double time, double velocity0) {
            return (acceleration / 2) * time * time + distTraveled(velocity0, time);
        }

        /**
         * Calculates the traveled distance when moving with an accelerating velocity for the specified amount of time.
         * @param acceleration
         * @param time
         * @param velocity0 The initial velocity
         * @return The distance traveled
         */
        public static final Vector3D distTraveled(Vector3D acceleration, double time, Vector3D velocity0) {
            return acceleration.scaled(0.5 * time * time).add(distTraveled(velocity0, time));
        }

        /**
         * Calculates the velocity after accelerating fot the given amount of time
         * @param acceleration
         * @param time
         * @param velocity0 The initial velocity
         * @return The final velocity
         */
        public static final double velocity(double acceleration, double time, double velocity0) {
            return acceleration * time + velocity0;
        }

        /**
         * Calculates the velocity after accelerating fot the given amount of time
         * @param acceleration
         * @param time
         * @param velocity0 The initial velocity
         * @return The final velocity
         */
        public static final Vector3D velocity(Vector3D acceleration, double time, Vector3D velocity0) {
            return acceleration.scaled(time).add(velocity0);
        }



        /**
         * Calculates the angular velocity of an object moving with the specified velocity on a circle
         * @param velocity
         * @param radius
         * @return The angular velocity
         */
        public static final double angularVel(double velocity, double radius) {
            return velocity / (2 * PI * radius);
        }

        /**
         * Calculates the velocity of an object traveling with the given angular velocity.
         * @param angularVelocity
         * @param radius
         * @return The velocity
         */
        public static final double velOnCircle(double angularVelocity, double radius) {
            return angularVelocity * radius;
        }

        /**
         * Calculates the orbital term for the given angular velocity.
         * @param angularVelocity
         * @return The orbital term
         */
        public static final double orbitalTerm(double angularVelocity) {
            return 2 * PI / angularVelocity;
        }

        /**
         * Calculates the revolutions per second for the given orbital term.
         * @param orbitalTerm
         * @return The frequency
         */
        public static final double frequency(double orbitalTerm) {
            return 1 / orbitalTerm;
        }




        /**
         * Calculates the velocity of an object falling for the specified time.
         * @param time
         * @param velocity0 The initial velocity
         * @return The final velocity
         */
        public static final double fallVel(double time, double velocity0) {
            return velocity(-G_EARTH, time, velocity0);
        }

        /**
         * Calculates the velocity of an object falling for the specified time.
         * @param time
         * @param velocity0 The initial velocity
         * @return The final velocity
         */
        public static final Vector3D fallVel(double time, Vector3D velocity0) {
            return velocity(G_EARTH_VEC, time, velocity0);
        }

        /**
         * Calculates the distance traveled by an object falling for the specified amount of time.
         * @param time
         * @param velocity0 The initial velocity
         * @return The final velocity
         */
        public static final double fallDist(double time, double velocity0) {
            return distTraveled(-G_EARTH, time, velocity0);
        }

        /**
         * Calculates the distance traveled by an object falling for the specified amount of time.
         * @param time
         * @param velocity0 The initial velocity
         * @return The final velocity
         */
        public static final Vector3D fallDist(double time, Vector3D velocity0) {
            return distTraveled(G_EARTH_VEC, time, velocity0);
        }

        /**
         * Calculates the time for that an object starting with the given velocity travels upwards.
         * @param velocity0 The initial velocity
         * @return The time the objects rises
         */
        public static final double riseTime(double velocity0) {
            return riseTime(G_EARTH, velocity0);
        }

        /**
         * Calculates the time for that an object starting with the given velocity travels upwards.
         * @param velocity0 The initial velocity
         * @return The time the objects rises
         */
        public static final Vector3D riseTime(Vector3D velocity0) {
            return riseTime(G_EARTH_VEC.inverted(), velocity0);
        }

        /**
         * Calculates the time for that an object starting with the given velocity rises when deccelerated
         * with the specified acceleration.
         * @param decceleration
         * @param velocity0 The initial velocity
         * @return The time the object rises
         */
        public static final double riseTime(double decceleration, double velocity0) {
            return velocity0 / decceleration;
        }

        /**
         * Calculates the time for that an object starting with the given velocity rises when deccelerated
         * with the specified acceleration.
         * @param decceleration
         * @param velocity0 The initial velocity
         * @return The time the object rises
         */
        public static final Vector3D riseTime(Vector3D decceleration, Vector3D velocity0) {
            return new Vector3D(velocity0.x() / decceleration.x(), velocity0.y() / decceleration.y(), velocity0.z() / decceleration.z());
        }

        /**
         * Calculates the distance that an object rises when starting with the given velocity.
         * @param velocity0 The initial velocity
         * @return The rise height
         */
        public static final double riseHeight(double velocity0) {
            return riseHeight(G_EARTH, velocity0);
        }

        /**
         * Calculates the distance that an object rises when starting with the given velocity.
         * @param velocity0 The initial velocity
         * @return The rise height
         */
        public static final Vector3D riseHeight(Vector3D velocity0) {
            return riseHeight(G_EARTH_VEC.inverted(), velocity0);
        }

        /**
         * Calculates the distance that an object rises when starting with the given velocity and being decceletated
         * with the given acceleration.
         * @param decceleration
         * @param velocity0 The initial velocity
         * @return The rise height
         */
        public static final double riseHeight(double decceleration, double velocity0) {
            return velocity0 * velocity0 / (2 * decceleration);
        }

        /**
         * Calculates the distance that an object rises when starting with the given velocity and being decceletated
         * with the given acceleration.
         * @param decceleration
         * @param velocity0 The initial velocity
         * @return The rise height
         */
        public static final Vector3D riseHeight(Vector3D decceleration, Vector3D velocity0) {
            return new Vector3D(
                velocity0.x() * velocity0.x() / (2 * decceleration.x()),
                velocity0.y() * velocity0.y() / (2 * decceleration.y()),
                velocity0.z() * velocity0.z() / (2 * decceleration.z())
            );
        }


        /**
         * Calculates the velocity of an object thrown with a certain velocity after the specfied time while falling down.
         * <p>Acceleration acts onto the z coordinate.
         * @param time
         * @param velocity0 The initial velocity
         * @return The final velocity
         */
        public static final Vector3D throwVel(double time, Vector3D velocity0) {
            return throwVel(-G_EARTH, time, velocity0);
        }

        /**
         * Calculates the velocity of an object thrown with a certain velocity after the specfied time being accelerated
         * the given amount.
         * <p>Acceleration acts onto the z coordinate.
         * @param acceleration
         * @param time
         * @param velocity0 The initial velocity
         * @return The final velocity
         */
        public static final Vector3D throwVel(double acceleration, double time, Vector3D velocity0) {
            return new Vector3D(velocity0.x(), velocity0.y(), velocity(acceleration, time, velocity0.z()));
        }

        /**
         * Calculates the location of an object thrown with a certain velocity ater the specified time while falling down.
         * <p>Acceleration acts onto the z coordinate.
         * @param time
         * @param velocity0 The initial veloctiy
         * @return The location
         */
        public static final Vector3D throwLoc(double time, Vector3D velocity0) {
            return throwLoc(-G_EARTH, time, velocity0);
        }

        /**
         * Calculates the location of an object thrown with a certain velocity ater the specified time being accellerated
         * the given amount.
         * <p>Acceleration acts onto the z coordinate.
         * @param acceleration
         * @param time
         * @param velocity0 The initial veloctiy
         * @return The location
         */
        public static final Vector3D throwLoc(double acceleration, double time, Vector3D velocity0) {
            return new Vector3D(distTraveled(velocity0.x(), time), distTraveled(velocity0.y(), time), distTraveled(acceleration, time, velocity0.z()));
        }





        /**
         * Calculates the force that exists if an object with a certain mass gets constantly accelerated.
         * @param mass
         * @param acceleration
         * @return The force
         */
        public static final Vector3D force(double mass, Vector3D acceleration) {
            return acceleration.scaled(mass);
        }

        /**
         * Calculates the force that exists if an object with a certain mass gets constantly accelerated.
         * @param mass
         * @param acceleration
         * @return The force
         */
        public static final double force(double mass, double acceleration) {
            return mass * acceleration;
        }





        /**
         * Returns the weight of an object with the specified mass on earth.
         * @param mass
         * @return The weight
         */
        public static final double weight(double mass) {
            return G_EARTH * mass;
        }

        /**
         * Returns the weight of an object with the specified mass with the given gravitational acceleration.
         * @param acceleration
         * @param mass
         * @return The weight
         */
        public static final double weight(double acceleration, double mass) {
            return G_EARTH * mass;
        }








        private Mechanics() {}
    }




    public static final class Relativity {

        /**
         * Calculates the lorenz factor for the given velocity.
         * @param velocity
         * @return The lorenz factor
         */
        public static final double lorenzFactor(double velocity) {
            return 1 / Math.sqrt(1 - velocity * velocity / (C * C));
        }

        /**
         * Calculates the lorenz factor for the given velocity.
         * @param velocity
         * @return The lorenz factor
         */
        public static final double lorenzFactor(Vector3D velocity) {
            return lorenzFactor(velocity.abs());
        }

        /**
         * Calculates the time for the second inertial system with the given relative velocity.
         * @param velocity The difference in velocity
         * @param time The time in the first inertial system
         * @return The time in the second inertial system
         */
        public static final double timeDilated(double velocity, double time) {
            return lorenzFactor(velocity) * time;
        }

        /**
         * Calculates the time for the second inertial system with the given relative velocity.
         * @param velocity The difference in velocity
         * @param time The time in the first inertial system
         * @return The time in the second inertial system
         */
        public static final double timeDilated(Vector3D velocity, double time) {
            return lorenzFactor(velocity) * time;
        }

        /**
         * Calculates the length contraction at the given velocity.
         * @param velocity
         * @param length The proper length
         * @return The contracted length
         */
        public static final double lengthContracted(double velocity, double length) {
            return length / lorenzFactor(velocity);
        }

        /**
         * Calculates the length contraction at the given velocity.
         * @param velocity
         * @param length The proper length
         * @return The contracted length
         */
        public static final double lengthContracted(Vector3D velocity, double length) {
            return length / lorenzFactor(velocity);
        }

        /**
         * Calculates the relativistic mass when moving with the given velocity.
         * @param velocity
         * @param mass The resting (proper) mass
         * @return The mass while moving
         */
        public static final double mass(double velocity, double mass) {
            return lorenzFactor(velocity) * mass;
        }

        /**
         * Calculates the relativistic mass when moving with the given velocity.
         * @param velocity
         * @param mass The resting (proper) mass
         * @return The mass while moving
         */
        public static final double mass(Vector3D velocity, double mass) {
            return lorenzFactor(velocity) * mass;
        }

        /**
         * Calculates the relativistic kinetic energy of a mass moving with the given velocity.
         * @param velocity
         * @param mass The resting (proper) mass
         * @return The kinetic energie of the mass
         */
        public static final double kineticEnergy(double velocity, double mass) {
            return (lorenzFactor(velocity) - 1) * mass * C * C;
        }

        /**
         * Calculates the relativistic kinetic energy of a mass moving with the given velocity.
         * @param velocity
         * @param mass The resting (proper) mass
         * @return The kinetic energie of the mass
         */
        public static final double kineticEnergy(Vector3D velocity, double mass) {
            return (lorenzFactor(velocity) - 1) * mass * C * C;
        }





        private Relativity() {}
    }


    public static final class Gravitation {

        /**
         * Calculates the schwarzschild-radius (better known as the 'event horizon') for the given mass.
         * @param mass
         * @return The schwarzschild-radius
         */
        public static final double schwarzschildRadius(double mass) {
            return 2 * G * mass / (C * C);
        }

        /**
         * Calculates the naturally emmitted temperature of the given mass
         * @param mass
         * @return The temperature
         */
        public static final double temperatur(double mass) {
            return H_RED * C * C * C / (8 * PI * K * G * mass);
        }




        private Gravitation() {}
    }





    private Physics() {}
}
