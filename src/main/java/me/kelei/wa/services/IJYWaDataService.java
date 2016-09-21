package me.kelei.wa.services;

import me.kelei.wa.entities.WaUser;

/**
 * 从精友考勤系统获取数据
 * Created by kelei on 2016/9/20.
 */
public interface IJYWaDataService {

    /**
     * 登录精友考勤网站
     * @param userName
     * @param password
     * @return
     */
    WaUser login(String userName, String password);
}
