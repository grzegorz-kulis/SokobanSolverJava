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
            //for(int a = 0; a < 1; a++) {
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
            for (int l = 0; l < MAX_POSSIBLE_MOVES; l++) {
                iterationsSolution++;
                State newBoardState = null;
                Object fieldClassFromOriginalBoardUnderCurrentPositionOfPlayer =
                        originalBoardState.getBoard().getGridBoard()
                                [currentBoardState.getBoard().getPlayerPosition().getX()]
                                [currentBoardState.getBoard().getPlayerPosition().getY()]
                                .getClass();
                Object fieldClassFromCurrentBoardInFrontOfPlayer =
                        currentBoardState.getBoard().getGridBoard()
                                [currentBoardState.getBoard().getPlayerPosition().getX() + MX[l]]
                                [currentBoardState.getBoard().getPlayerPosition().getY() + MY[l]]
                                .getClass();

                //ON THE CHECKING SPOT THERE IS A WALL
                if (fieldClassFromCurrentBoardInFrontOfPlayer == Wall.class) {
                    newBoardState = MoveSimulator.spotCheckIsWall();
                    addState = false;
                }
                //ON THE CHECKING SPOT THERE IS A EMPTY SPOT
                else if (fieldClassFromCurrentBoardInFrontOfPlayer == Field.class) {
                    newBoardState = MoveSimulator.spotCheckIsField(fieldClassFromOriginalBoardUnderCurrentPositionOfPlayer, currentBoardState, l);
                    addState = true;
                }
                //ON THE CHECKING SPOT THERE IS A GOAL SPOT
                else if (fieldClassFromCurrentBoardInFrontOfPlayer == Goal.class) {
                    newBoardState = MoveSimulator.spotCheckIsGoal(fieldClassFromOriginalBoardUnderCurrentPositionOfPlayer, currentBoardState, l);
                    addState = true;
                }
                //ON THE CHECKING SPOT THERE IS A BOX
                else if (fieldClassFromCurrentBoardInFrontOfPlayer == Box.class) {
                    newBoardState = MoveSimulator.spotCheckIsBox(fieldClassFromOriginalBoardUnderCurrentPositionOfPlayer, currentBoardState, l);
                    addState = true;
                } else {
                    System.out.println("Something went wrong");
                    addState = false;
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
                    }
                }
                System.out.println("\n----------------------------\n");
            }
            //just remove the current state from the queue, it is not a solution and it has been already added to HashMap of locked states
            stateQueue.poll();
        } //ending while
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

        private static TreeMap<String, Box> updateBoxMap(TreeMap<String, Box> boxes, String valueToRemove, Box valueToAdd) {
            TreeMap<String, Box> temp = new TreeMap<>(boxes);
            temp.remove(valueToRemove);
            temp.put(Integer.toString(valueToAdd.getX()) + Integer.toString(valueToAdd.getY()), valueToAdd);
            return temp;
        }

        private static State spotCheckIsWall() {
            System.out.println("Wall encountered. New state will not be generated.");
            return null;
        }

        private static State spotCheckIsField(Object gridClassUnderInitial, State currentState, int moveDirection) {
            System.out.println("Field encountered. New state will be generated.");
            int posXofFieldInCurrentBoard = currentState.getBoard().getPlayerPosition().getX();
            int posYofFieldInCurrentBoard = currentState.getBoard().getPlayerPosition().getY();
            Board buildNewBoard = new Board(currentState.getBoard().getBoardWidth(), currentState.getBoard().getBoardHeight());
            buildNewBoard.setGrid(currentState.getBoard().getGridBoard());

            //check if the spot where player is standing is Goal spot by any chance  in the original map
            if (gridClassUnderInitial == Goal.class) {
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

        private static State spotCheckIsGoal(Object gridClassUnderInitial, State currentState, int moveDirection) {
            System.out.println("Goal encountered. New state will be generated.");
            int posXofFieldInCurrentBoard = currentState.getBoard().getPlayerPosition().getX();
            int posYofFieldInCurrentBoard = currentState.getBoard().getPlayerPosition().getY();
            Board buildNewBoard = new Board(currentState.getBoard().getBoardWidth(), currentState.getBoard().getBoardHeight());
            buildNewBoard.setGrid(currentState.getBoard().getGridBoard());

            //check if the spot where player is standing is Goal spot by any chance  in the original map
            if (gridClassUnderInitial == Goal.class) {
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

        private static State spotCheckIsBox(Object gridClassUnderInitial, State currentState, int moveDirection) {
            System.out.println("Box encountered. New state might be generated.");
            Object fieldClassFromCurrentBoardTwiceInFrontOfPlayer =
                    currentState.getBoard().getGridBoard()[currentState.getBoard().getPlayerPosition().getX() + MX[moveDirection] + MX[moveDirection]]
                            [currentState.getBoard().getPlayerPosition().getY() + MY[moveDirection] + MY[moveDirection]]
                            .getClass();

            int posXofFieldInCurrentBoard = currentState.getBoard().getPlayerPosition().getX();
            int posYofFieldInCurrentBoard = currentState.getBoard().getPlayerPosition().getY();
            Board buildNewBoard = new Board(currentState.getBoard().getBoardWidth(), currentState.getBoard().getBoardHeight());
            buildNewBoard.setGrid(currentState.getBoard().getGridBoard());

            //check if there is a wall or another box behind the pushing box
            if (fieldClassFromCurrentBoardTwiceInFrontOfPlayer == Box.class || fieldClassFromCurrentBoardTwiceInFrontOfPlayer == Wall.class) {
                System.out.println("Can not push. Box or wall ahead.");
                return null;
            }
            else {
                System.out.println("Box can be pushed. New state will be generated.");
                //check if the spot where player is standing is Goal spot by any chance  in the original map
                if(gridClassUnderInitial == Goal.class) {
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

                return new State(buildNewBoard, appendSolution(new StringBuilder(currentState.getSolution()), moveDirection, GridType.BOX.toString()));
            }
        }
    }
}


