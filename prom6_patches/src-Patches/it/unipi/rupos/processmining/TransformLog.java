package it.unipi.rupos.processmining;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.processmining.contexts.cli.ProMFactory;
import org.processmining.contexts.cli.ProMManager;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.plugins.log.exporting.ExportLogMxml;
import org.processmining.plugins.petrinet.replay.ReplayAction;
import org.processmining.plugins.petrinet.replayfitness.PerformanceResult;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessSetting;
import org.processmining.plugins.petrinet.replayfitness.TotalFitnessResult;
import org.processmining.plugins.petrinet.replayfitness.TotalPerformanceResult;
import org.processmining.plugins.petrinet.replayfitness.PerformanceVisualJS;
import org.processmining.plugins.petrinet.replayfitness.PNVisualizzeJS;

import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.RemoveWithValues;

/**
 * @author Dipartimento di Informatica - Rupos
 *
 */
public class TransformLog {
    public static void main(String [] args) throws Exception {
		String logFile = "../prom5_log_files/official_review_little.mxml";
		String log2File = "../prom5_log_files/official_review_little2.mxml";
		String netFile = "../prom5_log_files/official_review6.pnml";
	    	
		ProMManager manager = new ProMFactory().createManager();
	
		XLog log = manager.openLog(logFile);
		
		System.out.println("Log size: " + log.size());

		XLog logNew = new XLogImpl( new XAttributeMapImpl());
		for (String key : log.getAttributes().keySet()) {
			logNew.getAttributes().put(key, log.getAttributes().get(key));
		}
		for (XTrace trace : log.subList(0, 20)) {
			XTrace traceNew = new XTraceImpl(new XAttributeMapImpl());
			for (String key : trace.getAttributes().keySet()) {
				traceNew.getAttributes().put(key, trace.getAttributes().get(key));
			}
			int xCount = 0;
			int iEvent = 0;
			int lastResult = 0;
			for (XEvent event : trace) {
				XEvent eventNew = new XEventImpl(new XAttributeMapImpl());
				if (iEvent == 0) {
					for (int i=1; i<=10; i++) {
						eventNew.getAttributes().put("result"+i, new XAttributeLiteralImpl("result"+i, "undefined"));
					}
				}
				iEvent++;
				
				String eventName = event.getAttributes().get("concept:name").toString();
				
				XAttributeImpl newName = null;
				if (eventName.startsWith("get review")) {
					newName = new XAttributeLiteralImpl("concept:name", "get review");
				}
				else if (eventName.startsWith("time-out")) {
					newName = new XAttributeLiteralImpl("concept:name", "time-out");
				}
				else {
					newName = new XAttributeLiteralImpl("concept:name", eventName);
				}
				
				eventNew.getAttributes().put("concept:name", newName);
					
				for (String key : event.getAttributes().keySet()) {
					if (key.equals("concept:name"))
						continue;

					if (!key.equals("result") || !eventName.startsWith("get review")) {
						eventNew.getAttributes().put(key, event.getAttributes().get(key));
						continue;
					}
					
					XAttributeLiteral attr = (XAttributeLiteral) event.getAttributes().get(key);
					
					lastResult++;
					String resultCode = "result" + lastResult;
					
					XAttributeImpl attrNew = new XAttributeLiteralImpl(resultCode, attr.getValue());
					eventNew.getAttributes().put(resultCode, attrNew);
				}
				traceNew.add(eventNew);
			}
			logNew.add(traceNew);
		}
		
		manager.exportLog(logNew, new File(log2File));
		
	    	
		PetriNetEngine engine = manager.createPetriNetEngine(netFile);

		ReplayFitnessSetting settings = engine.suggestSettings(log);
		System.out.println("Settings: " + settings);
		settings.setAction(ReplayAction.INSERT_ENABLED_MATCH, true);
		settings.setAction(ReplayAction.INSERT_ENABLED_INVISIBLE, true);
		settings.setAction(ReplayAction.REMOVE_HEAD, false);
		settings.setAction(ReplayAction.INSERT_ENABLED_MISMATCH, false);
		settings.setAction(ReplayAction.INSERT_DISABLED_MATCH, false);
		settings.setAction(ReplayAction.INSERT_DISABLED_MISMATCH, false);
		
		
		TotalFitnessResult fitness = engine.getFitness(logNew, settings);
		System.out.println("Fitness: " + fitness);
		
		//visualizza i dati di conformance con nella pagina html 
		PNVisualizzeJS js = new PNVisualizzeJS();
		js.generateJS("../javascrips/reviewConformance.html", engine.net, fitness);

		TotalPerformanceResult performance = engine.getPerformance(logNew, settings);
		//System.out.println(performance);

		PerformanceVisualJS js2 = new PerformanceVisualJS();
		js2.generateJS("../javascrips/mreviewPerformance.html", engine.net, performance.getList().get(0));
		
		
		System.out.println("+++++++++++++ Mining +++++++++++++");
		
		inferReview(logNew, performance, 1);
		inferReview(logNew, performance, 2);
		inferReview(logNew, performance, 3);
		
		inferReviewer(logNew, performance, "Pete");
		
		//inferActionByReviewer(logNew, performance);
		
		manager.closeContext();

    }

    /*
	private static void inferActionByReviewer(XLog logNew,
			TotalPerformanceResult performance) {
		Set<String> reviewers = new HashSet<String>();
		int i=0;
		for (Map<Place, PerformanceResult> perf : performance.getList()) {
			for (XEvent event : logNew.get(i)) {
				if (!event.getAttributes().containsKey("org:resource"))
					continue;

				String eventName = event.getAttributes().get("concept:name").toString();
				if (!eventName.startsWith("get review"))
					continue;

				
				String rev = ((XAttributeLiteral)event.getAttributes().get("org:resource")).getValue();
				if ("__INVALID__".equals(rev))
					continue;
				reviewers.add(rev);
			}
			i++;
		}
		
		List<String> rev_list = new Vector<String>();
		rev_list.addAll(reviewers);

		PrintStream stream = new PrintStream(new File("../datamininig/actionByRev.arff"));
		stream.println("@relation booking");
		stream.println("");
		stream.printf("@attribute rev {");
		for (int iRev=0; iRev<rev_list.size(); iRev++) {
			String rev = rev_list.get(iRev);
			stream.printf(rev);
			if (iRev < rev_list.size()-1)
				stream.printf(",");
		}
		stream.printf("\n");
		stream.printf("@result %s {accept, reject, undefined}\n");
		stream.printf("@action %s {Time out 3}\n");
		stream.println("");
		stream.println("@data");

		
		i=0;
		for (Map<Place, PerformanceResult> perf : performance.getList()) {
			Map<String, String> rev_res = new HashMap<String, String>();
			for (String rev : rev_list) {
				rev_res.put(rev, "undefined");
			}
			for (XEvent event : logNew.get(i)) {
				for (int j=1; j<=3; j++) {
					if (!event.getAttributes().containsKey("result"+j))
						continue;

					String eventName = event.getAttributes().get("concept:name").toString();
					if (!eventName.startsWith("get review "+j))
						continue;
					
					String result = ((XAttributeLiteral)event.getAttributes().get("result"+j)).getValue();
					String rev = ((XAttributeLiteral)event.getAttributes().get("org:resource")).getValue();
					rev_res.put(rev, result);
				}
			}
			for (int j=0; j<rev_list.size(); j++) {
				stream.printf("%s", rev_res.get(rev_list.get(j)));
				if (j < rev_list.size()-1)
					stream.printf(",");
			}
			stream.printf("\n");
			i++;
		}
		stream.flush();
		stream.close();
		

		get review 3+complete TO [[19, 2], [5]]
		
		// Print weka results
		Instances source = new Instances(new BufferedReader(new FileReader("../datamininig/reviewer_"+name+".arff")));
		source.setClassIndex(rev_list.indexOf(name));
		J48 j48 = new J48();
		j48.setUnpruned(true);        // using an unpruned J48
		 // meta-classifier
		FilteredClassifier fc = new FilteredClassifier();
		RemoveWithValues rm = new RemoveWithValues();
		//rm.setAttributeIndex(String.valueOf(rev_list.indexOf(name)));
		rm.setAttributeIndex(String.valueOf(rev_list.indexOf(name)+1));
		rm.setNominalIndices("3");
		fc.setFilter(rm );
		fc.setClassifier(j48);
		// train and make predictions
		fc.buildClassifier(source);
		
		System.out.println(j48);
	}
*/
	private static void inferReviewer(XLog logNew,
			TotalPerformanceResult performance, String name) throws Exception {

		Set<String> reviewers = new HashSet<String>();
		int i=0;
		for (Map<Place, PerformanceResult> perf : performance.getList()) {
			for (XEvent event : logNew.get(i)) {
				if (!event.getAttributes().containsKey("org:resource"))
					continue;

				String eventName = event.getAttributes().get("concept:name").toString();
				if (!eventName.startsWith("get review"))
					continue;

				
				String rev = ((XAttributeLiteral)event.getAttributes().get("org:resource")).getValue();
				if ("__INVALID__".equals(rev))
					continue;
				reviewers.add(rev);
			}
			i++;
		}
		
		List<String> rev_list = new Vector<String>();
		rev_list.addAll(reviewers);

		PrintStream stream = new PrintStream(new File("../datamininig/reviewer_" +name+ ".arff"));
		stream.println("@relation booking");
		stream.println("");
		for (String rev : rev_list) {
			stream.printf("@attribute %s {accept, reject, undefined}\n", rev);
		}
		stream.println("");
		stream.println("@data");

		
		i=0;
		for (Map<Place, PerformanceResult> perf : performance.getList()) {
			Map<String, String> rev_res = new HashMap<String, String>();
			for (String rev : rev_list) {
				rev_res.put(rev, "undefined");
			}
			for (XEvent event : logNew.get(i)) {
				for (int j=1; j<=3; j++) {
					if (!event.getAttributes().containsKey("result"+j))
						continue;

					String eventName = event.getAttributes().get("concept:name").toString();
					if (!eventName.startsWith("get review "+j))
						continue;
					
					String result = ((XAttributeLiteral)event.getAttributes().get("result"+j)).getValue();
					String rev = ((XAttributeLiteral)event.getAttributes().get("org:resource")).getValue();
					rev_res.put(rev, result);
				}
			}
			for (int j=0; j<rev_list.size(); j++) {
				stream.printf("%s", rev_res.get(rev_list.get(j)));
				if (j < rev_list.size()-1)
					stream.printf(",");
			}
			stream.printf("\n");
			i++;
		}
		stream.flush();
		stream.close();
		
		
		// Print weka results
		Instances source = new Instances(new BufferedReader(new FileReader("../datamininig/reviewer_"+name+".arff")));
		source.setClassIndex(rev_list.indexOf(name));
		J48 j48 = new J48();
		j48.setUnpruned(true);        // using an unpruned J48
		 // meta-classifier
		FilteredClassifier fc = new FilteredClassifier();
		RemoveWithValues rm = new RemoveWithValues();
		//rm.setAttributeIndex(String.valueOf(rev_list.indexOf(name)));
		rm.setAttributeIndex(String.valueOf(rev_list.indexOf(name)+1));
		rm.setNominalIndices("3");
		fc.setFilter(rm );
		fc.setClassifier(j48);
		// train and make predictions
		fc.buildClassifier(source);
		
		System.out.println(j48);
	}

	private static void inferReview(XLog logNew,
			TotalPerformanceResult performance, int review) throws Exception {
		
		PrintStream stream = new PrintStream(new File("../datamininig/review" +review+ ".arff"));
		stream.println("@relation booking");
		stream.println("");
		stream.println("@attribute result1 {accept, reject, undefined}");
		stream.println("@attribute result2 {accept, reject, undefined}");
		stream.println("@attribute result3 {accept, reject, undefined}");

		stream.println("");
		stream.println("@data");

		
		int i=0;
		for (Map<Place, PerformanceResult> perf : performance.getList()) {
			String result1="", result2="", result3="";
			for (XEvent event : logNew.get(i)) {
				if (event.getAttributes().containsKey("result1"))
					result1 = ((XAttributeLiteral)event.getAttributes().get("result1")).getValue();
				if (event.getAttributes().containsKey("result2"))
					result2 = ((XAttributeLiteral)event.getAttributes().get("result2")).getValue();
				if (event.getAttributes().containsKey("result3"))
					result3 = ((XAttributeLiteral)event.getAttributes().get("result3")).getValue();
			}
			stream.printf("%s,", result1);
			stream.printf("%s,", result2);
			stream.printf("%s\n", result3);
			i++;
		}
		stream.flush();
		stream.close();
		
		
		// Print weka results
		Instances source = new Instances(new BufferedReader(new FileReader("../datamininig/review"+review+".arff")));
		source.setClassIndex(review-1);
		J48 j48 = new J48();
		j48.setUnpruned(true);        // using an unpruned J48
		 // meta-classifier
		FilteredClassifier fc = new FilteredClassifier();
		RemoveWithValues rm = new RemoveWithValues();
		rm.setAttributeIndex(String.valueOf(review));
		rm.setNominalIndices("3");
		fc.setFilter(rm );
		fc.setClassifier(j48);
		// train and make predictions
		//j48.buildClassifier(source);
		fc.buildClassifier(source);
		
		System.out.println(j48);
	}
}
