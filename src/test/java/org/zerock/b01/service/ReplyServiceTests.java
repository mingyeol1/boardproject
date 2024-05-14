package org.zerock.b01.service;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ReplyDTO;

@SpringBootTest
@Log4j2
public class ReplyServiceTests {

    @Autowired
    private ReplyService replyService;

    @Test
    public void testRegister(){
        log.info(replyService.getClass().getName());

        ReplyDTO replyDTO = ReplyDTO.builder()
                .replyText("테스트")
                .replyer("응답자")
                .bno(307L)
                .build();


        log.info(replyService.register(replyDTO));
    }
    @Test
    public void testRead(){
        ReplyDTO replyDTO = replyService.read(103L);



        log.info(replyDTO);
    }

    @Test
    public void testModify(){
        ReplyDTO replyDTO = ReplyDTO.builder()
                .rno(104L)
                .replyText("바꿔버리기")
                .build();

        replyService.modify(replyDTO);
        //변경된지 확인
        log.info(replyService.read(replyDTO.getRno()));

    }

    @Test
    public void testDelete(){
       Long rno = 104L;

        replyService.remove(rno);
    }

    @Test
    public void testGetListOfBoard(){
        Long bno = 307L;
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder().build();
       PageResponseDTO<ReplyDTO> result = replyService.getListOfBoard(bno, pageRequestDTO);
       log.info("토탈 값 확인 : "+ result.getTotal());
       result.getDtoList().forEach(replyDTO -> log.info(replyDTO));
    }
}
