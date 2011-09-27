package it.unipi.rupos.processmining;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.processmining.contexts.cli.ProMFactory;
import org.processmining.contexts.cli.ProMManager;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
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
	
	
	TotalPerformanceResult performance = engine.getPerformance(log, settings);
	System.out.println(performance);

	System.out.println("+++++++++++++ PreMining +++++++++++++");
	PrintStream stream = new PrintStream(new File("../datamininig/booking.arff"));
	stream.println("@relation booking");
	stream.println("");
	
	// Evaluate list of attribute possible values
	Map<String, Set<String>> strAttributes = new HashMap<String, Set<String>>();
	for (XTrace trace : log) {
		for (XEvent event : trace) {
			for (Entry<String, XAttribute> attr : event.getAttributes().entrySet()) {
				if (! (attr.getValue() instanceof XAttributeLiteral))
					continue;

				if (!strAttributes.containsKey(attr.getKey())) {
					strAttributes.put(attr.getKey(), new HashSet<String>());
				}
				Set<String> values = strAttributes.get(attr.getKey());
				XAttributeLiteral strA = (XAttributeLiteral)attr.getValue();
				
				if (values.contains(strA.getValue()))
					continue;
				values.add(strA.getValue());
			}
		}
	}
	
	// Prepare attribute headers
	List<String> attrNames = new Vector<String>();
	attrNames.addAll(strAttributes.keySet());
	for (String attrName : attrNames) {
		stream.printf("@attribute %s {", attrName);
		int commas = strAttributes.get(attrName).size() -1;
		for (String value : strAttributes.get(attrName)) {
			stream.print(value);
			if (commas > 0)
				stream.print(",");
			commas--;
		}
		stream.print("}\n");
	}
	
	// Prepare List of place headers
	List<Place> places = new Vector<Place>();
	places.addAll(engine.net.getPlaces());
	for (Place place : places) {
		stream.printf("@attribute %s_wait_value real\n", place.getLabel());
		stream.printf("@attribute %s_sync_value real\n", place.getLabel());
		stream.printf("@attribute %s_wait_label {no, small, medium, high}\n", place.getLabel());
		stream.printf("@attribute %s_sync_label {no, small, medium, high}\n", place.getLabel());
	}
	
	stream.println("");
	stream.println("@data");

	int i=0;
	for (Map<Place, PerformanceResult> perf : performance.getList()) {
		int commas = attrNames.size() + 4 * places.size()-1 ;
		
		// Event attributes
		// I care about only the first event to inspect attributes
		XEvent event = log.get(i).get(0);
		for (String attrName : attrNames) {
			XAttributeLiteral attr = (XAttributeLiteral) event.getAttributes().get(attrName);
			stream.printf("%s", attr.getValue());
			if (commas > 0)
				stream.print(",");
			commas--;
		}

		// Timing attributes
		for (Place place : places) {
			if (! perf.containsKey(place)) {
				stream.print("0,0,no,no");
				commas-=3;
				if (commas > 0)
					stream.print(",");
				commas--;
				continue;
			}
				
			PerformanceResult result = perf.get(place);
			float wait = result.getWaitTime() / 60 / 60 / 1000;
			float sync = result.getSynchTime() / 60 / 60 / 1000; 
			
			stream.printf("%f,", wait);
			commas--;
			stream.printf("%f,", sync);
			commas--;

			stream.printf("%s,", convertToString(wait));
			commas--;
			stream.printf("%s", convertToString(sync));
			if (commas > 0)
				stream.print(",");
			commas--;
		}
		
		stream.printf("\n");

		i++;
	}
	
	/*
	System.out.println("+++++++++++++ PreMining +++++++++++++");
	PrintStream stream = new PrintStream(new File("../datamininig/booking.arff"));
	stream.println("@relation booking");
	stream.println("");
	stream.println("@attribute country {Italy, UK, USA, Spain}");
	stream.println("@attribute religion {Christian, Muslim, Scientology, Atheist}");
	
	stream.println("@attribute p8syncValue real");
	stream.println("@attribute p9syncValue real");
	stream.println("@attribute diffValue real");
	stream.println("@attribute p8sync {no, small, medium, high}");
	stream.println("@attribute p9sync {no, small, medium, high}");
	stream.println("@attribute branch {airReallySlow, airSlow, airSmallSlow, equals, carSmallSlow, carSlow, carReallySlow}");
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
		stream.printf("%f,", p8sync);
		stream.printf("%f,", p9sync);
		stream.printf("%f,", p9sync - p8sync);
		stream.printf("%s,", convertToString(p8sync));
		stream.printf("%s,", convertToString(p9sync));
		stream.printf("%s", convertToString(p9sync, p8sync));
		stream.printf("\n");
		//stream.printf("%f\n", p9sync);
		i++;
	}
	*/
	stream.flush();
	stream.close();
	
	manager.closeContext();
    }

	private static Object convertToString(float p9sync, float p8sync) {
		float diff = p8sync-p9sync;
		if (diff > 2)
			return "airReallySlow";
		if (diff > 1)
			return "airSlow";
		if (diff > 0.5)
			return "airSmallSlow";
		
		if (diff < -2)
			return "carReallySlow";
		if (diff < -1)
			return "carSlow";
		if (diff < -0.5)
			return "carSmallSlow";

		return "equals";
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
