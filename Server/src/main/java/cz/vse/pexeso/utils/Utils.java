package cz.vse.pexeso.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

}
