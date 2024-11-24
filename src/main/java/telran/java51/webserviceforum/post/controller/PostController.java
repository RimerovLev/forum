package telran.java51.webserviceforum.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import telran.java51.webserviceforum.post.dto.DatePeriodDto;
import telran.java51.webserviceforum.post.dto.NewComentDto;
import telran.java51.webserviceforum.post.dto.NewPostDto;
import telran.java51.webserviceforum.post.dto.PostDto;
import telran.java51.webserviceforum.post.service.PostService;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/forum")
public class PostController {
    final PostService postService;

    @PostMapping("/post/{user}")
    public PostDto addNewPost(@PathVariable String user,@RequestBody NewPostDto newPostDto) {
        return postService.addNewPost(user, newPostDto);
    }

    @GetMapping("/post/{id}")
    public PostDto findPostById(@PathVariable String id) {
        return postService.findPostById(id);
    }

    @DeleteMapping("/post/{id}")
    public PostDto deletePost(@PathVariable String id) {
        return postService.deletePost(id);
    }

    @PutMapping("/post/{id}")
    public PostDto updatePost(@PathVariable String id,@RequestBody NewPostDto newPostDto) {
        return postService.updatePost(id, newPostDto);
    }

    @PutMapping("/post/{postId}/comment/{user}")
    public PostDto addComent(@PathVariable String postId,@PathVariable String user,@RequestBody NewComentDto newComentDto) {
        return postService.addComent(postId, user, newComentDto);
    }

    @PutMapping("/post/{postId}/like")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addLike(@PathVariable String postId) {
        postService.addLike(postId);
    }

    @GetMapping("/posts/author/{user}")
    public Iterable<PostDto> findPostByAuthor(@PathVariable String user) {
        return postService.findPostByAuthor(user);
    }

    @PostMapping("/posts/tags")
    public Iterable<PostDto> findPostsByTag(@RequestBody List<String> tag) {
        return postService.findPostsByTag(tag);
    }

    @PostMapping("/posts/period")
    public Iterable<PostDto> findPostsByPeriod(@RequestBody DatePeriodDto datePeriodDto) {
        return postService.findPostsByPeriod(datePeriodDto);
    }
}
