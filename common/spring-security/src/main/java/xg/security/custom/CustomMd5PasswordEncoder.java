package xg.security.custom;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import xg.common.utils.MD5;

/**
 * @author XG
 * @create 2023-04-09 15:38
 */

@Component
public class CustomMd5PasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        return MD5.encrypt(rawPassword.toString());
    }
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(MD5.encrypt(rawPassword.toString()));
    }
}
