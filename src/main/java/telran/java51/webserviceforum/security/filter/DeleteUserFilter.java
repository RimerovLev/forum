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
import telran.java51.webserviceforum.security.model.User;

import java.io.IOException;
import java.security.Principal;

@Component
@Order(30)
public class DeleteUserFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        if (checkEndPoint(httpServletRequest.getMethod(), httpServletRequest.getServletPath())) {
            Principal principal = httpServletRequest.getUserPrincipal();
            String[] arr = httpServletRequest.getServletPath().split("/");
            String userName = arr[arr.length - 1];
            User user = (User) httpServletRequest.getUserPrincipal();
            if (!(user.getRoles().contains("ADMINISTRATOR")
                    || principal.getName().equalsIgnoreCase(userName))) {
                httpServletResponse.sendError(403);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);

    }

    private boolean checkEndPoint(String method, String path) {
        return HttpMethod.DELETE.matches(method) && path.matches("/account/user/\\w+");
    }
}
