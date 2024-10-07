// Java program to illustrate Client side 
// Implementation using DatagramSocket 
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner; 



public class Program {
    
    public static void main(String[] args) throws IOException
    {

        int port = 4000;
        
        Scanner scanner = new Scanner(System.in); 
        
        //broadcast declarations
        InetAddress broadcastAddress = InetAddress.getByName(args[0]);
        int broadcastPort = Integer.parseInt(args[1]);

        //UDP
        DatagramSocket datagramSocket = new DatagramSocket();
        datagramSocket.setBroadcast(true);

        //TCP 
        ServerSocket serverSocket = null;
        Socket clientSocket = null;

        byte[] receiveData = new byte[1024];
        boolean gameFound = false;






        
    }



    
}

