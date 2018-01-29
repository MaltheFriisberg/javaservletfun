package restapi;

import java.io.Serializable;

import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.NamingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import core.ApplicationUser;
import util.ErrorResponse;
import util.NamingConstants;
import util.Queue;

/**
 * The UserEndpoint class is accessible under the /users directory of our REST API
 * Requests to the /users directory are handled in this class
 */
@Path("users")
public class UserEndpoint {
	
	@Resource(lookup = NamingConstants.connectionFactory)
	ConnectionFactory connectionFactory;
	
	/**
	 * Gets the user given a bank account ID.
	 * @param uuid The user's bank account ID
	 * @return An ApplicationUser object
	 */
	@GET
	@Path("/{uuid}")
	public Response getUserWithId(@PathParam("uuid") String uuid) {
	    if(uuid == null || uuid.trim().length() == 0) {
	        return Response.serverError().entity("UUID cannot be blank").build();
	    }
	    
	    try {
			Serializable result = new Queue(NamingConstants.getAccountQueue).sendObjectWithReply(uuid);
			if (result instanceof ErrorResponse) {
				ErrorResponse err = (ErrorResponse)result;
				return Response.status(err.status).entity(err.errorMessage).build();
			} else
				return Response.ok(result, MediaType.APPLICATION_JSON).build();
		} catch (JMSException | NamingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity("Entity not found for UUID: " + uuid).build();
		}
	}
	
	/**
	 * { "cprNumber": "123456-4474","givenName": "heysa", "surname": "java2" }
	 * Creates a new user given a first name, last name and CPR number.
	 * @param user The ApplicationUser object with information about the new user
	 * @return The new ApplicationUser object, with the supplied information plus their bank account id
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createUser(ApplicationUser user) {
		System.out.println("CreateUser 1");
		try {
			System.out.println("CreateUser 2");
			Serializable result = new Queue(NamingConstants.createAccountQueue).sendObjectWithReply(user);
			System.out.println("CreateUser 3");
			if (result instanceof ErrorResponse) {
				ErrorResponse err = (ErrorResponse)result;
				return Response.status(err.status).entity(err.errorMessage).build();
			} else
				return Response.ok(result, MediaType.APPLICATION_JSON).build();
		} catch (JMSException | NamingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST).entity("Error: " + e.getMessage()).build();
		}
	}
	/**
	 * Deletes the user associated with the uuid given at the end of the url
	 * @param uuid
	 * @return
	 */
	@DELETE
	@Path("/{uuid}")
	public Response deleteUser(@PathParam("uuid") String uuid) {
		
		if(uuid == null || uuid.trim().length() == 0) {
	        return Response.serverError().entity("UUID cannot be blank").build();
	    }
	    
	    try {
			Serializable result = new Queue(NamingConstants.deleteAccountQueue).sendObjectWithReply(uuid);
			if (result instanceof ErrorResponse) {
				ErrorResponse err = (ErrorResponse)result;
				return Response.status(err.status).entity(err.errorMessage).build();
			} else
				return Response.ok(result, MediaType.APPLICATION_JSON).build();
		} catch (JMSException | NamingException e) {
			e.printStackTrace();
			return Response.status(Response.Status.NOT_FOUND).entity("Entity not found for UUID: " + uuid).build();
		}
	    
		
	}
	
}