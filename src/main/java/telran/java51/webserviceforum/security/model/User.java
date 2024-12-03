package telran.java51.webserviceforum.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.security.Principal;
import java.util.Set;

@Getter
@AllArgsConstructor
public class User implements Principal {
    private String name;
    private Set<String> roles;
}
