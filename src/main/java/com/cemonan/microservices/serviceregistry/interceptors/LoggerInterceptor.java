package com.cemonan.microservices.serviceregistry.interceptors;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Component
public class LoggerInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
        throws Exception {
        String method = request.getMethod();
        String path = request.getServletPath();
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String now = dateFormatter.format(date);
        System.out.println(String.format("[%s] - %s %s", now, method, path));
        return true;
    }
}
