package com.bhkim.auth.repository;

import com.bhkim.auth.common.TypeEnum;
import com.bhkim.auth.config.TestQueryDslConfig;
import com.bhkim.auth.dto.condition.UserSearchCondition;
import com.bhkim.auth.dto.response.UserResponseDTO;
import com.bhkim.auth.entity.jpa.Address;
import com.bhkim.auth.entity.jpa.Order;
import com.bhkim.auth.entity.jpa.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.awt.print.PageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.bhkim.auth.common.TypeEnum.F;
import static com.bhkim.auth.common.TypeEnum.M;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DataJpaTest
@EnableJpaAuditing
@Import(TestQueryDslConfig.class)
@EnableJpaRepositories
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

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

        //then
        assertThat(saveUser).isNotNull();
        assertThat(user.getSeq()).isEqualTo(saveUser.getSeq());
        assertThat(user.getId()).isEqualTo(saveUser.getId());
        assertThat(user.getName()).isEqualTo(saveUser.getName());
        assertThat(user.getAge()).isEqualTo(saveUser.getAge());
        assertThat(user.getSex()).isEqualTo(saveUser.getSex());
    }

    @Test
    @DisplayName("멤버 인덱스 확인")
    void 멤버_인덱스_확인() {
        //given
        List<User> userList = new ArrayList<>();

        for (int i = 0; i < 10000; i++) {
            User user = User.builder()
                    .id("bhkim" + i)
                    .name("김병호" + i / 500)
                    .password("test1234")
                    .age(30)
                    .sex(M)
                    .phoneNumber(null)
                    .build();

            userList.add(user);
        }
        //when
        userRepository.saveAll(userList);

        //then

        em.createQuery("select u from User u where u.id = :id and u.name like :name")
                .setParameter("id", "bhkim9900")
                .setParameter("name", "김병호")
                .getResultList();
    }

    @Test
    @DisplayName("유저 address 가져오기")
    void 멤버_주소_가져오기() {
        //given
        User user = User.builder()
                .id("bhkim62")
                .name("김병호")
                .password("test1234")
                .age(30)
                .sex(M)
                .phoneNumber(null)
                .build();

        em.persist(user);


        for (int i = 0; i < 10; i++) {
            Address address = Address.builder()
                    .zipcode(12345 + i)
                    .address1("서울시입니다" + i)
                    .user(user)
                    .build();
            em.persist(address);
            Address address1 = em.find(Address.class, address.getSeq());
            System.out.println("address1 = " + address1.getSeq());
            System.out.println("address1 = " + address1.getUser());
        }
        em.flush();
        em.clear();
        //when
//        User getUser = userRepository.findBySeq(1L).orElseThrow();
//        System.out.println("getUser = " + getUser.getAddresses());

        User findUser = userRepository.getUserAddressList(user.getSeq()).orElseThrow();


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
    void 특정유저_주문_가져오기_oneToMany_fetchJoin() {
        //given
        for(int i = 0; i < 5; i++) {
            User user = User.builder()
                    .id("bhkim6" + i)
                    .name("김병호")
                    .password("test1234")
                    .age(30)
                    .sex(M)
                    .phoneNumber(null)
                    .build();
            em.persist(user);
            for(int j = 0; j < 10; j++) {
                Order order = Order.builder()
                        .orderNum("orderNumtest" + i)
                        .user(user)
                        .build();
                em.persist(order);
            }
        }
        em.clear();

        String searchId = "bhkim62";
        UserSearchCondition condition = new UserSearchCondition(0L, null, 0, 0, null);

        //when
        Pageable pageRequest = PageRequest.of(0, 3);
        Page<User> userOrders = userRepository.getUserOrders(condition, pageRequest);

        System.out.println("userOrders1 = " + userOrders.getTotalPages());
        System.out.println("userOrders2 = " + userOrders.getTotalElements());
        System.out.println("userOrders3 = " + userOrders.getSize());
        System.out.println("userOrders4 = " + userOrders.getContent());
        //then
//        for (User userOrder : userOrders.getContent()) {
//            System.out.println("userOrder = " + userOrder);
//        }
    }

//    @Test
////    void 조건으로_멤버_검색() throws Exception {
////        //given
////        for (int i = 0; i < 100; i++) {
////            TypeEnum sex;
////            if (i % 2 == 0) {
////                sex = F;
////            } else {
////                sex = M;
////            }
////            Random random = new Random();
////
////            User user = User.builder()
////                    .id("bhkim" + i)
////                    .name("김병호")
////                    .password("test1234")
////                    .age(random.nextInt(50))
////                    .sex(sex)
////                    .phoneNumber(null)
////                    .build();
////            userRepository.save(user);
////        }
////        em.flush();
////
////        UserSearchCondition userSearchCondition = new UserSearchCondition("bhkim", 0, 46, M);
////        //when
////        List<UserResponseDTO.UserInfo> searchUserList = userRepository.searchUserWithCondition(userSearchCondition);
////
////        //then
////        assertThat(searchUserList).hasSizeGreaterThan(0);
////        System.out.println(searchUserList.size());
////    }
}