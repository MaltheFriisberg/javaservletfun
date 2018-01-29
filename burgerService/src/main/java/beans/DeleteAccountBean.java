package beans;

import java.rmi.RemoteException;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import util.ErrorResponse;
import util.NamingConstants;
import util.Queue;
import util.UserManager;
import dtu.ws.fastmoney.*;

/**
 * @author malthe
 *This bean will delete the user in the onMessage method. 
 *It will also retire the bank account from Fast Money Bank.
 *It will also remove all the barcodes associated with the user.
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destination", propertyValue = NamingConstants.deleteAccountQueue) })
public class DeleteAccountBean implements MessageListener {
	@Resource(lookup = NamingConstants.connectionFactory)
	ConnectionFactory connectionFactory;

	
	public void onMessage(Message message) {
		if (message instanceof ObjectMessage) {
			ObjectMessage objMessage = (ObjectMessage) message;

			try {
				if (message.getJMSReplyTo() != null) {

					try {
						BankService bank = new BankServiceProxy();
						String uuid = objMessage.getObject().toString();
						bank.retireAccount(uuid);
						UserManager.userList.remove(uuid);
						UserManager.barcodeList.remove(uuid);
						new Queue(message.getJMSReplyTo()).sendObject(true);
					} catch (BankServiceException e) {
						new Queue(message.getJMSReplyTo()).sendObject(new ErrorResponse(503, "Error contacting the bank"));
					}
				}
			} catch (JMSException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
				try {
					new Queue(message.getJMSReplyTo()).sendObject(new ErrorResponse(503, "Communication error"));
				} catch (JMSException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}