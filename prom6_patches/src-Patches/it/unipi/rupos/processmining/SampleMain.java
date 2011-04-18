package it.unipi.rupos.processmining;

import org.processmining.contexts.cli.ProMFactory;
import org.processmining.contexts.cli.ProMManager;
import org.deckfour.xes.model.XLog;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessSetting;

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
    }
}