import java.util.Collection;
import java.util.HashSet;
import java.util.Set;



public class SocialNetwork implements ISocialNetwork {

    private Account currentUser = null;
    private IAccountDAO accountDAO = DAOFactory.getInstance().getAccountDAO();

    public SocialNetwork() {
    }
    public SocialNetwork(IAccountDAO account) {
        accountDAO = account;
    }

    private class MyAccount extends Account {

        public MyAccount(String userName) {
            setUserName(userName);
        }
    }

    public IAccountDAO getCollaborator(){
        return accountDAO;
    }

    public Account join(String userName) throws UserExistsException {
        Account member = new MyAccount(userName);
        Account existingMember = accountDAO.findByUserName(userName);
        if (existingMember != null) throw new UserExistsException(userName);
        accountDAO.save(member);
        return member;
    }

    public Account login(Account me) throws UserNotFoundException {
        // find the user in the DB and return an Account record for user
        if (me == null) throw new UserNotFoundException("Null");
        Account member = accountDAO.findByUserName(me.getUserName());
        if (member == null) throw new UserNotFoundException(me.getUserName());
        currentUser = member;
        return member;
    }

    public void logout() {
        currentUser = null;

    }

    public Set<String> listMembers() throws NoUserLoggedInException{
        if (currentUser == null) throw new NoUserLoggedInException();
        Set<Account> members = accountDAO.findAll();
        Set<String> userNames = new HashSet<String>();
        for (Account each : members) {
            if (!each.blockedMembers().contains(currentUser.getUserName())) {
                userNames.add(each.getUserName());
            }
        }
        return userNames;
    }

    public boolean hasMember(String userName) throws NoUserLoggedInException {
        if (currentUser == null) throw new NoUserLoggedInException();
        Account member = accountDAO.findByUserName(userName);
        if (member == null) {
            return false;
        }
        if (member.blockedMembers().contains(currentUser.getUserName())) {
            return false;
        }
        return true;
    }

    public void sendFriendshipTo(String userName) throws UserNotFoundException, NoUserLoggedInException {
        if (currentUser == null) throw new NoUserLoggedInException();
        Account toMember = accountDAO.findByUserName(userName);
        if (toMember == null) throw new UserNotFoundException(userName);
        toMember.requestFriendship(currentUser);
        accountDAO.update(currentUser);
        accountDAO.update(toMember);
    }

    public void leave() throws NoUserLoggedInException {
        if (currentUser == null) throw new NoUserLoggedInException();
        for (String each : currentUser.getFriends()) {
            Account friend = accountDAO.findByUserName(each);
            friend.cancelFriendship(currentUser);
            accountDAO.update(friend);
        }
        accountDAO.delete(currentUser);
        currentUser = null;
    }

    public void sendFriendshipCancellationTo(String userName) throws UserNotFoundException, NoUserLoggedInException {
        if (currentUser == null) throw new NoUserLoggedInException();
        Account member = accountDAO.findByUserName(userName);
        if (member == null) throw new UserNotFoundException(userName);
        member.cancelFriendship(currentUser);
        accountDAO.update(member);
        accountDAO.update(currentUser);
    }

    public void acceptFriendshipFrom(String userName) throws UserNotFoundException, NoUserLoggedInException {
        if (currentUser == null) throw new NoUserLoggedInException();
        Account member = accountDAO.findByUserName(userName);
        if (member == null) throw new UserNotFoundException(userName);
        member.friendshipAccepted(currentUser);
        accountDAO.update(member);
        accountDAO.update(currentUser);
    }

    public void rejectFriendshipFrom(String userName) throws UserNotFoundException, NoUserLoggedInException {
        if (currentUser == null) throw new NoUserLoggedInException();
        Account member = accountDAO.findByUserName(userName);
        if (member == null) throw new UserNotFoundException(userName);
        member.friendshipRejected(currentUser);
        accountDAO.update(currentUser);
        accountDAO.update(member);
    }

    public void autoAcceptFriendships() throws NoUserLoggedInException {
        if (currentUser == null) throw new NoUserLoggedInException();
        currentUser.autoAcceptFriendships();
        accountDAO.update(currentUser);
    }

    public void cancelAutoAcceptFriendships() throws NoUserLoggedInException {
        if (currentUser == null) throw new NoUserLoggedInException();
        currentUser.cancelAutoAcceptFriendships();
        accountDAO.update(currentUser);
    }

    public void block(String userName) throws UserNotFoundException, NoUserLoggedInException {
        if (currentUser == null) throw new NoUserLoggedInException();
        Account member = accountDAO.findByUserName(userName);
        if (member == null) throw new UserNotFoundException(userName);
        currentUser.block(member);
        accountDAO.update(currentUser);
    }

    public void unblock(String userName) throws UserNotFoundException, NoUserLoggedInException {
        if (currentUser == null) throw new NoUserLoggedInException();
        Account member = accountDAO.findByUserName(userName);
        if (member == null) throw new UserNotFoundException(userName);
        accountDAO.update(currentUser);
    }

    public void rejectAllFriendships() throws NoUserLoggedInException {
        if (currentUser == null) throw new NoUserLoggedInException();
        // must clone incomingRequests to avoid concurrentModificationException
        Collection<String> incomingRequests = new HashSet<String>();
        for (String each : currentUser.getIncomingRequests()) {
            incomingRequests.add(new String(each));
        }
        for (String each : incomingRequests) {
            accountDAO.findByUserName(each).friendshipRejected(currentUser);
        }
    }

    public void acceptAllFriendships() throws NoUserLoggedInException {
        if (currentUser == null) throw new NoUserLoggedInException();
        // must clone incomingRequests to avoid concurrentModificationException
        Collection<String> incomingRequests = new HashSet<String>();
        for (String each : currentUser.getIncomingRequests()) {
            incomingRequests.add(new String(each));
        }
        for (String each : incomingRequests) {
            accountDAO.findByUserName(each).friendshipAccepted(currentUser);
        }
    }

    public Set<String> recommendFriends() throws NoUserLoggedInException, UserNotFoundException {
        if (currentUser == null) throw new NoUserLoggedInException();
        Set<String> recommendations = new HashSet<String>();
        Set<String> seen = new HashSet<String>();
        for (String each: currentUser.getFriends()) {
            Account friend = accountDAO.findByUserName(each);
            if (friend == null ) throw new UserNotFoundException(each);
            for (String friendOfFriend: friend.getFriends()) {
                if (seen.contains(friendOfFriend)) {
                    if (!currentUser.getFriends().contains(friendOfFriend) && !currentUser.blockedMembers().contains(friendOfFriend))
                        recommendations.add(friendOfFriend);
                }
                else { // should we do something special about currentUser?
                    if(friendOfFriend != currentUser.getUserName())
                        seen.add(friendOfFriend);
                }
            }
        }
        return recommendations;
    }

}
