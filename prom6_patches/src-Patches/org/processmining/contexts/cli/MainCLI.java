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

import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.PluginDescriptor;
import org.processmining.framework.boot.Boot;
import org.processmining.framework.plugin.annotations.Bootable;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.util.CommandLineArgumentList;

public class MainCLI {
	@Plugin(name = "MainCLI", parameterLabels = {}, returnLabels = {}, returnTypes = {}, userAccessible = false)
	@Bootable
	public Object main(CommandLineArgumentList commandlineArguments) {
		try {
			CLIContext globalContext = new CLIContext();
			PluginContext context = globalContext.getMainPluginContext();
			PluginDescriptor openLogPlugin = null;
			PluginDescriptor alphaPlugin = null;
			System.out.println("------------------------------");
			for (PluginDescriptor plugin : context.getPluginManager().getAllPlugins()) {
			    System.out.println(plugin.getName());
			    if ("Open XES Log File".equals(plugin.getName()))
				openLogPlugin = plugin;
			    if ("Alpha Miner".equals(plugin.getName()))
				alphaPlugin = plugin;
			    for (int j = 0; j < plugin.getParameterTypes().size(); j++) {
				System.out.println("  " + j + " ) " + plugin.getMethodLabel(j) + " " + plugin.getParameterTypes(j));
			    }
			}
			System.out.println("------------------------------");
			System.out.println(openLogPlugin);
			System.out.println("------------------------------");
			PluginContext context1 = context.createChildContext("Result of Import Log");
			openLogPlugin.invoke(0, context1, "/media/data/Sources/phd/rupos/prom5_log_files/sequence.mxml");
			context1.getResult().synchronize();
			org.deckfour.xes.model.XLog res = (org.deckfour.xes.model.XLog)context1.getResult().getResult(0);
			System.out.println("------------------------------");
			System.out.println(res);
			System.out.println("------------------------------");

			System.out.println("------------------------------");
			System.out.println(alphaPlugin);
			System.out.println("------------------------------");
			context1 = context.createChildContext("Result of Alpha");
			alphaPlugin.invoke(0, context1, res);
			context1.getResult().synchronize();
			Object res1 = context1.getResult().getResult(0);
			System.out.println("------------------------------");
			System.out.println(res1);
			System.out.println("------------------------------");

		} catch (Throwable t) {
			t.printStackTrace();
			System.err.println(t);
			System.exit(1);
		}
		System.exit(0);
		return null;
	}

	public static void main(String[] args) throws Exception {
	    Boot.boot(MainCLI.class, CLIPluginContext.class, args);
	}
}
