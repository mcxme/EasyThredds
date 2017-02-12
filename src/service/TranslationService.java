package service;

import java.net.URI;

import protocol.CollectiveProtocol;
import protocol.translated.TranslatedProtocol;
import protocol.translated.decision.nodes.SelectByWeightedPerformanceNode;
import protocol.translated.util.VariableReader;
import service.ProtocolPicker.Protocol;

public class TranslationService
{

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
    
    public static URI translate(CollectiveProtocol in, TranslatedProtocol out) {
	// measure the translation time
	long start = System.currentTimeMillis();
	URI translated = out.getTranslatedHttpUrl();
	long totalMillis = System.currentTimeMillis() - start;
	double totalSeconds = (double) totalMillis / 60.0;

	// store the translation performance for later decisions
	long nEstimatedItems = VariableReader.getInstance().getEstimatedQuerySelectionItems(in);
	double itemsPerSecond = ((double) nEstimatedItems) / totalSeconds;
	SelectByWeightedPerformanceNode.addPerformance(out.getType(), itemsPerSecond);
	
	updateCounterFor(out.getType());
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
    
    public static String getStats() {
	String out = "Performance in #items/second (#picked)";
	out += "\nOPeNDAP:\t" + SelectByWeightedPerformanceNode.getPerformance(Protocol.OpenDap)
		+ " (" + getOpenDapCounter() + ")";
	out += "\nNCSS:\t\t" + SelectByWeightedPerformanceNode.getPerformance(Protocol.Ncss)
		+ " (" + getNcssCounter() + ")";
	out += "\nCdmRemote:\t" + SelectByWeightedPerformanceNode.getPerformance(Protocol.CdmRemote)
		+ " (" + getCdmRemoteCounter() + ")";
	
	return out;
    }
    
}
