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

    private char currentPlayer; // 'X' for Player 1, 'O' for Player 2

    private BufferedReader in;
    private PrintWriter out;

    private Scanner scanner;
    private Socket socket;

    public GameManager(Socket socket, char startingPlayer) {
        this.socket = socket;
        this.currentPlayer = startingPlayer;
        this.scanner = new Scanner(System.in);

        // Initialize the board with empty slots
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                board[i][j] = '.';
                
            }
        }
    }

    public void playGame() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            boolean gameDone = false;
            drawCurrentBoard();

            while (!gameDone) {
                if (currentPlayer == 'X') {  // Player 1's turn
                    playTurn('X');
                } else {  // Player 2's turn
                    System.out.println("Waiting for Player 2's move...");
                    receiveTurn();
                }

                drawCurrentBoard();
                if (checkWin()) {
                    System.out.println("Player " + currentPlayer + " wins!");
                    gameDone = true;
                }

                switchPlayer();  // Switch to the other player
            }
        } catch (IOException e) {
            System.err.println("Error during game communication: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Failed to close socket: " + e.getMessage());
            }
        }
    }

    private void playTurn(char player) {
        int column;
        boolean validMove = false;

        // Keep prompting the player until a valid move is made
        while (!validMove) {
            System.out.print("Your turn. Enter column (1-7): ");
            if(Integer.parseInt(scanner.nextLine())-1 < 0 || Integer.parseInt(scanner.nextLine())-1 > COLUMNS ){ //check valid move
                System.out.println("Invalid move. Try again.");
                column = Integer.parseInt(scanner.nextLine()) - 1; // Convert to 0-based index
                if (insertToken(player, column)) {
                    validMove = true;
                    out.println(column);  // Send the move to the other player
                    out.flush();
                    System.out.println("Sent column " + (column + 1) + " to the other player.");
                } 
            }
            else{
                System.out.println("Invalid move. Try again.");
            }
            
        }
    }

    private void receiveTurn() throws IOException {
        String receivedMessage = in.readLine();
        int column = Integer.parseInt(receivedMessage);

        System.out.println("Received move: Player placed token in column " + (column + 1));
        insertToken(currentPlayer, column);
    }

    private boolean insertToken(char player, int column) {
        if (column < 0 || column >= COLUMNS) {
            return false; // Invalid column number
        }

        // Find the first empty row in the selected column
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][column] == '.') {
                board[row][column] = player;  // Place the player's token
                return true;
            }
        }

        return false; // Column is full
    }

    private void drawCurrentBoard() {
        System.out.println(" 1 2 3 4 5 6 7");
        System.out.println("---------------");
        for (int row = 0; row < ROWS; row++) {
            System.out.print("|");
            for (int col = 0; col < COLUMNS; col++) {
                System.out.print(board[row][col]);
                System.out.print("|");
            }
            System.out.println();
            System.out.println("---------------");
        }
        System.out.println(" 1 2 3 4 5 6 7");
    }

    private void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    private boolean checkWin() {
        // TODO: Implement win condition (horizontal, vertical, diagonal check)
        /*
         * Check top side and diagnal
         * 
         * 
         */

         //check line 4 to if there is 4 match
         for(int i =0; i<ROWS;i++){

         }

        checkTopWin();
        checkSideWin();
        checkDiagonalWin();
                
         
        return false;

    }

    public int checkTopWin() {
        int countX = 0;
        int countO = 0;
        for(int x= 0; x<=ROWS; x++){ //check 
            for(int y=0;y<= COLUMNS; y++){
                if(board[x][y] == 'X'){ //check 4 matches for X
                    countX ++;
                }
                else countX = 0; //need 4 in a collumn to win

                if(board[x][y] == 'O'){ //check 4 matches for O
                    countO ++;

                }
                else countO=0;
            }
        }

        if(countX == 4) return  1; //X wins
        if(countO == 4) return 2; //O Wins
        return 0; //no win found
        
    
    }

    private boolean checkSideWin(){
        return false;
     

    }

    private boolean checkDiagonalWin(){
        return false;

    }
}
