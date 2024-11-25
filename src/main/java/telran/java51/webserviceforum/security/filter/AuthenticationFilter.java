package telran.java51.webserviceforum.security.filter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import telran.java51.webserviceforum.accounting.dao.UserAccountRepository;
import telran.java51.webserviceforum.accounting.model.UserAccount;
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

@Component  // 1
@RequiredArgsConstructor  // 2
public class AuthenticationFilter implements Filter {

    final UserAccountRepository userAccountRepository;

    @Override  // 5
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)  // 6
            throws IOException, ServletException {
        // Преобразуем объекты ServletRequest и ServletResponse в HttpServletRequest и HttpServletResponse
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // Проверяем, нужно ли аутентифицировать запрос в зависимости от его метода и пути
        if (checkEndPoint(request.getMethod(), request.getServletPath())) {
            UserAccount userAccount;
            try {
                // Извлекаем учетные данные из заголовка Authorization
                String[] credentials = getCredentials(request.getHeader("Authorization"));

                // Ищем пользователя по ID (первый элемент credentials — это имя пользователя)
                userAccount = userAccountRepository.findById(credentials[0])
                        .orElseThrow(RuntimeException::new);

                // Проверяем пароль, используя BCrypt
                if (!BCrypt.checkpw(credentials[1], userAccount.getPassword())) {
                    throw new RuntimeException();
                }
            } catch (Exception e) {
                // Если произошла ошибка аутентификации, отправляем код 401 (Unauthorized)
                response.sendError(401);
                return;
            }
            // Оборачиваем запрос, добавляя информацию о логине пользователя
            request = new WrappedRequest(request, userAccount.getLogin());
        }

        // Передаем запрос и ответ дальше по цепочке фильтров
        chain.doFilter(request, response);
    }

    // Метод для проверки, нужно ли аутентифицировать запрос
    private boolean checkEndPoint(String method, String path) {
        // Возвращаем false, если это POST-запрос на /account/register (не требует аутентификации)
        return !(HttpMethod.POST.matches(method) && path.matches("/account/register")||
                !(path.matches("/posts(/.*)?")));
    }

    // Метод для извлечения учетных данных из заголовка Authorization
    private String[] getCredentials(String header) {
        // Извлекаем токен из заголовка, который находится после "Basic "
        String token = header.split(" ")[1];
        // Декодируем Base64 токен в строку и разделяем её по символу ":"
        String decode = new String(Base64.getDecoder().decode(token));
        return decode.split(":");
    }

    // Вспомогательный класс для оборачивания запроса и добавления логина пользователя
    private class WrappedRequest extends HttpServletRequestWrapper {
        private String login;  // 29

        // Конструктор, который инициализирует оригинальный запрос и логин пользователя
        public WrappedRequest(HttpServletRequest request, String login) {
            super(request);  // 31
            this.login = login;  // 32
        }

        // Переопределяем метод getUserPrincipal для возвращения логина пользователя
        @Override
        public Principal getUserPrincipal() {
            // Возвращаем объект Principal с логином пользователя
            return () -> login;
        }
    }
}
