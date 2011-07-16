package it.unipi.rupos.processmining;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.Map;

import org.processmining.contexts.cli.ProMFactory;
import org.processmining.contexts.cli.ProMManager;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.plugins.petrinet.replay.ReplayAction;
import org.processmining.plugins.petrinet.replayfitness.PerformanceResult;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessSetting;
import org.processmining.plugins.petrinet.replayfitness.TotalFitnessResult;
import org.processmining.plugins.petrinet.replayfitness.TotalPerformanceResult;
import org.processmining.plugins.petrinet.replayfitness.PerformanceVisualJS;
import org.processmining.plugins.petrinet.replayfitness.PNVisualizzeJS;

/**
 * @author Dipartimento di Informatica - Rupos
 *
 */
public class SampleDataMining {
    public static void main(String [] args) throws Exception {
	String logFile = "../prom5_log_files/datamining.mxml";
	String netFile = "../prom5_log_files/datamining6.pnml";
    	
	ProMManager manager = new ProMFactory().createManager();
	PetriNetEngine engine = manager.createPetriNetEngine(netFile);
	System.out.println(engine);

	engine = manager.createPetriNetEngine(netFile);
	System.out.println(engine);

	XLog log = manager.openLog(logFile);
	System.out.println("Log size: " + log.size());

	ReplayFitnessSetting settings = engine.suggestSettings(log);
	System.out.println("Settings: " + settings);
	settings.setAction(ReplayAction.INSERT_ENABLED_MATCH, true);
	settings.setAction(ReplayAction.INSERT_ENABLED_INVISIBLE, true);
	settings.setAction(ReplayAction.REMOVE_HEAD, false);
	settings.setAction(ReplayAction.INSERT_ENABLED_MISMATCH, false);
	settings.setAction(ReplayAction.INSERT_DISABLED_MATCH, true);
	settings.setAction(ReplayAction.INSERT_DISABLED_MISMATCH, false);
	
	
	TotalFitnessResult fitness = engine.getFitness(log, settings);
	System.out.println("Fitness: " + fitness);
	
	//visualizza i dati di conformance con nella pagina html 
	PNVisualizzeJS js = new PNVisualizzeJS();
	js.generateJS("../javascrips/miningConformace.html", engine.net, fitness);

	TotalPerformanceResult performance = engine.getPerformance(log, settings);
	System.out.println(performance);

	PerformanceVisualJS js2 = new PerformanceVisualJS();
	js2.generateJS("../javascrips/miningPerformance.html", engine.net, performance.getList().get(0));
	
	
	System.out.println("+++++++++++++ PreMining +++++++++++++");
	PrintStream stream = new PrintStream(new File("../datamininig/booking.arff"));
	stream.println("@relation booking");
	stream.println("");
	stream.println("@attribute country {Italy, UK, USA, Spain}");
	stream.println("@attribute religion {Christian, Muslim, Scientology, Atheist}");
	
	stream.println("@attribute p9sync real");
	//stream.println("@attribute p8sync {no, small, medium, high}");
	//stream.println("@attribute p9sync {no, small, medium, high}");
	stream.println("");
	stream.println("@data");

	
	Place p9 = null;
	Place p8 = null;
	for (Place place : engine.net.getPlaces()) {
		if (place.getLabel().equals("p9")) {
			p9 = place;
		}
		else if (place.getLabel().equals("p8")) {
			p8 = place;
		} 
	}
	int i=0;
	for (Map<Place, PerformanceResult> perf : performance.getList()) {
		PerformanceResult p9result = perf.get(p9);
		float p9sync = p9result.getSynchTime() / 60 / 60 / 1000; 
		PerformanceResult p8result = perf.get(p8);
		float p8sync = p8result.getSynchTime() / 60 / 60 / 1000; 
		stream.printf("%s,", log.get(i).get(0).getAttributes().get("destination"));
		stream.printf("%s,", log.get(i).get(0).getAttributes().get("religion"));
		//stream.printf("%s,%d\n", log.get(i).get(0).getAttributes().get("destination"), (int)p9sync);
		//stream.printf("%s,%f\n", log.get(i).get(0).getAttributes().get("destination"), p9sync);
		//stream.printf("%s,", convertToString(p8sync));
		//stream.printf("%s\n", convertToString(p9sync));
		stream.printf("%f\n", p9sync);
		i++;
	}
	stream.flush();
	stream.close();
	
	manager.closeContext();
    }

	private static String convertToString(float value) {
		if (value <= 0)
			return "no";
		else if (value <= 0.5)
			return "small";
		else if (value <= 2)
			return "medium";
		else
			return "high";
	}
}
