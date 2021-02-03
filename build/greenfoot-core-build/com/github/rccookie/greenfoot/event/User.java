package com.github.rccookie.greenfoot.event;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import greenfoot.GreenfootImage;
import greenfoot.UserInfo;
import greenfoot.UserInfoVisitor;
import greenfoot.util.GreenfootUtil;

/**
 * A wrapper class for the {@link UserInfo} class that allowes to create virtual
 * users and contains some conveniance methods.
 */
public class User {

    /**
     * The number of avaliable integer slots for {@link #getInt(int)} and
     * {@link #setInt(int, int)}.
     */
    public static final int NUM_INTS = UserInfo.NUM_INTS;

    /**
     * The number of available string slots for {@link #getString(int)} and
     * {@link #setString(int, String)}.
     */
    public static final int NUM_STRINGS = UserInfo.NUM_STRINGS;

    /**
     * The maximum length for strings to store using {@link #setString(int, String)}.
     */
    public static final int STRING_LENGTH_LIMIT = UserInfo.STRING_LENGTH_LIMIT;



    /**
     * The UserInfo that backs this User.
     */
    private final UserInfo userInfo;

    /**
     * Weather this user is marked as virtual and therefore does not actually store
     * its data.
     */
    private final boolean virtual;



    /**
     * Weather any values were modified since the last store.
     */
    private boolean modified = true;



    /**
     * Creates a new user from the given UserInfo object.
     * 
     * @param userInfo The UserInfo that should back this user, must not be {@code null}
     * @param virtual Weather this user should be marked as virtual
     */
    User(UserInfo userInfo, boolean virtual) {
        this.userInfo = Objects.requireNonNull(userInfo);
        this.virtual = virtual;
    }

    /**
     * Returns the integer mapped to the given index. The default value is {@code 0}.
     * 
     * @param index The index to request.
     * @return The int at that index
     */
    public int getInt(int index) {
        return userInfo.getInt(index);
    }

    /**
     * Returns the rank of the user.
     * 
     * @return The user's rank
     */
    public int getRank() {
        return userInfo.getRank();
    }

    /**
     * Returns the score of the user.
     * 
     * @return The user's score
     */
    public int getScore() {
        return userInfo.getScore();
    }

    /**
     * Returns the string mapped to the given index. The default value is {@code ""}.
     * 
     * @param index The index to request.
     * @return The string at that index
     */
    public String getString(int index) {
        return userInfo.getString(index);
    }

    /**
     * Returns the users image (50x50 pixels). If unavailable a fake image will be
     * returned, but the method will never result in a return of {@code null}.
     * 
     * @return The user's image
     */
    public GreenfootImage getImage() {
        return userInfo.getUserImage();
    }

    /**
     * Returns the username of the user.
     * 
     * @return The user's name
     */
    public String getName() {
        return userInfo.getUserName();
    }

    /**
     * Indicates weather this user is marked as virtual. This means that it does not
     * actually store anything when calling {@link #store()}.
     * 
     * @return Weather this user is virtual
     */
    public boolean isVirtual() {
        return virtual;
    }

    /**
     * Returns the UserInfo object that backs this user.
     * 
     * @return This user's UserInfo
     */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * Maps the given int value to the specified index (in the range {@code 0} to 
     * {@link #NUM_INTS} - 1). To store the change permanently {@link #store()} has
     * to be called later.
     * 
     * @param index The index to map the value to
     * @param value The value to store
     */
    public void setInt(int index, int value) {
        userInfo.setInt(index, value);
        modified = true;
    }

    /**
     * Maps the given string value to the specified index (in the range {@code 0} to 
     * {@link #NUM_STRINGS} - 1). To store the change permanently {@link #store()} has
     * to be called later.
     * 
     * @param index The index to map the value to
     * @param value The value to store
     */
    public void setString(int index, String value) {
        userInfo.setString(index, value);
        modified = true;
    }

    /**
     * Sets the user's store to the specified value. To store the change permanently
     * {@link #store()} has to be called later.
     * 
     * @param score The score to set
     */
    public void setScore(int score) {
        userInfo.setScore(score);
    }

    /**
     * Stores any changes to the server.
     * <p>If this user is marked as virtual, this method will simply return {@code true}
     * and not store anything. Otherwise, you can only store the user that is returned by
     * {@link #get()}, otherwise {@code false} will be returned.
     * 
     * @return {@code true} indicates that the store was successful, however {@code false}
     *         does not neccecarily mean the store failed, the UserInfo class is not very
     *         reliable here
     */
    public boolean store() {
        if(isVirtual() || !modified) return true;
        if(!Objects.equals(getName(), GreenfootUtil.getUserName())) return false;
        boolean success = userInfo.store();
        modified = false;
        return success;
    }

    /**
     * Indicates weather storing any modified data using {@link #store()} should work. If
     * this returns {@code false} storing will definetly have no effect. This can happen
     * if the user is virtual or not the current Greenfoot user's user object.
     * 
     * @return Weather storing using {@link #store()} will work.
     */
    public boolean storingWorks() {
        return !isVirtual() && Objects.equals(getName(), GreenfootUtil.getUserName());
    }

    @Override
    public String toString() {
        return "User '" + getName() + "'";
    }



    /**
     * Creates a new virtual user with the given name and rank.
     * 
     * @param name The user's name
     * @param rank The user's rank on the highscore
     * @return A user with the given specs marked as virtual
     */
    public static User create(String name, int rank) {
        return new User(UserInfoVisitor.allocate(name, rank, null), true);
    }

    /**
     * Creates and returns a new guest user object. This will be marked as virtual and
     * have the online rank {@code -1}.
     * 
     * @return A new guest user
     */
    public static User createGuest() {
        return create("Guest", -1);
    }

    /**
     * Returns the current Greenfoot user's user. If the Greenfoot user is not logged in
     * this will return an empty optional.
     * 
     * @return An optional containing the current user's user object, or empty
     */
    public static Optional<User> current() {
        UserInfo userInfo = UserInfo.getMyInfo();
        if(userInfo == null) return Optional.empty();
        return Optional.of(new User(UserInfo.getMyInfo(), false));
    }

    /**
     * Returns the user object of the current Greenfoot user, or a virtual guest user if
     * that is not available.
     * 
     * @return The current user or a guest user
     */
    public static User getOrGuest() {
        return current().orElseGet(() -> createGuest());
    }

    /**
     * Get a sorted list of the user items for this scenario surrounding the current user.
     * 
     * <p>This will be one item per user, and it will be sorted in descending order by the score
     * (i.e. the return of getScore()).  The parameter allows you to specify a limit
     * on the amount of users' data to retrieve.  If there is lots of data stored
     * for users in your app, this may take some time (and bandwidth) to retrieve all users' data,
     * and often you do not need all the users' data.
     * 
     * <p>The items will be those surrounding the current user.  So for example, imagine that the user is 50th
     * of 100 total users (when sorted by getScore()).  Calling getNearby(5) will get the
     * 48th, 49th, 50th, 51st and 52nd users in that order.  Do not rely on the user being at a fixed
     * location in the middle of the list: calling getNearby(5) when the user is 2nd overall will get the
     * 1st, 2nd, 3rd, 4th and 5th users, so the user will be 2nd in the list, and a similar thing will happen
     * if the user is near the end of the list.
     * 
     * <p>For example, if you want to show the high-scores surrounding the user, store the score with setScore(score) and store(),
     * and then use getNearby(10) to get the ten users with scores close to the current user.</p>
     * 
     * <p>If the loading fails this will return an empty list.
     * 
     * @param maxAmount The maximum number of data items to retrieve.
     *                  Passing zero or a negative number will get all the data, but see the note above.
     * @return A list of surrounding users
     */
    @SuppressWarnings("unchecked")
    public static List<User> getNearby(int maxAmount) {
        List<UserInfo> returned = ((List<UserInfo>)UserInfo.getNearby(maxAmount));
        if(returned == null) return Collections.emptyList();
        return returned.stream().map(info -> new User(info, false)).collect(Collectors.toList());
    }

    /**
     * Get a sorted list of the UserInfo items for this scenario, starting at the top.
     * 
     * <p>This will return one UserInfo item per user, and it will be sorted in descending order by the score
     * (i.e. the return of getScore()).  The parameter allows you to specify a limit
     * on the amount of users' data to retrieve.  If there is lots of data stored
     * for users in your app, it may take some time (and bandwidth) to retrieve all users' data,
     * and often you do not need all the users' data.</p>
     * 
     * <p>For example, if you want to show the high-scores, store the score with setScore(score) and store(),
     * and then use getTop(10) to get the users with the top ten scores.</p> 
     * 
     * <p>If the loading fails this will return an empty list.
     * 
     * @param maxAmount The maximum number of users to retrieve.
     *                  Passing zero or a negative number will get all the data, but see the note above.
     * @return A list of the top users
     */
    @SuppressWarnings("unchecked")
    public static List<User> getTop(int maxAmount) {
        return ((List<UserInfo>)UserInfo.getTop(maxAmount)).stream().map(info -> new User(info, false)).collect(Collectors.toList());
    }

    /**
     * Trys to return the user object of the Greenfoot user with the specified name. Note that you cannot store
     * anything for that user if its not the current users' user, in which case you can request it using
     * {@link #get()}. The user may not be found even if he has an account because no data was ever stored.
     * 
     * @param name The name of the user to search for
     * @return An optional containing the user with the specified name, or empty
     */
    public static Optional<User> find(String name) {
        return getTop(-1).stream().filter(u -> Objects.equals(u.getName(), name)).findAny();
    }

    /**
     * Trys to find the user object for the Greenfoot user with the specified name using {@link #find(String)}.
     * If no user was found a virtual user with that name and a rank of {@code -1} will be returned.
     * 
     * @param name The name of the user to find
     * @return The user's user object, or a newly created virtual user name like the user searched for
     */
    public static User findOrCreate(String name) {
        return find(name).orElseGet(() -> create(name, -1));
    }
}
