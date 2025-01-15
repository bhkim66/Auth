package com.bhkim.auth.repository;

import com.bhkim.auth.entity.jpa.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
