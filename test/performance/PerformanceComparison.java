package performance;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import config.ConfigReader;
import protocol.CollectiveProtocol;
import protocol.translated.TranslatedProtocol;
import protocol.translated.util.VariableReader;
import reader.IReader;
import service.ProtocolPicker;
import util.CleanUtil;

public class PerformanceComparison
{
    
    private static final String TEST_DATASET = "RC1SD-base-08/cloud";
    private static final String THREDDS = ConfigReader.getInstace().getThreddsUrl();
    private static final int REPETITIONS = 2;

    public static void main(String[] args)
    {
	if (args.length == 0) {
	    throw new IllegalArgumentException("expects at least one query");
	}

	List<CollectiveProtocol> input = new ArrayList<>();
	for (int i = 0; i < args.length; i++) {
	    input.add(new CollectiveProtocol(THREDDS, TEST_DATASET, args[i]));
	}

	try {
	plotPerformance(REPETITIONS, input);
	} catch (Exception e) {
	    throw new IllegalStateException("Failed to measure the performance", e);
	} finally {
	    VariableReader.getInstance().close();
	    CleanUtil.cleanAuxFiles();
	}
    }
    
    public static void plotPerformance(int nRepetitions, List<CollectiveProtocol> collectiveProtocols) {
	// create the dataset
	final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
	for (CollectiveProtocol collective : collectiveProtocols) {
	    for (int i = 0; i < ProtocolPicker.N_PROTOCOLS; i++) {
        	try {
        	    TranslatedProtocol translated = ProtocolPicker.pickByIndex(i, collective);
        	    double[] measuredMillis = measurePerformanceMillis(nRepetitions, translated);
        	    List<Double> convertedMillis = new ArrayList<Double>(measuredMillis.length);
        	    for (int j = 0; j < measuredMillis.length; j++) { convertedMillis.add(measuredMillis[j]); }
        	    dataset.add(convertedMillis, collective.toString(), translated.getProtocolName());
        	} catch (Exception e) {
        	    e.printStackTrace();
        	}
	    }
	}
	
	// create the boxplot
        final CategoryAxis xAxis = new CategoryAxis("Protocol");
        final NumberAxis yAxis = new NumberAxis("Performance [kB/s]");
        yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox(true);
        renderer.setUseOutlinePaintForWhiskers(true);
        renderer.setSeriesPaint(0, Color.CYAN);
        renderer.setSeriesOutlinePaint(0, Color.BLACK);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesOutlinePaint(1, Color.BLACK);
        renderer.setArtifactPaint(Color.GRAY);
        renderer.setMaximumBarWidth(0.10);
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        final JFreeChart chart = new JFreeChart(
            "Performance for different queries using different protocols",
            new Font("SansSerif", Font.BOLD, 14),
            plot,
            true
        );
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(450, 270));

        // create the frame
        ApplicationFrame frame = new ApplicationFrame("Protocol Performance");
        frame.setContentPane(chartPanel);
        frame.setExtendedState(frame.getExtendedState() | Frame.MAXIMIZED_BOTH);
        frame.pack();
        frame.setVisible(true);
    }
       
    private static double[] measurePerformanceMillis(int nRepetitions,
	    TranslatedProtocol translatedProtocol) {
	double[] performance = new double[nRepetitions];
	for (int i = 0; i < nRepetitions; i++) {
	    performance[i] = measurePerformanceMillis(translatedProtocol);
	}
	
	return performance;
    }
    
    private static double measurePerformanceMillis(TranslatedProtocol translated) {
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
	System.out.println("\tquery: " + translated.getTranslatedHttpUrl().toString());
	System.out.println("\ttime: " + millis + "ms (" + ((double) millis / (1000 * 60)) + " min)");
	System.out.println("\tsize: " + size + " bytes (" + ((double)size / (1024 * 1024)) + " MB)");
	System.out.println("\t-> " + String.format("%.2f", relativePerformance) + " kB/s");
	return relativePerformance;
    }
}
