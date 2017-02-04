package org.processmining.processriskindicators.main;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.processriskindicators.analysis.Features;
import org.processmining.processriskindicators.analysis.PriUtils;
import org.processmining.processriskindicators.inout.GetInputParameters;
import org.processmining.processriskindicators.inout.InOut;
import org.processmining.processriskindicators.inout.InputParameters;

import com.fluxicon.slickerbox.components.HeaderBar;

public class ProcessRiskIndicatorsAllLogWithModel {

	@Plugin(
			name = "Process Risk Indicators - with process model", 
			parameterLabels = { "Event log", "Process model" }, 
			returnLabels = { "PRI Analysis Result"}, 
			returnTypes = { HeaderBar.class }, 
			userAccessible = true, 
			help = ""
		)
	
	@UITopiaVariant(
			affiliation = "QUT", 
			author = "A.Pika", 
			email = "a.pika@qut.edu.au"
		)
		
public HeaderBar main(UIPluginContext context, XLog inputlog, PetrinetGraph net) throws Exception{
			
			Features func = new Features();
			InputParameters ip = new InputParameters();
			GetInputParameters gui = new GetInputParameters();
			ip = gui.defineOneLogParams(ip);
			PriUtils priUtils = new PriUtils();	
			InOut inout = new InOut();

XLog testLog = inputlog;

if(ip.configure)
{
	context.log("Preparing log");
	XLog log = func.preprocessLogforConfiguration(inputlog);		

	context.log("Identifying risks");	
	testLog = func.getOneDataSetRisksWithModel(context,log,ip,net);
			
}else
{
	Double dist = 1.483;
	Double stDev = 2.00;
	Double allstDev = 2.00;
	Date startTimeslot = new Date(1);
	Date endTimeslot = new Date(100000000);
	
	context.log("Identifying risks");	
	testLog = func.getRunRisksWithModel(context,inputlog, startTimeslot, endTimeslot, dist, allstDev, stDev, ip, net);	

}
			String[] prediction_header = {"TP", "FP", "TN", "FN", "Number of Traces"};
			
			JTable table1 = new JTable();
			JTable table2 = new JTable();
			JLabel label = new JLabel();
			
			
			if(ip.configure)
			{
				String[] PRIheader = {"Case ID", "Case duration", "Predicted outcome", "PRI1", "PRI2", "PRI3", "PRI4", "PRI5", "PRI6", "PRI7", "PRI8"};
				table1 = new JTable(priUtils.caseRisks(testLog), PRIheader);
				table2 = new JTable(priUtils.getResult(testLog), prediction_header);
				label = priUtils.getResultasLabel(testLog); 
				label.setForeground(Color.WHITE);
			}
			else
			{
				//no configuration prediction
				String[] PRIheader2 = {"Case ID", "Case duration", "Predicted outcome", "PRI1", "PRI2", "PRI3", "PRI4", "PRI5", "PRI6"};
				table1 = new JTable(inout.casePRIs(testLog), PRIheader2); 
				table2 = new JTable(inout.getResultNew(testLog), prediction_header);
				label = priUtils.getResultasLabel(testLog); 
				label.setForeground(Color.WHITE);
		
			}	
			
			final HeaderBar pane = new HeaderBar("");
			pane.setLayout(new GridLayout(0,1));
			pane.add(label);
			pane.add(new JScrollPane(table1));
		 
		      
			return pane;
	
		}
		


}

