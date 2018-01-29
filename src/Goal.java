import java.io.Serializable;

public class Goal extends Grid implements Serializable {

	private static final GridType gridType = GridType.GOAL;

	public Goal(int x, int y) {
		super(x, y, gridType);
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	public String toString() {
		return "Goal at: " + "(" + Integer.toString(super.getX()) + "," + Integer.toString(super.getY()) + ")\n";
	}
}
