package beans;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import core.Transaction;
import util.ErrorResponse;
import util.NamingConstants;
import util.Queue;
import util.UserManager;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceProxy;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destination", propertyValue = NamingConstants.createTransactionQueue) })
public class CreateTransactionBean implements MessageListener {
	@Resource(lookup = NamingConstants.connectionFactory)
	ConnectionFactory connectionFactory;
	//this method is invoked once an object makes it into the createTransactionQueue
	public void onMessage(Message message) {
		if (message instanceof ObjectMessage) {
			ObjectMessage objMessage = (ObjectMessage) message;

			System.out.println("Transx1");
			try {
				// ApplicationUser userInput = (ApplicationUser) objMessage.getObject();
				Transaction transaction = (Transaction) objMessage.getObject(); //Transaction recieved here
				System.out.println("Transx 2");
				boolean success = createTransaction(transaction.getToken(), transaction.getOtherId(),
						transaction.getAmount(), transaction.getDescription());

				System.out.println("Transx 3");
				if (message.getJMSReplyTo() != null) {
					if (success) {
						new Queue(message.getJMSReplyTo())
								.sendObject("Succesfully transfered: " + transaction.getAmount() + " from account: "
										+ transaction.getToken() + " to account: " + transaction.getOtherId());
					} else {
						new Queue(message.getJMSReplyTo()).sendObject(new ErrorResponse(400, "Could not perform transaction"));
					}
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean createTransaction(String token, String merchantId, BigDecimal price, String description) {
		try {
			BankService bank = new BankServiceProxy();
			String userId = UserManager.getAccountIdFromBarcode(token);
			if (userId != null) {
				bank.transferMoneyFromTo(userId, merchantId, price, description);
				UserManager.removeBarcode(userId, token);
				return true;
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}
}