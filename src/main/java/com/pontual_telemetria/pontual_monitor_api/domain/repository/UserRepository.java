package com.pontual_telemetria.pontual_monitor_api.domain.repository;

import com.pontual_telemetria.pontual_monitor_api.domain.model.user.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<AccountUser, Long> {
    AccountUser findByUsername(String username);
    boolean existsByUsername(String username);
    AccountUser findByPerson_Document(String document);
    AccountUser findByPerson_Id(Long id);
}
