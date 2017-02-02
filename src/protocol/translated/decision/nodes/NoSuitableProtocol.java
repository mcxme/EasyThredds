package protocol.translated.decision.nodes;

import java.util.Set;

import protocol.translated.decision.DecisionNode;
import service.ProtocolPicker.Protocol;

public class NoSuitableProtocol implements DecisionNode
{
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
	return Protocol.None;
    }

}
