package com.dy.websocket.websocketcluster.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class JsonUtil {

    public static String parseObjToJson(Object object) {
        String string = null;
        try {
            string = JSONObject.toJSONString(object);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return string;
    }

    public static <T> T parseJsonToObj(String json, Class<T> c) {
        try {
            JSONObject jsonObject = JSON.parseObject(json);
            return JSON.to(c, jsonObject);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }
}