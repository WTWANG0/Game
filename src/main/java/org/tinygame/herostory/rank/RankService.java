package org.tinygame.herostory.rank;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinygame.herostory.async.AsyncOperationProcessor;
import org.tinygame.herostory.async.IAsyncOperation;
import org.tinygame.herostory.util.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

//排行榜
public class RankService {

    private RankService(){}

    static private final Logger LOGGER = LoggerFactory.getLogger(RankService.class);

    //对象单例
    private static final RankService _instance = new RankService();

    static public RankService getInstance() {
        return _instance;
    }

    //获取排行榜消息
    //Function callback：回调函数，java8 lamada
    public void getRank(Function<List<RankItem>, Void> callback) {
        if (callback == null )return;

        IAsyncOperation asyncOp = new AsyncGetRank() {
            @Override
            public void doFinish() {
                callback.apply(this.getRankItemList());
            }
        };

        AsyncOperationProcessor.getInstance().process(asyncOp);
    }

    /**
     * 异步方式获取排行榜
     */
    private class AsyncGetRank implements IAsyncOperation {
        //排名条目列表
        private List<RankItem> _rankItemList = null;

        /**
         * 获取排名条目列表
         * @return 排名条目列表
         */
        public List<RankItem> getRankItemList() {
            return _rankItemList;
        }

        @Override
        public void doAsync() {
            try (Jedis redis = RedisUtil.getJedis()) {

                _rankItemList = new ArrayList<>();
                int rankId = 0;
                // 获取字符串集合
                Set<Tuple> valSet = redis.zrevrangeWithScores("Rank", 0, 9);

                for (Tuple t : valSet) {
                    // 用户 Id
                    int userId = Integer.parseInt(t.getElement());

                    // 获取用户基本信息
                    String jsonStr = redis.hget("User_" + userId, "BasicInfo");
                    if (jsonStr == null) continue;

                    // 创建排名条目
                    RankItem newItem = new RankItem();
                    newItem.rankId = ++rankId;
                    newItem.userId = userId;
                    newItem.win = (int)t.getScore();

                    JSONObject jsonObj = JSONObject.parseObject(jsonStr);

                    newItem.userName = jsonObj.getString("userName");
                    newItem.heroAvatar = jsonObj.getString("heroAvatar");

                    _rankItemList.add(newItem);
                }
            } catch (Exception ex) {
                LOGGER.error(ex.getMessage(), ex);
            }
        }
    }


}
