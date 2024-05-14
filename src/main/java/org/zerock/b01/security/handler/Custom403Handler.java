package org.zerock.b01.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Log4j2
public class Custom403Handler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info("--------------------------ACCESS DENIED ------------------------------");

        response.setStatus(HttpStatus.FORBIDDEN.value()); //FORBIDDEN.value() 403 에러를 띄워주라

        // JSON 요청이었는지 홧인
        String contentType = request.getHeader("Content-Type");
        boolean jsonRequest = contentType.startsWith("application/json"); //시작 문자가 어플리케이션/제이슨인지 보겠다.
        log.info(" isJSON : "+jsonRequest);

        // 일반 request인 경우
        if (!jsonRequest){
            response.sendRedirect("/member/login?error=ACCESS_DENIED");
        }

    }
}
