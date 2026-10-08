import java.net.*;

public class TimeClient {

    public static void main(String[] args) throws Exception {

        // Create UDP socket
        DatagramSocket socket = new DatagramSocket();

        // Prepare request message
        byte[] data = "TIME".getBytes();

        // Find the server
        InetAddress server =
            InetAddress.getByName("localhost");

        // Create request packet
        DatagramPacket request =
            new DatagramPacket(
                data,
                data.length,
                server,
                5000
            );

        // Send request to server
        socket.send(request);

        System.out.println("Time request sent to server.");

        // Buffer to receive server response
        byte[] buffer = new byte[100];

        // Create response packet
        DatagramPacket response =
            new DatagramPacket(
                buffer,
                buffer.length
            );

        // Wait for server response
        socket.receive(response);

        // Convert received bytes to String
        String time = new String(
            response.getData(),
            0,
            response.getLength()
        );

        // Display server time
        System.out.println("Server Time: " + time);

        // Close socket
        socket.close();
    }
}

