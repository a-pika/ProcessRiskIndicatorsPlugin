package org.processmining.processriskindicators.analysis;

import java.util.Date;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeBooleanImpl;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;


public class Basic {

// getting complete index of an event in a trace, 0 if it is the first complete event in a trace
		
public int CompleteIndex(XTrace t, XEvent e){
			int index = 0;
			
			Date eTime = XTimeExtension.instance().extractTimestamp(e);
			
			for (XEvent e1 : t) {
				
				String lifecycle = XLifecycleExtension.instance().extractTransition(e1);
				Date eventTime = XTimeExtension.instance().extractTimestamp(e1);
				if (lifecycle.equalsIgnoreCase("complete") && (eventTime.compareTo(eTime)<0))
				{index++;};			
			}
			
			return index;
		}

// returns previous 'complete' event
		
public XEvent PrevComplete(XTrace t, XEvent e){
				
				int current_index = CompleteIndex(t,e);

				XEvent return_event = e;
					for (XEvent e1 : t) 
					{
					
					String lifecycle = XLifecycleExtension.instance().extractTransition(e1);
					
					if (lifecycle.equalsIgnoreCase("complete") && CompleteIndex(t,e1)==(current_index-1))
					return_event = e1;			
					
					
				}
						
				return return_event;
	}	
	
public long SubprocessDuration(XTrace t, XEvent e){
		
		Date eventTime = XTimeExtension.instance().extractTimestamp(e);
		Date matchEventTime = XTimeExtension.instance().extractTimestamp(PrevComplete(t,e));
		long duration = eventTime.getTime()-matchEventTime.getTime();
		long dur = duration;
			
		return dur;
	}
	
// getting index of an event in a trace, 0 if it occurs only once	
	
public int Index(XTrace t, XEvent e){
		int index = 0;
		String eName = XConceptExtension.instance().extractName(e);
		String eLifecycle = XLifecycleExtension.instance().extractTransition(e);
		Date eTime = XTimeExtension.instance().extractTimestamp(e);
		for (XEvent e1 : t) {
			String eventName = XConceptExtension.instance().extractName(e1);
			String lifecycle = XLifecycleExtension.instance().extractTransition(e1);
			Date eventTime = XTimeExtension.instance().extractTimestamp(e1);
			if (eName.equals(eventName) && eLifecycle.equals(lifecycle) && (eventTime.compareTo(eTime)<0))
			{index++;};			
		}
		
		return index;
	}

//MATCH START-COMPLETE-----------------------------------------

public XEvent Match(XTrace t, XEvent e){
		
		String eName = XConceptExtension.instance().extractName(e);
		String eLifecycle = XLifecycleExtension.instance().extractTransition(e);
		XEvent return_event = e;
		if (eLifecycle.equalsIgnoreCase("complete"))
		{
			for (XEvent e1 : t) 
			{
			String eventName = XConceptExtension.instance().extractName(e1);
			String lifecycle = XLifecycleExtension.instance().extractTransition(e1);
			
			if (eName.equals(eventName) && lifecycle.equalsIgnoreCase("start") && Index(t,e1)==Index(t,e))
			return_event = e1;			
			
			}
		}
		else if (eLifecycle.equalsIgnoreCase("start"))
		
		{
			for (XEvent e1 : t) 
			{
			String eventName = XConceptExtension.instance().extractName(e1);
			String lifecycle = XLifecycleExtension.instance().extractTransition(e1);
			
			if (eName.equals(eventName) && lifecycle.equalsIgnoreCase("complete") && Index(t,e1)==Index(t,e))
			return_event = e1;	
			}
		}
			
		return return_event;
	}

// returns previous event
	
public XEvent Prev(XTrace t, XEvent e){
			
			XEvent return_event = t.get(t.indexOf(e)-1);
					
			return return_event;
}

// returns previous complete event
		
public XEvent PrevPRI2(XTrace t, XEvent e){
				
				Date eTime = XTimeExtension.instance().extractTimestamp(e);
				
				XEvent return_event = e;
					for (XEvent e1 : t) 
					{
					//---------
							String lifecycle = XLifecycleExtension.instance().extractTransition(e1);
							Date eventTime = XTimeExtension.instance().extractTimestamp(e1);
					//---------
					
					if (lifecycle.equalsIgnoreCase("complete") && eventTime.compareTo(eTime)<0)
					return_event = e1;			
					
				}
						
				return return_event;
	}	

public long ActivityStartDuration(XTrace t, XEvent e){
	
	Date eventTime = XTimeExtension.instance().extractTimestamp(e);
	Date matchEventTime = XTimeExtension.instance().extractTimestamp(Match(t,e));
	long duration = eventTime.getTime()-matchEventTime.getTime();
	long dur = duration;
		
	return dur;
}

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
					trace.getAttributes().put("outcome:expected_ab_duration",new XAttributeContinuousImpl("outcome:expected_ab_duration",0.00));
					trace.getAttributes().put("time:SLA",new XAttributeContinuousImpl("time:SLA",SLAdur));
													
								}
			return log;
			
		};
	
}

//---------------------------------------------------PREV. VERSIONS----------------------------------------------------------
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//--------------------------------ADD CASE QUALITY OUTCOME------------------------------------------------------ 
/*		
	public XLog addQualityOutcome (XLog log, ConcurrentSkipListSet<String> caseIDs){
			
			
			for (XTrace t : log) {
			XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
			XAttributeMap am = t.getAttributes();
			String caseID = am.get("concept:name").toString();
			
			if(caseIDs.contains(caseID))
				{
					trace.getAttributes().put("outcome:case_quality",new XAttributeBooleanImpl("outcome:case_quality",true));
				}else
				{
					trace.getAttributes().put("outcome:case_quality",new XAttributeBooleanImpl("outcome:case_quality",false));
				};
					
				trace.getAttributes().put("outcome:expected_quality",new XAttributeBooleanImpl("outcome:expected_quality",false));
				}
		return log;
		
	};
	*/
/*//----------------------------------------ACTIVITY DURATION PREVIOUS---------------------------------------
public long ActivityPrevDuration(XTrace t, XEvent e){
	
	Date eventTime = XTimeExtension.instance().extractTimestamp(e);
	Date matchEventTime = XTimeExtension.instance().extractTimestamp(Prev(t,e));
	long duration = eventTime.getTime()-matchEventTime.getTime();
	long dur = duration;
	
	return dur;
}*/
//-------------------------------------------ADD CASE TIME OUTCOME-----------------------------------------	

