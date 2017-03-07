package util;

import protocol.reader.IReader;
import protocol.translated.TranslatedProtocol;

/**
 * Helps to measure the performance for translating and reading a given protocol.
 */
public class MeasureUtil
{
    public static boolean PRINT = false;
    
    private MeasureUtil() {}

    /**
     * Measures the translation and full iteration n times. Optionally, the data
     * set can be prefetched to hold coordinate data in a cache.
     */
    public static double[] measurePerformanceMillis(int nRepetitions,
	    TranslatedProtocol translatedProtocol, boolean prefetchFirst) {
	if (prefetchFirst) {
	    measurePerformanceMillis(translatedProtocol);
	}
	
	return measurePerformanceMillis(nRepetitions, translatedProtocol);
    }
    
    /**
     * Measures the translation and full iteration n times. No prefetching.
     */
    public static double[] measurePerformanceMillis(int nRepetitions,
	    TranslatedProtocol translatedProtocol) {
	double[] performance = new double[nRepetitions];
	for (int i = 0; i < nRepetitions; i++) {
	    performance[i] = measurePerformanceMillis(translatedProtocol);
	}
	
	return performance;
    }
    
    /**
     * Measures the translation and full iteration once.
     */
    public static double measurePerformanceMillis(TranslatedProtocol translated) {
	if (PRINT)
	    System.out.println("---- " + translated.getProtocolName() + "\t----");
	
	long start = System.nanoTime();
	long size = 0;
	try (IReader reader = translated.getReader()) {
	    size = reader.iterateAllData();
	} catch (Exception e) {
	    throw new IllegalStateException("Failed to measure the execution time", e);
	}
	
	long millis = (System.nanoTime() - start) / (1_000_000);
	double relativePerformance = (double) size / millis;

	if (PRINT)
	{
	    System.out.println("\tquery: " + translated.getTranslatedHttpUrl().toString());
	    System.out.println("\ttime: " + millis + "ms (" + ((double) millis / (1000 * 60)) + " min)");
	    System.out.println("\tsize: " + size + " bytes (" + ((double) size / (1024 * 1024)) + " MB)");
	    System.out.println("\t-> " + String.format("%.2f", relativePerformance) + " kB/s");
	}

	return millis;
    }
    
}
