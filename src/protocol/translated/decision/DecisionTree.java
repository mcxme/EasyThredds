package protocol.translated.decision;

import java.util.Set;

import protocol.CollectiveProtocol;
import protocol.translated.decision.nodes.ThreddsSupportNode;
import service.ProtocolPicker;
import service.ProtocolPicker.Protocol;

/**
 * This class represents a decision tree for selecting the most suitable
 * translation protocol for a given collective protocol. Therefore, several
 * nodes are visited to eliminate possible protocols and to finally pick the
 * best performing one. Note, that this class only represents the root of this
 * decision process.
 */
public class DecisionTree
{
    // prohibit instantiation
    private DecisionTree() { }
    
    public static Protocol decide(CollectiveProtocol collective) {
	// First, all implemented protocols are possible
	Set<Protocol> protocols = ProtocolPicker.getProtocols();
	
	// Use the root node to start the decision process
	DecisionNode node = new ThreddsSupportNode(collective);
	
	// refine the set of possible protocols until a decision was made
	while (!node.isFinal()) {
	    node = node.decide(protocols);
	}
	
	return node.getFinal();
    }
}
