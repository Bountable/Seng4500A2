import java.io.BufferedReader;
import java.io.IOException;
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

    public void playGame(Socket socket) {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
    
            boolean gameDone = false;
            drawCurrentBoard(); // Show the initial board
    
            while (!gameDone) {
                if (currentPlayer == 'X') {
                    playTurn('X');   // Player 1's turn
                    receiveTurn();   // Wait for Player 2's response
                } else {
                    System.out.println("Waiting for Player 2's move...");
                    receiveTurn();   // Wait for Player 2's move
                    playTurn('O');   // Now Player 1 plays again
                }
    
                drawCurrentBoard(); // Update the board after each move
                switchPlayer();     // Switch turns
    
                // TODO: Add game win logic and set gameDone = true if someone wins
            }
        } catch (IOException e) {
            System.err.println("Error during game communication: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Failed to close socket: " + e.getMessage());
            }
        }
    }
    
    private void playTurn(char c) {
        System.out.print("Your turn. Enter column (1-7): ");
        int column = Integer.parseInt(scanner.nextLine());
        
        // Send the move to the other player
        out.println(column);
        out.flush();  // Ensure the message is sent immediately
        
        System.out.println("Sent column " + (column + 1) + " to the other player.");

        
    }

    private void receiveTurn() throws IOException {
        String receivedMessage = in.readLine(); // Read the message from the other player
        int column = Integer.parseInt(receivedMessage);
        System.out.println("Received move: Player placed token in column " + (column + 1));

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
