import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Box extends Grid implements Serializable {

	private static final GridType gridType = GridType.BOX;
	private Map<MoveDirections, Boolean> deadlockMapping;
	private int deadLockCounter;
	
	public Box(int x, int y) {
		super(x, y, gridType);
		this.deadlockMapping = fillDeadlockMapping();
		this.deadLockCounter = 0;
	}

	private static Map<MoveDirections, Boolean> fillDeadlockMapping() {
		Map<MoveDirections, Boolean> tempDeadlockMap = new HashMap<>(4);
		tempDeadlockMap.put(MoveDirections.DOWN, false);
		tempDeadlockMap.put(MoveDirections.RIGHT, false);
		tempDeadlockMap.put(MoveDirections.UP, false);
		tempDeadlockMap.put(MoveDirections.LEFT, false);

		return tempDeadlockMap;
	}

	/**********************************************************
	 ************************ GETTERS *************************
	 **********************************************************
	 */
	public int getDeadLockCounter() {
		return deadLockCounter;
	}

	public Map<MoveDirections, Boolean> getDeadlockMapping() {
		return deadlockMapping;
	}

	/**********************************************************
	 ************************ SETTERS *************************
	 **********************************************************
	 */

	public void setDeadLockCounter(int deadLockCounter) {
		this.deadLockCounter = deadLockCounter;
	}

	public void setDeadlockMapping(Map<MoveDirections, Boolean> deadlockMapping) {
		this.deadlockMapping = deadlockMapping;
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	public String toString() {
		return "\tBox at " + "(" + Integer.toString(super.getX()) + "," + Integer.toString(super.getY()) + ")\n";
	}
}
