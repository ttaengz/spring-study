package com.myspring.comment.controller;

import com.myspring.comment.model.CommentModel;
import com.myspring.comment.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class MainController {

    @Autowired
    private CommentService commentService;

    /*
        메인 페이지 - 댓글 목록을 표시
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getAllCommentList(Model model) {
        List<CommentModel> commentList = commentService.getAllCommentList();

        model.addAttribute("commentList", commentList);
        return "main";
    }

    /*
        댓글 등록을 처리한다.
     */
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public String createComment(CommentModel comment, Model model) {
        commentService.createComment(comment);

        // 등록 작업을 처리하고 난 후 main 페이지로 다시 redirect 시킴
        return "redirect:/";
    }
}
