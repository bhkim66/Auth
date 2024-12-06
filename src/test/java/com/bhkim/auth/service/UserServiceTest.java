package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.request.UserRequestDTO;
import com.bhkim.auth.dto.response.UserResponseDTO;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.exception.ApiException;
import com.bhkim.auth.record.SignUpRequest;
import com.bhkim.auth.record.UpdateUser;
import com.bhkim.auth.repository.UserRepository;
import com.bhkim.auth.service.impl.UserServiceImpl;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.bhkim.auth.common.TypeEnum.M;
import static com.bhkim.auth.common.TypeEnum.PENDING;
import static com.bhkim.auth.exception.ExceptionEnum.DUPLICATION_VALUE_IN_DATABASE_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    UserServiceImpl userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager em;


    @Test
    void 특정_멤버_조회() {
        Long userSeq = 1L;
        SignUpRequest signup = new SignUpRequest("bhkim62", "test1234", "김병호", 30, M, "01029292020");
        UserRequestDTO.Signup signupDTO = UserRequestDTO.Signup.builder()
                .id(signup.id())
                .password(signup.password())
                .name(signup.name())
                .age(signup.age())
                .sex(signup.sex())
                .phoneNumber(signup.phoneNumber())
                .build();
        User userEntity = signupDTO.toUserEntity();
        em.persist(userEntity);
        em.flush();
        // when
        UserResponseDTO.UserInfo memberInfo = userService.getMemberInfo(userSeq);

        // then
        // 반환된 결과가 성공 인지를 확인
        assertThat(memberInfo).extracting("id").isEqualTo(signup.id());
    }

    @Test
    void 회원가입() {
        //given
        Long userSeq = 1L;
        SignUpRequest signup = new SignUpRequest("bhkim62", "test1234", "김병호", 30, M, "01029292020");
        UserRequestDTO.Signup signupDTO = UserRequestDTO.Signup.builder()
                .id(signup.id())
                .password(signup.password())
                .name(signup.name())
                .age(signup.age())
                .sex(signup.sex())
                .phoneNumber(signup.phoneNumber())
                .build();


        // stubbing: memberRepository.save()가 호출되면 member 객체를 반환하도록 설정
//        given(userRepository.save(any())).willReturn(signup);

        // when
        ResponseEntity<Void> result = userService.signUp(signupDTO);
        em.flush();
        UserResponseDTO.UserInfo memberInfo = userService.getMemberInfo(userSeq);

        // then
        // 반환된 결과가 성공 인지를 확인
        assertThat(memberInfo).extracting("id").isEqualTo(signup.id());
    }

    @Test
    void 회원가입_중복id_값_전달_실패() {
        // given
        SignUpRequest signup = new SignUpRequest("bhkim62", "test1234", "김병호", 30, M, "01029292020");
        UserRequestDTO.Signup signupDTO = UserRequestDTO.Signup.builder()
                .id(signup.id())
                .password(signup.password())
                .name(signup.name())
                .age(signup.age())
                .sex(signup.sex())
                .phoneNumber(signup.phoneNumber())
                .build();
        User existUser = User.builder()
                .id("bhkim62")
                .password("test1234")
                .name("박병호")
                .age(35)
                .sex(M)
                .build();
        userRepository.save(existUser);
        // stubbing: memberRepository.save()가 호출되면 ApiException을 던지도록 설정
//        given(userRepository.save(any())).willThrow(new ApiException(DATABASE_INSERT_ERROR));

        // when
        // then
        assertThatThrownBy(() -> userService.signUp(signupDTO)).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void 회원가입_중복_ID_아닐경우() {
        // given
        String newId = "bhkim63";
        User existUser = User.builder()
                .id("bhkim62")
                .password("test1234")
                .name("박병호")
                .age(35)
                .sex(M)
                .build();
        userRepository.save(existUser);
        // stubbing: memberRepository.save()가 호출되면 ApiException을 던지도록 설정
//        given(userRepository.save(any())).willThrow(new ApiException(DATABASE_INSERT_ERROR));

        // when
        ResponseEntity<Boolean> result = userService.checkDuplicateId(newId);
        // then
        assertThat(result.getBody()).isTrue();
    }

    @Test
    void 회원가입_중복_ID() {
        // given
        String newId = "bhkim62";
        User existUser = User.builder()
                .id("bhkim62")
                .password("test1234")
                .name("박병호")
                .age(35)
                .sex(M)
                .build();
        userRepository.save(existUser);
        // stubbing: memberRepository.save()가 호출되면 ApiException을 던지도록 설정
//        given(userRepository.save(any())).willThrow(new ApiException(DATABASE_INSERT_ERROR));

        // when
        // then
        assertThatThrownBy(() -> userService.checkDuplicateId(newId)).isInstanceOf(ApiException.class).hasMessage(DUPLICATION_VALUE_IN_DATABASE_ERROR.getErrorMessage());
    }

    @Test
    void 멤버_정보_수정_성공() {
        //given
        User existUser = User.builder()
                .id("bhkim62")
                .password("test1234")
                .name("박병호")
                .age(35)
                .sex(M)
                .build();
//        userRepository.save(existUser);
        em.persist(existUser);
        em.flush();

        Long userSeq = 1L;
        UpdateUser updateUser = new UpdateUser("김병호", 30, M, "01029292020");
        UserRequestDTO.UpdateUserInfo updateUserInfo = UserRequestDTO.UpdateUserInfo.builder()
                .name(updateUser.name())
                .age(updateUser.age())
                .sex(updateUser.sex())
                .phoneNumber(updateUser.phoneNumber())
                .build();

        //when
        ResponseEntity<Boolean> result = userService.updateUser(updateUserInfo, userSeq);

        em.flush();
        //then
        assertThat(result.getBody()).isTrue();
    }

    @Test
    void 멤버_비밀번호_수정_성공() {
        //given
        User existUser = User.builder()
                .id("bhkim62")
                .password("test1234")
                .name("박병호")
                .age(35)
                .sex(M)
                .build();
//        userRepository.save(existUser);
        em.persist(existUser);
        em.flush();

        Long userSeq = 1L;
        UserRequestDTO.UpdatePassword requestUserInfo = UserRequestDTO.UpdatePassword.builder()
                .password("qwer1234") // 비밀번호 암호화
                .build();

        //when
        ResponseEntity<Boolean> result = userService.changePassword(requestUserInfo, userSeq);

        em.flush();
        //then
        assertThat(result.getBody()).isTrue();
    }

    @Test
    void 멤버_비밀번호_이전과_동일_값_오류() {
        //given
        User existUser = User.builder()
                .id("bhkim62")
                .password("$2a$10$WW2wZvQZAhx1WknnRPXP2OgD6Dm8f3Pt8Mp22COI7.R.f2bSJO566")
                .name("박병호")
                .age(35)
                .sex(M)
                .build();
//        userRepository.save(existUser);
        em.persist(existUser);
        em.flush();

        Long userSeq = 1L;
        UserRequestDTO.UpdatePassword requestUserInfo = UserRequestDTO.UpdatePassword.builder()
                .password("test1234") // 비밀번호 암호화
                .build();
        //when
        assertThatThrownBy(() -> userService.changePassword(requestUserInfo, userSeq)).isInstanceOf(ApiException.class).hasMessage("이전 비밀번호와 다른 비밀번호를 입력해주세요");
    }
}