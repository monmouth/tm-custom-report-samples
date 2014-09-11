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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.webmacro.servlet.WebContext;

import tw.com.mt.cust.report.data.ProjectData;
import tw.com.mt.cust.util.Util;
import tw.com.mt.tm.api.CustomReport;
import tw.com.mt.tm.api.bean.CustomizedReport;
import tw.com.mt.tm.api.bean.Project;
import tw.com.mt.tm.api.bean.User;
import tw.com.mt.tm.api.service.CustomizedReportApiService;
import tw.com.mt.tm.api.service.ProjectApiService;
import tw.com.mt.tm.api.service.UserApiService;

public class SampleReport2 extends CustomReport {
    private static final Logger LOG = Logger.getLogger(SampleReport2.class);
    /** Directory of WebMacro files after plugin deployed. */
    private static final String TEMPLATE_DIR = File.separator + "plugins";

    /** File path of web macro pages. */
    private static final String SETUP_PAGE = TEMPLATE_DIR + File.separator
            + "setup_sample_report";
    
    /** File path of web macro pages. */
    private static final String SHOW_PAGE = TEMPLATE_DIR + File.separator
            + "show_sample_report";

    /** Company key. */
    private int companyKey;

    /** ProjectApiService provide all Project related operation. */
    private ProjectApiService projectApiService;

    /** CustomizedReportApiService provide CustomizedReport related operation. */
    private CustomizedReportApiService customizedReportApiService;

    /** UserApiService provide all user related operation. */
    private UserApiService userApiService;

    private ResourceBundle rb;

    /** ProjectApiService、ResourceBundle 可以經由繼承CustomReport取得。 */
    private void init(WebContext cont) {
        projectApiService = getProjectApiService();
        customizedReportApiService = getCustomizedReportApiService();
        userApiService = getUserApiService();
        rb = getResourceBundle();
        companyKey = getCompanyKey();
    }
    
    /** Display setup page of the report. */
    public void setupSampleReport(WebContext cont) {
        init(cont);
        
        int reportKey = Integer.parseInt(Util.getValue(cont.getRequest(),
                "report_key", ""));
        CustomizedReport report = customizedReportApiService
                .getCustomizedReport(companyKey, reportKey);
        
        List<String> selectUsers = new ArrayList<String>();
        // 取得編輯條件
        if (!report.getQuery().isEmpty()) {
            StringTokenizer st = new StringTokenizer(report.getQuery(), ";");
            while (st.hasMoreTokens()) {
                String condition = st.nextToken();
                
                StringTokenizer st1 = new StringTokenizer(condition, ":");
                String attr = null;
                String value = null;
                if (st1.hasMoreTokens()) {
                    attr = st1.nextToken();
                }
                    
                if (st1.hasMoreTokens()) {
                    value = st1.nextToken();
                }
                
                if (attr.equals("check_select")) {
                    selectUsers = Util.makeTokens(value, ",");
                }
            }
        }

        List<User> users = userApiService.getUsers(companyKey);
        cont.put("allUsers", sortUser(users));
        cont.put("selectUsers", selectUsers);
        cont.put("reportKey", reportKey);

        Util.setDisplay(cont, null, "p_across_project_customer_report_man",
                SETUP_PAGE);
    }
    
    /** Save condition of the report, and show the report. */
    public void doSaveCondition(WebContext cont) {
        init(cont);

        int reportKey = Integer.parseInt(Util.getValue(cont.getRequest(),
                "report_key", ""));

        String query = "check_select:" + Util.getValue(cont.getRequest(),
                "check_select", "") + ";";        
        customizedReportApiService.updateCustomizedReport(companyKey, reportKey,
                query);

        showSampleReport(cont);
    }

    /** 
     * 顯示範例報表。報表內容:使用者參與專案的一覽表，顯示資訊包含使用者ID、使用者名稱、專案名稱
     * 、專案主持人、團隊角色。
     */
    public void showSampleReport(WebContext cont) {
        init(cont);

        int reportKey = Integer.parseInt(Util.getValue(cont.getRequest(),
                "report_key", ""));

        CustomizedReport report = customizedReportApiService
                .getCustomizedReport(companyKey, reportKey);
        
        List<String> userKeys = new ArrayList<String>();
        // 取得編輯條件
        if (!report.getQuery().isEmpty()) {
            StringTokenizer st = new StringTokenizer(report.getQuery(), ";");
            while (st.hasMoreTokens()) {
                String condition = st.nextToken();
                
                StringTokenizer st1 = new StringTokenizer(condition, ":");
                String attr = null;
                String value = null;
                if (st1.hasMoreTokens()) {
                    attr = st1.nextToken();
                }
                    
                if (st1.hasMoreTokens()) {
                    value = st1.nextToken();
                }
                
                if (attr.equals("check_select")) {
                    userKeys = Util.makeTokens(value, ",");
                }
            }
        }

        Map<Integer, List<ProjectData>> userKeyToProjData =
                new HashMap<Integer, List<ProjectData>>();
        List<Project> allProjects = projectApiService.getProjects(companyKey);

        for (Project project : allProjects) {
            List<User> canManage = projectApiService.getProjectManagers(
                    companyKey, project.getProjectKey());
            List<User> canWork = projectApiService.getProjectWorkers(
                    companyKey, project.getProjectKey());
            List<User> canBrowse = projectApiService.getProjectGuests(
                    companyKey, project.getProjectKey());

            for (User user : canBrowse) {
                boolean show = userKeys.isEmpty()
                        || userKeys.contains("" + user.getKey());

                if (show) {
                    ProjectData data = computeProjRole(project, canManage,
                            canWork, canBrowse, user);

                    if (userKeyToProjData.get(user.getKey()) == null) {
                        userKeyToProjData.put(user.getKey(),
                                new ArrayList<ProjectData>());
                    }

                    if (data != null) {
                        userKeyToProjData.get(user.getKey()).add(data);
                    }
                }
            }
        }

        List<User> users = userApiService.getUsers(companyKey);
        cont.put("allUsers", sortUser(users));
        cont.put("userKeyToProjData", userKeyToProjData);
        Util.setDisplay(cont, null, "p_across_project_customer_report_man",
                SHOW_PAGE);
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
     * @param users
     *            collection of users
     * @return list of sort users
     */
    private List<User> sortUser(Collection<User> users) {
        List<User> allUsers = new ArrayList<User>();
        allUsers.addAll(users);
        Collections.sort(allUsers, new Comparator<User>() {
            public int compare(User u1, User u2) {
                return u1.getId().compareTo(u2.getId());
            }
        });
        return allUsers;
    }
}
