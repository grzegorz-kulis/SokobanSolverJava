import java.io.Serializable;

public class Field extends Grid implements Serializable {
    private final static GridType gridType = GridType.FIELD;

    public Field(int x, int y) {
        super(x, y, gridType);
    }
}
