import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum MoveDirections {
    DOWN('d'),
    RIGHT('r'),
    UP('u'),
    LEFT('l'),
    PUSH_DOWN('D'),
    PUSH_RIGHT('R'),
    PUSH_UP('U'),
    PUSH_LEFT('L');

    private final char pushDirection;
    private static List<MoveDirections> listOfMoves;

    static {
        listOfMoves = new ArrayList<>(Arrays.asList(MoveDirections.values()))
                                    .subList(0, MoveDirections.values().length - 4);
    }

    MoveDirections(char pushDirection) {
        this.pushDirection = pushDirection;
    }

    public static MoveDirections getEnumByInt(int val) {
        return listOfMoves.get(val);
    }

    @Override
    public String toString() {
        return Character.toString(pushDirection);
    }
}
