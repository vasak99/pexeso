package cz.vse.pexeso.model.observer;

public enum MessageTypeClient {
    LOGIN,
    REGISTER,
    REDIRECT,

    CREATE_GAME_SUCCESS,
    EDIT_GAME_SUCCESS,

    ERROR_LOGIN,
    ERROR_REGISTER,
    ERROR_CREATE_GAME,
    ERROR_EDIT_GAME,
}
