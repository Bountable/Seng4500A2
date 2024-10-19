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

    private char localPlayer;  // Store the role of this player ('X' or 'O')
    private char currentPlayer;  // Keep track of whose turn it is

    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;
    private Socket socket;

    public GameManager(Socket socket, char localPlayer) {
        this.socket = socket;
        this.localPlayer = localPlayer;  // Set the role for this player
        this.currentPlayer = 'X';  // 'X' starts first
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
                if (currentPlayer == localPlayer) {
                    // If it's this player's turn
                    playTurn(localPlayer);
                } else {
                    // If it's the other player's turn, wait for their move
                    System.out.println("Waiting for the other player's move...");
                    receiveTurn();
                }

                drawCurrentBoard();

                if (checkWin()) {
                    System.out.println("Player " + currentPlayer + " wins!");
                    gameDone = true;
                    out.print("Player " + currentPlayer + " wins!");
                    break;
                }

                switchTurn();  // Switch to the next player
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

        while (!validMove) {
            System.out.print("Your turn. Enter column (1-7): ");
            column = Integer.parseInt(scanner.nextLine()) - 1;  // Convert to 0-based index

            if (insertToken(player, column)) {
                validMove = true;
                out.println(column);  // Send the move to the other player
                out.flush();
                System.out.println("Sent column " + (column + 1) + " to the other player.");
            } else {
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
            return false;  // Invalid column number
        }

        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][column] == '.') {
                board[row][column] = player;
                return true;
            }
        }
        return false;  // Column is full
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

    private void switchTurn() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    private boolean checkWin() {
        return checkTopWin() || checkSideWin() || checkDiagonalWin();
    }

    public boolean checkTopWin() {
        for (int row = 0; row < ROWS; row++) {
            int countX = 0, countO = 0;
            for (int col = 0; col < COLUMNS; col++) {
                if (board[row][col] == 'X') {
                    countX++;
                    countO = 0;
                } else if (board[row][col] == 'O') {
                    countO++;
                    countX = 0;
                } else {
                    countX = countO = 0;
                }

                if (countX == 4) return true;  // 'X' wins
                if (countO == 4) return true;  // 'O' wins
            }
        }
        return false;
    }

    private boolean checkSideWin() {
        for (int col = 0; col < COLUMNS; col++) {
            int countX = 0, countO = 0;
            for (int row = 0; row < ROWS; row++) {
                if (board[row][col] == 'X') {
                    countX++;
                    countO = 0;
                } else if (board[row][col] == 'O') {
                    countO++;
                    countX = 0;
                } else {
                    countX = countO = 0;
                }

                if (countX == 4) return true;  // 'X' wins
                if (countO == 4) return true;  // 'O' wins
            }
        }
        return false;
    }


    private boolean checkDiagonalWin(){
        // Check ↗ Directions 
        for (int row = 3; row < 6; row++) {
            for (int col = 0; col < 4; col++){
               
                if(board[row][col] == 'X' && board[row-1][col+1] == 'X' && board[row-2][col+2] == 'X' && board[row-3][col+3] == 'X' ){
                    return true;
                }
                if(board[row][col] == 'O' && board[row-1][col+1] == 'O' && board[row-2][col+2] == 'O' && board[row-3][col+3] == 'O' ){
                    return true;
                }
            }
        }


        //Check ↖ Directions
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 4; col++){
               
                if(board[row][col] == 'X' && board[row+1][col+1] == 'X' && board[row+2][col+2] == 'X' && board[row+3][col+3] == 'X' ){
                    return true;
                }
                if(board[row][col] == 'O' && board[row+1][col+1] == 'O' && board[row+2][col+2] == 'O' && board[row+3][col+3] == 'O' ){
                    return true;
                }
            }
        }
        return false;







    }
}