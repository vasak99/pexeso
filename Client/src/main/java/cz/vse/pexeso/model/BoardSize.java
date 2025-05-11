package cz.vse.pexeso.model;

public enum BoardSize {
    SMALL(16),
    MEDIUM(36),
    LARGE(64);

    public final int value;

    BoardSize(int value) {
        this.value = value;
    }

    public static BoardSize fromValue(int value) {
        for (BoardSize boardSize : BoardSize.values()) {
            if (boardSize.value == value) {
                return boardSize;
            }
        }
        return null;
    }
}
