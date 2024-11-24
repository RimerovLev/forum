package telran.java51.webserviceforum.accounting.dao;

import org.springframework.data.repository.CrudRepository;
import telran.java51.webserviceforum.accounting.model.UserAccount;

public interface UserAccountRepository extends CrudRepository<UserAccount, String> {

}
