package org.zerock.b01.controller.advice;

import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Log4j2
public class CustomRestAdvice {
//레스트 풀 예외처리.

    //입력값 잘못 넣을 시 예외 처리.
    @ExceptionHandler(BindException.class) //예외처리시 누구를 동작시킬지. BindException.class
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED) //상태정보.. 내가 기대하지 않는 값이 나올 때 처리. (기대하지 않는 값을 받았다.  417 에러.)
    public ResponseEntity<Map<String, String>> handleBindException(BindException e) { // ResponseEntity<Map<String, String>>(문자열을 전달.) //BindException e 예외 결과값을 매개변수로 받겠다.
        log.error(e); // 받은 내용 출력
        Map<String, String> errorMap = new HashMap<>(); //내용을 키 벨류 형태로 받기위해 객체 생성.
        if (e.hasErrors()){ // 만약 에러가 발생한다면?
            BindingResult bindingResult = e.getBindingResult();

            bindingResult.getFieldErrors().forEach(fieldError -> {
                errorMap.put(fieldError.getField(), fieldError.getCode()); // 키 벨류 형태로 코드형태로 나오게 한다.
            });
        }
        return ResponseEntity.badRequest().body(errorMap);// 응댑 내용은(body(errorMap)) /badRequest() 400번 내용 출력
    }

    @ExceptionHandler(DataIntegrityViolationException.class) //예외처리시 누구를 동작시킬지.
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED) //EXPECTATION_FAILED기댓값 오류 417.
    public ResponseEntity<Map<String, String>> handlerFKException(Exception e){
        log.error(e);
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("time",""+System.currentTimeMillis());
        errorMap.put("msg","constrant fails");

        return ResponseEntity.badRequest().body(errorMap); //잘못된 값을
    }

    @ExceptionHandler({NoSuchElementException.class,
                        EmptyResultDataAccessException.class})
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String, String>> handleNoSuchElementException(Exception e){
        log.error(e);

        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("time",""+System.currentTimeMillis());
        errorMap.put("msg", "No Such Element Exception");
        return ResponseEntity.badRequest().body(errorMap);
    }

}
