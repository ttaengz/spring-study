package com.myspring.comment.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentModel {
    private int no;
    private String author;
    private String comment;
    private LocalDateTime date;
}
