package broker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

class Client {

    //socket per contattare il broker
    private final Socket socket;
    //server per ricevere messaggi dal broker
    private ServerSocket server;

    public Client(
            InetAddress ipAddress,
            InetAddress serverAddress,
            int serverPort
    ) throws Exception {
        this.socket = new Socket(serverAddress, serverPort);
        if (ipAddress != null) {
            this.server = new ServerSocket(0, 1, ipAddress);
        } else {
            this.server = new ServerSocket(0, 1, InetAddress.getLocalHost());
        }
    }

    public void start() {
        String input = "subscribe " + 
                server.getInetAddress().toString() + 
                ":" + server.getLocalPort()
                + "nometopic";
        
        try (PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true)) {
            out.println(input);
            out.flush();
        } catch (IOException ex) {
        }

    }

    public void callAfterNotify(String topic) {
        System.out.println(server.getInetAddress() + " received notify about " + topic);
    }

    private void listen() {
        while (true) {
            try {
                String data;
                Socket c = server.accept();
                String clientAddress = c.getInetAddress().toString();
                System.out.println("\r\nNew connection from " + clientAddress);

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(c.getInputStream()));
                while ((data = in.readLine()) != null) {
                    System.out.println("\r\nMessage from " + clientAddress + ": " + data);
                }

            } catch (IOException ex) {
            }
        }
    }
    
    public static void main(String[] args) throws Exception {
        args = new String[]{
            "127.0.0.3",
            "127.0.0.20",
            "3000"
        };

        Client client = new Client(
                InetAddress.getByName(args[0]),
                InetAddress.getByName(args[1]),
                Integer.parseInt(args[2])
        );

        System.out.println("\r\nConnected to Server: " + client.socket.getInetAddress());
        client.start();
        client.listen();
    }
}
