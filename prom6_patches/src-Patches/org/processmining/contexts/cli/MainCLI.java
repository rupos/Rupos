package org.processmining.contexts.cli;

import jargs.gnu.CmdLineParser;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginDescriptor;
import org.processmining.framework.boot.Boot;
import org.processmining.framework.boot.Boot.Level;
import org.processmining.framework.plugin.annotations.Bootable;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.util.CommandLineArgumentList;
import org.processmining.framework.plugin.PluginExecutionResult;


import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitness;
import org.processmining.plugins.petrinet.replay.ReplayAction;
import org.processmining.plugins.petrinet.replayfitness.ReplayFitnessSetting;
import org.processmining.plugins.petrinet.replayfitness.TotalFitnessResult;

import java.util.Properties;

public class MainCLI {
	@Plugin(name = "MainCLI", parameterLabels = {}, returnLabels = {}, returnTypes = {}, userAccessible = false)
	@Bootable
	public Object main(CommandLineArgumentList commandlineArguments) {
		try {
		    boolean printPluginSignatures = true;

		    CLIContext globalContext = new CLIContext();
		    PluginContext context = globalContext.getMainPluginContext();
		    PluginDescriptor openLogPlugin = null;
		    PluginDescriptor alphaPlugin = null;
		    PluginDescriptor fitnessPlugin = null;
		    PluginDescriptor importNetPlugin = null;

		    System.out.println("------------------------------");
		    for (PluginDescriptor plugin : context.getPluginManager().getAllPlugins()) {
			if ("Open XES Log File".equals(plugin.getName()))
			    openLogPlugin = plugin;
			else if ("Alpha Miner".equals(plugin.getName()))
			    alphaPlugin = plugin;
			else if ("FitnessDetailsSettings".equals(plugin.getName()))
			    fitnessPlugin = plugin;
			else if ("Import Petri net from PNML file".equals(plugin.getName()))
			    importNetPlugin = plugin;
			else
			    continue;
			if (printPluginSignatures) {
			    System.out.println(plugin.getName());
			    for (int j = 0; j < plugin.getParameterTypes().size(); j++) {
				System.out.println("  " + j + " ) " + plugin.getMethodLabel(j) + " " + plugin.getParameterTypes(j));
			    }
			}
		    }

		    PluginContext context1 = null;

		    // System.out.println("------------------------------");
		    // System.out.println(openLogPlugin);
		    // System.out.println("------------------------------");
		    // context1 = context.createChildContext("Result of Import Log");
		    // openLogPlugin.invoke(0, context1, "../prom5_log_files/sequence.mxml");
		    // context1.getResult().synchronize();
		    // org.deckfour.xes.model.XLog res = (org.deckfour.xes.model.XLog)context1.getResult().getResult(0);
		    // System.out.println("------------------------------");
		    // // System.out.println(res);
		    // System.out.println("------------------------------");

		    // System.out.println("------------------------------");
		    // System.out.println(alphaPlugin);
		    // System.out.println("------------------------------");
		    // context1 = context.createChildContext("Result of Alpha");
		    // alphaPlugin.invoke(0, context1, res);
		    // context1.getResult().synchronize();
		    // Object res1 = context1.getResult().getResult(0);
		    // System.out.println("------------------------------");
		    // // System.out.println(res1);
		    // System.out.println("------------------------------");

		    System.out.println("------------------------------");
		    System.out.println(importNetPlugin);
		    System.out.println("------------------------------");
		    context1 = context.createChildContext("Import Net");
		    //importNetPlugin.invoke(0, context1, "../prom5_log_files/sequence_prom6.pnml");
		    // importNetPlugin.invoke(0, context1, "../prom5_log_files/TracceRuposAlpha.pnml");
		    // importNetPlugin.invoke(0, context1, "../prom5_log_files/TracceRuposLTS5.pnml");
		     importNetPlugin.invoke(0, context1, "../prom5_log_files/invioFlussoAlpha.pnml");
		    // importNetPlugin.invoke(0, context1, "../prom5_log_files/invioFlussoLTS5BAG.pnml");
		    // importNetPlugin.invoke(0, context1, "../prom5_log_files/ProcRuposAlpha.pnml");
		    context1.getResult().synchronize();
		    System.out.println("------------------------------");
		    PluginExecutionResult res1 = context1.getResult();
		    System.out.println("Obtained " + res1.getSize() + " results");
		    System.out.println("------------------------------");
		    Object net = res1.getResult(0);
		    Marking startMarking = res1.getResult(1);
		    System.out.println("------------------------------");


		    System.out.println("------------------------------");
		    System.out.println(openLogPlugin);
		    System.out.println("------------------------------");
		    context1 = context.createChildContext("Result of Import Log Error");
		    // openLogPlugin.invoke(0, context1, "../prom5_log_files/choice.mxml");
		    //openLogPlugin.invoke(0, context1, "../prom5_log_files/errors.mxml");
		    //openLogPlugin.invoke(0, context1, "../prom5_log_files/par.mxml");
		    // openLogPlugin.invoke(0, context1, "../prom5_log_files/rec.mxml");
		    // openLogPlugin.invoke(0, context1, "../prom5_log_files/TracceRupos.mxml");
		    openLogPlugin.invoke(0, context1, "../prom5_log_files/InviaFlusso.mxml");
		    // openLogPlugin.invoke(0, context1, "../prom5_log_files/ProcRupos.mxml");
		    context1.getResult().synchronize();
		    org.deckfour.xes.model.XLog errors = (org.deckfour.xes.model.XLog)context1.getResult().getResult(0);
		    System.out.println("------------------------------");
		    // System.out.println(res);
		    System.out.println("------------------------------");


		    System.out.println("------------------------------");
		    System.out.println(fitnessPlugin);
		    System.out.println("------------------------------");
		    context1 = context.createChildContext("Fitness Checking");

		    ReplayFitnessSetting setting = new ReplayFitnessSetting();
		    setting.setAction(ReplayAction.INSERT_ENABLED_MATCH, true);
		    setting.setAction(ReplayAction.INSERT_ENABLED_INVISIBLE, true);
		    setting.setAction(ReplayAction.REMOVE_HEAD, true);
		    setting.setAction(ReplayAction.INSERT_ENABLED_MISMATCH, true);
		    setting.setAction(ReplayAction.INSERT_DISABLED_MATCH, true);
		    setting.setAction(ReplayAction.INSERT_DISABLED_MISMATCH, true);

		    setting.setWeight(ReplayAction.INSERT_ENABLED_MATCH, 1);
		    setting.setWeight(ReplayAction.INSERT_ENABLED_INVISIBLE, 10);
		    setting.setWeight(ReplayAction.REMOVE_HEAD, 100);
		    setting.setWeight(ReplayAction.INSERT_ENABLED_MISMATCH, 100);
		    setting.setWeight(ReplayAction.INSERT_DISABLED_MATCH, 100);
		    setting.setWeight(ReplayAction.INSERT_DISABLED_MISMATCH, 1000);

		    loadSettings(setting);


		    fitnessPlugin.invoke(0, context1, errors, net, setting);
		    context1.getResult().synchronize();
		    System.out.println("------------------------------");
		    PluginExecutionResult res2 = context1.getResult();
		    System.out.println("Obtained " + res2.getSize() + " results");
		    System.out.println("------------------------------");
		    TotalFitnessResult fitness = res2.getResult(0);
		    System.out.println(fitness);
		    System.out.println("------------------------------");
		    // Marking remaining = res2.getResult(1);
		    // System.out.println(remaining);
		    // System.out.println("------------------------------");
		    // Marking missing = res2.getResult(2);
		    // System.out.println(missing);
		    // System.out.println("------------------------------");

		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
		return null;
	}

	public static void main(String[] args) throws Exception {
	    Boot.VERBOSE = Level.NONE;
	    Boot.boot(MainCLI.class, CLIPluginContext.class, args);
	}

    public static void loadSettings(ReplayFitnessSetting setting) {
	  Properties props = new Properties();
	  try {
	      props.load(new FileInputStream("fitness.properties"));
	      Map<ReplayAction, String> names = new HashMap<ReplayAction, String>();
	      names.put(ReplayAction.INSERT_ENABLED_MATCH, "INSERT_ENABLED_MATCH");
	      names.put(ReplayAction.INSERT_ENABLED_INVISIBLE, "INSERT_ENABLED_INVISIBLE");
	      names.put(ReplayAction.REMOVE_HEAD, "REMOVE_HEAD");
	      names.put(ReplayAction.INSERT_ENABLED_MISMATCH, "INSERT_ENABLED_MISMATCH");
	      names.put(ReplayAction.INSERT_DISABLED_MATCH, "INSERT_DISABLED_MATCH");
	      names.put(ReplayAction.INSERT_DISABLED_MISMATCH, "INSERT_DISABLED_MISMATCH");

	      for (ReplayAction k : names.keySet()) {
		  String value = props.getProperty(names.get(k));
		  if (value == null) {
		      setting.setAction(k, false);
		      continue;
		  }
		  int iValue = Integer.valueOf(value);
		  if (iValue < 0) {
		      setting.setAction(k, false);
		      continue;
		  }
		  setting.setAction(k, true);
		  setting.setWeight(k, iValue);
	      }
	  }
	  catch(IOException e) {
	      e.printStackTrace();
	  }
    }
}
