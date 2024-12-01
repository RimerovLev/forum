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
import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

@Component  // 1
@RequiredArgsConstructor
@Order(10)
public class AuthenticationFilter implements Filter {

    // Репозиторий для работы с аккаунтами пользователей
    final UserAccountRepository userAccountRepository;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) // Основной метод фильтрации запросов
            throws IOException, ServletException {
        // Преобразуем ServletRequest и ServletResponse в их HTTP-версии для удобной работы с методами HTTP
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // Проверяем, требует ли текущий запрос аутентификации
        if (checkEndPoint(request.getMethod(), request.getServletPath())) {
            UserAccount userAccount; // Для хранения данных пользователя после проверки
            try {
                // Получаем учетные данные из заголовка Authorization
                String[] credentials = getCredentials(request.getHeader("Authorization"));

                // Пытаемся найти пользователя в репозитории по его идентификатору
                userAccount = userAccountRepository.findById(credentials[0])
                        .orElseThrow(RuntimeException::new); // Выбрасываем исключение, если пользователь не найден

                // Проверяем, совпадает ли переданный пароль с хэшем в базе данных
                if (!BCrypt.checkpw(credentials[1], userAccount.getPassword())) {
                    throw new RuntimeException(); // Выбрасываем исключение, если пароли не совпадают
                }
            } catch (Exception e) {
                // В случае ошибки аутентификации возвращаем клиенту статус 401 (Unauthorized)
                response.sendError(401);
                return; // Прерываем дальнейшую обработку запроса
            }
            // Оборачиваем текущий запрос в специальный объект, добавляющий информацию о пользователе
            request = new WrappedRequest(request, userAccount.getLogin());
        }
        // Передаем запрос дальше по цепочке фильтров или в конечную точку
        chain.doFilter(request, response);
    }

    // Метод для проверки, нужно ли аутентифицировать запрос
    private boolean checkEndPoint(String method, String path) {
        // Возвращает false, если это POST-запрос на /account/register или /posts, т.к. они не требуют аутентификации
        return !((HttpMethod.POST.matches(method) && path.matches("/account/register") ||
                (HttpMethod.POST.matches(method) && path.matches("/posts(/.*)?"))));
    }

    // Метод для получения учетных данных из заголовка Authorization
    private String[] getCredentials(String header) {
        // Разделяем заголовок Authorization, чтобы получить Base64-токен
        String token = header.split(" ")[1];
        // Декодируем токен из Base64 и преобразуем в строку
        String decode = new String(Base64.getDecoder().decode(token));
        // Разделяем строку на логин и пароль, которые передаются через двоеточие
        return decode.split(":");
    }

    // Внутренний класс для оборачивания запроса и добавления пользовательских данных
    private class WrappedRequest extends HttpServletRequestWrapper {
        private String login; // Поле для хранения логина пользователя

        // Конструктор, который принимает исходный запрос и логин пользователя
        public WrappedRequest(HttpServletRequest request, String login) {
            super(request); // Вызываем конструктор родительского класса
            this.login = login; // Сохраняем логин
        }

        // Переопределяем метод для возврата Principal с логином текущего пользователя
        @Override
        public Principal getUserPrincipal() {
            return () -> login; // Возвращаем объект, который предоставляет логин пользователя
        }
    }
}
