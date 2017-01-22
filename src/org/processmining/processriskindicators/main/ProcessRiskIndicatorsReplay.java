package org.processmining.processriskindicators.main;

import java.awt.GridLayout;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.plugins.petrinet.replayer.PNLogReplayer;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;
import org.processmining.processriskindicators.analysis.Features;
import org.processmining.processriskindicators.inout.GetInputParameters;
import org.processmining.processriskindicators.inout.InOut;
import org.processmining.processriskindicators.inout.InputParameters;

import com.fluxicon.slickerbox.components.HeaderBar;

public class ProcessRiskIndicatorsReplay {


		@Plugin(
			name = "Process Risk Indicators - using log replay", 
			parameterLabels = { "Event log", "Petri net"}, 
			returnLabels = {"PRI Analysis Result"}, 
			returnTypes = {HeaderBar.class}, 
			userAccessible = true, 
			help = ""
			)
			
		@UITopiaVariant(
			affiliation = "QUT", 
			author = "A.Pika", 
			email = "a.pika@qut.edu.au"
		)
@PluginVariant(variantLabel = "Process Risk Indicators - using log replay", requiredParameterLabels = { 0, 1 })		
		
public HeaderBar main(UIPluginContext context, XLog inputlog, PetrinetGraph net) throws Exception {
			
			Features func = new Features();
			InputParameters ip = new InputParameters();
			GetInputParameters gui = new GetInputParameters();
			ip = gui.defineOneLogParams(ip);
			InOut inout = new InOut();

			context.log("Log replay");
	
			Vector <XLog> runs = new Vector<XLog>();
			runs = splitLog(context,inputlog,net);
			
			int i = 0;
			int num_runs = runs.size();
			String [][] results = new String [num_runs][8]; 
			
			
			if(ip.configure)
			{
				while(i < num_runs)
				{XLog clusterlog = runs.elementAt(i);
				
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
				
					while(i < num_runs)
					{XLog clusterlog = runs.elementAt(i);
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
				final HeaderBar pane = new HeaderBar("");//works strange header location	
				pane.setLayout(new GridLayout(0,1));
				pane.add(new JScrollPane(table));
				return pane;

	
		}

	
		
@SuppressWarnings("rawtypes")
// splitting log per runs -----------------------------------------------
		
		Vector<XLog> splitLog(UIPluginContext context, XLog inputlog, PetrinetGraph net){
			Vector<XLog> logruns = new Vector<XLog>();
			Vector <List<Object>>instances = new Vector<List<Object>>();
			Vector <List<StepTypes>>steps = new Vector<List<StepTypes>>();
			Vector <SortedSet<Integer>> runs = new Vector<SortedSet<Integer>>();
			
			try{
				
				// replaying a log and parsing replay result
				
			PNLogReplayer replayer = new PNLogReplayer();
			PNRepResult rep_res = replayer.replayLog(context, net, inputlog);
				
			Iterator runs_iter=rep_res.iterator();
			
	        while(runs_iter.hasNext())
	        {
	          
	        	SyncReplayResult run = (SyncReplayResult)runs_iter.next();
	        	
	   //populating cases
	        	SortedSet<Integer> caseIDs = run.getTraceIndex();  
	        	runs.add(caseIDs);
	        	
	   //populating instances
	        	List<Object> nodeInstance = run.getNodeInstance();
	        	instances.add(nodeInstance);
	        	
	   //populating steps
	        	List<StepTypes> stepTypes = run.getStepTypes();
	        	steps.add(stepTypes);
	        	        	
	        }
	       
		}catch(Exception e){;};	
	
	//splitting logs	
		for (int i=0;i<runs.size();i++){
		
		SortedSet<Integer> caseIDs = runs.elementAt(i);
		XLog log = XFactoryRegistry.instance().currentDefault().createLog(inputlog.getAttributes());
		
		for (XTrace t : inputlog) 
		{
			XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
			XAttributeMap am = t.getAttributes();
			Integer caseID = Integer.parseInt(am.get("concept:name").toString());
			
			if(caseIDs.contains(caseID))
			{log.add(trace);
			inputlog.remove(trace);
			
			for (XEvent e : t) {
				XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
				
				trace.add(event);}
			
			}
			
		}
		logruns.add(log);
		}
	//removing inserted activities from instances
		for (int i=0;i<instances.size();i++)
		{
			List<Object> nodeInstance = instances.elementAt(i);
			List<StepTypes> stepTypes = steps.elementAt(i);
			
			for(int j=0;j<stepTypes.size();j++)
			{
				if(stepTypes.get(j).equals(StepTypes.L))
				{
					stepTypes.remove(stepTypes.get(j));
					nodeInstance.remove(nodeInstance.get(j));
					j--;
				}
			}
		}
	//merging similar logs
		for (int i=0;i<instances.size();i++)
		{
			List<Object> current = instances.elementAt(i);
			
			for (int j=i+1;j<instances.size();j++)
			{
				List<Object> comparable = instances.elementAt(j);
				if(similar_runs(current,comparable))
				{
					logruns.elementAt(i).addAll(logruns.elementAt(j));
					logruns.removeElementAt(j);
					instances.removeElementAt(j);
					j--;
				};
			}
		}
		
			return logruns;
		}

Boolean similar_runs(List<Object> first, List<Object> second)
{
	boolean ret = true;
	if (first.size()!=second.size())
	{ret = false;}
	else
	{
		for (int i=0;i<first.size();i++)
		{
			if(!(first.get(i).equals(second.get(i)))){ret = false;};
		}
	}
	
	return ret;
}

}

