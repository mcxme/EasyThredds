package performance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.moment.Mean;

import com.amazonaws.services.kms.model.UnsupportedOperationException;
import com.vdurmont.etaprinter.ETAPrinter;

import protocol.CollectiveProtocol;
import protocol.translated.TranslatedProtocol;
import service.ProtocolPicker;
import service.ProtocolPicker.Protocol;
import util.CleanUtil;
import util.MeasureUtil;
import util.QueryGenerator;

public class RandomPerformanceComparison
{
    
    private static final int N_QUERIES = 20;//1_000;
    private static final int N_REPETITIONS = 3;//1_000;
    private static final boolean PREFETCH_ALLOWED = true;
    
    public static void main(String[] args)
    {
	CleanUtil.cleanAll();
	Set<CollectiveProtocol> protocols = QueryGenerator.getNRandCollectives(N_QUERIES);
	System.out.println("generated " + protocols.size() + " queries:");
	for (CollectiveProtocol query : protocols) {
	    System.out.println(query);
	}
	
	new RandomPerformanceComparison().measureBest(protocols);
	CleanUtil.cleanAll();
    }
    
    private void measureBest(Set<CollectiveProtocol> protocols) {
	List<ProtocolComparison> results = new ArrayList<>(protocols.size());
	
	// all queries are measured for each of the three protocols
	ETAPrinter eta = ETAPrinter.init("queries", protocols.size() * 3);
	
	System.out.println("Running each of the " + protocols.size() + " queries " + N_REPETITIONS + "-time(s)");
	CleanUtil.cleanAuxFiles();
	List<Protocol> translations = new ArrayList<>();
	translations.add(Protocol.Ncss);
	translations.add(Protocol.OpenDap);
	translations.add(Protocol.CdmRemote);
	
	
	for (CollectiveProtocol collective : protocols) {
	    // randomize order
	    Collections.shuffle(translations);
	    ProtocolComparison comparison = new ProtocolComparison(collective);
	    results.add(comparison);
	    // measure the execution time for each protocol
	    for (Protocol p : translations) {
		TranslatedProtocol ncss = ProtocolPicker.pickByName(p, collective);
		double performanceMillis = measureAveragePerformance(ncss);
		comparison.add(p, performanceMillis);
		eta.update(1);
		CleanUtil.cleanAll();
	    }
	}
	
	evaluate(results);
    }
    
    private double measureAveragePerformance(TranslatedProtocol protocol) {
	double[] measurements = MeasureUtil.measurePerformanceMillis(N_REPETITIONS, protocol, PREFETCH_ALLOWED);
	return new Mean().evaluate(measurements);
    }
    
    private Protocol getBest(double ncssAverage, double opendapAverage, double cdmremoteAverage) {
	if (ncssAverage >= opendapAverage && ncssAverage >= cdmremoteAverage) {
	    return Protocol.Ncss;
	} else if (opendapAverage >= ncssAverage && opendapAverage >= cdmremoteAverage) {
	    return Protocol.OpenDap;
	} else {
	    assert (cdmremoteAverage >= ncssAverage && cdmremoteAverage >= opendapAverage);
	    return Protocol.CdmRemote;
	}
    }
    
    private void evaluate(List<ProtocolComparison> results) {
	int dims = QueryGenerator.MAX_DIMS + 1;
	int[] ncssBest = new int[dims];
	int[] opendapBest = new int[dims];
	int[] cdmremoteBest = new int[dims];
	
	for (ProtocolComparison comp : results) {
	    int dim = comp.query.getNDimensions();
	    switch (getBest(comp.ncssMillis, comp.openDapMillis, comp.cdmRemoteMillis)) {
	    case Ncss: ncssBest[dim] += 1; break;
	    case OpenDap: opendapBest[dim] += 1; break;
	    case CdmRemote: cdmremoteBest[dim] += 1; break;
	    default:
		throw new UnsupportedOperationException("can only measure OPeNDAP, CdmRemote and NCSS at the moment");
	    }
	}
	
	System.out.println("Evalutation of " + N_QUERIES + " random queries with " + N_REPETITIONS + " repetition(s) each:");
	for (int i = 0; i < dims; i++) {
	    int totalWithDim = ncssBest[i] + opendapBest[i] + cdmremoteBest[i];
	    if (totalWithDim != 0) {
		System.out.println("////////// " + i + "-dim //////////");
		System.out.println("NCSS:\t" + ncssBest[i]);
		System.out.println("OPeNDAP:\t" + opendapBest[i]);
		System.out.println("CdmRemote:\t" + cdmremoteBest[i]);
	    }
	}
    }
    
    private static class ProtocolComparison {
	public CollectiveProtocol query;
	public double ncssMillis;
	public double cdmRemoteMillis;
	public double openDapMillis;
	public ProtocolComparison(CollectiveProtocol query) {
	    this.query = query;
	}
	
	public void add(Protocol p, double millis) {
	    switch (p) {
	    case Ncss:
		ncssMillis = millis; break;
	    case CdmRemote:
		cdmRemoteMillis = millis; break;
	    case OpenDap:
		openDapMillis = millis; break;
		default:
		    throw new IllegalStateException("Unsupported protocol " + p);
	    }
	}
    }
}
