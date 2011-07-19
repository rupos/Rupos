package org.processmining.plugins.pnml.exporting;

import java.io.File;
import java.io.IOException;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.contexts.uitopia.annotations.UIExportPlugin;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.opennet.OpenNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.pnml.Pnml;

@Plugin(name = "PNML export (Petri net)", returnLabels = {}, returnTypes = {}, parameterLabels = { "Petri net",
		"Open net", "File" }, userAccessible = true)
@UIExportPlugin(description = "PNML files", extension = "pnml")
public class PnmlExportNetToPNML extends PnmlExportNet {

	@PluginVariant(variantLabel = "PNML export (Petri net)", requiredParameterLabels = { 0, 2 })
	//public void exportPetriNetToPNMLFile(UIPluginContext context, Petrinet net, File file) throws IOException {
	    public void exportPetriNetToPNMLFile(PluginContext context, Petrinet net, File file) throws IOException {
		exportPetriNetToPNMLOrEPNMLFile(context, net, file, Pnml.PnmlType.PNML);
	}

	@PluginVariant(variantLabel = "PNML export (Open net)", requiredParameterLabels = { 1, 2 })
	//	public void exportPetriNetToPNMLFile(UIPluginContext context, OpenNet openNet, File file) throws IOException {
	public void exportPetriNetToPNMLFile(PluginContext context, OpenNet openNet, File file) throws IOException {
		exportPetriNetToPNMLOrEPNMLFile(context, openNet, file, Pnml.PnmlType.PNML);
	}
}
