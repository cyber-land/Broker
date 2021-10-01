package broker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;

public class Broker {

    private ServerSocket server;
    private HashMap<String, LinkedList<Socket>> hm;

    public Broker(String ipAddress) throws Exception {
        if (ipAddress != null && !ipAddress.isEmpty()) {
            this.server = new ServerSocket(3000, 1, InetAddress.getByName(ipAddress));
        } else {
            this.server = new ServerSocket(0, 1, InetAddress.getLocalHost());
        }
        this.hm = new HashMap();
    }

    public void listen() throws Exception {
        String data;
        Socket client = this.server.accept();
        String clientAddress = client.getInetAddress().toString();
        System.out.println("\r\nNew connection from " + clientAddress);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(client.getInputStream()));

        while ((data = in.readLine()) != null) {
            int firstIndex = data.indexOf(" ");
            int secondIndex = data.indexOf(" ", firstIndex);
            String address = data.substring(firstIndex, secondIndex);
            String topic = data.substring(secondIndex);
            Socket socket = new Socket(
                    address.substring(0, address.indexOf(":")),
                    Integer.valueOf(address.substring(address.indexOf(":") + 1))
            );
            if (data.substring(0, firstIndex).equals("subscribe")) {
                if (hm.containsKey(topic)) {
                    hm.get(topic).add(socket);
                }
            } else if (data.substring(0, firstIndex).equals("unsubscribe")) {
                if (hm.containsKey(topic)) {
                    hm.get(topic).remove(socket);
                }
            } else if (data.substring(0, firstIndex).equals("publish")) {
                if (!hm.containsKey(topic)) {
                    hm.put(topic, new LinkedList());
                }
            }
        }
    }

    public InetAddress getSocketAddress() {
        return this.server.getInetAddress();
    }

    public int getPort() {
        return this.server.getLocalPort();
    }

    public static void main(String[] args) throws Exception {
        args = new String[]{
            "127.0.0.20"
        };
        Broker app = new Broker(args[0]);

        System.out.println("\r\nRunning Server: "
                + "Host=" + app.getSocketAddress().getHostAddress()
                + " Port=" + app.getPort()
        );

        while (true) {
            app.listen();
        }
    }
}
