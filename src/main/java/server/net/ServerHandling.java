package server.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import server.model.FileHandle;



public class ServerHandling implements Runnable{
    private final String SERVERDIR = "server/";
    private final ServerSocket listeningSocket;
    private final int portNo;
    
    private final HashMap<String,InputStream> hmis = new HashMap<>();
    private final HashMap<String,PrintWriter> hmpw = new HashMap<>();
    
    private FileHandle toSned;
    private String toUser;
    
    private Socket sock1;
    private Socket sock2;
    
    public ServerHandling(int portNo)throws IOException{
        this.listeningSocket = new ServerSocket(portNo);
        this.portNo = portNo;
    }
    
    public void accept1(String username){
        try {
            sock1 = listeningSocket.accept();
            hmis.put(username, sock1.getInputStream());
        } catch (IOException ex) {
            System.err.println("Error during accepting socket");
        }
    }
    
    public void setFileToSend(FileHandle file, String toUs){
        toSned=file;
        toUser=toUs;
    }
    
    @Override
    public void run(){
        try {
            sendFile(toSned, toUser);
        } catch (IOException ex) {
            System.err.println("send help");
        }
    }
    
    public void sendFile(FileHandle file, String toUsr) throws IOException{
        File toSend = new File(SERVERDIR+file.getName());
        Socket sock = listeningSocket.accept();
        OutputStream os = sock.getOutputStream();
        byte[] fileContent = new byte[(int) toSend.length()];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(toSend));
        bis.read(fileContent, 0, fileContent.length);
        os.write(fileContent, 0, fileContent.length);
        os.flush();
        sock.close();
    }
    
    public FileHandle recvFile(String pathToFile, String fileName, String fromUser, long fileLength) throws IOException{
        InputStream is;
        if(!hmis.containsKey(fromUser)){
            throw new IOException("HashMap does not contain the correct stuff");
        }
        else{
            is=hmis.get(fromUser);
        }
        byte[] mybytearray = new byte[(int) fileLength];
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(SERVERDIR + fileName));
        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.close();
        FileHandle temp =  new FileHandle(fileName, fileLength, false, false, fromUser, false);
        sock1.close();
        return temp;
    }
    
    public void accept2(String username) {
        try {
            sock2 = listeningSocket.accept();
            hmpw.put(username, new PrintWriter(sock2.getOutputStream()));
        } catch (IOException ex) {
            System.err.println("Error during accepting for nots");
        }
        
    }
    
    
    
    public void update2(String username){
        try {
            if(betterContains(username)){
                sock2.close();
                betterRemove(username);
            }
        } catch (IOException ex) {
            System.err.println("Error during updating for nots");
        }
    }
    
    public void notifyClient(String cause, String client) throws IOException{
        PrintWriter toClient;
        if(!hmpw.containsKey(client)){
            throw new IOException("HashMap does not contain the correct stuff");
        }
        else{
            toClient=hmpw.get(client);
        }
        toClient.println(cause);
        toClient.flush();
    }
    
    private boolean betterContains(String username){
        return hmpw.keySet().stream().anyMatch((str) -> (str.equals(username)));
    }
    
    private void betterRemove(String username){
        hmpw.keySet().stream().filter((str) -> (str.equals(username))).forEachOrdered((str) -> {
            hmpw.remove(str);
        });
    }
}
