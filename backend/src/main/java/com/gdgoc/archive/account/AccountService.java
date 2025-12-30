package com.gdgoc.archive.account;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class AccountService {

    private static final String COL = "user_auth";
    private static final String DEFAULT_ROLE = "USER";

    private final Firestore firestore;

    public AccountService(Firestore firestore) {
        this.firestore = firestore;
    }

    public UserAuthDoc getOrCreate(String firebaseUid, String email) throws Exception {
        DocumentReference ref = firestore.collection(COL).document(firebaseUid);

        return firestore.runTransaction(tx -> {
            DocumentSnapshot snap = tx.get(ref).get();

            Timestamp nowTs = Timestamp.now();
            Instant now = Instant.ofEpochSecond(nowTs.getSeconds(), nowTs.getNanos());

            if (!snap.exists()) {
                String userId = UUID.randomUUID().toString();

                Map<String, Object> data = new HashMap<>();
                data.put("userId", userId);
                data.put("firebaseUid", firebaseUid);
                data.put("email", email);
                data.put("role", DEFAULT_ROLE);
                data.put("passwordHash", null);
                data.put("createdAt", nowTs);
                data.put("updatedAt", nowTs);

                tx.create(ref, data);

                // ✅ 생성 직후 반환값에도 email 반영
                return new UserAuthDoc(
                        userId, firebaseUid, email, DEFAULT_ROLE, null, now, now
                );
            }

            String userId = snap.getString("userId");
            if (userId == null || userId.isBlank()) {
                // 데이터 오염(또는 수동 생성) 조기 발견용
                throw new IllegalStateException("USER_AUTH_CORRUPTED: userId is missing");
            }

            String savedEmail = snap.getString("email");
            if ((savedEmail == null || savedEmail.isBlank()) && email != null && !email.isBlank()) {
                tx.update(ref, Map.of(
                        "email", email,
                        "updatedAt", nowTs
                ));
                savedEmail = email;
            }

            String role = snap.getString("role");
            if (role == null || role.isBlank()) {
                role = DEFAULT_ROLE;
            }

            String passwordHash = snap.getString("passwordHash");

            Timestamp createdAtTs = snap.getTimestamp("createdAt");
            Timestamp updatedAtTs = snap.getTimestamp("updatedAt");

            Instant createdAt = createdAtTs != null
                    ? Instant.ofEpochSecond(createdAtTs.getSeconds(), createdAtTs.getNanos())
                    : null;

            Instant updatedAt = updatedAtTs != null
                    ? Instant.ofEpochSecond(updatedAtTs.getSeconds(), updatedAtTs.getNanos())
                    : null;

            return new UserAuthDoc(userId, firebaseUid, savedEmail, role, passwordHash, createdAt, updatedAt);
        }).get();
    }
}
