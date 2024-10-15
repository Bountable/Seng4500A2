import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class GameManager {

    private final int ROWS = 6;
    private final int COLUMNS = 7;
    private final char[][] board = new char[ROWS][COLUMNS];

    private char currentPlayer = 'X'; // Player 1 is 'X', Player 2 is 'O'

    private BufferedReader in;
    private PrintWriter out;

    private Scanner scanner;

    private Socket socket;


    public GameManager(Socket socket, char currentPlayer) {
        this.socket = socket;
        this.currentPlayer = currentPlayer;

        // Initialize the board with empty slots
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                board[i][j] = '.';
            }
        }
        this.scanner = new Scanner(System.in);

    }

    public void playGame(Socket socket) throws Exception {    
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

    
        boolean gameDone  = false;
        while(gameDone == false){
            if(currentPlayer == 'X'){ //player 1 turn
                playTurn('X');



            }

        }
           

    }


    private void playTurn(char c) {
        System.out.print("Your turn. Enter column (1-7): ");
        int column;
        column = Integer.parseInt(this.scanner.nextLine());
        System.out.println("Player Somehing placed" + column);
        this.out.print("Sent out " + column);

        
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

    private void BoardState(){


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
