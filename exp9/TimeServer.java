import java.net.*;

public class TimeServer {

    public static void main(String[] args) throws Exception {

        // Create UDP socket on port 5000
        DatagramSocket socket = new DatagramSocket(5000);

        System.out.println("Time Server is running...");
        System.out.println("Waiting for client requests...");

        while (true) {

            // Buffer to receive client request
            byte[] buffer = new byte[100];

            // Create packet to receive request
            DatagramPacket request =
                new DatagramPacket(buffer, buffer.length);

            // Wait for client request
            socket.receive(request);

            // Create a new thread for this client
            new Thread(() -> {

                try {

                    // Get current server date and time
                    String time = new java.util.Date().toString();

                    // Convert time into bytes
                    byte[] data = time.getBytes();

                    // Create response packet
                    DatagramPacket response =
                        new DatagramPacket(
                            data,
                            data.length,
                            request.getAddress(),
                            request.getPort()
                        );

                    // Send time to client
                    socket.send(response);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }).start();
        }
    }
}
