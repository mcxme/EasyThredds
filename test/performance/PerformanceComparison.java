package performance;

import java.awt.Font;
import java.awt.Frame;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.ws.rs.core.UriInfo;

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
import reader.IReader;
import service.ProtocolPicker;

public class PerformanceComparison
{
    
    private static final String TEST_DATASET = "RC1SD-base-08/cloud";
    private static final String THREDDS = ConfigReader.getInstace().getThreddsUrl();
    private static final int REPETITIONS = 1;

    public static void main(String[] args)
    {
	if (args.length == 0) {
	    throw new IllegalArgumentException("expects at least one query");
	}

	List<CollectiveProtocol> input = new ArrayList<>();
	for (int i = 0; i < args.length; i++) {
	    input.add(new CollectiveProtocol(THREDDS, TEST_DATASET, args[i]));
	}
	
	plotPerformance(REPETITIONS, input);
    }
    
    public static void plotPerformance(int nRepetitions, List<CollectiveProtocol> collectiveProtocols) {
	// create the dataset
	final DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
	for (CollectiveProtocol collective : collectiveProtocols) {
	    for (int i = 0; i < ProtocolPicker.N_PROTOCOLS; i++) {
        	TranslatedProtocol translated = ProtocolPicker.pickByIndex(i, collective);
        	long[] measuredMillis = measurePerformanceMillis(nRepetitions, translated);
        	List<Long> convertedMillis = new ArrayList<Long>(measuredMillis.length);
        	for (int j = 0; j < measuredMillis.length; j++) { convertedMillis.add(measuredMillis[j]); }
        	dataset.add(convertedMillis, collective.toString(), translated.getProtocolName());
	    }
	}
	
	// create the boxplot
        final CategoryAxis xAxis = new CategoryAxis("Protocol");
        final NumberAxis yAxis = new NumberAxis("Time");
        yAxis.setAutoRangeIncludesZero(false);
        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setFillBox(false);
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);

        final JFreeChart chart = new JFreeChart(
            "Box-and-Whisker Demo",
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
       
    private static long[] measurePerformanceMillis(int nRepetitions,
	    TranslatedProtocol translatedProtocol) {
	long[] performance = new long[nRepetitions];
	for (int i = 0; i < nRepetitions; i++) {
	    performance[i] = measurePerformanceMillis(translatedProtocol);
	}
	
	return performance;
    }
    
    private static long measurePerformanceMillis(TranslatedProtocol translated) {
	long start = System.nanoTime();
	long size = 0;
	try (IReader reader = translated.getReader()) {
	    size = reader.iterateAllData();
	} catch (Exception e) {
	    throw new IllegalStateException("Failed to measure the execution time", e);
	}
	long millis = (System.nanoTime() - start) / (1_000_000);
	System.out.println(translated.getProtocolName() + " - " + millis + "ms for " + size + " bytes");
	return millis;
    }
}
