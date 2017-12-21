package taowu.core.config.common.poi;

import com.google.gson.GsonBuilder;
import taowu.core.config.common.util.DateUtils;
import taowu.core.config.common.util.ReflectUtils;
import taowu.core.config.common.util.StringUtils;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Excel导出
 */
public class ExcelUtils<E> {

    private static Logger logger = LoggerFactory.getLogger(ExcelUtils.class);
    private E row;
    private Class<E> cls;
    private ExcelResult<E> resultWrapper = new ExcelResult<>();
    private Map<String, ExcelColumn> fieldAnnotationMapper = new HashMap<>();
    private Field[] fields = null;


    public ExcelUtils(Class<E> cls) {
        this.cls = cls;
        try {
            // fields  通过反射获取到该类所有的变量
            fields = ReflectUtils.getClassFieldsAndSuperClassFields(cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (fields != null) {
            for (Field field : fields) {
                fieldAnnotationMapper.put(field.getName(), field.getAnnotation(ExcelColumn.class));
            }
        }
    }

    private E newRowData() {
        try {
            this.row = cls.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return this.row;
    }

    /**
     * 将数据写入到Excel文件
     *
     * @param filePath  文件路径
     * @param sheetName 工作表名称
     * @param title     工作表标题栏
     * @param data      工作表数据
     * @throws FileNotFoundException 文件不存在异常
     * @throws IOException           IO异常
     */
    public static void writeToFile(String filePath, String[] sheetName, List<? extends Object[]> title, List<? extends List<? extends Object[]>> data) throws FileNotFoundException, IOException {
        // 创建并获取工作簿对象
        Workbook wb = getWorkBook(sheetName, title, data);
        // 写入到文件
        FileOutputStream out = new FileOutputStream(filePath);
        wb.write(out);
        out.close();
    }

    /**
     * 创建工作簿对象<br>
     * <font color="red">工作表名称，工作表标题，工作表数据最好能够对应起来</font><br>
     * 比如三个不同或相同的工作表名称，三组不同或相同的工作表标题，三组不同或相同的工作表数据<br>
     * <b> 注意：<br>
     * 需要为每个工作表指定<font color="red">工作表名称，工作表标题，工作表数据</font><br>
     * 如果工作表的数目大于工作表数据的集合，那么首先会根据顺序一一创建对应的工作表名称和数据集合，然后创建的工作表里面是没有数据的<br>
     * 如果工作表的数目小于工作表数据的集合，那么多余的数据将不会写入工作表中 </b>
     *
     * @param sheetName 工作表名称的数组
     * @param title     每个工作表名称的数组集合
     * @param data      每个工作表数据的集合的集合
     * @return Workbook工作簿
     * @throws FileNotFoundException 文件不存在异常
     * @throws IOException           IO异常
     */
    public static Workbook getWorkBook(String[] sheetName, List<? extends Object[]> title, List<? extends List<? extends Object[]>> data) throws FileNotFoundException, IOException {

        // 创建工作簿
        Workbook wb = new SXSSFWorkbook();
        // 创建一个工作表sheet
        Sheet sheet = null;
        // 申明行
        Row row = null;
        // 申明单元格
        Cell cell = null;
        // 单元格样式
        CellStyle titleStyle = wb.createCellStyle();
        CellStyle cellStyle = wb.createCellStyle();
        // 字体样式
        Font font = wb.createFont();
        // 粗体
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
        titleStyle.setFont(font);
        // 水平居中
        titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
        // 垂直居中
        titleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        // 水平居中
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        // 垂直居中
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        cellStyle.setFillBackgroundColor(HSSFColor.BLUE.index);

        // 标题数据
        Object[] title_temp = null;

        // 行数据
        Object[] rowData = null;

        // 工作表数据
        List<? extends Object[]> sheetData = null;

        // 遍历sheet
        for (int sheetNumber = 0; sheetNumber < sheetName.length; sheetNumber++) {
            // 创建工作表
            sheet = wb.createSheet();
            // 设置默认列宽
            sheet.setDefaultColumnWidth(18);
            // 设置工作表名称
            wb.setSheetName(sheetNumber, sheetName[sheetNumber]);
            // 设置标题
            title_temp = title.get(sheetNumber);
            row = sheet.createRow(0);

            // 写入标题
            for (int i = 0; i < title_temp.length; i++) {
                cell = row.createCell(i);
                cell.setCellStyle(titleStyle);
                cell.setCellValue(title_temp[i].toString());
            }

            try {
                sheetData = data.get(sheetNumber);
            } catch (Exception e) {
                continue;
            }
            // 写入行数据
            for (int rowNumber = 0; rowNumber < sheetData.size(); rowNumber++) {
                // 如果没有标题栏，起始行就是0，如果有标题栏，行号就应该为1
                row = sheet.createRow(title_temp == null ? rowNumber : (rowNumber + 1));
                rowData = sheetData.get(rowNumber);
                for (int columnNumber = 0; columnNumber < rowData.length; columnNumber++) {
                    cell = row.createCell(columnNumber);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(rowData[columnNumber] + "");
                }
            }
        }
        return wb;
    }

    /**
     * 将数据写入到EXCEL文档
     *
     * @param list     数据集合
     * @param edf      数据格式化，比如有些数字代表的状态，像是0:女，1：男，或者0：正常，1：锁定，变成可读的文字
     *                 该字段仅仅针对Boolean,Integer两种类型作处理
     * @param filePath 文件路径
     * @throws Exception
     */
    public static <T> void writeToFile(List<T> list, ExcelDataFormatter edf, String filePath) throws Exception {
        // 创建并获取工作簿对象
        Workbook wb = getWorkBook(list, edf);
        // 写入到文件
        FileOutputStream out = new FileOutputStream(filePath);
        wb.write(out);
        out.close();
    }

    /**
     * 获得Workbook对象
     *
     * @param list 数据集合
     * @return Workbook
     * @throws Exception
     */
    public static <T> Workbook getWorkBook(List<T> list, ExcelDataFormatter edf) throws Exception {
        // 创建工作簿
        Workbook wb = new SXSSFWorkbook();

        if (list == null || list.size() == 0)
            return wb;

        // 创建一个工作表sheet
        Sheet sheet = wb.createSheet();
        // 申明行
        Row row = sheet.createRow(0);
        row.setHeight((short) (24 * 18));
        // 申明单元格
        Cell cell = null;

        CreationHelper createHelper = wb.getCreationHelper();
        Field[] fields = ReflectUtils.getClassFieldsAndSuperClassFields(list.get(0).getClass());
        XSSFCellStyle titleStyle = (XSSFCellStyle) wb.createCellStyle();
        titleStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        // 设置前景色
        titleStyle.setFillForegroundColor(new XSSFColor(new java.awt.Color(159, 213, 183)));
        titleStyle.setAlignment(CellStyle.ALIGN_CENTER);
        titleStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        Font font = wb.createFont();
        font.setColor(HSSFColor.BROWN.index);
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        // 设置字体
        titleStyle.setFont(font);

        //排序
        Map<String, ExcelColumn> fieldAnnotationMapper = new HashMap<>();
        if (fields != null) {
            for (Field field : fields) {
                fieldAnnotationMapper.put(field.getName(), field.getAnnotation(ExcelColumn.class));
            }
        }
        Arrays.sort(fields,new ExcelColumnCompare<Field>(fieldAnnotationMapper));

        int columnIndex = 0;
        ExcelColumn excel = null;
        for (Field field : fields) {
            field.setAccessible(true);
            excel = field.getAnnotation(ExcelColumn.class);
            if (excel == null || excel.skip() == true) {
                continue;
            }
            // 列宽注意乘256
            sheet.setColumnWidth(columnIndex, excel.width() * 256);
            // 写入标题
            cell = row.createCell(columnIndex);
            cell.setCellStyle(titleStyle);
            cell.setCellValue(excel.name());

            columnIndex++;
        }

        int rowIndex = 1;

        CellStyle cs = wb.createCellStyle();
        cs.setVerticalAlignment(CellStyle.VERTICAL_CENTER);

        for (T t : list) {
            row = sheet.createRow(rowIndex);
            row.setHeight((short) (24 * 18));
            columnIndex = 0;
            Object o = null;
            for (Field field : fields) {
                field.setAccessible(true);
                // 忽略标记skip的字段
                excel = field.getAnnotation(ExcelColumn.class);
                if (excel == null || excel.skip() == true) {
                    continue;
                }
                // 数据
                cell = row.createCell(columnIndex);

                o = field.get(t);
                // 如果数据为空，则设置为空字符
                if (o == null) {
                    o = "";
                }

                // 处理日期类型
                if (o instanceof Date) {
                    cs.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss"));
                    cell.setCellStyle(cs);
                    cell.setCellValue((Date) field.get(t));
                } else if (o instanceof Double || o instanceof Float) {
                    cell.setCellValue((Double) field.get(t));
                } else if (o instanceof Boolean) {
                    Boolean bool = (Boolean) field.get(t);
                    if (edf == null) {
                        cell.setCellValue(bool);
                    } else {
                        Map<String, String> map = edf.get(field.getName());
                        if (map == null) {
                            cell.setCellValue(bool);
                        } else {
                            cell.setCellValue(map.get(bool.toString().toLowerCase()));
                        }
                    }

                } else if (o instanceof Integer) {
                    Integer intValue = (Integer) field.get(t);
                    if (edf == null) {
                        cell.setCellValue(intValue);
                    } else {
                        Map<String, String> map = edf.get(field.getName());
                        if (map == null) {
                            cell.setCellValue(intValue);
                        } else {
                            cell.setCellValue(map.get(intValue.toString()));
                        }
                    }
                } else if (o instanceof BigDecimal) {
                    BigDecimal v = (BigDecimal) o;
                    DecimalFormat format = new DecimalFormat("0.00");
                    format.setRoundingMode(RoundingMode.HALF_UP);
                    String vformat = format.format(v);
                    if (edf == null) {
                        cell.setCellValue(vformat);
                    } else {
                        Map<String, String> map = edf.get(field.getName());
                        if (map == null) {
                            cell.setCellValue(vformat);
                        } else {
                            cell.setCellValue(map.get(vformat));
                        }
                    }
                } else {
                    cell.setCellValue(String.valueOf(o));
                }

                columnIndex++;
            }

            rowIndex++;
        }

        return wb;
    }


    /**
     * 从文件读取数据，最好是所有的单元格都是文本格式，日期格式要求yyyy-MM-dd HH:mm:ss,布尔类型0：真，1：假
     *
     * @param edf  数据格式化
     * @param file Excel文件，支持xlsx后缀，xls的没写，基本一样
     * @return
     * @throws Exception
     */
    public ExcelResult<E> readFromFile(ExcelDataFormatter edf, File file) throws Exception {
        return readFromFile(edf, new FileInputStream(file));
    }

    /**
     * 从文件读取数据，最好是所有的单元格都是文本格式，日期格式要求yyyy-MM-dd HH:mm:ss,布尔类型0：真，1：假
     *
     * @param edf         数据格式化
     * @param inputStream Excel文件数据流
     * @return
     * @throws Exception
     */
    public ExcelResult<E> readFromFile(ExcelDataFormatter edf, InputStream inputStream) throws Exception {

        Map<String, Field> textToKey = new HashMap<>();
        //得到excel的列名
        ExcelColumn _excel = null;
        for (Field field : fields) {
            _excel = field.getAnnotation(ExcelColumn.class);
            if (_excel == null || _excel.skip() == true) {
                continue;
            }
            textToKey.put(_excel.name(), field);
        }

        Workbook wb = new XSSFWorkbook(inputStream);

        Sheet sheet = wb.getSheetAt(0);
        Row title = sheet.getRow(0);
        // 标题数组，后面用到，根据索引去标题名称，通过标题名称去字段名称用到 textToKey
        String[] titles = new String[title.getPhysicalNumberOfCells()];
        for (int i = 0; i < title.getPhysicalNumberOfCells(); i++) {
            titles[i] = title.getCell(i).getStringCellValue();
        }

        List<E> list = new ArrayList<E>();

        E e = null;

        int rowIndex = 0;
        int columnCount = titles.length;
        Cell cell = null;
        Row row = null;

        for (Iterator<Row> it = sheet.rowIterator(); it.hasNext(); ) {

            row = it.next();
            if (rowIndex++ == 0) {
                continue;
            }


            if (row == null && row.getCell(1) == null) {
                break;
            }

            e = newRowData();
            boolean validated = true;
            if (CheckRowNull(row)>0){
            for (int i = 0; i < columnCount; i++) {
                cell = row.getCell(i);
                ExcelColumn column = fieldAnnotationMapper.get(textToKey.get(titles[i]).getName());
                try {
                    // cell == null 时必填验证 前提是这一行不能为null
                        if ((cell==null|| cell.equals(""))  && column.required() && CheckRowNull(row)>0) {
                            resultWrapper.error(rowIndex, column.name(), "", "需要必填");
                            throw new ValidationException("第" + rowIndex + "行 " + column.name() + " 出错: 需要必填");
                    }
                    //其他验证
                    validateCellValue(column, cell);
                    // 设值
                    readCellContent(column, textToKey.get(titles[i]), cell, e, edf);
                } catch (ValidationException exception) {
                    validated = false;
                }
            }
            }

            if (validated) {
                resultWrapper.addResult(e);
            }
        }
        return resultWrapper;
    }

    /**
     * 从单元格读取数据，根据不同的数据类型，使用不同的方式读取<br>
     * 有时候POI自作聪明，经常和我们期待的数据格式不一样，会报异常，<br>
     * 我们这里采取强硬的方式<br>
     * 使用各种方法，知道尝试到读到数据为止，然后根据Bean的数据类型，进行相应的转换<br>
     * 如果尝试完了（总共7次），还是不能得到数据，那么抛个异常出来，没办法了
     *
     * @param field Bean所有的字段数组
     * @param cell  单元格对象
     * @param e
     * @throws Exception
     */
    public void readCellContent(ExcelColumn excelColumn, Field field, Cell cell, E e, ExcelDataFormatter edf) throws Exception {

        if (cell == null) {
            return;
        }

        String cellValue = getCellStringValue(cell);

        field.setAccessible(true);
        Map<String, String> map = null;
        if (edf.get(field.getName()) != null) {
            map = edf.get(field.getName());
            cellValue = map.get(cellValue);
        }

        try {

            if (field.getType().equals(Date.class)) {
                // 日期格式
                //	{ "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
                //  "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
                //  "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
                field.set(e, DateUtils.parseDate(cellValue));
            } else if (field.getType().equals(String.class)) {
                field.set(e, cellValue);
            } else if (field.getType().equals(Long.class)) {
                field.set(e, Long.valueOf(cellValue));
            } else if (field.getType().equals(Integer.class)) {
                if (StringUtils.isNoneBlank(cellValue)){
                    field.set(e, Integer.valueOf(StringUtils.subZeroAndDot(cellValue)));
                }
            } else if (field.getType().equals(BigDecimal.class)) {
                field.set(e, BigDecimal.valueOf(Double.parseDouble(cellValue)));
            } else if (field.getType().equals(Float.class)) {
                field.set(e, Float.parseFloat(cellValue));
            } else if (field.getType().equals(Double.class)) {
                if (!"".equals(cellValue)){
                    field.set(e, Double.parseDouble(cellValue));
                }
            } else if (field.getType().equals(Boolean.class)) {
                field.set(e, Boolean.parseBoolean(cellValue));
            }
        } catch (RuntimeException ex) {
            logger.error("excel 列格式转换出错：", ex);
            resultWrapper.error(cell.getRowIndex() + 1, excelColumn.name(), cellValue, "格式转化出错!");
            throw new ValidationException("第" + cell.getRowIndex() + 1 + "行 " + field.getName() + " 出错: 格式转化出错");
        }
    }

    /**
     * Cell验证
     * @param column 列
     * @param cell Cell
     */
    private void validateCellValue(ExcelColumn column, Cell cell) {
        String cellString = getCellStringValue(cell);
        Boolean b=StringUtils.isBlank(cellString);
        // 必填验证

        if (column.required() && StringUtils.isBlank(cellString)) {
            resultWrapper.error(cell.getRowIndex() + 1, column.name(), cellString, "需要必填");
            throw new ValidationException("第" + cell.getRowIndex() + 1 + "行 " + column.name() + " 出错: 需要必填");
        }

        // 正则验证
        if (column.type() != ExcelColumnType.NONE && !regexMatch(column.type().getValue(), cellString)) {
            resultWrapper.error(cell.getRowIndex() + 1, column.name(), cellString, column.type().getDesc() + "验证失败!");
            throw new ValidationException("第" + cell.getRowIndex() + 1 + "行 " + column.name() + " 出错: " + column.type().getDesc() + "验证失败!");
        }

        if (column.type() == ExcelColumnType.OTHERS && StringUtils.isNotBlank(column.regex()) && !regexMatch(column.regex(), cellString)) {
            resultWrapper.error(cell.getRowIndex() + 1, column.name(), cellString, "正则表达式 " + column.regex() + " 验证失败!");
            throw new ValidationException("第" + cell.getRowIndex() + 1 + "行 " + column.name() + " 出错: 正则表达式 " + column.regex() + " 验证失败!");
        }
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        String cellValue = "";
        switch (cell.getCellType()) {
            case XSSFCell.CELL_TYPE_BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case XSSFCell.CELL_TYPE_NUMERIC:
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case XSSFCell.CELL_TYPE_STRING:
                cellValue = cell.getStringCellValue();
                break;
            case XSSFCell.CELL_TYPE_BLANK:
                cellValue = "";
                break;
            case XSSFCell.CELL_TYPE_FORMULA:
                cellValue = cell.getCellFormula();
                break;
            default:
                cellValue = "";
                break;
        }
        return cellValue;
    }

    /**
     * 正则匹配
     *
     * @param regex 正则表达式
     * @param value 待验证的值
     * @return true or false
     */
    private boolean regexMatch(String regex, String value) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    //测试
    public static void main(String[] args) throws Exception {

        System.out.println("写Excel");

        List<ExcelDemo> list = new ArrayList<ExcelDemo>();
        ExcelDemo u = new ExcelDemo();
        u.setAge("");
        u.setName("fdsafdsa1");
        u.setXx(123.23D);
        u.setYy(new Date());
        u.setLocked(false);
        u.setDb(new BigDecimal(123));
        list.add(u);

        u = new ExcelDemo();
        u.setAge("w");
        u.setName("fdsafdsa2");
        u.setXx(123.23D);
        u.setYy(new Date());
        u.setLocked(true);
        u.setDb(new BigDecimal(234));
        list.add(u);

        u = new ExcelDemo();
        u.setAge("123");
        u.setName("fdsafdsa3");
        u.setXx(123.23D);
        u.setYy(new Date());
        u.setLocked(false);
        u.setDb(new BigDecimal(2344));
        list.add(u);

        u = new ExcelDemo();
        u.setAge("22");
        u.setName("fdsafdsa4");
        u.setXx(123.23D);
        u.setYy(new Date());
        u.setLocked(true);
        u.setDb(new BigDecimal(908));
        list.add(u);

        ExcelDataFormatter edf = new ExcelDataFormatter();
        Map<String, String> map = new HashMap<String, String>();
        map.put("真", "true");
        map.put("假", "false");
        edf.set("locked", map);

        //writeToFile(list,edf, "/Users/aaron/Downloads/x.xlsx");

        ExcelResult<ExcelDemo> xx = new ExcelUtils<>(ExcelDemo.class).readFromFile(edf, new File("/Users/aaron/Downloads/x.xlsx"));
        System.out.println(new GsonBuilder().create().toJson(xx));
    }

    //判断行为空
    private int CheckRowNull(Row hssfRow){
        int num = 0;
        Iterator<Cell> cellItr =hssfRow.iterator();
        while(cellItr.hasNext()){
            Cell c =cellItr.next();
            if(StringUtils.isNoneBlank(c.toString())){
                num++;
            }
        }
        return num;
    }

}
