package com.myspring.comment.dao;

import com.myspring.comment.model.UserModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserDAO {
    void insertUser(UserModel userModel);

    UserModel selectUser(@Param("userId") String userId);
}
