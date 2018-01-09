import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ReadFile {

    private int boardHeight;
    private int boardWidth;
    private int boardBoxesNumber;
    List<String> fileAllLinesInList;
    private static State startingState;

    private static final String ASCII_SPACE = " ";

    private static class SingletonReadFileHolder {
        private static final ReadFile fileSingleton = new ReadFile();
    }

    public static ReadFile getInstance() {
        return SingletonReadFileHolder.fileSingleton;
    }

    private ReadFile() {
        this.fileAllLinesInList = new ArrayList<>();
        startingState = null;
    }

    public void loadBoardFromFile(String filename) throws IOException {
        Path path = Paths.get(filename);
        try(BufferedReader br = Files.newBufferedReader(path)) {
            try {
                fileAllLinesInList = Files.readAllLines(path);
                String[] firstLineSplit = fileAllLinesInList.get(0).split(ASCII_SPACE);
                boardHeight = Integer.parseInt(firstLineSplit[0]);
                boardWidth = Integer.parseInt(firstLineSplit[1]);
                boardBoxesNumber = Integer.parseInt(firstLineSplit[2]);
            } finally {
                if (br != null) {
                    br.close();
                }
            }
        }

        startingState = new State(new Board(boardWidth, boardHeight, fileAllLinesInList));
    }

    public static State getStartingState() {
        return startingState;
    }
}
