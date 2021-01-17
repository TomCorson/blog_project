package com.blog.blog_project.services;

import com.blog.blog_project.entities.BlogPost;
import com.blog.blog_project.entities.User;
import com.blog.blog_project.mapper.PostMapper;
import com.blog.blog_project.payload.request.PostRequest;
import com.blog.blog_project.payload.response.AuthenticationResponse;
import com.blog.blog_project.payload.response.PostResponse;
import com.blog.blog_project.repositories.BlogPostRepository;
import com.blog.blog_project.repositories.TagRepository;
import com.blog.blog_project.repositories.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogPostServiceTest {

    @Mock private BlogPostRepository blogPostRepository;
    @Mock private TagRepository tagRepository;
    @Mock private UserRepository userRepository;
    @Mock private AuthService authService;
    @Mock private PostMapper postMapper;

    @Captor
    private ArgumentCaptor<BlogPost> postArgumentCaptor;

    private BlogPostService blogPostService;

    @BeforeEach
    public void setup(){
        blogPostService = new BlogPostService(blogPostRepository, tagRepository, userRepository, authService, postMapper);
    }



    @Test
    @DisplayName("Find a blog post by an id")
    public void shouldFindPostById(){

        BlogPost post = new BlogPost(12L, "Myy blog post", LocalDateTime.now(), LocalDateTime.now(), "blurb", "full text", "wwww.image.com", "pompy", new HashSet<>(), null);
        PostResponse expectedPostResponse = new PostResponse(12L, "pompy", "Myy blog post", "www.image.com", "blurb", "full text", "test date", new HashSet<>());

        when(blogPostRepository.findById(12L)).thenReturn(Optional.of(post));
        when(postMapper.mapToDto(Mockito.any(BlogPost.class))).thenReturn(expectedPostResponse);

        PostResponse actualPostResponse = blogPostService.getPost(12L);

        Assertions.assertThat(actualPostResponse.getId()).isEqualTo(expectedPostResponse.getId());
        Assertions.assertThat(actualPostResponse.getTitle()).isEqualTo(expectedPostResponse.getTitle());

    }

    @Test
    @DisplayName("Should find all posts")
    public void shouldFindAllPosts(){
        User pompy = new User(1L, "pompy", "password", "pompy@gmail.com", LocalDateTime.now(), new ArrayList<>());

        BlogPost post1 = new BlogPost(1L, "Myy blog post", LocalDateTime.now(), LocalDateTime.now(), "blurb", "full text", "wwww.image.com", "pompy", new HashSet<>(), pompy);
        BlogPost post2 = new BlogPost(2L, "Myy blog post", LocalDateTime.now(), LocalDateTime.now(), "blurb", "full text", "wwww.image.com", "pompy", new HashSet<>(), pompy);

        doReturn(Arrays.asList(post1, post2)).when(blogPostRepository).findAll();
        List<PostResponse> actual = blogPostService.getAllPosts();

        assertEquals(2, actual.size(), "findAll should return 2 blog posts");

    }

    //WIP
//    @Test
//    @DisplayName("Find all blog posts by username")
//    public void shouldFindAllPostsByUsername(){
//        User pompy = new User(1L, "pompy", "password", "pompy@gmail.com", LocalDateTime.now(), new ArrayList<>());
//        String username = "pompy";
//
//        BlogPost post1 = new BlogPost(1L, "Myy blog post", LocalDateTime.now(), LocalDateTime.now(), "blurb", "full text", "wwww.image.com", "pompy", new HashSet<>(), null);
//        BlogPost post2 = new BlogPost(2L, "Myy blog post", LocalDateTime.now(), LocalDateTime.now(), "blurb", "full text", "wwww.image.com", "pompy", new HashSet<>(), null);
//
//        PostResponse expectedPostResponse1 = new PostResponse(1L, "pompy", "Myy blog post", "wwww.image.com", "blurb", "full text", new HashSet<>());
//        PostResponse expectedPostResponse2 = new PostResponse(2L, "pompy", "Myy blog post", "wwww.image.com", "blurb", "full text", new HashSet<>());
//
//       Mockito.when(blogPostService.findAllByUsername(username)).thenReturn((Arrays.asList(expectedPostResponse1, expectedPostResponse2)));
//       Integer expected = 2;
//       List<PostResponse> actual = blogPostService.findAllByUsername(username);
//
//        Assertions.assertThat(actual.size()).isEqualTo(expected);
//
//    }


    @Test
    @DisplayName("Create and save a blog post")
    public void shouldCreateAndSavePost(){
        User currentUser = new User(1234L, "pompy", "password", "pompy@email.com", LocalDateTime.now(), new ArrayList<>());
        BlogPost post = new BlogPost(12L, "Myy blog post", LocalDateTime.now(), LocalDateTime.now(), "blurb", "full text", "wwww.image.com", "pompy", new HashSet<>(), null);
        PostRequest postRequest = new PostRequest(12L, "Myy blog post", "blurb","full text", "wwww.image.com", "pompy", new HashSet<>());

        when(authService.getCurrentUser()).thenReturn(currentUser);
//        Mockito.when(blogPostRepository.findById(12L)).thenReturn(Optional.of(post));
        when(postMapper.map(postRequest, currentUser)).thenReturn(post);

        blogPostService.createBlogPost(postRequest);
        //since create post does not return anything, we use verify to verify that a piece of code was executed

        //capture the object that is passed to the method. assert if it matches our expectations or not with with ArgumentCaptor to capture
        //arguments, mockito captures the obj so you have access to it after the method execution
        verify(blogPostRepository, Mockito.times(1)).save(postArgumentCaptor.capture());

        Assertions.assertThat(postArgumentCaptor.getValue().getId()).isEqualTo(12L);
        Assertions.assertThat(postArgumentCaptor.getValue().getTitle()).isEqualTo("Myy blog post");
    }

}