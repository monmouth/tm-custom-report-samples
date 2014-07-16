/*
 * SampleReport.java    1.0 Jul 11, 2014
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
package tw.com.mt.cust.report;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.webmacro.servlet.WebContext;

import tw.com.mt.cust.report.data.ProjectData;
import tw.com.mt.cust.util.Util;
import tw.com.mt.tm.api.CustomReport;
import tw.com.mt.tm.api.bean.Project;
import tw.com.mt.tm.api.bean.User;
import tw.com.mt.tm.api.service.ProjectApiService;

public class SampleReport extends CustomReport {

    /** Directory of WebMacro files after plugin deployed. */
    private static final String TEMPLATE_DIR = File.separator + "plugins";

    /** File path of web macro pages. */
    private static final String SETUP_PAGE = TEMPLATE_DIR + File.separator
            + "show_sample_report";

    /** Company key. */
    private final int COMPANY_KEY = 100;

    /** ProjectApiService provide all Project related operation. */
    private ProjectApiService projectApiService;

    private ResourceBundle rb;

    /** ProjectApiService、ResourceBundle 可以經由繼承CustomReport取得。 */
    private void init() {
        projectApiService = getProjectApiService();
        rb = getResourceBundle();
    }

    /** 
     * 顯示範例報表。報表內容:使用者參與專案的一覽表，顯示資訊包含使用者ID、使用者名稱、專案名稱
     * 、專案主持人、團隊角色。
     */
    public void showSampleReport(WebContext cont) {
        init();

        Map<Integer, User> keyToUser = new HashMap<Integer, User>();
        Map<Integer, List<ProjectData>> userKeyToProjData =
                new HashMap<Integer, List<ProjectData>>();
        List<Project> allProjects = projectApiService.getProjects(COMPANY_KEY);

        for (Project project : allProjects) {
            List<User> canManage = projectApiService.getProjectManagers(
                    COMPANY_KEY, project.getProjectKey());
            List<User> canWork = projectApiService.getProjectWorkers(
                    COMPANY_KEY, project.getProjectKey());
            List<User> canBrowse = projectApiService.getProjectGuests(
                    COMPANY_KEY, project.getProjectKey());

            for (User user : canBrowse) {
                ProjectData data = computeProjRole(project, canManage, canWork,
                        canBrowse, user);

                if (userKeyToProjData.get(user.getKey()) == null) {
                    userKeyToProjData.put(user.getKey(),
                            new ArrayList<ProjectData>());
                }

                if (data != null) {
                    userKeyToProjData.get(user.getKey()).add(data);
                }

                if (keyToUser.get(user.getKey()) == null) {
                    keyToUser.put(user.getKey(), user);
                }
            }
        }

        cont.put("allUsers", sortUser(keyToUser));
        cont.put("userKeyToProjData", userKeyToProjData);
        Util.setDisplay(cont, null, "p_across_project_customer_report_man",
                SETUP_PAGE);
    }

    /**
     * Compute project role in specified project.
     *
     * @param project
     *            project
     * @param canManage
     *            list of users can manage the project
     * @param canWork
     *            list of users can work the project
     * @param canBrowse
     *            list of users can browse the project
     * @param user
     *            user
     * @return project data about project name, project owner, and project role
     *         of user
     */
    private ProjectData computeProjRole(Project project, List<User> canManage,
            List<User> canWork, List<User> canBrowse, User user) {
        if (project.getOwnerId().equals(user.getId())) {
            return new ProjectData(project, "owner", rb);
        } else if (canManage.contains(user)) {
            return new ProjectData(project, "manager", rb);
        } else if (canWork.contains(user)) {
            return new ProjectData(project, "worker", rb);
        } else if (canBrowse.contains(user)) {
            return new ProjectData(project, "guest", rb);
        }
        return null;
    }

    /**
     * Sort user by user id.
     *
     * @param keyToUser
     *            map that key is user key and value is user
     * @return list of sort user
     */
    private List<User> sortUser(Map<Integer, User> keyToUser) {
        List<User> allUsers = new ArrayList<User>();
        allUsers.addAll(keyToUser.values());
        Collections.sort(allUsers, new Comparator<User>() {
            public int compare(User u1, User u2) {
                return u1.getId().compareTo(u2.getId());
            }
        });
        return allUsers;
    }
}
