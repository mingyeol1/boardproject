package org.zerock.b01.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.Member;
import org.zerock.b01.domain.MemberRole;
import org.zerock.b01.dto.MemberJoinDTO;
import org.zerock.b01.repository.MemberRepository;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{



    private final MemberRepository memberRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;


    @Override
    public void join(MemberJoinDTO memberJoinDTO) throws MidExistException {
        String mid = memberJoinDTO.getMid();

        boolean exist = memberRepository.existsById(mid); //해당 mid가 존재하면 true 없으면 false 중복확인.

        //중복 아이디 확인 중복시 예외 실행
        if(exist){
            throw new MidExistException("아이디가 중복 되었습니다.");
        }

        // modelMapper로 MemberJoinDTO에 있는 값을 Member 클래스로 변환하고 반환한다.
        Member member = modelMapper.map(memberJoinDTO, Member.class);
        member.changePassword(passwordEncoder.encode(memberJoinDTO.getMpw())); //패스워드 변경 인코딩 처리.
        member.addRole(MemberRole.USER); //권한 부여

        //결과 확인  log
        log.info("=========================");
        log.info(member);
        log.info(member.getRoleSet());
        memberRepository.save(member); //DB에 회원 정보 저장.
    }
}
