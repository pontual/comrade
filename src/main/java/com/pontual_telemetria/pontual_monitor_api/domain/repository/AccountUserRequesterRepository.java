package com.pontual_telemetria.pontual_monitor_api.domain.repository;

import com.pontual_telemetria.pontual_monitor_api.domain.model.user.AccountUserRequester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountUserRequesterRepository extends JpaRepository<AccountUserRequester, Long> {
    List<AccountUserRequester> findAllByUser_id(Long id);
}
