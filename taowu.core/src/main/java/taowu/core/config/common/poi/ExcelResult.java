package taowu.core.config.common.poi;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel结果
 * Created by chenshiyang on 2017/1/17.
 */
public class ExcelResult<T> {

    /**
     * 错误
     */
    private List<String> errors =new ArrayList<>();

    /**
     * 警告
     */
    private List<String> warns=new ArrayList<>();

    /**
     * 结果集
     */
    private List<T> result =new ArrayList<>();


    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getWarns() {
        return warns;
    }

    public void setWarns(List<String> warns) {
        this.warns = warns;
    }

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

    public void error(int row, String colName, String value, String msg) {
        errors.add("第" + row + "行--列:" + colName + " 值:" + value + " 异常:" + msg);
    }

    public void addResult(T t) {
        if(result == null){
            result = new ArrayList<>();
        }

        if (t != null) {
            result.add(t);
        }
    }
}
