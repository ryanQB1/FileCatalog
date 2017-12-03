package client.startup;

import Common.FileCatalogIf;
import client.view.NonBlockingInterpreter;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Main {
    
    private static final String catalogName = "catalog";
    
    public static void main(String[] args) {
        try {
            FileCatalogIf filecatalog = (FileCatalogIf) Naming.lookup(catalogName);
            new NonBlockingInterpreter().start(filecatalog);
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            System.out.println("Could not start bank client.");
        }
    }
}