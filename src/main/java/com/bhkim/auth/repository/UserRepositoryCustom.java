package com.bhkim.auth.repository;

import com.bhkim.auth.dto.condition.UserSearchCondition;
import com.bhkim.auth.dto.response.UserResponseDTO;
import com.bhkim.auth.entity.jpa.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepositoryCustom {
    List<UserResponseDTO.UserInfo> searchUserWithCondition(UserSearchCondition condition);

    Page<User> getUserOrders(UserSearchCondition condition, Pageable page);

}
