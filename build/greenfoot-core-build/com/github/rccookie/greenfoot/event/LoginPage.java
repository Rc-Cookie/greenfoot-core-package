package com.github.rccookie.greenfoot.event;

import com.github.rccookie.greenfoot.event.LoginManager.InternalPasswordChangeException;
import com.github.rccookie.greenfoot.event.LoginManager.LoginException;

/**
 * Represents an effective login page using {@link LoginManager}.
 */
public class LoginPage {

    /**
     * The login page's login manager.
     */
    final LoginManager manager = new LoginManager();
    
    /**
     * Creates a new login page.
     */
    public LoginPage() { }

    /**
     * Trys to register the currently on Greenfoot logged in user for the
     * account system.
     * 
     * @param password0 The password for the account
     * @param password1 The password for the account, repeated
     * @return This login page
     * @throws LoginException If the user is already registered, or the
     *                        passwords do not match each other or the password
     *                        policy. Also passing the same instance of a
     *                        string is not allowed
     * @throws InternalPasswordChangeException If an exception occured during the
     *                                   saving of the password
     */
    public LoginPage register(String password0, String password1) throws LoginException, InternalPasswordChangeException {
        manager.register(password0, password1);
        return this;
    }

    /**
     * Trys to reset the currently on Greenfoot logged in user's password for
     * the account system.
     * 
     * @param password0 The password for the account
     * @param password1 The password for the account, repeated
     * @return This login page
     * @throws LoginException If the user is not yet registered, or the
     *                        passwords do not match each other or the password
     *                        policy. Also passing the same instance of a
     *                        string is not allowed
     * @throws InternalPasswordChangeException If an exception occured during the
     *                                   saving of the password
     */
    public LoginPage resetPassword(String password0, String password1) throws LoginException, InternalPasswordChangeException {
        manager.resetPassword(password0, password1);
        return this;
    }

    /**
     * Trys to log in with the given username and password.
     * 
     * @param username The name of the user to log in as. Equals the username of the
     *                 corresponding Greenfoot account
     * @param password The password for the account with the specified username
     * @return The logged in user
     * @throws LoginException If username or password are missing or incorrect
     */
    public LoggedInUser loginUser(String username, String password) throws LoginException {
        manager.loginUser(username, password);
        return new LoggedInUser(this);
    }

    /**
     * Logs in as a guest.
     * 
     * @return The logged in (guest) user
     */
    public LoggedInUser loginGuest() {
        manager.loginGuest();
        return new LoggedInUser(this);
    }
}
