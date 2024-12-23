package com.bhkim.auth.repository;

import com.bhkim.auth.common.TypeEnum;
import com.bhkim.auth.config.TestQueryDslConfig;
import com.bhkim.auth.dto.condition.UserSearchCondition;
import com.bhkim.auth.dto.response.UserResponseDTO;
import com.bhkim.auth.entity.jpa.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;
import java.util.Random;

import static com.bhkim.auth.common.TypeEnum.F;
import static com.bhkim.auth.common.TypeEnum.M;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
@EnableJpaAuditing
@Import(TestQueryDslConfig.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    EntityManager em;

//    @BeforeEach
//    void beforeEach() {
//        //멤버 한명 추가
//        User user = User.builder()
//                .id("bhkim62")
//                .name("김병호")
//                .age(30)
//                .sex(M)
//                .phoneNumber(null)
//                .build();
//        userRepository.save(user);
//    }

    @Test
    @DisplayName("멤버 회원가입 테스트")
    void 멤버_회원가입_DB값_넣기() {
        //given
        User user = User.builder()
                .id("bhkim62")
                .name("김병호")
                .password("test1234")
                .age(30)
                .sex(M)
                .phoneNumber(null)
                .build();
        //when
        User saveUser = userRepository.save(user);
        em.flush();

        //then
        assertThat(saveUser).isNotNull();
        assertThat(user.getSeq()).isEqualTo(saveUser.getSeq());
        assertThat(user.getId()).isEqualTo(saveUser.getId());
        assertThat(user.getName()).isEqualTo(saveUser.getName());
        assertThat(user.getAge()).isEqualTo(saveUser.getAge());
        assertThat(user.getSex()).isEqualTo(saveUser.getSex());
    }

    @Test
    @DisplayName("멤버 출력 테스트")
    void 특정_멤버_출력_테스트() {
        //given
        User user = User.builder()
                .id("bhkim62")
                .name("김병호")
                .password("test1234")
                .age(30)
                .sex(M)
                .phoneNumber(null)
                .build();
        //when
        User saveUser = userRepository.save(user);

        // when
        User actualUser = userRepository.findById(saveUser.getId()).orElseThrow();

        // then
        assertThat(actualUser).extracting("id").isEqualTo(user.getId());
        assertThat(actualUser).extracting("name").isEqualTo(user.getName());
        assertThat(actualUser).extracting("age").isEqualTo(user.getAge());
    }

    @Test
    void 조건으로_멤버_검색() throws Exception {
        //given
        for(int i = 0; i < 100; i++) {
            TypeEnum sex;
            if(i % 2 == 0) {
                sex = F;
            } else {
                sex = M;
            }
            Random random = new Random();

            User user = User.builder()
                    .id("bhkim" + i)
                    .name("김병호")
                    .password("test1234")
                    .age(random.nextInt(50))
                    .sex(sex)
                    .phoneNumber(null)
                    .build();
            userRepository.save(user);
        }
        em.flush();

        UserSearchCondition userSearchCondition = new UserSearchCondition("bhkim", 0, 46, M);
        //when
        List<UserResponseDTO.UserInfo> searchUserList = userRepository.searchUserWithCondition(userSearchCondition);

        //then
        assertThat(searchUserList).hasSizeGreaterThan(0);
        System.out.println(searchUserList.size());
    }
}