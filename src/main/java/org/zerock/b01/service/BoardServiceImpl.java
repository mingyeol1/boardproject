package org.zerock.b01.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Board;
import org.zerock.b01.domain.BoardLike;
import org.zerock.b01.domain.Member;
import org.zerock.b01.dto.*;
import org.zerock.b01.repository.BoardLikeRepository;
import org.zerock.b01.repository.BoardRepository;
import org.zerock.b01.repository.MemberRepository;
import org.zerock.b01.repository.ReplyRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service //서비스를 등록해야 빈에 등록함
@Log4j2 // 로그 확인용
@RequiredArgsConstructor //생성자 주입
@Transactional  // Transaction 은 DB에 여러 작업을 해야하는 경우, 완정 성공시 처리. 실패시 되돌리기가 된다.
public class BoardServiceImpl implements BoardService {

    private final ModelMapper modelMapper; //생성자 주입.

    private final BoardRepository boardRepository; //생성자 주입.

    private final BoardLikeRepository boardLikeRepository;
    private final MemberRepository memberRepository;



    @Override //구현
    public Long register(BoardDTO boardDTO) {

        Board board = dtoToEntity(boardDTO);

        Long bno = boardRepository.save(board).getBno();

        return bno;
    }

    @Override
    public BoardDTO readOne(Long bno) {

        Optional<Board> result =  boardRepository.findByIdWithImages(bno);

        Board board = result.orElseThrow();

        BoardDTO boardDTO = entityToDTO(board); //boardDTO > board



        return boardDTO;
    }

    @Override
    public void modify(BoardDTO boardDTO) {
       Optional<Board> result = boardRepository.findById(boardDTO.getBno()); //값을 받아오는 과정 boardDTO.getBno()있는 값을 받아온다.

       Board board = result.orElseThrow(); //만약에 불러오지 못하면 없는애를 수정하는거라 예외 발생. 노 서치 엘리멘트 익센션 오류가 뜸.

       board.change(boardDTO.getTitle(), boardDTO.getContent()); // boardDTO에 있는 title과 content를 바꾸겠다(수정하겠다.) 동작을 하면 퍼시턴트가 바뀜.(객체정보.) 퍼스턴트 컨텍스트에 작업을 하는중.



        //첨부파일 처리
        board.clearImages();

        if(boardDTO.getFileNames() != null){
            for (String fileName : boardDTO.getFileNames()) {
                String[] arr = fileName.split("_");
                board.addImage(arr[0], arr[1]);
            }
        }

        boardRepository.save(board); //퍼시던트 컨텍스트 안에 저장을 하겠다.
    }

    @Override
    public void remove(Long bno) {




       boardRepository.deleteById(bno);
    }

    @Override
    public PageResponseDTO<BoardDTO> list(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getType();
        String keyword = pageRequestDTO.getKeyword();
       Pageable pageable = pageRequestDTO.getPageable("bno");  //게시글 번호 기준으로 작업을 한다 정렬값.
        Page<Board> result = boardRepository.searchAll(types, keyword, pageable);
      //  result.getContent().forEach(i ->log.info("Service에서 searchAll 테스트 : "+i));
       //변환 ... Board -> BoardDTO
        List<BoardDTO> dtoList = result.getContent().stream()
                .map(board -> modelMapper.map(board, BoardDTO.class))
                .collect(Collectors.toList());
        return PageResponseDTO.<BoardDTO>withAll() //빌더 메서드 이름(withAll). 생성자가 실행됨.
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<BoardListReplyCountDTO> listWithReplyCount(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getType();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");  //게시글 번호 기준으로 작업을 한다 정렬값.
        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(
                types,
                keyword,
                pageable);

        return PageResponseDTO.<BoardListReplyCountDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    @Override
    public PageResponseDTO<BoardListAllDTO> listWithAll(PageRequestDTO pageRequestDTO) {
        String[] types = pageRequestDTO.getType();
        String keyword = pageRequestDTO.getKeyword();
        Pageable pageable = pageRequestDTO.getPageable("bno");

        Page<BoardListAllDTO> result = boardRepository.searchWithAll(types, keyword, pageable);

        return PageResponseDTO.<BoardListAllDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(result.getContent())
                .total((int)result.getTotalElements())
                .build();
    }

    public void likeBoard(BoardLikeDTO boardLikeDTO) {
        Board board = boardRepository.findById(boardLikeDTO.getBoard_bno()).orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));
        Member member = memberRepository.findById(boardLikeDTO.getMember_mid()).orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        Optional<BoardLike> existingLike = boardLikeRepository.findByBoardAndMember(board, member);
        if (existingLike.isPresent()) {
            boardLikeRepository.delete(existingLike.get());
        } else {
            BoardLike boardLike = BoardLike.builder().board(board).member(member).build();
            boardLikeRepository.save(boardLike);
        }
    }

    public int countLikes(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new IllegalArgumentException("Invalid board ID"));
        return boardLikeRepository.countByBoard(board);
    }
}
