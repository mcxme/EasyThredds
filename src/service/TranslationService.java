package service;

import java.net.URI;

import protocol.CollectiveProtocol;
import protocol.translated.TranslatedProtocol;
import protocol.translated.decision.nodes.SelectByWeightedPerformanceNode;
import protocol.translated.util.VariableReader;
import service.ProtocolPicker.Protocol;

public class TranslationService
{

    private TranslationService() {}
    
    public static URI translate(CollectiveProtocol in, TranslatedProtocol out) {
	// measure the translation time
	long start = System.currentTimeMillis();
	URI translated = out.getTranslatedHttpUrl();
	long totalMillis = System.currentTimeMillis() - start;

	// store the translation performance for later decisions
	long nEstimatedItems = VariableReader.getInstance().getEstimatedQuerySelectionItems(in);
	double millisPerItem = ((double) totalMillis) / nEstimatedItems;
	SelectByWeightedPerformanceNode.addPerformance(out.getType(), millisPerItem);
	
	return translated;
    }
    
    public static URI translate(CollectiveProtocol in) {
	TranslatedProtocol translated = ProtocolPicker.pickBest(in);
	return translate(in, translated);
    }
    
    public static URI translate(CollectiveProtocol in, Protocol protocol) {
	TranslatedProtocol translated = ProtocolPicker.pickByName(protocol, in);
	return translate(in, translated);
    }
    
}
