package com.studyjun.lottoweb.repository;

import com.studyjun.lottoweb.entity.UserLotto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserLottoRepository extends JpaRepository<UserLotto, Long> {
    Optional<UserLotto> findByUserEmail(String userEmail);
}