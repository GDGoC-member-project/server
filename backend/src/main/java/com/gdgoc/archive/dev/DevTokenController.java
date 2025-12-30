package com.gdgoc.archive.dev;

import com.google.firebase.auth.FirebaseAuth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DevTokenController {

    @GetMapping("/dev/custom-token")
    public String customToken(@RequestParam String uid) throws Exception {
        return FirebaseAuth.getInstance().createCustomToken(uid);
    }
}
