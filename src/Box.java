import java.io.Serializable;

public class Box extends Grid implements Serializable {

	private static final GridType gridType = GridType.BOX;
	
	public Box(int x, int y) {
		super(x, y, gridType);
	}
	
	public String toString() {
		return "\tBox at " + "(" + Integer.toString(super.getX()) + "," + Integer.toString(super.getY()) + ")\n";
	}
}
