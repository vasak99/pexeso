package cz.vse.pexeso.common.message.payload;

public class LoginPayload implements MessagePayload {
    private String username;
    private String password;

    public LoginPayload(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
