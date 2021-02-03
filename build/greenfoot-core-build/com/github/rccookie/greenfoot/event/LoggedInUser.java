package com.github.rccookie.greenfoot.event;

import com.github.rccookie.greenfoot.event.LoginManager.InternalPasswordChangeException;
import com.github.rccookie.greenfoot.event.LoginManager.LoginException;

/**
 * Represents the login state of a logged in user using {@link LoginManager}.
 * Logged in users can only be created using {@link LoginPage}.
 */
public class LoggedInUser {

    /**
     * The login page that was logged in from.
     */
    private final LoginPage login;

    /**
     * Creates a new logged in user from the given login page making use of
     * its login manager.
     */
    LoggedInUser(LoginPage login) {
        this.login = login;
    }

    /**
     * Returns the currently logged in user. This will not return {@code null}
     * 
     * @return The currently logged in user
     */
    public User getUser() {
        return login.manager.getUser();
    }

    /**
     * Returns weather the currently logged in user is using a guest account.
     * 
     * @return {@code true} if a guest is currently logged in
     */
    public boolean isGuest() {
        return login.manager.isGuest();
    }

    /**
     * Trys to register the currently on Greenfoot logged in user for the
     * account system.
     * 
     * @param password0 The password for the account
     * @param password1 The password for the account, repeated
     * @return This logged in user
     * @throws LoginException If the user is already registered, or the
     *                        passwords do not match each other or the password
     *                        policy. Also passing the same instance of a
     *                        string is not allowed
     * @throws InternalPasswordChangeException If an exception occured during the
     *                                   saving of the password
     */
    public LoggedInUser register(String password0, String password1) throws LoginException, InternalPasswordChangeException {
        login.manager.register(password0, password1);
        return this;
    }

    /**
     * Trys to reset the currently on Greenfoot logged in user's password for
     * the account system.
     * 
     * @param password0 The password for the account
     * @param password1 The password for the account, repeated
     * @return This logged in user
     * @throws LoginException If the user is not yet registered, or the
     *                        passwords do not match each other or the password
     *                        policy. Also passing the same instance of a
     *                        string is not allowed
     * @throws InternalPasswordChangeException If an exception occured during the
     *                                   saving of the password
     */
    public LoggedInUser resetPassword(String password0, String password1) throws LoginException, InternalPasswordChangeException {
        login.manager.resetPassword(password0, password1);
        return this;
    }

    /**
     * Logs out the current user.
     * 
     * @return The login page that this user initially logged in from
     */
    public LoginPage logout() {
        login.manager.logout();
        return login;
    }
}
