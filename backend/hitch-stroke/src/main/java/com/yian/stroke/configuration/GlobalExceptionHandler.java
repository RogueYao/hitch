package com.yian.stroke.configuration;

import com.yian.commons.domin.vo.response.ResponseVO;
import com.yian.commons.exception.BusinessRuntimeException;
import com.netflix.client.ClientException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * @param req
     * @param e
     * @return
     */
    @ExceptionHandler(value = BusinessRuntimeException.class)
    @ResponseBody
    public ResponseVO businessRuntimeException(HttpServletRequest req, BusinessRuntimeException e) {
        return ResponseVO.error(e);
    }

    @ExceptionHandler(value = ClientException.class)
    @ResponseBody
    public ResponseVO clientException(HttpServletRequest req, ClientException e) {
        return ResponseVO.error(e.getErrorMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseVO methodArgumentNotValidException(HttpServletRequest req, MethodArgumentNotValidException e) {
        // 获取所有异常
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());
        return ResponseVO.error(String.join(",", errors));
    }


}