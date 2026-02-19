import java.net.*;
import java.io.*;

public class ChatClient {

    public static void main(String[] args) {
        String serverIP = "127.0.0.1";  // same laptop
        int port = 6013;

        try {
            Socket socket = new Socket(serverIP, port);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

            // Thread to keep printing anything coming from server
            Thread reader = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException ignored) {}
            });
            reader.start();

            // Handshake: server asks for name
            while (true) {
                String serverMsg = in.readLine();
                if (serverMsg == null) return;

                if (serverMsg.equals("ENTER_NAME:")) {
                    System.out.print("Enter your name: ");
                    String name = keyboard.readLine();
                    out.println(name);
                } else if (serverMsg.equals("NAME_TAKEN")) {
                    System.out.println("That name is already taken. Try another.");
                } else if (serverMsg.equals("NAME_INVALID")) {
                    System.out.println("Invalid name. Try again.");
                } else {
                    // first non-handshake message means we’re in chat
                    break;
                }
            }

            // Send messages
            System.out.println("Type messages. Type /quit to exit.");
            while (true) {
                String msg = keyboard.readLine();
                if (msg == null) break;
                out.println(msg);
                if (msg.equalsIgnoreCase("/quit")) break;
            }

            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}