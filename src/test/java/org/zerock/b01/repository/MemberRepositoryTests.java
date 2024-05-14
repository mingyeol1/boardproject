package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.zerock.b01.domain.Member;
import org.zerock.b01.domain.MemberRole;

import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void insertMember() {
        IntStream.rangeClosed(1,100).forEach(i ->{
            Member member = Member.builder()
                    .mid("Member"+i)
                    .mpw(passwordEncoder.encode("1111"))
                    .email("email"+i+"@email.com")
                    .build();
            member.addRole(MemberRole.USER); //권한 설정
            if (i >= 90){
                member.addRole(MemberRole.ADMIN);
            }
            memberRepository.save(member);
        });
    }

    @Test
    public void testRead(){
        Optional<Member> result = memberRepository.getWithRoles("member100");
        Member member = result.orElseThrow();

        log.info(member);
        log.info(member.getRoleSet());
        member.getRoleSet().forEach(memberRole -> log.info(memberRole.name()));
    }

    @Test
    public void testDelete(){
        memberRepository.deleteById("ingye,ingye@email.com");
    }

    @Test
    public void testFindByEmail(){
        Optional<Member> result = memberRepository.findByEmail("email100@email.com");
        Member member = result.orElseThrow();

        log.info(member);
    }

    @Commit
    @Test
    public void testUpdate(){


        String mid = "rufals741@naver.com";
        String mpw = passwordEncoder.encode("1234");
        memberRepository.updatePassword(mpw, mid);
    }
}
