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
import telran.java51.webserviceforum.post.dao.PostRepository;
import telran.java51.webserviceforum.post.model.Post;

import java.io.IOException;
import java.security.Principal;

@Component
@RequiredArgsConstructor
@Order(90)
public class UpdatePostFilter implements Filter {
    final UserAccountRepository userAccountRepository;
    final PostRepository postRepository;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (checkPoint(request.getMethod(), request.getServletPath())) {
            Principal principal = request.getUserPrincipal();
            UserAccount userAccount = userAccountRepository
                    .findById(principal.getName()).get();
            String[] arr = request.getRequestURI().split("/");
            String postId = arr[arr.length - 1];
            Post post = postRepository.findById(postId).orElse(null);
            if (post == null){
                response.sendError(404, "Post not found");
            }

            if (post != null && !(userAccount.getLogin().equalsIgnoreCase(post.getAuthor()))) {
                response.sendError(403);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private boolean checkPoint(String method, String path) {
        return HttpMethod.PUT.matches(method) && path.matches("/forum/post/\\w+");
    }
}
