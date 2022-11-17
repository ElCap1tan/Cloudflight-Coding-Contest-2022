import java.io.*;

public class Level1 {
    public static void main(String[] args) {
        solveLevel1();
    }

    static void solveLevel1() {
        File[] levelFiles = new File("level1").listFiles();
        if (levelFiles != null) {
            for (File f: levelFiles) {
                if (f.getName().split("\\.")[1].equals("in")) {
                    try (FileWriter fW = new FileWriter("level1/" + f.getName().split("\\.")[0] + ".out")) {
                        char[][] board = genBoardMatrix(f);
                        fW.write(String.valueOf(countCoins(board)));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    static int countCoins(char[][] board) throws IOException {
        int c = 0;

        for (char[] row: board) {
            for (char field : row) {
                if (field == 'C') c++;
            }
        }

        return c;
    }

    static char[][] genBoardMatrix(File f) throws IOException {
        BufferedReader bR = new BufferedReader(new FileReader(f));
        int n = Integer.parseInt(bR.readLine());

        String line;
        char[][] boardMatrix = new char[n][n];
        int i = 0;

        while ((line = bR.readLine()) != null) {
            boardMatrix[i] = line.toCharArray();
            i++;
        }

        bR.close();

        return boardMatrix;
    }
}
