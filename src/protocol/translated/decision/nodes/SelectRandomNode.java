package protocol.translated.decision.nodes;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import com.amazonaws.services.kms.model.UnsupportedOperationException;

import protocol.translated.decision.DecisionNode;
import service.ProtocolPicker.Protocol;

/**
 * This decision node randomly picks any of the remaining protocols.
 */
public class SelectRandomNode implements DecisionNode
{

    @Override
    public DecisionNode decide(Set<Protocol> protocols)
    {
	Random rand = new Random();
	int n = protocols.size();
	int j = rand.nextInt(n);
	Iterator<Protocol> it = protocols.iterator();
	
	// skip j - 1 protocols
	for (int i = 0; i < j; i++) it.next();
	
	assert (it.hasNext());
	// take the j-th protocol
	return new BestProtocolNode(it.next());
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
