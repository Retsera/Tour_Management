package com.J2EE.TourManagement.Util;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.J2EE.TourManagement.Model.RestResponse;
import com.J2EE.TourManagement.Util.annotation.ApiMessage;

@ControllerAdvice
public class FormatResponseAPI implements ResponseBodyAdvice{

  @Override
  public boolean supports(MethodParameter returnType, Class converterType) {
    return true;
  }

  @Override
  public Object
  beforeBodyWrite(Object body, MethodParameter returnType,
                  MediaType selectedContentType, Class selectedConverterType,
                  ServerHttpRequest request, ServerHttpResponse response) {
    HttpServletResponse servletResponse =
        ((ServletServerHttpResponse)response).getServletResponse();
    int status = servletResponse.getStatus();
    RestResponse<Object> res = new RestResponse<Object>();
    res.setStatusCode(status);

    if (body instanceof String){
        return body;
    }

    if (status >= 400) {
      return body;
    } else {
      res.setData(body);
      ApiMessage mess = returnType.getMethodAnnotation(ApiMessage.class);
      res.setMessage(mess == null ? "call api success" : mess.value());
    }
    return res;
  }
}
