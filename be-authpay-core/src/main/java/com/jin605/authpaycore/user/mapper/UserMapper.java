package com.jin605.authpaycore.user.mapper;

import com.jin605.authpaycore.user.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findById(@Param("id") Long id);
    User findByEmail(@Param("email") String email);
    User findByNickname(@Param("nickname") String nickname);
    int insert(User user);
    int updateImageUrl(@Param("id") Long id, @Param("imageUrl") String imageUrl);

}
