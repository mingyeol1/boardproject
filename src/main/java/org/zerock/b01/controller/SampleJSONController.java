package org.zerock.b01.controller;


import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController //여기 있는 모든 메서드는 데이터 저리를 하겠다.
@Log4j2
public class SampleJSONController {



    @GetMapping("/helloArr")
    public String[] helloArr(){
        log.info("helloArr.............");
        return new String[] {"AAA","BBB","CCC"};
    }
}
