import java.io.*;
import java.util.HashMap;

public class Level3 {
    static HashMap<Character, int[]> moves = new HashMap<>();

    public static void main(String[] args) {
        moves.put('L', new int[]{0, -1});
        moves.put('R', new int[]{0, 1});
        moves.put('U', new int[]{-1, 0});
        moves.put('D', new int[]{1, 0});

        solveLevel3();
    }

    private static void solveLevel3() {
        File[] levelFiles = new File("level3").listFiles();
        if (levelFiles != null) {
            for (File f: levelFiles) {
                if (f.getName().split("\\.")[1].equals("in")) {
                    try (FileWriter fW = new FileWriter("level3/" + f.getName().split("\\.")[0] + ".out")) {
                        BoardInfo boardInfo = new BoardInfo(f);
                        GameStats stats = simulateGame(boardInfo);
                        fW.write(stats.coinCount + " " + (stats.alive ? "YES" : "NO") + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private static GameStats simulateGame(BoardInfo boardInfo) throws IOException {
        int count = 0;
        int ghostMove = 0;

        // Move PacMan
        for (char move : boardInfo.moveSeq) {
            boardInfo.pacRow += moves.get(move)[0];
            boardInfo.pacCol += moves.get(move)[1];

            // Check for wall collision
            if (boardInfo.boardMatrix[boardInfo.pacRow][boardInfo.pacCol] == 'W') return new GameStats(count, false);

            // Move ghosts
            for (Ghost g : boardInfo.ghosts) {
                int[] gMove = moves.get(g.moveSeq[ghostMove]);
                g.row += gMove[0];
                g.col += gMove[1];

                // Check for collision with ghost
                if (boardInfo.pacRow == g.row && boardInfo.pacCol == g.col) return new GameStats(count, false);
            }

            // Update index of move in ghost move sequency
            ghostMove++;

            // Check for coin pickup
            if (boardInfo.boardMatrix[boardInfo.pacRow][boardInfo.pacCol] == 'C') {
                boardInfo.boardMatrix[boardInfo.pacRow][boardInfo.pacCol] = 'c';
                count++;
            }
        }

        return new GameStats(count, true);
    }
}

class BoardInfo {
    char[][] boardMatrix;
    int pacRow;
    int pacCol;
    int seqLength;
    char[] moveSeq;

    Ghost[] ghosts;

    BoardInfo(File f) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        this.boardMatrix = genBoardMatrix(reader);
        String[] rowCol = reader.readLine().split(" ");
        this.pacRow = Integer.parseInt(rowCol[0]) - 1;
        this.pacCol = Integer.parseInt(rowCol[1]) - 1;
        this.seqLength = Integer.parseInt(reader.readLine());
        this.moveSeq = reader.readLine().toCharArray();

        int ghostCount = Integer.parseInt(reader.readLine());

        ghosts = new Ghost[ghostCount];

        for (int i = 0; i < ghostCount; i++) {
            ghosts[i] = new Ghost(reader);
        }

        reader.close();
    }

    private static char[][] genBoardMatrix(BufferedReader reader) throws IOException {
        int n = Integer.parseInt(reader.readLine());

        char[][] boardMatrix = new char[n][n];

        for (int i = 0; i < n; i++) {
            boardMatrix[i] = reader.readLine().toCharArray();
        }

        return boardMatrix;
    }
}

class Ghost {
    int row;
    int col;
    int seqLength;
    char[] moveSeq;

    Ghost(BufferedReader reader) throws IOException {
        String[] rowCol = reader.readLine().split(" ");
        this.row = Integer.parseInt(rowCol[0]) - 1;
        this.col = Integer.parseInt(rowCol[1]) - 1;
        this.seqLength = Integer.parseInt(reader.readLine());
        this.moveSeq = reader.readLine().toCharArray();
    }
}

class GameStats {
    int coinCount;
    boolean alive;

    GameStats(int coinCount, boolean alive) {
        this.coinCount = coinCount;
        this.alive = alive;
    }
}
