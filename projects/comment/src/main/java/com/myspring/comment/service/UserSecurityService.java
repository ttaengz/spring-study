package com.myspring.comment.service;

import com.myspring.comment.config.UserRole;
import com.myspring.comment.dao.UserDAO;
import com.myspring.comment.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserSecurityService implements UserDetailsService {
    @Autowired
    UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        System.out.println("로그인요청 " + userId);

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

        return new User(user.getUserId(), user.getPassword(), authorities);
    }
}
