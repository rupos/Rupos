package it.unipi.rupos.processmining.plugins;

import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.PluginContext;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;

import org.deckfour.xes.model.XLog;
import org.processmining.connections.logmodel.LogPetrinetConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.connections.logmodel.LogPetrinetConnection;



@Plugin(name = "PetriNetConformance", 
	parameterLabels = { "Log",
			    "PetriNet",
			    "Initial Marking"
	}, 
	returnLabels = { "Conformance Results One",  "Conformance Results Two"}, 
	returnTypes = { String.class, String.class})
public class Conformance {

    @UITopiaVariant(uiLabel = "PetriNet Conformance", 
		    affiliation = "University of Pisa", 
		    author = "Roberto Guanciale", 
		    email ="guancio" + (char) 0x40 + "gmail.com",
		    website = "guancio.wordpress.com"
		    )
    @PluginVariant(variantLabel = "Log, PetriNet and Marking",
		   requiredParameterLabels = {0, 1, 2}
		   )
    public Object[] doConformance(PluginContext context, XLog log, Petrinet net, Marking marking) {
	context.getFutureResult(0).setLabel("(Guancio Result)");
	context.getFutureResult(1).setLabel("(Guancio Result 2)");
	return new Object[] {"Guancio", "Guancio Secondo"};
    }
}