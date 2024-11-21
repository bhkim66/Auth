package com.bhkim.auth.repository;

import com.bhkim.auth.entity.jpa.User;
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
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        //멤버 한명 추가
        User user = User.builder()
                .id("bhkim62")
                .name("김병호")
                .age(30)
                .sex(M)
                .phoneNumber(null)
                .build();
        userRepository.save(user);
    }

    @Test
    @DisplayName("멤버 회원가입 테스트")
    void 멤버_회원가입_DB값_넣기() {
        //given
        User user = User.builder()
                .id("bhkim62")
                .name("김병호")
                .age(30)
                .sex(M)
                .phoneNumber(null)
                .build();
        //when
        User saveUser = userRepository.save(user);

        //then
        assertThat(saveUser).isNotNull();
        assertThat(user.getSeq()).isEqualTo(saveUser.getSeq());
        assertThat(user.getId()).isEqualTo(saveUser.getId());
        assertThat(user.getName()).isEqualTo(saveUser.getName());
        assertThat(user.getAge()).isEqualTo(saveUser.getAge());
        assertThat(user.getSex()).isEqualTo(saveUser.getSex());
        log.info("saveMember createdTime = {}", String.valueOf(saveUser.getCreatedTime()));
    }

    @Test
    @DisplayName("멤버 출력 테스트")
    void 특정_멤버_출력_테스트() {
        //given
        // `beforeAll()`에서 이미 저장한 `member` 객체를 가져오기
        User expectedUser = userRepository.findById("bhkim62").orElseThrow();

        // when
        User actualUser = userRepository.findById(expectedUser.getId()).orElseThrow();

        // then
        assertThat(actualUser).isNotNull();
        assertThat(actualUser).usingRecursiveComparison().isEqualTo(expectedUser);  // 객체의 모든 값을 비교
    }
    //given
    //when
    //then
}