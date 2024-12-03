package telran.java51.webserviceforum.security.context;

import org.springframework.stereotype.Component;
import telran.java51.webserviceforum.security.model.User;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SecurityContextImpl implements SecurityContext {

    private Map<String, User> context = new ConcurrentHashMap<>();


    @Override
    public User addUserSession(String sessionId, User user) {
        return context.put(sessionId, user);
    }

    @Override
    public User removeUserSession(String sessionId) {
        return context.remove(sessionId);
    }

    @Override
    public User getUserBySessionId(String sessionId) {
        return context.get(sessionId);
    }
}
