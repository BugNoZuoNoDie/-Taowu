package taowu.core.config.common.poi;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户
 */
public class ExcelDemo {

    @ExcelColumn(name = "姓名",width = 20,order = 0)
    private String name;

    @ExcelColumn(name = "年龄",required = true,width = 5,order = 1)
    private String age;

    @ExcelColumn(name="", skip = true, type = ExcelColumnType.MOBILE,regex = "sdfsdf",order = 2)
    private String password;

    @ExcelColumn(name = "xx",order = 3)
    private Double xx;

    @ExcelColumn(name = "yy",order = 4)
    private Date yy;

    @ExcelColumn(name = "锁定",order = 5)
    private Boolean locked;

    @ExcelColumn(name = "金额",order = 6)
    private BigDecimal db;


    public ExcelDemo(){

    }

    public ExcelDemo(String name, String age, String password, Double xx, Date yy, Boolean locked, BigDecimal db) {
        this.name = name;
        this.age = age;
        this.password = password;
        this.xx = xx;
        this.yy = yy;
        this.locked = locked;
        this.db = db;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Double getXx() {
        return xx;
    }

    public void setXx(Double xx) {
        this.xx = xx;
    }

    public Date getYy() {
        return yy;
    }

    public void setYy(Date yy) {
        this.yy = yy;
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }

    public BigDecimal getDb() {
        return db;
    }

    public void setDb(BigDecimal db) {
        this.db = db;
    }
}
