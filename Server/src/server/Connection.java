
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Sotiria Antaranian
 */
class Connection extends Thread {
DataInputStream in;
DataOutputStream out;
Socket clientSocket;
ServerSocket listenSocket;
MailServer mailserver;

public Connection (Socket aClientSocket,ServerSocket aListenSocket,MailServer aMailserver) 
{
    try 
    {
        clientSocket=aClientSocket;
        listenSocket=aListenSocket;
        mailserver=aMailserver;
        in=new DataInputStream( clientSocket.getInputStream());
        out=new DataOutputStream( clientSocket.getOutputStream());
        this.start();
    } catch(IOException e) {System.out.println("Connection:"+e.getMessage());}
}
    
@Override
public void run(){
    try 
    {        
        System.out.println("Request from client" + clientSocket.getInetAddress()+"at port "+ clientSocket.getPort());
        String choice= in.readUTF();
        while(!choice.equals("Exit"))
        {
            if(choice.equals("Log in"))
            {
                String username= in.readUTF(); // reads username
                String password= in.readUTF(); // reads password
                boolean isLoggedIn=mailserver.logIn(username,password);
                out.writeBoolean(isLoggedIn);
                if(isLoggedIn)
                {
                    Account user=null; //user is create so that the functions of class Account can be easily reached in function menu2
                    for(int i=0;i<MailServer.accounts.size();i++)
                    {
                        if(MailServer.accounts.get(i).getUsername().equals(username))
                            user=MailServer.accounts.get(i); // user is the account that has just logged in
                    }
                    mailserver.menu2(user,in,out,clientSocket,listenSocket); // function that deals with the choices a user with an account can make such as New email, Show emails etc.
                    if(clientSocket.isClosed()) // in case the user chose Exit
                        return;
                }
            }
            else if(choice.equals("Sign in"))
            {
                String username= in.readUTF();
                String password= in.readUTF();
                boolean registered=mailserver.register(username,password);
                out.writeBoolean(registered);
            }
            choice= in.readUTF(); //reads visitor new choice
        }   
        clientSocket.close(); // exit
        listenSocket.close();
    } catch(EOFException e) {System.out.println("EOF:"+e.getMessage());
    } catch(IOException e) {System.out.println("IO:"+e.getMessage());}
}    
}

