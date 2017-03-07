package protocol.translated.decision.nodes;

import java.util.Set;

import protocol.translated.decision.DecisionNode;
import service.ProtocolPicker.Protocol;

/**
 * This final decision node holds the protocol selected by the decision tree
 * process.
 */
public class BestProtocolNode implements DecisionNode
{
    private Protocol protocol;
    
    public BestProtocolNode(Protocol protocol)
    {
	if (protocol == Protocol.None || protocol == Protocol.Random) {
	    throw new IllegalArgumentException("The best protocol has to be a specific one and not random or None");
	}
	
	this.protocol = protocol;
    }
    
    public BestProtocolNode(Set<Protocol> protocol)
    {
	if (protocol == null || protocol.size() != 1) {
	    throw new IllegalArgumentException("There has to be exactly one protocol left");
	}
	
	this.protocol = protocol.iterator().next();
    }

    @Override
    public DecisionNode decide(Set<Protocol> protocols)
    {
	throw new UnsupportedOperationException("There is nothing to decide!");
    }

    @Override
    public boolean isFinal()
    {
	return true;
    }

    @Override
    public Protocol getFinal()
    {
	return protocol;
    }

}
