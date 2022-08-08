package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RegexPatterns;
import com.hmdp.utils.RegexUtils;
import com.hmdp.utils.constant.UserConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    public final String USER_NICK_NAME_PREFIX="user_";

    @Resource
    UserMapper userMapper;

    /**
     * 发送验证码
     *
     * @param phone
     * @param session
     * @return
     */
    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 1.校验验证码
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2.如果不符合，返回错误信息
            return Result.fail("手机号错误");
        }
        // 3.符合，生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 4.保存验证码到session
        session.setAttribute(UserConstant.CODE, code);
        // 5.发送验证码
        log.info("发送短信验证码成功");
        return Result.ok(code);
    }

    /**
     * 登录
     *
     * @param loginForm
     * @param session
     * @return
     */
    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        if (loginForm == null || session == null) {
            return Result.fail("空参数");
        }
        // 校验手机号格式
        if (RegexUtils.isPhoneInvalid(loginForm.getPhone())) {
            return Result.fail("手机号错误");
        }
        // 验证码是否存在
        String code = String.valueOf(session.getAttribute("code"));
        if (code == null) {
            throw new NullPointerException("session 中 code 为空");
        }
        // 验证码是否正确
        if (!code.equals(loginForm.getCode())) {
            return Result.fail("验证码错误");
        }
        // 用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", loginForm.getPhone());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            // 用户不存在 用户保存到session中
            user = createUserWithPhone(loginForm.getPhone());
        }
        // 用户存在
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        session.setAttribute(UserConstant.USER,userDTO);
        return Result.ok();
    }

    public User createUserWithPhone(String phone){
        User user = new User();
        user.setPhone(phone);
        user.setNickName(UserConstant.USER_NICK_NAME_PREFIX+RandomUtil.randomString(6));
        if (!save(user)) {
           return null;
        }
        return user;
    }
}
