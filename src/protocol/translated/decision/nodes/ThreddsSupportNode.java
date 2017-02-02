package protocol.translated.decision.nodes;

import java.util.Set;

import com.amazonaws.services.kms.model.UnsupportedOperationException;

import protocol.CollectiveProtocol;
import protocol.translated.decision.DecisionNode;
import service.ProtocolPicker.Protocol;
import service.Thredds;

public class ThreddsSupportNode implements DecisionNode
{
    private CollectiveProtocol collective;
    
    public ThreddsSupportNode(CollectiveProtocol collective)
    {
	this.collective = collective;
    }

    @Override
    public DecisionNode decide(Set<Protocol> protocols)
    {
	Set<Protocol> supportedProtocols = Thredds.getSupportedProtocols(collective.getDataset());
	protocols.retainAll(supportedProtocols);
	if (protocols.isEmpty()) {
	    return new NoSuitableProtocol();
	} else {
	    return new ProtocolSupportsQueryNode(collective);
	}
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
