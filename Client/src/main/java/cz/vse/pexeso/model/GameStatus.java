package cz.vse.pexeso.model;

public enum GameStatus {
    WAITING_FOR_PLAYERS("Waiting for players"),
    IN_PROGRESS("In progress"),
    FINISHED("Finished");

    private final String value;

    public String getValue() {
        return this.value;
    }

    GameStatus(String value) {
        this.value = value;
    }
}
