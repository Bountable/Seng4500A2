import java.io.IOException;


//Entry Point
public class Connect4Game {

      
    private static  int BROADCAST_PORT = 0; // UDP port for broadcasting
    private static final int[] TCP_PORT_RANGE = {9000, 9100}; // TCP port range
    private static  String BROADCAST_ADDRESS = "";

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Matchmaker <broadcastAddress> <broadcastPort>");
            return;
        }
    
        try {
            BROADCAST_ADDRESS = args[0];
            BROADCAST_PORT = Integer.parseInt(args[1]);
            MatchMaker.connect(BROADCAST_ADDRESS, BROADCAST_PORT);
            
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number. Please enter a valid integer for the broadcast port.");
        } catch (IOException e) {
            System.err.println("IO Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
        
    }




    
    
}
