package util;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class OrderUtil {
    public static String readBody(HttpServletRequest request) throws UnsupportedEncodingException {
        int length = request.getContentLength();
        byte[] buffer = new byte[length];
        try (InputStream inputStream = request.getInputStream()) {
            inputStream.read(buffer,0,length);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //构造String的时候，必须要指定该字符串的编码方式（字节数据转换成字符数据）
        return new String(buffer,"UTF-8");
    }
}
