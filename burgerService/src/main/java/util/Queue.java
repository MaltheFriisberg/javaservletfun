package util;

import java.io.Serializable;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.QueueConnection;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Queue {

	Destination destination;
	static InitialContext ctx;
	static ConnectionFactory connectionFactory;
	QueueConnection connection;

	public Queue() throws NamingException {
		setUp();
	}

	public Queue(String queueName) throws NamingException {
		setUp();
		destination = (Destination) ctx.lookup(queueName);
	}

	public Queue(Destination queue) {
		this.destination = queue;
	}

	public void setUp() throws NamingException {
		if (ctx == null) {
			ctx = new InitialContext();
		}
		if (connectionFactory == null) {
			connectionFactory = (ConnectionFactory) ctx.lookup(NamingConstants.connectionFactory);
		}
	}

	private Serializable sendObjectWithReply(Serializable obj, boolean reply) throws JMSException {
		Destination tmpQueue = null;
		connection = (QueueConnection) connectionFactory.createConnection();
		QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

		MessageProducer producer = session.createProducer(destination); //producer
		ObjectMessage message = session.createObjectMessage();
		message.setObject(obj); //put object on the queue
		if (reply) {
			tmpQueue = session.createTemporaryQueue(); //create temp queue
			message.setJMSReplyTo(tmpQueue); //Consumer(CreateTransactionBean) should send the response to the tempQueue
		}
		producer.send(message);  //send object to queue

		producer.close();
		Serializable o = null;
		if (reply) {
			MessageConsumer consumer = session.createConsumer(tmpQueue); //consuming the temp queue
			connection.start();
			ObjectMessage msg = (ObjectMessage) consumer.receive(); //blocking while listening to the temp queue
			o = msg.getObject();
			System.out.println(o.toString());
			connection.stop();
		}
		session.close();
		connection.close();
		return o;
	}

	public Serializable sendObjectWithReply(Serializable obj) throws JMSException {
		return sendObjectWithReply(obj, true);
	}

	public void sendObject(Serializable obj) throws JMSException {
		sendObjectWithReply(obj, false);
	}

	public Serializable receive() throws JMSException {
		connection = (QueueConnection) connectionFactory.createConnection();

		QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

		MessageConsumer consumer = session.createConsumer(destination);
		connection.start();
		ObjectMessage msg = (ObjectMessage) consumer.receive();
		return msg.getBody(Serializable.class);
	}
}
