package com.master.RetryPolicy.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.master.RetryPolicy.utils.SingletonInstance;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class CounterInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Intercept the request before it gets to the controller
        // and do something with it
        SingletonInstance.currentRequests++;
        try {
            if (Boolean.parseBoolean(request.getHeader("is-retry"))) {
                SingletonInstance.currentRetryRequests++;
            }
        } catch (Exception ignored) {}

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // Intercept the response after the controller has handled the request
        // and do something with it if needed
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Do something after the request has been handled and the response has been sent
        SingletonInstance.currentOutRequests--;
        try {
            if (Boolean.parseBoolean(request.getHeader("is-retry"))) {
                SingletonInstance.currentOutRetryRequests--;
            }
        } catch (Exception ignored) {}
    }
}
