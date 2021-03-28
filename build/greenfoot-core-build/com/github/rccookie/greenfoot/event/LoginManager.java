package com.github.rccookie.greenfoot.event;

import java.util.Objects;
import java.util.Optional;

import com.github.rccookie.greenfoot.core.User;

/**
 * The login manager handles user accounts. If allowes users to create an
 * account using their existing Greenfoot account but with an independent
 * password. The user can then also log in from another user's account or
 * without being logged in on Greenfoot at all. Users can also use a guest
 * account to log in without creating an account.
 */
public final class LoginManager {

    /**
     * Part of the password policy. Passwords are required to be in this
     * dimension of length.
     */
    public static final int MIN_PASSWORD_LENGTH = 6, MAX_PASSWORD_LENGTH = 30;



    /**
     * Weather there is currently someone logged in.
     */
    private boolean loggedIn = false;

    /**
     * The user that is currently logged in. {@code null} indicates that
     * either a guest is logged in or the user is logged out (see 
     * {@link #loggedIn}).
     */
    private User user = null;



    /**
     * Creates a new LoginManager with no user logged in by default.
     */
    public LoginManager() { }



    /**
     * Logs in as a guest user.
     * 
     * @return A UserInfo representing a guest account, a 'virtual' UserInfo instance,
     *         but not {@code null}
     * @throws AlreadyLoggedInException If a user is already logged in
     */
    public User loginGuest() throws AlreadyLoggedInException {
        if(loggedIn) throw new AlreadyLoggedInException("Cannot log in when already logged in");

        User guest = User.createGuest();

        loginUser(guest);

        return guest;
    }

    /**
     * Trys to log in with the given username and password.
     * 
     * @param username The name of the user to log in as. Equals the username of the
     *                 corresponding Greenfoot account
     * @param password The password for the account with the specified username
     * @return The UserInfo object of the now logged in user, will not be {@code null}
     * @throws LoginException If username or password are missing or incorrect
     * @throws AlreadyLoggedInException If a user is already logged in
     */
    public User loginUser(String username, String password) throws LoginException, AlreadyLoggedInException {

        if(loggedIn) throw new AlreadyLoggedInException("Cannot log in when already logged in");

        if(username == null || username.length() == 0) {
            if(password == null || password.length() == 0) throw new InputMissingException("Username and password are missing");
            else throw new InputMissingException("Username is missing");
        }
        else if(password == null || password.length() == 0) throw new InputMissingException("Password is missing");

        final User target;
        User currentUser = User.current().get();
        if(currentUser != null && Objects.equals(username, currentUser.getName())) {
            target = currentUser;
        }
        else target = User.find(username).orElse(null);

        if(target != null && getPasswordData(target) == getPasswordHash(password)) {
            loginUser(target);
            return target;
        }
        throw new UsernameOrPasswordIncorrectException();
    }

    /**
     * Internally logs in the given user
     * 
     * @param user The user to log in
     */
    private void loginUser(final User user) {
        this.user = user;
        loggedIn = true;
    }



    /**
     * Logs out the current user.
     * 
     * @throws NotLoggedInException If no user was logged in
     */
    public void logout() throws NotLoggedInException {
        if(!loggedIn) throw new NotLoggedInException("Cannot log out because no user is logged in");
        this.user = null;
        loggedIn = false;
    }



    /**
     * Trys to register the currently on Greenfoot logged in user for the
     * account system.
     * 
     * @param password0 The password for the account
     * @param password1 The password for the account, repeated
     * @return {@code true} if the user is definetly registered. {@code false}
     *         however does not neccecarily mean that the registery failed, it
     *         only means that {@link UserInfo#store()} returned {@code false}
     *         which has been proven to not be very reliable
     * @throws LoginException If the user is already registered, or the
     *                        passwords do not match each other or the password
     *                        policy. Also passing the same instance of a
     *                        string is not allowed
     * @throws InternalPasswordChangeException If an exception occured during the
     *                                   saving of the password
     */
    public boolean register(String password0, String password1) throws LoginException, InternalPasswordChangeException {
        User user = User.current().orElseThrow(() -> new NullPointerException("Can only register if the user is logged in on Greenfoot"));

        if(getUserState() != UserState.CAN_REGISTER) throw new AlreadyRegisteredException(user);

        boolean saveSuccess = setPassword(password0, password1);
        return saveSuccess;
    }



    /**
     * Trys to reset the <b>currently on Greenfoot logged in</b> user's password for
     * the account system.
     * 
     * @param password0 The new password
     * @param password1 The new password, repeated
     * @return {@code true} if the password was definetly saved. {@code false}
     *         however does not neccecarily mean that the registery failed, it
     *         only means that {@link UserInfo#store()} returned {@code false}
     *         which has been proven to not be very reliable
     * @throws LoginException If the user is not yet registered, or the
     *                        passwords do not match each other or the password
     *                        policy. Also passing the same instance of a
     *                        string is not allowed
     * @throws InternalPasswordChangeException If an exception occured during the
     *                                   saving of the password
     */
    public boolean resetPassword(String password0, String password1) throws LoginException, InternalPasswordChangeException {
        User user = User.current().orElseThrow(() -> new NullPointerException("Can only reset password if the user is logged in on Greenfoot"));

        if(getUserState() != UserState.REGISTERED) throw new NotRegisteredException(user);

        return setPassword(password0, password1);
    }



    /**
     * Trys to set the currently on Greenfoot logged in user's password for
     * the account system.
     * 
     * @param password0 The password for the account
     * @param password1 The password for the account, repeated
     * @return {@code true} if the password was definetly saved. {@code false}
     *         however does not neccecarily mean that the registery failed, it
     *         only means that {@link UserInfo#store()} returned {@code false}
     *         which has been proven to not be very reliable
     * @throws LoginException If the passwords do not match each other or the
     *                        password policy. Also passing the same instance
     *                        of a string is not allowed
     * @throws InternalPasswordChangeException If an exception occured during the
     *                                   saving of the password
     */
    private boolean setPassword(String password0, String password1) throws LoginException, InternalPasswordChangeException {
        User user = User.current().orElseThrow(() -> new NullPointerException("Can only change password if the user is logged in on Greenfoot"));

        if(password0 == null || password0.length() == 0 || password1 == null || password1.length() == 0) {
            throw new InputMissingException("Both passwords are required");
        }

        if(!Objects.equals(password0, password1)) throw new DifferentPasswordsException();

        validatePassword(password0);

        try {
            setPasswordData(user, getPasswordHash(password0));
            return user.store();
        } catch(Exception e) {
            throw new InternalPasswordChangeException();
        }
    }



    /**
     * Returns the currently logged in user. This will not return {@code null}
     * 
     * @return The currently logged in user
     * @throws NotLoggedInException If no user is logged in
     */
    public User getUser() throws NotLoggedInException {
        if(!loggedIn) throw new NotLoggedInException("Cannot request the current user if none is logged in");
        return user;
    }

    /**
     * Returns weather the currently logged in user is using a guest account.
     * 
     * @return {@code true} if a guest is currently logged in
     * @throws NotLoggedInException If no user is currently logged in
     */
    public boolean isGuest() throws NotLoggedInException {
        return getUser().getRank() < 0;
    }

    /**
     * Returns weather a user is currently logged in. This also includes guest accounts.
     * 
     * @return {@code true} if a user is currenly logged in
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Returns the current state of the on greenfoot logged in user.
     * 
     * @return The greenfoot user's state
     */
    public static UserState getUserState() {
        final Optional<User> current = User.current();
        if(current.isPresent()) return getPasswordData(current.get()) != 0 ? UserState.REGISTERED : UserState.CAN_REGISTER;
        return UserState.GUEST;
    }



    /**
     * Returns a hash for the given password
     * 
     * @param password The password to hash
     * @return The hash for the specified password
     */
    private static final int getPasswordHash(String password) {
        Objects.requireNonNull(password);
        int hash = password.length() + 1;
        for(int i=0; i<password.toCharArray().length; i++) {
            hash += (i+1) * password.charAt(i);
        }
        return hash;
    }

    /**
     * Returns the saved, hashed password of the given user. {@code 0}
     * indicates that the user has never set a password.
     * 
     * @param user The user to get the password data for
     * @return The hashed password of the given user
     */
    private static final int getPasswordData(User user) {
        Objects.requireNonNull(user);
        return user.getInt(User.NUM_INTS - 1);
    }

    /**
     * Sets the password data of the given user to the specified one.
     * This does <b>not</b> include storing it!
     * 
     * @param user The user to store the data for
     * @param data The data to store
     */
    private static final void setPasswordData(User user, int data) {
        user.setInt(User.NUM_INTS - 1, data);
    }

    /**
     * Validates the given password using the password policy.
     * 
     * @param password The password to validate
     * @throws InvalidPasswordDeclarationException If the password is invalid
     */
    private static final void validatePassword(String password) throws InvalidPasswordDeclarationException {
        if(password.length() < MIN_PASSWORD_LENGTH) throw new InvalidPasswordDeclarationException("The password must at least contain " + MIN_PASSWORD_LENGTH + " characters");
        if(password.length() > MAX_PASSWORD_LENGTH) throw new InvalidPasswordDeclarationException("The password must not contain more than " + MAX_PASSWORD_LENGTH + " characters");
    }



    /**
     * Represents the different states of the current Greenfoot user.
     */
    public enum UserState {
        /**
         * The current Greenfoot user is not logged in on the Greenfoot website
         * and cannot create or manage a user account. He can however log in
         * with username and password.
         */
        GUEST,
        /**
         * The current Greenfoot user is logged in on the Greenfoot website, but
         * does not have an account on the login system.
         */
        CAN_REGISTER,
        /**
         * The current Greenfoot user is logged in on the Greenfoot website and
         * does have an account on the ligin system.
         */
        REGISTERED
    }



    /**
     * Indicates that the user needs to be logged in but is not in for the current
     * operation.
     */
    public static final class NotLoggedInException extends IllegalStateException {

        private static final long serialVersionUID = -5641583592795871017L;

        private NotLoggedInException(String message) {
            super(message);
        }
    }

    /**
     * Indicates that the user needs to be logged out but is in for the current
     * operation.
     */
    public static final class AlreadyLoggedInException extends IllegalStateException {

        private static final long serialVersionUID = -5641583592795871018L;

        private AlreadyLoggedInException(String message) {
            super(message);
        }
    }



    /**
     * Indicating an exception that caused any login operation to fail.
     */
    public static abstract class LoginException extends Exception {

        private static final long serialVersionUID = 1396015744393703251L;

        private LoginException(String message) {
            super(message);
        }
    }

    /**
     * Indicates that some input is completly missing.
     */
    public static final class InputMissingException extends LoginException {

        private static final long serialVersionUID = -1708627956941282433L;

        private InputMissingException(String message) {
            super(message);
        }
    }

    /**
     * Indicates that username or password for a login operation was incorrect.
     */
    public static final class UsernameOrPasswordIncorrectException extends LoginException {

        private static final long serialVersionUID = -1708627956941282434L;

        private UsernameOrPasswordIncorrectException() {
            super("Username or password incorrect");
        }
    }

    /**
     * Indicates that the two entered passwords are not equal.
     */
    public static final class DifferentPasswordsException extends LoginException {

        private static final long serialVersionUID = -3550793950006859780L;

        private DifferentPasswordsException() {
            super("The passwords must not differ");
        }
    }

    /**
     * Indicates that the current Greenfoot user is already registered but should not be
     * for the current operation.
     */
    public static final class AlreadyRegisteredException extends LoginException {

        private static final long serialVersionUID = -3550793950006859781L;

        private AlreadyRegisteredException(User user) {
            super("Cannot register because the user '" + user.getName() + "' is already registered");
        }
    }

    /**
     * Indicates that the current Greenfoot user is not yet registered but should be
     * for the current operation.
     */
    public static final class NotRegisteredException extends LoginException {

        private static final long serialVersionUID = -3550793950006859782L;

        private NotRegisteredException(User user) {
            super("The user '" + user.getName() + "' is not registered yet");
        }
    }

    /**
     * Indicates that a password does not conform the password policy.
     */
    public static final class InvalidPasswordDeclarationException extends LoginException {

        private static final long serialVersionUID = -3550793950006859781L;

        private InvalidPasswordDeclarationException(String message) {
            super(message);
        }
    }

    /**
     * Indicates that an exception prevented proper execution of the operation that is
     * not caused by the calling instance but by some internal exception.
     */
    public static class InternalPasswordChangeException extends Exception {

        private static final long serialVersionUID = 1778555534138265826L;

        private InternalPasswordChangeException() {
            super("Something went wrong during registery. Try again later");
        }
    }
}
