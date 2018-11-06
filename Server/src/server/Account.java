
package server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sotiria Antaranian
 */
public class Account 
{
    private String username;
    private String password;
    private List<Email> mailbox;
    
    public Account (String aUsername, String aPassword)
    {
        username=aUsername;
        password=aPassword;
        mailbox=new ArrayList<>();
    }

    public void setUsername(String username) {this.username = username;}
    public void setPassword(String password) {this.password = password;}
    public void setMailbox(List<Email> mailbox) {this.mailbox = mailbox;}
    
    public String getUsername() {return username;}
    public String getPassword() {return password;}
    public List<Email> getMailbox() {return mailbox;}
    
    public void showEmails(DataOutputStream out) throws IOException
    {
        out.writeInt(mailbox.size());
        if(!mailbox.isEmpty()) // in case there aren't any emails in the mailbox
        {
            for(int i=0;i<mailbox.size();i++)
            {
                if(mailbox.get(i).isIsNew()) // in case the email was never read
                    out.writeUTF("[New]"); // write out "New"
                else
                    out.writeUTF(" "); // or else write -blank-
                out.writeUTF(mailbox.get(i).getSender());
                out.writeUTF(mailbox.get(i).getSubject());
            }
        }
    }
    
    public void readEmail (int id,DataOutputStream out) throws IOException
    {
        if(!mailbox.isEmpty())
        {
            if(id-1<mailbox.size()) // check if the given id is valid
            { // id-1 because the ids of the emails start from '1' and the list of emails in the mailbox start from '0' 
                if(mailbox.get(id-1).isIsNew())
                    mailbox.get(id-1).setIsNew(false); // change an unread email to read
                out.writeUTF(mailbox.get(id-1).getSender());
                out.writeUTF(mailbox.get(id-1).getSubject());
                out.writeUTF(mailbox.get(id-1).getMainbody());
            }
            else
                out.writeUTF("Invalid id.");
        }
        else
            out.writeUTF("Your mailbox is empty."); 
    }
    
    public void deleteEmail (int id,DataOutputStream out) throws IOException
    {
        if(!mailbox.isEmpty())
        {
            if(id-1<mailbox.size()) // check if the given id is valid
            { // id-1 because the ids of the emails start from '1' and the list of emails in the mailbox start from '0' 
                Email remove = mailbox.remove(id-1);
                out.writeUTF("Email successfully deleted.");
            }
            else
                out.writeUTF("Invalid id.");
        }
        else
            out.writeUTF("Your mailbox is empty.");
    }

}
