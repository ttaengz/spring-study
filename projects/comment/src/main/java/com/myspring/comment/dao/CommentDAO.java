package com.myspring.comment.dao;

import com.myspring.comment.model.CommentModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CommentDAO {
    List<CommentModel> selectAllCommentList();

    CommentModel selectComment(@Param("no") int no);

    int insertComment(CommentModel postModel);

    int updateComment(@Param("no") int no, @Param("comment") String comment);

    int deleteComment(@Param("no") int no);

}
