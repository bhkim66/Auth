package com.bhkim.auth.service;

import com.bhkim.auth.common.ApiResponseResult;
import com.bhkim.auth.dto.UserRequestDTO;
import com.bhkim.auth.entity.jpa.User;
import com.bhkim.auth.exception.ApiException;
import com.bhkim.auth.record.SignUpRequest;
import com.bhkim.auth.record.UpdateUser;
import com.bhkim.auth.repository.UserRepository;
import com.bhkim.auth.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;

import java.util.UUID;

import static com.bhkim.auth.common.TypeEnum.M;
import static com.bhkim.auth.common.TypeEnum.PENDING;
import static com.bhkim.auth.exception.ExceptionEnum.DATABASE_INSERT_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@Slf4j
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;


    @Test
    void 특정_멤버_조회() {
        System.out.println("uuid : "+ UUID.randomUUID().toString());
    }

    @Test
    @Rollback(false)
    void 회원가입() {
        //given
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
        ApiResponseResult<HttpStatus> result = userService.signUp(signupDTO);

        // then
        // 반환된 결과가 성공 인지를 확인
        assertThat(true).isEqualTo(result.isSuccess());
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

        // stubbing: memberRepository.save()가 호출되면 ApiException을 던지도록 설정
        given(userRepository.save(any())).willThrow(new ApiException(DATABASE_INSERT_ERROR));

        // when
        // then
        assertThatThrownBy(() -> userService.signUp(signupDTO)).isInstanceOf(ApiException.class).hasMessage("데이터 INSERT ERROR 발생");
    }

    @Test
    void 회원가입_인증메일_인증_성공() {
        // given
        SignUpRequest signup = new SignUpRequest("bhkim62", "test1234", "김병호", 30, M, "01029292020");
        User user = User.builder()
                .id(signup.id())
                .name(signup.name())
                .age(signup.age())
                .sex(signup.sex())
                .phoneNumber(signup.phoneNumber())
                .status(PENDING)
                .accessCode("1234asdf-basdfwqwer-asdf325w1b")
                .build();

        // stubbing: memberRepository.save()가 호출되면 ApiException을 던지도록 설정
        given(userRepository.save(any())).willReturn(user);

        // when
        ApiResponseResult<HttpStatus> result = userService.authenticateMail("1234asdf-basdfwqwer-asdf325w1b");
        // then
        assertThat(result.isSuccess()).isTrue();


    }

    @Test
    void 회원가입_인증메일_인증_실패() {
        // given
        SignUpRequest signup = new SignUpRequest("bhkim62", "test1234", "김병호", 30, M, "01029292020");
        User user = User.builder()
                .id(signup.id())
                .name(signup.name())
                .age(signup.age())
                .sex(signup.sex())
                .phoneNumber(signup.phoneNumber())
                .status(PENDING)
                .accessCode("1234asdf-basdfwqwer-asdf325w1b")
                .build();

        // stubbing: memberRepository.save()가 호출되면 ApiException을 던지도록 설정
        given(userRepository.save(any())).willReturn(user);

        // when
        // then
        assertThatThrownBy(() -> userService.authenticateMail("1234asdf-basdfwqwer-wrongcode")).isInstanceOf(ApiException.class).hasMessage("잘못된 인증 코드 입니다");
    }

    @Test
    void 멤버_정보_수정_성공() {
        //given
        UpdateUser updateUser = new UpdateUser("김병호", 30, M, "01029292020");
        UserRequestDTO.UpdateMemberInfo updateMemberInfo = UserRequestDTO.UpdateMemberInfo.builder()
                .name(updateUser.name())
                .age(updateUser.age())
                .sex(updateUser.sex())
                .phoneNumber(updateUser.phoneNumber())
                .build();

        //when
        ApiResponseResult<HttpStatus> result = userService.updateUser(updateMemberInfo);

        //then
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void 멤버_정보_수정_실패() {
        //given
        UpdateUser updateUser = new UpdateUser("김병호", 30, M, "01029292020");
        UserRequestDTO.UpdateMemberInfo updateMemberInfo = UserRequestDTO.UpdateMemberInfo.builder()
                .name(updateUser.name())
                .age(updateUser.age())
                .sex(updateUser.sex())
                .phoneNumber(updateUser.phoneNumber())
                .build();

        //when

        //then
//        assertThatThrownBy(() -> memberService.updateUser(updateMemberInfo)).

    }


}