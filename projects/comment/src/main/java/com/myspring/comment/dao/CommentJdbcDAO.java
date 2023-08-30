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
