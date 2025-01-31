package com.example.cartcheckoutservice.clients;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientRequestInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            // Propagate specific headers (e.g., Authorization, Correlation-ID)
            requestTemplate.header("Set-Cookie", request.getHeader("Set-Cookie"));
            requestTemplate.header("Set-Cookie2", request.getHeader("Set-Cookie2"));
        }
    }
}
