package cz.vse.pexeso.model;

public enum PlayerStatus {
    READY("Ready"),
    NOT_READY("Not ready");

    private final String value;

    PlayerStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
