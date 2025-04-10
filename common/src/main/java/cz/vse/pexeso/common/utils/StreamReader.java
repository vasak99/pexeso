package cz.vse.pexeso.common.utils;

import java.io.IOException;
import java.io.ObjectInputStream;

public class StreamReader {

    public static String readPacket(ObjectInputStream ois) throws ClassNotFoundException, IOException {

        String res = (String) ois.readObject();

        return res;
    }

}
