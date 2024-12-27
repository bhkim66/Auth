package com.bhkim.auth.config.aspect;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.bhkim.auth.exception.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static com.bhkim.auth.exception.ExceptionEnum.INTERNAL_SERVER_ERROR;

public class ApiLogAspect {
    @Aspect
    @Component
    public class ApiLogAop {
        final AmazonDynamoDBClient amazonDynamoDBClient;
        final HttpServletRequest httpServletRequest;
        final HttpServletResponse httpServletResponse;

        @Autowired
        public ApiLogAop(AmazonDynamoDBClient amazonDynamoDBClient, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
            this.amazonDynamoDBClient = amazonDynamoDBClient;
            this.httpServletRequest = httpServletRequest;
            this.httpServletResponse = httpServletResponse;
        }

        @Pointcut("within(net.kpnp.controller.api.*)")
        private void apiAspect() {
        }

        @Around("apiAspect()")
        public Object aspectGetApiMemberSeq(ProceedingJoinPoint joinPoint) throws Throwable {
            Object result = joinPoint.proceed();
            //상용만 출력
            //httpMethod 가져오기
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            String httpMethod = request.getMethod();

            String apiUrl = String.valueOf(httpServletRequest.getRequestURL());
            String userAgent = httpServletRequest.getHeader("User-Agent");
            String ip = httpServletRequest.getHeader("X-Forwarded-For");

            ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
            StringBuilder stringToArgs = new StringBuilder();
            for (Object obj : joinPoint.getArgs()) {
                if (!(obj instanceof HttpServletRequest)) {
                    stringToArgs.append(objectMapper.writeValueAsString(obj));
                }
            }
            try {
                LogPayload logContents = LogPayload.builder()
                        .ip(ip)
                        .userAgent(userAgent)
                        .request(stringToArgs.toString())
                        .response(objectMapper.writeValueAsString(result))
                        .build();
                String payload = objectMapper.writeValueAsString(logContents);

                DynamoTable dynamoTable = DynamoTable.builder()
                        .router("[" + httpMethod + "]" + apiUrl)
                        .payload(payload)
                        .build();

                try {
                    DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
                    mapper.save(dynamoTable);
                } catch (Exception e) {
                    throw new ApiException(INTERNAL_SERVER_ERROR);
                }
            } catch (Exception e) {
                throw new ApiException(INTERNAL_SERVER_ERROR);
            }
            return result;
        }
    }

}
