
package taowu.core.config.common.util;

import org.apache.commons.lang3.StringEscapeUtils;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类, 继承org.apache.commons.lang3.StringUtils类
 * @version 2013-05-22
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    private static final char SEPARATOR = '_';
    private static final String CHARSET_NAME = "UTF-8";
	public static final String EMPTY_STRING = "";
	public static final char DEFAULT_DELIMITER_CHAR = ',';
	public static final char DEFAULT_QUOTE_CHAR = '"';
    
    /**
     * 转换为字节数组
     * @param str
     * @return
     */
    public static byte[] getBytes(String str){
    	if (str != null){
    		try {
				return str.getBytes(CHARSET_NAME);
			} catch (UnsupportedEncodingException e) {
				return null;
			}
    	}else{
    		return null;
    	}
    }

	/**
	 * 将字符编码转换成UTF-8
	 * @param str
	 * @param oldCharset
	 * @return
	 * @throws UnsupportedEncodingException
     */
	public String toUTF_8(String str,String oldCharset) throws UnsupportedEncodingException {
		return this.changeCharset(str,oldCharset, CHARSET_NAME);
	}


	/**
	 * 字符串编码转换的实现方法
	 * @param str    待转换的字符串
	 * @param oldCharset    源字符集
	 * @param newCharset    目标字符集
	 */
	public String changeCharset(String str, String oldCharset, String newCharset) throws UnsupportedEncodingException {
		if(str != null) {
			//用源字符编码解码字符串
			byte[] bs = str.getBytes(oldCharset);
			return new String(bs, newCharset);
		}
		return null;
	}
    
    /**
     * 转换为字节数组
     * @param bytes
     * @return
     */
    public static String toString(byte[] bytes){
    	try {
			return new String(bytes, CHARSET_NAME);
		} catch (UnsupportedEncodingException e) {
			return EMPTY;
		}
    }
    
    /**
     * 是否包含字符串
     * @param str 验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inString(String str, String... strs){
    	if (str != null){
        	for (String s : strs){
        		if (str.equals(trim(s))){
        			return true;
        		}
        	}
    	}
    	return false;
    }
    
	/**
	 * 替换掉HTML标签方法
	 */
	public static String replaceHtml(String html) {
		if (isBlank(html)){
			return "";
		}
		String regEx = "<.+?>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(html);
		String s = m.replaceAll("");
		return s;
	}
	
	/**
	 * 替换为手机识别的HTML，去掉样式及属性，保留回车。
	 * @param html
	 * @return
	 */
	public static String replaceMobileHtml(String html){
		if (html == null){
			return "";
		}
		return html.replaceAll("<([a-z]+?)\\s+?.*?>", "<$1>");
	}
	
	/**
	 * 替换为手机识别的HTML，去掉样式及属性，保留回车。
	 * @param txt
	 * @return
	 */
	public static String toHtml(String txt){
		if (txt == null){
			return "";
		}
		return replace(replace(Encodes.escapeHtml(txt), "\n", "<br/>"), "\t", "&nbsp; &nbsp; ");
	}

	/**
	 * 缩略字符串（不区分中英文字符）
	 * @param str 目标字符串
	 * @param length 截取长度
	 * @return
	 */
	public static String abbr(String str, int length) {
		if (str == null) {
			return "";
		}
		try {
			StringBuilder sb = new StringBuilder();
			int currentLength = 0;
			for (char c : replaceHtml(StringEscapeUtils.unescapeHtml4(str)).toCharArray()) {
				currentLength += String.valueOf(c).getBytes("GBK").length;
				if (currentLength <= length - 3) {
					sb.append(c);
				} else {
					sb.append("...");
					break;
				}
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String abbr2(String param, int length) {
		if (param == null) {
			return "";
		}
		StringBuffer result = new StringBuffer();
		int n = 0;
		char temp;
		boolean isCode = false; // 是不是HTML代码
		boolean isHTML = false; // 是不是HTML特殊字符,如&nbsp;
		for (int i = 0; i < param.length(); i++) {
			temp = param.charAt(i);
			if (temp == '<') {
				isCode = true;
			} else if (temp == '&') {
				isHTML = true;
			} else if (temp == '>' && isCode) {
				n = n - 1;
				isCode = false;
			} else if (temp == ';' && isHTML) {
				isHTML = false;
			}
			try {
				if (!isCode && !isHTML) {
					n += String.valueOf(temp).getBytes("GBK").length;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			if (n <= length - 3) {
				result.append(temp);
			} else {
				result.append("...");
				break;
			}
		}
		// 取出截取字符串中的HTML标记
		String temp_result = result.toString().replaceAll("(>)[^<>]*(<?)",
				"$1$2");
		// 去掉不需要结素标记的HTML标记
		temp_result = temp_result
				.replaceAll(
						"</?(AREA|BASE|BASEFONT|BODY|BR|COL|COLGROUP|DD|DT|FRAME|HEAD|HR|HTML|IMG|INPUT|ISINDEX|LI|LINK|META|OPTION|P|PARAM|TBODY|TD|TFOOT|TH|THEAD|TR|area|base|basefont|body|br|col|colgroup|dd|dt|frame|head|hr|html|img|input|isindex|li|link|meta|option|p|param|tbody|td|tfoot|th|thead|tr)[^<>]*/?>",
						"");
		// 去掉成对的HTML标记
		temp_result = temp_result.replaceAll("<([a-zA-Z]+)[^<>]*>(.*?)</\\1>",
				"$2");
		// 用正则表达式取出标记
		Pattern p = Pattern.compile("<([a-zA-Z]+)[^<>]*>");
		Matcher m = p.matcher(temp_result);
		List<String> endHTML = new ArrayList<>();
		while (m.find()) {
			endHTML.add(m.group(1));
		}
		// 补全不成对的HTML标记
		for (int i = endHTML.size() - 1; i >= 0; i--) {
			result.append("</");
			result.append(endHTML.get(i));
			result.append(">");
		}
		return result.toString();
	}
	
	/**
	 * 转换为Double类型
	 */
	public static Double toDouble(Object val){
		if (val == null){
			return 0D;
		}
		try {
			return Double.valueOf(trim(val.toString()));
		} catch (Exception e) {
			return 0D;
		}
	}

	/**
	 * 转换为Float类型
	 */
	public static Float toFloat(Object val){
		return toDouble(val).floatValue();
	}

	/**
	 * 转换为Long类型
	 */
	public static Long toLong(Object val){
		return toDouble(val).longValue();
	}

	/**
	 * 转换为Integer类型
	 */
	public static Integer toInteger(Object val){
		return toLong(val).intValue();
	}
	

	/**
	 * 获得用户远程地址
	 */
	public static String getRemoteAddr(HttpServletRequest request){
		String remoteAddr = request.getHeader("X-Real-IP");
        if (isNotBlank(remoteAddr)) {
        	remoteAddr = request.getHeader("X-Forwarded-For");
        }
        if (isNotBlank(remoteAddr)) {
        	remoteAddr = request.getHeader("Proxy-Client-IP");
        }
        if (isNotBlank(remoteAddr)) {
        	remoteAddr = request.getHeader("WL-Proxy-Client-IP");
        }
        return remoteAddr != null ? remoteAddr : request.getRemoteAddr();
	}

	/**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld" 
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld" 
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    
    /**
	 * 驼峰命名法工具
	 * @return
	 * 		toCamelCase("hello_world") == "helloWorld" 
	 * 		toCapitalizeCamelCase("hello_world") == "HelloWorld"
	 * 		toUnderScoreCase("helloWorld") = "hello_world"
	 */
    public static String toUnderScoreCase(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i > 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }
    
    /**
     * 如果不为空，则设置值
     * @param target
     * @param source
     */
    public static void setValueIfNotBlank(String target, String source) {
		if (isNotBlank(source)){
			target = source;
		}
	}
 
    /**
     * 转换为JS获取对象值，生成三目运算返回结果
     * @param objectString 对象串
     *   例如：row.auth.id
     *   返回：!row?'':!row.auth?'':!row.auth.id?'':row.auth.id
     */
    public static String jsGetVal(String objectString){
    	StringBuilder result = new StringBuilder();
    	StringBuilder val = new StringBuilder();
    	String[] vals = split(objectString, ".");
    	for (int i=0; i<vals.length; i++){
    		val.append("." + vals[i]);
    		result.append("!"+(val.substring(1))+"?'':");
    	}
    	result.append(val.substring(1));
    	return result.toString();
    }

	/**
	 * 清除字符前后空格，为空返回NULL
	 * @param in
	 * @return
     */
	public static String clean(String in) {
		String out = in;

		if (in != null) {
			out = in.trim();
			if (out.equals(EMPTY_STRING)) {
				out = null;
			}
		}
		return out;
	}

	/**
	 * 清除字符前后空格，为空返回NULL
	 * @param in
	 * @return
	 */
	public static String trim(String in) {
		String out = in;
		if (in != null) {
			out = in.trim();
		}
		return out;
	}

	/**
	 * Check that the given String is neither <code>null</code> nor of length 0.
	 * Note: Will return <code>true</code> for a String that purely consists of whitespace.
	 * <p/>
	 * <code>StringUtils.hasLength(null) == false<br/>
	 * StringUtils.hasLength("") == false<br/>
	 * StringUtils.hasLength(" ") == true<br/>
	 * StringUtils.hasLength("Hello") == true</code>
	 * <p/>
	 * Copied from the Spring Framework while retaining all license, copyright and author information.
	 *
	 * @param str the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not null and has length
	 * @see #hasText(String)
	 */
	public static boolean hasLength(String str) {
		return (str != null && str.length() > 0);
	}



	/**
	 * Check whether the given String has actual text.
	 * More specifically, returns <code>true</code> if the string not <code>null</code>,
	 * its length is greater than 0, and it contains at least one non-whitespace character.
	 * <p/>
	 * <code>StringUtils.hasText(null) == false<br/>
	 * StringUtils.hasText("") == false<br/>
	 * StringUtils.hasText(" ") == false<br/>
	 * StringUtils.hasText("12345") == true<br/>
	 * StringUtils.hasText(" 12345 ") == true</code>
	 * <p/>
	 * <p>Copied from the Spring Framework while retaining all license, copyright and author information.
	 *
	 * @param str the String to check (may be <code>null</code>)
	 * @return <code>true</code> if the String is not <code>null</code>, its length is
	 *         greater than 0, and it does not contain whitespace only
	 * @see Character#isWhitespace
	 */
	public static boolean hasText(String str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * tokenizeToStringArray
	 * @param str
	 * @param delimiters
     * @return
     */
	public static String[] tokenizeToStringArray(String str, String delimiters) {
		return tokenizeToStringArray(str, delimiters, true, true);
	}

	/**
	 * tokenizeToStringArray
	 * @param str
	 * @param delimiters
	 * @param trimTokens
	 * @param ignoreEmptyTokens
     * @return
     */
	public static String[] tokenizeToStringArray(
			String str, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens) {

		if (str == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(str, delimiters);
		List tokens = new ArrayList();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0) {
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}

	/**
	 * 转化为字符数组
	 * @param collection
	 * @return
     */
	public static String[] toStringArray(Collection collection) {
		if (collection == null) {
			return null;
		}
		return (String[]) collection.toArray(new String[collection.size()]);
	}

	public static String[] splitKeyValue(String aLine) throws ParseException {
		String line = clean(aLine);
		if (line == null) {
			return null;
		}
		String[] split = line.split(" ", 2);
		if (split.length != 2) {
			//fallback to checking for an equals sign
			split = line.split("=", 2);
			if (split.length != 2) {
				String msg = "Unable to determine Key/Value pair from line [" + line + "].  There is no space from " +
						"which the split location could be determined.";
				throw new ParseException(msg, 0);
			}

		}

		split[0] = clean(split[0]);
		split[1] = clean(split[1]);
		if (split[1].startsWith("=")) {
			//they used spaces followed by an equals followed by zero or more spaces to split the key/value pair, so
			//remove the equals sign to result in only the key and values in the
			split[1] = clean(split[1].substring(1));
		}

		if (split[0] == null) {
			String msg = "No valid key could be found in line [" + line + "] to form a key/value pair.";
			throw new ParseException(msg, 0);
		}
		if (split[1] == null) {
			String msg = "No corresponding value could be found in line [" + line + "] for key [" + split[0] + "]";
			throw new ParseException(msg, 0);
		}

		return split;
	}

	public static String[] split(String line) {
		return split(line, DEFAULT_DELIMITER_CHAR);
	}

	public static String[] split(String line, char delimiter) {
		return split(line, delimiter, DEFAULT_QUOTE_CHAR);
	}

	public static String[] split(String line, char delimiter, char quoteChar) {
		return split(line, delimiter, quoteChar, quoteChar);
	}

	public static String[] split(String line, char delimiter, char beginQuoteChar, char endQuoteChar) {
		return split(line, delimiter, beginQuoteChar, endQuoteChar, false, true);
	}

	/**
	 * Splits the specified delimited String into tokens, supporting quoted tokens so that quoted strings themselves
	 * won't be tokenized.
	 * <p/>
	 * This method's implementation is very loosely based (with significant modifications) on
	 * <a href="http://blogs.bytecode.com.au/glen">Glen Smith</a>'s open-source
	 * <a href="http://opencsv.svn.sourceforge.net/viewvc/opencsv/trunk/src/au/com/bytecode/opencsv/CSVReader.java?&view=markup">CSVReader.java</a>
	 * file.
	 * <p/>
	 * That file is Apache 2.0 licensed as well, making Glen's code a great starting point for us to modify to
	 * our needs.
	 *
	 * @param aLine          the String to parse
	 * @param delimiter      the delimiter by which the <tt>line</tt> argument is to be split
	 * @param beginQuoteChar the character signifying the start of quoted text (so the quoted text will not be split)
	 * @param endQuoteChar   the character signifying the end of quoted text
	 * @param retainQuotes   if the quotes themselves should be retained when constructing the corresponding authz
	 * @param trimTokens     if leading and trailing whitespace should be trimmed from discovered tokens.
	 * @return the tokens discovered from parsing the given delimited <tt>line</tt>.
	 */
	public static String[] split(String aLine, char delimiter, char beginQuoteChar, char endQuoteChar,
								 boolean retainQuotes, boolean trimTokens) {
		String line = clean(aLine);
		if (line == null) {
			return null;
		}

		List<String> tokens = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		boolean inQuotes = false;

		for (int i = 0; i < line.length(); i++) {

			char c = line.charAt(i);
			if (c == beginQuoteChar) {
				// this gets complex... the quote may end a quoted block, or escape another quote.
				// do a 1-char lookahead:
				if (inQuotes  // we are in quotes, therefore there can be escaped quotes in here.
						&& line.length() > (i + 1)  // there is indeed another character to check.
						&& line.charAt(i + 1) == beginQuoteChar) { // ..and that char. is a quote also.
					// we have two quote chars in a row == one quote char, so consume them both and
					// put one on the authz. we do *not* exit the quoted text.
					sb.append(line.charAt(i + 1));
					i++;
				} else {
					inQuotes = !inQuotes;
					if (retainQuotes) {
						sb.append(c);
					}
				}
			} else if (c == endQuoteChar) {
				inQuotes = !inQuotes;
				if (retainQuotes) {
					sb.append(c);
				}
			} else if (c == delimiter && !inQuotes) {
				String s = sb.toString();
				if (trimTokens) {
					s = s.trim();
				}
				tokens.add(s);
				sb = new StringBuilder(); // start work on next authz
			} else {
				sb.append(c);
			}
		}
		String s = sb.toString();
		if (trimTokens) {
			s = s.trim();
		}
		tokens.add(s);
		return tokens.toArray(new String[tokens.size()]);
	}

	/**
	 * 填充字符串
	 * @param str
	 * @param strLength
	 * @return
	 */
	public static String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				//sb.append("0").append(str);// 左补0
				sb.append(str).append("0");//右补0
				str = sb.toString();
				strLen = str.length();
			}
		}
		return str;
	}

	/**
	 * 使用java正则表达式去掉多余的.与0
	 * @param s
	 * @return
	 */
	public static String subZeroAndDot(String s){
		if(s.indexOf(".") > 0){
			s = s.replaceAll("0+?$", "");//去掉多余的0
			s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
		}
		return s;
	}

	/**
	 * 首字母大写
	 * @param str
	 * @return
	 */
	public static String firstLetterToUpperCase(String str) {
		if (isNotEmpty(str)) {
			return str.substring(0, 1).toUpperCase().concat(str.substring(1));
		} else {
			return str;
		}
	}
}
