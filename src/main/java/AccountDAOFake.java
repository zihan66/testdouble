import java.util.HashSet;
import java.util.Set;



public class AccountDAOFake implements IAccountDAO {
    /*
     * A full in-memory fake of AccountDAO.
     *
     * IMPORTANT NOTE:
     * If you take advantage of the Account class for simulating an in-memory database,
     * make sure that your storage representation relies on fully cloned objects, not just Account references passed.
     * Tests should not be able to distinguish this object from a real DAO object connected to a real DB.
     * In this sense, this class is a full fake that works with all inputs.
     */

    private Set<Account> members = new HashSet<Account>();

    public boolean isFullFake() {
        return true;
    }

    public void save(Account member) {
        // implement this method
        if(findByUserName(member.getUserName()) == null){
            Account clone = member.clone();
            members.add(clone);
        }

    }

    public Account findByUserName(String userName)  {
        for(Account user : members){
            if(user.getUserName() == userName)
                return user;
        }
        return null;
        // implement this method
    }

    public Set<Account> findAll()  {
        return members;
        // implement this method
    }

    public void delete(Account member) {
        if(member != null){
            members.remove(member);
        }
        // implement this method
    }

    public void update(Account member) {
        if(member != null){
            delete(findByUserName(member.getUserName()));
            save(member);
        }
        // implement this method
    }

}

