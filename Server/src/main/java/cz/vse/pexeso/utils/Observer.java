package cz.vse.pexeso.utils;

/**
 * Observer - Observable model
 */
public interface Observer {

    void onNotify(Observable obs, Object o);

}
