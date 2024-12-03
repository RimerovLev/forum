package telran.java51.webserviceforum.security.context;

import telran.java51.webserviceforum.security.model.User;

public interface SecurityContext {
    User addUserSession(String sessionId, User user);

    User removeUserSession(String sessionId);

    User getUserBySessionId(String sessionId);
}
