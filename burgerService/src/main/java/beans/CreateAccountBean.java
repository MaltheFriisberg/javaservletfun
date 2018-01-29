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
import core.ApplicationUser;
import dtu.ws.fastmoney.BankService;
import dtu.ws.fastmoney.BankServiceException;
import dtu.ws.fastmoney.BankServiceProxy;
import dtu.ws.fastmoney.User;
import util.ErrorResponse;
import util.NamingConstants;
import util.Queue;
import util.UserManager;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destination", propertyValue = NamingConstants.createAccountQueue) })
public class CreateAccountBean implements MessageListener {
	@Resource(lookup = NamingConstants.connectionFactory)
	ConnectionFactory connectionFactory;
	
	public void onMessage(Message message) {
		if (message instanceof ObjectMessage) {
			ObjectMessage objMessage = (ObjectMessage) message;

			try {
				ApplicationUser userInput = (ApplicationUser) objMessage.getObject();
				String givenName = userInput.getGivenName();
				String surname = userInput.getSurname();
				String cprNumber = userInput.getCprNumber();

				if (givenName == null || surname == null || cprNumber == null || givenName.length() < 2
						|| surname.length() < 2 || cprNumber.length() < 1) {
					new Queue(message.getJMSReplyTo()).sendObject(
							new ErrorResponse(503, "Invalid arguments. Expects givenName, surname and cprNumber"));
					return;
				}

				BankService bank = new BankServiceProxy();

				String id = bank.createAccountWithBalance(new User(cprNumber, givenName, surname),
						new BigDecimal(1000));

				ApplicationUser user = UserManager.addUser(givenName, surname, cprNumber, id);

				System.out.println("A user was created with the follow information: given name: \n" + givenName + " "
						+ surname + ", cprNr: " + cprNumber + " & id: " + id);

				String[] barcodes = UserManager.generateAndReturnBarcodes(id);

				System.out.println("--------------- Barcodes -------------------");

				for (int i = 0; i < barcodes.length; i++)
					System.out.println("Barcode nr: " + i + " - " + barcodes[i]);

				System.out.println("---------------------------------------------");
				if (message.getJMSReplyTo() != null) {
					new Queue(message.getJMSReplyTo()).sendObject(user);
				}
			} catch (JMSException e) {
				e.printStackTrace();
				try {
					if (message.getJMSReplyTo() != null) {
						new Queue(message.getJMSReplyTo()).sendObject(new ErrorResponse(503, "JMS Error"));
					}
				} catch (JMSException e1) {
					e1.printStackTrace();
				}
			} catch (BankServiceException e) {
				try {
					new Queue(message.getJMSReplyTo()).sendObject(new ErrorResponse(409, "Account already exists!"));
				} catch (JMSException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
}