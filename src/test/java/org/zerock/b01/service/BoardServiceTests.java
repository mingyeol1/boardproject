package org.zerock.b01.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b01.domain.Board;
import org.zerock.b01.dto.BoardDTO;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;

import java.util.NoSuchElementException;

@SpringBootTest
@Log4j2
public class BoardServiceTests {

    @Autowired
    private BoardService boardService;

    @Test
    public void testRegister() {
        log.info(boardService.getClass().getName());

        BoardDTO boardDTO = BoardDTO.builder()
                .title("Sample1 test...")
                .content("Sample1 content....")
                .writer("user1")
                .build();

        long bno = boardService.register(boardDTO); //bno 반환

        log.info("bno : "+bno); //없으면 insert 있으면 update


    }

    @Test
    public void testReadOne(){
        long bno = 101L;

       BoardDTO boardDTO = boardService.readOne(bno);

       log.info(boardDTO);
    }

    @Test
    public void testModify(){
        //변경에 필요한 데이터만 처리.
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(102L)
                .title("update title...101")
                .content("update content....101")
                .build();
        boardService.modify(boardDTO);
        //변경 된지 확인한것
        log.info(boardService.readOne(boardDTO.getBno()));
    }
    @Test
    public void testDelete(){
        Long bno = 102L;

        boardService.remove(bno);
        Assertions.assertThrows(NoSuchElementException.class, () -> boardService.remove(bno));
 //       BoardDTO boardDTO = boardService.readOne(bno);
  //      log.info(boardDTO);
    }
    @Test
    public void testList(){
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .type("twc")
                .keyword("1")
                .page(1)
                .size(10)
                .build();
        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);
        log.info(responseDTO);

    }
}
