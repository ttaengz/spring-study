package com.myspring.comment.controller;

import com.myspring.comment.model.CommentModel;
import com.myspring.comment.model.UserModel;
import com.myspring.comment.service.CommentService;
import com.myspring.comment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    // 댓글 목록 표시
    @GetMapping("/comments")
    public String getAllCommentList(Principal principal, Model model) {

        // 로그인 상태일 경우 유저 정보는 다음과 같이 구할 수 있다. (로그인 정보가 principal객체로 전달됨)
        // 매번 구하지 말고 좀 더 효율적으로 사용할 방법이 있을까?
        if (principal != null) {
            model.addAttribute("userName",
                    Optional.ofNullable(userService.getUser(principal.getName()))
                            .map(UserModel::getUserName)
                            .orElse(""));
        }

        List<CommentModel> commentList = commentService.getAllCommentList();

        model.addAttribute("commentList", commentList);
        return "main";
    }

    // 댓글 등록 처리
    @PostMapping("/comments")
    public String createComment(CommentModel comment) {
        commentService.createComment(comment);

        // 등록 작업을 처리하고 난 후 main 페이지로 다시 redirect 시킴
        return "redirect:/";
    }

    // 댓글 수정 폼
    @GetMapping("/comments/{no}")
    public String modifyForm(@PathVariable int no, Model model) {

        CommentModel commentModel = commentService.getComment(no);

        model.addAttribute("comment", commentModel);
        return "modify";
    }

    @PostMapping("/comments/{no}")
    public String modifyComment(@PathVariable int no, CommentModel comment) {
        comment.setNo(no);
        commentService.updateComment(comment);

        return "redirect:/";
    }
}
