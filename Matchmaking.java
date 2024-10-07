import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

public class Matchmaking {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: java Program <broadcastAddress> <broadcastPort>");
            return;
        }

        // Broadcast declarations
        InetAddress broadcastAddress = InetAddress.getByName(args[0]);
        int broadcastPort = Integer.parseInt(args[1]);
        int tcpPort = generateRandomPort();

        DatagramSocket datagramSocket = new DatagramSocket();
        datagramSocket.setBroadcast(true);

        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        byte[] receiveData = new byte[1024];
        boolean gameFound = false;

        while (!gameFound) {
            System.out.println("Listening for [NEW GAME].....");
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            datagramSocket.setSoTimeout(30000); // Listen for 30 seconds

            try {
                // Try to receive a "NEW GAME" message from the network
                datagramSocket.receive(receivePacket);
                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received message: " + receivedMessage);

                if (receivedMessage.equals("NEW GAME")) {
                    System.out.println("Match found. Connecting to player...");

                    // Connect to the sender of the message via TCP (Player 2 connects to Player 1)
                    InetAddress player1Address = receivePacket.getAddress();
                    clientSocket = new Socket(player1Address, tcpPort);
                    System.out.println("Connected to Player 1 via TCP: " + player1Address + " :" + tcpPort);

                    gameFound = true;
                }

            } catch (SocketTimeoutException e) {
                // No 'NEW GAME' message received within 30 seconds
                System.out.println("No 'NEW GAME' message received within 30 seconds. Sending out [NEW GAME]...");

                // Send "NEW GAME" message if no game was found within the timeout period
                String newGameMessage = "NEW GAME";
                byte[] sendData = newGameMessage.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcastAddress, broadcastPort);
                datagramSocket.send(sendPacket);

                System.out.println("[NEW GAME] message broadcasted. Waiting for others to connect...");

                try {
                    // Wait for another player to connect as Player 1
                    serverSocket = new ServerSocket(tcpPort); // Use tcpPort for the server
                    serverSocket.setSoTimeout(30000); // Wait 30 seconds for a connection

                    clientSocket = serverSocket.accept(); // Accept connection from Player 2
                    System.out.println("Player connected: " + clientSocket.getInetAddress());

                    gameFound = true;

                } catch (SocketTimeoutException ex) {
                    System.out.println("No player connected within 30 seconds.");
                    if (serverSocket != null && !serverSocket.isClosed()) {
                        serverSocket.close();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Close sockets
        if (datagramSocket != null && !datagramSocket.isClosed()) {
            datagramSocket.close();
        }

        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }

        if (clientSocket != null && !clientSocket.isClosed()) {
            clientSocket.close();
        }

        // Proceed with the game logic here
        System.out.println("Game setup complete. Ready to start the game.");
    }

     private static int generateRandomPort() {
        Random rand = new Random();
        int MIN_PORT = 5000;
        int MAX_PORT = 6000;
        return rand.nextInt(MAX_PORT - MIN_PORT) + MIN_PORT;
    }
}
