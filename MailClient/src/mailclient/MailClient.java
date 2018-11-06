
package mailclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 *
 * @author Sotiria Antaranian
 */
public class MailClient {
    
    public void visitorsMenu ()
    {
        System.out.println("Welcome to our email service.");
        System.out.println("Type your choice.");
        System.out.println("> Log in");
        System.out.println("> Sign in");
        System.out.println("> Exit");
    }
    
    public void usersMenu ()
    {
        System.out.println("Type your choice.");
        System.out.println("> New email");
        System.out.println("> Show emails");
        System.out.println("> Read email");
        System.out.println("> Delete email");
        System.out.println("> Log out");
        System.out.println("> Exit");
    }
    
    public void menu2(String username,DataInputStream in,DataOutputStream out, Socket s) throws IOException
    {
        Scanner input=new Scanner(System.in); 
        MailClient mailclient=new MailClient();
        mailclient.usersMenu(); // prints user's (someone with an account) menu
        String choice2=input.nextLine(); // reads user's choice
        out.writeUTF(choice2);
        int id;
        
        while(!choice2.equals("Exit") && !choice2.equals("Log out"))
        {
            if (choice2.equals("New email"))
            {
                System.out.println("Receiver:");
                String receiver=input.nextLine();
                System.out.println("Subject:");
                String subject=input.nextLine();
                System.out.println("Main body:");
                String mainbody=input.nextLine();
                out.writeUTF(receiver);
                out.writeUTF(subject);
                out.writeUTF(mainbody);
                boolean newEmail=in.readBoolean();
                if(newEmail)
                    System.out.println("Mail send successfully.");
                else
                    System.out.println("Invalid receiver.");
            }
            else if (choice2.equals("Show emails"))
            {
                int mailboxSize=in.readInt();
                if(mailboxSize==0)
                    System.out.println("Your mailbox is empty.");
                else
                {
                    System.out.printf("%-30.30s  %-30.30s  %-30.30s  %-30.30s%n","Id"," ","From","Subject");
                    for(int i=0;i<mailboxSize;i++)
                        System.out.printf("%-30.30s  %-30.30s  %-30.30s  %-30.30s%n",i+1,in.readUTF(),in.readUTF(),in.readUTF());                        
                }
            }
            else if (choice2.equals("Read email"))
            {
                System.out.println("Type the id of the email you would like to read.");
                id=input.nextInt();
                input.nextLine();
                out.writeInt(id);
                System.out.println("sender:");
                System.out.println(in.readUTF());
                System.out.println("subject:");
                System.out.println(in.readUTF());
                System.out.println("mainbody:");
                System.out.println(in.readUTF());
                
            }
            else if (choice2.equals("Delete email")) 
            {
                System.out.println("Type the id of the email you would like to delete.");
                id=input.nextInt();
                out.writeInt(id);
                System.out.println(in.readUTF());
            }
            else 
                System.out.println("Please type again."); // in case the user types something that wasn't one of the options
            mailclient.usersMenu(); // the users' menu re-appears in case the user wants to do more than one thing
            choice2=input.nextLine();
            out.writeUTF(choice2);
        } // if the user chooses "Log out", the visitors' menu will appear
        if(choice2.equals("Exit"))
        {
           s.close();
           in.close();
           out.close();
        }
    }
    
    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    
    public static void main(String[] args) throws IOException 
    {
	Socket s = null;
	try{
            int serverPort=Integer.parseInt(args[0]); // listening port
            InetAddress localhost=InetAddress.getByName(args[1]); // server's IP address
            s=new Socket(localhost, serverPort);    
            
            DataOutputStream out;
            try (DataInputStream in = new DataInputStream(s.getInputStream())) {
                out = new DataOutputStream(s.getOutputStream());
                MailClient mailclient=new MailClient();
                mailclient.visitorsMenu(); // prints visitors' menu
                Scanner input=new Scanner(System.in);
                String choice=input.nextLine();
                out.writeUTF(choice);
                while(!choice.equals("Exit"))
                {
                    switch (choice) {
                        case "Log in":
                            {
                                System.out.println("Type your username:");
                                String username=input.nextLine();
                                out.writeUTF(username);
                                System.out.println("Type your password:");
                                String password=input.nextLine();
                                out.writeUTF(password);
                                boolean isLoggedIn=in.readBoolean();
                                if(isLoggedIn) // right username and password
                                {
                                    System.out.println("Welcome back!");
                                    mailclient.menu2(username, in, out, s); // function that manages the user's choice, such as New email, show emails etc.
                                    // the socket s is a parameter in order to be closed the socket in case the user chooses "Exit"
                                    if(s.isClosed()) // if the user chose "Exit", the socket is closed
                                        return;
                                }
                                    
                                else
                                    System.out.println("Invalid username or password.");
                                break;
                            }
                        case "Sign in":
                            {
                                System.out.println("Type your username:");
                                String username=input.nextLine();
                                out.writeUTF(username);
                                System.out.println("Type your password:");
                                String password=input.nextLine();
                                out.writeUTF(password);
                                boolean registered=in.readBoolean();
                                if(registered) // the given username hasn't been used before
                                    System.out.println("Your registration was successful.");
                                else
                                    System.out.println("This username already exists or there was a problem with your registration. Please try again later.");
                                break;
                            }
                        default:
                            System.out.println("Please type again."); // in case the user types something that wasn't one of the options
                            break;
                    }
                    mailclient.visitorsMenu(); // the visitors' menu re-appears in case the visitor wants to exit, log in after signing in, create more than one accounts, or a user has logged out.
                    choice=input.nextLine(); 
                    out.writeUTF(choice); // writes visitor's new choice
                }   
                s.close();//exit
            }
            out.close();
	}
        catch (UnknownHostException e){System.out.println("Socket:"+e.getMessage());}
        catch (EOFException e){System.out.println("EOF:"+e.getMessage());}               
        catch (IOException e){System.out.println("readline:"+e.getMessage());}
        finally 
        {if(s!=null) try {s.close();}
        catch (IOException e){System.out.println("close:"+e.getMessage());}}
                
    }
    
}
