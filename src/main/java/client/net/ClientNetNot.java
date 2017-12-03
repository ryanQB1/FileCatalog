package client.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientNetNot implements Runnable{
    
    private boolean recving = false;
    
    private OutputHandler outputHandler;
    private BufferedReader br;
    private Socket sock2;
    
    public void setup(OutputHandler outputHandler, Socket sock2){
        try {
            this.outputHandler=outputHandler;
            this.br = new BufferedReader(new InputStreamReader(sock2.getInputStream()));
            this.sock2=sock2;
        } catch (IOException ex) {
            System.err.println("error during clientnetnot setup");
        }
    }
    
    public void setOnOff(boolean recving){
        this.recving=recving;
    }
    
    public void start(){
        new Thread(this).start();
    }
    
    @Override
    public void run(){
        while(recving){
        try{
            String notification = br.readLine();
            outputHandler.printmsg(notification);
        }catch(IOException e){
            System.err.println("Error during recieving notification");
        }
        }
        try {
            sock2.close();
        } catch (IOException ex) {
            System.err.println("Couldnt close socket during clientnetnot");
        }
    }
    
}
