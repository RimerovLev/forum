package telran.java51.webserviceforum.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import telran.java51.webserviceforum.accounting.dao.UserAccountRepository;
import telran.java51.webserviceforum.accounting.model.UserAccount;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Order(20)
public class AdminManagingRolesFilter implements Filter {
    final UserAccountRepository userAccountRepository;
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)  // 6
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
                if (checkEndPoint(request.getMethod(), request.getServletPath())){
                    UserAccount userAccount = userAccountRepository.findById(request.getUserPrincipal().getName()).get();
                    if(!userAccount.getRoles().contains("ADMINISTRATOR")){
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
