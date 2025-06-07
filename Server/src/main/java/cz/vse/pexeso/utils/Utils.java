package cz.vse.pexeso.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.vse.pexeso.common.message.payload.SendablePlayer;
import cz.vse.pexeso.game.Player;

/**
 * Unclassified utility methods
 */
public final class Utils {
    public static final Logger log = LoggerFactory.getLogger(Utils.class);

    private Utils() {}

    /**
     * Retrieves current server address
     * @return {@link String}
     */
    public static String getLocalAddress() {
        String ret = "localhost";
        try {
            ret = InetAddress.getLocalHost().getHostAddress();
        } catch(UnknownHostException e) {
            log.error("Could not get local address");
        }
        return ret;
    }

    /**
     * Transforms a {@link Player} object into Client-recieveable form
     * @param player {@link Player} objectt to be transformed
     * @return {@link SendablePlayer}
     */
    public static SendablePlayer toSendable(Player player) {
        return new SendablePlayer(player.getPlayerId(), player.getName(), player.isReady(), player.getScore());
    }

}
