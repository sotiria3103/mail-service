
package server;

/**
 *
 * @author Sotiria Antaranian
 */
public class Email 
{
    private boolean isNew;
    private String sender;
    private String receiver;
    private String subject;
    private String mainbody;
    
    public Email(String aSender,String aReceiver,String aSubject,String aMainbody)
    {
        this.isNew=true;
        sender=aSender;
        receiver=aReceiver;
        subject=aSubject;
        mainbody=aMainbody;
    }

    public void setIsNew(boolean isNew) {this.isNew = isNew;}
    public void setSender(String sender) {this.sender = sender;}
    public void setReceiver(String receiver) {this.receiver = receiver;}
    public void setSubject(String subject) {this.subject = subject;}
    public void setMainbody(String mainbody) {this.mainbody = mainbody;}
    
    public boolean isIsNew() {return isNew;}
    public String getSender() {return sender;}
    public String getReceiver() {return receiver;}
    public String getSubject() {return subject;}
    public String getMainbody() {return mainbody;}
  
}
