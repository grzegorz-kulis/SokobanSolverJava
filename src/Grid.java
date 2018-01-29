import java.io.Serializable;
import java.util.Objects;

public abstract class Grid implements Serializable {

    private int x;
    private int y;
    private GridType gridType;

    public Grid(int x, int y, GridType gridType)
    {
        this.x = x;
        this.y = y;
        this.gridType = gridType;
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public GridType getGridType() {
        return this.gridType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Grid grid = (Grid) o;
        return x == grid.x &&
                y == grid.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
