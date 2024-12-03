package telran.java51.webserviceforum.accounting.dto;

import lombok.*;
import telran.java51.webserviceforum.accounting.model.Role;

import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RolesDto {
    String login;
    @Singular
    Set<Role> roles;
}
