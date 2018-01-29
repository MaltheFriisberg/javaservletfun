package restapi;

import java.io.Serializable;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import core.Transaction;
import util.ErrorResponse;
import util.NamingConstants;
import util.Queue;

/**
 * The TransactionEndpoint class is accessible under the /transaction directory of our REST API
 * Requests to the /transaction directory are handled in this class
 */
@Path("transaction")
public class TransactionEndpoint {
	
	@Resource(lookup = NamingConstants.connectionFactory)
	ConnectionFactory connectionFactory;

	@Resource(lookup = NamingConstants.createTransactionQueue)
	Destination destination;
	
	/**
	 * Gets the transactions associated with the user id
	 * @param uuid The user's bank account ID
	 * @return A Transaction array of the user's transactions
	 */
	@GET
	@Path("/{uuid}")
	public Response getTransactionsWithId(@PathParam("uuid") String uuid) {
	    if(uuid == null || uuid.trim().length() == 0) {
	        return Response.serverError().entity("UUID cannot be blank").build();
	    }
	    
	    try {
			Serializable result = new Queue(NamingConstants.getTransactionsQueue).sendObjectWithReply(uuid);
			if(result instanceof String) {
				return Response.ok(new Transaction[0], MediaType.APPLICATION_JSON).build();
			} else if(result instanceof ErrorResponse) {
				ErrorResponse err = (ErrorResponse)result;
				return Response.status(err.status).entity(err.errorMessage).build();
			}
			return Response.ok(result, MediaType.APPLICATION_JSON).build();
		} catch (JMSException | NamingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity("Entity not found for UUID: " + uuid).build();
		}
	}
	
	/**
	 * Performs a new transaction, given a barcode token, merchant id, price and description.
	 * @param transaction The transaction containing the relevant information required to transfer the money
	 * @return Error object on failure, nothing on success
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createTransaction(Transaction transaction) {
		System.out.println("Trans 1");
		try {
			Serializable result = new Queue(NamingConstants.createTransactionQueue).sendObjectWithReply(transaction);
			System.out.println("Trans 2");
			if(result instanceof ErrorResponse) {
				ErrorResponse err = (ErrorResponse)result;
				return Response.status(err.status).entity(err.errorMessage).build();
			}
			return Response.ok(result).build();
		} catch (JMSException | NamingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
		}
	}
}
