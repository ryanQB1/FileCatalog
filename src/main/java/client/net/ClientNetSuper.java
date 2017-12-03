package client.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;


public class ClientNetSuper {
    
    private final int TIMEOUT_HALF_HOUR = 1800000;
    private final int TIMEOUT_ONE_SECOND = 10000;
    
    private final String host;
    private final int portNo;
    private int amnt;
    
    public ClientNetSuper(String host, int portNo){
        this.host=host;
        this.portNo=portNo;
    }
    
    public void send(ClientNetSend clientnetsend){
        try {
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress(host, portNo), TIMEOUT_ONE_SECOND);
            sock.setSoTimeout(TIMEOUT_HALF_HOUR);
            clientnetsend.setup(sock);
        } catch (IOException ex) {
            System.err.println("Error during send");
        }
    }
    
    public void recv(ClientNetReciever clientnetrecv) {
        try {
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress(host, portNo), TIMEOUT_ONE_SECOND);
            sock.setSoTimeout(TIMEOUT_HALF_HOUR);
            clientnetrecv.setup(sock);
        } catch (IOException ex) {
            System.err.println("Error during recv");
        }
    }
    
    public void notif(ClientNetNot clientnetnot, OutputHandler outhandle){
        try {
            Socket sock2 = new Socket();
            sock2.connect(new InetSocketAddress(host, portNo), TIMEOUT_ONE_SECOND);
            sock2.setSoTimeout(TIMEOUT_HALF_HOUR);
            clientnetnot.setup(outhandle, sock2);
        } catch (IOException ex) {
            System.err.println("Error during connect1");
        }
    }
    
}
