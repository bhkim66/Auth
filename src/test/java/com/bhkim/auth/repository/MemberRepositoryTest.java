package com.bhkim.auth.repository;

import com.bhkim.auth.entity.jpa.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static com.bhkim.auth.common.TypeEnum.M;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void beforeEach() {
        //멤버 한명 추가
        Member member = Member.builder()
                .id("bhkim62")
                .name("김병호")
                .age(30)
                .sex(M)
                .phoneNumber(null)
                .build();
        memberRepository.save(member);
    }

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
        assertThat(saveMember).isNotNull();
        assertThat(member.getId()).isEqualTo(saveMember.getId());
        assertThat(member.getName()).isEqualTo(saveMember.getName());
        assertThat(member.getAge()).isEqualTo(saveMember.getAge());
        assertThat(member.getSex()).isEqualTo(saveMember.getSex());
        log.info("saveMember createdTime = {}", String.valueOf(saveMember.getCreatedTime()));
    }

    @Test
    @DisplayName("멤버 출력 테스트")
    void 특정_멤버_출력_테스트() {
        //given
        // `beforeAll()`에서 이미 저장한 `member` 객체를 가져오기
        Member expectedMember = memberRepository.findById("bhkim62").orElseThrow();

        // when
        Member actualMember = memberRepository.findById(expectedMember.getId()).orElseThrow();

        // then
        assertThat(actualMember).isNotNull();
        assertThat(actualMember).usingRecursiveComparison().isEqualTo(expectedMember);  // 객체의 모든 값을 비교
    }
    //given
    //when
    //then
}