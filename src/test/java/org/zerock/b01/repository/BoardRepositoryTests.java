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
import org.springframework.test.annotation.Commit;
import org.zerock.b01.domain.Board;
import org.zerock.b01.domain.BoardImage;
import org.zerock.b01.dto.*;
import org.zerock.b01.service.BoardService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private BoardService boardService;

    //insert
    @Test
    public void TestInsert(){//1부터 100까지 만들겠다.

            Board board = Board.builder()
                    .title("title...")
                    .content("content........")
                    .writer("user") //유저는 0번부터 10까지. 100개가 만들어진다.
                    .build();
            Board result = boardRepository.save(board);
            log.info("BNO : "+result.getBno());

    }

    @Test
    public void testSelect(){
        Long bno = 100L;

      Optional<Board> result = boardRepository.findById(bno);
      Board board = result.orElseThrow();

      log.info(board);
    }

    // update
    // Entity는 생성시 불변이면 좋이나, 변경이 일어날 경우 최소한으로 설계한다.
    @Test
    public void testUpdate(){
        Long bno = 100L;

        Optional<Board> result = boardRepository.findById(bno);

        Board board = result.orElseThrow();

        board.change("update....title1000","update content.........100");

        boardRepository.save(board);
    }

    //삭제하기
    @Test
    public void testDelete(){
        Long bno = 1L;

        boardRepository.deleteById(bno);
    }

    //Pageable과 Page<E> 타입을 이용한 페이징 처리
    // 페이징 처리는 Pageble이라는 타입의 객체를 구성해서 파라미터로 전달...
    // Pageable은 인터페이스로 설계되어 있고, 일반적으로 PageRequest.of()를 이용해서 개발한다.
    // PageRequest.of(페이지번호, 사이즈) : 페이지번호는 0번부터...
    // PageRequest.of(페이지번호, 사이즈,Sort) : Sort 객체를 통한 정렬 조건 추가
    // PageRequest.of(페이지번호, 사이즈,Sort.Direction) : 정렬 방향과 여러 속성추가 지정.

    // Pageable로 값을 넘기면 반환 타입은 Page<T>이를 이용하게 된다.
    @Test
    public void testPaging(){
        //1page order by bno desc
        Pageable pageable =
                PageRequest.of(0,10, Sort.by("bno").descending());

       Page<Board> result = boardRepository.findAll(pageable);

        log.info("total count : "+result.getTotalElements() ); // 총 게시물 갯수
        log.info("total page : "+ result.getTotalPages()); //총 페이지 갯수
        log.info("page number : "+ result.getNumber()); //페이지번호
        log.info("page size" + result.getSize()); //페이지 사이즈
        log.info("다음 페이지 여부 : "+ result.hasNext());
        log.info("이전 페이지 여부 : "+ result.hasPrevious());

        List<Board> boardList = result.getContent();
        boardList.forEach(board -> log.info(board));
    }

    // 쿼리 메서드 및 @Query 테스트
    @Test
    public void testQueryMethod(){

        Pageable pageable = PageRequest.of(0,10);

        String keyword = "title";

       Page<Board> result = boardRepository.findByTitleContainingOrderByBnoDesc(
               keyword,
               pageable
       );
    result.getContent().forEach(board -> log.info(board));
    }

    @Test
    public void testQueryAnnotation() {
        Pageable pageable = PageRequest.of(
                0,
                10,
                Sort.by("bno").descending());
        Page<Board> result = boardRepository.findKeyword("title",pageable);

        result.getContent().forEach(board -> log.info(board));
    }

    @Test
    public void testGetTime(){
        log.info(boardRepository.getTime());
    }

    @Test
    public void testSearch1(){
        //2page order by bno desc
        Pageable pageable = PageRequest.of(1,10,Sort.by("bno").descending());

        boardRepository.search1(pageable);
    }

    @Test
    public void testSearchAll(){

        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(1,10,Sort.by("bno").descending());

        Page<Board> result =  boardRepository.searchAll(types,keyword,pageable);

        result.getContent().forEach(board -> log.info(board));
        log.info("사이즈 : "+ result.getSize());
        log.info("페이지 번호 : "+result.getNumber());
        log.info("이전 페이지 : " +result.hasPrevious());
        log.info("다음 페이지 : "+result.hasNext());
    }

    @Test
    public void testSearchWithReplyCount() {

        String[] types = {"t","c","w"};

        String keyword = "12345";
                                                //페이지가 0이 1부터 시작이니 0~ 10까지
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types,keyword,pageable);

        result.getContent().forEach(boardListReplyCountDTO -> {
            log.info("boardListReplyCountDTO : " + boardListReplyCountDTO);
        });

    }

    @Test
    public void testInsertWithImages(){
        Board board = Board.builder()
                .title("Image test")
                .content("첨부파일 테스트")
                .writer("tester")
                .build();
        for (int i = 0; i< 3; i++){
            board.addImage(UUID.randomUUID().toString(),"file"+i+".jpg");
        }
        boardRepository.save(board);
    }

    @Test
    public void testReadWithImage(){
        Optional<Board> result = boardRepository.findByIdWithImages(1L);
        Board board = result.orElseThrow();


        log.info(board);
        log.info("------------------");
        for (BoardImage boardImage : board.getImageSet()){
            log.info(boardImage);
        }
    }
    @Transactional
    @Commit
    @Test
    public void testModifyImages(){
        Optional<Board> result = boardRepository.findByIdWithImages(1L);
        Board board = result.orElseThrow();

        //기존의 첨부파일 삭제
        board.clearImages();
        //새로운 첨부파일듯
        for (int i = 0; i<2; i++){
            board.addImage(UUID.randomUUID().toString(),"file"+i+".jpg");
        }
        boardRepository.save(board);
    }

    @Test
    @Transactional
    @Commit
    public void testRemoveAll(){
        Long bno = 1L;

        replyRepository.deleteByBoard_Bno(bno);

        boardRepository.deleteById(bno);
    }
    @Test
    public void testInsertAll(){
        for (int i =1; i<=100; i++) {

            Board board = Board.builder()
                    .title("Title.."+i)
                    .content("content.."+i)
                    .writer("writer.."+i)
                    .build();

            for (int j = 0; j< 3; j++){
                if (i % 5 ==0){
                    continue;
                }
                board.addImage(UUID.randomUUID().toString(),i+"file"+j+".jpg");
            }
            boardRepository.save(board);
        }
    }
    @Transactional
    @Test
    public void testSearchImageReplyCount(){
        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());
      //  boardRepository.searchWithReplyCount(null,null,pageable);

        Page<BoardListAllDTO> result = boardRepository.searchWithAll(null,null,pageable);

        log.info("-----------------------------");
        log.info(result.getTotalElements());

        result.getContent().forEach(boardListAllDTO -> log.info(boardListAllDTO));
    }

    @Test
    public void testRegisterWithImages(){
        log.info(boardRepository.getClass().getName());

        BoardDTO boardDTO = BoardDTO.builder()
                .title("File...Sample...Title....")
                .content("Sample Content.....")
                .writer("user00")
                .build();

        boardDTO.setFileNames(
                Arrays.asList(
                        UUID.randomUUID()+"_aaa.jpg",
                        UUID.randomUUID()+"_bbb.jpg",
                        UUID.randomUUID()+"_ccc.jpg"
                ));
    Long bno = boardService.register(boardDTO);

    log.info("bno : "+ bno);

    }

    @Test
    public void testReadAll(){
        Long bno = 102L;

        BoardDTO boardDTO = boardService.readOne(bno);
        log.info("board DTO -------"+boardDTO);

        for (String fileName : boardDTO.getFileNames()) {
            log.info(fileName);
        }

    }

    @Test
    public void testModify(){
        //변경에 필요한 데이터
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(102L)
                .title("Update...102")
                .content("Updated contend 102......")
                .build();

        //첨부파일을 하나 추가
        boardDTO.setFileNames(Arrays.asList(UUID.randomUUID()+"_zzz.jpg"));

        boardService.modify(boardDTO);
    }

    @Test
    public void testRemove(){
        Long bno = 1L;

        boardService.remove(bno);
    }

    @Test
    public void testListWithAll(){
        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<BoardListAllDTO> responseDTO =
                boardService.listWithAll(pageRequestDTO);

        List<BoardListAllDTO> dtoList = responseDTO.getDtoList();

        dtoList.forEach(boardListAllDTO -> {
            log.info(boardListAllDTO.getBno()+":"+boardListAllDTO.getTitle());

            if (boardListAllDTO.getBoardImages() != null){
                for (BoardImageDTO boardImageDTO : boardListAllDTO.getBoardImages()) {
                    log.info(boardImageDTO);
                }
            }
            log.info("=================================");

        });
    }



}
