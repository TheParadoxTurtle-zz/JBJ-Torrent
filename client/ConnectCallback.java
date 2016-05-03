import java.net.*;

public class ConnectCallback implements iCallBack {
    Socket connSocket;
    ClientNode node;
    Neighbor neighbor;
    String fileName;
    boolean received;

    public ConnectCallback(Socket connSocket, ClientNode node, Neighbor neighbor,
            String fileName) {
        this.connSocket = connSocket;
        this.node = node;
        this.neighbor = neighbor;
        this.fileName = fileName;
        receieved = false;
    }

    public void stop() {
        received = true;
    }

    public void call() {
        try {
            if(!received) {
                connSocket.close();
                node.connect(neighbor, fileName);
            }
        } catch (Exception e) {

        }
    }
}
