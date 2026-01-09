package com.gdgoc.member.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserAuthRepository extends JpaRepository<UserAuth, UUID> {
    Optional<UserAuth> findByExternalUid(String externalUid);
    Optional<UserAuth> findByEmail(String email);
}
