package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import server.model.RejectedException;

public interface FileCatalogIf extends Remote{
    
    public int login(String username, String password) throws RemoteException, RejectedException;
    
    public void deregister(String username, String password) throws RemoteException, RejectedException;
    
    public void accept1(String username) throws RemoteException, RejectedException;
    
    public void update1(String username) throws RemoteException, RejectedException;
    
    public void register(String username, String password) throws RemoteException, RejectedException;
    
    public void uploadFile(String username, String fileName, long fileLength) throws RemoteException, RejectedException;
    
    public FileHandleif downloadFile(String username, String fileName) throws RemoteException, RejectedException;
    
    public void deleteFile(String username, String fileName) throws RemoteException, RejectedException;
    
    public FileHandleif giveFileNfo(String username, String fileName) throws RemoteException, RejectedException;
    
    public void giveWritePermissions(String username, String fileName, String givingto) throws RemoteException, RejectedException;
    
    public void giveReadPermissions(String username, String fileName, String givingto) throws RemoteException, RejectedException;
    
    public List<String> viewWritePermissions(String username, String fileName) throws RemoteException, RejectedException;
    
    public List<String> viewReadPermissions(String username, String fileName) throws RemoteException, RejectedException;
    
    public List<? extends FileHandleif> viewMyFiles(String username) throws RemoteException, RejectedException;
    
    public List<? extends FileHandleif> viewMyReadFiles(String username) throws RemoteException, RejectedException;
    
    public List<? extends FileHandleif> viewMyWriteFiles(String username) throws RemoteException, RejectedException;
    
    public void givePublicRead(String username, String fileName, boolean setTo) throws RemoteException, RejectedException;
    
    public void givePublicWrite(String username, String fileName, boolean setTo) throws RemoteException, RejectedException;
    
    public void setNotify(String username, String fileName, boolean notify) throws RemoteException, RejectedException;
    
    public void sendNotif(String username) throws RemoteException, RejectedException;
}
