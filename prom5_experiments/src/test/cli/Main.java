package test.cli;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.processmining.framework.log.LogFile;
import org.processmining.framework.log.LogReader;
import org.processmining.framework.log.LogReaderFactory;
import org.processmining.framework.log.LogSummary;
import org.processmining.framework.ui.Progress;
import org.processmining.mining.MiningResult;
import org.processmining.mining.logabstraction.LogRelations;
import org.processmining.mining.petrinetmining.AlphaProcessMiner;


public class Main {

	protected class Alg extends AlphaProcessMiner {
		Alg() {
			super();
		}
	}
	
	protected class GProgress extends Progress {
		GProgress() {
			
		}
	}
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		LogFile res = LogFile.getInstance("/home/guancio/Downloads/repairExample.mxml");
		LogReader log = LogReaderFactory.createInstance(null, res);
		AlphaProcessMiner alg = new AlphaProcessMiner();
		
		Progress progress = new Main().new GProgress();
		
		LogRelations relation = alg.getLogRelations(log, progress);
		MiningResult res2 = alg.mine(log, relation, progress);
		System.out.println(res);
	}

}
