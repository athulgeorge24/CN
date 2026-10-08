import java.io.*;
import java.net.*;

public class ConcurrentFileServer {

    private static final int PORT = 5000;

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {

            // Get the server's Process ID
            long pid = ProcessHandle.current().pid();

            System.out.println("Concurrent File Server Started...");
            System.out.println("Server PID: " + pid);
            System.out.println("Listening on port " + PORT);

            while (true) {

                // Wait for a client
                Socket clientSocket = serverSocket.accept();

                System.out.println(
                    "New client connected: "
                    + clientSocket.getInetAddress()
                );

                // Create a separate thread for the client
                new Thread(
                    new ClientHandler(clientSocket, pid)
                ).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


// Handles one client
class ClientHandler implements Runnable {

    private Socket clientSocket;
    private long serverPid;

    public ClientHandler(Socket socket, long pid) {
        this.clientSocket = socket;
        this.serverPid = pid;
    }

    @Override
    public void run() {

        try (
            BufferedReader in =
                new BufferedReader(
                    new InputStreamReader(
                        clientSocket.getInputStream()
                    )
                );

            PrintWriter out =
                new PrintWriter(
                    clientSocket.getOutputStream(),
                    true
                )
        ) {

            // Receive filename from client
            String fileName = in.readLine();

            System.out.println(
                "[" + Thread.currentThread().getId()
                + "] Requested file: " + fileName
            );

            File file = new File(fileName);

            // Send PID and Thread ID
            out.println("Server PID: " + serverPid);

            out.println(
                "Servicing Thread ID: "
                + Thread.currentThread().getId()
            );

            // Check whether file exists
            if (file.exists() && file.isFile()) {

                out.println("--- FILE FOUND ---");

                BufferedReader fileReader =
                    new BufferedReader(
                        new FileReader(file)
                    );

                String line;

                // Send file contents
                while ((line = fileReader.readLine()) != null) {
                    out.println(line);
                }

                fileReader.close();

            } else {

                // File does not exist
                out.println(
                    "--- ERROR: File '"
                    + fileName
                    + "' does not exist on server. ---"
                );
            }

            // Tell client that response is complete
            out.println("EOF");

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {
                clientSocket.close();
            } catch (IOException e) {
                // Ignore closing error
            }
        }
    }
}

