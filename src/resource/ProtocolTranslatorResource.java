package resource;

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
import javax.ws.rs.core.UriInfo;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.translated.util.VariableReader;
import service.ProtocolPicker.Protocol;
import service.TranslationService;

/**
 * This class specifies the valid URL for this servlet.
 */
@Path("")
public class ProtocolTranslatorResource
{
    private ConfigReader config;
    private final static Logger LOGGER = Logger.getLogger(ProtocolTranslatorResource.class.getSimpleName());

    @PostConstruct
    public void init()
    {
	LOGGER.info("Initializing...");
	this.config = ConfigReader.getInstace();
	VariableReader.getInstance();
    }

    @PreDestroy
    public void close()
    {
	LOGGER.info("Closing...");
	this.config.close();
	VariableReader.getInstance().close();
    }

    @GET
    @Path("ncss/translate/{dataset: .*}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response translateNcss(@Context UriInfo info, @PathParam("dataset") List<PathSegment> datasetSegments)
    {
	return processProtocol(info, datasetSegments, Protocol.Ncss);
    }
    
    @GET
    @Path("cdmremote/translate/{dataset: .*}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response translateCdmRemote(@Context UriInfo info, @PathParam("dataset") List<PathSegment> datasetSegments)
    {
	return processProtocol(info, datasetSegments, Protocol.CdmRemote);
    }
    
    @GET
    @Path("opendap/translate/{dataset: .*}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response translateOpenDap(@Context UriInfo info, @PathParam("dataset") List<PathSegment> datasetSegments)
    {
	return processProtocol(info, datasetSegments, Protocol.OpenDap);
    }
    
    @GET
    @Path("dap4/translate/{dataset: .*}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response translateDap4(@Context UriInfo info, @PathParam("dataset") List<PathSegment> datasetSegments)
    {
	return processProtocol(info, datasetSegments, Protocol.Dap4);
    }
    
    @GET
    @Path("translate/{dataset: .*}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response translate(@Context UriInfo info, @PathParam("dataset") List<PathSegment> datasetSegments)
    {
	return processProtocol(info, datasetSegments, Protocol.None);
    }
    
    @GET
    @Path("stats")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getStats()
    {
	String stats = TranslationService.getStats();
	return Response.ok(stats).build();
    }
    
    private Response processProtocol(UriInfo info, List<PathSegment> datasetSegments, Protocol protocol)
    {
	// extract the query from the request
	CollectiveProtocol query = new CollectiveProtocol(this.config.getThreddsUrl(), datasetToString(datasetSegments), queryToString(info));

	URI translatedUri;
	if (protocol == Protocol.None) {
	    translatedUri = TranslationService.translate(query);
	} else {
	    translatedUri = TranslationService.translate(query, protocol);
	}
	
	LOGGER.info(translatedUri.toString());
	ResponseBuilder response = Response.seeOther(translatedUri);

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
