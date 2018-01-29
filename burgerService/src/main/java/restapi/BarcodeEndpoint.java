	package restapi;

import java.io.Serializable;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import util.ErrorResponse;
import util.NamingConstants;
import util.Queue;

/**
 * The BarcodeEndpoint class is accessible under the /barcode directory of our REST API
 * Requests to the /barcode directory are handled in this class
 */
@Path("barcode")
public class BarcodeEndpoint {
	
	@Resource(lookup = NamingConstants.connectionFactory)
	ConnectionFactory connectionFactory;

	@Resource(lookup = NamingConstants.getBarCodesQueue)
	Destination destination;
	
	/**
	 * Refills the storage of barcode tokens for the given user, and returns those.
	 * @param uuid The user's bank account ID
	 * @return A list of barcode tokens
	 */
	@POST
	@Consumes(MediaType.TEXT_PLAIN)
	public Response createBarcodesById(String uuid) {
		System.out.println("Bar 1");
	    if(uuid == null || uuid.trim().length() == 0) {
	        return Response.status(Response.Status.BAD_REQUEST).entity("UUID cannot be blank").build();
	    }
	    try {
	    	System.out.println("Bar 2");
			Serializable result = new Queue(NamingConstants.getBarCodesQueue).sendObjectWithReply(uuid);
			System.out.println("Bar 3");
			if (result == null)
				return Response.status(Response.Status.NOT_FOUND).entity("No user found").build();
			if (result instanceof ErrorResponse) {
				ErrorResponse err = (ErrorResponse)result;
				return Response.status(err.status).entity(err.errorMessage).build();
			}
			try {
				return Response.ok(new JSONParser().parse(result.toString()), MediaType.APPLICATION_JSON).build();
			} catch (ParseException e) {
				return Response.status(Response.Status.CONFLICT).entity(result).build();
			}
		} catch (JMSException | NamingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity("Entity not found for UUID: " + uuid).build();
		}
	}
}
