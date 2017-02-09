package performance;

import java.util.ArrayList;
import java.util.List;

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
	List<CollectiveProtocol> protocols = new ArrayList<>();
	for (int i = 0; i < N_QUERIES; i++) {
	    protocols.add(QueryGenerator.getRandCollective());
	}

	System.out.println("generated " + protocols.size() + " queries");
	measureBest(protocols);
	CleanUtil.cleanAuxFiles();
    }
    
    private static void measureBest(List<CollectiveProtocol> protocols) {
	
	int ncssBest = 0;
	int opendapBest = 0;
	int cdmremoteBest = 0;
	
	// all queries are measured for each of the three protocols
	ETAPrinter eta = ETAPrinter.init("queries", protocols.size() * 3);
	
	for (CollectiveProtocol collective : protocols) {
	    TranslatedProtocol ncss = ProtocolPicker.pickByName(Protocol.Ncss, collective);
	    double ncssPerformance = measureAveragePerformance(ncss);
	    eta.update(1);
	    TranslatedProtocol opendap = ProtocolPicker.pickByName(Protocol.CdmRemote, collective);
	    double opendapPerformance = measureAveragePerformance(opendap);
	    eta.update(1);
	    TranslatedProtocol cdmremote = ProtocolPicker.pickByName(Protocol.OpenDap, collective);
	    double cdmremotePerformance = measureAveragePerformance(cdmremote);
	    eta.update(1);

	    switch (getBest(ncssPerformance, opendapPerformance, cdmremotePerformance)) {
	    case Ncss: ncssBest += 1; break;
	    case OpenDap: opendapBest += 1; break;
	    case CdmRemote: cdmremoteBest += 1; break;
	    default:
		throw new UnsupportedOperationException("can only measure OPeNDAP, CdmRemote and NCSS at the moment");
	    }
	}

	System.out.println("Evalutation of " + N_QUERIES + " random queries with " + N_REPETITIONS + " repetitions each:");
	System.out.println("NCSS: " + ncssBest);
	System.out.println("OPeNDAP: " + opendapBest);
	System.out.println("CdmRemote: " + cdmremoteBest);
    }
    
    private static double measureAveragePerformance(TranslatedProtocol protocol) {
	double[] measurements = MeasureUtil.measurePerformanceMillis(N_REPETITIONS, protocol, PREFETCH_ALLOWED);
	return new Mean().evaluate(measurements);
    }
    
    private static Protocol getBest(double ncssAverage, double opendapAverage, double cdmremoteAverage) {
	if (ncssAverage >= opendapAverage && ncssAverage >= cdmremoteAverage) {
	    return Protocol.Ncss;
	} else if (opendapAverage >= ncssAverage && opendapAverage >= cdmremoteAverage) {
	    return Protocol.OpenDap;
	} else {
	    assert (cdmremoteAverage >= ncssAverage && cdmremoteAverage >= opendapAverage);
	    return Protocol.CdmRemote;
	}
    }
}
