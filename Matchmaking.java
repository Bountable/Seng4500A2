import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class Matchmaking {

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Usage: java Program <broadcastAddress> <broadcastPort> <server|client>");
            return;
        }

        InetAddress broadcastAddress = InetAddress.getByName(args[0]);
        int broadcastPort = Integer.parseInt(args[1]);
        String role = args[2];
        int tcpPort = broadcastPort + 1;

        DatagramSocket datagramSocket = new DatagramSocket();
        datagramSocket.setBroadcast(true);

        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        byte[] receiveData = new byte[1024];
        boolean gameFound = false;

        if (role.equals("server")) {
            // Server logic
            while (!gameFound) {
                System.out.println("Listening for [NEW GAME].....");
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                datagramSocket.setSoTimeout(30000);

                try {
                    datagramSocket.receive(receivePacket);
                    String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    System.out.println("Received message: " + receivedMessage);

                    if (receivedMessage.equals("NEW GAME")) {
                        System.out.println("Match found. Connecting to player...");

                        InetAddress player1Address = receivePacket.getAddress();
                        clientSocket = new Socket(player1Address, tcpPort);
                        System.out.println("Connected to Player 1 via TCP: " + player1Address + " :" + tcpPort);

                        gameFound = true;
                    }

                } catch (SocketTimeoutException e) {
                    System.out.println("No 'NEW GAME' message received within 30 seconds. Sending out [NEW GAME]...");

                    String newGameMessage = "NEW GAME";
                    byte[] sendData = newGameMessage.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcastAddress, broadcastPort);
                    datagramSocket.send(sendPacket);

                    System.out.println("[NEW GAME] message broadcasted. Waiting for others to connect...");

                    try {
                        serverSocket = new ServerSocket(tcpPort); 
                        serverSocket.setSoTimeout(30000);

                        clientSocket = serverSocket.accept();
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

        } else if (role.equals("client")) {
            // Client logic
            System.out.println("Sending [NEW GAME] to the network...");
            String newGameMessage = "NEW GAME";
            byte[] sendData = newGameMessage.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcastAddress, broadcastPort);
            datagramSocket.send(sendPacket);

            System.out.println("Waiting for server to accept the connection...");

            try {
                serverSocket = new ServerSocket(tcpPort);
                serverSocket.setSoTimeout(30000);
                clientSocket = serverSocket.accept();
                System.out.println("Connected to server via TCP!");

            } catch (SocketTimeoutException ex) {
                System.out.println("Failed to connect to server within 30 seconds.");
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

        System.out.println("Game setup complete. Ready to start the game.");
    }

}
