package service;

import java.net.URI;

import protocol.CollectiveProtocol;
import protocol.translated.TranslatedProtocol;
import protocol.translated.decision.nodes.SelectByWeightedPerformanceNode;
import protocol.translated.util.VariableReader;
import service.ProtocolPicker.Protocol;

/**
 * This service helps in the translation process by selecting the correct
 * protocol and measuring the performance for future calls.
 */
public class TranslationService
{

    // counters for the selection of each of the protocols
    private static long ncssCounter = 0;
    private static long cdmRemoteCounter = 0;
    private static long openDapCounter = 0;
    
    private TranslationService() {}
    
    
    public static long getNcssCounter() {
	return ncssCounter;
    }
    
    public static long getCdmRemoteCounter() {
	return cdmRemoteCounter;
    }
    
    public static long getOpenDapCounter() {
	return openDapCounter;
    }
    
    private static synchronized void updateCounterFor(Protocol protocol) {
	    switch (protocol) {
	    case OpenDap:
		openDapCounter += 1;
		break;
	    case CdmRemote:
		cdmRemoteCounter += 1;
		break;
	    case Ncss:
		ncssCounter += 1;
		break;
	    default:
		throw new IllegalStateException("Only expects OPeNDAP, CdmRemote or NCSS as output type");
	    }
    }
    
    /**
     * Translated the collective protocol into the desired protocol and measures
     * the performance.
     */
    public static URI translate(CollectiveProtocol in, TranslatedProtocol out) {
	// TODO time measurement for translation only not suitable
	// measure the translation time
	long start = System.currentTimeMillis();
	URI translated = out.getTranslatedHttpUrl();
	long totalMillis = System.currentTimeMillis() - start;
	double totalSeconds = (double) totalMillis / 1000.0;

	// store the translation performance for later decisions
	VariableReader vars = out.getDimensionData();
	long nEstimatedItems = vars.getEstimatedQuerySelectionItems(in);
	double itemsPerSecond = ((double) nEstimatedItems) / totalSeconds;
	SelectByWeightedPerformanceNode.addPerformance(out.getType(), itemsPerSecond);
	
	updateCounterFor(out.getType());
	return translated;
    }
    
    /**
     * Translated the collective protocol into the best protocol and measures
     * the performance.
     */
    public static URI translate(CollectiveProtocol in) {
	TranslatedProtocol translated = ProtocolPicker.pickBest(in);
	return translate(in, translated);
    }
    
    /**
     * Translated the collective protocol into the given protocol and measures
     * the performance.
     */
    public static URI translate(CollectiveProtocol in, Protocol protocol) {
	TranslatedProtocol translated = ProtocolPicker.pickByName(protocol, in);
	return translate(in, translated);
    }
    
    /**
     * Returns textual information on the selected protocols and the performance
     */
    public static String getStats() {
	String out = "Translation performance in #items/second (#picked)";
	out += "\nOPeNDAP:\t" + SelectByWeightedPerformanceNode.getPerformance(Protocol.OpenDap)
		+ " (" + getOpenDapCounter() + ")";
	out += "\nNCSS:\t\t" + SelectByWeightedPerformanceNode.getPerformance(Protocol.Ncss)
		+ " (" + getNcssCounter() + ")";
	out += "\nCdmRemote:\t" + SelectByWeightedPerformanceNode.getPerformance(Protocol.CdmRemote)
		+ " (" + getCdmRemoteCounter() + ")";
	
	return out;
    }
    
}
