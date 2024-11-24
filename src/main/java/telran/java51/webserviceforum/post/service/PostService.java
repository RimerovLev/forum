package telran.java51.webserviceforum.post.service;

import telran.java51.webserviceforum.post.dto.DatePeriodDto;
import telran.java51.webserviceforum.post.dto.NewComentDto;
import telran.java51.webserviceforum.post.dto.NewPostDto;
import telran.java51.webserviceforum.post.dto.PostDto;

import java.util.List;

public interface PostService {
    PostDto addNewPost(String author, NewPostDto newPostDto);
    PostDto findPostById(String id);
    PostDto deletePost(String id);
    PostDto updatePost(String id, NewPostDto newPostDto);
    PostDto addComent(String id, String author, NewComentDto newComentDto);
    void addLike(String postId);
    Iterable<PostDto> findPostByAuthor(String author);
    Iterable<PostDto> findPostsByTag(List<String> tag);
    Iterable<PostDto> findPostsByPeriod(DatePeriodDto datePeriodDto);
}
