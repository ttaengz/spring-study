# Spring Validation

Spring에서 데이터를 주고 받을 때 형식과 데이터타입을 강제하여 데이터의 유효성을 검증하고 이를 처리하는 방법을 알아보자.

## 데이터 검증의 Flow
 - Controller는 `@RequestBody`, `@RequestParam` `@PathVariable` 어노테이션이 붙은 변수들에 클라이언트에서 요청 시 전송되는 값들을 맵핑한다.
 - 이때 `@Valid`, `@Validated`어노테이션이 있다면 데이터 유효성을 검증
   - 검증을 통과한 경우
     - API는 데이터를 처리한 후 클라이언트로 성공(200 OK) 응답.
   - 검증 실패 시
     -  MethodArgumentNotValidException 에러가 발생한다.
     -  `@ControllerAdvice` / `@ExceptionHandler` 로 구성한 ‘GlobalException’에서 해당 에러를 캐치 후 클라이언트로 에러 응답을 전송한다.

## @Valid와 @Validated

### @Valid
 - @Valid는 JSR-303 표준 스펙(자바 진영 스펙)
 - 빈 검증기(Bean Validator)를 이용해 객체의 제약 조건을 검증하도록 지시하는 어노테이션.
 - JSR 표준의 빈 검증 기술의 특징은 객체의 필드에 달린 어노테이션으로 편리하게 검증을 수행할 수 있다.
 - http요청이 컨트롤러로 전달 될 때 컨트롤러 메소드의 객체를 만들어주는 ArgumentResolver가 동작하는데, @Valid어노테이션이 있다면 ArgumentResolver에 의해 처리된다.
 - Spring에서는 LocalValidatorFactoryBean이 제약 조건 검증을 처리하며, 아래의 의존성을 추가해주면 해당 기능들이 자동 설정된다.

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
        <version>3.1.2</version>
    </dependency>
    ```

### @Validated
 - @Valid의 경우 Controller에서만 처리할 수 있고, 입력 파라미터의 검증은 최대한 Controller에서 검증 후 넘겨주는 것이 좋다.
 - 불가피하게 다른 곳(Service 등)에서 파라미터 검증이 필요할 경우 Spring에서 제공하는 @Validated를 사용할 수 있다.
 - @Validated는 JSR 표준 기술은 아니며 Spring 프레임워크에서 제공하는 어노테이션 및 기능이다.
 - 다음과 같이 클래스에 @Validated를 붙여주고, 유효성을 검증할 메소드의 파라미터에 @Valid를 붙여주면 유효성 검증이 진행된다.

    ```java
    @Service
    @Validated
    public class UserService {

      public void addUser(@Valid AddUserRequest addUserRequest) {
        //...
      }
    }
    ```

 - @Validated를 클래스 레벨에 선언하면 해당 클래스에 유효성 검증을 위한 AOP의 어드바이스 또는 인터셉터(MethodValidationInterceptor)가 등록된다. 그리고 해당 클래스의 메소드들이 호출될 때 AOP의 포인트 컷으로써 요청을 가로채서 유효성 검증을 진행한다.
 - @Validated가 검증 실패 시 ConstraintViolationException예외를 발생시킨다.

### @Validated 유효성 검증 그룹의 지정
 - 동일한 클래스에 대해 제약조건이 요청에 따라 달라질 수 있다.
 - 예를 들어 일반 사용자의 요청과 관리자의 요청이 동일한 클래스로 처리될 때, 다른 제약 조건이 적용되어야 할 경우
 - Spring은 이를 위해 제약 조건이 적용될 검증 그룹을 지정할 수 있는 기능 역시 @Validated를 통해 제공한다.
 - 검증 그룹을 지정하기 위해서는 (내용이 없는) 마커 인터페이스를 그룹 별로 정의한다. 

    ```java
    public interface UserValidationGroup {} 
    public interface AdminValidationGroup {}
    ```

 - 해당 제약 조건이 적용될 그룹을 groups로 지정해줄 수 있다.

    ```java
    @NotEmpty(groups = {UserValidationGroup.class, AdminValidationGroup.class} ) 
    private String name; 

    @NotEmpty(groups = UserValidationGroup.class) 
    private String userId; 

    @NotEmpty(groups = AdminValidationGroup.class) 
    private String adminId;
    ```
 - 그리고 컨트롤러에서도 다음과 같이 제약조건 검증을 적용할 클래스를 지정해준다.

    ```java
    @PostMapping("/users") 
    public ResponseEntity<Void> addUser(
        @RequestBody @Validated(UserValidationGroup.class) AddUserRequest addUserRequest) {
        //  ...
    }
    ```
 - 위와 같이 UserValidationGroup를 @Validated의 파라미터로 넣어주었다면 UserValidationGroup에 해당하는 제약 조건만 검증이 된다.
 - @Validated에 특정 클래스를 지정하지 않는 경우: groups가 없는 속성들만 처리
 - @Valid or @Validated에 특정 클래스를 지정한 경우: 지정된 클래스를 groups로 가진 제약사항만 처리

### @Valid와 @Validated 유효성 검증 차이(요약)
 - @Valid
   -JSR-303 자바 표준 스펙
   - 특정 ArgumentResolver를 통해 진행되어 컨트롤러 메소드의 유효성 검증만 가능하다.
   - 유효성 검증에 실패할 경우 MethodArgumentNotValidException이 발생한다.
 - @Validated
   - 자바 표준 스펙이 아닌 스프링 프레임워크가 제공하는 기능
   - AOP를 기반으로 스프링 빈의 유효성 검증을 위해 사용되며 클래스에는 @Validated를, 메소드에는 @Valid를 붙여주어야 한다.
   - 유효성 검증에 실패할 경우 ConstraintViolationException이 발생한다.


## 제약조건 어노테이션
 [JSR 표준 스펙은 다양한 제약 조건 어노테이션을 제공](https://javaee.github.io/javaee-spec/javadocs/javax/validation/constraints/package-summary.html)하고 있는데, 대표적인 어노테이션으로는 다음과 같은 것들이 있다.

 - @NotNull: 해당 값이 null이 아닌지 검증함
 - @NotEmpty: 해당 값이 null이 아니고, 빈 스트링("") 아닌지 검증함(" "은 허용됨)
 - @NotBlank: 해당 값이 null이 아니고, 공백(""과 " " 모두 포함)이 아닌지 검증함
 - @AssertTrue: 해당 값이 true인지 검증함
 - @Size: 해당 값이 주어진 값 사이에 해당하는지 검증함(String, Collection, Map, Array에도 적용 가능)
 - @Min: 해당 값이 주어진 값보다 작지 않은지 검증함
 - @Max: 해당 값이 주어진 값보다 크지 않은지 검증함
 - @Pattern: 해당 값이 주어진 정규식 패턴과 일치하는지 검증함

## @Valid를 이용한 검증 적용해보기

### Model(VO)객체 필드에 다음과 같이 제약 조건 어노테이션을 설정한다.

```java
@Data
public class CommentModel {
    private int no;

    @NotBlank(message = "작성자 항목은 필수입니다.")
    private String author;
    @NotNull
    private String comment;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime date;
}
```

### Controller파라미터에 @Valid어노티에션 설정
 - 다음과 같이 컨트롤러의 메소드에 @Valid를 붙여주면 유효성 검증이 진행된다.

    ```java
    // 댓글 등록을 처리한다.
    @PostMapping("/comment")
    public String createComment(@Valid CommentModel comment, Model model) {
        commentService.createComment(comment);

        return "redirect:/";
    }
    ```
 - 서버를 띄우고 작성자 항목을 비워둔 채로 댓글을 등록해보면 오류 페이지와 함께 로그에 MethodArgumentNotValidException예외가 발생함을 볼 수 있다.

### 오류 페이지 작성
 - templates폴더에 다음과 같이 error.html 파일을 작성해두면 오류 발생 시 작성한 오류 화면이 표시된다.
 - message를 커스텀 하려면 ExceptionHandler를 구현하여 에러 메시지를 넘겨준다. 

    ```html
    <!DOCTYPE html>
    <html lang="ko">
    <head>
        <meta charset="UTF-8">
        <title>Error</title>
    </head>
    <body>
        <h1>오류가 발생했습니다.</h1>
        <h2 th:text="${message}"></h2>
    </body>
    </html>
    ```

### ExceptionHandler 구현하기
 - 발생한 오류를 상황에 따라 적절히 처리해보자.
 - 다음과 같이 @ExceptionHandler어노테이션을 활용하여 컨트롤러에서 throw된 에러를 처리할 수 있다.

    ```java
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidError(Model model, MethodArgumentNotValidException ex) {

        // 다음과 같이 message를 가져오는 경우 NullPointerException 발생 위험이 있다.
        //String message = ex.getFieldError().getDefaultMessage();

        // Optional을 활용하여 메시지가 있다면 가져오고 null인 경우 "알 수 없는 오류"와 같이 기본 메시지를 설정한다.
        String message = Optional.ofNullable(ex.getFieldError())
                .map(FieldError::getDefaultMessage)
                .orElse("알 수 없는 오류");

        model.addAttribute("message", message);
        return "error"; // error.html로 넘긴다.
    }
    ```
 - ExceptionHandler는 컨트롤러에 작성할 수도 있지만 모든 컨트롤러에서 발생하는 에러를 처리하기 위해 @ControllerAdvice클래스를 생성하고 구현하는것이 좋다.
 - 다음은 모든 Controller에서 발생하는 에러를 처리하기 위한 목적으로 구현한 GlobalExceptionHandler클래스의 예시이다.

    ```java
    package com.myspring.comment.controller;

    import org.springframework.ui.Model;
    import org.springframework.validation.FieldError;
    import org.springframework.web.bind.MethodArgumentNotValidException;
    import org.springframework.web.bind.annotation.ControllerAdvice;
    import org.springframework.web.bind.annotation.ExceptionHandler;

    import java.util.Optional;

    // @RestControllerAdvice // 에러 결과를 JSON 형식의 응답 사용 시
    @ControllerAdvice
    public class GlobalExceptionHandler {


        @ExceptionHandler(MethodArgumentNotValidException.class)
        public String handleValidError(Model model, MethodArgumentNotValidException ex) {


            String message = Optional.ofNullable(ex.getFieldError())
                    .map(FieldError::getDefaultMessage)
                    .orElse("알 수 없는 오류");

            model.addAttribute("message", message);
            return "error";
        }
    }
    ```
## 에러 페이지로 보내지 않고 검증만 하고싶은 경우
 - 다음과 같이 검증할 객체 바로 다음에 BindingResult 객체를 받으면 예외를 throw하는 대신 검증 정보가 BindingResult로 전달된다.

    ```java
    // 댓글 등록을 처리한다.
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public String createComment(@Valid CommentModel comment, BindingResult bindingResult) {

        if (bindingResult.hasFieldErrors()) {
            System.out.println("검증 실패한 부분이 있습니다!");
            // 무언가 처리
            comment.setAuthor("익명");
        }
        commentService.createComment(comment);

        // 등록 작업을 처리하고 난 후 main 페이지로 다시 redirect 시킴
        return "redirect:/";
    }
    ```

## 커스텀 어노테이션 만들어 직접 Validation 검사

다음과 같이 제약 조건 어노테이션을 직접 만들어 작성자명이 "관리자"로 들어오는 경우를 검증해보자

 - validator패키지를 생성한 후 다음과 같이 Author라는 이름의 어노테이션을 작성한다.

```java
package com.myspring.comment.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = AuthorValidator.class)
@Documented
public @interface Author {
    String message() default "작성자명이 잘못되었습니다.";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
```

여기에 사용된 어노테이션들은 각각 다음의 역할을 한다.

 - @Target(FIELD): 해당 어노테이션을 필드에만 선언 가능함
 - @Retention(RUNTIME): 해당 어노테이션이 유지되는 시간으로써 런타임까지 유효함
 - @Constraint(validatedBy = PhoeValidator.class): PhoeValidator를 통해 유효성 검사를 진행함
 - @Documented: JavaDoc 생성시 Annotation에 대한 정보도 함께 생성

어노테이션 내부의 message, groups, payload 속성들은 JSR-303 표준 어노테이션들이 갖는 공통 속성들이다.
 - message: 유효하지 않을 경우 반환할 메세지
 - groups: 유효성 검증이 진행될 그룹
 - payload: 유효성 검증 시에 전달할 메타 정보


어노테이션을 만들었다면 실제 검증할 Validator클래스를 작성해보자
 - validator패키지에 다음과 같이 클래스를 작성한다.
 - isValid메서드의 첫 번째 String 파라미터로 검증할 대상 값이 넘어온다.
 - isValid 메서드에는 검증 성공 시 true를, 실패 시 false를 리턴시킨다.

```java
package com.myspring.comment.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.thymeleaf.util.StringUtils;

public class AuthorValidator implements ConstraintValidator<Author, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.equals("관리자", s)) {
            return false;
        }
        return true;
    }
}

```

모델(VO) 객체에 다음과 같이 커스텀 어노테이션을 사용해본다.
```java
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
```

이제 어플리케이션을 다시 구동시키고 댓글 작성자 부분을 "관리자"로 쓰고 등록해보자. 
 - "작성자명이 잘못되었습니다"라는 메시지를 볼 수 있다.
