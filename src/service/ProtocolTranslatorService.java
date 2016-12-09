package service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/translate")
public class ProtocolTranslatorService
{

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public Response translate(@Context UriInfo info) {
		
		
		
		return Response.ok().build();
	}
	
}
