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
import dtu.ws.fastmoney.*;

/**
 * @author malthe
 *This bean fetches the Account information from the fast money bank..
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destination", propertyValue = NamingConstants.getAccountQueue) })
public class GetAccountBean implements MessageListener {
	@Resource(lookup = NamingConstants.connectionFactory)
	ConnectionFactory connectionFactory;
	
	public void onMessage(Message message) {
		if (message instanceof ObjectMessage) {
			ObjectMessage objMessage = (ObjectMessage) message;

			try {
				if (message.getJMSReplyTo() != null) {
					BankService bank = new BankServiceProxy();
					Account account = null;
					
					try {
					account = bank.getAccount(objMessage.getObject().toString());
					} catch(BankServiceException e) {
						account = bank.getAccountByCprNumber(objMessage.getObject().toString());
					}
					account.setTransactions(null);
					new Queue(message.getJMSReplyTo()).sendObject(account);
				}
			} catch (JMSException e) {
				e.printStackTrace();
			} catch (BankServiceException e) {
				e.printStackTrace();
				try {
					new Queue(message.getJMSReplyTo()).sendObject(new ErrorResponse(503, "Error contacting the bank"));
				} catch (JMSException e1) {
					e1.printStackTrace();
				}
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