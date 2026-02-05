import java.net.*;
import java.io.*;
import java.util.Date;

public class DateServer {

    public static void main(String[] args) {

        int port = 6013;

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started...");
            System.out.println("Waiting for client on port " + port);

            while (true) {

                // Step 1: Client connects
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected");

                // Step 2: Send data to client
                PrintWriter out = new PrintWriter(
                        clientSocket.getOutputStream(), true);

                out.println("Server Date & Time:");
                out.println(new Date().toString());

                // Step 3: Close client connection
                clientSocket.close();
                System.out.println("Client disconnected\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
