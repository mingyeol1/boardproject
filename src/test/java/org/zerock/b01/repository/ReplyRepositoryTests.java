package org.zerock.b01.repository;

import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.b01.domain.Board;
import org.zerock.b01.domain.Reply;

import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class ReplyRepositoryTests {

    @Autowired
    private ReplyRepository replyRepository;
    //테스트 : 있는 게시글 중에 댓글 추가.... 307번에 댓글 추가.(insert 처리)
    @Test
    public void testInsert() {
        //실제 DB에 있는 bno를 선택.
        Long bno = 307L;

        Board board = Board.builder().bno(bno).build();
        IntStream.rangeClosed(1, 100).forEach(i -> {
            ;
            Reply reply = Reply.builder()
                    .board(board)
                    .replyText("잉여킹")
                    .replyer("잉여킹맨이야")
                    .build();
            replyRepository.save(reply);

        });
    }
    @Transactional //쿼리가 다 성공해야 성공처리를 한다.// 처리가 다 끝날때 까지 완료를 안한다.
    @Test
    public void testBoardReplies(){
        //실제 게시물 번호
        Long bno = 307L;

        Pageable pageable = PageRequest.of(0,10, Sort.by("rno").descending());

        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);
        log.info("게시물의 댓글 수 : "+result.getTotalElements());

        result.getContent().forEach(reply -> {
            log.info(reply);
        });

    }
    @Test
    public void  testTotal(){
        log.info("count : "+replyRepository.count());
    }

}
