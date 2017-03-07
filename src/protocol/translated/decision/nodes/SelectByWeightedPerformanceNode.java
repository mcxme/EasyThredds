package protocol.translated.decision.nodes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.amazonaws.services.kms.model.UnsupportedOperationException;

import protocol.translated.decision.DecisionNode;
import service.ProtocolPicker.Protocol;

/**
 * This decision node picks the best remaining protocol based on the weighted
 * floating average of each of the protocols.
 * 
 * The protocol with the highest average performance is selected.
 */
public class SelectByWeightedPerformanceNode implements DecisionNode
{
    
    // The weight for historical data
    private static final double FLOATING_WEIGHT = 0.3;
    
    // Some manually predetermined performances
    private static final double INITIAL_NCSS_PERFORMANCE = 1.0;
    private static final double INITIAL_OPENDAP_PERFORMANCE = 0.4;
    private static final double INITIAL_CDMREMOTE_PERFORMANCE = 0.3;
    
    // Holds the performance value for each protocol
    private static Map<Protocol, Double> protocolPerformance;
    
    private static void initProtocolPerformance() {
	if (protocolPerformance == null) {
	    synchronized (SelectByWeightedPerformanceNode.class)
	    {
		if (protocolPerformance == null) {
		    protocolPerformance = new HashMap<>();
		    protocolPerformance.put(Protocol.Ncss, INITIAL_NCSS_PERFORMANCE);
		    protocolPerformance.put(Protocol.OpenDap, INITIAL_OPENDAP_PERFORMANCE);
		    protocolPerformance.put(Protocol.CdmRemote, INITIAL_CDMREMOTE_PERFORMANCE);
		}
	    }
	}
    }
    
    public static double getPerformance(Protocol protocol) {
	initProtocolPerformance();
	synchronized (protocolPerformance)
	{
	    return protocolPerformance.get(protocol);
	}
    }
    
    public static void clean() {
	initProtocolPerformance();
	synchronized (protocolPerformance)
	{
	    protocolPerformance = null;
	    initProtocolPerformance();
	}
    }
    
    public static void addPerformance(Protocol protocol, double performance) {
	initProtocolPerformance();
	
	synchronized (protocolPerformance)
	{
	    if (!protocolPerformance.containsKey(protocol)) {
		throw new IllegalArgumentException("Unknown protocol " + protocol);
	    }
	    
	    Double currentPerformance = protocolPerformance.get(protocol);
	    currentPerformance = currentPerformance * (1.0 - FLOATING_WEIGHT)
		    + performance * FLOATING_WEIGHT;
	    protocolPerformance.put(protocol, currentPerformance); 
	}
    }
    
    @Override
    public DecisionNode decide(Set<Protocol> protocols)
    {
	Protocol best = Protocol.None;
	double bestPerformance = -1.0;

	initProtocolPerformance();
	synchronized (protocolPerformance)
	{
	    for (Protocol protocol : protocols) {
		Double performance = protocolPerformance.get(protocol);
		if (performance > bestPerformance) {
		    best = protocol;
		    bestPerformance = performance;
		}
	    }
	}
	
	return new BestProtocolNode(best);
    }

    @Override
    public boolean isFinal()
    {
	return false;
    }

    @Override
    public Protocol getFinal()
    {
	throw new UnsupportedOperationException("This is not a final decision");
    }

}
