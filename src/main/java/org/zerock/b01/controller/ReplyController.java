package org.zerock.b01.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ReplyDTO;
import org.zerock.b01.service.ReplyService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/replies")
@Log4j2
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService; // 의존성 주입..

    @Operation(summary = "Replies Post - Post방식으로 댓글 등록") // 댓글등록 // 브라우저 창에 뜨는 글씨..?
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Long> register(
            @Valid @RequestBody ReplyDTO replyDTO,
            BindingResult bindingResult) throws BindException {
        log.info(replyDTO);

        if(bindingResult.hasErrors()) {
            throw new BindException(bindingResult);//에러를 받아서 해당값을 받아 에러를 처리
        } //값 검증이 맞으면. //레스트 방식으로 처리를 하면 에러를 발생 시켜줘야 한다.

        Map<String, Long> resultMap = new HashMap<>();
        Long rno = replyService.register(replyDTO);

        resultMap.put("rno", rno);
        return resultMap; // 값을 반환해준다. 111L
    }

    //특정 게시물의 댓글 목록 보기.
    // URI - '/replies/list/{bno}' , 방식은 -> GET방식으로(정보요청)           , //Post(정보입력) //PUT(정보수정)
    // @PathVariable 어노테이션 사용!
    @Operation(summary = "Replies of Board로 GET방식으로 특정 게시물 댓글 목록 처리") //설명입력
    @GetMapping(value = "/list/{bno}")
    public PageResponseDTO<ReplyDTO> getList( @PathVariable("bno") Long bno, // 경로중에 bno를 변수로 사용한다.
                                              PageRequestDTO pageRequestDTO) {

        PageResponseDTO<ReplyDTO> responseDTO = replyService.getListOfBoard(bno,pageRequestDTO);
        return responseDTO;
    }

    @Operation(summary = "Read Reply - GET방식으로 댓글 조회")
    @GetMapping("/{rno}")
    public ReplyDTO getReplyDTO(@PathVariable("rno") Long rno) {
        ReplyDTO replyDTO = replyService.read(rno);
        return replyDTO;
    }

    @Operation(summary = "Delete Reply - DELETE 메서드를 이용한 댓글 삭제")
    @DeleteMapping("/{rno}")
    public Map<String, Long> remove(@PathVariable("rno") Long rno){
        replyService.remove(rno);

        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("rno",rno);

        return resultMap;
    }

    @Operation(summary = "Modify Reply - PUT 방식으로 댓글 수정") //PUT : 내용을 수정 할 때 사용
    @PutMapping(value = "/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE) //제이슨 방식으로 들어온 값 처리
    public Map<String, Long> modify(@PathVariable("rno")Long rno,
                                    @RequestBody ReplyDTO replyDTO){
        replyDTO.setRno(rno); // 번호 일치 시키려고 넣는다.
        replyService.modify(replyDTO); //아이디 값과 매칭시키고 변환시킨다.
        Map<String, Long> resultMap = new HashMap<>();
        resultMap.put("rno",rno);

        return resultMap;

    }




}


// swagger 에서 확인 가능
