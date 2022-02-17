import java.util.Set;

public interface ISocialNetwork {

    // join the social network and get an Account handle for logging in
    public Account join(String userName) throws UserExistsException;

    // login using a valid Account handle  -- only one user can be logged in
    // returns an up-to-date handle to member's account
    // possible to switch accounts without logging out
    public Account login(Account me) throws UserNotFoundException;

    // log out
    public void logout();

    // These operations requires the user to be logged in...

    // List all members visible to the logged-in user
    public Set<String> listMembers() throws NoUserLoggedInException;

    // Returns true if a member has joined the social network (if visible to logged-in user)
    public boolean hasMember(String userName) throws NoUserLoggedInException;

    // Send a friend request to a valid, visible member
    public void sendFriendshipTo(String userName) throws UserNotFoundException, NoUserLoggedInException;

    // Block a member from befriending the logged-in user: blocked members can't see the logged-in user
    public void block(String userName) throws UserNotFoundException, NoUserLoggedInException;

    // Unblock a previously blocked member
    public void unblock(String userName) throws UserNotFoundException, NoUserLoggedInException;

    // Unfriend and existing friend
    public void sendFriendshipCancellationTo(String userName) throws UserNotFoundException, NoUserLoggedInException;

    // Accept a friend request from another visible member
    public void acceptFriendshipFrom(String userName) throws UserNotFoundException, NoUserLoggedInException;

    // Reject a friend request from another  member
    public void rejectFriendshipFrom(String userName) throws UserNotFoundException, NoUserLoggedInException;

    // Accept all friend requests automatically, unless they are blocked by logged-in user
    // Once auto-acceptance is enabled, logged-in member does not need to call acceptFriendRequestFrom
    public void autoAcceptFriendships() throws NoUserLoggedInException;

    // Accept all friend requests
    public void acceptAllFriendships() throws NoUserLoggedInException;

    // Reject all friend requests
    public void rejectAllFriendships() throws NoUserLoggedInException;

    // Turn off auto-acceptance, and turn on explicit acceptance
    public void cancelAutoAcceptFriendships() throws NoUserLoggedInException;

    // Recommend friends to logged-in user: if two friends have a common friend, include that member in return set
    // Don't recommend members blocked by the logged-in user
    public Set<String> recommendFriends() throws NoUserLoggedInException, UserNotFoundException;

    // Leave the social network and cease to exist to other members
    public void leave() throws NoUserLoggedInException;

    // No other public methods are allowed in ISocialNetwork implementations

}
