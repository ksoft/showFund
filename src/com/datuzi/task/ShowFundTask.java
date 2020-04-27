package com.datuzi.task;

import com.datuzi.service.ShowFundService;
import com.intellij.openapi.project.Project;

/**
 * @author 张建波
 * @since 2020年04月23日
 */
public class ShowFundTask implements Runnable {

    private Project project;


    public ShowFundTask(Project project){
        this.project = project;
    }

    @Override
    public void run() {
        ShowFundService.getInstance().showFund(project);
    }
}
