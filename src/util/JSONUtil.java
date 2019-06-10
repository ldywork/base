package util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializationConfig.Feature;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;


public class JSONUtil {
	

	
	/**
	 * @描述：将json解析为对象
	 * @开发人员：
	 * @开发时间：2015年7月24日 上午08:00:00
	 * @param jsonstr 要解析的json字符串
	 * @param clazz 目标对象的字节码对象
	 * @return 转换后的对象
	 */
	public static  <T> T readJson2Entity(String jsonstr,Class<T> clazz){
		try {
			ObjectMapper om = new ObjectMapper();
			om.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true); 
	        T acc = om.readValue(jsonstr, clazz);
	        return acc;
	    } catch (JsonParseException e) {
	    } catch (JsonMappingException e) {
	    } catch (IOException e) {
	    }
		return null;
	}
	
    
}

