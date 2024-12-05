package telran.java51.webserviceforum.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import telran.java51.webserviceforum.post.dao.PostRepository;
import telran.java51.webserviceforum.post.model.Post;

@Service("customSecurity")
@RequiredArgsConstructor
public class CustomWebSecurity {
    final PostRepository postRepository;


    public boolean checkPostAuthor(String postId, String userName) {
        Post post = postRepository.findById(postId).orElse(null);
        return post != null && userName.equals(post.getAuthor());
    }
}
