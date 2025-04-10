package cz.vse.pexeso.common.message;

public interface MessageTranslator {

    String messageToString(Message message);

    Message stringToMessage(String messageString);
}
