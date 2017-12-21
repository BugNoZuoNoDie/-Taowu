package taowu.core.config.common.poi;

import java.lang.annotation.*;

/**
 * Excel注解，用以生成Excel表格文件
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
@Inherited
public @interface  ExcelColumn {

    /**
     * 列名
     * @return
     */
    String name() default "";

    /**
     * 排序
     * @return
     */
    int order() default 0;

    /**
     * 宽度
     * @return
     */
    int width() default 20;

    /**
     * 忽略该字段
     * @return
     */
    boolean skip() default false;

    /**
     * 必需的
     * @return
     */
    boolean required() default false;

    /**
     * 列类型，用于验证数据
     * @return
     */
    ExcelColumnType type() default ExcelColumnType.NONE;

    /**
     * 自定义正则
     * @return
     */
    String regex() default "";
}
