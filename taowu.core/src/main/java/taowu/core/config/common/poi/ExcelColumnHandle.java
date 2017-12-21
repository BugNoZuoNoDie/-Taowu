package taowu.core.config.common.poi;

/**
 * Excel列处理器
 * Created by chenshiyang on 2017/1/17.
 */
public interface ExcelColumnHandle {

    /**
     * 列处理
     * @param columnValue
     * @return
     */
    ExcelColumnHandleResult<String> handle(String columnValue, int row, int column);

}
