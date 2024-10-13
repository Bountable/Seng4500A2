import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameManager {

    private final int rows = 6;
    private final int columns = 7;
    private final char[][] board = new char[rows][columns];
    private char currentPlayer = 'X'; // Player 1 is 'X', Player 2 is 'O'


    public GameManager() {
        // Initialize the board with empty slots
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                board[i][j] = '.';
            }
        }
    }



    public static void Play(Socket socket) throws Exception {
            Matchmaker.connect();//might cuase problems

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

    }


    private void drawCurrentBoard() {

    }



    // private boolean insertToken(int column) {

    // }

    // public boolean checkHorizontalWin(){

    // }

    // public boolean checkVerticalWin(){

    // }

    // public boolean checkDiagonalWin(){

    // }

   
    
}

