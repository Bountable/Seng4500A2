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

    public void playGame(Socket socket) throws Exception {    
        GameManager game = new GameManager();   
        game.drawCurrentBoard();
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
    }


    private void drawCurrentBoard() {
        System.out.println(" 0 1 2 3 4 5 6");
		System.out.println("---------------");
		for (int row = 0; row < board.length; row++){
			System.out.print("|");
			for (int col = 0; col < board[0].length; col++){
				System.out.print(board[row][col]);
				System.out.print("|");
			}
			System.out.println();
			System.out.println("---------------");
		}
		System.out.println(" 0 1 2 3 4 5 6");
		System.out.println();
        
    }

    private void switchPlayer(){
        if(currentPlayer == 'X') currentPlayer = 'O';
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
