package protocol.translated.decision;

import java.util.Set;

import service.ProtocolPicker.Protocol;

public interface DecisionNode
{
    DecisionNode decide(Set<Protocol> protocols);
    
    boolean isFinal();
    
    Protocol getFinal();
}
