import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Level5 {
    static HashMap<Character, int[]> moves = new HashMap<>();

    public static void main(String[] args) {
        moves.put('L', new int[]{0, -1});
        moves.put('R', new int[]{0, 1});
        moves.put('U', new int[]{-1, 0});
        moves.put('D', new int[]{1, 0});

        solveLevel5();
    }

    private static void solveLevel5() {
        File[] levelFiles = new File("level5").listFiles();
        if (levelFiles != null) {
            for (File f: levelFiles) {
                if (f.getName().split("\\.")[1].equals("in")) {
                    try (FileWriter fW = new FileWriter("level5/" + f.getName().split("\\.")[0] + ".out")) {
                        BoardInfo boardInfo;
                        Stats stats;
                        do {
                            boardInfo = new BoardInfo(f);
                            stats = simulateGame(boardInfo);
                        } while (stats.moveCount > boardInfo.maxMoves);
                        fW.write(stats.moveSeq + "\n");
                        System.out.println("Finished: " + f.getName());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private static Stats simulateGame(BoardInfo boardInfo) throws IOException {
        StringBuilder moveSeqBuilder = new StringBuilder();
        int coinCount = countCoins(boardInfo);
        int pickUpCount = 0;

        int moveCounter = 0;
        int ghostMove = 0;

        while (pickUpCount != coinCount) {
            // Move ghosts
            for (Ghost g : boardInfo.ghosts) {
                int[] gMove = moves.get(g.moveSeq[ghostMove]);
                g.row += gMove[0];
                g.col += gMove[1];
            }

            // Update index of move in ghost move sequency
            ghostMove++;

            if (ghostMove == boardInfo.ghosts[0].seqLength) {
                ghostMove = 0;
                for (Ghost g : boardInfo.ghosts) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(g.moveSeq);
                    g.moveSeq = stringBuilder.reverse().toString().toCharArray();
                }
            }

            // Find a move for PacMan
            char move = findBestMove(boardInfo);
            moveSeqBuilder.append(move);
            int[] moveOffset = moves.get(move);
            boardInfo.pacRow += moveOffset[0];
            boardInfo.pacCol += moveOffset[1];
            moveCounter++;

            // Check for coin pickup
            if (boardInfo.boardMatrix[boardInfo.pacRow][boardInfo.pacCol] == 'C') {
                boardInfo.boardMatrix[boardInfo.pacRow][boardInfo.pacCol] = 'c';
                pickUpCount++;
            }

            // update already visited
            if (boardInfo.boardMatrix[boardInfo.pacRow][boardInfo.pacCol] == 'E') {
                boardInfo.boardMatrix[boardInfo.pacRow][boardInfo.pacCol] = 'A';
            }

            if (moveCounter > boardInfo.maxMoves) return new Stats(moveSeqBuilder.toString(), moveCounter);
        }

        return new Stats(moveSeqBuilder.toString(), moveCounter);
    }

    private static char findBestMove(BoardInfo boardInfo) {
        ArrayList<Character> possibleMoves = new ArrayList<>();

        for (char k : moves.keySet()) {
            int[] move = moves.get(k);
            int[] newPos = new int[]{boardInfo.pacRow + move[0], boardInfo.pacCol + move[1]};

            if ((newPos[0] >= 0 && newPos[0] < boardInfo.n) && (newPos[1] >= 0 && newPos[1] < boardInfo.n)) {
                char encounter = boardInfo.boardMatrix[newPos[0]][newPos[1]];

                if (encounter == 'C' || encounter == 'E') return k;

                if (encounter == 'W' || encounter == 'G') continue;

                boolean moveHitsGhost = false;
                for (Ghost g : boardInfo.ghosts) {
                    if (g.row == newPos[0] && g.col == newPos[1]) {
                        moveHitsGhost = true;
                        break;
                    }
                }

                if (moveHitsGhost) continue;

                possibleMoves.add(k);
            }
        }

        return possibleMoves.get(new Random().nextInt(possibleMoves.size()));
    }

    private static int countCoins(BoardInfo boardInfo) {
        int c = 0;

        for (char[] row: boardInfo.boardMatrix) {
            for (char field : row) {
                if (field == 'C') c++;
            }
        }

        return c;
    }
}

class BoardInfo {
    int n;
    char[][] boardMatrix;
    int pacRow;
    int pacCol;

    Ghost[] ghosts;
    int maxMoves;

    BoardInfo(File f) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        this.n = Integer.parseInt(reader.readLine());
        this.boardMatrix = genBoardMatrix(reader);
        String[] rowCol = reader.readLine().split(" ");
        this.pacRow = Integer.parseInt(rowCol[0]) - 1;
        this.pacCol = Integer.parseInt(rowCol[1]) - 1;

        int ghostCount = Integer.parseInt(reader.readLine());

        ghosts = new Ghost[ghostCount];

        for (int i = 0; i < ghostCount; i++) {
            ghosts[i] = new Ghost(reader);
        }

        this.maxMoves = Integer.parseInt(reader.readLine());

        reader.close();
    }

    private char[][] genBoardMatrix(BufferedReader reader) throws IOException {
        char[][] boardMatrix = new char[this.n][this.n];

        for (int i = 0; i < this.n; i++) {
            boardMatrix[i] = reader.readLine().toCharArray();
        }

        return boardMatrix;
    }
}

class Stats {
    String moveSeq;
    int moveCount;

    Stats(String moveSeq, int moveCount) {
        this.moveSeq = moveSeq;
        this.moveCount = moveCount;
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
