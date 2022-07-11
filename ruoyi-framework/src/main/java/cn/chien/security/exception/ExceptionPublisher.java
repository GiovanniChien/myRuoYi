package cn.chien.security.exception;

import cn.chien.core.domain.AjaxResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qian.diqi
 * @date 2022/7/7
 */
@Component
public class ExceptionPublisher {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void process(Exception e, HttpRequestResponseHolder holder) throws IOException {
        process(e, holder, HttpStatus.OK);
    }

    public void process(Exception e, HttpRequestResponseHolder holder, HttpStatus httpStatus)
            throws IOException {
        HttpServletResponse response = holder.getResponse();

        if (httpStatus != null) {
            response.setStatus(httpStatus.value());
        }
        else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        response.setHeader("Content-Type", MediaType.APPLICATION_JSON_UTF8_VALUE);

        PrintWriter pw = response.getWriter();
        objectMapper.writeValue(pw, AjaxResult.error(e.getMessage()));
        pw.flush();
        pw.close();
    }

}
