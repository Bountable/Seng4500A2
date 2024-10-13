import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

public class Matchmaker {

  
    private static final int BROADCAST_PORT = 50000; // UDP port for broadcasting
    private static final int[] TCP_PORT_RANGE = {9000, 9100}; // TCP port range

    public static void connect() throws IOException{
        String broadcastAddress = "192.168.1.255"; // Replace with actual network broadcast address
        int timeoutSeconds = 30;

        // Start listening for game invitations
        if (!listenForNewGame(broadcastAddress, BROADCAST_PORT, timeoutSeconds)) {
            // If no invitations were received, broadcast a new game
            broadcastNewGame(broadcastAddress, BROADCAST_PORT);
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
        // Connect to Player 1 via TCP
        Socket socket = new Socket(address, tcpPort);
        System.out.println("Connected to Player 1 on TCP port " + tcpPort);

        // Start exchanging game data (omitted here for simplicity)
        socket.close();
    }

    public static void waitForPlayerConnection(int tcpPort) throws IOException {
        ServerSocket serverSocket = new ServerSocket(tcpPort);
        System.out.println("Waiting for Player 2 to connect on TCP port " + tcpPort);

        Socket player2Socket = serverSocket.accept();
        System.out.println("Player 2 connected!");

        // Start exchanging game data (omitted here for simplicity)
        Connect4Game connect4game = new Connect4Game();
        connect4game.play();

        player2Socket.close();
        serverSocket.close();
    }
}
