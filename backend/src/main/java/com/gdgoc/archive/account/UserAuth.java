package com.gdgoc.archive.account;

import jakarta.persistence.*;

@Entity
@Table(
        name = "user_auth",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_auth_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_user_auth_firebase_uid", columnNames = "firebase_uid")
        }
)
public class UserAuth {

    @Id
    @Column(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    private String userId;

    @Column(name = "firebase_uid", length = 128, nullable = false)
    private String firebaseUid;

    @Column(name = "email", length = 255) // nullable 허용 권장
    private String email;

    @Column(name = "password_hash", length = 255) // nullable 허용 권장
    private String passwordHash;

    @Column(name = "role", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    protected UserAuth() { }

    public UserAuth(String userId, String firebaseUid, String email, String passwordHash, Role role) {
        this.userId = userId;
        this.firebaseUid = firebaseUid;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }



    public String getUserId() { return userId; }
    public String getFirebaseUid() { return firebaseUid; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }

    public void setEmail(String email) { this.email = email; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setRole(Role role) { this.role = role; }
}
