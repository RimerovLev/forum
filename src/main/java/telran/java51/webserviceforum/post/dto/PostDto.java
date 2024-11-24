package telran.java51.webserviceforum.post.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {
    String id;
    String title;
    String content;
    String author;
    LocalDateTime dateCreated;
    @Singular
    Set<String> tags;
    Integer likes;
    @Singular
    List<CommentDto> comments;

}
