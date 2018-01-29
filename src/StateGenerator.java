import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class StateGenerator {
    // {right, up, left, down}
    private final static int[] MX = {1, 0, -1, 0};
    private final static int[] MY = {0, 1, 0, -1};
    private final static int MAX_POSSIBLE_MOVES = 4;

    private static int iterationsSolution = 0;

    private State startingBoardState;
    private final State originalBoardState;

    public StateGenerator(State boardState) {
        this.startingBoardState = boardState;
        this.originalBoardState = boardState; // this is needed so that goal grids are tracked in board generation
    }

    private static boolean checkSolution(TreeMap<String, Box> boxMap, TreeMap<String, Goal> goalMap) {
        return boxMap.keySet().equals(goalMap.keySet());
    }

    public String generatingChildrenStates() {

        Queue<State> stateQueue = new LinkedBlockingQueue<>();
        stateQueue.offer(startingBoardState);

        boolean addState; // if this is false no additional actions will be performed on adding the state (saves time possibly?)
        Map<String, State> lockedStates = new HashMap<>();
        Set<String> alreadyInQueue = new HashSet<>();

        System.out.println("START");
        long startTime = System.currentTimeMillis();

        while (stateQueue.size() != 0) {
//            for(int a = 0; a < 1; a++) {
            State currentBoardState = stateQueue.peek();
            //peek to the first State in the queue and examine it and generate children from it
            System.out.println("\nLEADING STATE");
            System.out.println(currentBoardState);
            System.out.println("\n---------------------\n");
            //add currentBoardState to the HashMap of locked (visited) states
            alreadyInQueue.remove(currentBoardState.getStateHash());
            lockedStates.put(currentBoardState.getStateHash(), currentBoardState);

            if (stateQueue.size() == 0) {
                System.out.println("Something went terribly wrong!");
                break;
            }

            //check all the moves: down, right, up, left
            for (int move = 0; move < MAX_POSSIBLE_MOVES; move++) {
                iterationsSolution++;
                State newBoardState = null;

                GridType fieldClassFromOriginalBoardUnderCurrentPositionOfPlayerGrid =
                        originalBoardState.getBoard().getGridBoard()
                                [currentBoardState.getBoard().getPlayerPosition().getX()]
                                [currentBoardState.getBoard().getPlayerPosition().getY()]
                                .getGridType();

                GridType fieldClassFromCurrentBoardInFrontOfPlayerGrid =
                        currentBoardState.getBoard().getGridBoard()
                                [currentBoardState.getBoard().getPlayerPosition().getX() + MX[move]]
                                [currentBoardState.getBoard().getPlayerPosition().getY() + MY[move]]
                                .getGridType();

                switch (fieldClassFromCurrentBoardInFrontOfPlayerGrid) {
                    case WALL: {
                        newBoardState = MoveSimulator.spotCheckIsWall();
                        addState = false;
                        break;
                    }
                    case FIELD: {
                        newBoardState = MoveSimulator.spotCheckIsField(fieldClassFromOriginalBoardUnderCurrentPositionOfPlayerGrid,
                                                                       currentBoardState,
                                                                       move);
                        addState = true;
                        break;
                    }
                    case GOAL: {
                        newBoardState = MoveSimulator.spotCheckIsGoal(fieldClassFromOriginalBoardUnderCurrentPositionOfPlayerGrid,
                                                                      currentBoardState,
                                                                      move);
                        addState = true;
                        break;
                    }
                    case BOX: {
                        newBoardState = MoveSimulator.spotCheckIsBox(fieldClassFromOriginalBoardUnderCurrentPositionOfPlayerGrid,
                                                                     startingBoardState.getBoard().getGoals(),
                                                                     currentBoardState,
                                                                     move);
                        addState = true;
                        break;
                    }
                    default: {
                        System.out.println("Something went wrong");
                        addState = false;
                    }
                }

                System.out.println(newBoardState);
                if (newBoardState != null) {
                    //before doing anything to the created state, check its possible solution
                    if (checkSolution(newBoardState.getBoard().getBoxes(), startingBoardState.getBoard().getGoals())) {
                        System.out.format("\n%d iterations were required to find solution.\n", iterationsSolution);
                        System.out.format("It took %d seconds and %d milliseconds to find solution.\n",
                                TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime),
                                (System.currentTimeMillis() - startTime) - (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - startTime)*1000));
                        return newBoardState.getSolution();
                    }
                    if (lockedStates.containsKey(newBoardState.getStateHash()) || alreadyInQueue.contains(newBoardState.getStateHash())) {
                        //it is in the HashMap of locked states, we will not add it
                        System.out.println("This state has already happened. Moving on...");
                    } else if (addState) {
                        //else, it is not. lets add it to the queue and add it to the HashMap of locked states to make sure we already mark it as 'visited'
                        System.out.println("Adding state to the queue");
                        alreadyInQueue.add(newBoardState.getStateHash());
                        stateQueue.offer(newBoardState);
                    } else {
                        return "Check your source code mr developer xD";
                    }
                }
                System.out.println("\n----------------------------\n");
            } // ending for
            // just remove the current state from the queue, it is not a solution and it has been already added to HashMap of locked states
            stateQueue.poll();
        } // ending while
        return "Unable to find solution, sorry";
    }

    /**********************************************************
     ******************** MOVE SIMULATOR **********************
     **********************************************************
     */

    private static class MoveSimulator {
        private static StringBuilder appendSolution(StringBuilder solutionFromCurrentState, int id, String moveLiteral) {
            StringBuilder resultingNewSolution = new StringBuilder(solutionFromCurrentState);
            if(moveLiteral.equals(GridType.FIELD.toString()) || moveLiteral.equals(GridType.GOAL.toString())) {
                switch(id) {
                    case 0:
                        resultingNewSolution.append(MoveDirections.DOWN); //down
                        break;
                    case 1:
                        resultingNewSolution.append(MoveDirections.RIGHT); //right
                        break;
                    case 2:
                        resultingNewSolution.append(MoveDirections.UP); //up
                        break;
                    case 3:
                        resultingNewSolution.append(MoveDirections.LEFT); //left
                        break;
                    default:
                        System.out.println("Default behaviour, should not happen");
                }
            } else if(moveLiteral.equals(GridType.BOX.toString()))	{
                switch(id) {
                    case 0:
                        resultingNewSolution.append(MoveDirections.PUSH_DOWN); //DOWN
                        break;
                    case 1:
                        resultingNewSolution.append(MoveDirections.PUSH_RIGHT); //RIGHT
                        break;
                    case 2:
                        resultingNewSolution.append(MoveDirections.PUSH_UP); //UP
                        break;
                    case 3:
                        resultingNewSolution.append(MoveDirections.PUSH_LEFT); //LEFT
                        break;
                    default:
                        System.out.println("Default behaviour, should not happen");
                }
            }
            return resultingNewSolution;
        }

        private static TreeMap<String, Box> updateBoxMap(Map<String, Box> boxes, String valueToRemove, Box valueToAdd) {
            TreeMap<String, Box> temp = new TreeMap<>(boxes);
            temp.remove(valueToRemove);
            temp.put(Integer.toString(valueToAdd.getX()) + Integer.toString(valueToAdd.getY()), valueToAdd);
            return temp;
        }

        private static State spotCheckIsWall() {
            System.out.println("Wall encountered. New state will not be generated.");
            return null;
        }

        private static State spotCheckIsField(GridType gridClassUnderInitial, State currentState, int moveDirection) {
            System.out.println("Field encountered. New state will be generated.");
            int posXofFieldInCurrentBoard = currentState.getBoard().getPlayerPosition().getX();
            int posYofFieldInCurrentBoard = currentState.getBoard().getPlayerPosition().getY();
            Board buildNewBoard = new Board(currentState.getBoard().getBoardWidth(), currentState.getBoard().getBoardHeight());
            buildNewBoard.setGrid(currentState.getBoard().getGridBoard());

            //check if the spot where player is standing is Goal spot by any chance  in the original map
            if (gridClassUnderInitial.equals(GridType.GOAL)) {
                buildNewBoard.getGridBoard()[posXofFieldInCurrentBoard][posYofFieldInCurrentBoard]
                        = new Goal(posXofFieldInCurrentBoard, posYofFieldInCurrentBoard);
                buildNewBoard.setPlayerPosition(new Player(posXofFieldInCurrentBoard + MX[moveDirection], posYofFieldInCurrentBoard + MY[moveDirection]));
            } else {
                buildNewBoard.getGridBoard()[posXofFieldInCurrentBoard][posYofFieldInCurrentBoard]
                        = new Field(posXofFieldInCurrentBoard, posYofFieldInCurrentBoard);
                buildNewBoard.setPlayerPosition(new Player(posXofFieldInCurrentBoard + MX[moveDirection], posYofFieldInCurrentBoard + MY[moveDirection]));
            }

            buildNewBoard.setBoxes(currentState.getBoard().getBoxes());
            return new State(buildNewBoard, appendSolution(new StringBuilder(currentState.getSolution()), moveDirection, GridType.FIELD.toString()));
        }

        private static State spotCheckIsGoal(GridType gridClassUnderInitial, State currentState, int moveDirection) {
            System.out.println("Goal encountered. New state will be generated.");
            int posXofFieldInCurrentBoard = currentState.getBoard().getPlayerPosition().getX();
            int posYofFieldInCurrentBoard = currentState.getBoard().getPlayerPosition().getY();
            Board buildNewBoard = new Board(currentState.getBoard().getBoardWidth(), currentState.getBoard().getBoardHeight());
            buildNewBoard.setGrid(currentState.getBoard().getGridBoard());

            //check if the spot where player is standing is Goal spot by any chance  in the original map
            if (gridClassUnderInitial.equals(GridType.GOAL)) {
                buildNewBoard.getGridBoard()[posXofFieldInCurrentBoard][posYofFieldInCurrentBoard]
                        = new Goal(posXofFieldInCurrentBoard, posYofFieldInCurrentBoard);
                buildNewBoard.setPlayerPosition(new Player(posXofFieldInCurrentBoard + MX[moveDirection], posYofFieldInCurrentBoard + MY[moveDirection]));
            } else {
                buildNewBoard.getGridBoard()[posXofFieldInCurrentBoard][posYofFieldInCurrentBoard]
                        = new Field(posXofFieldInCurrentBoard, posYofFieldInCurrentBoard);
                buildNewBoard.setPlayerPosition(new Player(posXofFieldInCurrentBoard + MX[moveDirection], posYofFieldInCurrentBoard + MY[moveDirection]));
            }
            buildNewBoard.setBoxes(currentState.getBoard().getBoxes());
            return new State(buildNewBoard, appendSolution(new StringBuilder(currentState.getSolution()), moveDirection, GridType.GOAL.toString()));
        }

        private static State spotCheckIsBox(GridType gridTypeUnderInitial, TreeMap<String, Goal> goals, State currentState, int moveDirection) {
            System.out.println("Box encountered. New state might be generated.");
            GridType fieldClassFromCurrentBoardTwiceInFrontOfPlayer =
                    currentState.getBoard().getGridBoard()
                            [currentState.getBoard().getPlayerPosition().getX() + MX[moveDirection] + MX[moveDirection]]
                            [currentState.getBoard().getPlayerPosition().getY() + MY[moveDirection] + MY[moveDirection]]
                            .getGridType();

            int posXofFieldInCurrentBoard = currentState.getBoard().getPlayerPosition().getX();
            int posYofFieldInCurrentBoard = currentState.getBoard().getPlayerPosition().getY();
            Board buildNewBoard = new Board(currentState.getBoard().getBoardWidth(), currentState.getBoard().getBoardHeight());
            buildNewBoard.setGrid(currentState.getBoard().getGridBoard());

            //check if there is a wall or another box behind the pushing box
            if (fieldClassFromCurrentBoardTwiceInFrontOfPlayer.equals(GridType.BOX)|| fieldClassFromCurrentBoardTwiceInFrontOfPlayer.equals(GridType.WALL)) {
                System.out.println("Can not push. Box or wall ahead.");
                return null;
            }
            else {
                System.out.println("Box can be pushed. New state will be generated unless it is deadlock.");
                //check if the spot where player is standing is Goal spot by any chance  in the original map
                if(gridTypeUnderInitial.equals(GridType.GOAL)) {
                    buildNewBoard.getGridBoard()[posXofFieldInCurrentBoard][posYofFieldInCurrentBoard]
                            = new Goal(posXofFieldInCurrentBoard, posYofFieldInCurrentBoard);
                    buildNewBoard.setPlayerPosition(new Player(posXofFieldInCurrentBoard + MX[moveDirection], posYofFieldInCurrentBoard + MY[moveDirection]));
                }
                else {
                    buildNewBoard.getGridBoard()[posXofFieldInCurrentBoard][posYofFieldInCurrentBoard]
                            = new Field(posXofFieldInCurrentBoard, posYofFieldInCurrentBoard);
                    buildNewBoard.setPlayerPosition(new Player(posXofFieldInCurrentBoard + MX[moveDirection], posYofFieldInCurrentBoard + MY[moveDirection]));
                }
                buildNewBoard.setBoxes(updateBoxMap(
                        currentState.getBoard().getBoxes(),
                        Integer.toString(buildNewBoard.getPlayerPosition().getX()) + Integer.toString(buildNewBoard.getPlayerPosition().getY()),
                        new Box(buildNewBoard.getPlayerPosition().getX() + MX[moveDirection], buildNewBoard.getPlayerPosition().getY() + MY[moveDirection])));

                // check if newly built board is not in deadlock by any means
                if(DeadlockChecker.checkDeadlock(buildNewBoard, goals)) {
                    System.out.println("This will lead to deadlock, generating this state is pointless");
                    return null;
                }
                else
                    return new State(buildNewBoard, appendSolution(new StringBuilder(currentState.getSolution()), moveDirection, GridType.BOX.toString()));
            }
        }

    }

    private static class DeadlockChecker {
        private static boolean checkDeadlock(Board board, TreeMap<String, Goal> goals) {
            // check if position under board is a deadlock position
            // 1. simple deadlock - corner
            boolean result = simpleCornerDeadlock(board, goals);

            return result;
        }

        private static boolean simpleCornerDeadlock(Board board, TreeMap<String, Goal> goals) {
            Map<String, Box> boxes = board.getBoxes();
            //System.out.println("\n----------- DEADLOCKS --------------");

            for(Box  box : boxes.values()) {
                Map<MoveDirections, Boolean> possible = box.getDeadlockMapping();
                int deadLockCounter = 0;
                for(int move = 0; move < MAX_POSSIBLE_MOVES; move++) {
                    //System.out.println("box X: " + (box.getX()+ MX[move]) + ", box Y: " + (box.getY()+ MY[move]) + ", gridType: " + board.getGridBoard()[box.getX() + MX[move]][box.getY() + MY[move]].getGridType().name());
                    if((board.getGridBoard()[box.getX() + MX[move]][box.getY() + MY[move]].getGridType()).equals(GridType.WALL) &&
                            seekThroughGoals(box, goals)) {
                        possible.put(MoveDirections.getEnumByInt(move), true);
                        //System.out.println("Deadlock Counter++");
                        box.setDeadLockCounter(++deadLockCounter);
                    }
                }
                System.out.println("Box at (" + box.getX() + ", " + box.getY() + "): " + DebugFunctions.mapToString(possible));
                if((possible.get(MoveDirections.DOWN) && possible.get(MoveDirections.LEFT)) ||
                        (possible.get(MoveDirections.DOWN) && possible.get(MoveDirections.RIGHT)) ||
                        (possible.get(MoveDirections.UP) && possible.get(MoveDirections.RIGHT)) ||
                        (possible.get(MoveDirections.UP) && possible.get(MoveDirections.LEFT))) {
                    System.out.println("Looks like corner deadlock to me");
                    return true;
                }
                if(box.getDeadLockCounter() >= 3) return true;
            }
            return false;
        }

        private static boolean seekThroughGoals(Box box, TreeMap<String, Goal> goals) {
            for(Goal goal : goals.values()) {
                //System.out.println("goal X: " + goal.getX() + ", goal Y: " + goal.getY());
                if(goal.equals(box))  {
                    //System.out.println("RETURN FALSE");
                    return false;
                }
            }
            //System.out.println("RETURN TRUE");
            return true;
        }
    }

    private static class DebugFunctions {
        private static String mapToString(Map<MoveDirections, Boolean> map) {
            StringBuilder stringedAll = new StringBuilder();
            for (MoveDirections k : map.keySet()) {
                stringedAll.append(k.name()).append(": ").append(map.get(k)).append(", ");
            }
            return stringedAll.toString();
        }
    }
}


