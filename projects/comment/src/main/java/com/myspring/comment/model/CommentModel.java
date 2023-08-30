package com.myspring.comment.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.myspring.comment.validator.Author;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentModel {
    private int no;

    @Author
    @NotBlank(message = "작성자 항목은 필수입니다.")
    private String author;
    @NotNull
    private String comment;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime date;
}
