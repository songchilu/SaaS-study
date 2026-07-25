package com.yaya.service;

/**
 * 在线用户-业务逻辑层
 */
public interface OnlineUserService {

    /**
     * 心跳接口
     */
    void heartbeat();
    /**
     * 查询本租户在线人数
     */
    Integer getOnlineUserCount();
}
