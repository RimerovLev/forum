package telran.java51.webserviceforum.post.service;


import java.util.List;
import java.util.Set;


import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

import telran.java51.webserviceforum.post.dao.PostRepository;
import telran.java51.webserviceforum.post.dto.DatePeriodDto;
import telran.java51.webserviceforum.post.dto.NewComentDto;
import telran.java51.webserviceforum.post.dto.NewPostDto;
import telran.java51.webserviceforum.post.dto.PostDto;
import telran.java51.webserviceforum.post.dto.exception.PostNotFoundException;
import telran.java51.webserviceforum.post.model.Comment;
import telran.java51.webserviceforum.post.model.Post;


@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    final PostRepository postRepository;
    final ModelMapper modelMapper;


    @Override
    public PostDto addNewPost(String author, NewPostDto newPostDto) {
        Post post = modelMapper.map(newPostDto, Post.class);
        post.setAuthor(author);
        post = postRepository.save(post);
        return modelMapper.map(post, PostDto.class);
    }


    @Override
    public PostDto findPostById(String id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        return modelMapper.map(post, PostDto.class);
    }


    @Override
    public PostDto deletePost(String id) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        postRepository.delete(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto updatePost(String id, NewPostDto newPostDto) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        String content = newPostDto.getContent();
        if (content != null && !content.isBlank()) {
            post.setContent(content);
        }
        String title = newPostDto.getTitle();
        if (title != null && !title.isBlank()) {
            post.setTitle(title);
        }
        Set<String> tags = newPostDto.getTags();
        if (tags != null && !tags.isEmpty()) {
            post.getTags().addAll(tags);
        }
        post = postRepository.save(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public PostDto addComent(String id, String author, NewComentDto newComentDto) {
        Post post = postRepository.findById(id).orElseThrow(PostNotFoundException::new);
        Comment comment = new Comment(author, newComentDto.getMessage());
        post.addComment(comment);
        postRepository.save(post);
        return modelMapper.map(post, PostDto.class);
    }

    @Override
    public void addLike(String postId) {
        Post post = postRepository.findById(postId).orElseThrow(PostNotFoundException::new);
        post.addLike();
        postRepository.save(post);
    }

    @Override
    public Iterable<PostDto> findPostByAuthor(String author) {
        return postRepository.findByAuthorIgnoreCase(author)
                .map(p -> modelMapper.map(p, PostDto.class))
                .toList();
    }

    @Override
    public Iterable<PostDto> findPostsByTag(List<String> tags) {
        return postRepository.findByTagsInIgnoreCase(tags)
                .map(p -> modelMapper.map(p, PostDto.class))
                .toList();
    }

    @Override
    public Iterable<PostDto> findPostsByPeriod(DatePeriodDto datePeriodDto) {
        return postRepository.findByDateCreatedBetween(datePeriodDto.getDateFrom(), datePeriodDto.getDateTo())
                .map(p -> modelMapper.map(p, PostDto.class))
                .toList();
    }
}
