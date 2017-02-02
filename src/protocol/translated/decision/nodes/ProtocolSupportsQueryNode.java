package protocol.translated.decision.nodes;

import java.util.Iterator;
import java.util.Set;

import com.amazonaws.services.kms.model.UnsupportedOperationException;

import protocol.CollectiveProtocol;
import protocol.translated.TranslatedProtocol;
import protocol.translated.decision.DecisionNode;
import service.ProtocolPicker;
import service.ProtocolPicker.Protocol;

public class ProtocolSupportsQueryNode implements DecisionNode
{
    
    private CollectiveProtocol collective;
    
    public ProtocolSupportsQueryNode(CollectiveProtocol collective) {
	this.collective = collective;
    }

    @Override
    public DecisionNode decide(Set<Protocol> protocols)
    {
	Iterator<Protocol> it = protocols.iterator();
	while (it.hasNext()) {
	    Protocol protocol = it.next();
	    TranslatedProtocol translated = ProtocolPicker.pickByName(protocol, collective);
	    if (!translated.canTranslate(collective)) {
		it.remove();
	    }
	}
	
	if (protocols.isEmpty()) {
	    return new NoSuitableProtocol();
	} else if (protocols.size() == 1) {
	    return new BestProtocolNode(protocols);
	} else {
	    throw new java.lang.UnsupportedOperationException("Not implemented");
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
