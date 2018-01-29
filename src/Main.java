import java.io.IOException;

public class Main {

	private static String boardSolution;

	public static void main(String[] args) {
		try {
			ReadFile.getInstance().loadBoardFromFile("res/test_deadlock.txt");
//			ReadFile.getInstance().loadBoardFromFile("res/test.txt");
//			ReadFile.getInstance().loadBoardFromFile("res/test_5.txt");
		} catch(IOException e) {
			e.printStackTrace();
		}
		State startingState = ReadFile.getStartingState();
		StateGenerator boardSolver = new StateGenerator(startingState);
		boardSolution = boardSolver.generatingChildrenStates();
		System.out.println("Solution for the provided board is: " + boardSolution);
	}
}
