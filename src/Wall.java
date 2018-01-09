import java.io.Serializable;

public class Wall extends Grid implements Serializable {
    private static final GridType gridType = GridType.WALL;

    public Wall(int x, int y) {
        super(x,y,gridType);
    }
}
