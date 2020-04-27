package com.datuzi.action;

import com.datuzi.constant.SysConstant;
import com.datuzi.task.ShowFundTask;
import com.datuzi.util.FileUtils;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author 张建波
 * @since 2020年04月22日
 */
public class ShowFund extends AnAction {
    public static ScheduledExecutorService executor;

    @Override
    public void actionPerformed(AnActionEvent e) {
        try {
            FileUtils fileUtils = FileUtils.getInstance();
            Map<String,String> propertiesMap = fileUtils.setConfigPath("d://sysConfig.ini").loadFile().getMap();
            String runningStatus = propertiesMap.get(SysConstant.RUNNING_STATUS);
            if (propertiesMap == null || propertiesMap.size() == 0 || SysConstant.NO.equals(runningStatus)) {
                fileUtils.setValue(SysConstant.RUNNING_STATUS,SysConstant.YES);
                initExecutor();
                Integer taskPeriod = propertiesMap.get(SysConstant.TASK_PERIOD) == null ? 60:Integer.valueOf(propertiesMap.get(SysConstant.TASK_PERIOD));
                if(propertiesMap.get(SysConstant.TASK_PERIOD) == null){
                    fileUtils.setValue(SysConstant.TASK_PERIOD,taskPeriod);
                }
                executor.scheduleAtFixedRate(new Thread(new ShowFundTask(getEventProject(e))), 0, taskPeriod, TimeUnit.SECONDS);
                Notification notification = new Notification("fund", "信息", "任务已启动", NotificationType.WARNING);
                Notifications.Bus.notify(notification);
            } else {
                fileUtils.setValue(SysConstant.RUNNING_STATUS,SysConstant.NO);
                if (executor != null && !executor.isShutdown() && !executor.isTerminated()) {
                    executor.shutdown();
                    Notification notification = new Notification("fund", "信息", "任务已终止", NotificationType.WARNING);
                    Notifications.Bus.notify(notification);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void initExecutor() {
        ShowFund.executor = Executors.newScheduledThreadPool(10);
    }
}
