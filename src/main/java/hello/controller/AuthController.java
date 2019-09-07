package hello.controller;

import hello.Entity.LoginResult;
import hello.Entity.Result;
import hello.Entity.User;
import hello.service.AuthService;
import hello.service.UserService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class AuthController {
    private AuthenticationManager authenticationManager;
    private UserService userService;
    private AuthService authService;

    @Inject
    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          AuthService authService) {
        this.userService=userService;
        this.authenticationManager = authenticationManager;
        this.authService = authService;
    }

    @GetMapping("/auth")
    @ResponseBody
    public LoginResult auth(){
        return authService.getCurrentUser()
                .map(LoginResult::success)
                .orElse(LoginResult.success("用户没有登录",false));
    }
    @PostMapping("/auth/register")
    @ResponseBody
    public LoginResult register(@RequestBody Map<String ,String> usernameAndPassword){
        String username = usernameAndPassword.get("username");
        String password  = usernameAndPassword.get("password");
        if(username==null||password==null){
            return  LoginResult.failure("用户名和密码不能为空！");
        }
        if(username.length()<2||username.length()>15){
            return LoginResult.failure("invalid username");
        }
        if(password.length()<4||password.length()>16){
            return  LoginResult.failure("invalid password");
        }
        try {
            userService.save(username, password);
        }catch (DuplicateKeyException e){
            return  LoginResult.failure("用户已经存在");
        }
        return LoginResult.success("注册成功",userService.getUserByUsername(username));


    }

    @GetMapping("/auth/logout")
    @ResponseBody
    public Object logout(){
//        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
//        User loggedInUser = userService.getUserByUsername(userName);
//        if(loggedInUser==null){
//            return  LoginResult.failure("用户没有登录");
//        }else{
//            SecurityContextHolder.clearContext();
//
//            return LoginResult.success("ok","注销成功",false);
//        }
        SecurityContextHolder.clearContext();
        return authService.getCurrentUser()
                .map(user->LoginResult.success("success",false))
                .orElse(LoginResult.failure("用户没有登录"));
    }
    @PostMapping("/auth/login")
    @ResponseBody
    public Object login(@RequestBody Map<String,Object> usernameAndPassword){
//        if(request.getHeader("user-agent")==null||request.getHeader("user-agent").contains("Mozilla")){
//            return "爬是爬不了的";
//        }

        String username = usernameAndPassword.get("username").toString();
        String password  = usernameAndPassword.get("password").toString();

        UserDetails userDetails;
        try {
            userDetails = userService.loadUserByUsername(username);
        }catch (UsernameNotFoundException e){
            return LoginResult.failure("用户不存在");
        }
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails,password,userDetails.getAuthorities());

        try {
            authenticationManager.authenticate(token);

            SecurityContextHolder.getContext().setAuthentication(token);

            return LoginResult.success("登录成功",userService.getUserByUsername(username));
        }catch (BadCredentialsException e){
            return LoginResult.failure("密码不正确");
        }
    }

}
