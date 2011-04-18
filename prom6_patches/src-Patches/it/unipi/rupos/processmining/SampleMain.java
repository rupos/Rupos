package it.unipi.rupos.processmining;

import org.processmining.contexts.cli.ProMFactory;
import org.processmining.contexts.cli.ProMManager;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessSetting;
import org.processmining.plugins.petrinet.replayfitness.TotalFitnessResult;

public class SampleMain {
    public static void main(String [] args) throws Exception {
	
	ProMManager manager = new ProMFactory().createManager();
	PetriNetEngine engine = manager.createPetriNetEngine("../prom5_log_files/TracceRuposAlpha.pnml");
	System.out.println(engine);

	engine = manager.createPetriNetEngine("../prom5_log_files/TracceRuposAlpha.pnml");
	System.out.println(engine);

	XLog log = manager.openLog("../prom5_log_files/TracceRupos.mxml");
	System.out.println("Log size: " + log.size());

	ReplayFitnessSetting settings = engine.suggestSettings(log);
	System.out.println("Settings: " + settings);

	long startFitness = System.currentTimeMillis();
	TotalFitnessResult fitness = engine.getFitness(log, settings);
	long endFitness = System.currentTimeMillis();

	System.out.println("Fitness: " + fitness);


	System.out.println("Fitness for a single TRACE");

	long startFitness2 = System.currentTimeMillis();
	for (XTrace trace:log) {
	    fitness = engine.getFitness(log.get(0), settings);
	    // System.out.println("Fitness: " + fitness);
	}
	long endFitness2 = System.currentTimeMillis();

	System.out.println("Time fitness single call " + (endFitness - startFitness));
	System.out.println("Time fitness multiple calls " + (endFitness2 - startFitness2));
    }
}