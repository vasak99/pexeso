package cz.vse.pexeso.common.message;

public class MessageTranslatorImpl implements MessageTranslator {

    private static final String START = "MESSAGE_START;";
    private static final String END = "MESSAGE_END;";

    @Override
    public String messageToString(Message message) {
        return "";
    }

    @Override
    public Message stringToMessage(String messageString) {
        String[] lines = messageString.split("\n");
        Message msg = new Message();


        return msg;
    }
}
