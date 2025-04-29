package cz.vse.pexeso.service;

import cz.vse.pexeso.model.ClientSession;
import cz.vse.pexeso.model.UserCredentials;
import cz.vse.pexeso.network.MessageBuilder;
import cz.vse.pexeso.util.FormValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegisterService {
    private static final Logger log = LoggerFactory.getLogger(RegisterService.class);
    private final AppServices appServices = AppServices.getInstance();

    public String register(String username, String password, String confirmPassword) {
        if (FormValidator.isEmpty(username, password, confirmPassword)) {
            return "Please fill in all fields.";
        } else if (!FormValidator.passwordMatch(password, confirmPassword)) {
            return "Passwords do not match.";
        } else if (!FormValidator.passwordStrong(password)) {
            return "Password must be at least 8 characters long.";
        } else {
            UserCredentials userCredentials = new UserCredentials(username, password);
            sendRegisterMessage(userCredentials);
            return "";
        }
    }

    private void sendRegisterMessage(UserCredentials userCredentials) {
        String message = MessageBuilder.buildRegisterMessage(userCredentials);
        appServices.getConnection().sendMessage(message);
        log.info("Registration credentials submitted for verification.");
    }

    public void initializeSession(long playerId, String username, String password) {
        appServices.setClientSession(new ClientSession(playerId, new UserCredentials(username, password)));
        log.info("Registration successful.");
    }
}
