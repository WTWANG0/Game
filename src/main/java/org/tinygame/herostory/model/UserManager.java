package org.tinygame.herostory.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//用户管理类
public final class UserManager {

    //私有化类默认构造器
    private UserManager(){}
    //用户字典，用来存储用户信息：ConcurrentHashMap线程安全
    private static final Map<Integer, User> _userMap = new ConcurrentHashMap<>();

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

    //根据id获取用户信息
    public static User getUserById(int userId) {
        return _userMap.get(userId);
    }
}
