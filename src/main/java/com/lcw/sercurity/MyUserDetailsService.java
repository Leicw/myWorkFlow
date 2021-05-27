package com.lcw.sercurity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lcw.domain.UserInfo;
import com.lcw.mappper.UserMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author ManGo
 */
@Component
public class MyUserDetailsService implements UserDetailsService {

    @Resource
    private PasswordEncoder passwordEncoder;
    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserInfo user = userMapper.selectOne(new QueryWrapper<UserInfo>().eq("username", username));
        if (Objects.isNull(user)){
            throw new UsernameNotFoundException("无此用户");
        }

        return user;

//      不校验用户只校验固定的密码
        /*String password = "333";
        return new User(username,
                passwordEncoder.encode(password),
                AuthorityUtils.commaSeparatedStringToAuthorityList("GROUP_activitiTeam"));*/
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
