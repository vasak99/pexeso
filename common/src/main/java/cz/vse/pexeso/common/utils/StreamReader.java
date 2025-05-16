package cz.vse.pexeso.common.utils;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import cz.vse.pexeso.common.message.Message;


public class StreamReader {

    public static String readPacket(ObjectInputStream ois) throws ClassNotFoundException, IOException, InterruptedException {
        String res = null;

        try {
            res = (String) ois.readObject();
        } catch (EOFException e) {}

        return res;
    }

    public static void writePacket(ObjectOutputStream oos, String msg) throws IOException {
        oos.writeObject(msg);
    }

}
