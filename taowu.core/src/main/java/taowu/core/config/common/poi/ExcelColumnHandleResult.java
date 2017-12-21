package taowu.core.config.common.poi;

import java.util.List;

/**
 * Created by chenshiyang on 2017/1/17.
 */
public class ExcelColumnHandleResult<T> {


    /**
     * 错误
     */
    private String error;

    /**
     * 警告
     */
    private List<String> warns;

    /**
     * 结果
     */
    private T result;


    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getWarns() {
        return warns;
    }

    public void setWarns(List<String> warns) {
        this.warns = warns;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

}
