package telran.java51.webserviceforum.security.filter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import telran.java51.webserviceforum.accounting.dao.UserAccountRepository;
import telran.java51.webserviceforum.accounting.model.UserAccount;
import telran.java51.webserviceforum.security.model.User;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

@Component  // 1. Аннотация Spring, обозначающая, что данный класс является компонентом и будет управляться контейнером Spring.
@RequiredArgsConstructor  // 2. Аннотация Lombok, создающая конструктор для финальных полей класса (в данном случае `userAccountRepository`).
@Order(10)  // 3. Задает порядок выполнения фильтров. Чем меньше значение, тем выше приоритет.
public class AuthenticationFilter implements Filter { // 4. Реализация интерфейса Filter для создания кастомного фильтра.

    final UserAccountRepository userAccountRepository; // 5. Репозиторий для работы с данными пользователей.

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) // 6. Основной метод фильтрации запросов.
            throws IOException, ServletException {
        // 7. Преобразуем общие ServletRequest и ServletResponse в их HTTP-версии для работы с HTTP-запросами.
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // 8. Проверяем, нужно ли применять аутентификацию для данного запроса.
        if (checkEndPoint(request.getMethod(), request.getServletPath())) {
            UserAccount userAccount; // 9. Объект для хранения данных пользователя.
            try {
                // 10. Извлекаем учетные данные из заголовка Authorization.
                String[] credentials = getCredentials(request.getHeader("Authorization"));

                // 11. Ищем пользователя в репозитории по идентификатору.
                userAccount = userAccountRepository.findById(credentials[0])
                        .orElseThrow(RuntimeException::new); // Если пользователь не найден, выбрасывается исключение.

                // 12. Проверяем, совпадает ли переданный пароль с хэшем в базе данных.
                if (!BCrypt.checkpw(credentials[1], userAccount.getPassword())) {
                    throw new RuntimeException(); // Если пароли не совпадают, выбрасываем исключение.
                }
            } catch (Exception e) {
                // 13. Если аутентификация не удалась, возвращаем клиенту HTTP-ответ с кодом 401 (Unauthorized).
                response.sendError(401);
                return; // Прерываем дальнейшую обработку запроса.
            }

            // 14. Извлекаем роли пользователя и преобразуем их в множество строк.
            Set<String> roles = userAccount.getRoles().stream().map(r -> r.toString()).collect(Collectors.toSet());

            // 15. Оборачиваем запрос в специальный класс, добавляющий информацию о пользователе.
            request = new WrappedRequest(request, userAccount.getLogin(), roles);
        }
        // 16. Передаем запрос дальше по цепочке фильтров или к конечной точке.
        chain.doFilter(request, response);
    }

    // 17. Метод проверки, нужно ли применять аутентификацию к текущему запросу.
    private boolean checkEndPoint(String method, String path) {
        // Возвращает false, если это POST-запрос на /account/register или /posts, которые не требуют аутентификации.
        return !((HttpMethod.POST.matches(method) && path.matches("/account/register") ||
                (HttpMethod.POST.matches(method) && path.matches("/posts(/.*)?"))));
    }

    // 18. Метод извлечения учетных данных из заголовка Authorization.
    private String[] getCredentials(String header) {
        // Разделяем заголовок Authorization и декодируем Base64-токен.
        String token = header.split(" ")[1];
        String decode = new String(Base64.getDecoder().decode(token));
        // Разделяем результат на логин и пароль.
        return decode.split(":");
    }

    // 19. Внутренний класс для обертки запроса с добавлением информации о пользователе.
    private class WrappedRequest extends HttpServletRequestWrapper {
        private String login; // Поле для хранения логина пользователя.
        private Set<String> roles; // Поле для хранения ролей пользователя.

        // 20. Конструктор, принимающий исходный запрос, логин и роли пользователя.
        public WrappedRequest(HttpServletRequest request, String login, Set<String> roles) {
            super(request); // Вызываем конструктор родительского класса.
            this.login = login; // Сохраняем логин.
            this.roles = roles; // Сохраняем роли.
        }

        // 21. Переопределяем метод для предоставления данных о текущем пользователе.
        @Override
        public Principal getUserPrincipal() {
            // Возвращаем объект Principal, содержащий логин и роли пользователя.
            return new User(login, roles);
        }
    }
}
