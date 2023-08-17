# Model생성과 DataBase연결

Spring 어플리케이션에서 데이터베이스에 접근하여 자료를 조작하기 위해 다음과 같이 JDBC라이브러리를 추가한다.
<br>
먼저 JDBC(Java Database Connectivity)만으로 접근해보자.

## JDBC 활용하여 데이터베이스에 접근

### 1. pom.xml
 - 다음 디펜던시를 pom.xml파일의 `<dependencies>...</dependencies>` 사이에 추가한다.
 - 추가 후 프로젝트 폴더 → 메뉴 → Maven → Reload Project 실행

    ```xml
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-jdbc</artifactId>
    </dependency>
    <dependency>
        <groupId>org.mariadb.jdbc</groupId>
        <artifactId>mariadb-java-client</artifactId>
        <scope>runtime</scope>
    </dependency>
    ```
### 2. JDBC 활용 Repository클래스 만들어보기
 - 다음 패키지 경로를 생성한다.
   - src → java → com..(프로젝트 패키지명).. → dao (하위 폴더 누른 후 메뉴 → New → Pagkage)

 - 생성된 dao 패키지 하위에 CommentJdbcDAO.java 파일 생성
 - 다음과 같이 작성

    ```java
    package com.myspring.comment.dao;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.jdbc.core.JdbcTemplate;
    import org.springframework.stereotype.Repository;

    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;

    @Repository
    public class CommentJdbcDAO {
        @Autowired
        JdbcTemplate jdbcTemplate;

        public List<Map<String, ?>> selectAllCommentList() {

            return jdbcTemplate.query("SELECT * FROM tb_comment", (rs, rowNum) -> {
                Map<String, Object> mss = new HashMap<>();
                mss.put("no", rs.getInt(1));
                mss.put("author", rs.getString(2));
                mss.put("comment", rs.getString(3));
                mss.put("date", rs.getString(4));
                return mss;
            });
        }
    }
    ```

### 3. Controller만들고 호출해보기
 - CommentRestController.java 파일에 다음과 같이 테스트해볼 수 있는 컨트롤러를 추가해본다.

    ```java
    package com.myspring.comment.controller;

    import com.myspring.comment.dao.CommentJdbcDAO;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;
    import java.util.Map;

    @RestController
    public class CommentRestController {   
        @Autowired
        private CommentJdbcDAO commentJdbcDAO;

        @GetMapping(value = "/test")
        public String testRequest() {
            List<Map<String, ?>> r =  commentJdbcDAO.selectAllCommentList();
            Map<String, ?> comment = r.get(0);

            return String.format("번호: %s, 작성자: %s, 댓글: %s, 날짜: %s",
                    r.get(0).get("no"),
                    r.get(0).get("author"),
                    r.get(0).get("comment"),
                    r.get(0).get("date")
            );
        }
    }
    ```
 - 브라우저를 열고 http://localhost:8081/test 주소로 접속해보자.

## MyBatis 활용하여 데이터베이스에 접근

 JDBC만으로 데이터베이스 접근 시 테이블의 컬럼과 Java객체 간 일일이 맵핑 해주어야 하는 번거로움이 있다.
 <br><br>
 이를 개선하고자 Object(자바 객체)와 DB테이블을 매핑해주는 기술이 등장했는데 SQL Mapper와 ORM기술이 있다.
 <br>
 간략한 특징은 다음과 같다.

#### SQL Mapper
 - SQL문을 직접 작성하고 쿼리 수행 결과를 어떠한 객체에 매핑할지 바인딩 하는 방법
 - 대표적인 구현 기술로 MyBatis가 있다.

#### ORM
 - ORM: 객체(Object)와 DB의 테이블을 Mapping 시켜 RDB 테이블을 객체지향적으로 사용하게 해주는 기술
 - ORM은 SQL문이 아닌 RDB의 데이터 그 자체와 매핑하기 때문에 SQL을 직접 작성하지 않는다.
 - 기본적인 CRUD메소드가 제공되지만 복잡한 쿼리에 대한 대응이 어렵다.
 - Hibernate와 같은 구현 기술이 이에 해당한다.

### MyBatis 설정
 - pom.xml에 다음 디펜던시 추가

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.2</version>
</dependency>
```

- application.yml에 다음 설정 확인 (없으면 추가)

```yml
mybatis:
  mapper-locations:
    - classpath:mappers/*.xml
  configuration:
    map-underscore-to-camel-case: true # (DB)value_name -> (Java)valueName 자동 매핑
```

 - 다음 경로에 mapper폴더를 생성하고 CommentMapper.xml 파일을 생성한다.

    ![mybatis mapper path](./img/mybatis%20mapper%20path.png)

 - Mapper xml파일의 기본 내용(틀)은 다음과 같이 작성한다.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="맵핑할 객체명">
    
</mapper>
```
  
## 데이터를 맵핑할 객체 클래스 작성 (Model, VO)
 - 어플리케이션 패키지에 model 패키지(폴더)를 추가하고 다음과 같이 `CommentModel.java`파일을 생성 및 작성한다.
  
    ![Model Class Path](./img/model%20class%20path.png)

    ```java
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
    ```

## Mapper XML에 SQL 쿼리 작성
 - CommentMapper.xml파일에 다음과 같이 SQL 쿼리문을 작성한다.
    
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

    <mapper namespace="com.myspring.comment.dao.CommentDAO">
        <select id="selectAllCommentList" resultType="com.myspring.comment.model.CommentModel">
            SELECT * FROM tb_comment;
        </select>

        <select id="selectComment" resultType="com.myspring.comment.model.CommentModel">
            SELECT * FROM tb_comment WHERE no = #{no};
        </select>

        <insert id="insertComment" parameterType="com.myspring.comment.model.CommentModel" useGeneratedKeys="true" keyProperty="no">
            INSERT INTO tb_comment (author, comment, date)
            VALUES(#{author}, #{comment}, now());
        </insert>

        <update id="updateComment" parameterType="com.myspring.comment.model.CommentModel">
            UPDATE tb_comment
            SET comment = #{comment}
            WHERE no = #{no}
        </update>

        <delete id="deleteComment" parameterType="int">
            DELETE FROM tb_comment WHERE no = #{no}
        </delete>
    </mapper>
    ```

## DAO 인터페이스 작성
 - 프로젝트의 dao패키지 폴더에 `CommentDAO.java`파일을 생성한다.
 - Java Class가 아닌 Interface로 작성한다.

    ```java
    package com.myspring.comment.dao;

    import com.myspring.comment.model.CommentModel;
    import org.apache.ibatis.annotations.Mapper;
    import org.apache.ibatis.annotations.Param;

    import java.util.List;

    @Mapper
    public interface CommentDAO {
        List<CommentModel> selectAllCommentList();

        CommentModel selectComment(@Param("no") int no);

        int insertComment(CommentModel postModel);

        int updateComment(@Param("no") int no, @Param("comment") String comment);

        int deleteComment(@Param("no") int no);
    }
    ```

## DAO 사용해보기
 - JDBC에서 테스트해보기 위해 만들었던 Controller를 다음과 같이 수정 후 어플리케이션을 재시작해보자

    ```java
    package com.myspring.comment.controller;

    import com.myspring.comment.dao.CommentDAO;
    import com.myspring.comment.model.CommentModel;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.ui.Model;
    import org.springframework.web.bind.annotation.*;
    import org.thymeleaf.util.StringUtils;

    import java.util.List;

    @RestController
    public class CommentRestController {
        @Autowired
        private CommentDAO commentDAO;

        @GetMapping(value = "/test")
        public String testRequest() {

            List<CommentModel> commentList = commentDAO.selectAllCommentList();
        CommentModel cmt = commentList.get(0); // 테스트로 하나만 꺼내서 찍어보자

            return String.format("번호: %s, 작성자: %s, 댓글: %s, 날짜: %s",
                    cmt.getNo(),
                    cmt.getAuthor(),
                    cmt.getComment(),
                    StringUtils.toString(cmt.getDate())
            );
        }
    }

    ```

## Quiz
 - `StringUtils.toString(cmt.getDate())`는 `cmt.toString()`으로 사용할 수 있음에도 굳이 StringUtils 클래스를 사용했을까?
 - CommentDAO는 왜 `CommentDAO commentDAO = new commentDAO()`와 같이 생성해서 쓰지 않았을까?