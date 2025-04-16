package cz.vse.pexeso.common.message;

import cz.vse.pexeso.common.utils.MessageComponent;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class MessageTranslatorImpl implements MessageTranslator {

    @Override
    public String messageToString(Message message) {
        var entries = message.getEntries();

        if(entries == null || entries.isEmpty()) {
            return "";
        }

        String ret = "";

        ret += MessageComponent.START.getValue();
        ret += MessageComponent.SEPARATOR.getValue();

        ret += MessageComponent.TIMESTAMP.getValue();
        ret += MessageComponent.KEY_VALUE_SEPARATOR.getValue();
        ret += Instant.now().toString();
        ret += MessageComponent.SEPARATOR.getValue();

        for(MessageComponent mc : MessageComponent.getOrderedKeys()) {
            var entry = entries.get(mc);

            if(entry == null) {
                continue;
            }

            ret += mc.getValue();
            ret += MessageComponent.KEY_VALUE_SEPARATOR.getValue();
            ret += entry;
            ret += MessageComponent.SEPARATOR.getValue();
        }

        ret += MessageComponent.END.getValue();

        return ret;
    }

    @Override
    public Message stringToMessage(String messageString) {
        String[] entries = messageString.split(MessageComponent.SEPARATOR.getValue());

        Map<MessageComponent, String> inp = new HashMap<>();
        Message msg = new Message();

        for(String entry : entries) {
            if(entry.equals(MessageComponent.START.getValue()) || entry.equals(MessageComponent.END.getValue())) {
                continue;
            }

            String[] sep = entry.split(MessageComponent.KEY_VALUE_SEPARATOR.getValue());

            if(sep.length < 2) continue;

            MessageComponent mc;
            try {
                mc = MessageComponent.fromString(sep[0]);
            } catch (IllegalArgumentException e) {
                continue;
            }

            msg.setEntry(mc, sep[1]);
        }


        return msg;
    }
}
