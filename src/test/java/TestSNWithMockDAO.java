import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;


public class TestSNWithMockDAO extends TestSNAbstractGeneric {
    IAccountDAO accountDAOMock = mock(AccountDAO.class);
    @Override @Before
    public void setUp() throws Exception {
        // whatever you need to do here
        sn = new SocialNetwork(accountDAOMock);
        super.setUp();
        when(accountDAOMock.findByUserName(m1.getUserName())).thenReturn(m1);
        when(accountDAOMock.findByUserName(m2.getUserName())).thenReturn(m2);
        when(accountDAOMock.findByUserName(m3.getUserName())).thenReturn(m3);
        when(accountDAOMock.findByUserName(m4.getUserName())).thenReturn(m4);
        when(accountDAOMock.findByUserName(m5.getUserName())).thenReturn(m5);
    }

    @Override @Test
    public void canListMembers() throws NoUserLoggedInException,
            UserNotFoundException {
        Set<Account> set = new HashSet<>();
        set.add(m1);
        set.add(m2);
        set.add(m3);
        set.add(m4);
        set.add(m5);
        when(accountDAOMock.findAll()).thenReturn(set);
        sn.login(m1);
        Set<String> members = sn.listMembers();
        assertTrue(members.contains(m2.getUserName()));
        assertTrue(members.contains(m3.getUserName()));
    }

    @Test
    public void canLeaveSocialNetwork() throws UserNotFoundException,
            NoUserLoggedInException {
        sn.login(m2);
        sn.leave();
        when(accountDAOMock.findByUserName(m2.getUserName())).thenReturn(null);
        // might have to do additional checking if using a Mockito mock
        sn.login(m3);
        assertFalse(sn.hasMember(m2.getUserName()));
    }
    /*
     * Generic tests are automatically inherited from abstract superclass - they should continue to work here!
     */

    /*
     * VERIFICATION TESTS
     *
     * These tests use a mock and verify that persistence operations are called.
     * They ONLY ensure that the right persistence operations of the mocked IAccountDAO implementation are called with
     * the right parameters. They need not and cannot verify that the underlying DB is actually updated.
     * They don't verify the state of the SocialNetwork either.
     *
     */

    @Test
    public void willAttemptToPersistANewAccount() throws UserExistsException {
        // make sure that when a new member account is created, it will be persisted
        Account newUser = sn.join("Zihan");
        verify(accountDAOMock).save(newUser);
    }

    @Test public void willAttemptToPersistSendingAFriendRequest()
            throws UserNotFoundException, UserExistsException, NoUserLoggedInException {
        // make sure that when a logged-in member issues a friend request, any changes to the affected accounts will be persisted
        sn.login(m1);
        sn.sendFriendshipTo(m2.getUserName());
        verify(accountDAOMock).update(m2);
        verify(accountDAOMock).update(m1);
    }

    @Test public void willAttemptToPersistAcceptanceOfFriendRequest()
            throws UserNotFoundException, UserExistsException, NoUserLoggedInException {
        // make sure that when a logged-in member issues a friend request, any changes to the affected accounts will be persisted
        sn.login(m1);
        sn.sendFriendshipTo(m2.getUserName());
        sn.login(m2);
        sn.acceptFriendshipFrom(m1.getUserName());
        verify(accountDAOMock, times(2)).update(m2);
        verify(accountDAOMock, times(2)).update(m1);
    }

    @Test public void willAttemptToPersistRejectionOfFriendRequest()
            throws UserNotFoundException, UserExistsException, NoUserLoggedInException {
        // make sure that when a logged-in member rejects a friend request, any changes to the affected accounts will be persisted
        sn.login(m1);
        sn.sendFriendshipTo(m2.getUserName());
        sn.login(m2);
        sn.rejectFriendshipFrom(m1.getUserName());
        verify(accountDAOMock, times(2)).update(m2);
        verify(accountDAOMock, times(2)).update(m1);	}

    @Test public void willAttemptToPersistBlockingAMember()
            throws UserNotFoundException, UserExistsException, NoUserLoggedInException {
        // make sure that when a logged-in member blocks another member, any changes to the affected accounts will be persisted
        sn.login(m1);
        sn.block(m2.getUserName());
        verify(accountDAOMock).update(m1);
    }

    @Test public void willAttemptToPersistLeavingSocialNetwork()
            throws UserExistsException, UserNotFoundException, NoUserLoggedInException {
        // make sure that when a logged-in member leaves the social network, his account will be permanenlty deleted and
        // any changes to the affected accounts will be persisted
        sn.login(m1);
        sn.sendFriendshipTo(m2.getUserName());
        sn.login(m2);
        sn.acceptFriendshipFrom(m1.getUserName());
        sn.leave();
        verify(accountDAOMock, times(2)).update(m2);
        verify(accountDAOMock, times(3)).update(m1);
        verify(accountDAOMock, times(1)).delete(m2);
    }

}
