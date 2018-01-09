import java.io.*;
import java.util.*;

public class Board implements Serializable, DeepCopy {
    private int boardWidth;
    private int boardHeight;

    private Player playerPosition;
    private Map<String, Box> boxes;
    private Map<String, Goal> goals;

    private Grid[][] gridBoard;

    //establish first ever Board from file
    public Board(int boardWidth, int boardHeight, List<String> fileAllLinesInList) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.gridBoard = new Grid[boardWidth][boardHeight];
        this.boxes = new TreeMap<>();
        this.goals = new TreeMap<>();
        fillBoardFromList(fileAllLinesInList);
    }

    //establish any other new Board
    public Board(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        this.gridBoard = new Grid[boardWidth][boardHeight];
    }

    private void fillBoardFromList(List<String> fileAllLinesInList) {
        for(int rowList = 1, row = rowList - 1; rowList < fileAllLinesInList.size(); rowList++, row = rowList - 1) {
            String s = fileAllLinesInList.get(rowList);
            for(int col = 0; col < s.length(); col++) {
                char[] arr = s.toCharArray();
                if(arr[col] == GridType.PLAYER.toString().charAt(0)) {
                    Player tempPlayer = new Player(row,col);
                    gridBoard[row][col] = this.playerPosition = tempPlayer;
                } else if(arr[col] == GridType.BOX.toString().charAt(0)) {
                    Box tempBox = new Box(row, col);
                    gridBoard[row][col] = tempBox;
                    boxes.put(Integer.toString(row) + Integer.toString(col), tempBox);
                } else if(arr[col] == GridType.GOAL.toString().charAt(0)) {
                    Goal tempGoal = new Goal(row, col);
                    gridBoard[row][col] = tempGoal;
                    goals.put(Integer.toString(row) + Integer.toString(col), tempGoal);
                } else if(arr[col] == GridType.WALL.toString().charAt(0)) {
                    gridBoard[row][col] = new Wall(row, col);
                } else if(arr[col] == GridType.FIELD.toString().charAt(0)) {
                    gridBoard[row][col] = new Field(row, col);
                }
            }
        }
    }

    /**********************************************************
     ************************ SETTERS *************************
     **********************************************************
     */

    public void setGrid(Grid[][] gameBoard) {
        this.gridBoard = (Grid[][]) copy(gameBoard);
    }

    public void setPlayerPosition(Player playerPosition) {
        this.playerPosition = playerPosition;
        this.gridBoard[playerPosition.getX()][playerPosition.getY()] = playerPosition;
    }

    public void setBoxes(TreeMap<String, Box> boxes) {
        this.boxes = boxes;
        for(Box tempBox : boxes.values())
            this.gridBoard[tempBox.getX()][tempBox.getY()] = tempBox;
    }

    /**********************************************************
     ************************ GETTERS *************************
     **********************************************************
     */

    public int getBoardWidth() {
        return this.boardWidth;
    }

    public int getBoardHeight() {
        return this.boardHeight;
    }

    public Grid[][] getGridBoard() {
        return this.gridBoard;
    }

    public Player getPlayerPosition() {
        return this.playerPosition;
    }

    public TreeMap<String, Box> getBoxes() {
        return (TreeMap)this.boxes;
    }

    public TreeMap<String, Goal> getGoals() {
        return (TreeMap)this.goals;
    }

    @Override
    public Object copy(Object orig) {
        Object obj = null;
        try {
            // Write the object out to a byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bos);
            out.writeObject(orig);
            out.flush();
            out.close();

            // Make an input stream from the byte array and read
            // a copy of the object back in.
            ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(bos.toByteArray()));
            obj = in.readObject();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        catch(ClassNotFoundException cnfe) {
            cnfe.printStackTrace();
        }
        return obj;
    }

    @Override
    public String toString() {
        return DebugFunctions.printBoard(this)
                + this.playerPosition.toString()
                + "\nBox addresses:\n" + DebugFunctions.boxListToString((TreeMap)this.boxes).toString();
    }

    private static class DebugFunctions {
        private static String printBoard(Board gameBoard) {
            StringBuilder output = new StringBuilder();
            for(int i = 0; i < gameBoard.getBoardWidth(); i++) {
                for (int j = 0; j < gameBoard.getBoardHeight(); j++) {
                    output.append(gameBoard.gridBoard[i][j].getGridType());
                }
                output.append("\n");
            }
            return output.toString();
        }

        private static StringBuilder boxListToString(TreeMap<String, Box> boxList) {
            StringBuilder boxListString = new StringBuilder();
            for(String name: boxList.keySet()) {
                String value = boxList.get(name).toString();
                boxListString.append(value);
            }
            return boxListString;
        }
    }
}
