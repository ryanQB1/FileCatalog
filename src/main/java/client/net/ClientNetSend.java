package client.net;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;



public class ClientNetSend implements Runnable{
    
    private String pathTo;
    
    private OutputStream os;
    private Socket sock;
    
    public void setup(Socket sock){
        try {
            this.sock=sock;
            this.os = sock.getOutputStream();
        } catch (IOException ex) {
            System.err.println("Error during clientnetsend setup");
        }
    }
    
    public void start(String pathTo){
        this.pathTo = pathTo;
        new Thread(this).start();
    }
    
    @Override
    public void run(){
        try{
            File toSend = new File(pathTo);
            byte[] fileContent = new byte[(int) toSend.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(toSend));
            bis.read(fileContent, 0, fileContent.length);
            os.write(fileContent, 0, fileContent.length);
            os.flush();
            sock.close();
        }catch(IOException e){
            
        }
    }
    
}
