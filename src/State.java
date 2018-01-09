import java.io.*;
import java.util.Set;
import java.util.TreeMap;


public class State implements Serializable {

	private String solutionState;
	private String hashState;

	private Board boardOfState;
	private static String hashGoal;

	// for init State
	public State(Board boardOfState) {
		this.boardOfState = boardOfState;
		this.solutionState = "";
		this.hashState = hashStateGenerator();
		hashGoal = treeMapKeySetToString(boardOfState.getGoals());
	}

	//for any other auto-generated State
	public State(Board boardOfState, StringBuilder solutionState) {
		this.boardOfState = boardOfState;
		this.solutionState = solutionState.toString();
		this.hashState = hashStateGenerator();
	}

	/**********************************************************
	 ************************ GETTERS *************************
	 **********************************************************
	 */
	public Board getBoard() {
		return boardOfState;
	}
	
	public String getSolution() {
		return this.solutionState;
	}

	public String getStateHash() {
		return this.hashState;
	}

	private String hashStateGenerator() {
		return GridType.PLAYER.toString() + this.boardOfState.getPlayerPosition().hashCode() +
				GridType.BOX.toString() + treeMapKeySetToString(this.boardOfState.getBoxes());
	}

	private static <K,V> String treeMapKeySetToString(TreeMap<K, V> treeMap) {
		Set<K> keys = treeMap.keySet();
		StringBuilder stringedKeySet = new StringBuilder();
		for(K i : keys) {
			stringedKeySet.append(i);
		}
		return stringedKeySet.toString();
	}

	public String toString() {
		return "State:\n" + this.boardOfState.toString()
				+ "Goal hash: " + hashGoal
				+ "\nSolution: " + this.solutionState
				+ "\nState hash: " + this.hashState;
	}
}
