package performance;

import java.util.ArrayList;
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
    
    private static final int N_QUERIES = 5;//1_000;
    private static final int N_REPETITIONS = 1;//1_000;
    private static final boolean PREFETCH_ALLOWED = true;
    
    private static int lastProgress = -1;

    public static void main(String[] args)
    {
	Set<CollectiveProtocol> protocols = QueryGenerator.getNRandCollectives(N_QUERIES);
	System.out.println("generated " + protocols.size() + " queries");
	new RandomPerformanceComparison().measureBest(protocols);
	CleanUtil.cleanAuxFiles();
    }
    
    private void measureBest(Set<CollectiveProtocol> protocols) {
	List<ProtocolComparison> results = new ArrayList<>(protocols.size());
	
	// all queries are measured for each of the three protocols
	ETAPrinter eta = ETAPrinter.init("queries", protocols.size() * 3);
	
	CleanUtil.cleanAuxFiles();
	for (CollectiveProtocol collective : protocols) {
	    TranslatedProtocol ncss = ProtocolPicker.pickByName(Protocol.Ncss, collective);
	    double ncssPerformance = measureAveragePerformance(ncss);
	    eta.update(1);
	    CleanUtil.cleanAuxFiles();
	    TranslatedProtocol opendap = ProtocolPicker.pickByName(Protocol.OpenDap, collective);
	    double opendapPerformance = measureAveragePerformance(opendap);
	    eta.update(1);
	    CleanUtil.cleanAuxFiles();
	    TranslatedProtocol cdmremote = ProtocolPicker.pickByName(Protocol.CdmRemote, collective);
	    double cdmremotePerformance = measureAveragePerformance(cdmremote);
	    eta.update(1);
	    CleanUtil.cleanAuxFiles();

	    
	    results.add(new ProtocolComparison(collective, ncssPerformance, cdmremotePerformance, opendapPerformance));
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
	
	System.out.println("Evalutation of " + N_QUERIES + " random queries with " + N_REPETITIONS + " repetitions each:");
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
	public ProtocolComparison(CollectiveProtocol query, double ncssMillis, double cdmRemoteMillis, double openDapMillis) {
	    this.query = query;
	    this.ncssMillis = ncssMillis;
	    this.cdmRemoteMillis = cdmRemoteMillis;
	    this.openDapMillis = openDapMillis;
	}
    }
}
