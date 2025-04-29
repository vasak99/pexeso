package cz.vse.pexeso.service;

import cz.vse.pexeso.model.ClientSession;
import cz.vse.pexeso.model.UserCredentials;
import cz.vse.pexeso.network.MessageBuilder;
import cz.vse.pexeso.util.FormValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginService {
    private static final Logger log = LoggerFactory.getLogger(LoginService.class);
    private final AppServices appServices = AppServices.getInstance();

    public String login(String username, String password) {
        if (FormValidator.isEmpty(username, password)) {
            return "Please fill in all fields.";
        } else {
            UserCredentials userCredentials = new UserCredentials(username, password);
            sendLoginMessage(userCredentials);
            return "";
        }
    }

    private void sendLoginMessage(UserCredentials userCredentials) {
        String message = MessageBuilder.buildLoginMessage(userCredentials);
        appServices.getConnection().sendMessage(message);
        log.info("Login credentials submitted for verification.");
    }

    public void initializeSession(long playerId, String username, String password) {
        appServices.setClientSession(new ClientSession(playerId, new UserCredentials(username, password)));
        log.info("Login successful.");
    }

}
