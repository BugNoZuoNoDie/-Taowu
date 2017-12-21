package taowu.core.config.common.json;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.JSONPObject;

import taowu.core.config.common.util.DateUtils;
import taowu.core.config.common.util.StringUtils;


/**
 * Created by chenshiyang on 2017/5/8.
 */
public class JsonMapper  extends ObjectMapper {

    private static final long serialVersionUID = 1L;

    private static Logger logger = LoggerFactory.getLogger(JsonMapper.class);

    public JsonMapper() {
        this(Include.ALWAYS);
    }

    public JsonMapper(Include include) {

        SimpleModule module = new SimpleModule();
        //==============序列化
        // Long序列化
        module.addSerializer(Long.class, new LongSerializer());
        // Date对象转化为JSON字符串
        module.addSerializer(Date.class, new DateSerializer());
        // BigDecimal序列化 精确到两位小数
        module.addSerializer(BigDecimal.class, new BigDecimalSerializer());
        // HTML解码
        module.addSerializer(String.class,new HtmlJsonSerializer());

        //==============反序列化
        // JSON字符串转化为Long
        module.addDeserializer(Long.class, new LongDeserializer());
        // JSON字符串转化为Date对象
        module.addDeserializer(Date.class, new DateDeserializer());

        //
        this.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);  //空对象时不报错
        this.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);  //接受空字符转化为NULL
        this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES); //未知属性时，不报错
        this.disable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);  //缺少属性时，不报错
        this.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,true);  //allow no '
        this.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES,true);  //allow '
        this.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS,true);

        this.getSerializerProvider().setNullValueSerializer(new NullJsonSerializer());
        this.registerModule(module);

        // 允许单引号、允许不带引号的字段名称
        this.enableSimple();

        // 设置输出时包含属性的风格
        if (include != null) {
            this.setSerializationInclusion(include);
        }

        // 设置时区
        this.setTimeZone(TimeZone.getDefault());
    }

    /**
     * 创建只输出非Null且非Empty(如List.isEmpty)的属性到Json字符串的Mapper,建议在外部接口中使用.
     */
    public static JsonMapper getInstance() {
        return  new JsonMapper().enableSimple();
    }

    /**
     * 创建只输出初始值被改变的属性到Json字符串的Mapper, 最节约的存储方式，建议在内部接口中使用。
     */
    public static JsonMapper nonDefaultMapper() {
        return new JsonMapper(Include.NON_DEFAULT);
    }

    /**
     * 创建只输出不为NULL为EMPTY属性到Json字符串的Mapper, 最节约的存储方式，建议在内部接口中使用。
     */
    public static JsonMapper nonEmptyMapper() {
        return new JsonMapper(Include.NON_EMPTY);
    }

    /**
     * 创建只输出来为NULL属性到Json字符串的Mapper, 最节约的存储方式，建议在内部接口中使用。
     */
    public static JsonMapper nonMapper() {
        return new JsonMapper(Include.NON_NULL);
    }

    /**
     * Object可以是POJO，也可以是Collection或数组。
     * 如果对象为Null, 返回"null".
     * 如果集合为空集合, 返回"[]".
     */
    public String toJson(Object object) {
        try {
            return this.writeValueAsString(object);
        } catch (IOException e) {
            logger.warn("write to json string error:" + object, e);
            return null;
        }
    }

    /**
     * 反序列化POJO或简单Collection如List<String>.
     *
     * 如果JSON字符串为Null或"null"字符串, 返回Null.
     * 如果JSON字符串为"[]", 返回空集合.
     *
     * 如需反序列化复杂Collection如List<MyBean>, 请使用fromJson(String,JavaType)
     * @see #fromJson(String, JavaType)
     */
    public <T> T fromJson(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            return this.readValue(jsonString, clazz);
        } catch (IOException e) {
            logger.warn("parse json string error:" + jsonString, e);
            return null;
        }
    }

    /**
     * 反序列化复杂Collection如List<Bean>, 先使用函數createCollectionType构造类型,然后调用本函数.
     * @see #createCollectionType(Class, Class...)
     */
    @SuppressWarnings("unchecked")
    public <T> T fromJson(String jsonString, JavaType javaType) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        try {
            return (T) this.readValue(jsonString, javaType);
        } catch (IOException e) {
            logger.warn("parse json string error:" + jsonString, e);
            return null;
        }
    }

    /**
     * 構造泛型的Collection Type如:
     * ArrayList<MyBean>, 则调用constructCollectionType(ArrayList.class,MyBean.class)
     * HashMap<String,MyBean>, 则调用(HashMap.class,String.class, MyBean.class)
     */
    public JavaType createCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {
        return this.getTypeFactory().constructParametricType(collectionClass, elementClasses);
    }

    /**
     * 當JSON裡只含有Bean的部分屬性時，更新一個已存在Bean，只覆蓋該部分的屬性.
     */
    @SuppressWarnings("unchecked")
    public <T> T update(String jsonString, T object) {
        try {
            return (T) this.readerForUpdating(object).readValue(jsonString);
        } catch (JsonProcessingException e) {
            logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
        } catch (IOException e) {
            logger.warn("update json string:" + jsonString + " to object:" + object + " error.", e);
        }
        return null;
    }

    /**
     * 輸出JSONP格式數據.
     */
    public String toJsonP(String functionName, Object object) {
        return toJson(new JSONPObject(functionName, object));
    }

    /**
     * 設定是否使用Enum的toString函數來讀寫Enum,
     * 為False時時使用Enum的name()函數來讀寫Enum, 默認為False.
     * 注意本函數一定要在Mapper創建後, 所有的讀寫動作之前調用.
     */
    public JsonMapper enableEnumUseToString() {
        this.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        this.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        return this;
    }

//    /**
//     * 支持使用Jaxb的Annotation，使得POJO上的annotation不用与Jackson耦合。
//     * 默认会先查找jaxb的annotation，如果找不到再找jackson的。
//     */
//    public JsonMapper enableJaxbAnnotation() {
//        JaxbAnnotationModule module = new JaxbAnnotationModule();
//        this.registerModule(module);
//        return this;
//    }

    /**
     * 允许单引号
     * 允许不带引号的字段名称
     */
    public JsonMapper enableSimple() {
        this.configure(Feature.ALLOW_SINGLE_QUOTES, true);
        this.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        return this;
    }

    /**
     * 取出Mapper做进一步的设置或使用其他序列化API.
     */
    public ObjectMapper getMapper() {
        return this;
    }

    /**
     * 对象转换为JSON字符串
     * @param object
     * @return
     */
    public static String toJsonString(Object object){
        return JsonMapper.getInstance().toJson(object);
    }

    /**
     * JSON字符串转换为对象
     * @param jsonString
     * @param clazz
     * @return
     */
    public static Object fromJsonString(String jsonString, Class<?> clazz){
        return JsonMapper.getInstance().fromJson(jsonString, clazz);
    }

    /**
     * Long 序列化转换
     */
    public static final class LongSerializer extends JsonSerializer<Long> {
        @Override
        public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            gen.writeString(String.valueOf(value));
        }
    }


    /**
     * JSON String to Long
     */
    private static class LongDeserializer extends StdDeserializer<Long> {

        LongDeserializer() {
            super(Long.class);
        }

        @Override
        public Long deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String str = p.getText();
            if (org.apache.commons.lang3.StringUtils.isEmpty(str)) {
                return null;
            }
           Long value=Long.parseLong(str);

            //如果为NULL返回0
            if(null==value)
            {
              return 0L;
            }
            return value;
        }
    }

    /**
     * JSON String to Date
     */
    private static class DateDeserializer extends StdDeserializer<Date> {

        DateDeserializer() {
            super(Date.class);
        }

        @Override
        public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String str = p.getText();
            if (org.apache.commons.lang3.StringUtils.isEmpty(str)) {
                return null;
            }
            Date date= DateUtils.parseDate(str);

            //如果为NULL尝试转为TIME
            if(null==date)
            {
                try{
                    Long time=Long.valueOf(str);
                    date=new Date(time);
                }
                catch (NumberFormatException ex)
                {
                    return null;
                }
            }
            return date;
        }
    }

    private static class DateSerializer extends JsonSerializer<Date> {

        @Override
        public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String format = formatter.format(value);
            gen.writeString(format);
        }
    }

    /**
     * BigDecimal序列化转换
     */
    private static class BigDecimalSerializer extends JsonSerializer<BigDecimal> {
        @Override
        public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider provider) throws IOException, JsonProcessingException {
            DecimalFormat format = new DecimalFormat("0.00");
            format.setRoundingMode(RoundingMode.HALF_UP);
            gen.writeNumber(format.format(value));
        }
    }

    /**
     * 进行HTML解码
     */
    private static class HtmlJsonSerializer extends JsonSerializer<String> {
        @Override
        public void serialize(String value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeString(StringEscapeUtils.unescapeHtml4(value));
        }
    }

    /**
     * NULL 转化为""
     */
    private static class NullJsonSerializer extends JsonSerializer<Object> {
        @Override
        public void serialize(Object value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            if (value == null) {
                gen.writeString("");
            }
        }
    }

    public static class  testCl{
        String[] status;

        public String[] getStatus() {
            return status;
        }

        public void setStatus(String[] status) {
            this.status = status;
        }

        public Integer getStart() {
            return start;
        }

        public void setStart(Integer start) {
            this.start = start;
        }

        private Integer start;


    }

    /**
     * 测试
     */
    public static void main(String[] args) {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("id", 1);
        map.put("pId", -1);
        map.put("name", "根节点");
        list.add(map);
        map = new HashMap<>();
        map.put("id", 2);
        map.put("pId", 1);
        map.put("name", "你好");
        map.put("open", true);
        list.add(map);
        String json = JsonMapper.getInstance().toJson(list);
        System.out.println(json);
        //

        System.out.println("=="+JsonMapper.getInstance().toJson(new testCl()));
    }
}
