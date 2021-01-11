package com.blog.blog_project.payload.response;

import com.blog.blog_project.entities.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostResponse {

    private Long id;
    private String userName;
    private String title;
    private String imageLink;
    private String blurb;
    private String fullText;
    private Set<Tag> tags;



}
