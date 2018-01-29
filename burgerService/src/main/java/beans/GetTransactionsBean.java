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
 *	This bean fetches the transaction log for a given user from the fast money bank
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destination", propertyValue = NamingConstants.getTransactionsQueue) })
public class GetTransactionsBean implements MessageListener {
	@Resource(lookup = NamingConstants.connectionFactory)
	ConnectionFactory connectionFactory;
	
	public void onMessage(Message message) {
		if (message instanceof ObjectMessage) {
			ObjectMessage objMessage = (ObjectMessage) message;

			try {
				System.out.println("BEAN 2");
				System.out.println("Account ID: " + objMessage.getObject().toString());
				Transaction[] transactions = getAccountTransactions(objMessage.getObject().toString());
				System.out.println("BEAN 2.1");

				if (transactions == null) {
					System.out.println("BEAN 3");
					if (message.getJMSReplyTo() != null) {
						new Queue(message.getJMSReplyTo()).sendObject(new ErrorResponse(404, "User did not exist"));
						System.out.println("BEAN 4");
					}
					return;
				}

				if (message.getJMSReplyTo() != null) {
					if (transactions.length == 0) {
						new Queue(message.getJMSReplyTo()).sendObject("[]");
						return;
					}
					new Queue(message.getJMSReplyTo()).sendObject(transactions);
					System.out.println("BEAN 6");
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

	private Transaction[] getAccountTransactions(String uuid) {
		Transaction[] result = null;
		try {
			BankService bank = new BankServiceProxy();
			Account account = bank.getAccount(uuid);
			System.out.println("Account name: " + account.getUser().getFirstName());
			result = new Transaction[0];
			Transaction[] temp = account.getTransactions();
			if (temp != null)
				result = temp;
		} catch (RemoteException e) {
			System.out.println("Failed in getting Account!");
			e.printStackTrace();
		}
		return result;
	}
}