/*
 * ProjectData.java    1.0 Jul 14, 2014
 *
 * Copyright (c) 2014-2030 Monmouth Technologies, Inc.
 * http://www.mt.com.tw
 * 10F-1 No. 306 Chung-Cheng 1st Road, Linya District, 802, Kaoshiung, Taiwan
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Monmouth
 * Technologies, Inc. You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement you
 * entered into with Monmouth Technologies.
 */
package tw.com.mt.cust.report.data;

import java.util.ResourceBundle;

import tw.com.mt.tm.api.bean.Project;

/** Information about project name, project owner, and project role of user. */
public class ProjectData {

    /** Specified project. */
    private Project project;

    /** ResourceBundle. */
    private ResourceBundle rb;

    /** projRole 內容應該是 "owner"、"managers"、"worker"、"guest" 其中一個。 */
    private String projRole;

    public ProjectData(Project project, String projRole,
            ResourceBundle resourceBundle) {
        this.project = project;
        this.projRole = projRole;
        this.rb = resourceBundle;
    }

    /**
     * Get name in specified project.
     *
     * @return project name
     */
    public String getProjectName() {
        return project.getName();
    }

    /**
     * Get owner's name in specified project.
     *
     * @return owner's name
     */
    public String getProjectOwnerName() {
        return project.getOwnerName();
    }

    /**
     * Get proj role name.
     * 回傳的名稱會是專案主持人、管理人員、工作人員、瀏覽者其中一個。
     *
     * @return proj role name
     */
    public String getProjRoleName() {
        if (projRole.indexOf("owner") >= 0) {
            return rb.getString("sample.report.proj_owner");
        } else if (projRole.indexOf("manager") >= 0) {
            return rb.getString("sample.report.proj_mgmt_team");
        } else if (projRole.indexOf("worker") >= 0) {
            return rb.getString("sample.report.proj_work_team");
        } else {
            return rb.getString("sample.report.proj_guest_team");
        }
    }
    
}
