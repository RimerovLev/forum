package telran.java51.webserviceforum.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import telran.java51.webserviceforum.accounting.dao.UserAccountRepository;
import telran.java51.webserviceforum.accounting.model.Role;
import telran.java51.webserviceforum.accounting.model.UserAccount;
import telran.java51.webserviceforum.security.model.User;

import java.io.IOException;

@Component
@Order(20)
public class AdminManagingRolesFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        if (checkEndPoint(request.getMethod(), request.getServletPath())) {
            User user = (User) request.getUserPrincipal();
            if (!user.getRoles().contains(Role.ADMINISTRATOR)) {
                response.sendError(403, "Permission denied");
                return;
            }
        }
        chain.doFilter(request, response);
    }


    private boolean checkEndPoint(String method, String path) {
        return path.matches("/account/user/\\w+/role/\\w+");
    }


}
