package com.gdgoc.archive.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, String> {
    Optional<UserAuth> findByFirebaseUid(String firebaseUid);
}