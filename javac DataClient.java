import java.net.*;
import java.io.*;

public class DateClient {

    public static void main(String[] args) {

        String serverIP = "127.0.0.1";
        int port = 6013;

        try {
            // Step 1: Connect to server
            Socket socket = new Socket(serverIP, port);
            System.out.println("Connected to server");

            // Step 2: Read data from server
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));

            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }

            // Step 3: Close connection
            socket.close();
            System.out.println("Connection closed");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
