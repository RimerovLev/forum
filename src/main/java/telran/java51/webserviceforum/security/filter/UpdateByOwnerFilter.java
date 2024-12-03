package telran.java51.webserviceforum.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;
@Component
@Order(40)
public class UpdateByOwnerFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (checkEndPoint(request.getMethod(), request.getServletPath())) {
            Principal principal = request.getUserPrincipal();
            String[] arr = request.getServletPath().split("/");
            String user = arr[arr.length - 1];

            if (!principal.getName().equalsIgnoreCase(user)) {
                response.sendError(403);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    // Метод для проверки, нужно ли применять фильтр к текущей конечной точке
    private boolean checkEndPoint(String method, String servletPath) {
        // Проверяем, является ли запрос методом PUT и соответствует ли путь шаблону /account/user/{имя_пользователя}
        return HttpMethod.PUT.matches(method) && servletPath.matches("/account/user/\\w+")
                || (HttpMethod.POST.matches(method) && servletPath.matches ("/forum/post/\\w+"))
                || HttpMethod.PUT.matches(method) && servletPath.matches("/forum/post/\\w+/comment/\\w+");
    }
}

