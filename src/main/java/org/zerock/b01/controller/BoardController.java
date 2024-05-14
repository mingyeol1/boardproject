package org.zerock.b01.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.b01.dto.BoardDTO;
import org.zerock.b01.dto.BoardListReplyCountDTO;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.service.BoardService;

@Controller
@Log4j2
@RequiredArgsConstructor//생성자주입
@RequestMapping("/board") // /board를 써야만 들어 올 수 있음
public class BoardController {

    private final BoardService boardService; //싱글톤

    @GetMapping("/list")
    public void setLog(PageRequestDTO pageRequestDTO, Model model) {
//        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);
        PageResponseDTO<BoardListReplyCountDTO> responseDTO = boardService.listWithReplyCount(pageRequestDTO);
        log.info(responseDTO);

        model.addAttribute("responseDTO", responseDTO);
    }

    @PreAuthorize("hasRole('USER')")//사전인가. //인가처리 //ROLE_USER와 같은 의미로 특정 권한 사용자만 권한 접근 가능하도록 설정
    @GetMapping("/register")
    public void registerGet(){

    }
    @PostMapping("/register") //등록 입력.
    public String registerPost(@Valid BoardDTO boardDTO, //값이 들어가 있는지 검증.
                               BindingResult bindingResult, //오류 출력
                                RedirectAttributes redirectAttributes){

        log.info("board Post register........");
        if(bindingResult.hasErrors()){ //에러가 발생시 출력
            log.info("has errors");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/board/register"; //에러가 발생하면 이쪽으로 리턴.
        }

        // 등록작업
        log.info(boardDTO);

        Long bno = boardService.register(boardDTO);
        redirectAttributes.addFlashAttribute("result", bno); //화면에만 보이고

        return "redirect:/board/list";
    }

    @PreAuthorize("isAuthenticated()") //인증된 사용자만 접근 가능.
    @GetMapping({"/read", "/modify"})//조회기능
    public void read(Long bno, PageRequestDTO pageRequestDTO, Model model){
        BoardDTO boardDTO = boardService.readOne(bno);
        log.info(boardDTO);
        model.addAttribute("dto",boardDTO);
    }

    @PreAuthorize("principal.username == #boardDTO.writer")  //수정 처리는 게시물 작성자와 로그인한 사용자가 같은 경우
    @PostMapping("/modify")
    public String modify(PageRequestDTO pageRequestDTO,
                         @Valid BoardDTO boardDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes){
        log.info("board Modify post......"+ boardDTO);
        if(bindingResult.hasErrors()){
            log.info("has errors"); //오류시에 나오는 문구 출력

            String link = pageRequestDTO.getLink();
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addAttribute("bno", boardDTO.getBno());
            return "redirect:/board/modify?"+link;
        }

        boardService.modify(boardDTO);
        redirectAttributes.addFlashAttribute("result", "modified");
        redirectAttributes.addAttribute("bno", boardDTO.getBno()); //주소 뒤에 값이 붙음.
        return "redirect:/board/read";

    }

    @PreAuthorize("principal.username == #boardDTO.writer")//사전인가. //인가처리 //ROLE_USER와 같은 의미로 특정 권한 사용자만 권한 접근 가능하도록 설정
    @PostMapping("/remove")
    public String remove(BoardDTO boardDTO, RedirectAttributes redirectAttributes){
        log.info("REMOVE----------------------------------------------");
        log.info("bno : "+ boardDTO.getBno());


        boardService.remove(boardDTO.getBno());
        redirectAttributes.addFlashAttribute("result","removed");

        return "redirect:/board/list";
    }


}
