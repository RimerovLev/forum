package telran.java51.webserviceforum.post.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode(of = "id")
@Document(collection = "posts")

public class Post {
    String id;
    @Setter
    String title;
    @Setter
    String content;
    @Setter
    String author;
    LocalDateTime dateCreated;
    @Singular
    Set<String> tags;
    int likes;
    @Singular
    List<Comment> comments;

    public Post(){
        dateCreated = LocalDateTime.now();
        comments = new ArrayList<>();
    }

    public Post(String title, String content, String author, Set<String> tags) {
        this();
        this.title = title;
        this.content = content;
        this.author = author;
        this.tags = tags;
    }

    public boolean addTag(String tag) {
        return tags.add(tag);
    }
    public boolean removeTag(String tag) {
        return tags.remove(tag);
    }
    public void addLike() {
        likes++;
    }
    public boolean addComment(Comment comment) {return comments.add(comment);}
    public boolean removeComment(Comment comment) {
        return comments.remove(comment);
    }



}
