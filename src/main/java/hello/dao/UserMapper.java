package hello.dao;

import hello.Entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Select("select*from user where username =#{username}")
    User findUserByName(@Param("username") String username);

    @Select("insert into user(username, password, created_at,updated_at) "+
    "values(#{username}, #{password}, now(),now())")
    void save(@Param("username") String username, @Param("password") String password);
}
