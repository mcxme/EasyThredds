package protocol.translated.decision;

import java.util.Set;

import protocol.CollectiveProtocol;
import protocol.translated.decision.nodes.ThreddsSupportNode;
import service.ProtocolPicker;
import service.ProtocolPicker.Protocol;

public class DecisionTree
{
    // prohibit instantiation
    private DecisionTree() { }
    
    public static Protocol decide(CollectiveProtocol collective) {
	Set<Protocol> protocols = ProtocolPicker.getProtocols();
	DecisionNode node = new ThreddsSupportNode(collective);
	while (!node.isFinal()) {
	    node = node.decide(protocols);
	}
	
	return node.getFinal();
    }
    
}
