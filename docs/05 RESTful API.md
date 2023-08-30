# RESTful API

## REST란?
  REST(Representational State Transfer)의 약자로 자원을 이름으로 구분하여 해당 자원의 상태를 주고받는 모든 것을 의미한다.

  - HTTP URI(Uniform Resource Identifier)를 통해 **자원(Resource)** 을 명시하고,
  - HTTP Method(POST, GET, PUT, DELETE, PATCH 등) 를 통해 해당 자원(URI)에 대한 **CRUD Operation**을 적용하는 것을 의미.

> **CRUD Operation**
> <br>
> CRUD는 대부분의 컴퓨터 소프트웨어가 가지는 기본적인 데이터 처리 기능인 Create(생성), Read(읽기), Update(갱신), Delete(삭제)를 묶어서 일컫는 말로 REST에서의 CRUD Operation 동작 예시는 다음과 같다.
>
> - Create : 데이터 생성(POST)
> - Read : 데이터 조회(GET)
> - Update : 데이터 수정(PUT, PATCH)
> - Delete : 데이터 삭제(DELETE)

 - [HTTP의 상태 메서드는 이곳을 참고](https://developer.mozilla.org/ko/docs/Web/HTTP/Methods)

### REST 구성 요소
 - 자원 (Resource) : HTTP URI
 - 자원에 대한 행위 (Verb) : HTTP Method
 - 자원에 대한 행위의 내용 (Representations) : HTTP Message Payload

### REST의 특징
 - Server-Client (서버-클라이언트 구조)
   - 자원이 있는 쪽이 Server, 자원을 요청하는 쪽이 Client가 된다. 
 - Stateless (무상태)
   - HTTP 프로토콜은 Stateless Protocol이므로 REST 역시 무상태성을 갖는다.
   - Client의 context를 Server에 저장하지 않는다.
   - Server는 각각의 요청을 완전히 별개의 것으로 인식하고 처리한다.
 - Cacheable (캐시 처리 가능)
   - 웹 표준 HTTP 프로토콜을 그대로 사용하므로 웹에서 사용하는 기존의 인프라를 그대로 활용할 수 있다.
   - HTTP 프로토콜 표준에서 사용하는 캐싱 기능 활용 
 - Layered System (계층화)
   - Client - API Server : 심플하게 구성할 수 도 있고
   - Client - Proxy - Gateway, LB, Auth - Server : 다중 계층으로 구성하여 보안성과 처리 능력을 향상시킬 수 있다.
 - Uniform Interface (인터페이스 일관성)
   - URI로 지정한 Resource에 대한 조작을 통일되고 한정적인 인터페이스로 수행한다.
   - HTTP 표준 프로토콜에 따르는 모든 플랫폼에서 사용이 가능하므로 특정 언어나 기술에 종속되지 않는다.

## REST API란?
 - RESPT API는 REST의 원리를 따르는 API를 의미.
 - (엄밀히 말하면 아닌 경우도 있으나) 일반적으로 http를 활용한 웹 API를 흔히 그렇게 부른다.
  > API(application programming interface)의 본래 용어적 의미는 어플리케이션(프로그램)간 연결과 데이터 교환을 위한 인터페이스를 말하지만 <br>웹 API에서는 주로 서버-서버간 또는 서버-프론트간 통신을 위한 인터페이스를 API라고 하기도 한다. 

## REST API 설계 원칙
 REST API를 올바르게 설계하기 위해서는 지켜야 하는 몇가지 규칙이 있다.

1. URI는 동사보다는 명사를, 대문자보다는 소문자를 사용하여야 한다.
 - Bad Example `http://example.com/Run/`
 - Good Example  `http://example.com/running`

2. 마지막에 슬래시 (/)를 포함하지 않는다.
 - Bad Example `http://example.com/test/`  
 - Good Example  `http://example.com/test`
 
3. 언더바 대신 하이픈을 사용한다.
 - Bad Example `http://example.com/test_blog`
 - Good Example  `http://example.com/test-blog`  
 
4. 파일확장자는 URI에 포함하지 않는다.
 - Bad Example `http://example.com/photo.jpg`  
 - Good Example  `http://example.com/photo`
 
5. 행위를 포함하지 않는다.
 - Bad Example `http://example.com/delete-post/1`
 - Good Example  `http://example.com/post/1`

<br>

## RESTful이란?
RESTFul이란 REST의 원리를 따르는 시스템을 의미한다. 하지만 REST를 사용했다 하여 모두가 RESTful 한 것은 아니다.  REST API의 설계 규칙을 올바르게 지킨 시스템을 RESTful하다 말할 수 있지만 REST API의 설계 규칙을 올바르게 지키지 못한 시스템은 REST API를 사용하였지만 RESTful 하지 못한 시스템이라고 할 수 있다.

예 (Not RESTful):
 - 모든 CRUD를 GET또는 POST로만 처리
 - URI에 동작을 표현: `/posts/create`

## REST API 설계 예시
 게시판 예로 들어 REST API를 설계해보자
 | 기능 | URI | Method |
 |---|---|---|
 |게시글 목록| /posts | GET |
 |게시글 작성| /posts | POST |
 |게시글 조회| /posts/{postId} | GET |
 |게시글 수정| /posts/{postId} | PUT |
 |게시글 삭제| /posts/{postId} | DELETE |
 |게시글 작성 폼| /posts/form | GET |
 |게시글 수정 폼| /posts/{postId}/form | GET |

 - REST API 명세서의 예시는 다음을 참조
   - https://developers.worksmobile.com/kr/docs/200180301
   - https://developers.kakao.com/docs/latest/ko/message/rest-api#default-template-msg-me


## Spring에서의 REST API 구현
 - Model(VO) 설계
   - 주고 받는 데이터(자원)에 대한 내용을 Java객체에 맵핑할 수 있도록 설계한다.
   - 다음은 심플 댓글 모델의 예시
      ```java
      @Data
      public class CommentModel {
          private int no;
          private String author;
          private String comment;
          private LocalDateTime date;
      }
      ```
 - Controller 구현
   - REST API는 View를 거치지 않고 Java 모델 객체가 특수한 포맷(JSON, XML등)으로 응답된다.
   - Controller에 Model객체를 리턴하고 `@ResponseBody`어노테이션을 붙여보자.

      ```java
      @GetMapping("/comments/test")
      @ResponseBody
      public CommentModel getCommentModel() {
          CommentModel cm = new CommentModel();
          cm.setNo(1);
          cm.setAuthor("나테스트");
          cm.setComment("REST API Body로 응답 되는지 보자");
          cm.setDate(LocalDateTime.now());

          return cm;
      }
      ```
      ![REST API Result](./img/rest%20api%20get%20test.png)

  - `@Controller`어노테이션과 `@ResponseBody`어노테이션을 통합하여 `@RestController`어노테이션을 사용할 수 있다.
  - 일반적으로 REST API 처리용으로 RestController를 View처리용 Controller와 구분하여 만들거나 API서버를 별도(의 프로젝트)로 두기도 한다.

 - CRUD구현
   - 모델 객체에 해당하는 데이터를 DB에서 가져올 수 있도록 적절히 Repository클래스를 구현한다. <br>본 실습에서는 MyBatis기술을 활용할 것이다.
 - 서비스 로직 구현
   - Service클래스에는 비즈니스 로직을 처리한다.
   - CRUD구현에서 만들어진 Repository클래스들이 이곳에서 활용된다.
   - DB에서 가져온 데이터를 적절히 가공하거나, 여러 데이터를 결합하여 하나의 모델을 생성하거나, 데이터 처리에 대한 트랜젝션 처리 등의 절차를 이곳에 작성한다.
   - Controller에서는 Http요청을 받아 여기서 구현된(Service클래스의) 메서드들을 호출하게 된다. 

### Controller에서 POST, PUT요청 처리하기
 - 다음과 같이 POST 요청을 받는 메서드를 만들고 Model객체(VO)를 파라미터로 받는다. 
 - 파라미터에 `@RequestBody`어노테이션을 붙이면 POST요청 body의 내용이 객체에 맵핑된다.

    ```java
    @PostMapping("/comments/test")
    @ResponseBody
    public CommentModel postCommentModel(@RequestBody CommentModel commentModel) {

        // XXXService.createXXX(commentModel); 와 같이 서비스 로직으로 넘겨 처리한다.

        return commentModel; // Request 받은 객체를 바로 리턴해본 것, 실제는 처리 결과 등을 표현하는 객체를 리턴
    }
    ```

    > ### LocalDateTime의 맵핑 포맷 지정
    > Java에서 시간을 표현할 때 `Date`나 `LocalDateTime`가 사용되는데, 다음과 같이 JSON필드의 포맷을 지정할 수 있다.<br>
    > [(Java의 날짜 시간 API 관련 D2 참고)](https://d2.naver.com/helloworld/645609)
    >  ```java
    >  @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
    >  private Date date;
    >  ```
    >  ```java
    >  @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    >  private LocalDateTime date;
    >  ```
    ![http post request](./img/spring%20http%20post.png)


## REST API 호출 방법
 - 툴을 사용하거나 직접 개발하거나...
### 브라우저에서 호출 (GET, POST만 가능)
 - 브라우저의 주소(URL)창에 API주소를 입력하고 바로 호출해보는 방법 (GET으로 동작)
 - `<form action="API주소" method="post"> <button type="submit">제출</button> </form>` 태그를 간단히 작성하여 POST 호출

### JavaScript 이용한 호출
 - JavaScript의 Fetch API를 활용하여 REST API호출을 구현할 수 있다.
 - JavaScript Fetch API에 관한 내용은 [이 가이드를 참고](https://developer.mozilla.org/ko/docs/Web/API/Fetch_API/Using_Fetch)

    ```JS
    fetch("http://localhost:8080/comments/test", {
        method: 'POST',
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            "no": 1,
            "author": "spring",
            "comment": "HTTP POST Test",
            "date": "2023-08-22 12:34"
        }),
        redirect: 'follow'
    })
        .then(response => response.json())
        .then(result => console.log(result))
        .catch(error => console.log('error', error));
    ```

### 툴을 이용한 방법 (Postman등)
 - [Postman](https://www.postman.com/downloads/)은 REST API를 호출 및 테스트해볼 수 있는 툴이다.
 - VS Code 확장 플러그인인 [Thunder Client](https://marketplace.visualstudio.com/items?itemName=rangav.vscode-thunder-client)를 활용하기도 한다.
 - IntelliJ의 [HTTP Client 플러그인](https://www.jetbrains.com/help/idea/http-client-in-product-code-editor.html) - Ultimate버전에서 사용 가능
### 운영체제 커맨드 (curl)
 - 윈도우(cmd)와 리눅스(bash) 모두 curl이라는 http호출 도구를 지원한다. 이를 활용하여 REST API를 호출해볼 수 있다.
    ```bash
    curl --location --request POST 'http://localhost:8080/comments/test' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "no": 1,
        "author": "spring",
        "comment": "HTTP POST Test",
        "date": "2023-08-22 12:34"
    }'
    ```
### Python requests
 - Python의 requests모듈을 활용하여 코딩

    ```python
    import requests

    headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
    requests.post(url, data=json.dumps(data), headers=headers)

    # requests.post(url, json={"key": "value"})
    ```
