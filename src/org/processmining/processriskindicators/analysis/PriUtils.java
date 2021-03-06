package org.processmining.processriskindicators.analysis;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListSet;

import javax.swing.JLabel;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeBooleanImpl;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;



public class PriUtils {

public XLog getTrainLog75 (XLog log){
			
			XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
			int i = 1;	
						
						for (XTrace t : log) 
						{
							if ((i % 4) != 0)
							{	
							XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
							copylog.add(trace);
																					 
						for (XEvent e : t) 
							{
							XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
							trace.add(event);
						}
											
						}i++;
						}
						
			return copylog;
			
};

public XLog getTestLog25 (XLog log){
			
			XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
			int i = 1;	
						
						for (XTrace t : log) 
						{
							if ((i % 4) == 0)
							{	
							XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
							copylog.add(trace);
																					 
						for (XEvent e : t) 
							{
							XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
							trace.add(event);
						}
											
						}i++;
						}
						
			return copylog;
			
};

public XLog addTimeOutcome (XLog log, Double SLAdur){
						
				
				for (XTrace t : log) {
				XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
						
				XAttributeDiscrete trace_duration = (XAttributeDiscrete)trace.getAttributes().get("time:duration");
				long t_duration = trace_duration.getValue();
				if(t_duration > SLAdur)
					{
						trace.getAttributes().put("outcome:case_duration",new XAttributeBooleanImpl("outcome:case_duration",true));
						trace.getAttributes().put("outcome:SLA_violation",new XAttributeContinuousImpl("outcome:SLA_violation",t_duration-SLAdur));
					}else
					{
						trace.getAttributes().put("outcome:case_duration",new XAttributeBooleanImpl("outcome:case_duration",false));
						trace.getAttributes().put("outcome:SLA_violation",new XAttributeContinuousImpl("outcome:SLA_violation",0.00));
					};
						
					trace.getAttributes().put("outcome:expected_outcome",new XAttributeBooleanImpl("outcome:expected_outcome",false));
														
								}
			return log;
			
		};

public XLog processTime(XLog log) {
			
			XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
			
			for (XTrace t : log) {
				
				XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
				boolean expectedOutcome = false;
				
				XAttributeMap am = t.getAttributes();
				String pri5 = am.get("PRI5").toString();
				//String pri7 = am.get("PRI7").toString();	
								
				for (XEvent e : t) {
					
					XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
										
					
						String pri1 = e.getAttributes().get("PRI1").toString();
						String pri2 = e.getAttributes().get("PRI2").toString();
						String pri3 = e.getAttributes().get("PRI3").toString();
						String pri4 = e.getAttributes().get("PRI4").toString();
						String pri6 = e.getAttributes().get("PRI6").toString();
						String pri8 = e.getAttributes().get("PRI8").toString();
						String pri9 = e.getAttributes().get("PRI9").toString();
										
						if (pri1.equals("true") || pri2.equals("true") || pri3.equals("true") || pri4.equals("true") || pri6.equals("true") || pri8.equals("true") || pri9.equals("true"))
						{
							expectedOutcome = true;
						};
							
						trace.add(event);
						
						
				}
				
				if (pri5.equals("true")) //pri 7 check removed here - many tasks in a case, not in the thesis 
				{
					expectedOutcome = true;
				}
				
				
				trace.getAttributes().put("outcome:expected_outcome",new XAttributeBooleanImpl("outcome:expected_outcome",expectedOutcome));
				copylog.add(trace);
			} 
			
			return copylog;

		}

public Double LongAverage(Vector<Long> numbers)
			{
				double average = 0.0;
				int size = numbers.size();
				double sum = 0.00;
				
				for (int i=0; i<size; i++)
				{
					sum+=numbers.elementAt(i);
				}

				average = sum/size;
				
		return average;
			}
		
public Double DoubleAverage(Vector<Double> numbers)
		{
			double average = 0.0;
			int size = numbers.size();
			double sum = 0.00;
			
			for (int i=0; i<size; i++)
			{
				sum+=numbers.elementAt(i);
			}

			average = sum/size;
			
	return average;
		}
	
public Double getStDev (Vector<Long> numbers){
						Double StDev = 0.00;
						if (numbers.size()!=0)	{
					Vector<Double> StDevVals = new Vector<Double>();
						
									Double average = LongAverage(numbers);
									for(int i=0;i<numbers.size();i++)
									{StDevVals.add(Math.pow(numbers.elementAt(i)-average,2));}
									StDev = Math.sqrt(DoubleAverage(StDevVals));
						}
						
					return StDev;
						
					};
		
//----------------------------------PREDICTION RESULTS--------------------------------------------

public JLabel getResultasLabel(XLog log) {
						
	String text = "";
	String[][] jtable = new String[2][5];
							
					Integer num_traces = log.size();
					Integer correctlypredicted = 0;
					Integer falselypredicted = 0;
					Integer notpredicted = 0;
					Integer correctnotpredicted = 0;
													
						for (XTrace t : log) {
							
						
							XAttributeMap am = t.getAttributes();
							
							String caseSLAdur = am.get("outcome:case_duration").toString();
							String expectedOutcome = am.get("outcome:expected_outcome").toString();
							
								
							if (caseSLAdur.equals("true") && expectedOutcome.equals("true"))
							{correctlypredicted+=1;};
							if (caseSLAdur.equals("false") && expectedOutcome.equals("true"))
							{falselypredicted+=1;};
							if (caseSLAdur.equals("true") && expectedOutcome.equals("false"))
							{notpredicted+=1;};
							if (caseSLAdur.equals("false") && expectedOutcome.equals("false"))
							{correctnotpredicted+=1;};
									
						} 	
						
						double precision = (double)correctlypredicted/(correctlypredicted+falselypredicted);
						double recall = (double)correctlypredicted/(correctlypredicted+notpredicted);
									
						jtable[0][0] = correctlypredicted.toString();
						jtable[0][1] = falselypredicted.toString();
						jtable[0][2] = notpredicted.toString();
						jtable[0][3] = correctnotpredicted.toString();
						jtable[0][4] = num_traces.toString();
						
						jtable[1][0] = "Precision: ";
						jtable[1][1] = String.format("%.2f", precision);
						jtable[1][2] = "Recall: ";
						jtable[1][3] = String.format("%.2f", recall);
						jtable[1][4] = "-";
						
						text = "<html><h1>Precision: "+String.format("%.2f", precision)+" <br/>Recall: "+String.format("%.2f", recall)+" <br/>True Positives: "+correctlypredicted.toString()+" <br/>False Positives: "+falselypredicted.toString()+" <br/>True Negatives: "+notpredicted.toString()+" <br/>False Negatives: "+correctnotpredicted.toString()+" <br/> Number of traces in the test set: "+num_traces.toString()+"</h1></html>";
									
						return new JLabel(text);

					}
					
public String[][] getResult(XLog log) {
						
	String[][] jtable = new String[2][5];
							
					Integer num_traces = log.size();
					Integer correctlypredicted = 0;
					Integer falselypredicted = 0;
					Integer notpredicted = 0;
					Integer correctnotpredicted = 0;
													
						for (XTrace t : log) {
							
						
							XAttributeMap am = t.getAttributes();
							
							String caseSLAdur = am.get("outcome:case_duration").toString();
							String expectedOutcome = am.get("outcome:expected_outcome").toString();
							
								
							if (caseSLAdur.equals("true") && expectedOutcome.equals("true"))
							{correctlypredicted+=1;};
							if (caseSLAdur.equals("false") && expectedOutcome.equals("true"))
							{falselypredicted+=1;};
							if (caseSLAdur.equals("true") && expectedOutcome.equals("false"))
							{notpredicted+=1;};
							if (caseSLAdur.equals("false") && expectedOutcome.equals("false"))
							{correctnotpredicted+=1;};
									
						} 	
						
						double precision = (double)correctlypredicted/(correctlypredicted+falselypredicted);
						double recall = (double)correctlypredicted/(correctlypredicted+notpredicted);
									
						jtable[0][0] = correctlypredicted.toString();
						jtable[0][1] = falselypredicted.toString();
						jtable[0][2] = notpredicted.toString();
						jtable[0][3] = correctnotpredicted.toString();
						jtable[0][4] = num_traces.toString();
						
						jtable[1][0] = "Precision: ";
						jtable[1][1] = String.format("%.2f", precision);
						jtable[1][2] = "Recall: ";
						jtable[1][3] = String.format("%.2f", recall);
						jtable[1][4] = "-";
						
									
						return jtable;

					}

public String[][] caseRisks(XLog log) {
		
	Integer num_traces = log.size();
	String[][] jtable = new String[num_traces][11];
		int i = 0;
		
				
		for (XTrace t : log) {
			
		
			XAttributeMap am = t.getAttributes();
			String caseID = am.get("concept:name").toString();
			String caseDuration = am.get("time:duration").toString();
			//String delayed = am.get("outcome:case_duration").toString();
			String expectedOutcome = am.get("outcome:expected_outcome").toString();
			
			Long lduration = Long.valueOf(caseDuration);
			Double duration = (double)lduration/1000/60/60;//in hours
			caseDuration = String.format("%.2f", duration);
			//caseDuration = duration.toString();
			
			String pri5 = am.get("PRI5").toString();
			//String pri7 = am.get("PRI7").toString();
			
			Integer pri1 = 0;
			Integer pri2 = 0;
			Integer pri3 = 0;
			Integer pri4 = 0;
			Integer pri6 = 0;
			Integer pri8 = 0;
			Integer pri9 = 0;
						 
			for (XEvent e : t) {
				
				String pris1 = e.getAttributes().get("PRI1").toString();
				String pris2 = e.getAttributes().get("PRI2").toString();
				String pris3 = e.getAttributes().get("PRI3").toString();
				String pris4 = e.getAttributes().get("PRI4").toString();
				String pris6 = e.getAttributes().get("PRI6").toString();
				String pris8 = e.getAttributes().get("PRI8").toString();
				String pris9 = e.getAttributes().get("PRI9").toString();
				
					
				
				if (pris1.equals("true")) {pri1++;}
				if (pris2.equals("true")) {pri2++;}
				if (pris3.equals("true")) {pri3++;}
				if (pris4.equals("true")) {pri4++;}
				if (pris6.equals("true")) {pri6++;}
				if (pris8.equals("true")) {pri8++;}
				if (pris9.equals("true")) {pri9++;}
				
						
					
			}
			jtable[i][0] = caseID;
			jtable[i][1] = caseDuration;
			jtable[i][2] = expectedOutcome;
			jtable[i][3] = pri1.toString();
			jtable[i][4] = pri2.toString();
			jtable[i][5] = pri3.toString();
			jtable[i][6] = pri4.toString();
			jtable[i][7] = pri5;
			jtable[i][8] = pri6.toString();
			jtable[i][9] = pri9.toString();
			jtable[i][10] = pri8.toString();
			//jtable[i][11] = pri9.toString();
			i++;
			
		} 		
	          
			
		return jtable;

	}


//---------------------------------------------Prev Complete for PRI 2---------------------------------------------
// returns previous event
//used in version with process model
@SuppressWarnings({ "rawtypes", "unchecked" })
public XEvent PrevPRI2(/*UIPluginContext context,*/ XTrace t, XEvent e, PetrinetGraph net){
			
			ConcurrentSkipListSet<String> prevActivities = new ConcurrentSkipListSet<String>();
			String activity = XConceptExtension.instance().extractName(e);		
						
			Iterator iterator = net.getTransitions().iterator();			
			while (iterator.hasNext()) 
			{
				Transition trans = (Transition)iterator.next();
				String label = trans.getLabel();
				label = label.replace("+complete", "");
				//context.log("label:"+label);
				if(label.equalsIgnoreCase(activity))
				{//context.log("label true:"+label);
				Collection<Transition> pred = trans.getVisiblePredecessors();
				Vector prev = new Vector();
				prev.addAll(pred);
								
				for(int i=0;i<prev.size();i++)
				{
					String actLabel = ((Transition)prev.elementAt(i)).getLabel();
					actLabel = actLabel.replace("+complete", "");
					//context.log("prevlabel:"+actLabel);
					prevActivities.add(actLabel);
				}
				
				}
					
			}	
			
			
				Date eTime = XTimeExtension.instance().extractTimestamp(e);
				XEvent return_event = e;
				XEvent prevModel = e;
				XEvent prevComplete = e;
				
						for (XEvent e1 : t) 
						{
						//---------
								String lifecycle = XLifecycleExtension.instance().extractTransition(e1);
								String name = XConceptExtension.instance().extractName(e1);		
								Date eventTime = XTimeExtension.instance().extractTimestamp(e1);
						//---------
						
						if (lifecycle.equalsIgnoreCase("complete") && eventTime.compareTo(eTime)<0 && prevActivities.contains(name))
						prevModel = e1;	
						if (lifecycle.equalsIgnoreCase("complete") && eventTime.compareTo(eTime)<0)
						prevComplete = e1;			
												
					}
						if (prevModel.equals(e)){return_event = prevComplete;}else{return_event = prevModel;}
						
					return return_event;
		}	


}

//------------------------------------------PREV. VERSIONS-------------------------------------------------------
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*		
public String[][] totalOut(XLog log) {
	String[][] result = new String[5][5];
	return result;}*/

//---------------------------------------separate PRIs per case----------------------------------------------------
/*
public String[][] casePRIs(XLog log) {
	
	Integer num_traces = log.size();
	String[][] jtable = new String[num_traces][10];
		int i = 0;
						
		for (XTrace t : log) {
			
		
			XAttributeMap am = t.getAttributes();
			String caseID = am.get("concept:name").toString();
			
			String caseDuration = am.get("time:duration").toString();
			Double casedur = Double.parseDouble(caseDuration);
			caseDuration = ((Double)(casedur/1000/60/60/24)).toString();
			
			String expectedOutcome = am.get("outcome:expected_outcome").toString();
			
			String pri5 = am.get("PRI5").toString();
			String pri7 = am.get("PRI7").toString();
			
			Integer act_dur = 0;
			Integer wait_dur = 0;
			Integer act_rep = 0;
			Integer atyp_act = 0;
			Integer mult_res = 0;
			Integer subproc = 0;
			Integer mult_act = 0;
			
			if (pri5.equals("true")) {mult_res = 1;}
			if (pri7.equals("true")) {mult_act = 1;}
			
						 
			for (XEvent e : t) {
				
				String actdur = e.getAttributes().get("PRI1").toString();
				String waitdur = e.getAttributes().get("PRI2").toString();
				String multact = e.getAttributes().get("PRI3").toString();
				String oddact = e.getAttributes().get("PRI4").toString();
				String f_subprocDuration = e.getAttributes().get("PRI6").toString();
				
				if (actdur.equals("true")) {act_dur += 1;}
				if (waitdur.equals("true")) { wait_dur += 1;}
				if (multact.equals("true")) {act_rep += 1;}
				if (oddact.equals("true")) {atyp_act += 1;}
				if (f_subprocDuration.equals("true")) {subproc += 1;}
						
					
			}
			jtable[i][0] = caseID;
			jtable[i][1] = caseDuration;
			jtable[i][2] = act_dur.toString();
			jtable[i][3] = wait_dur.toString();
			jtable[i][4] = act_rep.toString();
			jtable[i][5] = atyp_act.toString();
			jtable[i][6] = mult_res.toString();
			jtable[i][7] = subproc.toString();
			jtable[i][8] = mult_act.toString();
			jtable[i][9] = expectedOutcome;
			
			i++;
			
		} 		
	          
			
		return jtable;

	}

*///---------------------------------------Display case thresholds----------------------------------------------------

//---------------------------------------separate PRIs per case----------------------------------------------------
/*
public String[][] casePRIs(XLog log) {
	
	Integer num_traces = log.size();
	String[][] jtable = new String[num_traces][10];
		int i = 0;
						
		for (XTrace t : log) {
			
		
			XAttributeMap am = t.getAttributes();
			String caseID = am.get("concept:name").toString();
			
			String caseDuration = am.get("time:duration").toString();
			Double casedur = Double.parseDouble(caseDuration);
			caseDuration = ((Double)(casedur/1000/60/60/24)).toString();
			
			String expectedOutcome = am.get("outcome:expected_outcome").toString();
			
			String pri5 = am.get("PRI5").toString();
			String pri7 = am.get("PRI7").toString();
			
			Integer act_dur = 0;
			Integer wait_dur = 0;
			Integer act_rep = 0;
			Integer atyp_act = 0;
			Integer mult_res = 0;
			Integer subproc = 0;
			Integer mult_act = 0;
			
			if (pri5.equals("true")) {mult_res = 1;}
			if (pri7.equals("true")) {mult_act = 1;}
			
						 
			for (XEvent e : t) {
				
				String actdur = e.getAttributes().get("PRI1").toString();
				String waitdur = e.getAttributes().get("PRI2").toString();
				String multact = e.getAttributes().get("PRI3").toString();
				String oddact = e.getAttributes().get("PRI4").toString();
				String f_subprocDuration = e.getAttributes().get("PRI6").toString();
				
				if (actdur.equals("true")) {act_dur += 1;}
				if (waitdur.equals("true")) { wait_dur += 1;}
				if (multact.equals("true")) {act_rep += 1;}
				if (oddact.equals("true")) {atyp_act += 1;}
				if (f_subprocDuration.equals("true")) {subproc += 1;}
						
					
			}
			jtable[i][0] = caseID;
			jtable[i][1] = caseDuration;
			jtable[i][2] = act_dur.toString();
			jtable[i][3] = wait_dur.toString();
			jtable[i][4] = act_rep.toString();
			jtable[i][5] = atyp_act.toString();
			jtable[i][6] = mult_res.toString();
			jtable[i][7] = subproc.toString();
			jtable[i][8] = mult_act.toString();
			jtable[i][9] = expectedOutcome;
			
			i++;
			
		} 		
	          
			
		return jtable;

	}

*///---------------------------------------Display case thresholds----------------------------------------------------

/*
public String[][] activityThresholdsOut(PriCaseThreshold thresholds) {
	
	int size = thresholds.activities.size();
	String[][] jtable = new String[size][15];
	
	for (int i=0; i<size; i++)
		
	{
		
		PriActivityThreshold act = thresholds.activities.elementAt(i);
		jtable[i][0] = act.name;
		jtable[i][1] = Integer.toString(act.numExecutions);
		
		jtable[i][2] = Double.toString(act.PRI1threshold/1000/60/60/24);
		jtable[i][3] = Double.toString(act.PRI1av/1000/60/60/24);
		jtable[i][4] = Double.toString(act.PRI1dev/1000/60/60/24);
		
		jtable[i][5] = Double.toString(act.PRI2threshold/1000/60/60/24);
		jtable[i][6] = Double.toString(act.PRI2av/1000/60/60/24);
		jtable[i][7] = Double.toString(act.PRI2dev/1000/60/60/24);
		
		jtable[i][8] = act.PRI3threshold.toString();
		jtable[i][9] = act.PRI3av.toString();
		jtable[i][10] = act.PRI3dev.toString();
		
		jtable[i][11] = Double.toString(act.PRI6threshold/1000/60/60/24);
		jtable[i][12] = Double.toString(act.PRI6av/1000/60/60/24);
		jtable[i][13] = Double.toString(act.PRI6dev/1000/60/60/24);
		
		jtable[i][14] = act.PRI4.toString();
		
		
		
	}
		
			
		return jtable;

	}

//---------------------------------------Display PRI1 learning----------------------------------------------------

public String[][] activityThresholdsPRI1(PriCaseThreshold thresholds) {
	
	int size = thresholds.activities.size();
	String[][] jtable = new String[size][24];
	
	for (int i=0; i<size; i++)
		
	{
		
		PriActivityThreshold act = thresholds.activities.elementAt(i);
		jtable[i][0] = act.name;
		jtable[i][1] = Integer.toString(act.numExecutions);
		
		for(int j=0;j<11;j++){
			
			jtable[i][j*2+2] = Integer.toString(act.pri1t[j]);
			jtable[i][j*2+3] = Integer.toString(act.pri1f[j]);
			
		}		
		
		
	}
		
			
		return jtable;

	}

//---------------------------------------Display PRI2 learning----------------------------------------------------

public String[][] activityThresholdsPRI2(PriCaseThreshold thresholds) {
	
	int size = thresholds.activities.size();
	String[][] jtable = new String[size][24];
	
	for (int i=0; i<size; i++)
		
	{
		
		PriActivityThreshold act = thresholds.activities.elementAt(i);
		jtable[i][0] = act.name;
		jtable[i][1] = Integer.toString(act.numExecutions);
		
		for(int j=0;j<11;j++){
			
			jtable[i][j*2+2] = Integer.toString(act.pri2t[j]);
			jtable[i][j*2+3] = Integer.toString(act.pri2f[j]);
			
		}		
		
		
	}
		
			
		return jtable;

	}

//---------------------------------------Display PRI3 learning----------------------------------------------------

public String[][] activityThresholdsPRI3(PriCaseThreshold thresholds) {
	
	int size = thresholds.activities.size();
	String[][] jtable = new String[size][24];
	
	for (int i=0; i<size; i++)
		
	{
		
		PriActivityThreshold act = thresholds.activities.elementAt(i);
		jtable[i][0] = act.name;
		jtable[i][1] = Integer.toString(act.numExecutions);
		
		for(int j=0;j<11;j++){
			
			jtable[i][j*2+2] = Integer.toString(act.pri3t[j]);
			jtable[i][j*2+3] = Integer.toString(act.pri3f[j]);
			
		}		
		
		
	}
		
			
		return jtable;

	}

//---------------------------------------Display PRI6 learning----------------------------------------------------

public String[][] activityThresholdsPRI6(PriCaseThreshold thresholds) {
	
	int size = thresholds.activities.size();
	String[][] jtable = new String[size][24];
	
	for (int i=0; i<size; i++)
		
	{
		
		PriActivityThreshold act = thresholds.activities.elementAt(i);
		jtable[i][0] = act.name;
		jtable[i][1] = Integer.toString(act.numExecutions);
		
		for(int j=0;j<11;j++){
			
			jtable[i][j*2+2] = Integer.toString(act.pri6t[j]);
			jtable[i][j*2+3] = Integer.toString(act.pri6f[j]);
			
		}		
		
		
	}
		
			
		return jtable;

	}
//---------------------------------------LOG OUTPUT---------------------------------------------------------

*/

//---------------------------------------LOG OUTPUT---------------------------------------------------------
/*
public String[][] cumResults(XLog log, Double SLA) {
		
	Integer num_traces = log.size();
	Integer delayed = 0;
	Integer intime = 0;
	
	Integer TPpri1 = 0;
	Integer FPpri1 = 0;
	Integer TPSLApri1 = 0;
	Integer FPSLApri1 = 0;
	
	Integer TPpri2 = 0;
	Integer FPpri2 = 0;
	Integer TPSLApri2 = 0;
	Integer FPSLApri2 = 0;
	
	Integer TPpri3 = 0;
	Integer FPpri3 = 0;
	Integer TPSLApri3 = 0;
	Integer FPSLApri3 = 0;
	
	Integer TPpri4 = 0;
	Integer FPpri4 = 0;
	Integer TPSLApri4 = 0;
	Integer FPSLApri4 = 0;
	
	Integer TPpri5 = 0;
	Integer FPpri5 = 0;
	Integer TPSLApri5 = 0;
	Integer FPSLApri5 = 0;
	
	Integer TPpri6 = 0;
	Integer FPpri6 = 0;
	Integer TPSLApri6 = 0;
	Integer FPSLApri6 = 0;
	
	Integer TPpri7 = 0;
	Integer FPpri7 = 0;
	Integer TPSLApri7 = 0;
	Integer FPSLApri7 = 0;
	
	Integer TPpri8 = 0;
	Integer FPpri8 = 0;
	Integer TPSLApri8 = 0;
	Integer FPSLApri8 = 0;
	
	Integer TPpri9 = 0;
	Integer FPpri9 = 0;
	Integer TPSLApri9 = 0;
	Integer FPSLApri9 = 0;
	
	Integer TPtotal = 0;
	Integer FPtotal = 0;
	Integer TPSLAtotal = 0;
	Integer FPSLAtotal = 0;
	
	Double precisionPRI1 = 0.00;
	Double recallPRI1 = 0.00;
	Integer NPSLApri1 = 0;
	Double NPSLApartpri1 = 0.00;
	
	Double precisionPRI2 = 0.00;
	Double recallPRI2 = 0.00;
	Integer NPSLApri2 = 0;
	Double NPSLApartpri2 = 0.00;
	
	Double precisionPRI3 = 0.00;
	Double recallPRI3 = 0.00;
	Integer NPSLApri3 = 0;
	Double NPSLApartpri3 = 0.00;
	
	Double precisionPRI4 = 0.00;
	Double recallPRI4 = 0.00;
	Integer NPSLApri4 = 0;
	Double NPSLApartpri4 = 0.00;
	
	Double precisionPRI5 = 0.00;
	Double recallPRI5 = 0.00;
	Integer NPSLApri5 = 0;
	Double NPSLApartpri5 = 0.00;
	
	Double precisionPRI6 = 0.00;
	Double recallPRI6 = 0.00;
	Integer NPSLApri6 = 0;
	Double NPSLApartpri6 = 0.00;
	
	Double precisionPRI7 = 0.00;
	Double recallPRI7 = 0.00;
	Integer NPSLApri7 = 0;
	Double NPSLApartpri7 = 0.00;
	
	Double precisionPRI8 = 0.00;
	Double recallPRI8 = 0.00;
	Integer NPSLApri8 = 0;
	Double NPSLApartpri8 = 0.00;
	
	Double precisionPRI9 = 0.00;
	Double recallPRI9 = 0.00;
	Integer NPSLApri9 = 0;
	Double NPSLApartpri9 = 0.00;
	
	Double precisionTotal = 0.00;
	Double recallTotal = 0.00;
	Integer NPSLATotal = 0;
	Double NPSLApartTotal = 0.00;
	
	String[][] jtable = new String[8][11];
					
		for (XTrace t : log) {
			
					
			XAttributeMap am = t.getAttributes();
			//String caseID = am.get("concept:name").toString();
			String caseDuration = am.get("time:duration").toString();
			String SLAviolated = am.get("outcome:case_duration").toString();
			
			if (SLAviolated.equalsIgnoreCase("true")){delayed++;};
			
			String expectedOutcome = am.get("outcome:expected_outcome").toString();
			
			Long lduration = Long.valueOf(caseDuration);
			//Double duration = (double)lduration/1000/60/60/24;
			//caseDuration = duration.toString();
			
			Long times[] = new Long[9];
			
			String pri11 = am.get("time:PRI1").toString();
			Long pri1t = Long.valueOf(pri11);
			times[0]=pri1t;
			
			String pri22 = am.get("time:PRI2").toString();
			Long pri2t = Long.valueOf(pri22);
			times[1]=pri2t;
			
			String pri33 = am.get("time:PRI3").toString();
			Long pri3t = Long.valueOf(pri33);
			times[2]=pri3t;	
			
			String pri44 = am.get("time:PRI4").toString();
			Long pri4t = Long.valueOf(pri44);
			times[3]=pri4t;
			
			String pri55 = am.get("time:PRI5").toString();
			Long pri5t = Long.valueOf(pri55);
			times[4]=pri5t;
			
			String pri66 = am.get("time:PRI6").toString();
			Long pri6t = Long.valueOf(pri66);
			times[5]=pri6t;
			
			String pri77 = am.get("time:PRI7").toString();
			Long pri7t = Long.valueOf(pri77);
			times[6]=pri7t;
			
			String pri88 = am.get("time:PRI8").toString();
			Long pri8t = Long.valueOf(pri88);
			times[7]=pri8t;
			
			String pri99 = am.get("time:PRI9").toString();
			Long pri9t = Long.valueOf(pri99);
			times[8]=pri9t;
			
			Long min = lduration;
						
			for (int j=0; j<9;j++)
			{
				if ((times[j] > 0) && (times[j] < min)){min = times[j];}
				
			}
			
			
			
			String pri5 = am.get("PRI5").toString();
			
			if(pri5.equalsIgnoreCase("true") && SLAviolated.equalsIgnoreCase("true")){TPpri5++;}
			if(pri5.equalsIgnoreCase("true") && !SLAviolated.equalsIgnoreCase("true")){FPpri5++;}
			if(pri5.equalsIgnoreCase("true") && SLAviolated.equalsIgnoreCase("true") && (pri5t <= SLA)){TPSLApri5++;}
			if(pri5.equalsIgnoreCase("true") && !SLAviolated.equalsIgnoreCase("true") && (pri5t <= SLA)){FPSLApri5++;}
			
			String pri7 = am.get("PRI7").toString();
			
			if(pri7.equalsIgnoreCase("true") && SLAviolated.equalsIgnoreCase("true")){TPpri7++;}
			if(pri7.equalsIgnoreCase("true") && !SLAviolated.equalsIgnoreCase("true")){FPpri7++;}
			if(pri7.equalsIgnoreCase("true") && SLAviolated.equalsIgnoreCase("true") && (pri7t <= SLA)){TPSLApri7++;}
			if(pri7.equalsIgnoreCase("true") && !SLAviolated.equalsIgnoreCase("true") && (pri7t <= SLA)){FPSLApri7++;}
		
			
			Integer pri1 = 0;
			Integer pri2 = 0;
			Integer pri3 = 0;
			Integer pri4 = 0;
			Integer pri6 = 0;
			Integer pri8 = 0;
			Integer pri9 = 0;
			Integer total = 0;
			
			if(pri5.equalsIgnoreCase("true") || pri7.equalsIgnoreCase("true")){total = 1;};
						 
			for (XEvent e : t) {
				
				String pris1 = e.getAttributes().get("PRI1").toString();
				String pris2 = e.getAttributes().get("PRI2").toString();
				String pris3 = e.getAttributes().get("PRI3").toString();
				String pris4 = e.getAttributes().get("PRI4").toString();
				String pris6 = e.getAttributes().get("PRI6").toString();
				String pris8 = e.getAttributes().get("PRI8").toString();
				String pris9 = e.getAttributes().get("PRI9").toString();
				
					
				
				if (pris1.equals("true")) {pri1=1;}
				if (pris2.equals("true")) {pri2=1;}
				if (pris3.equals("true")) {pri3=1;}
				if (pris4.equals("true")) {pri4=1;}
				if (pris6.equals("true")) {pri6=1;}
				if (pris8.equals("true")) {pri8=1;}
				if (pris9.equals("true")) {pri9=1;}
				
				if(pris1.equalsIgnoreCase("true") || pris2.equalsIgnoreCase("true") || pris3.equalsIgnoreCase("true") || pris4.equalsIgnoreCase("true") || pris6.equalsIgnoreCase("true") || pris8.equalsIgnoreCase("true") || pris9.equalsIgnoreCase("true")){total = 1;};		
					
			}
			
			if(pri1 == 1 && SLAviolated.equalsIgnoreCase("true")){TPpri1++;}
			if(pri1 == 1 && !SLAviolated.equalsIgnoreCase("true")){FPpri1++;}
			if(pri1 == 1 && SLAviolated.equalsIgnoreCase("true") && (pri1t <= SLA)){TPSLApri1++;}
			if(pri1 == 1 && !SLAviolated.equalsIgnoreCase("true") && (pri1t <= SLA)){FPSLApri1++;}
			
			if(pri2 == 1 && SLAviolated.equalsIgnoreCase("true")){TPpri2++;}
			if(pri2 == 1 && !SLAviolated.equalsIgnoreCase("true")){FPpri2++;}
			if(pri2 == 1 && SLAviolated.equalsIgnoreCase("true") && (pri2t <= SLA)){TPSLApri2++;}
			if(pri2 == 1 && !SLAviolated.equalsIgnoreCase("true") && (pri2t <= SLA)){FPSLApri2++;}
			
			if(pri3 == 1 && SLAviolated.equalsIgnoreCase("true")){TPpri3++;}
			if(pri3 == 1 && !SLAviolated.equalsIgnoreCase("true")){FPpri3++;}
			if(pri3 == 1 && SLAviolated.equalsIgnoreCase("true") && (pri3t <= SLA)){TPSLApri3++;}
			if(pri3 == 1 && !SLAviolated.equalsIgnoreCase("true") && (pri3t <= SLA)){FPSLApri3++;}
			
			if(pri4 == 1 && SLAviolated.equalsIgnoreCase("true")){TPpri4++;}
			if(pri4 == 1 && !SLAviolated.equalsIgnoreCase("true")){FPpri4++;}
			if(pri4 == 1 && SLAviolated.equalsIgnoreCase("true") && (pri4t <= SLA)){TPSLApri4++;}
			if(pri4 == 1 && !SLAviolated.equalsIgnoreCase("true") && (pri4t <= SLA)){FPSLApri4++;}
			
			if(pri6 == 1 && SLAviolated.equalsIgnoreCase("true")){TPpri6++;}
			if(pri6 == 1 && !SLAviolated.equalsIgnoreCase("true")){FPpri6++;}
			if(pri6 == 1 && SLAviolated.equalsIgnoreCase("true") && (pri6t <= SLA)){TPSLApri6++;}
			if(pri6 == 1 && !SLAviolated.equalsIgnoreCase("true") && (pri6t <= SLA)){FPSLApri6++;}
			
			if(pri8 == 1 && SLAviolated.equalsIgnoreCase("true")){TPpri8++;}
			if(pri8 == 1 && !SLAviolated.equalsIgnoreCase("true")){FPpri8++;}
			if(pri8 == 1 && SLAviolated.equalsIgnoreCase("true") && (pri8t <= SLA)){TPSLApri8++;}
			if(pri8 == 1 && !SLAviolated.equalsIgnoreCase("true") && (pri8t <= SLA)){FPSLApri8++;}
			
			if(pri9 == 1 && SLAviolated.equalsIgnoreCase("true")){TPpri9++;}
			if(pri9 == 1 && !SLAviolated.equalsIgnoreCase("true")){FPpri9++;}
			if(pri9 == 1 && SLAviolated.equalsIgnoreCase("true") && (pri9t <= SLA)){TPSLApri9++;}
			if(pri9 == 1 && !SLAviolated.equalsIgnoreCase("true") && (pri9t <= SLA)){FPSLApri9++;}
			
			if(total == 1 && SLAviolated.equalsIgnoreCase("true")){TPtotal++;}
			if(total == 1 && !SLAviolated.equalsIgnoreCase("true")){FPtotal++;}
			if(total == 1 && SLAviolated.equalsIgnoreCase("true") && (min <= SLA)){TPSLAtotal++;}
			if(total == 1 && !SLAviolated.equalsIgnoreCase("true") && (min <= SLA)){FPSLAtotal++;}
			
		} 	
		
		
	
		
		intime = num_traces - delayed;
		
		precisionPRI1 = (double)TPSLApri1/(TPSLApri1+FPSLApri1);
		recallPRI1 = (double)TPSLApri1/delayed;
		NPSLApri1 = delayed - TPSLApri1;
		NPSLApartpri1 = (double)NPSLApri1/delayed;
		
		precisionPRI2 = (double)TPSLApri2/(TPSLApri2+FPSLApri2);
		recallPRI2 = (double)TPSLApri2/delayed;
		NPSLApri2 = delayed - TPSLApri2;
		NPSLApartpri2 = (double)NPSLApri2/delayed;
		
		precisionPRI3 = (double)TPSLApri3/(TPSLApri3+FPSLApri3);
		recallPRI3 = (double)TPSLApri3/delayed;
		NPSLApri3 = delayed - TPSLApri3;
		NPSLApartpri3 = (double)NPSLApri3/delayed;
		
		precisionPRI4 = (double)TPSLApri4/(TPSLApri4+FPSLApri4);
		recallPRI4 = (double)TPSLApri4/delayed;
		NPSLApri4 = delayed - TPSLApri4;
		NPSLApartpri4 = (double)NPSLApri4/delayed;
		
		precisionPRI5 = (double)TPSLApri5/(TPSLApri5+FPSLApri5);
		recallPRI5 = (double)TPSLApri5/delayed;
		NPSLApri5 = delayed - TPSLApri5;
		NPSLApartpri5 = (double)NPSLApri5/delayed;
		
		precisionPRI6 = (double)TPSLApri6/(TPSLApri6+FPSLApri6);
		recallPRI6 = (double)TPSLApri6/delayed;
		NPSLApri6 = delayed - TPSLApri6;
		NPSLApartpri6 = (double)NPSLApri6/delayed;
		
		precisionPRI7 = (double)TPSLApri7/(TPSLApri7+FPSLApri7);
		recallPRI7 = (double)TPSLApri7/delayed;
		NPSLApri7 = delayed - TPSLApri7;
		NPSLApartpri7 = (double)NPSLApri7/delayed;
		
		precisionPRI8 = (double)TPSLApri8/(TPSLApri8+FPSLApri8);
		recallPRI8 = (double)TPSLApri8/delayed;
		NPSLApri8 = delayed - TPSLApri8;
		NPSLApartpri8 = (double)NPSLApri8/delayed;
		
		precisionPRI9 = (double)TPSLApri9/(TPSLApri9+FPSLApri9);
		recallPRI9 = (double)TPSLApri9/delayed;
		NPSLApri9 = delayed - TPSLApri9;
		NPSLApartpri9 = (double)NPSLApri9/delayed;
		
		precisionTotal = (double)TPSLAtotal/(TPSLAtotal+FPSLAtotal);
		recallTotal = (double)TPSLAtotal/delayed;
		NPSLATotal = delayed - TPSLAtotal;
		NPSLApartTotal = (double)NPSLATotal/delayed;
		
		
		jtable[0][0] = "TP";
		jtable[1][0] = "FP";
		jtable[2][0] = "TP<SLA";
		jtable[3][0] = "FP<SLA";
		jtable[4][0] = "precision<SLA";
		jtable[5][0] = "recall<SLA";
		jtable[6][0] = "NP<SLA";
		jtable[7][0] = "NP<SLA%";
		
		jtable[0][1] = TPpri1.toString();
		jtable[1][1] = FPpri1.toString();
		jtable[2][1] = TPSLApri1.toString();
		jtable[3][1] = FPSLApri1.toString();
		jtable[4][1] = precisionPRI1.toString();
		jtable[5][1] = recallPRI1.toString();
		jtable[6][1] = NPSLApri1.toString();
		jtable[7][1] = NPSLApartpri1.toString();
		
		jtable[0][2] = TPpri2.toString();
		jtable[1][2] = FPpri2.toString();
		jtable[2][2] = TPSLApri2.toString();
		jtable[3][2] = FPSLApri2.toString();
		jtable[4][2] = precisionPRI2.toString();
		jtable[5][2] = recallPRI2.toString();
		jtable[6][2] = NPSLApri2.toString();
		jtable[7][2] = NPSLApartpri2.toString();
		
		jtable[0][3] = TPpri3.toString();
		jtable[1][3] = FPpri3.toString();
		jtable[2][3] = TPSLApri3.toString();
		jtable[3][3] = FPSLApri3.toString();
		jtable[4][3] = precisionPRI3.toString();
		jtable[5][3] = recallPRI3.toString();
		jtable[6][3] = NPSLApri3.toString();
		jtable[7][3] = NPSLApartpri3.toString();
		
		jtable[0][4] = TPpri4.toString();
		jtable[1][4] = FPpri4.toString();
		jtable[2][4] = TPSLApri4.toString();
		jtable[3][4] = FPSLApri4.toString();
		jtable[4][4] = precisionPRI4.toString();
		jtable[5][4] = recallPRI4.toString();
		jtable[6][4] = NPSLApri4.toString();
		jtable[7][4] = NPSLApartpri4.toString();
		
		jtable[0][5] = TPpri5.toString();
		jtable[1][5] = FPpri5.toString();
		jtable[2][5] = TPSLApri5.toString();
		jtable[3][5] = FPSLApri5.toString();
		jtable[4][5] = precisionPRI5.toString();
		jtable[5][5] = recallPRI5.toString();
		jtable[6][5] = NPSLApri5.toString();
		jtable[7][5] = NPSLApartpri5.toString();
		
		jtable[0][6] = TPpri6.toString();
		jtable[1][6] = FPpri6.toString();
		jtable[2][6] = TPSLApri6.toString();
		jtable[3][6] = FPSLApri6.toString();
		jtable[4][6] = precisionPRI6.toString();
		jtable[5][6] = recallPRI6.toString();
		jtable[6][6] = NPSLApri6.toString();
		jtable[7][6] = NPSLApartpri6.toString();
		
		jtable[0][7] = TPpri7.toString();
		jtable[1][7] = FPpri7.toString();
		jtable[2][7] = TPSLApri7.toString();
		jtable[3][7] = FPSLApri7.toString();
		jtable[4][7] = precisionPRI7.toString();
		jtable[5][7] = recallPRI7.toString();
		jtable[6][7] = NPSLApri7.toString();
		jtable[7][7] = NPSLApartpri7.toString();
		
		jtable[0][8] = TPpri8.toString();
		jtable[1][8] = FPpri8.toString();
		jtable[2][8] = TPSLApri8.toString();
		jtable[3][8] = FPSLApri8.toString();
		jtable[4][8] = precisionPRI8.toString();
		jtable[5][8] = recallPRI8.toString();
		jtable[6][8] = NPSLApri8.toString();
		jtable[7][8] = NPSLApartpri8.toString();
		
		jtable[0][9] = TPpri9.toString();
		jtable[1][9] = FPpri9.toString();
		jtable[2][9] = TPSLApri9.toString();
		jtable[3][9] = FPSLApri9.toString();
		jtable[4][9] = precisionPRI9.toString();
		jtable[5][9] = recallPRI9.toString();
		jtable[6][9] = NPSLApri9.toString();
		jtable[7][9] = NPSLApartpri9.toString();
		
		jtable[0][10] = TPtotal.toString();
		jtable[1][10] = FPtotal.toString();
		jtable[2][10] = TPSLAtotal.toString();
		jtable[3][10] = FPSLAtotal.toString();
		jtable[4][10] = precisionTotal.toString();
		jtable[5][10] = recallTotal.toString();
		jtable[6][10] = NPSLATotal.toString();
		jtable[7][10] = NPSLApartTotal.toString();
	
		return jtable;

	}

//---------------------------------------Discovery times---------------------------------------------------------

public String[][] caseRiskTimes(XLog log) {
		
	Integer num_traces = log.size();
	String[][] jtable = new String[num_traces][12];
		int i = 0;
		
				
		for (XTrace t : log) {
			
		
			XAttributeMap am = t.getAttributes();
			String caseID = am.get("concept:name").toString();
			String caseDuration = am.get("time:duration").toString();
			String expectedOutcome = am.get("outcome:expected_outcome").toString();
			
			Long lduration = Long.valueOf(caseDuration);
			Double duration = (double)lduration/1000/60/60/24;
			caseDuration = duration.toString();
			
			String pri1 = am.get("time:PRI1").toString();
			Long pri1t = Long.valueOf(pri1);
			Double pri1time = (double)pri1t/1000/60/60/24;
			pri1 = pri1time.toString();
			
			String pri2 = am.get("time:PRI2").toString();
			Long pri2t = Long.valueOf(pri2);
			Double pri2time = (double)pri2t/1000/60/60/24;
			pri2 = pri2time.toString();
			
			String pri3 = am.get("time:PRI3").toString();
			Long pri3t = Long.valueOf(pri3);
			Double pri3time = (double)pri3t/1000/60/60/24;
			pri3 = pri3time.toString();
			
			String pri4 = am.get("time:PRI4").toString();
			Long pri4t = Long.valueOf(pri4);
			Double pri4time = (double)pri4t/1000/60/60/24;
			pri4 = pri4time.toString();
			
			String pri5 = am.get("time:PRI5").toString();
			Long pri5t = Long.valueOf(pri5);
			Double pri5time = (double)pri5t/1000/60/60/24;
			pri5 = pri5time.toString();
			
			String pri6 = am.get("time:PRI6").toString();
			Long pri6t = Long.valueOf(pri6);
			Double pri6time = (double)pri6t/1000/60/60/24;
			pri6 = pri6time.toString();
			
			String pri7 = am.get("time:PRI7").toString();
			Long pri7t = Long.valueOf(pri7);
			Double pri7time = (double)pri7t/1000/60/60/24;
			pri7 = pri7time.toString();
			
			String pri8 = am.get("time:PRI8").toString();
			Long pri8t = Long.valueOf(pri8);
			Double pri8time = (double)pri8t/1000/60/60/24;
			pri8 = pri8time.toString();
			
			String pri9 = am.get("time:PRI9").toString();
			Long pri9t = Long.valueOf(pri9);
			Double pri9time = (double)pri9t/1000/60/60/24;
			pri9 = pri9time.toString();
			
			jtable[i][0] = caseID;
			jtable[i][1] = caseDuration;
			jtable[i][2] = expectedOutcome;
			
			jtable[i][3] = pri1;
			jtable[i][4] = pri2;
			jtable[i][5] = pri3;
			jtable[i][6] = pri4;
			jtable[i][7] = pri5;
			jtable[i][8] = pri6;
			jtable[i][9] = pri7;
			jtable[i][10] = pri8;
			jtable[i][11] = pri9;
			i++;
			
		} 		
	          
			
		return jtable;

	}

*///---------------------------------------activity risks table---------------------------------------------------------
/*
public String[][] activityRisks(XLog log, ConcurrentSkipListSet<String> activities) {
		
	Integer num = activities.size();
	
	String[][] jtable = new String[num][8];
	
	Vector<PriActivityRisks> activityRisks = new Vector<PriActivityRisks>();
	Iterator iterator = activities.iterator();			
	while (iterator.hasNext()) 
	{
		activityRisks.add(new PriActivityRisks((String)iterator.next()));
	}
	
	
	int index = 0;	
	for (XTrace t : log) {
			
							 
			for (XEvent e : t) {
				
				String eventName = XConceptExtension.instance().extractName(e);
				index = getIndex(activityRisks, eventName);
		
				String pris1 = e.getAttributes().get("PRI1").toString();
				String pris2 = e.getAttributes().get("PRI2").toString();
				String pris3 = e.getAttributes().get("PRI3").toString();
				String pris4 = e.getAttributes().get("PRI4").toString();
				String pris6 = e.getAttributes().get("PRI6").toString();
				String pris8 = e.getAttributes().get("PRI8").toString();
				String pris9 = e.getAttributes().get("PRI9").toString();
				
				if (pris1.equals("true")) {activityRisks.elementAt(index).pri1++;}
				if (pris2.equals("true")) {activityRisks.elementAt(index).pri2++;}
				if (pris3.equals("true")) {activityRisks.elementAt(index).pri3++;}
				if (pris4.equals("true")) {activityRisks.elementAt(index).pri4++;}
				if (pris6.equals("true")) {activityRisks.elementAt(index).pri6++;}
				if (pris8.equals("true")) {activityRisks.elementAt(index).pri8++;}
				if (pris9.equals("true")) {activityRisks.elementAt(index).pri9++;}
					
			}
			
			for(int i=0; i<num;i++){
				
				jtable[i][0] = activityRisks.elementAt(i).name;
				jtable[i][1] = activityRisks.elementAt(i).pri1.toString();
				jtable[i][2] = activityRisks.elementAt(i).pri2.toString();
				jtable[i][3] = activityRisks.elementAt(i).pri3.toString();
				jtable[i][4] = activityRisks.elementAt(i).pri4.toString();
				jtable[i][5] = activityRisks.elementAt(i).pri6.toString();
				jtable[i][6] = activityRisks.elementAt(i).pri8.toString();
				jtable[i][7] = activityRisks.elementAt(i).pri9.toString();
				
			}
			
		} 		
	          
			
		return jtable;

	}
//------------------------------------------------------
//---------------------------------------activity risks table---------------------------------------------------------
*/
/*public String[][] eventOut(XLog log) {
		
	Integer num = 200000;
	
	String[][] jtable = new String[num][9];
	
	
	int i = 0;	
	
	
	for (XTrace t : log) {
		XAttributeMap am = t.getAttributes();
		String caseID = am.get("concept:name").toString();
					 
			for (XEvent e : t) {
				boolean out = false;
				String eventName = XConceptExtension.instance().extractName(e);
			
				String pris1 = e.getAttributes().get("PRI1").toString();
				String pris2 = e.getAttributes().get("PRI2").toString();
				String pris3 = e.getAttributes().get("PRI3").toString();
				String pris4 = e.getAttributes().get("PRI4").toString();
				String pris6 = e.getAttributes().get("PRI6").toString();
				String pris8 = e.getAttributes().get("PRI8").toString();
				String pris9 = e.getAttributes().get("PRI9").toString();
				
				
				if (pris1.equals("true")) {out = true;}
				if (pris2.equals("true")) {out = true;}
				if (pris3.equals("true")) {out = true;}
				if (pris4.equals("true")) {out = true;}
				if (pris6.equals("true")) {out = true;}
				if (pris8.equals("true")) {out = true;}
				if (pris9.equals("true")) {out = true;}
				
				if (out)
				{
					jtable[i][0] = caseID;
					jtable[i][1] = eventName;
					jtable[i][2] = pris1;
					jtable[i][3] = pris2;
					jtable[i][4] = pris3;
					jtable[i][5] = pris4;
					jtable[i][6] = pris6;
					jtable[i][7] = pris8;
					jtable[i][8] = pris9;
					i++;
				}
					
			}
			
		} 		
	          
			
		return jtable;

	}
//------------------------------------------------------
*//*public int getIndex(Vector<PriActivityRisks> activityRisks, String name){
	  
	  int index = -1;
	  
	  for (int i=0;i<activityRisks.size();i++){
		  
		  if(activityRisks.elementAt(i).name.equals(name)){index = i;}
		  
	  }
	  return index;
}
*/
//---------------------------------------Display case thresholds----------------------------------------------------
/*
public String[][] resourceThresholdsOut(PriCaseThreshold thresholds) {
	
	int resNum = thresholds.resources.size();
	int actNum = thresholds.activities.size();
	
	String[][] jtable = new String[resNum+1][actNum+4];
	
	for (int i=0; i<resNum; i++)
	{
		for(int j=0; j<actNum; j++)
		{
			
			String activity = thresholds.activities.elementAt(j).name;
			int actIndex = thresholds.resources.elementAt(i).getIndex(activity);
			String out;
			if (actIndex == -1){out = "NA";}
			else{out = thresholds.resources.elementAt(i).resourceActivities.elementAt(actIndex).PRI8.toString();}
			jtable[i+1][j+1] = out;
			jtable[0][j+1] = activity;
			jtable[i+1][0] = thresholds.resources.elementAt(i).resource;
		}
		
		jtable[i+1][actNum+1] = thresholds.resources.elementAt(i).PRI9av.toString();
		jtable[i+1][actNum+2] = thresholds.resources.elementAt(i).PRI9dev.toString();
		jtable[i+1][actNum+3] = thresholds.resources.elementAt(i).PRI9threshold.toString();
	}
	jtable[0][actNum+1] = "PRI9av";
	jtable[0][actNum+2] = "PRI9dev";
	jtable[0][actNum+3] = "PRI9threshold";
			
		return jtable;

	}

*///-----------------------------------------MEDIAN-----------------------------------------------------
/*
	public Double Median(Vector<Long> numbers)
	{
		Collections.sort(numbers);
		double median = 0.0;
		int size = numbers.size();
		if (size!=0)	
		{if( (size % 2) != 0)
		{median = numbers.elementAt((size+1)/2-1);}
		else
		{median = 0.5*numbers.elementAt(size/2-1)+0.5*numbers.elementAt(size/2);}}
		return median;
	}
	
*///-----------------------------------------Double MEDIAN-----------------------------------------------------
/*
		public Double DoubleMedian(Vector<Double> numbers)
		{
			Collections.sort(numbers);
			double median = 0.0;
			int size = numbers.size();
			if (size!=0)
			{if( (size % 2) != 0)
			{median = numbers.elementAt((size+1)/2-1);}
			else
			{median = 0.5*numbers.elementAt(size/2-1)+0.5*numbers.elementAt(size/2);}}
			return median;
		}
	
*///-----------------------------------MAD--------------------------------------------------------------
/*	
	public Double MAD (Vector<Long> numbers){
		
	Double MAD = 0.00;
	if (numbers.size()!=0)	{
	Vector<Double> MadVals = new Vector<Double>();
	Collections.sort(numbers);

	Double median = Median(numbers);
	for(int i=0;i<numbers.size();i++)
	{MadVals.add(Math.abs(numbers.elementAt(i)-median));}
	Collections.sort(MadVals);
	MAD = DoubleMedian(MadVals);
					
										
		}
		
	return MAD;
		
	};
	
*/	

/*
public String[][] caseThresholdsOut(PriCaseThreshold thresholds) {
	
	String[][] jtable = new String[4][16];
		
	jtable[0][0] = "PRI5";
	jtable[1][0] = "PRI7";
	jtable[0][1] = thresholds.PRI5threshold.toString();
	jtable[1][1] = thresholds.PRI7threshold.toString();
	jtable[0][2] = thresholds.PRI5av.toString();
	jtable[1][2] = thresholds.PRI7av.toString();
	jtable[0][3] = thresholds.PRI5dev.toString();
	jtable[1][3] = thresholds.PRI7dev.toString();
	
	jtable[0][4] = "PRI5t";
	jtable[1][4] = "PRI5f";
	jtable[2][4] = "PRI7t";
	jtable[3][4] = "PRI7f";
	
	for (int i=0; i<11; i++){
		
		jtable[0][i+5] = Integer.toString(thresholds.pri5t[i]);
		jtable[1][i+5] = Integer.toString(thresholds.pri5f[i]);
		jtable[2][i+5] = Integer.toString(thresholds.pri7t[i]);
		jtable[3][i+5] = Integer.toString(thresholds.pri7f[i]);
			
	}
	
	//jtable[0][4] = Integer.toString(thresholds.pri5t1t);
	//jtable[1][4] = Integer.toString(thresholds.pri7t1t);
	
	//jtable[0][5] = Integer.toString(thresholds.pri5t1f);
	//jtable[1][5] = Integer.toString(thresholds.pri7t1f);
	
	//jtable[0][6] = Integer.toString(thresholds.pri5t2t);
	//jtable[1][6] = Integer.toString(thresholds.pri7t2t);
	//
	//jtable[0][7] = Integer.toString(thresholds.pri5t2f);
	//jtable[1][7] = Integer.toString(thresholds.pri7t2f);
	
	//jtable[0][8] = Integer.toString(thresholds.pri5t3t);
	//jtable[1][8] = Integer.toString(thresholds.pri7t3t);
	
	//jtable[0][9] = Integer.toString(thresholds.pri5t3f);
	//jtable[1][9] = Integer.toString(thresholds.pri7t3f);
		
			
		return jtable;

	}
*/
