package beans;

import javax.annotation.Resource;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import com.google.gson.Gson;

import util.ErrorResponse;
import util.NamingConstants;
import util.Queue;
import util.UserManager;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destination", propertyValue = NamingConstants.getBarCodesQueue) })
public class CreateBarcodesBean implements MessageListener {
	@Resource(lookup = NamingConstants.connectionFactory)
	ConnectionFactory connectionFactory;

	public void onMessage(Message message) {
		System.out.println("BarBean 0");
		if (message instanceof ObjectMessage) {
			ObjectMessage objMessage = (ObjectMessage) message;
			
			System.out.println("BarBean 1");

			try {
				System.out.println(objMessage.getObject().toString());
				String userId = objMessage.getObject().toString();
				System.out.println("BarBean 2");
				String[] barcodes = UserManager.generateAndReturnBarcodes(userId);
				System.out.println("BarBean 3");
				
				if (barcodes == null) {
					if (message.getJMSReplyTo() != null) {
						new Queue(message.getJMSReplyTo()).sendObject(new ErrorResponse(404, "User did not exist"));
						return;
					} 
				}
				System.out.println("BarBean 4");
				
				System.out.println(String.format("Barcodes were refreshed for user: '%s'", userId));
				
				if (message.getJMSReplyTo() != null) {
					new Queue(message.getJMSReplyTo()).sendObject(new Gson().toJson(barcodes));
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
			}
		} else
			try {
				if (message.getJMSReplyTo() != null)
					new Queue(message.getJMSReplyTo()).sendObject(new ErrorResponse(503, "JMS payload was not an ObjectMessage"));
			} catch (JMSException e) {
				e.printStackTrace();
			}
	}

}