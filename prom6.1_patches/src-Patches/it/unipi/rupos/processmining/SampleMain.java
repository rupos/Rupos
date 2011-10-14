package it.unipi.rupos.processmining;

import java.io.File;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagram;
import org.processmining.models.graphbased.directed.bpmn.BPMNDiagramExt;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;

import org.processmining.contexts.cli.ProMFactory;
import org.processmining.contexts.cli.ProMManager;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.petrinet.replay.ReplayAction;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessSetting;
import org.processmining.plugins.petrinet.replay.conformance.PNVisualizzeJS;
import org.processmining.plugins.petrinet.replay.conformance.TotalConformanceResult;
import org.processmining.plugins.petrinet.replay.performance.PerformanceVisualJS;
import org.processmining.plugins.petrinet.replay.performance.TotalPerformanceResult;


/**
 * @author Dipartimento di Informatica - Rupos
 *
 */
public class SampleMain {
    public static void main(String [] args) throws Exception {
	
    	//String logFile = "../prom5_log_files/TracceRupos.mxml";
    	//String netFile = "../prom5_log_files/TracceRuposAlpha.pnml";
    	//String logFile = "../prom5_log_files/InviaFlusso.mxml";
    	//String netFile = "../prom5_log_files/InviaFlussoWoped.pnml";
    	//String logFile = "../prom5_log_files/InviaFlusso.mxml";
    	//String netFile = "../prom5_log_files/InviaFlussoWoped.pnml";
    	
	//String logFile = "../prom5_log_files/provepar.mxml";
    	//String netFile = "../prom5_log_files/provepar3xProm6.pnml";
	//String logFile = "../prom5_log_files/Export_Protocollo.xes";
    	//String netFile = "../prom5_log_files/porva.pnml";
	String logFile = "../prom5_log_files/wsfm.mxml";
    	String netFile = "../prom5_log_files/residency.pnml";
        String BpmnFile = "../prom5_log_files/Residency.xpdl";
	//String netFile = "../prom5_log_files/ReteAdHocRicorsionePerformace3xProm6.pnml";
        //String logFile = "../prom5_log_files/sequence.mxml";
    	//String netFile = "../prom5_log_files/seqAlphahiddenx6.pnml";
    	//String logFile = "../prom5_log_files/sequence.mxml";
    	//String netFile = "../prom5_log_files/sequence_prom6.pnml";
    	
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
	settings.setAction(ReplayAction.INSERT_DISABLED_MATCH, false);
	settings.setAction(ReplayAction.INSERT_DISABLED_MISMATCH, false);
	
	
	long startFitness = System.currentTimeMillis();
	// TotalConformanceResult fitness = engine.getConformance(log, settings);
	// System.out.println("Fitness: " + fitness);
	long endFitness = System.currentTimeMillis();

	//visualizza i dati di conformance con nella pagina html 
	//PNVisualizzeJS js = new PNVisualizzeJS(manager.getPluginContext().getConnectionManager());
	//js.generateJS("../javascrips/conformance.html", engine.net, fitness);
	


	System.out.println("Conformance for a single TRACE");

	long startFitness2 = System.currentTimeMillis();
	//fitness = engine.getConformance(log.get(0), settings);
	//System.out.println("Fitness: " + fitness);
	long endFitness2 = System.currentTimeMillis();
	
	
	System.out.println("Time Conformance single call " + (endFitness - startFitness));
	System.out.println("Time Conformance multiple calls " + (endFitness2 - startFitness2));
	
	
	long startPerformance= System.currentTimeMillis();
	// TotalPerformanceResult performance = engine.getPerformance(log, settings);
	// System.out.println(performance);
	long endPerformance = System.currentTimeMillis();

	long startPerformance2 = System.currentTimeMillis();
	//TotalPerformanceResult performance = engine.getPerformance(log.get(3), settings);
	//System.out.println("Conformance: " + performance);
	long endPerformance2 = System.currentTimeMillis();

	//PerformanceVisualJS js2 = new PerformanceVisualJS(manager.getPluginContext().getConnectionManager());
	//js2.generateJS("../javascrips/Performance.html", engine.net, performance.getList().get(0));
	
	
	System.out.println("Time Performance single call " + (endPerformance - startPerformance));
	System.out.println("Time Performance multiple calls " + (endPerformance2 - startPerformance2));


	//traslate BPMN to  PN

	BPMNDiagram bpmn = manager.openBpmn(BpmnFile);
	//Petrinet pne = manager.getBpmntoPn(bpmn);
	PetriNetEngine pn = manager.getBpmntoPn(bpmn);

	System.out.println(pn.net);
	XLog logs = manager.openLog(logFile);

	ReplayFitnessSetting settings2 = manager.suggestSettings(pn.net, logs);
	settings2.setAction(ReplayAction.INSERT_ENABLED_MATCH, true);
	settings2.setAction(ReplayAction.INSERT_ENABLED_INVISIBLE, true);
	settings2.setAction(ReplayAction.REMOVE_HEAD, false);
	settings2.setAction(ReplayAction.INSERT_ENABLED_MISMATCH, false);
	settings2.setAction(ReplayAction.INSERT_DISABLED_MATCH, true);
	settings2.setAction(ReplayAction.INSERT_DISABLED_MISMATCH, false);
		
	TotalConformanceResult fitnesstrasl = manager.getConformance(pn.net, logs, settings2,pn.marking);
	
	System.out.println(fitnesstrasl);
	
	BPMNDiagramExt bpmnext = manager.getBPMNwithAnalysis(fitnesstrasl);
	 
	 File f = new File("/home/spagnolo1/Dropbox/Casa/pathname.xpdl");
	 if(!f.exists()){
		  f.createNewFile();
	 }
	 manager.writefilebpmn(f, bpmnext);

	settings2.setAction(ReplayAction.INSERT_DISABLED_MATCH, false);
	 
	TotalPerformanceResult performance1 = manager.getPerformance(pn.net, logs, settings2,pn.marking);
	System.out.println("Performance: " + performance1);
	System.out.println("Performance:0 " +performance1.getListperformance().get(0));
	 
	bpmnext = manager.getBPMNwithAnalysis(performance1);
	 
	 File f2 = new File("/home/spagnolo1/Dropbox/Casa/pathnamep.xpdl");
	 if(!f2.exists()){
		  f2.createNewFile();
	 }
	manager.writefilebpmn(f2, bpmnext);
	

	PerformanceVisualJS js22 = new PerformanceVisualJS(manager.getPluginContext().getConnectionManager());
		
	js22.generateJS("../javascrips/PerformancedaBpmn.html", pn.net, performance1.getListperformance().get(0).getList(),performance1.getListperformance().get(0).getMaparc());
	


	PNVisualizzeJS js = new PNVisualizzeJS(manager.getPluginContext().getConnectionManager());
	js.generateJS("../javascrips/conformance.html", pn.net, fitnesstrasl);


	
	manager.closeContext();
    }
}
