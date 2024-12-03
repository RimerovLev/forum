package telran.java51.webserviceforum.security.filter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import telran.java51.webserviceforum.accounting.dao.UserAccountRepository;
import telran.java51.webserviceforum.accounting.model.UserAccount;
import telran.java51.webserviceforum.security.context.SecurityContext;
import telran.java51.webserviceforum.security.model.User;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Order(10)
public class AuthenticationFilter implements Filter {

    final UserAccountRepository userAccountRepository;
    final SecurityContext securityContext;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        if (checkEndPoint(request.getMethod(), request.getServletPath())) {
            String sessionId = request.getSession().getId();
            User user = securityContext.getUserBySessionId(sessionId);
            if (user == null) {
                try {
                    String[] credentials = getCredentials(request.getHeader("Authorization"));
                    UserAccount userAccount = userAccountRepository.findById(credentials[0])
                            .orElseThrow(RuntimeException::new);
                    if (!BCrypt.checkpw(credentials[1], userAccount.getPassword())) {
                        throw new RuntimeException();
                    }
                    Set<String> roles = userAccount.getRoles().stream().map(r -> r.toString()).collect(Collectors.toSet());
                    user = new User(userAccount.getLogin(), roles);
                    securityContext.addUserSession(sessionId, user);
                } catch (Exception e) {
                    response.sendError(401);
                    return;
                }
            }
            request = new WrappedRequest(request, user.getName(), user.getRoles());
        }
        chain.doFilter(request, response);
    }
    private boolean checkEndPoint(String method, String path) {
        return !((HttpMethod.POST.matches(method) && path.matches("/account/register") ||
                (HttpMethod.POST.matches(method) && path.matches("/posts(/.*)?"))));
    }

    private String[] getCredentials(String header) {
        String token = header.split(" ")[1];
        String decode = new String(Base64.getDecoder().decode(token));
        return decode.split(":");
    }

    private class WrappedRequest extends HttpServletRequestWrapper {
        private String login;
        private Set<String> roles;
        public WrappedRequest(HttpServletRequest request, String login, Set<String> roles) {
            super(request);
            this.login = login;
            this.roles = roles;
        }

        @Override
        public Principal getUserPrincipal() {
            return new User(login, roles);
        }
    }
}
