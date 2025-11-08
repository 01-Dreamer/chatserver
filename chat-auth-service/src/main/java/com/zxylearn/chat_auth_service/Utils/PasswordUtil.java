package com.zxylearn.chat_auth_service.Utils;

import org.mindrot.jbcrypt.BCrypt;


public class PasswordUtil {
    public static String encode(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean match(String password, String passwordHash) {
        return BCrypt.checkpw(password, passwordHash);
    }
}
