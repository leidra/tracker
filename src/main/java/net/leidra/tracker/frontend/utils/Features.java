package net.leidra.tracker.frontend.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import java.util.Base64;

/**
 * Created by afuentes on 29/01/2018.
 */
public enum Features {
    REQUEST_LOCATION;

    private String value;

    Features() {
        this.value = new String(Base64.getEncoder().encode(name().getBytes()));
    }

    public String encode() {
        return value;
    }
}