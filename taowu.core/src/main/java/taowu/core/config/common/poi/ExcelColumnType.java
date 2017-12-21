package taowu.core.config.common.poi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenshiyang on 2017/1/9.
 */
public enum ExcelColumnType {

    /**
     * 用户名
     */
    USER_NAME("用户名", "^[\\w|\\d]{4,16}$"),
    /**
     * 手机号
     */
    MOBILE("手机号", "^1[3|5|8]\\d{9}$"),
    /**
     * 密码
     */
    PASSWORD("密码", "^[\\w!@#$%^&*.]{6,16}$"),
    /**
     * 真实姓名
     */
    REAL_NAME("真实姓名", "^[\\u4e00-\\u9fa5 ]{2,10}$"),
    /**
     * 银行卡号
     */
    BANK_NUM("银行卡号", "^\\d{10,19}$"),
     /**
     * 钱
     */
    MONEY("钱", "^([1-9]\\d*|0)$"),
    /**
     * EMAIL
     */
    MAIL ("EMAIL","^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$"),

    /**
     * 其他
     */
    OTHERS("其他","others"),

    /**
     *无
     */
    NONE("无","none");

    /** 枚举值 */
    private String value;
    /** 描述 */
    private String desc;

    private ExcelColumnType(String desc, String value) {
        this.value = value;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ExcelColumnType getEnum(String value) {
        ExcelColumnType resultEnum = null;
        ExcelColumnType[] enumAry = ExcelColumnType.values();
        for (int i = 0; i < enumAry.length; i++) {
            if (enumAry[i].getValue().equals(value)) {
                resultEnum = enumAry[i];
                break;
            }
        }
        return resultEnum;
    }

    public static Map<String, Map<String, Object>> toMap() {
        ExcelColumnType[] ary = ExcelColumnType.values();
        Map<String, Map<String, Object>> enumMap = new HashMap<String, Map<String, Object>>();
        for (int num = 0; num < ary.length; num++) {
            Map<String, Object> map = new HashMap<String, Object>();
            String key = String.valueOf(getEnum(ary[num].getValue()));
            map.put("value", String.valueOf(ary[num].getValue()));
            map.put("desc", ary[num].getDesc());
            enumMap.put(key, map);
        }
        return enumMap;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List toList() {
        ExcelColumnType[] ary = ExcelColumnType.values();
        List list = new ArrayList();
        for (int i = 0; i < ary.length; i++) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("value", String.valueOf(ary[i].getValue()));
            map.put("desc", ary[i].getDesc());
            list.add(map);
        }
        return list;
    }

    /**
     * 取枚举的json字符串
     *
     * @return
     */
    public static String getJsonStr() {
        ExcelColumnType[] enums = ExcelColumnType.values();
        StringBuffer jsonStr = new StringBuffer("[");
        for (ExcelColumnType senum : enums) {
            if (!"[".equals(jsonStr.toString())) {
                jsonStr.append(",");
            }
            jsonStr.append("{id:'").append(senum).append("',desc:'").append(senum.getDesc()).append("',value:'").append(senum.getValue()).append("'}");
        }
        jsonStr.append("]");
        return jsonStr.toString();
    }
}
