package com.study.douyin.socialize.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.study.douyin.socialize.dao.FollowDao;
import com.study.douyin.socialize.entity.FollowEntity;
import com.study.douyin.socialize.feign.BasicFeignService;
import com.study.douyin.socialize.service.FollowService;
import com.study.douyin.socialize.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 粉丝
 */
@Service
public class FollowServiceImpl extends ServiceImpl<FollowDao, FollowEntity> implements FollowService {

    @Autowired
    private BasicFeignService basicFeignService;

    /**
     * 关注
     * @param token
     * @param toUserId 对方id
     * @param actionType 1-关注 2-取消关注
     * @return
     */
    @Override
    public boolean action(String token, Integer toUserId, Integer actionType) {
        // 获取当前用户id
        int userId = basicFeignService.getUserIdByToken(token);
        // 当前token对应的user不存在
        if (userId == -1)
            return false;
        // 数据库变化行数
        int count = 0;
        if (actionType == 1) {
            FollowEntity followEntity = new FollowEntity();
            followEntity.setUserId(toUserId);
            followEntity.setFollowId(userId);
            // 查询当前数据，不存在才添加
            int num = baseMapper.selectCount(new QueryWrapper<FollowEntity>()
                    .eq("user_id", toUserId)
                    .eq("follow_id", userId));
            if (num == 0)
                count = baseMapper.insert(followEntity);
        } else if (actionType == 2) {
            count = baseMapper.delete(new QueryWrapper<FollowEntity>()
                    .eq("user_id", toUserId)
                    .eq("follow_id", userId));
        }
        // actionType若不等于1或2，则count为初始值0，return false
        return count == 1;
    }

    /**
     * 获取关注列表
     * @param userId
     * @param token
     * @return
     */
    @Override
    public User[] getFollowList(Integer userId, String token) {
        // 判断userId和token是否匹配
        Integer id = basicFeignService.getUserIdByToken(token);
        if (userId != id)
            return null;

        // 获取所有关注用户的userId
        List<FollowEntity> followEntities = baseMapper.selectList(new QueryWrapper<FollowEntity>().eq("follow_id", userId));
        List<Integer> userIds = followEntities.stream().map(followEntity -> {
            return followEntity.getUserId();
        }).collect(Collectors.toList());

        // 通过userId查询所有用户信息
        User[] userList = new User[userIds.size()];
        for (int i = 0; i < userIds.size(); i++) {
            User user = basicFeignService.getUserById(userIds.get(i), userId);
            userList[i] = user;
        }

        return userList;
    }

    /**
     * 获取粉丝列表
     * @param userId
     * @param token
     * @return
     */
    @Override
    public User[] getFollowerList(Integer userId, String token) {
        // 判断userId和token是否匹配
        Integer id = basicFeignService.getUserIdByToken(token);
        if (userId != id)
            return null;

        // 获取所有粉丝的userId
        List<FollowEntity> followEntities = baseMapper.selectList(new QueryWrapper<FollowEntity>().eq("user_id", userId));
        List<Integer> userIds = followEntities.stream().map(followEntity -> {
            return followEntity.getFollowId();
        }).collect(Collectors.toList());

        // 通过userId查询所有用户信息
        User[] userList = new User[userIds.size()];
        for (int i = 0; i < userIds.size(); i++) {
            User user = basicFeignService.getUserById(userIds.get(i), userId);
            userList[i] = user;
        }

        return userList;
    }

    /**
     * 获取好友列表
     * @param userId
     * @param token
     * @return
     */
    @Override
    public User[] getFriendList(Integer userId, String token) {
        // 判断userId和token是否匹配
        Integer id = basicFeignService.getUserIdByToken(token);
        if (userId != id)
            return null;

        // 查寻所有与当前用户互关的用户id
        List<FollowEntity> followEntities = baseMapper.selectList(new QueryWrapper<FollowEntity>().eq("user_id", userId));
        List<Integer> userIds = followEntities.stream().filter(followEntity -> {
            int count = baseMapper.selectCount(new QueryWrapper<FollowEntity>()
                    .eq("user_id", followEntity.getFollowId())
                    .eq("follow_id", userId));
            return count == 1;
        }).map(followEntity -> {
            return followEntity.getFollowId();
        }).collect(Collectors.toList());

        // 通过userId查询所有用户信息
        User[] userList = new User[userIds.size()];
        for (int i = 0; i < userIds.size(); i++) {
            User user = basicFeignService.getUserById(userIds.get(i), userId);
            userList[i] = user;
        }

        return userList;
    }
}
