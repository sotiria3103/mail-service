
package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sotiria Antaranian
 */
public class MailServer 
{
    public static List<Account> accounts = new ArrayList<>();
    
    public boolean register (String username,String password) // sign in
    {
        for(int i=0;i<accounts.size();i++)
            if(accounts.get(i).getUsername().equals(username)) // check if there is an account with the same username
                return false; 
        boolean add=accounts.add(new Account(username,password));
        return true;
    }        
    
    public boolean logIn (String username,String password)
    {
        for(int i=0;i<accounts.size();i++)
            if(accounts.get(i).getUsername().equals(username) && accounts.get(i).getPassword().equals(password)) // search the given username and check if the given password matches
                return true;
        return false;
    }
    
    public boolean newEmail (String sender,String receiver,String subject,String mainbody)
    {
        System.out.println("new email");
        System.out.println(accounts.size());
        for(int i=0;i<accounts.size();i++)
        {
            System.out.println("for");
            if(accounts.get(i).getUsername().equals(receiver)) // check if the receiver exits (is an account)
            {
                System.out.println("if");
                System.out.println(receiver);
                Email email=new Email(sender,receiver,subject,mainbody);
                boolean add=accounts.get(i).getMailbox().add(email);
                System.out.println(add);
                return true;
            }   
        } 
        return false;
    }
    
    public void menu2 (Account user,DataInputStream in,DataOutputStream out,Socket clientSocket,ServerSocket listenSocket) throws IOException
    {
        MailServer mailserver=new MailServer(); 
        String choice2=in.readUTF(); //what the user chose from the users' menu
        while(!choice2.equals("Exit") && !choice2.equals("Log out"))
        {
            if (choice2.equals("New email"))
            {
                String receiver=in.readUTF();
                System.out.println(receiver);
                String subject=in.readUTF();
                String mainbody=in.readUTF();
                boolean newemail=mailserver.newEmail(user.getUsername(),receiver,subject,mainbody);
                out.writeBoolean(newemail);
            }
            else if (choice2.equals("Show emails"))
                user.showEmails(out); // at Account.java
            else if (choice2.equals("Read email"))
            {
                int id=in.readInt(); // the id of the email the user wants to read
                user.readEmail(id,out); // at Account.java
            }
            else if (choice2.equals("Delete email"))
            {
                int id=in.readInt(); // the id of the email the user wants to delete
                user.deleteEmail(id,out); // at Account.java
            }
            choice2=in.readUTF();       
        }
        if(choice2.equals("Exit"))
        {
            clientSocket.close();
            listenSocket.close();
            in.close();
            out.close();
        }                 
    }
    
    public void createAccounts()
    {
        Account account1=new Account("user1@csd.gr","123");
        Email email1=new Email("user2@csd.gr","user1@csd.gr","Hello!","Hello! I will see you tomorrow at our meeting.");
        Email email2=new Email("user2@csd.gr","user1@csd.gr","Great meeting","Today was great but do you think we can have another one this week?");
        Email email3=new Email("user2@csd.gr","user1@csd.gr","New meeting","Great! See you there!");
        boolean add=account1.getMailbox().add(email1);
        add=account1.getMailbox().add(email2);
        add=account1.getMailbox().add(email3);
        add=accounts.add(account1);
        Account account2=new Account("user2@csd.gr","000");
        Email emailA=new Email("user1@csd.gr","user2@csd.gr","Re:Hello!","Great, I can't wait!");
        Email emailB=new Email("user1@csd.gr","user2@csd.gr","Re:Great meeting","Of course! How about Friday at 10 a.m., same place?");
        Email emailC=new Email("user1@csd.gr","user2@csd.gr","Re:New meeting","See you there!");
        add=account2.getMailbox().add(emailA);
        add=account2.getMailbox().add(emailB);
        add=account2.getMailbox().add(emailC);
        add=accounts.add(account2);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {   
        try
        {
            int serverPort=Integer.parseInt(args[0]); // port that the server "runs on"
            MailServer mailserver=new MailServer();
            mailserver.createAccounts(); // creates two accounts with three emails each
            try (ServerSocket listenSocket = new ServerSocket(serverPort)) {
                while(true)
                {
                    Socket clientSocket = listenSocket.accept();
                    Connection c = new Connection(clientSocket,listenSocket,mailserver); // the sockets are parameters to the constructor in order to be closed in case the user or the visitor chooses "Exit"
                }
            }
	} catch(IOException e) 
        {System.out.println("Listen socket:"+e.getMessage());}
    }
}

