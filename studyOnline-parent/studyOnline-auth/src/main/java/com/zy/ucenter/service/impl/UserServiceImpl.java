package com.zy.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.zy.ucenter.mapper.XcMenuMapper;
import com.zy.ucenter.mapper.XcUserMapper;
import com.zy.ucenter.model.dto.AuthParamsDto;
import com.zy.ucenter.model.dto.XcUserExt;
import com.zy.ucenter.model.po.XcMenu;
import com.zy.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UserServiceImpl implements UserDetailsService {
    @Autowired
    XcUserMapper xcUserMapper;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    XcMenuMapper xcMenuMapper;



    //传入的请求认证的参数就是AuthParamsDto
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //将传入的json转成AuthParamsDto对象
        AuthParamsDto authParamsDto = null;
        try {
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            throw new RuntimeException("请求认证参数不符合要求");
        }

        //认证类型，有password，wx。。。
        String authType = authParamsDto.getAuthType();


        //根据认证类型从spring容器取出指定的bean
        String beanName = authType+"_authservice";
        AuthService authService = applicationContext.getBean(beanName, AuthService.class);
        //调用统一execute方法完成认证
        XcUserExt xcUserExt = authService.execute(authParamsDto);
        //封装xcUserExt用户信息为UserDetails

        UserDetails userPrincipal = getUserPrincipal(xcUserExt);

        return userPrincipal;
    }

    /**
     * @description 查询用户信息
     * @param xcUser  用户id，主键
     * @return com.xuecheng.ucenter.model.po.XcUser 用户信息
     * @author Mr.M
     * @date 2022/9/29 12:19
     */
    public UserDetails getUserPrincipal(XcUserExt xcUser){
        String password = xcUser.getPassword();
        //权限
        List<XcMenu> xcMenus = xcMenuMapper.selectPermissionByUserId(xcUser.getId());
        List<String> permissions = new ArrayList<>();
        if(xcMenus.size()<=0){
            permissions.add("p1");
        }else{
            xcMenus.forEach(menu->{
                permissions.add(menu.getCode());
            });
        }
        xcUser.setPermissions(permissions);
        xcUser.setPassword(null);
        String[] authorities = permissions.toArray(new String[0]);
        //将用户信息转json
        String userJson = JSON.toJSONString(xcUser);
        UserDetails userDetails = User.withUsername(userJson).password(password).authorities(authorities).build();
        return  userDetails;
    }

}
