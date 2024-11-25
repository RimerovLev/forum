package telran.java51.webserviceforum.accounting.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import telran.java51.webserviceforum.accounting.dto.RolesDto;
import telran.java51.webserviceforum.accounting.dto.UserDto;
import telran.java51.webserviceforum.accounting.dto.UserEditDto;
import telran.java51.webserviceforum.accounting.dto.UserRegisterDto;

@Service
public interface UserAccountService {


    UserDto register(UserRegisterDto userRegisterDto);




    UserDto getUser(String login);


    UserDto removeUser(String login);


    UserDto updateUser(String login, UserEditDto userEditDto);


    RolesDto changeRolesList(String login, String role, boolean isAddRole);


    void changePassword(String login, String newPassword);


}
