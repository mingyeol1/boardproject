package org.zerock.b01.repository.search;

/*
     BoardSearch를 상속받는 구현체
*/

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.Board;
import org.zerock.b01.domain.QBoard;
import org.zerock.b01.domain.QReply;
import org.zerock.b01.dto.BoardImageDTO;
import org.zerock.b01.dto.BoardListAllDTO;
import org.zerock.b01.dto.BoardListReplyCountDTO;

import java.util.List;
import java.util.stream.Collectors;

public class BoardSearchImpl extends QuerydslRepositorySupport implements BoardSearch{


    public BoardSearchImpl(){
        super(Board.class);
    }

    @Override
    public Page<Board> search1(Pageable pageable) {

        //Q도메인을 이용한 쿼리 작성 및 테스트
        //Querydsl의 목적은 "타입" 기반으로 "코드"를 이용해서 JPQL 쿼리를 생성하고 실행한다...
        //Q도메인은 이 때에 코드를 만드는 대신에 클래스가 Q도메인 클래스...

        //1. Q도메인 객체 생성
        QBoard board = QBoard.board; //Q 도메인 객체

        JPQLQuery<Board> query = from(board); // select .. from board 라는 뜻.

        //BooleanBuilder()사용
        BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

        booleanBuilder.or(board.title.contains("11")); // title에서 11을 가지고 있는지 /title like
        booleanBuilder.or(board.content.contains("11")); //content like

//        query.where(board.title.contains("1")); //where title like ...

        query.where(booleanBuilder);        //)
        query.where(board.bno.gt(0L)); // .gt 는 ~보다 크다. bno > 0

        //paging
        this.getQuerydsl().applyPagination(pageable,query);

        List<Board> title = query.fetch();  // JPQLQuery에 대한 실행

        long count = query.fetchCount();    //fetchCount 로 인한 쿼리실행

        return null;
    }

    @Override
    public Page<Board> searchAll(String[] types, String keyword, Pageable pageable) {
        //Qdomain 객체 생성
        QBoard board = QBoard.board;

        //2. QL 작성...
        JPQLQuery<Board> query = from(board); //select... from board

        if (( types != null && types.length > 0) && keyword != null) {
            //검색 조건과 키워드가 있는경우

            BooleanBuilder booleanBuilder = new BooleanBuilder(); //조건을 위한 ( 가 시작

            for (String type: types){
                switch (type){
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword)); // title like concat('%',keyword,'%')
                        break;
                    case"c":
                        booleanBuilder.or(board.content.contains(keyword));// content like concat('%',keyword,'%')
                        break;
                    case"w":
                        booleanBuilder.or(board.writer.contains(keyword));// writer like concat('%',keyword,'%')
                        break;
                }
            }   //for end
            query.where(booleanBuilder);  // 조건을 위한 ) 가 생성
        }// end if
        //> dno > 0
        query.where(board.bno.gt(0L));

        // paging
        this.getQuerydsl().applyPagination(pageable, query);

        List<Board> list = query.fetch();
        long count = query.fetchCount();

        //Page<T> 형식으로 반환 : Page<Board>
        //PageImpl을 통해서 반환 : (list((실제 등록 데이터)알아온 데이터), pageable, total(전체 개수))
        return new PageImpl<>(list, pageable, count);
    }

    @Override
    public Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types, String keyword, Pageable pageable) {

        QBoard board = QBoard.board;  //select * from board      //Query dsl에 의해 만들어짐
        QReply reply = QReply.reply;  // select * from board leftjoin  reply on reply.board_bno=board.bno

        JPQLQuery<Board> query = from(board);
        query.leftJoin(reply).on(reply.board.eq(board));

        query.groupBy(board); //게시물 당 처리를 하기 위해서 --까지 join기능 // group by  //board로 묶임

        if (( types != null && types.length > 0) && keyword != null) {
            //검색 조건과 키워드가 있는경우

            BooleanBuilder booleanBuilder = new BooleanBuilder(); //조건을 위한 ( 가 시작

            for (String type: types){
                switch (type){
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword)); // title like concat('%',keyword,'%')
                        break;
                    case"c":
                        booleanBuilder.or(board.content.contains(keyword));// content like concat('%',keyword,'%')
                        break;
                    case"w":
                        booleanBuilder.or(board.writer.contains(keyword));// writer like concat('%',keyword,'%')
                        break;
                }
            }   //for end
            query.where(booleanBuilder);  // 조건을 위한 ) 가 생성
        }// end if
        //> dno > 0
        query.where(board.bno.gt(0L));

        // Projections.bean() -> JPQL의 결과를 바로 DTO로 처리하는 기능 제공.
        // Querydsl도 마찬가지로 이런 기능을 제공한다.
        JPQLQuery<BoardListReplyCountDTO> dtoQuery = query.select(Projections. //지금까지 정보를BoardListReplyCountDTO에 넣기 위해
                bean(BoardListReplyCountDTO.class,
                        board.bno,
                        board.title,
                        board.writer,
                        board.regDate,
                        reply.count().as("replyCount") //replyCount 로별칭처리 //응답 객체로 가져온다.  //reply.count()댓글 갯수.
                ));
        this.getQuerydsl().applyPagination(pageable, dtoQuery);
        List<BoardListReplyCountDTO> dtoList = dtoQuery.fetch();

        Long count = dtoQuery.fetchCount();

        return new PageImpl<>(dtoList, pageable, count);
    }
    @Override
    public Page<BoardListAllDTO> searchWithAll(String[] types, String keyword, Pageable pageable) {

        QBoard board = QBoard.board;
        QReply reply = QReply.reply;

        JPQLQuery<Board> boardJPQLQuery = from(board);
        boardJPQLQuery.leftJoin(reply).on(reply.board.eq(board)); //left join

        if( (types != null && types.length > 0) && keyword != null ){

            BooleanBuilder booleanBuilder = new BooleanBuilder(); // (

            for(String type: types){

                switch (type){
                    case "t":
                        booleanBuilder.or(board.title.contains(keyword));
                        break;
                    case "c":
                        booleanBuilder.or(board.content.contains(keyword));
                        break;
                    case "w":
                        booleanBuilder.or(board.writer.contains(keyword));
                        break;
                }
            }//end for
            boardJPQLQuery.where(booleanBuilder);
        }

        boardJPQLQuery.groupBy(board);

        getQuerydsl().applyPagination(pageable, boardJPQLQuery); //paging



        JPQLQuery<Tuple> tupleJPQLQuery = boardJPQLQuery.select(board, reply.countDistinct());

        List<Tuple> tupleList = tupleJPQLQuery.fetch();

        List<BoardListAllDTO> dtoList = tupleList.stream().map(tuple -> {

            Board board1 = (Board) tuple.get(board);
            long replyCount = tuple.get(1,Long.class);

            BoardListAllDTO dto = BoardListAllDTO.builder()
                    .bno(board1.getBno())
                    .title(board1.getTitle())
                    .writer(board1.getWriter())
                    .regDate(board1.getRegDate())
                    .replyCount(replyCount)
                    .build();

            //BoardImage를 BoardImageDTO 처리할 부분
            List<BoardImageDTO> imageDTOS = board1.getImageSet().stream().sorted()
                    .map(boardImage -> BoardImageDTO.builder()
                            .uuid(boardImage.getUuid())
                            .fileName(boardImage.getFileName())
                            .ord(boardImage.getOrd())
                            .build()
                    ).collect(Collectors.toList());

            dto.setBoardImages(imageDTOS); //처리된 BoardImageDTO들을 추가

            return dto;
        }).collect(Collectors.toList());

        long totalCount = boardJPQLQuery.fetchCount();


        return new PageImpl<>(dtoList, pageable, totalCount);
    }

}
