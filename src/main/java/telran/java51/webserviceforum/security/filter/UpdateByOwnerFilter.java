package telran.java51.webserviceforum.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.Principal;
@Component
@Order(30)
public class UpdateByOwnerFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // Преобразуем запрос и ответ в их HTTP-версии для удобной работы с методами HTTP
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Проверяем, нужно ли применять фильтр для текущего запроса
        if (checkEndPoint(request.getMethod(), request.getServletPath())) {
            // Получаем данные текущего аутентифицированного пользователя
            Principal principal = request.getUserPrincipal();

            // Извлекаем имя пользователя из URI (последняя часть пути)
            String[] arr = request.getRequestURI().split("/");
            String user = arr[arr.length - 1];

            // Проверяем, совпадает ли имя пользователя из URI с именем текущего пользователя
            if (!principal.getName().equalsIgnoreCase(user)) {
                // Если не совпадает, возвращаем статус ошибки 403 (Forbidden)
                response.sendError(403);
                return; // Прерываем дальнейшую обработку запроса
            }
        }
        // Передаем запрос дальше по цепочке фильтров или в конечную точку
        filterChain.doFilter(servletRequest, servletResponse);
    }

    // Метод для проверки, нужно ли применять фильтр к текущей конечной точке
    private boolean checkEndPoint(String method, String servletPath) {
        // Проверяем, является ли запрос методом PUT и соответствует ли путь шаблону /account/user/{имя_пользователя}
        return HttpMethod.PUT.matches(method) && servletPath.matches("/account/user/\\w+");
    }
}

