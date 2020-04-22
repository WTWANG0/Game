package org.tinygame.herostory.login;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.MySqlSessionFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.login.db.IUserDao;
import org.tinygame.herostory.login.db.UserEntity;

import java.util.function.Function;

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

    /**
     * 用户登陆
     *
     * @param userName 用户名称
     * @param password 密码
     * @return 用户实体
     * Function<UserEntity, Void> :回调函数
     */
    public void userLogin(String userName, String password, Function<UserEntity, Void> callback) {
        if (userName == null || password == null) return;

        IAsyncOperation asyOp = new AsyncGetUserByName(userName, password) {
            @Override
            public void doFinish() {
                if (callback != null) {
                    callback.apply(this.getUserEntity());
                }
            }
        };

        //执行异步操作
        AsyncOperationProcessor.getInstance().process(asyOp);
    }

    //异步方式获取用户
    private class AsyncGetUserByName implements IAsyncOperation {
        private final String _userName;
        private final String _password;
        private UserEntity _userEntity = null;

        AsyncGetUserByName(String userName, String password) {
            _userName = userName;
            _password = password;
        }

        //
        public UserEntity getUserEntity() {
            return _userEntity;
        }

        //异步执行
        @Override
        public void doAsync() {
            try (SqlSession mySqlSession = MySqlSessionFactory.openSession()) {
                // 获取 DAO 对象,
                //通过javaassist和反射进行
                IUserDao dao = mySqlSession.getMapper(IUserDao.class);

                // 看看当前线程
                LOGGER.info("当前线程 = {}", Thread.currentThread().getName());

                // 更间用户名称获取用户实体
                UserEntity userEntity = dao.getUserByName(_userName);

                if (null != userEntity) {
                    // 判断用户密码
                    if (!_password.equals(userEntity.password)) {
                        // 用户密码错误,
                        LOGGER.error("用户密码错误, userId = {}, userName = {}",
                                userEntity.userId, _userName
                        );

                        //throw new RuntimeException("用户密码错误");
                    }
                } else {
                    // 如果用户实体为空, 则新建用户!
                    userEntity = new UserEntity();
                    userEntity.userName = _userName;
                    userEntity.password = _password;
                    userEntity.heroAvatar = "Hero_Shaman"; // 默认使用萨满

                    // 将用户实体添加到数据库
                    dao.insertInto(userEntity);
                }

                _userEntity = userEntity;
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }

        //保证每次访问同一个对象的线程是一样的
        @Override
        public int getBindId() {
            return _userName.charAt(_userName.length() - 1);
        }
    }

}
