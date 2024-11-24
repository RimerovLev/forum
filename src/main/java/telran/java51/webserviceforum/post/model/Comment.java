package telran.java51.webserviceforum.post.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@EqualsAndHashCode(of = {"user", "dateCreated"})
public class Comment {
    @Setter
    String user;
    @Setter
    String message;
    LocalDateTime dateCreated;
    Integer likes;

    public Comment() {
        dateCreated = LocalDateTime.now();
        likes = 0;
    }

    public Comment(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public void addLike() {
        likes++;
    }
}
