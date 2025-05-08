package cz.vse.pexeso.model.result;

public interface AuthResultListener {
    void onAuthSuccess(long playerId);

    void onAuthError(String errorMessage);
}
