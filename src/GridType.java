public enum GridType {
    PLAYER('M'),
    BOX('J'),
    GOAL('G'),
    FIELD('.'),
    WALL('X');

    private final char asciiGrid;
    GridType(char asciiGrid) {
        this.asciiGrid = asciiGrid;
    }

    @Override
    public String toString() {
        return Character.toString(this.asciiGrid);
    }

}

