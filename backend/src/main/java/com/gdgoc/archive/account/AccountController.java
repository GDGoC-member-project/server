package com.gdgoc.archive.account;

import com.gdgoc.archive.security.FirebaseAuthFilter;
import com.gdgoc.archive.error.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/me/account")
    public AccountResponse meAccount(HttpServletRequest request) throws Exception {
        String firebaseUid = (String) request.getAttribute(FirebaseAuthFilter.ATTR_FIREBASE_UID);
        String email = (String) request.getAttribute(FirebaseAuthFilter.ATTR_FIREBASE_EMAIL);

        // 방어: 필터가 정상 동작하지 않으면 계정 생성/조회 자체를 막음
        if (firebaseUid == null || firebaseUid.isBlank()) {
            // GlobalExceptionHandler에서 401로 매핑되게 하거나,
            // 당장 최소 구현이면 RuntimeException으로 던져도 됨
            throw new UnauthorizedException();
        }

        UserAuthDoc doc = accountService.getOrCreate(firebaseUid, email);

        // passwordHash는 응답 금지
        return new AccountResponse(doc.userId(), doc.firebaseUid(), doc.email(), doc.role());
    }

    public record AccountResponse(
            String userId,
            String firebaseUid,
            String email,
            String role
    ) {}
}
