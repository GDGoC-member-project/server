package com.gdgoc.archive.account;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AccountService {

    private static final Role DEFAULT_ROLE = Role.MEMBER;
    private final UserAuthRepository repo;

    public AccountService(UserAuthRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public UserAuth getOrCreate(String firebaseUid, String email) {
        return repo.findByFirebaseUid(firebaseUid)
                .map(existing -> {
                    if ((existing.getEmail() == null || existing.getEmail().isBlank())
                            && email != null && !email.isBlank()) {
                        existing.setEmail(email);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    UserAuth created = new UserAuth(
                            UUID.randomUUID().toString(),
                            firebaseUid,
                            email,
                            null,
                            DEFAULT_ROLE
                    );
                    try {
                        return repo.save(created);
                    } catch (DataIntegrityViolationException e) {
                        return repo.findByFirebaseUid(firebaseUid)
                                .orElseThrow(() -> new IllegalStateException("USER_AUTH_RACE_CONDITION", e));
                    }
                });
    }
}
