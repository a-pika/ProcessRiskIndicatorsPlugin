package org.processmining.processriskindicators.inout;

import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeBooleanImpl;


public class InOut {


public String[][] casePRIs(XLog log) {
	
	Integer num_traces = log.size();
	String[][] jtable = new String[num_traces][9];
		int i = 0;
						
		for (XTrace t : log) {
			XAttributeMap am = t.getAttributes();
			String caseID = am.get("concept:name").toString();
			
			String caseDuration = am.get("time:duration").toString();
			Double casedur = Double.parseDouble(caseDuration);
			caseDuration = String.format("%.2f", (Double)(casedur/1000/60/60));
			
			String caseabdur = am.get("outcome:SLA_violation").toString();
			Double caseadur = Double.parseDouble(caseabdur);
			caseabdur = ((Double)(caseadur/1000/60/60/24)).toString();
			
			String expectedOutcome = am.get("outcome:expected_outcome").toString();
			
			String expectedAbDur = am.get("outcome:expected_ab_duration").toString();
			Double exabdur = Double.parseDouble(expectedAbDur);
			expectedAbDur = ((Double)(exabdur/1000/60/60/24)).toString();
			
			String multres = am.get("feature:multiple_resources").toString();
			
			Integer act_dur = 0;
			Integer wait_dur = 0;
			Integer act_rep = 0;
			Integer atyp_act = 0;
			Integer mult_res = 0;
			Integer subproc = 0;
			
			if (multres.equals("true")) {mult_res = 1;}
			
						 
			for (XEvent e : t) {
				
				String actdur = e.getAttributes().get("feature:act_duration").toString();
				String waitdur = e.getAttributes().get("feature:wait_duration").toString();
				String multact = e.getAttributes().get("feature:act_repetition").toString();
				String oddact = e.getAttributes().get("feature:odd_activity").toString();
				String f_subprocDuration = e.getAttributes().get("feature:subproc_duration").toString();
				
				if (actdur.equals("true")) {act_dur = 1;}
				if (waitdur.equals("true")) { wait_dur = 1;}
				if (multact.equals("true")) {act_rep = 1;}
				if (oddact.equals("true")) {atyp_act = 1;}
				if (f_subprocDuration.equals("true")) {subproc = 1;}
						
					
			}
			jtable[i][0] = caseID;
			jtable[i][1] = caseDuration;
			jtable[i][2] = expectedOutcome;
			jtable[i][3] = act_dur.toString();
			jtable[i][4] = wait_dur.toString();
			jtable[i][5] = act_rep.toString();
			jtable[i][6] = atyp_act.toString();
			jtable[i][7] = mult_res.toString();
			jtable[i][8] = subproc.toString();
			i++;
			
		} 		
	          
			
		return jtable;

	}


public String[][] getResultNew(XLog log) {
	
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


public String[][] getResultMult(XLog log, Integer num) {
	
	String[][] jtable = new String[1][8];
		
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
				
	jtable[0][0] = num.toString();
	jtable[0][1] = num_traces.toString();
	jtable[0][2] = correctlypredicted.toString();
	jtable[0][3] = falselypredicted.toString();
	jtable[0][4] = notpredicted.toString();
	jtable[0][5] = correctnotpredicted.toString();
	jtable[0][6] = String.format("%.2f", precision);
	jtable[0][7] = String.format("%.2f", recall);
		
	return jtable;

}


public XLog processTimeNew(XLog log) {
	
	XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
	
	for (XTrace t : log) {
		
		XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
		boolean expectedOutcome = false;
		
		XAttributeMap am = t.getAttributes();
		String multres = am.get("feature:multiple_resources").toString();			 
		
		for (XEvent e : t) {
			
			XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
								
			
				String actdur = e.getAttributes().get("feature:act_duration").toString();
				String waitdur = e.getAttributes().get("feature:wait_duration").toString();
				String multact = e.getAttributes().get("feature:act_repetition").toString();
				String oddact = e.getAttributes().get("feature:odd_activity").toString();
				String subprocdur = e.getAttributes().get("feature:subproc_duration").toString();
								
				//checking for double feature: activity repetition and odd activity
				
			/*	if (multact.equals("true") && oddact.equals("true"))
				{
					
					event.getAttributes().put("feature:act_repetition",new XAttributeBooleanImpl("feature:act_repetition",false));
					event.getAttributes().put("feature:rep_duration",new XAttributeContinuousImpl("feature:rep_duration",0.00));
									
				}
		*/		
							
			/*	XAttributeContinuous odd = (XAttributeContinuous)e.getAttributes().get("feature:odd_activity_duration");
				double oddDur = odd.getValue();
				XAttributeContinuous cycle = (XAttributeContinuous)e.getAttributes().get("feature:rep_duration");
				double cycleDur = cycle.getValue();
				XAttributeContinuous act = (XAttributeContinuous)e.getAttributes().get("feature:ab_duration");
				double actDur = act.getValue();
				XAttributeContinuous wait = (XAttributeContinuous)e.getAttributes().get("feature:ab_wait_duration");
				double waitDur = wait.getValue();
		*/		
				//XAttributeDiscrete subprocDuration = (XAttributeDiscrete)e.getAttributes().get("subprocess:duration");
				//long subprocDur = subprocDuration.getValue();
				
				//expectedAbDur+=oddDur+cycleDur+actDur+waitDur;
				if (multact.equals("true") || subprocdur.equals("true") || oddact.equals("true") || actdur.equals("true") || waitdur.equals("true"))
				{
					expectedOutcome = true;
				};
				
				//if (multact.equals("true")){totalAbRepDuration+=subprocDur;}
				
				trace.add(event);
				
				
		}
		
		if (multres.equals("true"))
		{
			expectedOutcome = true;
		}
		
		//long dur_threshold = 36000000;
		
		//if (totalAbRepDuration > dur_threshold)
		//{		expectedOutcome = true;		}
		
		//if ((totalAbRepDuration > 0) && (totalAbRepDuration <= dur_threshold))
			
		//{	trace.getAttributes().put("time:PRI3",new XAttributeDiscreteImpl("time:PRI3",0)); 	}
		
		trace.getAttributes().put("outcome:expected_outcome",new XAttributeBooleanImpl("outcome:expected_outcome",expectedOutcome));
		//trace.getAttributes().put("outcome:expected_ab_duration",new XAttributeContinuousImpl("outcome:expected_ab_duration",expectedAbDur));
		copylog.add(trace);
	} 
	
	return copylog;

}

}

//-------------------------------------------PREV. VERSIONS---------------------------------------------------------
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//---------------------------------------LOG OUTPUT---------------------------------------------------------
/*
public String[][] outAll(XLog log) {
		
		String[][] jtable = new String[200000][27];
		int i = 0;
		
				
		for (XTrace t : log) {
			
		
			XAttributeMap am = t.getAttributes();
			
			String caseID = am.get("concept:name").toString();
			String caseDuration = am.get("time:duration").toString();
			Double casedur = Double.parseDouble(caseDuration);
			caseDuration = ((Double)(casedur/1000/60/60/24)).toString();
			String caseabdur = am.get("outcome:SLA_violation").toString();
			Double caseadur = Double.parseDouble(caseabdur);
			caseabdur = ((Double)(caseadur/1000/60/60/24)).toString();
			
			String PRI5av = am.get("PRI5:average").toString();
			String PRI5stDev = am.get("PRI5:stDev").toString();
			String PRI5dev = am.get("PRI5:deviation").toString();
			
			String PRI7av = am.get("PRI7:average").toString();
			String PRI7stDev = am.get("PRI7:stDev").toString();
			String PRI7dev = am.get("PRI7:deviation").toString();
			
		
						 
			for (XEvent e : t) {
				
									
					String eventName = XConceptExtension.instance().extractName(e);
					String lifecycle = XLifecycleExtension.instance().extractTransition(e);
					
					String actDuration = e.getAttributes().get("time:duration").toString();
					String subprocDuration = e.getAttributes().get("subprocess:duration").toString();
					
					String PRI1av = e.getAttributes().get("PRI1:average").toString();
					String PRI1stDev = e.getAttributes().get("PRI1:stDev").toString();
					String PRI1dev = e.getAttributes().get("PRI1:deviation").toString();
					
					String PRI2av = e.getAttributes().get("PRI2:average").toString();
					String PRI2stDev = e.getAttributes().get("PRI2:stDev").toString();
					String PRI2dev = e.getAttributes().get("PRI2:deviation").toString();
					
					String PRI3av = e.getAttributes().get("PRI3:average").toString();
					String PRI3stDev = e.getAttributes().get("PRI3:stDev").toString();
					String PRI3dev = e.getAttributes().get("PRI3:deviation").toString();
					
					String PRI4part = e.getAttributes().get("PRI4:part").toString();
					String PRI4times = e.getAttributes().get("PRI4:times").toString();
					
					String PRI6av = e.getAttributes().get("PRI6:average").toString();
					String PRI6stDev = e.getAttributes().get("PRI6:stDev").toString();
					String PRI6dev = e.getAttributes().get("PRI6:deviation").toString();
					
					
					jtable[i][0] = caseID;
					jtable[i][1] = caseDuration;
					jtable[i][2] = caseabdur;
					jtable[i][3] = eventName;
					jtable[i][4] = lifecycle;
					jtable[i][5] = actDuration;
					jtable[i][6] = subprocDuration;
					jtable[i][7] = PRI1av;
					jtable[i][8] = PRI1stDev;
					jtable[i][9] = PRI1dev;
					jtable[i][10] = PRI2av;
					jtable[i][11] = PRI2stDev;
					jtable[i][12] = PRI2dev;
					jtable[i][13] = PRI3av;
					jtable[i][14] = PRI3stDev;
					jtable[i][15] = PRI3dev;
					jtable[i][16] = PRI4part;
					jtable[i][17] = PRI4times;
					jtable[i][18] = PRI5av;
					jtable[i][19] = PRI5stDev;
					jtable[i][20] = PRI5dev;
					jtable[i][21] = PRI6av;
					jtable[i][22] = PRI6stDev;
					jtable[i][23] = PRI6dev;
					jtable[i][24] = PRI7av;
					jtable[i][25] = PRI7stDev;
					jtable[i][26] = PRI7dev;
					
					
					i++;
			}
			
		} 		
	          
			
		return jtable;

	}

*/
//---------------------------------------LOG OUTPUT---------------------------------------------------------
/*
public String[][] outLog(XLog log) {
		
		String[][] jtable = new String[200000][29];
		int i = 0;
		
				
		for (XTrace t : log) {
			
		
			XAttributeMap am = t.getAttributes();
			String caseID = am.get("concept:name").toString();
			String caseDuration = am.get("time:duration").toString();
			String caseSLAdur = am.get("outcome:case_duration").toString();
			String caseabdur = am.get("outcome:SLA_violation").toString();
			String expectedOutcome = am.get("outcome:expected_outcome").toString();
			String expectedAbDur = am.get("outcome:expected_ab_duration").toString();
			String multres = am.get("feature:multiple_resources").toString();
			String abmultres = am.get("feature:ab_mult_res").toString();
			String caseQuality = am.get("outcome:case_quality").toString();
			String caseExpectedQuality = am.get("outcome:expected_quality").toString();
			String testset = am.get("set:test").toString();
			String SLA = am.get("time:SLA").toString();
		
						 
			for (XEvent e : t) {
				
									
					String eventName = XConceptExtension.instance().extractName(e);
					String lifecycle = XLifecycleExtension.instance().extractTransition(e);
					String actdur = e.getAttributes().get("feature:act_duration").toString();
					String abactdur = e.getAttributes().get("feature:ab_duration").toString();
					String waitdur = e.getAttributes().get("feature:wait_duration").toString();
					String abwaitdur = e.getAttributes().get("feature:ab_wait_duration").toString();
					String multact = e.getAttributes().get("feature:act_repetition").toString();
					String repdur = e.getAttributes().get("feature:rep_duration").toString();
					String oddact = e.getAttributes().get("feature:odd_activity").toString();
					String oddactdur = e.getAttributes().get("feature:odd_activity_duration").toString();
					String riskyslot = e.getAttributes().get("feature:risky_timeslot").toString();
					String inexpres = e.getAttributes().get("feature:inexperienced_resource").toString();
					String inexpresnumexec = e.getAttributes().get("feature:ab_num_exec").toString();
					String actDuration = e.getAttributes().get("time:duration").toString();
					String subprocDuration = e.getAttributes().get("subprocess:duration").toString();
					String f_subprocDuration = e.getAttributes().get("feature:subproc_duration").toString();
					String f_ab_subprocDuration = e.getAttributes().get("feature:subproc_ab_duration").toString();
					
					jtable[i][0] = caseID;
					jtable[i][1] = eventName;
					jtable[i][2] = lifecycle;
					jtable[i][3] = caseDuration;
					jtable[i][4] = caseSLAdur;
					jtable[i][5] = caseabdur;
					jtable[i][6] = actdur;
					jtable[i][7] = abactdur;
					jtable[i][8] = waitdur;
					jtable[i][9] = abwaitdur;
					jtable[i][10] = oddact;
					jtable[i][11] = oddactdur;
					jtable[i][12] = multact;
					jtable[i][13] = repdur;
					jtable[i][14] = expectedOutcome;
					jtable[i][15] = expectedAbDur;
					jtable[i][16] = riskyslot;
					jtable[i][17] = inexpres;
					jtable[i][18] = inexpresnumexec;
					jtable[i][19] = multres;
					jtable[i][20] = abmultres;
					jtable[i][21] = caseQuality;
					jtable[i][22] = caseExpectedQuality;
					jtable[i][23] = testset;
					jtable[i][24] = actDuration;
					jtable[i][25] = SLA;
					jtable[i][26] = subprocDuration;
					jtable[i][27] = f_subprocDuration;
					jtable[i][28] = f_ab_subprocDuration;
					
					i++;
			}
			
		} 		
	          
			
		return jtable;

	}
//---------------------------------------LOG OUTPUT Only risky events------------------------------------------------
*/

/*public String[][] outLogRisky(XLog log) {
		
		String[][] jtable = new String[200000][29];
		int i = 0;
		
				
		for (XTrace t : log) {
			
		
			XAttributeMap am = t.getAttributes();
			String caseID = am.get("concept:name").toString();
			String caseDuration = am.get("time:duration").toString();
			String caseSLAdur = am.get("outcome:case_duration").toString();
			String caseabdur = am.get("outcome:SLA_violation").toString();
			String expectedOutcome = am.get("outcome:expected_outcome").toString();
			String expectedAbDur = am.get("outcome:expected_ab_duration").toString();
			String multres = am.get("feature:multiple_resources").toString();
			String abmultres = am.get("feature:ab_mult_res").toString();
			String caseQuality = am.get("outcome:case_quality").toString();
			String caseExpectedQuality = am.get("outcome:expected_quality").toString();
			String testset = am.get("set:test").toString();
			String SLA = am.get("time:SLA").toString();
		
						 
			for (XEvent e : t) {
				
									
					String eventName = XConceptExtension.instance().extractName(e);
					String lifecycle = XLifecycleExtension.instance().extractTransition(e);
					String actdur = e.getAttributes().get("feature:act_duration").toString();
					String abactdur = e.getAttributes().get("feature:ab_duration").toString();
					String waitdur = e.getAttributes().get("feature:wait_duration").toString();
					String abwaitdur = e.getAttributes().get("feature:ab_wait_duration").toString();
					String multact = e.getAttributes().get("feature:act_repetition").toString();
					String repdur = e.getAttributes().get("feature:rep_duration").toString();
					String oddact = e.getAttributes().get("feature:odd_activity").toString();
					String oddactdur = e.getAttributes().get("feature:odd_activity_duration").toString();
					String riskyslot = e.getAttributes().get("feature:risky_timeslot").toString();
					String inexpres = e.getAttributes().get("feature:inexperienced_resource").toString();
					String inexpresnumexec = e.getAttributes().get("feature:ab_num_exec").toString();
					String actDuration = e.getAttributes().get("time:duration").toString();
					String subprocDuration = e.getAttributes().get("subprocess:duration").toString();
					String f_subprocDuration = e.getAttributes().get("feature:subproc_duration").toString();
					String f_ab_subprocDuration = e.getAttributes().get("feature:subproc_ab_duration").toString();
					
if (actdur.equals("true") || waitdur.equals("true") || multact.equals("true") || oddact.equals("true") || f_subprocDuration.equals("true") || multres.equals("true"))
{
					jtable[i][0] = caseID;
					jtable[i][1] = eventName;
					jtable[i][2] = lifecycle;
					jtable[i][3] = caseDuration;
					jtable[i][4] = caseSLAdur;
					jtable[i][5] = caseabdur;
					jtable[i][6] = actdur;
					jtable[i][7] = abactdur;
					jtable[i][8] = waitdur;
					jtable[i][9] = abwaitdur;
					jtable[i][10] = oddact;
					jtable[i][11] = oddactdur;
					jtable[i][12] = multact;
					jtable[i][13] = repdur;
					jtable[i][14] = expectedOutcome;
					jtable[i][15] = expectedAbDur;
					jtable[i][16] = riskyslot;
					jtable[i][17] = inexpres;
					jtable[i][18] = inexpresnumexec;
					jtable[i][19] = multres;
					jtable[i][20] = abmultres;
					jtable[i][21] = caseQuality;
					jtable[i][22] = caseExpectedQuality;
					jtable[i][23] = testset;
					jtable[i][24] = actDuration;
					jtable[i][25] = SLA;
					jtable[i][26] = subprocDuration;
					jtable[i][27] = f_subprocDuration;
					jtable[i][28] = f_ab_subprocDuration;
					
					i++;
}
			}
			
		} 		
	          
			
		return jtable;

	}

*///---------------------------------------score function----------------------------------------------------

/*

public String[][] caseRisksScore(XLog log) {
	
	Integer num_traces = log.size();
	String[][] jtable = new String[num_traces][20];
		int i = 0;
						
		for (XTrace t : log) {
			
		
			XAttributeMap am = t.getAttributes();
			String caseID = am.get("concept:name").toString();
			String caseDuration = am.get("time:duration").toString();
			String SLA = am.get("time:SLA").toString();
			String caseSLAdur = am.get("outcome:case_duration").toString();
			String caseabdur = am.get("outcome:SLA_violation").toString();
			String expectedOutcome = am.get("outcome:expected_outcome").toString();
			String expectedAbDur = am.get("outcome:expected_ab_duration").toString();
			String multres = am.get("feature:multiple_resources").toString();
			String PRI1 = am.get("time:PRI1").toString();
			String PRI2 = am.get("time:PRI2").toString();
			String PRI3 = am.get("time:PRI3").toString();
			String PRI4 = am.get("time:PRI4").toString();
			String PRI5 = am.get("time:PRI5").toString();
			String PRI6 = am.get("time:PRI6").toString();
			
			Long pri1 = Long.parseLong(PRI1);
			Long pri2 = Long.parseLong(PRI2);
			Long pri3 = Long.parseLong(PRI3);
			Long pri4 = Long.parseLong(PRI4);
			Long pri5 = Long.parseLong(PRI5);
			Long pri6 = Long.parseLong(PRI6);
			
			Long min = pri1;
			if (pri2 != 0 && (pri2 < min || min==0)){min = pri2;}
			if (pri3 != 0 && (pri3 < min || min==0)){min = pri3;}
			if (pri4 != 0 && (pri4 < min || min==0)){min = pri4;}
			if (pri5 != 0 && (pri5 < min || min==0)){min = pri5;}
			if (pri6 != 0 && (pri6 < min || min==0)){min = pri6;}
			
			//String abmultres = am.get("feature:ab_mult_res").toString();
			//String caseQuality = am.get("outcome:case_quality").toString();
			//String caseExpectedQuality = am.get("outcome:expected_quality").toString();
		
			Integer act_dur = 0;
			Integer wait_dur = 0;
			Integer act_rep = 0;
			Integer atyp_act = 0;
			Integer mult_res = 0;
			Integer subproc = 0;
			
			if (multres.equals("true")) {mult_res = 1;}
			
						 
			for (XEvent e : t) {
				
				String actdur = e.getAttributes().get("feature:act_duration").toString();
				String waitdur = e.getAttributes().get("feature:wait_duration").toString();
				String multact = e.getAttributes().get("feature:act_repetition").toString();
				String oddact = e.getAttributes().get("feature:odd_activity").toString();
				String f_subprocDuration = e.getAttributes().get("feature:subproc_duration").toString();
				
					//String eventName = XConceptExtension.instance().extractName(e);
					//String lifecycle = XLifecycleExtension.instance().extractTransition(e);
					//String abactdur = e.getAttributes().get("feature:ab_duration").toString();
					//String abwaitdur = e.getAttributes().get("feature:ab_wait_duration").toString();
					//String repdur = e.getAttributes().get("feature:rep_duration").toString();
					//String oddactdur = e.getAttributes().get("feature:odd_activity_duration").toString();
					//String riskyslot = e.getAttributes().get("feature:risky_timeslot").toString();
					//String inexpres = e.getAttributes().get("feature:inexperienced_resource").toString();
					//String inexpresnumexec = e.getAttributes().get("feature:ab_num_exec").toString();
				
				if (actdur.equals("true")) {act_dur = 1;}
				if (waitdur.equals("true")) { wait_dur = 1;}
				if (multact.equals("true")) {act_rep = 1;}
				if (oddact.equals("true")) {atyp_act = 1;}
				if (f_subprocDuration.equals("true")) {subproc = 1;}
						
					
			}
			jtable[i][0] = caseID;
			jtable[i][1] = caseDuration;
			jtable[i][2] = SLA;
			jtable[i][3] = caseSLAdur;
			jtable[i][4] = caseabdur;
			jtable[i][5] = act_dur.toString();
			jtable[i][6] = wait_dur.toString();
			jtable[i][7] = act_rep.toString();
			jtable[i][8] = atyp_act.toString();
			jtable[i][9] = mult_res.toString();
			jtable[i][10] = expectedOutcome;
			jtable[i][11] = expectedAbDur;
			jtable[i][12] = PRI1;
			jtable[i][13] = PRI2;
			jtable[i][14] = PRI3;
			jtable[i][15] = PRI4;
			jtable[i][16] = PRI5;
			jtable[i][17] = min.toString();
			jtable[i][18] = PRI6;
			jtable[i][19] = subproc.toString();
			i++;
			
		} 		
	          
			
		return jtable;

	}

*///---------------------------------------LOG OUTPUT---------------------------------------------------------

/*

public String[][] caseRisks(XLog log) {
		
	Integer num_traces = log.size();
	String[][] jtable = new String[num_traces][12];
		int i = 0;
		
				
		for (XTrace t : log) {
			
		
			XAttributeMap am = t.getAttributes();
			String caseID = am.get("concept:name").toString();
			String caseDuration = am.get("time:duration").toString();
			String SLA = am.get("time:SLA").toString();
			String caseSLAdur = am.get("outcome:case_duration").toString();
			String caseabdur = am.get("outcome:SLA_violation").toString();
			String expectedOutcome = am.get("outcome:expected_outcome").toString();
			String expectedAbDur = am.get("outcome:expected_ab_duration").toString();
			//String multres = am.get("feature:multiple_resources").toString();
			String abmultres = am.get("feature:ab_mult_res").toString();
			//String caseQuality = am.get("outcome:case_quality").toString();
			//String caseExpectedQuality = am.get("outcome:expected_quality").toString();
		
			Integer act_dur = 0;
			Integer wait_dur = 0;
			Integer act_rep = 0;
			Integer atyp_act = 0;
						 
			for (XEvent e : t) {
				
				String actdur = e.getAttributes().get("feature:act_duration").toString();
				String waitdur = e.getAttributes().get("feature:wait_duration").toString();
				String multact = e.getAttributes().get("feature:act_repetition").toString();
				String oddact = e.getAttributes().get("feature:odd_activity").toString();
				
					//String eventName = XConceptExtension.instance().extractName(e);
					//String lifecycle = XLifecycleExtension.instance().extractTransition(e);
					//String abactdur = e.getAttributes().get("feature:ab_duration").toString();
					//String abwaitdur = e.getAttributes().get("feature:ab_wait_duration").toString();
					//String repdur = e.getAttributes().get("feature:rep_duration").toString();
					//String oddactdur = e.getAttributes().get("feature:odd_activity_duration").toString();
					//String riskyslot = e.getAttributes().get("feature:risky_timeslot").toString();
					//String inexpres = e.getAttributes().get("feature:inexperienced_resource").toString();
					//String inexpresnumexec = e.getAttributes().get("feature:ab_num_exec").toString();
				
				if (actdur.equals("true")) {act_dur++;}
				if (waitdur.equals("true")) { wait_dur++;}
				if (multact.equals("true")) {act_rep++;}
				if (oddact.equals("true")) {atyp_act++;}
						
					
			}
			jtable[i][0] = caseID;
			jtable[i][1] = caseDuration;
			jtable[i][2] = SLA;
			jtable[i][3] = caseSLAdur;
			jtable[i][4] = caseabdur;
			jtable[i][5] = act_dur.toString();
			jtable[i][6] = wait_dur.toString();
			jtable[i][7] = act_rep.toString();
			jtable[i][8] = atyp_act.toString();
			jtable[i][9] = abmultres;
			jtable[i][10] = expectedOutcome;
			jtable[i][11] = expectedAbDur;
			i++;
			
		} 		
	          
			
		return jtable;

	}
*/
// -------------------------------------BOOLEAN TABLE------------------------------------------------
/*
public String[][] getBoolean(XLog log) {
	
	String[][] jtable = new String[100000][3];
	
	int i=0;
	
for (XTrace t : log) {
	
	XAttributeMap am = t.getAttributes();
			
	String realCaseOutcome = am.get("outcome:case_duration").toString();
	Integer realAbDur = 0;
	if(realCaseOutcome.equals("true")){realAbDur=1;}
	
	String expectedCaseDur = am.get("outcome:expected_outcome").toString();
	Integer expectedDur = 0;
	if(expectedCaseDur.equals("true")){expectedDur=1;}
	
	Integer diff = Math.abs(realAbDur-expectedDur);
	
	jtable[i][0] = realAbDur.toString();
	jtable[i][1] = expectedDur.toString();
	jtable[i][2] = diff.toString();
	i++;
} 		


return jtable;


}

*/

//------------------------------ABNORMAL DURATION TABLE------------------------------------------------------
/*
public String[][] getAbDuration(XLog log) {
	
	String[][] jtable = new String[100000][5];
				
		int i=0;
		
	for (XTrace t : log) {
		
		XAttributeMap am = t.getAttributes();
				
		XAttributeContinuous realCaseAbDur = (XAttributeContinuous)am.get("outcome:SLA_violation");
		Double realAbDur = realCaseAbDur.getValue();
		
		XAttributeContinuous expectedCaseAbDur = (XAttributeContinuous)am.get("outcome:expected_ab_duration");
		Double expectedAbDur = expectedCaseAbDur.getValue();
		
		XAttributeContinuous aSLA = (XAttributeContinuous)am.get("time:SLA");
		Double SLA = aSLA.getValue();
	
		Integer realRisk = 0;
		Integer expectedRisk = 0;
		
		if (realAbDur > 0){
		if (realAbDur < 0.1*SLA) realRisk = 1;
		else if (realAbDur >= 0.1*SLA && realAbDur < 0.5*SLA) realRisk = 2;
		else if (realAbDur >= 0.5*SLA && realAbDur < SLA) realRisk = 3;
		else if (realAbDur >= SLA) realRisk = 4;
		}
		
		if (expectedAbDur > 0){
			if (expectedAbDur < 0.1*SLA) expectedRisk = 1;
			else if (expectedAbDur >= 0.1*SLA && expectedAbDur < 0.5*SLA) expectedRisk = 2;
			else if (expectedAbDur >= 0.5*SLA && expectedAbDur < SLA) expectedRisk = 3;
			else if (expectedAbDur >= SLA) expectedRisk = 4;
			}
		
		Double diff = Math.abs(realAbDur-expectedAbDur);
		
		jtable[i][0] = realAbDur.toString();
		jtable[i][1] = expectedAbDur.toString();
		jtable[i][2] = diff.toString();
		jtable[i][3] = realRisk.toString();
		jtable[i][4] = expectedRisk.toString();
		i++;
	} 		
	
	
	return jtable;

}

*///----------------------------------PREDICTION RESULTS--------------------------------------------

/*
public String[][] getResult(XLog log) {
	
	String[][] jtable = new String[1][9];
		
		Integer num_traces = log.size();
		Integer correctlypredicted = 0;
		Integer falselypredicted = 0;
		Integer notpredicted = 0;
		Integer correctnotpredicted = 0;
		Double totalRisk = 0.00;
			
	for (XTrace t : log) {
		
	
		XAttributeMap am = t.getAttributes();
		
		String multres = am.get("feature:multiple_resources").toString();	
		
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
		
		
		//-------------------Rifened risks predictions-------------------
		XAttributeContinuous realCaseAbDur = (XAttributeContinuous)am.get("outcome:SLA_violation");
		Double realAbDur = realCaseAbDur.getValue();
		
		XAttributeContinuous expectedCaseAbDur = (XAttributeContinuous)am.get("outcome:expected_ab_duration");
		Double expectedAbDur = expectedCaseAbDur.getValue();
		
		XAttributeContinuous aSLA = (XAttributeContinuous)am.get("time:SLA");
		Double SLA = aSLA.getValue();
	
		Integer realRisk = 0;
		Integer expectedRisk = 0;
		
		if (realAbDur > 0){
		if (realAbDur < 0.1*SLA) realRisk = 1;
		else if (realAbDur >= 0.1*SLA && realAbDur < 0.5*SLA) realRisk = 2;
		else if (realAbDur >= 0.5*SLA && realAbDur < SLA) realRisk = 3;
		else if (realAbDur >= SLA) realRisk = 4;
		}
		
		if (expectedAbDur > 0){
			if (expectedAbDur < 0.1*SLA) expectedRisk = 1;
			else if (expectedAbDur >= 0.1*SLA && expectedAbDur < 0.5*SLA) expectedRisk = 2;
			else if (expectedAbDur >= 0.5*SLA && expectedAbDur < SLA) expectedRisk = 3;
			else if (expectedAbDur >= SLA) expectedRisk = 4;
			}
		//----------------------increasing expected risk if multiple resources is true---------------------------
		if (multres.equals("true") && expectedRisk!=4){expectedRisk+=1;}
		//-------------------------------------------------------------------------------------------------------
		int riskDiff = Math.abs(realRisk-expectedRisk);
		Double normRisk = (double)riskDiff/4;
		totalRisk=totalRisk + normRisk;
		//---------------------------------------------------------------
			
	} 	
	Double partfalsepred = (double)falselypredicted/(falselypredicted+correctnotpredicted);
	Double partnotpred = (double)notpredicted/(notpredicted+correctlypredicted);
	Double falseandnot = (double)falselypredicted+notpredicted;
	Double wrongpart = falseandnot/num_traces;
	Double wrongpartRefined = totalRisk/num_traces;
		
	jtable[0][0] = correctlypredicted.toString();
	jtable[0][1] = falselypredicted.toString();
	jtable[0][2] = notpredicted.toString();
	jtable[0][3] = partfalsepred.toString();
	jtable[0][4] = partnotpred.toString();
	jtable[0][5] = falseandnot.toString();
	jtable[0][6] = wrongpart.toString();
	jtable[0][7] = num_traces.toString();
	jtable[0][8] = wrongpartRefined.toString();
		
	return jtable;

}


*/

/*
public JLabel getResultasLabel(XLog log) {
	
	String labelText = "";
	
	String[][] jtable = new String[1][9];
		
		Integer num_traces = log.size();
		Integer correctlypredicted = 0;
		Integer falselypredicted = 0;
		Integer notpredicted = 0;
		Integer correctnotpredicted = 0;
		Double totalRisk = 0.00;
			
	for (XTrace t : log) {
		
	
		XAttributeMap am = t.getAttributes();
		
		String multres = am.get("feature:multiple_resources").toString();	
		
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
		
		
		//-------------------Rifened risks predictions-------------------
		XAttributeContinuous realCaseAbDur = (XAttributeContinuous)am.get("outcome:SLA_violation");
		Double realAbDur = realCaseAbDur.getValue();
		
		XAttributeContinuous expectedCaseAbDur = (XAttributeContinuous)am.get("outcome:expected_ab_duration");
		Double expectedAbDur = expectedCaseAbDur.getValue();
		
		XAttributeContinuous aSLA = (XAttributeContinuous)am.get("time:SLA");
		Double SLA = aSLA.getValue();
	
		Integer realRisk = 0;
		Integer expectedRisk = 0;
		
		if (realAbDur > 0){
		if (realAbDur < 0.1*SLA) realRisk = 1;
		else if (realAbDur >= 0.1*SLA && realAbDur < 0.5*SLA) realRisk = 2;
		else if (realAbDur >= 0.5*SLA && realAbDur < SLA) realRisk = 3;
		else if (realAbDur >= SLA) realRisk = 4;
		}
		
		if (expectedAbDur > 0){
			if (expectedAbDur < 0.1*SLA) expectedRisk = 1;
			else if (expectedAbDur >= 0.1*SLA && expectedAbDur < 0.5*SLA) expectedRisk = 2;
			else if (expectedAbDur >= 0.5*SLA && expectedAbDur < SLA) expectedRisk = 3;
			else if (expectedAbDur >= SLA) expectedRisk = 4;
			}
		//----------------------increasing expected risk if multiple resources is true---------------------------
		if (multres.equals("true") && expectedRisk!=4){expectedRisk+=1;}
		//-------------------------------------------------------------------------------------------------------
		int riskDiff = Math.abs(realRisk-expectedRisk);
		Double normRisk = (double)riskDiff/4;
		totalRisk=totalRisk + normRisk;
		//---------------------------------------------------------------
			
	} 	
	Double partfalsepred = (double)falselypredicted/(falselypredicted+correctnotpredicted);
	Double partnotpred = (double)notpredicted/(notpredicted+correctlypredicted);
	Double falseandnot = (double)falselypredicted+notpredicted;
	Double wrongpart = falseandnot/num_traces;
	Double wrongpartRefined = totalRisk/num_traces;
		
	jtable[0][0] = correctlypredicted.toString();
	jtable[0][1] = falselypredicted.toString();
	jtable[0][2] = notpredicted.toString();
	jtable[0][3] = partfalsepred.toString();
	jtable[0][4] = partnotpred.toString();
	jtable[0][5] = falseandnot.toString();
	jtable[0][6] = wrongpart.toString();
	jtable[0][7] = num_traces.toString();
	jtable[0][8] = wrongpartRefined.toString();
	
	labelText = "<html> TP: "+correctlypredicted.toString() +"<br/> FP: "+ falselypredicted.toString()+"<br/> Not Predicted: " + notpredicted.toString()+"<br/> Number of Traces: " + num_traces.toString();
		
	return new JLabel(labelText);

}

*/


//---------------------------------PROCESS TIME RESULTS----------------------------------------------------
/*// 
public XLog processTimeOld(XLog log) {
	
	XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
	
	for (XTrace t : log) {
		
		XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
		double expectedAbDur = 0.00;
		boolean expectedOutcome = false;
		
		XAttributeMap am = t.getAttributes();
		String multres = am.get("feature:multiple_resources").toString();			 
		//long totalAbRepDuration = 0;
		
		for (XEvent e : t) {
			
			XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
								
			
				String actdur = e.getAttributes().get("feature:act_duration").toString();
				String waitdur = e.getAttributes().get("feature:wait_duration").toString();
				String multact = e.getAttributes().get("feature:act_repetition").toString();
				String oddact = e.getAttributes().get("feature:odd_activity").toString();
				String subprocdur = e.getAttributes().get("feature:subproc_duration").toString();
								
				//checking for double feature: activity repetition and odd activity
				
				if (multact.equals("true") && oddact.equals("true"))
				{
					
					event.getAttributes().put("feature:act_repetition",new XAttributeBooleanImpl("feature:act_repetition",false));
					event.getAttributes().put("feature:rep_duration",new XAttributeContinuousImpl("feature:rep_duration",0.00));
									
				}
				
							
				XAttributeContinuous odd = (XAttributeContinuous)e.getAttributes().get("feature:odd_activity_duration");
				double oddDur = odd.getValue();
				XAttributeContinuous cycle = (XAttributeContinuous)e.getAttributes().get("feature:rep_duration");
				double cycleDur = cycle.getValue();
				XAttributeContinuous act = (XAttributeContinuous)e.getAttributes().get("feature:ab_duration");
				double actDur = act.getValue();
				XAttributeContinuous wait = (XAttributeContinuous)e.getAttributes().get("feature:ab_wait_duration");
				double waitDur = wait.getValue();
				
				//XAttributeDiscrete subprocDuration = (XAttributeDiscrete)e.getAttributes().get("subprocess:duration");
				//long subprocDur = subprocDuration.getValue();
				
				expectedAbDur+=oddDur+cycleDur+actDur+waitDur;
				if (multact.equals("true") || subprocdur.equals("true") || oddact.equals("true") || actdur.equals("true") || waitdur.equals("true"))
				{
					expectedOutcome = true;
				};
				
				//if (multact.equals("true")){totalAbRepDuration+=subprocDur;}
				
				trace.add(event);
				
				
		}
		
		if (multres.equals("true"))
		{
			expectedOutcome = true;
		}
		
		//long dur_threshold = 36000000;
		
		//if (totalAbRepDuration > dur_threshold)
		//{		expectedOutcome = true;		}
		
		//if ((totalAbRepDuration > 0) && (totalAbRepDuration <= dur_threshold))
			
		//{	trace.getAttributes().put("time:PRI3",new XAttributeDiscreteImpl("time:PRI3",0)); 	}
		
		trace.getAttributes().put("outcome:expected_outcome",new XAttributeBooleanImpl("outcome:expected_outcome",expectedOutcome));
		trace.getAttributes().put("outcome:expected_ab_duration",new XAttributeContinuousImpl("outcome:expected_ab_duration",expectedAbDur));
		copylog.add(trace);
	} 
	
	return copylog;

}

*///

/*

// alternative
public XLog processTime(XLog log) {
	
	XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
	
	for (XTrace t : log) {
		
		XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
		double expectedAbDur = 0.00;
		boolean expectedOutcome = false;
		double times = 2.0;
		
		//XAttributeMap am = t.getAttributes();
		Double multres = Double.parseDouble(t.getAttributes().get("PRI5:deviation").toString());			 
		//long totalAbRepDuration = 0;
		
		for (XEvent e : t) {
			
			XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
					
			
			
				Double actdur = Double.parseDouble(e.getAttributes().get("PRI1:deviation").toString());
				Double waitdur = Double.parseDouble(e.getAttributes().get("PRI2:deviation").toString());
				Double multact = Double.parseDouble(e.getAttributes().get("PRI3:deviation").toString());
				Double oddact = Double.parseDouble(e.getAttributes().get("PRI4:times").toString());
				Double subprocdur = Double.parseDouble(e.getAttributes().get("PRI6:deviation").toString());
				
				
				if(actdur > times || waitdur > times || multact > times || oddact > times || subprocdur > times)
				expectedOutcome = true;
				
				
				
				trace.add(event);
		
								
				//checking for double feature: activity repetition and odd activity
				
				if (multact.equals("true") && oddact.equals("true"))
				{
					
					event.getAttributes().put("feature:act_repetition",new XAttributeBooleanImpl("feature:act_repetition",false));
					event.getAttributes().put("feature:rep_duration",new XAttributeContinuousImpl("feature:rep_duration",0.00));
									
				}
			 		
							
				XAttributeContinuous odd = (XAttributeContinuous)e.getAttributes().get("feature:odd_activity_duration");
				double oddDur = odd.getValue();
				XAttributeContinuous cycle = (XAttributeContinuous)e.getAttributes().get("feature:rep_duration");
				double cycleDur = cycle.getValue();
				XAttributeContinuous act = (XAttributeContinuous)e.getAttributes().get("feature:ab_duration");
				double actDur = act.getValue();
				XAttributeContinuous wait = (XAttributeContinuous)e.getAttributes().get("feature:ab_wait_duration");
				double waitDur = wait.getValue();
		 			
				//XAttributeDiscrete subprocDuration = (XAttributeDiscrete)e.getAttributes().get("subprocess:duration");
				//long subprocDur = subprocDuration.getValue();
				
				expectedAbDur+=oddDur+cycleDur+actDur+waitDur;
				if (multact.equals("true") || subprocdur.equals("true") || oddact.equals("true") || actdur.equals("true") || waitdur.equals("true"))
				{
					expectedOutcome = true;
				};
				
				//if (multact.equals("true")){totalAbRepDuration+=subprocDur;}
				
				
				
				
				
		}
		
		if(multres > times)
			expectedOutcome = true;
		
		
		if (multres.equals("true"))
		{
			expectedOutcome = true;
		}
		
		//long dur_threshold = 36000000;
		
		//if (totalAbRepDuration > dur_threshold)
		//{		expectedOutcome = true;		}
		
		//if ((totalAbRepDuration > 0) && (totalAbRepDuration <= dur_threshold))
			
		//{	trace.getAttributes().put("time:PRI3",new XAttributeDiscreteImpl("time:PRI3",0)); 	}
		
		trace.getAttributes().put("outcome:expected_outcome",new XAttributeBooleanImpl("outcome:expected_outcome",expectedOutcome));
		trace.getAttributes().put("outcome:expected_ab_duration",new XAttributeContinuousImpl("outcome:expected_ab_duration",expectedAbDur));
		copylog.add(trace);
	} 
	
	return copylog;

}


 */

/*
//---------------------------------PROCESS QUALITY RESULTS------------------------------------------------
	
	public XLog processQuality(XLog log) {
		
		XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
		
		for (XTrace t : log) {
			boolean expectedOutcome = false;
			XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
			XAttributeMap am = t.getAttributes();
			String multres = am.get("feature:multiple_resources").toString();			 
			for (XEvent e : t) {
				
				XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
									
					String riskytime = e.getAttributes().get("feature:risky_timeslot").toString();
					String inexpRes = e.getAttributes().get("feature:inexperienced_resource").toString();
					
					
					if (riskytime.equals("true") || inexpRes.equals("true"))
					{
						expectedOutcome = true;
					};
					
					
					trace.add(event);
			
			}
			
			if (multres.equals("true"))
			{
				expectedOutcome = true;
			}
			
			trace.getAttributes().put("outcome:expected_quality",new XAttributeBooleanImpl("outcome:expected_quality",expectedOutcome));
			copylog.add(trace);
		} 
		
	return copylog;

}
*/
//-----------------------------------USER INPUT------------------------------------------------------------
/*	
public String getParam(UIPluginContext context, int number) {
				String p = JOptionPane.showInputDialog(null, "Cluster number: "+number,
				"Cluster number: "+number, JOptionPane.QUESTION_MESSAGE);
				return p;
	}
*/
/*public String getParam(UIPluginContext context) {
	String p = JOptionPane.showInputDialog(null, "Cluster number",
	"Cluster number: ", JOptionPane.QUESTION_MESSAGE);
	return p;
}
*/
