package server.controller;

import Common.FileCatalogIf;
import Common.FileHandleif;
import java.io.IOException;
import server.integration.CatalogDAO;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.model.DeprUser;
import server.model.FileHandle;
import server.model.RejectedException;
import server.model.Usero;
import server.net.ServerHandling;



public class Controller extends UnicastRemoteObject implements FileCatalogIf{
    private CatalogDAO catalogDb;
    private ServerHandling servout;
    
    public Controller(int portNo) throws RemoteException {
        super();
        catalogDb = new CatalogDAO();
        try{
            this.servout = new ServerHandling(portNo);
        }
        catch(IOException e){
            System.err.println("Couldn't initialize the serversocket send help SOS");
        }
    }
    
    @Override
    public void accept1(String username){
        servout.accept1(username);
    }
    
    @Override
    public int login(String username, String password) throws RemoteException, RejectedException {
        if(!catalogDb.checkPassw(username, password)){
            throw new RejectedException("Login failed!");
        }
        int fin = 0;
        List<FileHandle> toCheck = catalogDb.findMyFiles(username, true);
        fin = toCheck.stream().filter((file) -> (file.getNotify())).map((_item) -> 1).reduce(fin, Integer::sum);
        return fin;
    }
    
    @Override
    public void update1(String username){
        servout.accept2(username);
        
    }
    
    @Override
    public void register(String username, String password) throws RemoteException, RejectedException{
        Usero temp1 = catalogDb.findUsero(username);
        if(temp1!=null) throw new RejectedException("Usero alreaday exists, pick an original name");
        Usero temp2 = new Usero(username, password);
        catalogDb.createNewUsero(temp2);
    }
    
    @Override
    public void sendNotif(String username) throws RemoteException, RejectedException {
        try {
            servout.notifyClient("Don't forget, you still have notifications running! Those will be reinitialized on login", username);
        } catch (IOException ex) { throw new RejectedException("Error during notification");}
        
    }
    
    @Override
    public void uploadFile(String username, String pathTo, long fileLength) throws RemoteException, RejectedException{
        String[] path = pathTo.split("/");
        String fileName = path[path.length-1];
        boolean existed = false;
        if(fileName==null || username == null) throw new RejectedException("Invalid filename or username");
        FileHandle temp = catalogDb.findFile(fileName, true);
        if(temp!=null && !canEdit(temp,username) && !temp.publicWrite()) throw new RejectedException("A file with this filename already exists");
        if(temp!=null && (canEdit(temp,username) || temp.publicWrite())){
            existed=true;
            if(temp.getNotify()){
                try {
                    servout.notifyClient("File " + temp.getName() + " was replaced by " + username, temp.getOwner());
                } catch (IOException ex) { throw new RejectedException("Error during notification");}
            }
        }
        try {
            FileHandle file = servout.recvFile(fileName, fileName, username, fileLength);
            if(!existed){
                Usero us = catalogDb.findUsero(username);
                String tff = us.getUsern();
                catalogDb.createNewFile(file);
                file.giveReadPermissions(us);
                file.giveWritePermissions(us);
                catalogDb.updateFile();
            }
        } catch (IOException ex) {
            throw new RejectedException("Error during file transmit");
        }
    }
    
    @Override
    public FileHandleif downloadFile(String username, String fileName) throws RemoteException, RejectedException{
        if(fileName==null || username == null) throw new RejectedException("Invalid filename or username");
        FileHandle temp = catalogDb.findFile(fileName, true);
        if(temp==null || (!canView(temp,username) && !temp.publicRead())) throw new RejectedException("File couldn't be found or you don't have access to this file");
        if(!temp.getOwner().equals(username)){
            if(temp.getNotify()){
                try {
                    servout.notifyClient("File " + temp.getName() + " was downloaded by " + username, temp.getOwner());
                } catch (IOException ex) { throw new RejectedException("Error during notification");}
            }
        }
        servout.setFileToSend(temp, username);
        new Thread(servout).start();
        return temp;
    }
    
    @Override
     public void setNotify(String username, String fileName, boolean notify) throws RemoteException, RejectedException{
         if(fileName==null || username == null) throw new RejectedException("Invalid filename or username");
        FileHandle temp = catalogDb.findFile(fileName, false);
        if(temp==null || !temp.getOwner().equals(username)) throw new RejectedException("File couldn't be found or you don't have access to this file");
        temp.setNotfiyMe(notify);
        catalogDb.updateFile();
        if(notify){
            servout.accept2(username);
        }
        else{
             try {
                 servout.notifyClient("You will no longer recieve notifications for " + fileName, username);
             } catch (IOException ex) {
                 throw new RejectedException("Error during notification");
             }
            servout.update2(username);
        }
     }
    
    @Override
    public void deleteFile(String username, String fileName) throws RemoteException, RejectedException{
        FileHandle temp = catalogDb.findFile(fileName, true);
        if(temp==null || (!temp.getOwner().equals(username) && !canEdit(temp,username) && !temp.publicWrite())) throw new RejectedException("This file does not exist, or you don't have permission to edit this file");
        List<DeprUser> ff1 = temp.getReadPermissions();
        List<DeprUser> ff2 = temp.getWritePermissions();
        catalogDb.deleteDepr(ff1);
        catalogDb.deleteDepr(ff2);
        catalogDb.deleteFile(fileName);
        if(!temp.getOwner().equals(username)&&temp.getNotify()){
            try {
                servout.notifyClient("File" + temp.getName() + "was deleted by " + username, temp.getOwner());
            } catch (IOException ex) { throw new RejectedException("Error during notification");}
        }
    }
    
    @Override
    public FileHandleif giveFileNfo(String username, String fileName) throws RemoteException, RejectedException{
        FileHandle temp = catalogDb.findFile(fileName, true);
        if(temp==null || (!canView(temp, username) && !temp.publicRead())) throw new RejectedException("This file does not exist, or you don't have access to this file");
        if(!temp.getOwner().equals(username) && temp.getNotify()){
            try {
                servout.notifyClient("File info was requested by " + username, temp.getOwner());
            } catch (IOException ex) { throw new RejectedException("Error during notification");}
        }
        return temp;
    }
    
    @Override
    public void giveWritePermissions(String username, String fileName, String givingto) throws RemoteException, RejectedException{
        DeprUser dus = catalogDb.findDeprUsero(givingto);
        if(dus==null) throw new RejectedException("This user is not registered");
        FileHandle temp = catalogDb.findFile(fileName, false);
        if(temp==null || !canEdit(temp,username)) throw new RejectedException("This file does not exist, or you don't have access to this file");
        if(!canView(temp, givingto)) temp.giveReadPermissions(dus);
        if(!canEdit(temp, givingto)) temp.giveWritePermissions(dus);
        catalogDb.updateFile();
        if(!temp.getOwner().equals(username) && temp.getNotify()){
            try {
                servout.notifyClient(username + " gave write permissions to " + givingto + " on file " + temp.getName(), temp.getOwner());
            } catch (IOException ex) { throw new RejectedException("Error during notification");}
        }
    }
    
    @Override
    public void giveReadPermissions(String username, String fileName, String givingto) throws RemoteException, RejectedException{
        DeprUser dus = catalogDb.findDeprUsero(givingto);
        if(dus==null) throw new RejectedException("This user is not registered");
        FileHandle temp = catalogDb.findFile(fileName, false);
        if(temp==null || !canEdit(temp,username)) throw new RejectedException("This file does not exist, or you don't have access to this file");
        if(!canView(temp, givingto)) temp.giveReadPermissions(dus);
        catalogDb.updateFile();
        if(!temp.getOwner().equals(username) && temp.getNotify()){
            try {
                servout.notifyClient(username + " gave read permissions to " + givingto + " on file " + temp.getName(), temp.getOwner());
            } catch (IOException ex) { throw new RejectedException("Error during notification");}
        }
    }
    
    @Override
    public List<String> viewReadPermissions(String username, String fileName) throws RemoteException, RejectedException {
        FileHandle temp = catalogDb.findFile(fileName, true);
        if(temp==null || (!canView(temp,username) && temp.publicRead())) throw new RejectedException("This file does not exist, or you don't have access to this file");
        List<DeprUser> uslist = temp.getReadPermissions();
        List<String> fin = new ArrayList<>();
        uslist.forEach((us) -> {
            fin.add(us.getUsern());
        });
        return fin;
    }
    
    @Override
    public List<String> viewWritePermissions(String username, String fileName) throws RemoteException, RejectedException {
        FileHandle temp = catalogDb.findFile(fileName, true);
        if(temp==null || (!canView(temp,username)&&!temp.publicRead())) throw new RejectedException("This file does not exist, or you don't have access to this file");
        List<DeprUser> uslist = temp.getWritePermissions();
        List<String> fin = new ArrayList<>();
        uslist.forEach((us) -> {
            fin.add(us.getUsern());
        });
        return fin;
    }
    
    @Override
    public List<? extends FileHandleif> viewMyFiles(String username) throws RemoteException, RejectedException {
        if(username==null) return null;
        return catalogDb.findMyFiles(username, true);
    }
    
    @Override
    public List<? extends FileHandleif> viewMyReadFiles(String username) throws RemoteException, RejectedException {
        if(username==null) return null;
        List<FileHandle> fin = new ArrayList<>();
        List<FileHandle> temp1 = catalogDb.findAllFiles();
        temp1.stream().filter((file) -> (canView(file,username) || file.publicRead())).forEachOrdered((file) -> {
            fin.add(file);
            if(!username.equals(file.getOwner()) && file.getNotify()){
                try {
                servout.notifyClient(username + " listed that he can read the file called " + file.getName(), file.getOwner());
            } catch (IOException ex) { System.err.println("Error during notification");}
            }
        });
        return fin;
    }
    
    @Override
    public List<? extends FileHandleif> viewMyWriteFiles(String username) throws RemoteException, RejectedException {
        if(username==null) return null;
        List<FileHandle> fin = new ArrayList<>();
        List<FileHandle> temp1 = catalogDb.findAllFiles();
        temp1.stream().filter((file) -> (canEdit(file,username) || file.publicWrite())).forEachOrdered((file) -> {
            fin.add(file);
            if(!username.equals(file.getOwner()) && file.getNotify()){
                try {
                servout.notifyClient(username + " listed that he can edit the file called " + file.getName(), file.getOwner());
            } catch (IOException ex) { System.err.println("Error during notification");}
            }
        });
        return fin;
    }
    
    @Override
    public void deregister(String username, String password) throws RemoteException, RejectedException {
        if(username==null||password==null) throw new RejectedException("Wrong input for this function");
        if(!catalogDb.checkPassw(username, password))throw new RejectedException("Wrong username/password");
        catalogDb.deleteUsero(username);
    }
    
    @Override
    public void givePublicRead(String username, String fileName, boolean setTo) throws RemoteException, RejectedException {
        if(username==null || fileName == null) throw new RejectedException("Wrong input for this function");
        try{
            FileHandle temp = catalogDb.findFile(fileName, false);
            if(temp==null || !temp.getOwner().equals(username)) throw new RejectedException("This file does not exist, or you don't have access to this file");
            temp.setPublicRead(setTo);
        }
        finally{
            catalogDb.updateFile();
        }
    }
    @Override
    public void givePublicWrite(String username, String fileName, boolean setTo) throws RemoteException, RejectedException {
        if(username==null || fileName == null) throw new RejectedException("Wrong input for this function");
        try{
            FileHandle temp = catalogDb.findFile(fileName, false);
            if(temp==null || !temp.getOwner().equals(username)) throw new RejectedException("This file does not exist, or you don't have access to this file");
            temp.setPublicWrite(setTo);
            if(setTo) temp.setPublicRead(setTo);
        }
        finally{
            catalogDb.updateFile();
        }
    }
    
    private boolean canEdit(FileHandle file, String username){
        List<DeprUser> uslist = file.getWritePermissions();
        return uslist.stream().anyMatch((us) -> (us.getUsern().equals(username)));
    }
    
    private boolean canView(FileHandle file, String username){
        List<DeprUser> uslist = file.getReadPermissions();
        return uslist.stream().anyMatch((us) -> (us.getUsern().equals(username)));
    }
    
    private boolean canEdit(FileHandleif file, String username){
        return file.getWritePermissions().stream().anyMatch((ff) -> (ff.equals(username)));
    }
    
    private boolean canView(FileHandleif file, String username){
        return file.getReadPermissions().stream().anyMatch((ff) -> (ff.equals(username)));
    }
}
