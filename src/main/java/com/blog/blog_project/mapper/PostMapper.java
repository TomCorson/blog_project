package com.blog.blog_project.mapper;

import com.blog.blog_project.entities.BlogPost;
import com.blog.blog_project.entities.BlogPost.BlogPostBuilder;
import com.blog.blog_project.entities.Tag;
import com.blog.blog_project.entities.User;
import com.blog.blog_project.payload.request.PostRequest;
import com.blog.blog_project.payload.response.PostResponse;
import com.blog.blog_project.services.AuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Component
public class  PostMapper {

    public static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy 'at' hh:mm a");

    public BlogPost map(PostRequest postRequest, User user){
        if (postRequest == null && user == null){
            return null;
        }

        BlogPostBuilder blogPost = BlogPost.builder();

        if( postRequest != null){
            blogPost.id(postRequest.getPostId());
            blogPost.title(postRequest.getTitle());
            blogPost.blurb(postRequest.getBlurb());
            blogPost.fulltext(postRequest.getFulltext());
            blogPost.tags(postRequest.getTags());
            blogPost.imagelink(postRequest.getImagelink());
            blogPost.username(postRequest.getUsername());
        }

       // blogPost.username(user.getUsername());
        blogPost.creationTimestamp(LocalDateTime.now());
        return blogPost.build();
    }

    public PostResponse mapToDto(BlogPost blogPost){
        if(blogPost ==  null){
            return null;
        }

        PostResponse postResponse = new PostResponse();
        postResponse.setId(blogPost.getId());
        postResponse.setUserName(blogPost.getUsername());
        postResponse.setTitle(blogPost.getTitle());
        postResponse.setBlurb(blogPost.getBlurb());
        postResponse.setFullText(blogPost.getFulltext());
        postResponse.setCreationTimestamp(blogPost.getCreationTimestamp().format(dateFormat));
        postResponse.setTags(blogPost.getTags());
        postResponse.setImageLink(blogPost.getImagelink());

        return postResponse;
    }


    private String postUserUsername(BlogPost blogPost){
        if(blogPost == null){
            return null;
        }

        User user = blogPost.getUser();
        if( user == null){
            return null;
        }

        String username = user.getUsername();
        if(username == null){
            return null;
        }

        return username;
    }
}
