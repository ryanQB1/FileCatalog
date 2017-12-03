package server.Startup;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import server.controller.Controller;

public class ServerStart {
    
    private static final String USAGE = "java bankjpa.Server [bank name in rmi registry]";
    private String catalogName = "catalog";
    private int portNo = 8080;
    
    public static void main(String[] args) {
        try {
            ServerStart server = new ServerStart();
            server.parseCommandLineArgs(args);
            server.startRMIServant();
            System.out.println("Server started.");
        } catch (RemoteException | MalformedURLException e ) {
            System.out.println("Failed to start server.");
        }
    }
    
    private void startRMIServant() throws RemoteException, MalformedURLException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException noRegistryRunning) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        Controller contr = new Controller(portNo);
        Naming.rebind(catalogName, contr);
    }

    private void parseCommandLineArgs(String[] args) {
        for(int ind = 0; args.length > ind; ind += 2){
            if(args[ind].equalsIgnoreCase("-h")){
                System.out.println(USAGE);
                System.exit(1);
            }
            else if(args[ind].equalsIgnoreCase("-b") && args.length > ind+1){
                catalogName = args[ind+1];
            }
            else if(args[ind].equalsIgnoreCase("-p") && args.length > ind+1){
                portNo = Integer.parseInt(args[ind+1]);
            }
        }
    }
}
