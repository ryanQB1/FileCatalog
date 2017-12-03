package client.view;

import Common.FileCatalogIf;
import Common.FileHandleif;
import client.net.ClientNetNot;
import client.net.ClientNetReciever;
import client.net.ClientNetSend;
import client.net.ClientNetSuper;
import client.net.OutputHandler;
import java.io.File;
import java.util.List;
import java.util.Scanner;

public class NonBlockingInterpreter implements Runnable{
    
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private final ThreadSafeStdOut outMgr = new ThreadSafeStdOut();
    private FileCatalogIf catalog;
    private boolean receivingCmds = false;
    ClientNetSend clientnetsend = new ClientNetSend();
    ClientNetReciever clientnetrecv = new ClientNetReciever();
    ClientNetNot clientnetnot = new ClientNetNot();
    ClientNetSuper clientnetsup;
    
    private final outhandl outh = new outhandl();
    
    private int recvingnotsfor = 0;
    
    public void start(FileCatalogIf bank) {
        this.catalog = bank;
        if (receivingCmds) {
            return;
        }
        receivingCmds = true;
        new Thread(this).start();
    }
    
    @Override
    public void run() {
        String username = "";
        while (receivingCmds) {
            try {
                CmdLine cmdLine = new CmdLine(readNextLine());
                switch (cmdLine.getCmd()) {
                    case CONNECT:
                        clientnetsup = new ClientNetSuper(cmdLine.getParameter(0), Integer.parseInt(cmdLine.getParameter(1)));
                        break;
                    case HELP:
                        for (Command command : Command.values()) {
                            if (command == Command.ILLEGAL_COMMAND) {
                                continue;
                            }
                            System.out.println(command.toString().toLowerCase());
                        }
                        break;
                    case QUIT:
                        receivingCmds = false;
                        clientnetnot.setOnOff(false);
                        if(recvingnotsfor!=0) catalog.sendNotif(username);
                        username="";
                        break;
                    case REGISTER:
                        catalog.register(cmdLine.getParameter(0), cmdLine.getParameter(1));
                        username = cmdLine.getParameter(0);
                        break;
                    case LOGIN:
                        recvingnotsfor = catalog.login(cmdLine.getParameter(0), cmdLine.getParameter(1));
                        username = cmdLine.getParameter(0);
                        outMgr.println(recvingnotsfor + " notification systems were restored");
                        if(recvingnotsfor!=0){
                            clientnetsup.notif(clientnetnot, outh);
                            catalog.update1(username);
                            clientnetnot.setOnOff(true);
                            clientnetnot.start();
                        }
                        break;
                    case LISTMY:
                        if(!username.isEmpty()){
                            List<? extends FileHandleif> files = catalog.viewMyFiles(username);
                            files.forEach((file) -> {
                                outMgr.println(printfile(file));
                            });
                        }
                        break;
                    case LISTREAD:
                        if(!username.isEmpty()){
                            List<? extends FileHandleif> files = catalog.viewMyReadFiles(username);
                            files.forEach((file) -> {
                                outMgr.println(printfile(file));
                            });
                        }
                        break;
                    case LISTWRITE:
                        if(!username.isEmpty()){
                            List<? extends FileHandleif> files = catalog.viewMyWriteFiles(username);
                            files.forEach((file) -> {
                                outMgr.println(printfile(file));
                            });
                        }
                        break;
                    case LISTALL:
                        if(!username.isEmpty()){
                            List<? extends FileHandleif> files = catalog.viewMyFiles(username);
                            if(!files.isEmpty()) outMgr.println("Files you own:");
                            files.forEach((file) -> {
                                outMgr.println(printfile(file));
                            });
                            List<? extends FileHandleif> files2 = catalog.viewMyReadFiles(username);
                            if(!files2.isEmpty()) outMgr.println("Files you are allowed to read:");
                            files2.forEach((file) -> {
                                outMgr.println(printfile(file));
                            });
                            List<? extends FileHandleif> files3 = catalog.viewMyWriteFiles(username);
                            if(!files2.isEmpty()) outMgr.println("Files you are allowed to edit:");
                            files3.forEach((file) -> {
                                outMgr.println(printfile(file));
                            });
                        }
                    break;
                    case DEREGISTER:
                        catalog.deregister(cmdLine.getParameter(0), cmdLine.getParameter(1));
                        break;
                    case DELETE:
                        catalog.deleteFile(username, cmdLine.getParameter(0));
                        break;
                    case UPLOAD:
                        if(!username.isEmpty()){
                            File file = new File(cmdLine.getParameter(0));
                            if (file.exists()){
                                clientnetsup.send(clientnetsend);
                                catalog.accept1(username);
                                clientnetsend.start(cmdLine.getParameter(0));
                                catalog.uploadFile(username, cmdLine.getParameter(0), file.length());
                            }
                        }
                        break;
                    case DOWNLOAD:
                        if(!username.isEmpty()){
                            clientnetsup.recv(clientnetrecv);
                            FileHandleif file = catalog.downloadFile(username, cmdLine.getParameter(0));
                            clientnetrecv.start(file.getName(),file.getSize());
                        }
                        break;
                    case NOTIFY:
                        if(!username.isEmpty()){
                            boolean notify;
                            if(cmdLine.getParameter(1).equals("on")){
                                notify = true;
                                recvingnotsfor++;
                            }
                            else if(cmdLine.getParameter(1).equals("off")){
                                notify = false;
                                recvingnotsfor--;
                            }
                            else{
                                continue;
                            }
                            if(notify){
                                clientnetsup.notif(clientnetnot, outh);
                            }
                            else if(recvingnotsfor==0 && !notify){
                                clientnetnot.setOnOff(false);
                            }
                            catalog.setNotify(username, cmdLine.getParameter(0), notify);
                            if(recvingnotsfor==1 && notify){
                                clientnetnot.setOnOff(true);
                                clientnetnot.start();
                            }
                        }
                        break;
                    case GIVENFO:
                        if(!username.isEmpty()){
                            FileHandleif file = catalog.giveFileNfo(username, cmdLine.getParameter(0));
                            outMgr.println(printfile(file));
                        }
                        break;
                    case GIVEWRITEPERM:
                        if(!username.isEmpty()){
                            catalog.giveWritePermissions(username, cmdLine.getParameter(0), cmdLine.getParameter(1));
                        }
                        break;
                    case GIVEWRITEPERMPUBLIC:
                        if(!username.isEmpty()){
                            boolean setTo = false;
                            if(cmdLine.getParameter(1).equals("true")){
                                setTo = true;
                            }
                            else if(cmdLine.getParameter(1).equals("false")){
                                setTo = false;
                            }
                            catalog.givePublicWrite(username, cmdLine.getParameter(0), setTo);
                        }
                        break;
                    case GIVEREADPERMPUBLIC:
                        if(!username.isEmpty()){
                            boolean setTo = false;
                            if(cmdLine.getParameter(1).equals("true")){
                                setTo = true;
                            }
                            else if(cmdLine.getParameter(1).equals("false")){
                                setTo = false;
                            }
                            catalog.givePublicRead(username, cmdLine.getParameter(0), setTo);
                        }
                        break;
                    case GIVEREADPERM:
                        if(!username.isEmpty()){
                            catalog.giveReadPermissions(username, cmdLine.getParameter(0), cmdLine.getParameter(1));
                        }
                        break;
                    case VIEWREADPERM:
                        if(!username.isEmpty()){
                            for(String name : catalog.viewReadPermissions(username, cmdLine.getParameter(0))){
                                outMgr.println(name);
                            }
                        }
                        break;
                    case VIEWWRITEPERM:
                        if(!username.isEmpty()){
                            for(String name : catalog.viewWritePermissions(username, cmdLine.getParameter(0))){
                                outMgr.println(name);
                            }
                        }
                        break;
                    default:
                        outMgr.println("illegal command");
                }
            } catch (Exception e) {
                outMgr.println("Operation failed");
                outMgr.println(e.getMessage());
            }
        }
    }
    
    private String readNextLine() {
        outMgr.print(PROMPT);
        return console.nextLine();
    }
    
    private String printfile(FileHandleif file){
        return "     Name: " + file.getName() + " \n     Owner: " + file.getOwner() + "\n     Size: " + file.getSize() + "\n----------";
    }
    
    private class outhandl implements OutputHandler {
        
        @Override
        public void printmsg(String msg){
            outMgr.println(msg);
            outMgr.print(PROMPT);
        }
        
    }
}
