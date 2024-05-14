package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Reply;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ReplyDTO;
import org.zerock.b01.repository.ReplyRepository;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor //의존성 주입
public class ReplyServiceImpl implements ReplyService{

    private final ReplyRepository replyRepository; //의존성 주입

    private final ModelMapper modelMapper; //의존성 주입

    @Override
    public Long register(ReplyDTO replyDTO) {
        Reply reply = modelMapper.map(replyDTO, Reply.class); //DTO를 Reply로 변환

        reply.setBoard(replyDTO.getBno());

        Long rno = replyRepository.save(reply).getRno();

        return rno;  //return replyRepository.save(reply).getRno();도 가능
    }

    @Override
    public ReplyDTO read(Long rno) {
     Optional<Reply> result = replyRepository.findById(rno);

     Reply reply = result.orElseThrow();

     ReplyDTO replyDTO = modelMapper.map(reply, ReplyDTO.class);
        log.info("read ReplyDTO"+replyDTO);
        replyDTO.setBno(reply.getBoard().getBno());
        return replyDTO;
    }

    @Override
    public void modify(ReplyDTO replyDTO) {
      Optional<Reply> replyOptional = replyRepository.findById(replyDTO.getRno());

      Reply reply = replyOptional.orElseThrow(); // Optional에 값이 없을 때 .orElseThrow() 에서 내가 지정한 예외가 발생한다.

      reply.changeText(replyDTO.getReplyText()); //댓글의 내용만 수정.

      replyRepository.save(reply);
    }

    @Override
    public void remove(Long rno) {
        replyRepository.deleteById(rno);
    }

    @Override
    public PageResponseDTO<ReplyDTO> getListOfBoard(Long bno, PageRequestDTO pageRequestDTO) {
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPage() <= 0 ? 0: pageRequestDTO.getPage() - 1,
                pageRequestDTO.getSize(),
                Sort.by("rno").ascending());  // 처음 댓글을 위로 올리기 위해서...
        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);

        List<ReplyDTO> dtoList = result.getContent().stream().map(reply -> {
            ReplyDTO replyDTO = modelMapper.map(reply, ReplyDTO.class);
            replyDTO.setBno(reply.getBoard().getBno());
            return replyDTO;
        }).collect(Collectors.toList());

        return PageResponseDTO.<ReplyDTO>withAll()
                .pageRequestDTO(pageRequestDTO)
                .dtoList(dtoList)
                .total((int)result.getTotalElements())
                .build();
    }
}
