package hello.service;

import hello.Entity.User;
import hello.dao.UserMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceTest  {

   @Mock
    BCryptPasswordEncoder mockEncoder;
   @Mock
    UserMapper mockMapper;
   @InjectMocks
    UserService userService ;
   @Test
   void testSave(){
       when(mockEncoder.encode("Password")).thenReturn("password");
       userService.save("myUser","Password");
       verify(mockMapper).save("myUser","password");

   }
   @Test
    void testGetUserByUsername(){
       userService.getUserByUsername("myUser");
       verify(mockMapper).findUserByName("myUser");
   }
   @Test
    void throwExceptionWhenUserNotFound(){
       Assertions.assertThrows(UsernameNotFoundException.class,
               ()->userService.loadUserByUsername("myUser"));
   }
   @Test
    void returnUserDetailsWhenUserFound(){
       when(mockMapper
               .findUserByName("myUser"))
               .thenReturn(new User(123,"myUser","password"));
       UserDetails userDetails = userService.loadUserByUsername("myUser");
       Assertions.assertEquals("myUser",userDetails.getUsername());
       Assertions.assertEquals("password",userDetails.getPassword());
    }

}