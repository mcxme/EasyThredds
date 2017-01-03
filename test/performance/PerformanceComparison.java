package performance;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.ui.ApplicationFrame;

import protocol.CollectiveProtocol;
import protocol.translated.TranslatedProtocol;
import service.ProtocolPicker;

public class PerformanceComparison
{
    
    private static final String TEST_DATASET = "";
    private static final String THREDDS = "";
    private static final int REPETITIONS = 100;

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
        frame.setExtendedState(frame.getExtendedState() | frame.MAXIMIZED_BOTH);
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
	// TODO measure time of protocol execurtion
	Random rand = new Random(System.nanoTime());
	return Math.abs(rand.nextLong()) % 1000;
    }
}
