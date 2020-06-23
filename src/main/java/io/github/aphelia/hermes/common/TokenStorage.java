package io.github.aphelia.hermes.common;

public class TokenStorage {
    private static String token = null;

    public static String getToken() {
        if(token == null) throw new NullPointerException("The token is null!");
        return token;
    }
    public static void setToken(String token) {
        TokenStorage.token = token;
    }
}
