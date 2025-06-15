package com.peter.tanxuannewapp.util;

import com.peter.tanxuannewapp.domain.annotation.ApiMessage;
import com.peter.tanxuannewapp.domain.resposne.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class FormatApiResponse implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true; // All responses must be overwritten => true
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        HttpServletResponse httpResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = httpResponse.getStatus();

        if(body instanceof String) {
            return body;
        }

        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(status);

        if(status >= 400){
            return body;
        } else {
            res.setData(body);
            ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
            res.setMessage(message != null ? message.value() : "Call api success");
        }
        return res;
    }
}
