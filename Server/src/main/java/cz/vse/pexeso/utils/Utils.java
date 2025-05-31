package cz.vse.pexeso.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vse.pexeso.common.message.payload.SendablePlayer;
import cz.vse.pexeso.game.Player;

public class Utils {
    public static final Logger log = LoggerFactory.getLogger(Utils.class);

    public static String getLocalAddress() {
        String ret = "localhost";
        try {
            ret = InetAddress.getLocalHost().getHostAddress();
        } catch(UnknownHostException e) {
            log.error("Could not get local address");
        }
        return ret;
    }

    public static SendablePlayer toSendable(Player player) {
        return new SendablePlayer(player.getPlayerId(), player.getName(), player.isReady(), player.getScore());
    }

}
