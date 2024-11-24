package telran.java51.webserviceforum.post.dao;

import org.springframework.data.repository.CrudRepository;
import telran.java51.webserviceforum.post.model.Post;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public interface PostRepository extends CrudRepository<Post, String> {
    Stream<Post> findByAuthorIgnoreCase(String author);
    Stream<Post> findByTagsInIgnoreCase(List<String> title);
    Stream<Post> findByDateCreatedBetween(LocalDate from, LocalDate to);
}
