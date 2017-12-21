package taowu.core.config.common.poi;

import java.util.HashMap;
import java.util.Map;

/**
 *  Excel列数据处理<br>
 * Created by chenshiyang on 2017/1/17.
 */
public class ExcelDataHandler {
    private Map<String,ExcelColumnHandle> handler=new HashMap<String, ExcelColumnHandle>();

    public void set(String key, ExcelColumnHandle handle){
        handler.put(key, handle);
    }

    public ExcelColumnHandle get(String key){
        return handler.get(key);
    }
}
