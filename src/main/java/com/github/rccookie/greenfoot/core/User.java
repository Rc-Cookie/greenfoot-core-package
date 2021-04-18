package com.github.rccookie.greenfoot.core;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.github.rccookie.greenfoot.java.util.Optional;

import greenfoot.UserInfo;
import greenfoot.util.GreenfootUtil;

/**
 * A wrapper class for the {@link UserInfo} class that allowes to create virtual
 * users and contains some conveniance methods.
 * 
 * @author RcCookie
 * @version 1.1
 */
public class User {

    static {
        Core.initialize();
    }

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
     * The user's name.
     */
    private final String name;

    /**
     * The user's current score.
     */
    private final int rank;

    /**
     * The user's score.
     */
    private int score = 0;

    /**
     * The integers mapped to the user.
     */
    private final int[] ints = new int[NUM_INTS];

    /**
     * The Strings mapped to the user.
     */
    private final String[] strings = new String[NUM_STRINGS];

    /**
     * The UserInfo that stores this user's data.
     */
    private final UserInfo userInfo;



    /**
     * Weather any values were modified since the last store.
     */
    private boolean modified = true;



    /**
     * Creates a new user from the given UserInfo object.
     * 
     * @param userInfo The UserInfo that should back this user, must not be {@code null}
     */
    User(UserInfo userInfo) {
        this.userInfo = Objects.requireNonNull(userInfo);
        name = userInfo.getUserName();
        rank = userInfo.getRank();
        score = userInfo.getScore();
        for(int i=0; i<NUM_INTS; i++) ints[i] = userInfo.getInt(i);
        for(int i=0; i<NUM_STRINGS; i++) strings[i] = userInfo.getString(i);
    }

    /**
     * Creates a new virtual user that cannot store anything.
     * 
     * @param name The user's name
     * @param rank The user's rank on the highscore
     */
    User(String name, int rank) {
        this.name = name;
        this.rank = rank;
        userInfo = null;
    }

    /**
     * Returns the integer mapped to the given index. The default value is {@code 0}.
     * 
     * @param index The index to request.
     * @return The int at that index
     */
    public int getInt(int index) {
        return ints[index];
    }

    /**
     * Returns the rank of the user.
     * 
     * @return The user's rank
     */
    public int getRank() {
        return rank;
    }

    /**
     * Returns the score of the user.
     * 
     * @return The user's score
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the string mapped to the given index. The default value is {@code ""}.
     * 
     * @param index The index to request.
     * @return The string at that index
     */
    public String getString(int index) {
        return strings[index];
    }

    /**
     * Returns the users image (50x50 pixels). If unavailable a fake image will be
     * returned, but the method will never result in a return of {@code null}.
     * 
     * @return The user's image
     */
    public Image getImage() {
        return userInfo != null ? Image.of(userInfo.getUserImage()) : new Image(50, 50);
    }

    /**
     * Returns the username of the user.
     * 
     * @return The user's name
     */
    public String getName() {
        return name;
    }

    /**
     * Indicates weather this user is marked as virtual. This means that it does not
     * actually store anything when calling {@link #store()}.
     * 
     * @return Weather this user is virtual
     */
    public boolean isVirtual() {
        return userInfo == null;
    }

    /**
     * Returns the UserInfo object that this user stores to.
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
        ints[index] = value;
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
        strings[index] = value;
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
        this.score = score;
        userInfo.setScore(score);
    }

    /**
     * Updates the user's score with the given score value. This means that it will
     * set the given score if it is more than the current score.
     * 
     * @param score The score to update the current score with
     * @param store Weather the changes should be stored instantly
     * @return Weather the given score was a new highscore
     */
    public boolean updateScore(int score, boolean store) {
        if(score > getScore()) {
            setScore(score);
            if(store) store();
            return true;
        }
        return false;
    }

    /**
     * Updates the user's score with the given score value. This means that it will
     * set the given score if it is more than the current score. It that case this
     * change will instantly be saved to the server / file.
     * 
     * @param score The score to update the current score with
     * @return Weather the given score was a new highscore
     */
    public boolean updateScore(int score) {
        return updateScore(score, true);
    }

    /**
     * Stores any changes to the server.
     * <p>If this user is marked as virtual, this method will simply return {@code true}
     * and not store anything. Otherwise, you can only store the user that is returned by
     * {@link #now()}, otherwise {@code false} will be returned.
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
        return new User(name, rank);
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
        return Optional.of(new User(UserInfo.getMyInfo()));
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
        return returned.stream().map(info -> new User(info)).collect(Collectors.toList());
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
        return ((List<UserInfo>)UserInfo.getTop(maxAmount)).stream().map(info -> new User(info)).collect(Collectors.toList());
    }

    /**
     * Trys to return the user object of the Greenfoot user with the specified name. Note that you cannot store
     * anything for that user if its not the current users' user, in which case you can request it using
     * {@link #now()}. The user may not be found even if he has an account because no data was ever stored.
     * 
     * @param name The name of the user to search for
     * @return An optional containing the user with the specified name, or empty
     */
    public static Optional<User> find(String name) {
        return Optional.ofNullable(getTop(-1).stream().filter(u -> Objects.equals(u.getName(), name)).findAny().orElse(null));
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
