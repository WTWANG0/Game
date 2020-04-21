package org.tinygame.herostory.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

//用户管理类
public final class UserManager {

    //私有化类默认构造器
    private UserManager(){}
    //用户字典，用来存储用户信息
    private static final Map<Integer, User> _userMap = new HashMap<>();

    //添加用户
    public static void addUser(User newUser) {
        if (newUser != null) {
            _userMap.put(newUser.userId, newUser);
        }
    }

    //根据id删除用户
    public static void removeUserById(int userId) {
        _userMap.remove(userId);
    }

    //用户列表
    public static Collection<User> listUser() {
        return _userMap.values();
    }
}
