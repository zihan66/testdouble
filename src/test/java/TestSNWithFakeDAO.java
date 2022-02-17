
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;


public class TestSNWithFakeDAO extends TestSNAbstractGeneric {

    @Override @Before
    public void setUp() throws Exception {
        IAccountDAO accountDAOFake = new AccountDAOFake();
        sn = new SocialNetwork(accountDAOFake);
        super.setUp();
    }

    /*
     * Generic tests are automatically inherited here - they should work with a fake DAO!
     */

    /*
     * ... plus this additional test should work with a fake
     */
    @Test (expected = UserExistsException.class)
    // this test only applies to the fake that emulates a real DB
    public void nonPersistedAccountCanCauseThisTestToFailButItShouldNot() throws UserExistsException {
        Account surya = sn.join("Surya"); // this should persist hakan in a deep way, using clone()
        surya.setUserName("Somebody Else"); // if not, I can change the DB in a sneaky way
        Account anotherSurya = sn.join("Surya"); // and this would mysteriously work, but it shouldn't
//        fail("How come creating another "  + anotherSurya.getUserName() + " worked?");
        // because a DB doesn’t behave this way, does it?
    }

}
