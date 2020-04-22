package org.tinygame.herostory.login;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.login.db.IUserDao;
import org.tinygame.herostory.login.db.UserEntity;

//登陆服务
public class LoginService {

    private LoginService(){}

    //log
    static private final Logger LOGGER = LoggerFactory.getLogger(LoginService.class);

    //单例
    private static final LoginService _instance = new LoginService();

    //获取单例对象
    public static LoginService getInstance() {
        return _instance;
    }

    //
    /**
     * 用户登陆
     *
     * @param userName 用户名称
     * @param password 密码
     * @return 用户实体
     */
    public UserEntity userLogin(String userName, String password) {
        if (userName == null || password == null) return null;

        try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {
            // 获取 DAO 对象,
            //通过javaassist和反射进行
            IUserDao dao = mySqlSession.getMapper(IUserDao.class);

            // 看看当前线程
            LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

            // 更间用户名称获取用户实体
            UserEntity userEntity = dao.getUserByName(userName);

            if (null != userEntity) {
                // 判断用户密码
                if (!password.equals(userEntity.password)) {
                    // 用户密码错误,
                    LOGGER.error("用户密码错误, userId = {}, userName = {}",
                            userEntity.userId, userName
                    );

                    throw new RuntimeException("用户密码错误");
                }
            } else {
                // 如果用户实体为空, 则新建用户!
                userEntity = new UserEntity();
                userEntity.userName = userName;
                userEntity.password = password;
                userEntity.heroAvatar = "Hero_Shaman"; // 默认使用萨满

                // 将用户实体添加到数据库
                dao.insertInto(userEntity);
            }

            return userEntity;
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }

}
