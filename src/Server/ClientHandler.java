package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {

    Socket socket;
    Server server;

    DataInputStream in;
    DataOutputStream out;

    public ClientHandler(Socket socket, Server server) {

        try {
            this.socket = socket;
            this.server = server;
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Client connected" + socket.getRemoteSocketAddress());

            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/end")) {
                            sendMessage("/end");
                            break;
                        }
                        server.broadcastMsg(str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    server.unsubscribe(this);
                    System.out.println("Client disconnected" + socket.getRemoteSocketAddress());
                    try {
                        socket.close();
                        in.close();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage (String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
