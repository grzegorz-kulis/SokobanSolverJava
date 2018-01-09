public enum MoveDirections {
    UP('u'),
    DOWN('d'),
    RIGHT('r'),
    LEFT('l'),
    PUSH_UP('U'),
    PUSH_DOWN('D'),
    PUSH_RIGHT('R'),
    PUSH_LEFT('L');

    private final char pushDirection;

    MoveDirections(char pushDirection) {
        this.pushDirection = pushDirection;
    }

    @Override
    public String toString() {
        return Character.toString(pushDirection);
    }
}
