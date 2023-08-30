package com.myspring.comment.controller;

import com.myspring.comment.model.CommentModel;
import com.myspring.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CommentRestController {
    @Autowired
    private CommentService commentService;

    @GetMapping(value = "/api/comments")
    public List<CommentModel> getAllCommentList() {
        return commentService.getAllCommentList();
    }

    /*
        댓글 삭제를 처리한다.
     */
    // @RequestMapping(value = "/api/comment/{no}", method = RequestMethod.DELETE)
    @DeleteMapping(value = "/api/comment/{no}")
    public String deleteComment(@PathVariable(value = "no") int no, Model model) {
        commentService.deleteComment(no);

        return "OK";
    }
}
