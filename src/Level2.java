import java.io.*;
import java.util.HashMap;

public class Level2 {
    static HashMap<Character, int[]> moves = new HashMap<>();

    public static void main(String[] args) {
        moves.put('L', new int[]{0, -1});
        moves.put('R', new int[]{0, 1});
        moves.put('U', new int[]{-1, 0});
        moves.put('D', new int[]{1, 0});

        solveLevel2();
    }

    static void solveLevel2() {
        File[] levelFiles = new File("level2").listFiles();
        if (levelFiles != null) {
            for (File f: levelFiles) {
                if (f.getName().split("\\.")[1].equals("in")) {
                    try (FileWriter fW = new FileWriter("level2/" + f.getName().split("\\.")[0] + ".out")) {
                        BoardInfo boardInfo = new BoardInfo(f);
                        fW.write(countCoins(boardInfo) + "\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    static int countCoins(BoardInfo boardInfo) throws IOException {
        int count = 0;

        for (char move : boardInfo.moveSeq) {
            boardInfo.pacRow += moves.get(move)[0];
            boardInfo.pacCol += moves.get(move)[1];

            if (boardInfo.boardMatrix[boardInfo.pacRow][boardInfo.pacCol] == 'C') {
                boardInfo.boardMatrix[boardInfo.pacRow][boardInfo.pacCol] = 'c';
                count++;
            }
        }

        return count;
    }
}

class BoardInfo {
    char[][] boardMatrix;
    int pacRow;
    int pacCol;
    int seqLength;
    char[] moveSeq;

    BoardInfo(File f) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(f));
        this.boardMatrix = genBoardMatrix(reader);
        String[] rowCol = reader.readLine().split(" ");
        this.pacRow = Integer.parseInt(rowCol[0]) - 1;
        this.pacCol = Integer.parseInt(rowCol[1]) - 1;
        this.seqLength = Integer.parseInt(reader.readLine());
        this.moveSeq = reader.readLine().toCharArray();

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
