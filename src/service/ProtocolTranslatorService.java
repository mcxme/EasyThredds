package service;

import java.net.URI;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.ProtocolPicker;
import protocol.translated.TranslatedProtocol;

@Path("/translate")
public class ProtocolTranslatorService
{
    private ConfigReader config;
    private final static Logger LOGGER = Logger.getLogger(ProtocolTranslatorService.class.getName());

    @PostConstruct
    public void init()
    {
	LOGGER.info("Initializing...");
	this.config = ConfigReader.getInstace();
    }

    @PreDestroy
    public void close()
    {
	LOGGER.info("Closing...");
	this.config.close();
    }

    @GET
    @Path("{dataset: .*}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response translate(@Context UriInfo info, @PathParam("dataset") List<PathSegment> datasetSegments)
    {
	ResponseBuilder response;
	try {
	    // extract the query from the request
	    CollectiveProtocol query = new CollectiveProtocol(
		    this.config.getThreddsUrl(),
		    datasetToString(datasetSegments),
		    queryToString(info));
	    
	    TranslatedProtocol translated = ProtocolPicker.pickBest(query);
	    // TODO redirect!
//	    response = Response.seeOther(translated.getTranslatedUrl());
	    response = Response.ok(translated.getTranslatedUrl().toString());
	} catch (IllegalStateException | IllegalArgumentException e) {
	    response = Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage());
	}

	return response.build();
    }
    
    private String queryToString(UriInfo info) {
	StringBuilder builder = new StringBuilder();
	MultivaluedMap<String, String> params = info.getQueryParameters();
	for (String key : params.keySet()) {
	    builder.append(key);
	    builder.append("=");
	    builder.append(params.getFirst(key));
	    builder.append("&");
	}
	
	// remove the last '&' character
	if (builder.length() > 0) {
	    builder.deleteCharAt(builder.length() - 1);
	}
	
	return builder.toString();
    }
    
    private String datasetToString(List<PathSegment> datasetSegments) {
	if (datasetSegments.isEmpty()) {
	    throw new IllegalArgumentException("no dataset specified");
	}
	
	StringBuilder builder = new StringBuilder();
	for (PathSegment segment : datasetSegments) {
	    builder.append(segment.getPath());
	    builder.append("/");
	}
	
	builder.deleteCharAt(builder.length() - 1);	
	return builder.toString();
    }

}
