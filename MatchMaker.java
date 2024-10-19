import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

public class MatchMaker {

  
    private static  int BROADCAST_PORT = 50000; // UDP port for broadcasting
    private static final int[] TCP_PORT_RANGE = {9000, 9100}; // TCP port range
    private static  String BROADCAST_ADDRESS = "";


    public static void main(String[] args) {
        // if (args.length < 2) {
        //     System.out.println("Usage: java Matchmaker <broadcastAddress> <broadcastPort>");
        //     return;
        // }
    
        // try {

        //     BROADCAST_ADDRESS = args[0];
        //     BROADCAST_PORT = Integer.parseInt(args[1]);
        //     connect();
        // } catch (NumberFormatException e) {
        //     System.err.println("Invalid port number. Please enter a valid integer for the broadcast port.");
        // } catch (IOException e) {
        //     System.err.println("IO Exception occurred: " + e.getMessage());
        //     e.printStackTrace();
        //}
    }
    

    public static void connect(String address, int port) throws IOException{
        int timeoutSeconds = 5;

        // Start listening for game invitations
        if (!listenForNewGame(address, port, timeoutSeconds)) {
            // If no invitations were received, broadcast a new game
            broadcastNewGame(address, port);
        }
        
    }

    public static boolean listenForNewGame(String broadcastAddress, int port, int timeoutSeconds) throws IOException {
        DatagramSocket socket = new DatagramSocket(port);
        socket.setBroadcast(true);
        socket.setSoTimeout(timeoutSeconds * 1000); // Set the timeout to 30 seconds
        
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        System.out.println("Listening for 'NEW GAME' messages...");

        try {
            socket.receive(packet);
            String message = new String(packet.getData(), 0, packet.getLength());
            if (message.startsWith("NEW GAME:")) {
                String[] parts = message.split(":");
                int tcpPort = Integer.parseInt(parts[1]);
                System.out.println("Received 'NEW GAME' message from " + packet.getAddress() + " on port " + tcpPort);
                
                // Connect to Player 1 using TCP
                connectToPlayer(packet.getAddress(), tcpPort);
                return true;
            }
        } catch (SocketTimeoutException e) {
            System.out.println("No 'NEW GAME' messages received within the timeout.");
        } finally {
            socket.close();
        }

        return false;
    }

    public static void broadcastNewGame(String broadcastAddress, int port) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.setBroadcast(true);

        Random random = new Random();
        int tcpPort = TCP_PORT_RANGE[0] + random.nextInt(TCP_PORT_RANGE[1] - TCP_PORT_RANGE[0] + 1);
        String newGameMessage = "NEW GAME:" + tcpPort;
        
        byte[] buffer = newGameMessage.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(broadcastAddress), port);
        
        // Send broadcast
        socket.send(packet);
        System.out.println("Broadcasted 'NEW GAME' message on port " + tcpPort);

        socket.close();

        // Now start the TCP server to wait for the connection
        waitForPlayerConnection(tcpPort);
    }

    public static void connectToPlayer(InetAddress address, int tcpPort) throws IOException {
        Socket socket = new Socket(address, tcpPort);
        System.out.println("Connected to Player 1 on TCP port " + tcpPort);
    
        // Start the game manager for Player 2
        GameManager gameManager = new GameManager(socket, 'O');
        gameManager.playGame();  // Keep playing until the game is done
    }
    
 
    public static void waitForPlayerConnection(int tcpPort) throws IOException {
        ServerSocket serverSocket = new ServerSocket(tcpPort);
        System.out.println("Waiting for Player 2 to connect on TCP port " + tcpPort);
    
        Socket player2Socket = serverSocket.accept();
        System.out.println("Player 2 connected!");
    
        GameManager gameManager = new GameManager(player2Socket, 'X');
        try {
            gameManager.playGame();  // Keep playing until the game is done
        } catch (Exception e) {
            System.err.println("Error during game play: " + e.getMessage());
            e.printStackTrace();
        } finally {
            player2Socket.close();
            serverSocket.close();
        }
    }
}