package hello.controller;

import hello.Entity.*;
import hello.service.AuthService;
import hello.service.BlogService;
import hello.utils.AssertUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.util.Map;

@Controller
public class BLogController {
    private final AuthService authService;
    private final BlogService blogService;

    @Inject
    public BLogController(AuthService authService,BlogService blogService) {
        this.blogService = blogService;
        this.authService = authService;
    }
    @GetMapping("/blog")
    @ResponseBody
    public Result getBlogs(@RequestParam("page") Integer page,@RequestParam(value="userId",required = false) Integer userId){
        if(page==null||page<0){
            page=1;
        }
        return blogService.getBlogs(page,10,userId);
    }

    @GetMapping("/blog/{blogId}")
    @ResponseBody
    public BlogResult getBlog(@PathVariable("blogId") int blogId){
        return blogService.getBlogById(blogId);
    }

    @PostMapping("/blog")
    @ResponseBody
    public BlogResult newBlog(@RequestBody Map<String,String> param){
        try{
            return authService.getCurrentUser()
                    .map(user -> blogService.insertBlog(fromParam(param,user)))
                    .orElse(BlogResult.failure("登录后才能操作"));
        }catch (IllegalArgumentException e){
            return BlogResult.failure(e);
        }
    }

    @PatchMapping("/blog/{blogId}")
    @ResponseBody
    public BlogResult updateBlog(@PathVariable("blogId") int blogId,@RequestBody Map<String, String> param){
        try{
            return authService.getCurrentUser()
                    .map(user -> blogService.updateBlog(blogId,fromParam(param,user)))
                    .orElse(BlogResult.failure("登录后才能操作"));
        }catch (IllegalArgumentException e){
            return  BlogResult.failure(e);
        }

    }

    @DeleteMapping("/blog/{blogId}")
    @ResponseBody
    public Result deleteBlog(@PathVariable("blogId") int blogId) {
        try {
            return authService.getCurrentUser()
                    .map(user -> blogService.deleteBlog(blogId, user))
                    .orElse(BlogResult.failure("登录后才能操作"));
        } catch (IllegalArgumentException e) {
            return BlogResult.failure(e);
        }
    }


    private Blog fromParam(Map<String, String> params, User user) {
        Blog blog = new Blog();
        String title = params.get("title");
        String content = params.get("content");
        String description = params.get("description");

        AssertUtils.assertTrue(StringUtils.isNotBlank(title) && title.length() < 100, "title is invalid!");
        AssertUtils.assertTrue(StringUtils.isNotBlank(content) && content.length() < 10000, "content is invalid");

        if (StringUtils.isBlank(description)) {
            description = content.substring(0, Math.min(content.length(), 10)) + "...";
        }

        blog.setTitle(title);
        blog.setContent(content);
        blog.setDescription(description);
        blog.setUser(user);
        return blog;
    }
}
