package it.unipi.rupos.processmining;

import org.processmining.contexts.cli.ProMFactory;
import org.processmining.contexts.cli.ProMManager;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.petrinet.replay.ReplayAction;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessSetting;
import org.processmining.plugins.petrinet.replayfitness.TotalFitnessResult;
import org.processmining.plugins.petrinet.replayfitness.TotalPerformanceResult;

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
    	
	String logFile = "../prom5_log_files/recursionprove3.mxml";
    	String netFile = "../prom5_log_files/ReteAdHocRicorsionePerformacexProm6.pnml";
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
	TotalFitnessResult fitness = engine.getFitness(log, settings);
	long endFitness = System.currentTimeMillis();

	System.out.println("Fitness: " + fitness);


	System.out.println("Fitness for a single TRACE");

	long startFitness2 = System.currentTimeMillis();
	int i=0;
	int maxIter = 10;
	for (XTrace trace:log) {
	    fitness = engine.getFitness(trace, settings);
	    // System.out.println("Fitness: " + fitness);
	    if (++i > maxIter)
	    	break;
	}
	long endFitness2 = System.currentTimeMillis();
	
	
	System.out.println("Time fitness single call " + (endFitness - startFitness));
	System.out.println("Time fitness multiple calls " + (endFitness2 - startFitness2));
	
	
	long startPerformance= System.currentTimeMillis();
	TotalPerformanceResult performance = engine.getPerformance(log, settings);
	System.out.println(performance);
	long endPerformance = System.currentTimeMillis();

	long startPerformance2 = System.currentTimeMillis();
	 i=0;
	 maxIter = 10;
	for (XTrace trace:log) {
	    fitness = engine.getFitness(trace, settings);
	    // System.out.println("Fitness: " + fitness);
	    if (++i > maxIter)
	    	break;
	}
	long endPerformance2 = System.currentTimeMillis();

	
	System.out.println("Time Performance single call " + (endPerformance - startPerformance));
	System.out.println("Time Performance multiple calls " + (endPerformance2 - startPerformance2));
	
	manager.closeContext();
    }
}
