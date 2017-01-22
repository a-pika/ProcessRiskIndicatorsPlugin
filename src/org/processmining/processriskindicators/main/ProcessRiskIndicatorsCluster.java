package org.processmining.processriskindicators.main;

import java.awt.GridLayout;
import java.util.Date;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.guidetreeminer.ClusterLogOutput;
import org.processmining.plugins.guidetreeminer.GuideTreeMinerInput;
import org.processmining.plugins.guidetreeminer.MineGuideTree;
import org.processmining.plugins.guidetreeminer.ui.GuideTreeMinerUI;
import org.processmining.processriskindicators.analysis.Features;
import org.processmining.processriskindicators.inout.GetInputParameters;
import org.processmining.processriskindicators.inout.InOut;
import org.processmining.processriskindicators.inout.InputParameters;

import com.fluxicon.slickerbox.components.HeaderBar;


@Plugin(
		name = "Process Risk Indicators - using trace clustering", 
		parameterLabels = { "Event Log" }, 
		returnLabels = {"PRI Analysis Result","ClusterLogOutput"},
		returnTypes = { HeaderBar.class, ClusterLogOutput.class }, 
		userAccessible = true
		)
public class ProcessRiskIndicatorsCluster {

		@UITopiaVariant(
			affiliation = "QUT", 
			author = "A.Pika", 
			email = "a.pika@qut.edu.au"
		)
@PluginVariant(variantLabel = "Select options to use", requiredParameterLabels = { 0 })		
public Object[]  main(UIPluginContext context, final XLog inlog) throws Exception {
			
			Features func = new Features();
			InputParameters ip = new InputParameters();
			GetInputParameters gui = new GetInputParameters();
			ip = gui.defineOneLogParams(ip);
			InOut inout = new InOut();

			context.log("Log clustering");
			
			//-------------------------------------------------------------
			GuideTreeMinerUI guideTreeMinerUI = new GuideTreeMinerUI(context);
			GuideTreeMinerInput input = guideTreeMinerUI.mineTree(inlog);
			MineGuideTree mineGuideTree = new MineGuideTree();
			Object[] returnObjects = mineGuideTree.mine(context, input, inlog);
			
			
				ClusterLogOutput clo = (ClusterLogOutput) returnObjects[1];
				
				int i = 0;
				int numOfClusters = clo.getNoClusters();
				
				String [][] results = new String [numOfClusters][8]; 
		
			//-------------------------------------------------------------
		if(ip.configure)
		{
			while(i < numOfClusters)
			{XLog clusterlog = clo.getClusterLog(i);
			
			context.log("Preparing log");
			XLog log = func.preprocessLogforConfiguration(clusterlog);		

			context.log("Identifying risks");	
			XLog testLog = func.getOneDataSetRisks(context,log,ip);

			String [][] line = new String [1][8];
			line = inout.getResultMult(testLog,i+1);
			
			for (int j=0;j<8;j++)
			{
				results[i][j] = line[0][j];
			}
			
			i++;
			
			}

		}
		else
		{
			Double dist = 1.483;
			Double stDev = 2.00;
			Double allstDev = 2.00;
			Date startTimeslot = new Date(1);
			Date endTimeslot = new Date(100000000);
			
				while(i < numOfClusters)
				{XLog clusterlog = clo.getClusterLog(i);
				context.log("Identifying risks");	
				XLog log = func.getRunRisks(context, clusterlog, startTimeslot, endTimeslot, dist, allstDev, stDev,ip);
				String [][] line = new String [1][8];
				line = inout.getResultMult(log,i+1);
				
				for (int j=0;j<8;j++)
				{
					results[i][j] = line[0][j];
				}
				
				i++;
				
				}

		}	
										
			//-------------------------------------------------------------------
			String[] prediction_header = {"Run", "Number of traces", "TP", "FP", "TN", "FN", "Precision", "Recall"};
			
			JTable table = new JTable(results, prediction_header);
			
			final HeaderBar pane = new HeaderBar("");
			pane.setLayout(new GridLayout(0,1));
			pane.add(new JScrollPane(table));
			return new Object[] {pane, returnObjects[1] };
					
		}
	
}

