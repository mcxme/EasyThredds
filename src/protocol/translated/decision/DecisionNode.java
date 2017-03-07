package protocol.translated.decision;

import java.util.Set;

import service.ProtocolPicker.Protocol;

/**
 * This interface specifies the functionality of each decision tree node.
 */
public interface DecisionNode
{
    /**
     * This might limit the number of remaining valid protocols.
     * @return the next node in the decision process
     */
    DecisionNode decide(Set<Protocol> protocols);
    
    /**
     * Indicates whether the decision node holds a final decision or not.
     */
    boolean isFinal();
    
    /**
     * Return the final decision (only if {@link #isFinal()} is true})
     */
    Protocol getFinal();
}
