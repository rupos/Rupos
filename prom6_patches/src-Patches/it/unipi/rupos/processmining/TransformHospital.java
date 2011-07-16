package it.unipi.rupos.processmining;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.Map;

import org.processmining.contexts.cli.ProMFactory;
import org.processmining.contexts.cli.ProMManager;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeTimestamp;
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

/**
 * @author Dipartimento di Informatica - Rupos
 *
 */
public class TransformHospital {
    public static void main(String [] args) throws Exception {
		String logFile = "../prom5_log_files/hospital_50.mxml";
		String log2File = "../prom5_log_files/hospital_50_2.mxml";
		String netFile = "../prom5_log_files/datamining6.pnml";
	    	
		ProMManager manager = new ProMFactory().createManager();
	
		XLog log = manager.openLog(logFile);
		
		System.out.println("Log size: " + log.size());

		XLog logNew = new XLogImpl( new XAttributeMapImpl());
		for (String key : log.getAttributes().keySet()) {
			logNew.getAttributes().put(key, log.getAttributes().get(key));
		}
		
		//			      ms    s    m    h    g    m
		long max_time = 1000L * 60 * 60 * 24 * 31 * 6;
		
		for (XTrace trace : log) {
			long time0 = 0;
			int count = 0;
			
			XTrace traceNew = cloneTraceHeader(trace);
			logNew.add(traceNew);

			for (XEvent event : trace) {
				XEvent eventNew = new XEventImpl(new XAttributeMapImpl());
				String eventName = event.getAttributes().get("concept:name").toString();
				
				long time1 = ((XAttributeTimestamp) event.getAttributes().get("time:timestamp")).getValueMillis();
				if (time0 != 0 && time1-time0>max_time) {
					count+=1;
					
					traceNew = cloneTraceHeader(trace);
					String name = ((XAttributeLiteral)traceNew.getAttributes().get("concept:name")).getValue();
					name+="-"+count;
					traceNew.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", name));
					
					logNew.add(traceNew);
				}
				time0 = time1;
				
				for (String key : event.getAttributes().keySet()) {
					eventNew.getAttributes().put(key, event.getAttributes().get(key));
				}
				
				String name = ((XAttributeLiteral)eventNew.getAttributes().get("concept:name")).getValue();
				name = translateName(name);
				eventNew.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", name));
				
				traceNew.add(eventNew);
			}
		}
		
		manager.exportLog(logNew, new File(log2File));
		
		
    }

	private static XTrace cloneTraceHeader(XTrace trace) {
		//System.out.println("-----------------");
		 XTraceImpl traceNew = new XTraceImpl(new XAttributeMapImpl());
			for (String key : trace.getAttributes().keySet()) {
				traceNew.getAttributes().put(key, trace.getAttributes().get(key));
				//System.out.println(key + " : " + trace.getAttributes().get(key));
			}
		return traceNew;
	}
	
	public static String translateName(String name) {
		if ("aanname laboratoriumonderzoek".equals(name))
			return "assumption laboratory";
		if ("ordertarief".equals(name))
			return "order rate";
		
		if ("aanname laboratoriumonderzoek".equals(name))
			return "assumption laboratory";
		
		return name;
	}
}
