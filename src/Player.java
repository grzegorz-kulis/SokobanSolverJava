import java.io.Serializable;

public class Player extends Grid implements Serializable {

	private static final GridType gridType = GridType.PLAYER;

	public Player(int x, int y) {
		super(x, y, gridType);
	}

	public String toString() {
		return "Player address: (" + Integer.toString(super.getX()) + "," + Integer.toString(super.getY()) + ")";
	}
}
