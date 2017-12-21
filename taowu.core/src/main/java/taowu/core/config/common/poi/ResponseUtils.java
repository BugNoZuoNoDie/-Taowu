package taowu.core.config.common.poi;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import taowu.core.config.common.util.JsonUtils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;

/**
 * Created by chenshiyang on 2016/8/4.
 */
public class ResponseUtils {

    /**
     * 转换为HttpServletRequest
     * @param request
     * @return
     */
    public static HttpServletResponse  getHttpResponse(ServletResponse request)
    {
        if (request instanceof HttpServletResponse) {
            return (HttpServletResponse) request;
        }
        return null;
    }


    /**
     * 写入文件
     * @param response
     * @param fileName
     * @throws IOException
     */
    public static  void responseFile(HttpServletResponse response,byte[] data,String fileName) throws IOException {
        // 清空response
        response.reset();
        // 设置响应头，控制浏览器下载该文件
        response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream;charset=utf-8");
        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
        toClient.write(data);
        toClient.flush();
        toClient.close();
    }


    /**
     * 直接输出Json
     * @param response
     */
    public  static void responseOut(HttpServletResponse response,
                                            String data) {
        responseOut(response,data,"application/json; charset=utf-8","UTF-8");
    }

    /**
     * 直接输出Json
     * @param response
     */
    public  static void responseOutWithJson(HttpServletResponse response,
                                       Object responseObject) {
        responseOut(response,JsonUtils.toJson(responseObject),"application/json; charset=utf-8","UTF-8");
    }

    /**
     * 输出内容
     * @param response
     * @param data
     * @param contentType
     * @param charset
     */
    public static void responseOut(HttpServletResponse response,
                                   String data,
                                   String contentType,String charset)
    {
        response.setCharacterEncoding(charset);
        response.setContentType(contentType);
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.append(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

    /**
     * 303-重定向
     * @param response
     * @param url
     * @throws ServletException
     * @throws IOException
     */
    public static void sendRedirect(HttpServletResponse response,String url)
            throws ServletException, IOException {
        response.setStatus(303);
        response.setHeader("Location", url);
    }
}
