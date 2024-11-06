package com.bhkim.auth.repository;

import com.bhkim.auth.entity.jpa.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static com.bhkim.auth.common.TypeEnum.M;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("멤버 회원가입 테스트")
    void 멤버_회원가입() {
        //given
        Member member = Member.builder()
                .id("bhkim62")
                .name("김병호")
                .age(30)
                .sex(M)
                .phoneNumber(null)
                .build();
        //when
        Member saveMember = memberRepository.save(member);

        //then
        assertNotNull(saveMember);
        assertEquals(member.getId(), saveMember.getId());
        assertEquals(member.getName(), saveMember.getName());
        assertEquals(member.getAge(), saveMember.getAge());
        assertEquals(member.getSex(), saveMember.getSex());
    }
}