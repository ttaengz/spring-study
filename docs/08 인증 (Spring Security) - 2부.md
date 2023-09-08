# Spring Security 2부

## 인증 및 인가가 필요한 리소스(페이지) 처리
 글 수정 및 삭제에 대하여 적절한 권한이 있는 사용자에 대해서만 동작할 수 있도록 Spring Security를 활용하여 설정해보자.<br>
 글 수정 및 삭제의 경우 관리자 또는 해당 글을 작성한 사람이 수행할 수 있다.

 - 인증이 필요한 리소스에 대해서 config (실습 프로젝트에서는 SecurityConfig.java에 설정하였다.)에서 다음과 같이 설정하여 접근 제어를 설정할 수 있다. 
 - 관리자만 댓글 수정 가능하도록 제한하는 예시
    ```java
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .requestMatchers(new AntPathRequestMatcher("/comments/*")).hasAuthority("ROLE_ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/**")).permitAll()
                )
                /* ... 생략... */
        ;
        return http.build();
    }
    ```
 - requestMatchers 설정 시 우선순위대로 명시해야 하며 인증이 필요한 모든 URL을 설정하기에는 번거롭기 때문에 아래와 같이 어노테이션을 활용하여 메소드레벨에서 설정하는 방법을 주로 사용한다.

### Security 메소드 어노테이션
 - **@Secured** : 각 요청경로에 따른 권한 설정은 config에서 할 수 있지만, 메소드레벨에서 `@Secured`를 통해서도 할 수 있다.
   - 우선 config에서 `@EnableMethodSecurity(securedEnabled = true) `어노테이션을 통해 활성화한다.
   - `@Secured("ROLE_ADMIN")` , `@Secured({"ROLE_ADMIN","ROLE_USER"})`
   - 비인가자 접근 시 AccessDeniedException 발생.
 - **@PreAuthorize** : @Secured와 비슷하지만 [spEL](https://docs.spring.io/spring-framework/reference/core/expressions/beandef.html#expressions-beandef-annotation-based) 을 사용할 수 있다.
   - 메소드 호출 전에 권한을 체크한다.
   - config에 `@EnableMethodSecurity(prePostEnabled = true)`으로 활성화
   - `@PreAuthorize("hasRole('ADMIN')")`
   - 표현식에서 `#param`과 같이 파라미터에 접근할 수 있다. (아래 예시 참고)
   - 요청 객체의 user와 로그인 user가 일치하는지 또는 관리자인지 체크하는 예시
      ```java
      // UserController
      @PreAuthorize("isAuthenticated() and (( #user.name == principal.name ) or hasRole('ROLE_ADMIN'))")
      @PutMapping(value = "/")
      public String updateUser( User user ){
        //...
      }
      ```
 - **@PostAuthorize**
   - 메소드를 일단 호출하고난 후 리턴 전 권한을 체크한다.
   - DB에서 조회 된 리소스의 사용자 정보와 요청한 사용자 정보가 일치하는지 비교해볼 때 사용할 수 있다.
   - 표현식에서 `returnObject`를 통해 함수의 반환 값에 접근할 수 있다. (아래 예시 참고)
   - 본인의 댓글만 조회할 수 있는 getCommentWithAuth()메서드 예시 (CommentService.java에 적용)
      ```java
      // CommentService
      @PostAuthorize("isAuthenticated() and (( returnObject.userId == principal.username ) or hasRole('ROLE_ADMIN'))")
      public CommentModel getCommentWithAuth(int no) {
          return getComment(no);
      }

      public CommentModel getComment(int no) {
          return commentDAO.selectComment(no);
      }
      ```
 - **@AuthenticationPrincipal** : 컨트롤러단에서 세션의 정보에 접근하고 싶을 때 파라미터에 선언.
   - `public ModelAndView userInfo(@AuthenticationPrincipal User user)`
   - 이 정보는 SecurityContextHolder내에도 저장되어 있다. 
   - `(User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();`

### @Secured와 @PreAuthorize 차이
 - 두 어노테이션 모두 Spring Security의 어노테이션으로, 메소드 수준의 보안을 제공하는 데 사용되며, 기능상으로는 동일하다
 - @PreAuthroize는 [Spring EL(표현식)](https://docs.spring.io/spring-framework/reference/core/expressions/beandef.html#expressions-beandef-annotation-based)을 사용할 수 있고, AND나 OR 같은 표현식을 사용할 수 있다.

    ```
    // ROLE_USER와 ROLE_ADMIN 두 개의 권한을 가져야 접근 가능
    @PreAuthorize("hasRole('ROLE_USER') and hasRole('ROLE_ADMIN')")
    ```
 - @Secured는 표현식을 사용할 수 없고 OR만 표현할 수 있다.

    ```
    // ROLE_USER 또는 ROLE_ADMIN 중 하나의 권한을 가지고 있다면 접근 가능
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    ```


**Spring Security 표현식**

|표현식 | 설명 |
|-|-|
hasRole(String role)|해당 롤을 가지고 있는 경우 true
hasAnyRole(String… roles)|해당 롤 중에 하나를 가지고 있는 경우 true
isAnonymous()|익명 사용자인 경우 true
isRememberMe()|Remember Me 인증을 통해 로그인한 경우 true
isAuthenticated()|이미 인증된 사용자인 경우 true
isFullyAuthenticated()|Remember Me가 아닌 일반적인 인증 방법으로 로그인한 경우 true
permitAll|항상 true
denyAll|항상 false
principal|인증된 사용자의 사용자 정보(UserDetails 구현한 클래스의 객체) 반환
authentication|인증된 사용자의 인증 정보**(Authentication** 구현한 클래스의 객체) 반환

## 유저 정보를 Controller에서 전달 받는 방법
 로그인 후 로그인 사용자 정보를 View에 넘겨 표시하는 부분이 있었다. 또는 요청을 처리하는 데 사용자 정보가 필요한 부분이 있을 것이다. Controller에서 인증된 유저의 정보에 접근하는 방법을 알아보자. 

### SecurityContextHolder에서 꺼내오기
 - 사용자가 로그인하게 되면 인증 정보(Principal)를 SecurityContextHolder객체에 보관하게 되는데 여기에서 꺼내오는 방법
 - Principal의 username필드에 id를 설정하였으므로 `principal.getUsername();`으로 유저 아이디를 알아올 수 있다.

    ```java
   Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
   if(!StringUtils.equals("anonymousUser", principal)) {
       System.out.println(((UserDetails) principal).getUsername());
   }
    ```

### Controller에서 파라미터로 Principal객체를 주입받기
 - 다음과 같이 Controller에서 Principal객체를 파라미터로 받아올 수 있다.
 - 필요한 경우 `principal.getUsername();`으로 userId를 구한 후 DB에서 유저에 대한 부가 정보를 select할 수 있다. (DB접근이 한 번 더 발생하는 단점이 있었다.)
    ```java
    @Controller 
    public class SecurityController { 
        @GetMapping("/username") 
        @ResponseBody 
        public String currentUserName(Principal principal) { 
            return principal.getName(); 
        } 
    }
    ```

### @AuthenticationPrincipal 어노테이션 활용
 - Spring Security 3.2 부터는 annotation을 이용하여 현재 로그인한 사용자 객체를 인자로 주입받을 수 있다.
    ```java
    @GetMapping("/comments/{no}")
    public String modifyForm(@PathVariable int no, @AuthenticationPrincipal User user) {/*...*/}
    ```
 - 이 때 User객체는 UserSecurityService (UserDetailsService 구현체)에서 인증을 진행하는 `loadUserByUsername()`메서드에서 리턴하는 유저 객체(UserDetails 구현체)이다.
 - 즉 UserSecurityService.loadUserByUsername()에서 인증을 위해 유저 정보를 select하는 부분이 있으므로 **필요한 정보를 포함해서 return할 수 있도록 User를 상속하여 우리만의 LoginUser객체를 만들어 리턴**하면 Controller에서 주입 받아 로그인한 유저 정보를 활용할 수 있다.

### 커스텀 User객체
Controller에서 @AuthenticationPrincipal어노테이션을 활용하여 로그인한 유저 정보를 주입 받아 사용하기 위해 필요한 정보를 커스텀하여 User객체를 생성해보자.

 - 다음과 같이 우리 프로젝트에서 User테이블에 대응하는 UserModel객체를 품고 있는 LoginUser클래스를 구현한다.
 - 이때 스프링 시큐리티의 User (UserDetails 기본 구현체)를 상속받아 구현한다.
    
    ```java
    @Getter
    public class LoginUser extends User {
        private UserModel user;

        // 생성자
        public LoginUser(UserModel user, Collection<? extends GrantedAuthority> authorities) {
            super(user.getUserId(), user.getPassword(), authorities);
            this.user = user;
        }

        // UserModel객체와 authorities정보를 받아서 객체를 생성해주는 정적 메서드
        public static LoginUser valueOf(UserModel user, Collection<? extends GrantedAuthority> authorities) {
            return new LoginUser(user, authorities);
        }
    }
    ```

 - 스프링 시큐리티에서 DB를 조회하여 인증 로직을 담당하는 UserSecurityService (UserDetailsService)의 loadUserByUsername() 메서드를 다음과 같이 위에서 만든 LoginUser객체를 리턴하도록 수정한다.

    ```java
    @Service
    public class UserSecurityService implements UserDetailsService {
        @Autowired
        UserDAO userDAO;

        @Override
        public User loadUserByUsername(String userId) throws UsernameNotFoundException {

            UserModel user = userDAO.selectUser(userId);

            if (ObjectUtils.isEmpty(user)) {
                throw new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
            }

            List<GrantedAuthority> authorities = new ArrayList<>();

            if ("admin".equals(user.getUserId())) {
                authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
            } else {
                authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
            }

            return LoginUser.valueOf(user, authorities);
            //return new User(user.getUserId(), user.getPassword(), authorities);
        }
    }
    ```

 - 다음과 같이 Controller에서 LoginUser정보를 사용할 수 있다.

    ```java
    @GetMapping("/comments/{no}")
    public String modifyForm(@PathVariable int no, @AuthenticationPrincipal LoginUser user, Model model) {

        if (user != null) {
            System.out.println("로그인유저 : " + user.getUser().getUserName());
            model.addAttribute("user", user.getUser());
        }

        CommentModel commentModel = commentService.getCommentWithAuth(no);
        
        model.addAttribute("comment", commentModel);
        return "modify";
    }
    ```

## 권한이 없을 경우 처리

Spring Security를 적용해서 HTTP 요청에 대해 인증 및 인가를 적용할 경우 시큐리티 필터 체인에 의해 인증 여부나 인가 여부에 따라 요청이 수락되거나 거절된다. <br><br>
필터 체인의 구현은 서블릿 필터 단계에 속하는 부분이기 때문에 (Controller도달 전 단계이다.) `@ControllerAdvice` 같은 예외 처리기로 처리할 수 없다. <br><br>
따라서 별도의 권한 에러 처리나 API의 경우 JSON 응답을 커스텀하려면 별도로 설정이 필요하다.

### AuthenticationEntryPoint, AccessDeniedHandler
 Spring Security에 의해서 차단된 경우 요청을 핸들링 할 수 있는 인터페이스이다.

#### AuthenticationEntryPoint
 - 인증 예외(AuthenticationException) 시 처리할 핸들러 인터페이스 (로그인 안되어 있을 경우)
 - 기본적으로 인증이 되어 있지 않다면 로그인 페이지로 리다이렉트 되지만 API의 경우 커스텀 Error응답을 만들어 리턴하고자 할 경우 다음과 같이 설정할 수 있다.<br><br>
 - 우선 API Error응답에 사용할 ErrorResponse객체를 다음과 같이 간단히 설계하였다.

    ```java
    // ErrorResponse.java (model 패키지)
    @Data
    @AllArgsConstructor
    public class ErrorResponse {
        private int status;
        private String message;
    }
    ```
 - AuthenticationEntryPoint 인터페이스를 다음과 같이 구현(implements)하여 config에 등록한다.
    ```java
    // ApiAuthenticationEntryPoint.java (handler 패키지)
    public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Autowired
        private ObjectMapper objectMapper;

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            // Error에 대한 Json 응답을 직접 생성한다.
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "로그인 인증 후 요청할 수 있습니다.");
            String responseBody = objectMapper.writeValueAsString(errorResponse);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(responseBody);
        }
    }
    ```
 - SecurityConfig.java에 다음과 같이 설정한다.

    ```java
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                /* ...생략... */
                /* API요청에 대한 인증 예외처리 핸들러를 등록한다. */
                .exceptionHandling((exceptionHandling) ->
                        exceptionHandling
                          .defaultAuthenticationEntryPointFor(apiAuthenticationEntryPoint(), new AntPathRequestMatcher("/api/**")));
                          
        return http.build();
    }
    ```

 - 댓글 조회 API를 다음과 같이 수정하고 로그인 없이 호출한다면 ErrorResponse응답이 JSON형태로 응답된다.

    ```java
    // CommentRestContoller.java (CommentApiController)
    @GetMapping("/api/comments/{no}")
    public CommentModel getComment(@PathVariable int no) {
        //@PostAuthorize("isAuthenticated() and ...") // 적용되어 있음
        return commentService.getCommentWithAuth(no);
    }
    ```

    ```json
    {
    "status": 403,
    "message": "로그인 인증 후 요청할 수 있습니다."
    }
    ```

### AccessDeniedHandler 
 - 인가 예외(AccessDeniedException)를 처리하는 인터페이스 (로그인 했으나 접근 권한이 없는 경우)
 - 웹의 경우 권한 오류 화면을 표시하고, API요청은 ErrorResponse에 메시지를 실어 응답하는 두 가지 경우를 처리해보자.
 - 우선 웹 오류 화면을 처리할 핸들러를 구현한다. 

    ```java
    // WebAccessDeniedHandler.java (hadler 패키지에 생성)
    public class WebAccessDeniedHandler implements AccessDeniedHandler {
        @Autowired
        private ObjectMapper objectMapper;

        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
            // 접근 거부 안내 페이지로 리다이렉트 시킨다.
            response.sendRedirect("/errors/access-denied");
        }
    }
    ```
 - 오류 페이지를 처리할 Controller를 생성
    ```java
    // MainController (또는 ErrorController)
    @GetMapping("/errors/access-denied")
    public String accessDeniedPage(Model model) {
        model.addAttribute("message", "요청 페이지에 접근할 권한이 없습니다.");
        return "error";
    }
    ```

 - API Error응답을 처리할 핸들러를 다음과 같이 구현한다.

    ```java
    // ApiAccessDeniedHandler (handler 패키지)
    public class ApiAccessDeniedHandler implements AccessDeniedHandler {
        @Autowired
        private ObjectMapper objectMapper;

        // Error에 대한 Json 응답을 직접 생성한다.
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

            // Error에 대한 Json 응답을 직접 생성한다.
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), "(API) 인가되지 않았습니다.");
            String responseBody = objectMapper.writeValueAsString(errorResponse);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write(responseBody);
        }
    }
    ```
 - config(SerucityConfig.java)를 다음과 같이 설정한다.

    ```java
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                /*...생략...*/
                .exceptionHandling((exceptionHandling) ->
                  exceptionHandling
                    .defaultAccessDeniedHandlerFor(apiAccessDeniedHandler(), new AntPathRequestMatcher("/api/**"))
                    .defaultAccessDeniedHandlerFor(accessDeniedHandler(), new AntPathRequestMatcher("/**"))
                    .defaultAuthenticationEntryPointFor(apiAuthenticationEntryPoint(), new AntPathRequestMatcher("/api/**")));

        return http.build();
    }
    ```
