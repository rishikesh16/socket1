 import java.net.*;
import java.io.*;
import java.util.*;

public class ChatServer {

    private static final int PORT = 6013;

    // Store unique client names
    private static final Set<String> names = new HashSet<>();

    // Store client output streams so we can broadcast messages
    private static final Set<PrintWriter> writers = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("ChatServer started on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket).start(); // handle each client in a new thread
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler extends Thread {
        private Socket socket;
        private String name;
        private BufferedReader in;
        private PrintWriter out;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // 1) Ask for name (no duplicates)
                while (true) {
                    out.println("ENTER_NAME:");
                    String proposed = in.readLine();
                    if (proposed == null) return;

                    proposed = proposed.trim();
                    if (proposed.isEmpty()) {
                        out.println("NAME_INVALID");
                        continue;
                    }

                    synchronized (names) {
                        if (!names.contains(proposed)) {
                            names.add(proposed);
                            name = proposed;
                            break;
                        }
                    }
                    out.println("NAME_TAKEN");
                }

                // Store client writer
                synchronized (writers) {
                    writers.add(out);
                }

                System.out.println(name + " joined.");
                broadcast("[SERVER] " + name + " joined the chat.");

                // 2) Read messages and display on server + broadcast to others
                String msg;
                while ((msg = in.readLine()) != null) {
                    msg = msg.trim();
                    if (msg.equalsIgnoreCase("/quit")) break;

                    System.out.println(name + ": " + msg);          // server displays messages
                    broadcast(name + ": " + msg);                   // send to all clients
                }

            } catch (IOException e) {
                System.out.println("Connection error with client.");
            } finally {
                // cleanup
                if (name != null) {
                    synchronized (names) {
                        names.remove(name);
                    }
                    broadcast("[SERVER] " + name + " left the chat.");
                    System.out.println(name + " disconnected.");
                }

                synchronized (writers) {
                    writers.remove(out);
                }

                try { socket.close(); } catch (IOException ignored) {}
            }
        }

        private void broadcast(String message) {
            synchronized (writers) {
                for (PrintWriter w : writers) {
                    w.println(message);
                }
            }
        }
    }
}