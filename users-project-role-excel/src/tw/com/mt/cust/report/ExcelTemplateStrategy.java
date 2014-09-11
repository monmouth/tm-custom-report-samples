package tw.com.mt.cust.report;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import tw.com.mt.cust.report.data.ProjectData;
import tw.com.mt.tm.api.bean.User;

/** Display excel template. */
public class ExcelTemplateStrategy {

    /** Workbook object of excel template. */
    private Workbook book;

    /** Resource bundle. */
    private ResourceBundle rb;

    /** The only sheet of workbook. */
    private Sheet sheet;
    
    private Map<Integer, List<ProjectData>> userKeyToProjData;
    
    private List<User> allUsers;

    /**
     * Constructor create new HSSFWorkbook and new sheet.
     *
     * @param bom
     *            to export as excel
     * @param rb
     *            CTU resource bundle
     */
    public ExcelTemplateStrategy(final ResourceBundle rb,
            final Map<Integer, List<ProjectData>> userKeyToProjData,
            final List<User> allUsers) {
        this.rb = rb;
        this.userKeyToProjData = userKeyToProjData;
        this.allUsers = allUsers;
        book = new HSSFWorkbook();
        sheet = book.createSheet();
    }

    public Workbook create() {
        sheet.createRow(0).createCell(0).setCellValue(
                new HSSFRichTextString(rb.getString("sample.report.user_proj_role")));
        initHeaderRow();
        initDataRow();
        return book;
    }

    /** Initialize header. */
    private void initHeaderRow() {
        Row nameRow = sheet.createRow(1);
        nameRow.createCell(0).setCellValue(
                rb.getString("sample.report.user_id"));
        nameRow.createCell(1).setCellValue(
                rb.getString("sample.report.user_name"));
        nameRow.createCell(2).setCellValue(
                rb.getString("sample.report.proj_name"));
        nameRow.createCell(3).setCellValue(
                rb.getString("sample.report.proj_owner"));
        nameRow.createCell(4).setCellValue(
                rb.getString("sample.report.proj_role"));
    }

    /** Initialize exist data. */
    private void initDataRow() {
        int dataRowStartIndex = 2;

        for (User u : allUsers) {
            List<ProjectData> projectDatas = userKeyToProjData.get(u.getKey());

            // 合併儲存格
            CellRangeAddress region0 = new CellRangeAddress(
                    dataRowStartIndex,
                    dataRowStartIndex + projectDatas.size() - 1,
                    0, 0);
            sheet.addMergedRegion(region0);

            CellRangeAddress region1 = new CellRangeAddress(
                    dataRowStartIndex,
                    dataRowStartIndex + projectDatas.size() - 1,
                    1, 1);
            sheet.addMergedRegion(region1);

            for (ProjectData pd : projectDatas) {
                Row row = sheet.createRow(dataRowStartIndex++);
                row.createCell(0).setCellValue(
                        new HSSFRichTextString(u.getId()));
                row.createCell(1).setCellValue(
                        new HSSFRichTextString(u.getName()));
                row.createCell(2).setCellValue(
                        new HSSFRichTextString(pd.getProjectName()));
                row.createCell(3).setCellValue(
                        new HSSFRichTextString(pd.getProjectOwnerName()));
                row.createCell(4).setCellValue(
                        new HSSFRichTextString(pd.getProjRoleName()));
            }
        }
    }
}
