package com.datuzi.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.datuzi.model.FundInfo;
import com.datuzi.util.FileUtils;
import com.datuzi.util.HttpUtils;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;
import org.apache.commons.compress.utils.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 张建波
 * @since 2020年04月23日
 */
public class ShowFundService {

    private static final String url = "http://fundgz.1234567.com.cn/js/{@}.js";

    private static ShowFundService instance;

    public static ShowFundService getInstance(){
        if(instance == null){
            instance = new ShowFundService();
        }
        return instance;
    }

    public void showFund(Project project) {
        try {
            List<String> list = FileUtils.getInstance().setConfigPath("d://config.ini").loadFile().getList();
            if (list == null || list.size() == 0) {
                Notification notification = new Notification("error","信息","请先配置d:/config.ini文件", NotificationType.ERROR);
                Notifications.Bus.notify(notification);
                return;
            }
            List<String> msgList = Lists.newArrayList();
            String str = "";
            for (int i = 0; i < list.size(); i++) {
                String finalUrl = url.replace("{@}", list.get(i));
                String json = null;
                for(int j=0;j<5;j++) {
                    Map<Boolean,String> result = getJson(finalUrl);
                    if(result.containsKey(true)){
                        json = result.get(true);
                        break;
                    }
                }

                if(json == null){
                    throw new Exception("查询失败");
                }

                FundInfo fundInfo = JSON.parseObject(json, new TypeReference<FundInfo>() {
                });
                double gszzl = Double.valueOf(fundInfo.getGszzl());
                str += "[" + fundInfo.getName() + ": " + fundInfo.getGsz() + ", " + (gszzl > 0 ? "涨" : "跌") + gszzl + "%]";
                msgList.add(str);
            }
            Notification notification = new Notification("fund","信息",str, NotificationType.INFORMATION);
            Notifications.Bus.notify(notification);
            Thread.sleep(10000L);
            notification.expire();
        } catch (Exception ex) {
            Notification notification = new Notification("fund","信息","查询出现错误："+ ex.getMessage(), NotificationType.ERROR);
            Notifications.Bus.notify(notification);
        }
    }

    /**
     * 获取json数据
     * @param url
     * @return
     */
    private Map<Boolean,String> getJson(String url){
        Map<Boolean,String> result = new HashMap<>();
        String json;
        try {
            json = HttpUtils.sendGet(url);
            json = json.replace("jsonpgz(", "");
            json = json.substring(0, json.lastIndexOf(")"));
            result.put(true,json);
        }catch (Exception e){
            result.put(false,null);
        }
        return result;
    }
}
