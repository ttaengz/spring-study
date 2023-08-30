package com.myspring.comment.service;

import com.myspring.comment.dao.CommentDAO;
import com.myspring.comment.model.CommentModel;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Service
public class CommentService {

    @Autowired
    private CommentDAO commentDAO;

    /*
        리스트 조회 (Read)
     */
    public List<CommentModel> getAllCommentList() {
        return commentDAO.selectAllCommentList();
    }

    /*
        조회 (Read)
     */
    public CommentModel getComment(int no) {
        return commentDAO.selectComment(no);
    }

    /*
        등록 (Create)
     */
    public int createComment(@Valid CommentModel commentModel) {
        commentDAO.insertComment(commentModel);
        return commentModel.getNo(); // 신규 생성된 글 번호 리턴
    }

    /*
        수정 (Update)
     */
    public int updateComment(CommentModel commentModel) {
        return commentDAO.updateComment(commentModel.getNo(), commentModel.getComment());
    }

    /*
        삭제 (Delete)
     */
    public int deleteComment(int no) {
        return commentDAO.deleteComment(no);
    }
}
