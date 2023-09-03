package com.blogwebsite.blogwebapp.rest;


import com.blogwebsite.blogwebapp.model.Posts;
import com.blogwebsite.blogwebapp.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class PostRestController {
    final int PAGE_LIMIT = 10;
    private final PostService postService;

    @Autowired
    public PostRestController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts")
    public List<Posts> getPosts() {
        return postService.findAll();
    }

    @GetMapping("/posts/{id}")
    public Posts getPostById(@PathVariable int id) {
        Posts post = postService.findPostById(id);

        if (post != null) {
            return post;
        } else {
            throw new RuntimeException("Post not found");
        }

    }

    @PostMapping("/posts")
    public Posts savePost(@RequestBody Posts posts) {
        return postService.saveReturnObject(posts);
    }

    @PutMapping("/posts/{id}")
    public Posts updatePost(@PathVariable int id, @RequestBody Posts posts) {
        Posts post = postService.findPostById(id);

        if (post == null) {
            throw new RuntimeException("Post not found");
        }

        posts.setId(id);
        postService.updateCompletePost(posts);
        return postService.findPostById(id);
    }

    @DeleteMapping("/posts/{id}")
    public String deletePost(@PathVariable int id) {
        Posts post = postService.findPostById(id);

        if (post == null) {
            throw new RuntimeException("Post not found");
        }

        postService.deletePostById(id);
        return "Post deleted id: " + id;
    }

    @GetMapping("/posts/next/{page}")
    public List<Posts> nextPage(@PathVariable(name = "page", required = false) int page) {
        return postService.findOnlyTenPost(PAGE_LIMIT, page * PAGE_LIMIT);
    }

    @GetMapping("/posts/previous/{page}")
    public List<Posts> previousPage(@PathVariable(name = "page", required = false) int page) {
        List<Posts> postsList = new ArrayList<>();

        if (page < 0) {
            throw new RuntimeException("Page cannot be less than 0");
        } else {
            postsList = postService.findOnlyTenPost(PAGE_LIMIT, page / PAGE_LIMIT);
        }

        return postsList;
    }

    @GetMapping("/posts/search/{search}")
    public List<Posts> search(@PathVariable String search) {
        List<Posts> postsList = new ArrayList<>();

        if (search.isEmpty()) {
            throw new RuntimeException("Search field cannot be empty");
        } else {
            postsList = postService.findPostByTagTitleContentAuthorExcerpt(search);
        }

        return postsList;
    }

    @GetMapping("/posts/sort/{order}")
    public List<Posts> sortByPublished(@PathVariable(name = "order", required = false) String order) {

        if (order.equals("asc")) {
            return postService.findPostSortedByPublishedASC();
        } else if (order.equals("desc")) {
            return postService.findPostSortedByPublishedDesc();
        } else {
            throw new RuntimeException("Invalid order");
        }

    }

    @GetMapping("/posts/filter")
    public Set<Posts> filterByAuthors(@RequestParam(name = "authorName", required = false) String[] authorName,
                                      @RequestParam(name = "tagId", required = false) Long[] tagId,
                                      @RequestParam(name = "dateStart", required = false) String dateStart,
                                      @RequestParam(name = "dateEnd", required = false) String dateEnd) {
        Set<Posts> postList = new HashSet<>();

        if (dateStart != null && dateEnd != null) {
            String datePattern = "\\d{4}-\\d{2}-\\d{2}";
            Pattern pattern = Pattern.compile(datePattern);
            Matcher matcher1 = pattern.matcher(dateStart);
            Matcher matcher2 = pattern.matcher(dateEnd);

            if(matcher1.matches() && matcher2.matches()){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate startDate = LocalDate.parse(dateStart, formatter);
                LocalDate endDate = LocalDate.parse(dateEnd, formatter);
                LocalDateTime startDateTime = startDate.atTime(LocalTime.MIDNIGHT);
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MIDNIGHT);
                postList.addAll(postService.findByStartDateAndEndDate(startDateTime, endDateTime));
            }else {
                throw new RuntimeException("Invalid date");
            }
        }

        if (tagId != null) {
            postList.addAll(postService.findPostByTagIds(tagId));
        }

        if (authorName != null) {
            postList.addAll(postService.findPostByAuthorName(authorName));
        }

        return postList;
    }

}
