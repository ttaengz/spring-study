package com.myspring.comment.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserModel {
    @Size(min = 3, max = 15)
    @NotEmpty(message = "사용자 ID는 필수 항목입니다.")
    private String userId;
    @Size(min = 2, max = 15)
    @NotEmpty(message = "사용자 이름은 필수 항목입니다.")
    private String userName;
    @Size(min = 4, max = 20)
    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    private String password;
    private String email;
    private LocalDateTime joinDatetime;
}
