package client.net;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;



public class ClientNetReciever implements Runnable{
    
    private int portNo;
    private String host;
    private String fileName;
    private long length;
    
    private final String storePath = "client/";
    
    private InputStream is;
    private Socket sock;
    
    public void start(String fileName, long length){
        this.fileName=fileName;
        this.length=length;
        new Thread(this).start();
    }
    
    public void setup(Socket sock){
        try {
            this.is = sock.getInputStream();
            this.sock=sock;
        } catch (IOException ex) {
            System.err.println("Error during clientnetrecv setup");
        }
    }
    
    @Override
    public void run(){
        try{
            byte[] fileBytes = new byte[(int) length];
            FileOutputStream fos = new FileOutputStream(storePath + fileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int bytesRead = is.read(fileBytes, 0, fileBytes.length);
            bos.write(fileBytes, 0, bytesRead);
            sock.close();
        }catch(IOException e){
            
        }
    }
    
}
