package taowu.core.config.common.poi;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenshiyang on 2017/1/20.
 */
public class ExcelColumnCompare<T> implements Comparator<T> {

    private Map<String, ExcelColumn> fieldAnnotationMapper = new HashMap<>();

    /**
     * 初始化
     * @param fieldAnnotationMapper
     */
    public ExcelColumnCompare(Map<String, ExcelColumn> fieldAnnotationMapper)
    {
        this.fieldAnnotationMapper=fieldAnnotationMapper;
    }


    @Override
    public int compare(Object o1, Object o2) {
        if(o1 instanceof Field && o2 instanceof Field) {
            Field f1=(Field)o1;
            Field f2=(Field)o2;
            //
            ExcelColumn sort1=fieldAnnotationMapper.get(f1.getName());
            ExcelColumn sort2=fieldAnnotationMapper.get(f2.getName());
            if(null!=sort1&&null!=sort2)
            {
                return sort1.order()>sort2.order()?1:-1;
            }
        }

        //
        return 0;
    }
}
