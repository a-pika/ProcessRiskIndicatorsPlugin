package org.processmining.processriskindicators.analysis;

import java.util.Date;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentSkipListSet;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeBooleanImpl;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.processriskindicators.inout.InOut;
import org.processmining.processriskindicators.inout.InputParameters;
import org.processmining.processriskindicators.pri.AbnormalActivityDuration;
import org.processmining.processriskindicators.pri.AbnormalSubprocessDuration;
import org.processmining.processriskindicators.pri.AbnormalWaitDuration;
import org.processmining.processriskindicators.pri.AtypicalActivity;
import org.processmining.processriskindicators.pri.MultipleActivityRepetitions;
import org.processmining.processriskindicators.pri.MultipleResourcesPerCase;
import org.processmining.processriskindicators.threshold.PriActivityThreshold;
import org.processmining.processriskindicators.threshold.PriCaseThreshold;
import org.processmining.processriskindicators.threshold.PriResourceActivityThreshold;
import org.processmining.processriskindicators.threshold.PriResourceThreshold;

public class Features {

//to do - check	
public XLog  getRunRisks(UIPluginContext context, XLog inputlog, Date startTimeslot, Date endTimeslot, Double dist, Double allstDev, Double stDev, InputParameters ip) {
			
	InOut inout = new InOut();
	Basic basic = new Basic();
	Stat stat = new Stat();
		
			XLog log = XFactoryRegistry.instance().currentDefault().createLog(inputlog.getAttributes());
		
			Vector <String> task_names = new Vector<String>();
			
			for (XTrace t : inputlog) {
				XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
				log.add(trace);
							 
			for (XEvent e : t) {
					XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
					
					String eventName = XConceptExtension.instance().extractName(e);
					String lifecycle = XLifecycleExtension.instance().extractTransition(e);
					
					event.getAttributes().put("feature:act_duration",new XAttributeBooleanImpl("feature:act_duration",false));
					event.getAttributes().put("feature:ab_duration",new XAttributeContinuousImpl("feature:ab_duration",0.00));
					event.getAttributes().put("feature:wait_duration",new XAttributeBooleanImpl("feature:wait_duration",false));
					event.getAttributes().put("feature:ab_wait_duration",new XAttributeContinuousImpl("feature:ab_wait_duration",0.00));
					event.getAttributes().put("feature:odd_activity",new XAttributeBooleanImpl("feature:odd_activity",false));
					event.getAttributes().put("feature:odd_activity_duration",new XAttributeContinuousImpl("feature:odd_activity_duration",0.00));
					event.getAttributes().put("feature:act_repetition",new XAttributeBooleanImpl("feature:act_repetition",false));
					event.getAttributes().put("feature:rep_duration",new XAttributeContinuousImpl("feature:rep_duration",0.00));
					event.getAttributes().put("feature:risky_timeslot",new XAttributeBooleanImpl("feature:risky_timeslot",false));
					event.getAttributes().put("feature:inexperienced_resource",new XAttributeBooleanImpl("feature:inexperienced_resource",false));
					event.getAttributes().put("feature:ab_num_exec",new XAttributeContinuousImpl("feature:ab_num_exec",0.00));
					event.getAttributes().put("time:duration",new XAttributeDiscreteImpl("time:duration",0));
					event.getAttributes().put("subprocess:duration",new XAttributeDiscreteImpl("subprocess:duration",0));
					event.getAttributes().put("feature:subproc_duration",new XAttributeBooleanImpl("feature:subproc_duration",false));
					event.getAttributes().put("feature:subproc_ab_duration",new XAttributeContinuousImpl("feature:subproc_ab_duration",0.00));
					
		
		event.getAttributes().put("PRI1:average",new XAttributeContinuousImpl("PRI1:average",0.00));
		event.getAttributes().put("PRI1:stDev",new XAttributeContinuousImpl("PRI1:stDev",0.00));
		event.getAttributes().put("PRI1:deviation",new XAttributeContinuousImpl("PRI1:deviation",0.00));
								
		event.getAttributes().put("PRI2:average",new XAttributeContinuousImpl("PRI2:average",0.00));
		event.getAttributes().put("PRI2:stDev",new XAttributeContinuousImpl("PRI2:stDev",0.00));
		event.getAttributes().put("PRI2:deviation",new XAttributeContinuousImpl("PRI2:deviation",0.00));
		
		event.getAttributes().put("PRI3:average",new XAttributeContinuousImpl("PRI3:average",0.00));
		event.getAttributes().put("PRI3:stDev",new XAttributeContinuousImpl("PRI3:stDev",0.00));
		event.getAttributes().put("PRI3:deviation",new XAttributeContinuousImpl("PRI3:deviation",0.00));
		
		event.getAttributes().put("PRI6:average",new XAttributeContinuousImpl("PRI6:average",0.00));
		event.getAttributes().put("PRI6:stDev",new XAttributeContinuousImpl("PRI6:stDev",0.00));
		event.getAttributes().put("PRI6:deviation",new XAttributeContinuousImpl("PRI6:deviation",0.00));
		
		event.getAttributes().put("PRI4:part",new XAttributeContinuousImpl("PRI4:part",0.00));
		event.getAttributes().put("PRI4:times",new XAttributeContinuousImpl("PRI4:times",0.00));
				
		// adding activity durations
					
					if (lifecycle.equalsIgnoreCase("complete"))
					{
						event.getAttributes().put("time:duration",new XAttributeDiscreteImpl("time:duration",basic.ActivityStartDuration(t, e)));
						event.getAttributes().put("subprocess:duration",new XAttributeDiscreteImpl("subprocess:duration",basic.SubprocessDuration(t, e)));
					}
					
					
					trace.add(event);
					
					
		//getting array of activity names
					
					boolean flag = false;
		
					for(int z=0;z<task_names.size();z++){
						if(task_names.elementAt(z).equals(eventName)){flag = true;}
					};
					if (!flag){task_names.add(eventName);}
					}
			
		//adding case duration to log
			
			XEvent first = trace.get(0);
			XEvent last = trace.get(trace.size()-1);
			Date begin = XTimeExtension.instance().extractTimestamp(first);
			Date end = XTimeExtension.instance().extractTimestamp(last);
			long duration = end.getTime()-begin.getTime();
			trace.getAttributes().put("time:duration",new XAttributeDiscreteImpl("time:duration",duration));
			trace.getAttributes().put("feature:multiple_resources",new XAttributeBooleanImpl("feature:multiple_resources",false));
			trace.getAttributes().put("feature:ab_mult_res",new XAttributeContinuousImpl("feature:ab_mult_res",0));
			trace.getAttributes().put("time:SLA",new XAttributeContinuousImpl("time:SLA",0));
			trace.getAttributes().put("time:PRI1",new XAttributeDiscreteImpl("time:PRI1",0));
			trace.getAttributes().put("time:PRI2",new XAttributeDiscreteImpl("time:PRI2",0));
			trace.getAttributes().put("time:PRI3",new XAttributeDiscreteImpl("time:PRI3",0));
			trace.getAttributes().put("time:PRI4",new XAttributeDiscreteImpl("time:PRI4",0));
			trace.getAttributes().put("time:PRI5",new XAttributeDiscreteImpl("time:PRI5",0));
			trace.getAttributes().put("time:PRI6",new XAttributeDiscreteImpl("time:PRI6",0));
			trace.getAttributes().put("set:test",new XAttributeDiscreteImpl("set:test",0));
			
			trace.getAttributes().put("PRI5:average",new XAttributeContinuousImpl("PRI5:average",0));
			trace.getAttributes().put("PRI5:stDev",new XAttributeContinuousImpl("PRI5:stDev",0));
			trace.getAttributes().put("PRI5:deviation",new XAttributeContinuousImpl("PRI5:deviation",0));
			
			trace.getAttributes().put("PRI7:average",new XAttributeContinuousImpl("PRI7:average",0));
			trace.getAttributes().put("PRI7:stDev",new XAttributeContinuousImpl("PRI7:stDev",0));
			trace.getAttributes().put("PRI7:deviation",new XAttributeContinuousImpl("PRI7:deviation",0));
		
			}
	
//--------------------getting SLA------------------------------------------------------
			
//Double allSLA = stat.getMedianLogSLASplit(inputlog, dist, allstDev);
//Double allSLA = stat.getMedianLogSLA(inputlog, dist, allstDev);
//Double allSLA = stat.getMedianSLA(inputlog, dist, allstDev);
//Double allSLA = stat.getAverageSLA(inputlog, allstDev);
//Double allSLA = stat.getAverageLogSLA(inputlog, 2.00);

//Double allSLA = 2592000000.00;
//Double allSLA = 18000000.00;

Double allSLA = 0.0;

if(ip.calculateSLA)
	{allSLA = stat.getMedianLogSLA(inputlog, dist, allstDev);}
else
	{
		allSLA = ip.SLA;
	}	
			
context.log("SLA: "+allSLA);
					
			
	//getting number of deviations for time-based features
	//Double act_stdev_threshold = stat.getMedianStDevThreshold (log, 1.483, allSLA);
	//Double wait_stdev_threshold = stat.getMedianStDevThreshold (log, 1.483, allSLA);
	//Double odd_threshold = stat.getNumViolated (context, log, allSLA);
	//Double cycle_stdev_threshold = stat.getMedianStDevThreshold (log, 1.00, allSLA);
	
	log = basic.addTimeOutcome (log, allSLA);	
	
	// checking risk features
	
	XLog testLog = getTestLog25(log);
	XLog trainLog = getTrainLog75(log);
	//XLog testLog = getTestLog(log);
	//XLog trainLog = getTrainLog(log);
					
			for(int i=0; i<task_names.size(); i++)
			{
			//log = AbnormalSubprocessDuration.getRiskyLogMedianSplit (context, log, task_names.elementAt(i), dist, stDev);
			//log = AtypicalActivity.getRiskySplit (context, log, task_names.elementAt(i), 0.05);
			//log = AbnormalActivityDuration.getRiskyLogMedianSplit (context, log, task_names.elementAt(i), dist, stDev);
			//log = AbnormalWaitDuration.getRiskyLogMedianSplit (log, task_names.elementAt(i), dist, stDev);
			//testLog = AbnormalWaitDuration.getRiskyLogAverageTwoLogs (trainLog, testLog, task_names.elementAt(i), dist, stDev);
			//log = MultipleActivityRepetitions.getRiskyMedianSplit (context, log, task_names.elementAt(i), dist, stDev);
			//log = MultipleActivityRepetitions.getRiskyAverage (context, log, task_names.elementAt(i), dist, 1.00);
			//log = AbnormalActivityDuration.getRiskyLogAverage (context, log, task_names.elementAt(i), dist, 1.50);
			//log = AbnormalWaitDuration.getRiskyLogAverage (log, task_names.elementAt(i), dist, 2.00);
			//log = MultipleActivityRepetitions.getRiskyAverage (context, log, task_names.elementAt(i), dist, 1.50);
			//testLog = AtypicalActivity.getRisky (context, testLog, task_names.elementAt(i), 0.05);
			//log = AbnormalActivityDuration.getRiskyLogMedian (context, log, task_names.elementAt(i), dist, stDev);
			//testLog = AbnormalActivityDuration.getRiskyLogAverageTwoLogs (context, trainLog, testLog, task_names.elementAt(i), dist, stDev);
			//log = AbnormalWaitDuration.getRiskyLogMedian (log, task_names.elementAt(i), dist, stDev);
			//log = MultipleActivityRepetitions.getRiskyMedian (context, log, task_names.elementAt(i), dist, stDev);
			//log = RiskyTimeslots.getRisky(log, task_names.elementAt(i), startTimeslot, endTimeslot);
			//log = InexperiencedResource.getRiskyLeftMedian (log, task_names.elementAt(i), 1.483, 1.00);	
			
			
			testLog = AbnormalSubprocessDuration.getRiskyLogMedianTwoLogs (context, trainLog, testLog, task_names.elementAt(i), dist, stDev);
			//testLog = AtypicalActivity.getRiskyTwoLogs (context, trainLog, testLog, task_names.elementAt(i), 0.05);
			testLog = AtypicalActivity.getRiskyTwoLogs (context, trainLog, testLog, task_names.elementAt(i), ip.atypicalActivityThreshold);
			
			testLog = AbnormalActivityDuration.getRiskyLogMedianTwoLogs (context, trainLog, testLog, task_names.elementAt(i), dist, stDev);
			testLog = AbnormalWaitDuration.getRiskyLogMedianTwoLogs (trainLog, testLog, task_names.elementAt(i), dist, stDev);
			testLog = MultipleActivityRepetitions.getRiskyMedianTwoLogs (context, trainLog, testLog, task_names.elementAt(i), dist, stDev);
			
			
			/*
			testLog = AbnormalSubprocessDuration.Risky(context, trainLog, testLog, task_names.elementAt(i));
			testLog = AtypicalActivity.Risky (context, trainLog, testLog, task_names.elementAt(i), 1.00);
			testLog = AbnormalActivityDuration.Risky (context, trainLog, testLog, task_names.elementAt(i));
			testLog = AbnormalWaitDuration.Risky (trainLog, testLog, task_names.elementAt(i));
			testLog = MultipleActivityRepetitions.Risky (context, trainLog, testLog, task_names.elementAt(i));
			*/		
			}
			//log = MultipleResourcesPerCase.getRiskyMedian (log, task_names.elementAt(0), dist, stDev);
			//log = MultipleResourcesPerCase.getRiskyMedianSplit (log, dist, stDev);
			//log = MultipleResourcesPerCase.getRiskyAverage (log, task_names.elementAt(0), dist, stDev);
			
			testLog = MultipleResourcesPerCase.getRiskyMedianTwoLogs (trainLog, testLog, task_names.elementAt(0), dist, stDev);
			//testLog = MultipleResourcesPerCase.Risky (trainLog, testLog, task_names.elementAt(0));
						
			testLog = inout.processTimeNew(testLog);	
			//testLog = inout.processQuality(testLog);
			return testLog;
			
			//log = inout.processTime(log);	
			//log = inout.processQuality(log);
			//return log;		
								
			}	

public XLog  getRunRisksWithModel(UIPluginContext context, XLog inputlog, Date startTimeslot, Date endTimeslot, Double dist, Double allstDev, Double stDev, InputParameters ip, PetrinetGraph net) {
	
	InOut inout = new InOut();
	Basic basic = new Basic();
	Stat stat = new Stat();
		
			XLog log = XFactoryRegistry.instance().currentDefault().createLog(inputlog.getAttributes());
		
			Vector <String> task_names = new Vector<String>();
			
			for (XTrace t : inputlog) {
				XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
				log.add(trace);
							 
			for (XEvent e : t) {
					XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
					
					String eventName = XConceptExtension.instance().extractName(e);
					String lifecycle = XLifecycleExtension.instance().extractTransition(e);
					
					event.getAttributes().put("feature:act_duration",new XAttributeBooleanImpl("feature:act_duration",false));
					event.getAttributes().put("feature:ab_duration",new XAttributeContinuousImpl("feature:ab_duration",0.00));
					event.getAttributes().put("feature:wait_duration",new XAttributeBooleanImpl("feature:wait_duration",false));
					event.getAttributes().put("feature:ab_wait_duration",new XAttributeContinuousImpl("feature:ab_wait_duration",0.00));
					event.getAttributes().put("feature:odd_activity",new XAttributeBooleanImpl("feature:odd_activity",false));
					event.getAttributes().put("feature:odd_activity_duration",new XAttributeContinuousImpl("feature:odd_activity_duration",0.00));
					event.getAttributes().put("feature:act_repetition",new XAttributeBooleanImpl("feature:act_repetition",false));
					event.getAttributes().put("feature:rep_duration",new XAttributeContinuousImpl("feature:rep_duration",0.00));
					event.getAttributes().put("feature:risky_timeslot",new XAttributeBooleanImpl("feature:risky_timeslot",false));
					event.getAttributes().put("feature:inexperienced_resource",new XAttributeBooleanImpl("feature:inexperienced_resource",false));
					event.getAttributes().put("feature:ab_num_exec",new XAttributeContinuousImpl("feature:ab_num_exec",0.00));
					event.getAttributes().put("time:duration",new XAttributeDiscreteImpl("time:duration",0));
					event.getAttributes().put("subprocess:duration",new XAttributeDiscreteImpl("subprocess:duration",0));
					event.getAttributes().put("feature:subproc_duration",new XAttributeBooleanImpl("feature:subproc_duration",false));
					event.getAttributes().put("feature:subproc_ab_duration",new XAttributeContinuousImpl("feature:subproc_ab_duration",0.00));
					
		
		event.getAttributes().put("PRI1:average",new XAttributeContinuousImpl("PRI1:average",0.00));
		event.getAttributes().put("PRI1:stDev",new XAttributeContinuousImpl("PRI1:stDev",0.00));
		event.getAttributes().put("PRI1:deviation",new XAttributeContinuousImpl("PRI1:deviation",0.00));
								
		event.getAttributes().put("PRI2:average",new XAttributeContinuousImpl("PRI2:average",0.00));
		event.getAttributes().put("PRI2:stDev",new XAttributeContinuousImpl("PRI2:stDev",0.00));
		event.getAttributes().put("PRI2:deviation",new XAttributeContinuousImpl("PRI2:deviation",0.00));
		
		event.getAttributes().put("PRI3:average",new XAttributeContinuousImpl("PRI3:average",0.00));
		event.getAttributes().put("PRI3:stDev",new XAttributeContinuousImpl("PRI3:stDev",0.00));
		event.getAttributes().put("PRI3:deviation",new XAttributeContinuousImpl("PRI3:deviation",0.00));
		
		event.getAttributes().put("PRI6:average",new XAttributeContinuousImpl("PRI6:average",0.00));
		event.getAttributes().put("PRI6:stDev",new XAttributeContinuousImpl("PRI6:stDev",0.00));
		event.getAttributes().put("PRI6:deviation",new XAttributeContinuousImpl("PRI6:deviation",0.00));
		
		event.getAttributes().put("PRI4:part",new XAttributeContinuousImpl("PRI4:part",0.00));
		event.getAttributes().put("PRI4:times",new XAttributeContinuousImpl("PRI4:times",0.00));
				
		// adding activity durations
					
					if (lifecycle.equalsIgnoreCase("complete"))
					{
						event.getAttributes().put("time:duration",new XAttributeDiscreteImpl("time:duration",basic.ActivityStartDuration(t, e)));
						event.getAttributes().put("subprocess:duration",new XAttributeDiscreteImpl("subprocess:duration",basic.SubprocessDuration(t, e)));
					}
					
					
					trace.add(event);
					
					
		//getting array of activity names
					
					boolean flag = false;
		
					for(int z=0;z<task_names.size();z++){
						if(task_names.elementAt(z).equals(eventName)){flag = true;}
					};
					if (!flag){task_names.add(eventName);}
					}
			
		//adding case duration to log
			
			XEvent first = trace.get(0);
			XEvent last = trace.get(trace.size()-1);
			Date begin = XTimeExtension.instance().extractTimestamp(first);
			Date end = XTimeExtension.instance().extractTimestamp(last);
			long duration = end.getTime()-begin.getTime();
			trace.getAttributes().put("time:duration",new XAttributeDiscreteImpl("time:duration",duration));
			trace.getAttributes().put("feature:multiple_resources",new XAttributeBooleanImpl("feature:multiple_resources",false));
			trace.getAttributes().put("feature:ab_mult_res",new XAttributeContinuousImpl("feature:ab_mult_res",0));
			trace.getAttributes().put("time:SLA",new XAttributeContinuousImpl("time:SLA",0));
			trace.getAttributes().put("time:PRI1",new XAttributeDiscreteImpl("time:PRI1",0));
			trace.getAttributes().put("time:PRI2",new XAttributeDiscreteImpl("time:PRI2",0));
			trace.getAttributes().put("time:PRI3",new XAttributeDiscreteImpl("time:PRI3",0));
			trace.getAttributes().put("time:PRI4",new XAttributeDiscreteImpl("time:PRI4",0));
			trace.getAttributes().put("time:PRI5",new XAttributeDiscreteImpl("time:PRI5",0));
			trace.getAttributes().put("time:PRI6",new XAttributeDiscreteImpl("time:PRI6",0));
			trace.getAttributes().put("set:test",new XAttributeDiscreteImpl("set:test",0));
			
			trace.getAttributes().put("PRI5:average",new XAttributeContinuousImpl("PRI5:average",0));
			trace.getAttributes().put("PRI5:stDev",new XAttributeContinuousImpl("PRI5:stDev",0));
			trace.getAttributes().put("PRI5:deviation",new XAttributeContinuousImpl("PRI5:deviation",0));
			
			trace.getAttributes().put("PRI7:average",new XAttributeContinuousImpl("PRI7:average",0));
			trace.getAttributes().put("PRI7:stDev",new XAttributeContinuousImpl("PRI7:stDev",0));
			trace.getAttributes().put("PRI7:deviation",new XAttributeContinuousImpl("PRI7:deviation",0));
		
			}
	
//--------------------getting SLA------------------------------------------------------
			
//Double allSLA = stat.getMedianLogSLASplit(inputlog, dist, allstDev);
//Double allSLA = stat.getMedianLogSLA(inputlog, dist, allstDev);
//Double allSLA = stat.getMedianSLA(inputlog, dist, allstDev);
//Double allSLA = stat.getAverageSLA(inputlog, allstDev);
//Double allSLA = stat.getAverageLogSLA(inputlog, 2.00);

//Double allSLA = 2592000000.00;
//Double allSLA = 18000000.00;

Double allSLA = 0.0;

if(ip.calculateSLA)
	{allSLA = stat.getMedianLogSLA(inputlog, dist, allstDev);}
else
	{
		allSLA = ip.SLA;
	}	
			
context.log("SLA: "+allSLA);
					
			
	//getting number of deviations for time-based features
	//Double act_stdev_threshold = stat.getMedianStDevThreshold (log, 1.483, allSLA);
	//Double wait_stdev_threshold = stat.getMedianStDevThreshold (log, 1.483, allSLA);
	//Double odd_threshold = stat.getNumViolated (context, log, allSLA);
	//Double cycle_stdev_threshold = stat.getMedianStDevThreshold (log, 1.00, allSLA);
	
	log = basic.addTimeOutcome (log, allSLA);	
	
	// checking risk features
	
	XLog testLog = getTestLog25(log);
	XLog trainLog = getTrainLog75(log);
	//XLog testLog = getTestLog(log);
	//XLog trainLog = getTrainLog(log);
					
			for(int i=0; i<task_names.size(); i++)
			{
			//log = AbnormalSubprocessDuration.getRiskyLogMedianSplit (context, log, task_names.elementAt(i), dist, stDev);
			//log = AtypicalActivity.getRiskySplit (context, log, task_names.elementAt(i), 0.05);
			//log = AbnormalActivityDuration.getRiskyLogMedianSplit (context, log, task_names.elementAt(i), dist, stDev);
			//log = AbnormalWaitDuration.getRiskyLogMedianSplit (log, task_names.elementAt(i), dist, stDev);
			//testLog = AbnormalWaitDuration.getRiskyLogAverageTwoLogs (trainLog, testLog, task_names.elementAt(i), dist, stDev);
			//log = MultipleActivityRepetitions.getRiskyMedianSplit (context, log, task_names.elementAt(i), dist, stDev);
			//log = MultipleActivityRepetitions.getRiskyAverage (context, log, task_names.elementAt(i), dist, 1.00);
			//log = AbnormalActivityDuration.getRiskyLogAverage (context, log, task_names.elementAt(i), dist, 1.50);
			//log = AbnormalWaitDuration.getRiskyLogAverage (log, task_names.elementAt(i), dist, 2.00);
			//log = MultipleActivityRepetitions.getRiskyAverage (context, log, task_names.elementAt(i), dist, 1.50);
			//testLog = AtypicalActivity.getRisky (context, testLog, task_names.elementAt(i), 0.05);
			//log = AbnormalActivityDuration.getRiskyLogMedian (context, log, task_names.elementAt(i), dist, stDev);
			//testLog = AbnormalActivityDuration.getRiskyLogAverageTwoLogs (context, trainLog, testLog, task_names.elementAt(i), dist, stDev);
			//log = AbnormalWaitDuration.getRiskyLogMedian (log, task_names.elementAt(i), dist, stDev);
			//log = MultipleActivityRepetitions.getRiskyMedian (context, log, task_names.elementAt(i), dist, stDev);
			//log = RiskyTimeslots.getRisky(log, task_names.elementAt(i), startTimeslot, endTimeslot);
			//log = InexperiencedResource.getRiskyLeftMedian (log, task_names.elementAt(i), 1.483, 1.00);	
			
			
			testLog = AbnormalSubprocessDuration.getRiskyLogMedianTwoLogs (context, trainLog, testLog, task_names.elementAt(i), dist, stDev);
			//testLog = AtypicalActivity.getRiskyTwoLogs (context, trainLog, testLog, task_names.elementAt(i), 0.05);
			testLog = AtypicalActivity.getRiskyTwoLogs (context, trainLog, testLog, task_names.elementAt(i), ip.atypicalActivityThreshold);
			
			testLog = AbnormalActivityDuration.getRiskyLogMedianTwoLogs (context, trainLog, testLog, task_names.elementAt(i), dist, stDev);
			testLog = AbnormalWaitDuration.getRiskyLogMedianTwoLogsWithModel(trainLog, testLog, task_names.elementAt(i), dist, stDev, net);
			testLog = MultipleActivityRepetitions.getRiskyMedianTwoLogs (context, trainLog, testLog, task_names.elementAt(i), dist, stDev);
			
			
			/*
			testLog = AbnormalSubprocessDuration.Risky(context, trainLog, testLog, task_names.elementAt(i));
			testLog = AtypicalActivity.Risky (context, trainLog, testLog, task_names.elementAt(i), 1.00);
			testLog = AbnormalActivityDuration.Risky (context, trainLog, testLog, task_names.elementAt(i));
			testLog = AbnormalWaitDuration.Risky (trainLog, testLog, task_names.elementAt(i));
			testLog = MultipleActivityRepetitions.Risky (context, trainLog, testLog, task_names.elementAt(i));
			*/		
			}
			//log = MultipleResourcesPerCase.getRiskyMedian (log, task_names.elementAt(0), dist, stDev);
			//log = MultipleResourcesPerCase.getRiskyMedianSplit (log, dist, stDev);
			//log = MultipleResourcesPerCase.getRiskyAverage (log, task_names.elementAt(0), dist, stDev);
			
			testLog = MultipleResourcesPerCase.getRiskyMedianTwoLogs (trainLog, testLog, task_names.elementAt(0), dist, stDev);
			//testLog = MultipleResourcesPerCase.Risky (trainLog, testLog, task_names.elementAt(0));
						
			testLog = inout.processTimeNew(testLog);	
			//testLog = inout.processQuality(testLog);
			return testLog;
			
			//log = inout.processTime(log);	
			//log = inout.processQuality(log);
			//return log;		
								
			}	


public static XLog getTrainLog75 (XLog log){
	
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

public static XLog getTestLog25 (XLog log){
	
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

// functions for PRI Configuration

public XLog preprocessLogforConfiguration(XLog inputlog)
{
	Basic basic = new Basic();
	
	XLog log = XFactoryRegistry.instance().currentDefault().createLog(inputlog.getAttributes());
	
	for (XTrace t : inputlog) {
		XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
		log.add(trace);
					 
	for (XEvent e : t) {
			XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
			
			String lifecycle = XLifecycleExtension.instance().extractTransition(e);
				
			event.getAttributes().put("time:duration",new XAttributeDiscreteImpl("time:duration",0));
			event.getAttributes().put("subprocess:duration",new XAttributeDiscreteImpl("subprocess:duration",0));
			
			event.getAttributes().put("PRI1",new XAttributeBooleanImpl("PRI1",false));
			event.getAttributes().put("PRI2",new XAttributeBooleanImpl("PRI2",false));
			event.getAttributes().put("PRI3",new XAttributeBooleanImpl("PRI3",false));
			event.getAttributes().put("PRI4",new XAttributeBooleanImpl("PRI4",false));
			event.getAttributes().put("PRI6",new XAttributeBooleanImpl("PRI6",false));
			event.getAttributes().put("PRI8",new XAttributeBooleanImpl("PRI8",false));
			event.getAttributes().put("PRI9",new XAttributeBooleanImpl("PRI9",false));
			
			if (lifecycle.equalsIgnoreCase("complete"))
			{
				event.getAttributes().put("time:duration",new XAttributeDiscreteImpl("time:duration",basic.ActivityStartDuration(t, e)));
				event.getAttributes().put("subprocess:duration",new XAttributeDiscreteImpl("subprocess:duration",basic.SubprocessDuration(t, e)));
			}
			
			
			trace.add(event);
			
			
}
	
//adding case duration to log
	
	XEvent first = trace.get(0);
	XEvent last = trace.get(trace.size()-1);
	Date begin = XTimeExtension.instance().extractTimestamp(first);
	Date end = XTimeExtension.instance().extractTimestamp(last);
	long duration = end.getTime()-begin.getTime();
	trace.getAttributes().put("time:duration",new XAttributeDiscreteImpl("time:duration",duration));
	trace.getAttributes().put("PRI5",new XAttributeBooleanImpl("PRI5",false));
	trace.getAttributes().put("PRI7",new XAttributeBooleanImpl("PRI7",false));
	
//adding discovery time attribute to log
	
	trace.getAttributes().put("time:PRI1",new XAttributeDiscreteImpl("time:PRI1",0));
	trace.getAttributes().put("time:PRI2",new XAttributeDiscreteImpl("time:PRI2",0));
	trace.getAttributes().put("time:PRI3",new XAttributeDiscreteImpl("time:PRI3",0));
	trace.getAttributes().put("time:PRI4",new XAttributeDiscreteImpl("time:PRI4",0));
	trace.getAttributes().put("time:PRI5",new XAttributeDiscreteImpl("time:PRI5",0));
	trace.getAttributes().put("time:PRI6",new XAttributeDiscreteImpl("time:PRI6",0));
	trace.getAttributes().put("time:PRI7",new XAttributeDiscreteImpl("time:PRI7",0));
	trace.getAttributes().put("time:PRI8",new XAttributeDiscreteImpl("time:PRI8",0));
	trace.getAttributes().put("time:PRI9",new XAttributeDiscreteImpl("time:PRI9",0));
			
	
	
}
	
	return log;
	
}

public XLog  getRisks(UIPluginContext context, XLog testlog, PriCaseThreshold thresholds) {
	
	Basic basic = new Basic();
	
	XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(testlog.getAttributes());
	
	Double PRI5threshold = thresholds.PRI5threshold;
	Double PRI7threshold = thresholds.PRI7threshold;
	
	for (XTrace t : testlog) {
		XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
		copylog.add(trace);
		
		//---------------------case beginning time----------------------------------------
		XEvent first = t.get(0);
		Date begin = XTimeExtension.instance().extractTimestamp(first);
		//--------------------------------------------------------------------------------							
			
		ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();	
		ConcurrentSkipListSet<String> activities = new ConcurrentSkipListSet<String>();
		ConcurrentSkipListSet<String> repeated_activities = new ConcurrentSkipListSet<String>();
		ConcurrentSkipListSet<String> risky_activities = new ConcurrentSkipListSet<String>();
	
																 
	for (XEvent e : t) 
		{
		XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
		String eventName = XConceptExtension.instance().extractName(event);
		String lifecycle = XLifecycleExtension.instance().extractTransition(event);
		String resource = XOrganizationalExtension.instance().extractResource(event);
		
		//getting current event time
		Date end = XTimeExtension.instance().extractTimestamp(event);
		long discovered = end.getTime()-begin.getTime();
				
		
		// PRI 5
		
		if(resource != null)
		{resources.add(resource);}
			
		// PRI 7
		
		if(eventName != null)
		{activities.add(eventName);}
		
		// updating current workload for PRI 9
		
		
				int resIndex = thresholds.getResourceIndex(resource);
				
					if(lifecycle.equalsIgnoreCase("start"))
					{
						thresholds.resources.elementAt(resIndex).current_workload+=1;
					}
					
					if(lifecycle.equalsIgnoreCase("complete"))
					{
						thresholds.resources.elementAt(resIndex).current_workload-=1;
					}
				
				
		// ------------------------
			
		
		// getting thresholds
		Double PRI1threshold = thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1threshold;
		Double PRI2threshold = thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2threshold;
		Double PRI3threshold = thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3threshold;
		Boolean PRI4 = thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI4;
		Double PRI6threshold = thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6threshold;
	
		Boolean PRI8 = false;
		int actIndex = thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).getIndex(eventName);
		if(actIndex != -1)
		{PRI8 = thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).resourceActivities.elementAt(actIndex).PRI8;}
		Double PRI9threshold = thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9threshold;
		
		if (lifecycle.equalsIgnoreCase("complete")){
									
			// PRI 1
			XAttributeDiscrete act_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
			long actDuration = act_duration.getValue();
			if ((actDuration > PRI1threshold) && (PRI1threshold > 0))					
			{event.getAttributes().put("PRI1",new XAttributeBooleanImpl("PRI1",true));
			
			//discovery time
			String current_discovered = t.getAttributes().get("time:PRI1").toString();
			Long current = Long.parseLong(current_discovered);
			if(current > discovered || current_discovered.equals("0"))
				
			{
				
				trace.getAttributes().put("time:PRI1",new XAttributeDiscreteImpl("time:PRI1",discovered));
			}
			//------------
			}
			
			// PRI 3
			if(((basic.Index(t, e)+1) > PRI3threshold) && (PRI3threshold > 0) && !repeated_activities.contains(eventName))
			{repeated_activities.add(eventName);
				event.getAttributes().put("PRI3",new XAttributeBooleanImpl("PRI3",true));
				//discovery time
				String current_discovered = t.getAttributes().get("time:PRI3").toString();
				Long current = Long.parseLong(current_discovered);
				if(current > discovered || current_discovered.equals("0"))
					
				{
					
					trace.getAttributes().put("time:PRI3",new XAttributeDiscreteImpl("time:PRI3",discovered));
				}
				//------------	
			}
						
			// PRI 4
			if (PRI4 && !risky_activities.contains(eventName))					
			{risky_activities.add(eventName);
				event.getAttributes().put("PRI4",new XAttributeBooleanImpl("PRI4",true));
				
				//discovery time
				String current_discovered = t.getAttributes().get("time:PRI4").toString();
				Long current = Long.parseLong(current_discovered);
				if(current > discovered || current_discovered.equals("0"))
					
				{
					
					trace.getAttributes().put("time:PRI4",new XAttributeDiscreteImpl("time:PRI4",discovered));
				}
				//------------
			}
						
			// PRI 6
			XAttributeDiscrete subproc_duration = (XAttributeDiscrete)event.getAttributes().get("subprocess:duration");
			long subProcDuration = subproc_duration.getValue();
			if ((subProcDuration > PRI6threshold) && (PRI6threshold > 0))					
			{event.getAttributes().put("PRI6",new XAttributeBooleanImpl("PRI6",true));
			
			//discovery time
			String current_discovered = t.getAttributes().get("time:PRI6").toString();
			Long current = Long.parseLong(current_discovered);
			if(current > discovered || current_discovered.equals("0"))
				
			{
				
				trace.getAttributes().put("time:PRI6",new XAttributeDiscreteImpl("time:PRI6",discovered));
			}
			//------------
			
			}
			
			
		}
		
		if (lifecycle.equalsIgnoreCase("start") && t.indexOf(e)!=0){
			
			// PRI 2
			Date eventTime = XTimeExtension.instance().extractTimestamp(e);
			Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.PrevPRI2(t,e));
			long waitDuration = eventTime.getTime()-prevEventTime.getTime();
			if ((waitDuration > PRI2threshold) && (PRI2threshold > 0))					
			{event.getAttributes().put("PRI2",new XAttributeBooleanImpl("PRI2",true));
			
			//discovery time
			String current_discovered = t.getAttributes().get("time:PRI2").toString();
			Long current = Long.parseLong(current_discovered);
			if(current > discovered || current_discovered.equals("0"))
				
			{
				
				trace.getAttributes().put("time:PRI2",new XAttributeDiscreteImpl("time:PRI2",discovered));
			}
			//------------
			}
			
			
		}
		
		if (lifecycle.equalsIgnoreCase("start")){
			
			// PRI 8
			if (PRI8)					
			{event.getAttributes().put("PRI8",new XAttributeBooleanImpl("PRI8",true));
			
			//discovery time
			String current_discovered = t.getAttributes().get("time:PRI8").toString();
			Long current = Long.parseLong(current_discovered);
			if(current > discovered || current_discovered.equals("0"))
				
			{
				
				trace.getAttributes().put("time:PRI8",new XAttributeDiscreteImpl("time:PRI8",discovered));
			}
			//------------
			}
			
			// PRI 9
			
			Long workload = thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload;
			
			if ((workload > PRI9threshold) && (PRI9threshold > 0))					
			{event.getAttributes().put("PRI9",new XAttributeBooleanImpl("PRI9",true));
			
			//discovery time
			String current_discovered = t.getAttributes().get("time:PRI9").toString();
			Long current = Long.parseLong(current_discovered);
			if(current > discovered || current_discovered.equals("0"))
				
			{
				
				trace.getAttributes().put("time:PRI9",new XAttributeDiscreteImpl("time:PRI9",discovered));
			}
			//------------
			}
			
			// PRI 5
			long resNum = resources.size();
			if ((resNum > PRI5threshold) && (PRI5threshold > 0))					
			{trace.getAttributes().put("PRI5",new XAttributeBooleanImpl("PRI5",true));
			//discovery time
			String current_discovered = t.getAttributes().get("time:PRI5").toString();
			Long current = Long.parseLong(current_discovered);
			if(current > discovered || current_discovered.equals("0"))
				
			{
				
				trace.getAttributes().put("time:PRI5",new XAttributeDiscreteImpl("time:PRI5",discovered));
			}
			//------------
			}
			
			
			// PRI 7
			long actNum = activities.size();	
			if ((actNum > PRI7threshold) && (PRI7threshold > 0))				
			{trace.getAttributes().put("PRI7",new XAttributeBooleanImpl("PRI7",true));
			
			//discovery time
			String current_discovered = t.getAttributes().get("time:PRI7").toString();
			Long current = Long.parseLong(current_discovered);
			if(current > discovered || current_discovered.equals("0"))
				
			{
				
				trace.getAttributes().put("time:PRI7",new XAttributeDiscreteImpl("time:PRI7",discovered));
			}
			//------------
			
			}			
		
		}
		
			trace.add(event);
		}

	}		
					
					return copylog;
					
			
}


public XLog  getRisksWithModel(UIPluginContext context, XLog testlog, PriCaseThreshold thresholds, PetrinetGraph net) {
	
	Basic basic = new Basic();
	PriUtils priUtils = new PriUtils();
	
	XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(testlog.getAttributes());
	
	Double PRI5threshold = thresholds.PRI5threshold;
	Double PRI7threshold = thresholds.PRI7threshold;
	
	for (XTrace t : testlog) {
		XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
		copylog.add(trace);
		
		//---------------------case beginning time----------------------------------------
		XEvent first = t.get(0);
		Date begin = XTimeExtension.instance().extractTimestamp(first);
		//--------------------------------------------------------------------------------							
			
		ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();	
		ConcurrentSkipListSet<String> activities = new ConcurrentSkipListSet<String>();
		ConcurrentSkipListSet<String> repeated_activities = new ConcurrentSkipListSet<String>();
		ConcurrentSkipListSet<String> risky_activities = new ConcurrentSkipListSet<String>();
	
																 
	for (XEvent e : t) 
		{
		XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
		String eventName = XConceptExtension.instance().extractName(event);
		String lifecycle = XLifecycleExtension.instance().extractTransition(event);
		String resource = XOrganizationalExtension.instance().extractResource(event);
		
		//getting current event time
		Date end = XTimeExtension.instance().extractTimestamp(event);
		long discovered = end.getTime()-begin.getTime();
				
		
		// PRI 5
		
		if(resource != null)
		{resources.add(resource);}
			
		// PRI 7
		
		if(eventName != null)
		{activities.add(eventName);}
		
		// updating current workload for PRI 9
		
		
				int resIndex = thresholds.getResourceIndex(resource);
				
					if(lifecycle.equalsIgnoreCase("start"))
					{
						thresholds.resources.elementAt(resIndex).current_workload+=1;
					}
					
					if(lifecycle.equalsIgnoreCase("complete"))
					{
						thresholds.resources.elementAt(resIndex).current_workload-=1;
					}
				
				
		// ------------------------
			
		
		// getting thresholds
		Double PRI1threshold = thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1threshold;
		Double PRI2threshold = thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2threshold;
		Double PRI3threshold = thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3threshold;
		Boolean PRI4 = thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI4;
		Double PRI6threshold = thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6threshold;
	
		Boolean PRI8 = false;
		int actIndex = thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).getIndex(eventName);
		if(actIndex != -1)
		{PRI8 = thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).resourceActivities.elementAt(actIndex).PRI8;}
		Double PRI9threshold = thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9threshold;
		
		if (lifecycle.equalsIgnoreCase("complete")){
									
			// PRI 1
			XAttributeDiscrete act_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
			long actDuration = act_duration.getValue();
			if ((actDuration > PRI1threshold) && (PRI1threshold > 0))					
			{event.getAttributes().put("PRI1",new XAttributeBooleanImpl("PRI1",true));
			
			//discovery time
			String current_discovered = t.getAttributes().get("time:PRI1").toString();
			Long current = Long.parseLong(current_discovered);
			if(current > discovered || current_discovered.equals("0"))
				
			{
				
				trace.getAttributes().put("time:PRI1",new XAttributeDiscreteImpl("time:PRI1",discovered));
			}
			//------------
			}
			
			// PRI 3
			if(((basic.Index(t, e)+1) > PRI3threshold) && (PRI3threshold > 0) && !repeated_activities.contains(eventName))
			{repeated_activities.add(eventName);
				event.getAttributes().put("PRI3",new XAttributeBooleanImpl("PRI3",true));
				//discovery time
				String current_discovered = t.getAttributes().get("time:PRI3").toString();
				Long current = Long.parseLong(current_discovered);
				if(current > discovered || current_discovered.equals("0"))
					
				{
					
					trace.getAttributes().put("time:PRI3",new XAttributeDiscreteImpl("time:PRI3",discovered));
				}
				//------------	
			}
						
			// PRI 4
			if (PRI4 && !risky_activities.contains(eventName))					
			{risky_activities.add(eventName);
				event.getAttributes().put("PRI4",new XAttributeBooleanImpl("PRI4",true));
				
				//discovery time
				String current_discovered = t.getAttributes().get("time:PRI4").toString();
				Long current = Long.parseLong(current_discovered);
				if(current > discovered || current_discovered.equals("0"))
					
				{
					
					trace.getAttributes().put("time:PRI4",new XAttributeDiscreteImpl("time:PRI4",discovered));
				}
				//------------
			}
						
			// PRI 6
			XAttributeDiscrete subproc_duration = (XAttributeDiscrete)event.getAttributes().get("subprocess:duration");
			long subProcDuration = subproc_duration.getValue();
			if ((subProcDuration > PRI6threshold) && (PRI6threshold > 0))					
			{event.getAttributes().put("PRI6",new XAttributeBooleanImpl("PRI6",true));
			
			//discovery time
			String current_discovered = t.getAttributes().get("time:PRI6").toString();
			Long current = Long.parseLong(current_discovered);
			if(current > discovered || current_discovered.equals("0"))
				
			{
				
				trace.getAttributes().put("time:PRI6",new XAttributeDiscreteImpl("time:PRI6",discovered));
			}
			//------------
			
			}
			
			
		}
		
		if (lifecycle.equalsIgnoreCase("start") && t.indexOf(e)!=0){
			
			// PRI 2
			Date eventTime = XTimeExtension.instance().extractTimestamp(e);
			//Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.PrevPRI2(t,e));
			Date prevEventTime = XTimeExtension.instance().extractTimestamp(priUtils.PrevPRI2(t,e,net));
			long waitDuration = eventTime.getTime()-prevEventTime.getTime();
			if ((waitDuration > PRI2threshold) && (PRI2threshold > 0))					
			{event.getAttributes().put("PRI2",new XAttributeBooleanImpl("PRI2",true));
			
			//discovery time
			String current_discovered = t.getAttributes().get("time:PRI2").toString();
			Long current = Long.parseLong(current_discovered);
			if(current > discovered || current_discovered.equals("0"))
				
			{
				
				trace.getAttributes().put("time:PRI2",new XAttributeDiscreteImpl("time:PRI2",discovered));
			}
			//------------
			}
			
			
		}
		
		if (lifecycle.equalsIgnoreCase("start")){
			
			// PRI 8
			if (PRI8)					
			{event.getAttributes().put("PRI8",new XAttributeBooleanImpl("PRI8",true));
			
			//discovery time
			String current_discovered = t.getAttributes().get("time:PRI8").toString();
			Long current = Long.parseLong(current_discovered);
			if(current > discovered || current_discovered.equals("0"))
				
			{
				
				trace.getAttributes().put("time:PRI8",new XAttributeDiscreteImpl("time:PRI8",discovered));
			}
			//------------
			}
			
			// PRI 9
			
			Long workload = thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload;
			
			if ((workload > PRI9threshold) && (PRI9threshold > 0))					
			{event.getAttributes().put("PRI9",new XAttributeBooleanImpl("PRI9",true));
			
			//discovery time
			String current_discovered = t.getAttributes().get("time:PRI9").toString();
			Long current = Long.parseLong(current_discovered);
			if(current > discovered || current_discovered.equals("0"))
				
			{
				
				trace.getAttributes().put("time:PRI9",new XAttributeDiscreteImpl("time:PRI9",discovered));
			}
			//------------
			}
			
			// PRI 5
			long resNum = resources.size();
			if ((resNum > PRI5threshold) && (PRI5threshold > 0))					
			{trace.getAttributes().put("PRI5",new XAttributeBooleanImpl("PRI5",true));
			//discovery time
			String current_discovered = t.getAttributes().get("time:PRI5").toString();
			Long current = Long.parseLong(current_discovered);
			if(current > discovered || current_discovered.equals("0"))
				
			{
				
				trace.getAttributes().put("time:PRI5",new XAttributeDiscreteImpl("time:PRI5",discovered));
			}
			//------------
			}
			
			
			// PRI 7
			long actNum = activities.size();	
			if ((actNum > PRI7threshold) && (PRI7threshold > 0))				
			{trace.getAttributes().put("PRI7",new XAttributeBooleanImpl("PRI7",true));
			
			//discovery time
			String current_discovered = t.getAttributes().get("time:PRI7").toString();
			Long current = Long.parseLong(current_discovered);
			if(current > discovered || current_discovered.equals("0"))
				
			{
				
				trace.getAttributes().put("time:PRI7",new XAttributeDiscreteImpl("time:PRI7",discovered));
			}
			//------------
			
			}			
		
		}
		
			trace.add(event);
		}

	}		
					
					return copylog;
					
			
}


@SuppressWarnings("rawtypes")
public XLog getOneDataSetRisks(UIPluginContext context, XLog log, InputParameters ip)
{
	Stat stat = new Stat();
	Double dist = 1.483;
	Double allstDev = 2.00;
	
	int devNum = ip.devNum;
	double step = ip.step;

	Double SLA = 0.0;
	if(ip.calculateSLA)
	{
		SLA = stat.getMedianLogSLA(log, dist, allstDev);
	}
	else
	{SLA = ip.SLA;}
	PriUtils priUtils = new PriUtils();	
	Basic basic = new Basic();
	
	Double p1 = ip.desiredPrecision;
/*	Double p2 = ip.desiredPrecision;
	Double p3 = ip.desiredPrecision;
	Double p4 = ip.desiredPrecision;
	Double p5 = ip.desiredPrecision;
	Double p6 = ip.desiredPrecision;
	Double p7 = ip.desiredPrecision;
	Double p8 = ip.desiredPrecision;
	Double p9 = ip.desiredPrecision;
*/
	
	ConcurrentSkipListSet<String> activities = new ConcurrentSkipListSet<String>();
	ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();
	
	for (XTrace t : log) {
		//XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
		//log.add(trace);
					 
	for (XEvent e : t) {
			//XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
			
			//String lifecycle = XLifecycleExtension.instance().extractTransition(e);
			String activity = XConceptExtension.instance().extractName(e);
			String resource = XOrganizationalExtension.instance().extractResource(e);
			
			if(resource != null)
			{resources.add(resource);}
		
			if(activity != null)
			{activities.add(activity);}

						}
							}
	
// annotating case outcomes

log = priUtils.addTimeOutcome (log, SLA);	//true - delayed, false - in time

// getting training and test sets

XLog testLog = priUtils.getTestLog25(log); // random split
XLog trainLog = priUtils.getTrainLog75(log); //random split

		
//-----------TRAINING-------------------------------

PriCaseThreshold thresholds = new PriCaseThreshold(devNum,step);
Iterator iterator = activities.iterator();			
while (iterator.hasNext()) 
{
thresholds.activities.add(new PriActivityThreshold((String)iterator.next(),devNum,step));
}

Iterator iterator2 = resources.iterator();			
while (iterator2.hasNext()) 
{
thresholds.resources.add(new PriResourceThreshold((String)iterator2.next(),devNum,step));
}


// learning values for thresholds -------------------------------

for (XTrace t : trainLog) {

ConcurrentSkipListSet<String> caseResources = new ConcurrentSkipListSet<String>();	
ConcurrentSkipListSet<String> caseActivities = new ConcurrentSkipListSet<String>();
											 
for (XEvent e : t) 
{
String eventName = XConceptExtension.instance().extractName(e);
String lifecycle = XLifecycleExtension.instance().extractTransition(e);
String resource = XOrganizationalExtension.instance().extractResource(e);

if(resource != null)
	{caseResources.add(resource);}

if(eventName != null)
{caseActivities.add(eventName);}

int resIndex = thresholds.getResourceIndex(resource);
					
	//PRI 2
	if (lifecycle.equalsIgnoreCase("start") && t.indexOf(e)!=0)
	{
		Date eventTime = XTimeExtension.instance().extractTimestamp(e);
		Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.PrevPRI2(t,e));
		long waitDuration = eventTime.getTime()-prevEventTime.getTime();
		thresholds.activities.elementAt(thresholds.getIndex(eventName)).waitDurations.add(waitDuration);
						
	}
	//------------------------
	if (lifecycle.equalsIgnoreCase("complete"))
	{
	// PRI 1
		XAttributeDiscrete act_duration = (XAttributeDiscrete)e.getAttributes().get("time:duration");
		long actDuration = act_duration.getValue();
		thresholds.activities.elementAt(thresholds.getIndex(eventName)).actDurations.add(actDuration);
		
	// PRI 6
		XAttributeDiscrete subproc_duration = (XAttributeDiscrete)e.getAttributes().get("subprocess:duration");
		long subprocDuration = subproc_duration.getValue();
		thresholds.activities.elementAt(thresholds.getIndex(eventName)).subProcDurations.add(subprocDuration);
		
	// PRI 4
		thresholds.activities.elementAt(thresholds.getIndex(eventName)).numExecutions +=1;
		
		
	// PRI 3
		thresholds.activities.elementAt(thresholds.getIndex(eventName)).caseRepetitions +=1;
	
	// PRI 8
		
	int actIndex = thresholds.resources.elementAt(resIndex).getIndex(eventName);
		
	if (actIndex == -1){thresholds.resources.elementAt(resIndex).resourceActivities.add(new PriResourceActivityThreshold(eventName));}
	actIndex = thresholds.resources.elementAt(resIndex).getIndex(eventName);
		
	thresholds.resources.elementAt(resIndex).resourceActivities.elementAt(actIndex).currentNumExecutions+=1;
	thresholds.resources.elementAt(resIndex).resourceActivities.elementAt(actIndex).currentTotalDuration+=actDuration;
	
	}
	
// PRI 9
	
	for (int i=0; i<thresholds.resources.size();i++)
	{
		if(lifecycle.equalsIgnoreCase("start"))
		{
			if (i == resIndex){thresholds.resources.elementAt(i).current_workload+=1;}
		}
		
		if(lifecycle.equalsIgnoreCase("complete"))
		{
			if (i == resIndex){thresholds.resources.elementAt(i).current_workload-=1;}
		}
		
		if(thresholds.resources.elementAt(i).current_workload > 0)
		{
			thresholds.resources.elementAt(i).resourceWorkloads.add(thresholds.resources.elementAt(i).current_workload);
		}
		
	}
	
	}

for (int i=0;i<thresholds.activities.size();i++)
{
if (thresholds.activities.elementAt(i).caseRepetitions != 0){thresholds.activities.elementAt(i).Repetitions.add(thresholds.activities.elementAt(i).caseRepetitions);}
thresholds.activities.elementAt(i).caseRepetitions = new Long(0);
}

// PRI 7

thresholds.caseActivities.add(new Long(caseActivities.size()));

// PRI 5

thresholds.caseResources.add(new Long(caseResources.size()));

}
				
//--------Learning averages and deviations------------------------------------------------------------------

// PRI 5

thresholds.PRI5av = priUtils.LongAverage(thresholds.caseResources);
thresholds.PRI5dev = priUtils.getStDev(thresholds.caseResources);

// PRI 7

thresholds.PRI7av = priUtils.LongAverage(thresholds.caseActivities);
thresholds.PRI7dev = priUtils.getStDev(thresholds.caseActivities);


for (int i=0;i<thresholds.activities.size();i++){

// PRI 1

thresholds.activities.elementAt(i).PRI1av = priUtils.LongAverage(thresholds.activities.elementAt(i).actDurations);
thresholds.activities.elementAt(i).PRI1dev = priUtils.getStDev(thresholds.activities.elementAt(i).actDurations);

// PRI 2

thresholds.activities.elementAt(i).PRI2av = priUtils.LongAverage(thresholds.activities.elementAt(i).waitDurations);
thresholds.activities.elementAt(i).PRI2dev = priUtils.getStDev(thresholds.activities.elementAt(i).waitDurations);

// PRI 3

thresholds.activities.elementAt(i).PRI3av = priUtils.LongAverage(thresholds.activities.elementAt(i).Repetitions);
thresholds.activities.elementAt(i).PRI3dev = priUtils.getStDev(thresholds.activities.elementAt(i).Repetitions);

// PRI 6

thresholds.activities.elementAt(i).PRI6av = priUtils.LongAverage(thresholds.activities.elementAt(i).subProcDurations);
thresholds.activities.elementAt(i).PRI6dev = priUtils.getStDev(thresholds.activities.elementAt(i).subProcDurations);

}

for (int i=0;i<thresholds.resources.size();i++){

// PRI 9

thresholds.resources.elementAt(i).PRI9av = priUtils.LongAverage(thresholds.resources.elementAt(i).resourceWorkloads);
thresholds.resources.elementAt(i).PRI9dev = priUtils.getStDev(thresholds.resources.elementAt(i).resourceWorkloads);
thresholds.resources.elementAt(i).current_workload = new Long(0);

// PRI 8

for (int j=0; j<thresholds.resources.elementAt(i).resourceActivities.size();j++)
{
	thresholds.resources.elementAt(i).resourceActivities.elementAt(j).PRI8av = (double)thresholds.resources.elementAt(i).resourceActivities.elementAt(j).currentTotalDuration/thresholds.resources.elementAt(i).resourceActivities.elementAt(j).currentNumExecutions;
	
}
}

//--------------------------------------------------

//if(ip.configure)
//{	System.out.println("Getting thresholds...");
	//thresholds = getThresholds(context, trainLog, thresholds, p1, p2, p3, p4, p5, p6, p7, p8, p9);
	//thresholds = getThresholdsFixed(context, trainLog, thresholds, p1);
//}
//else
//{
	//thresholds = getThresholdsNoConfiguration(context, trainLog, thresholds, p1, p2, p3, p4, p5, p6, p7, p8, p9);	
	//System.out.println("Getting thresholds - no configuration...");
	//thresholds = getThresholdsNoConfigurationFixed(context, trainLog, thresholds, p1);
//}
	
System.out.println("Getting thresholds");
thresholds = getThresholdsFixed(context, trainLog, thresholds, p1);

testLog = getRisks(context, testLog, thresholds);	
testLog = priUtils.processTime(testLog);	
	
	
	return testLog;
}


@SuppressWarnings("rawtypes")
public XLog getOneDataSetRisksWithModel(UIPluginContext context, XLog log, InputParameters ip, PetrinetGraph net)
{
	Stat stat = new Stat();
	Double dist = 1.483;
	Double allstDev = 2.00;
	
	int devNum = ip.devNum;
	double step = ip.step;

	Double SLA = 0.0;
	if(ip.calculateSLA)
	{
		SLA = stat.getMedianLogSLA(log, dist, allstDev);
	}
	else
	{SLA = ip.SLA;}
	PriUtils priUtils = new PriUtils();	
	Basic basic = new Basic();
	
	Double p1 = ip.desiredPrecision;
/*	Double p2 = ip.desiredPrecision;
	Double p3 = ip.desiredPrecision;
	Double p4 = ip.desiredPrecision;
	Double p5 = ip.desiredPrecision;
	Double p6 = ip.desiredPrecision;
	Double p7 = ip.desiredPrecision;
	Double p8 = ip.desiredPrecision;
	Double p9 = ip.desiredPrecision;
*/
	
	ConcurrentSkipListSet<String> activities = new ConcurrentSkipListSet<String>();
	ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();
	
	for (XTrace t : log) {
		//XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
		//log.add(trace);
					 
	for (XEvent e : t) {
			//XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
			
			//String lifecycle = XLifecycleExtension.instance().extractTransition(e);
			String activity = XConceptExtension.instance().extractName(e);
			String resource = XOrganizationalExtension.instance().extractResource(e);
			
			if(resource != null)
			{resources.add(resource);}
		
			if(activity != null)
			{activities.add(activity);}

						}
							}
	
// annotating case outcomes

log = priUtils.addTimeOutcome (log, SLA);	//true - delayed, false - in time

// getting training and test sets

XLog testLog = priUtils.getTestLog25(log); // random split
XLog trainLog = priUtils.getTrainLog75(log); //random split

		
//-----------TRAINING-------------------------------

PriCaseThreshold thresholds = new PriCaseThreshold(devNum,step);
Iterator iterator = activities.iterator();			
while (iterator.hasNext()) 
{
thresholds.activities.add(new PriActivityThreshold((String)iterator.next(),devNum,step));
}

Iterator iterator2 = resources.iterator();			
while (iterator2.hasNext()) 
{
thresholds.resources.add(new PriResourceThreshold((String)iterator2.next(),devNum,step));
}


// learning values for thresholds -------------------------------

for (XTrace t : trainLog) {

ConcurrentSkipListSet<String> caseResources = new ConcurrentSkipListSet<String>();	
ConcurrentSkipListSet<String> caseActivities = new ConcurrentSkipListSet<String>();
											 
for (XEvent e : t) 
{
String eventName = XConceptExtension.instance().extractName(e);
String lifecycle = XLifecycleExtension.instance().extractTransition(e);
String resource = XOrganizationalExtension.instance().extractResource(e);

if(resource != null)
	{caseResources.add(resource);}

if(eventName != null)
{caseActivities.add(eventName);}

int resIndex = thresholds.getResourceIndex(resource);
					
	//PRI 2
	if (lifecycle.equalsIgnoreCase("start") && t.indexOf(e)!=0)
	{
		Date eventTime = XTimeExtension.instance().extractTimestamp(e);
		//Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.PrevPRI2(t,e));
		Date prevEventTime = XTimeExtension.instance().extractTimestamp(priUtils.PrevPRI2(t,e,net));
		long waitDuration = eventTime.getTime()-prevEventTime.getTime();
		thresholds.activities.elementAt(thresholds.getIndex(eventName)).waitDurations.add(waitDuration);
						
	}
	//------------------------
	if (lifecycle.equalsIgnoreCase("complete"))
	{
	// PRI 1
		XAttributeDiscrete act_duration = (XAttributeDiscrete)e.getAttributes().get("time:duration");
		long actDuration = act_duration.getValue();
		thresholds.activities.elementAt(thresholds.getIndex(eventName)).actDurations.add(actDuration);
		
	// PRI 6
		XAttributeDiscrete subproc_duration = (XAttributeDiscrete)e.getAttributes().get("subprocess:duration");
		long subprocDuration = subproc_duration.getValue();
		thresholds.activities.elementAt(thresholds.getIndex(eventName)).subProcDurations.add(subprocDuration);
		
	// PRI 4
		thresholds.activities.elementAt(thresholds.getIndex(eventName)).numExecutions +=1;
		
		
	// PRI 3
		thresholds.activities.elementAt(thresholds.getIndex(eventName)).caseRepetitions +=1;
	
	// PRI 8
		
	int actIndex = thresholds.resources.elementAt(resIndex).getIndex(eventName);
		
	if (actIndex == -1){thresholds.resources.elementAt(resIndex).resourceActivities.add(new PriResourceActivityThreshold(eventName));}
	actIndex = thresholds.resources.elementAt(resIndex).getIndex(eventName);
		
	thresholds.resources.elementAt(resIndex).resourceActivities.elementAt(actIndex).currentNumExecutions+=1;
	thresholds.resources.elementAt(resIndex).resourceActivities.elementAt(actIndex).currentTotalDuration+=actDuration;
	
	}
	
// PRI 9
	
	for (int i=0; i<thresholds.resources.size();i++)
	{
		if(lifecycle.equalsIgnoreCase("start"))
		{
			if (i == resIndex){thresholds.resources.elementAt(i).current_workload+=1;}
		}
		
		if(lifecycle.equalsIgnoreCase("complete"))
		{
			if (i == resIndex){thresholds.resources.elementAt(i).current_workload-=1;}
		}
		
		if(thresholds.resources.elementAt(i).current_workload > 0)
		{
			thresholds.resources.elementAt(i).resourceWorkloads.add(thresholds.resources.elementAt(i).current_workload);
		}
		
	}
	
	}

for (int i=0;i<thresholds.activities.size();i++)
{
if (thresholds.activities.elementAt(i).caseRepetitions != 0){thresholds.activities.elementAt(i).Repetitions.add(thresholds.activities.elementAt(i).caseRepetitions);}
thresholds.activities.elementAt(i).caseRepetitions = new Long(0);
}

// PRI 7

thresholds.caseActivities.add(new Long(caseActivities.size()));

// PRI 5

thresholds.caseResources.add(new Long(caseResources.size()));

}
				
//--------Learning averages and deviations------------------------------------------------------------------

// PRI 5

thresholds.PRI5av = priUtils.LongAverage(thresholds.caseResources);
thresholds.PRI5dev = priUtils.getStDev(thresholds.caseResources);

// PRI 7

thresholds.PRI7av = priUtils.LongAverage(thresholds.caseActivities);
thresholds.PRI7dev = priUtils.getStDev(thresholds.caseActivities);


for (int i=0;i<thresholds.activities.size();i++){

// PRI 1

thresholds.activities.elementAt(i).PRI1av = priUtils.LongAverage(thresholds.activities.elementAt(i).actDurations);
thresholds.activities.elementAt(i).PRI1dev = priUtils.getStDev(thresholds.activities.elementAt(i).actDurations);

// PRI 2

thresholds.activities.elementAt(i).PRI2av = priUtils.LongAverage(thresholds.activities.elementAt(i).waitDurations);
thresholds.activities.elementAt(i).PRI2dev = priUtils.getStDev(thresholds.activities.elementAt(i).waitDurations);

// PRI 3

thresholds.activities.elementAt(i).PRI3av = priUtils.LongAverage(thresholds.activities.elementAt(i).Repetitions);
thresholds.activities.elementAt(i).PRI3dev = priUtils.getStDev(thresholds.activities.elementAt(i).Repetitions);

// PRI 6

thresholds.activities.elementAt(i).PRI6av = priUtils.LongAverage(thresholds.activities.elementAt(i).subProcDurations);
thresholds.activities.elementAt(i).PRI6dev = priUtils.getStDev(thresholds.activities.elementAt(i).subProcDurations);

}

for (int i=0;i<thresholds.resources.size();i++){

// PRI 9

thresholds.resources.elementAt(i).PRI9av = priUtils.LongAverage(thresholds.resources.elementAt(i).resourceWorkloads);
thresholds.resources.elementAt(i).PRI9dev = priUtils.getStDev(thresholds.resources.elementAt(i).resourceWorkloads);
thresholds.resources.elementAt(i).current_workload = new Long(0);

// PRI 8

for (int j=0; j<thresholds.resources.elementAt(i).resourceActivities.size();j++)
{
	thresholds.resources.elementAt(i).resourceActivities.elementAt(j).PRI8av = (double)thresholds.resources.elementAt(i).resourceActivities.elementAt(j).currentTotalDuration/thresholds.resources.elementAt(i).resourceActivities.elementAt(j).currentNumExecutions;
	
}
}

//--------------------------------------------------

//if(ip.configure)
//{	System.out.println("Getting thresholds...");
	//thresholds = getThresholds(context, trainLog, thresholds, p1, p2, p3, p4, p5, p6, p7, p8, p9);
	//thresholds = getThresholdsFixed(context, trainLog, thresholds, p1);
//}
//else
//{
	//thresholds = getThresholdsNoConfiguration(context, trainLog, thresholds, p1, p2, p3, p4, p5, p6, p7, p8, p9);	
	//System.out.println("Getting thresholds - no configuration...");
	//thresholds = getThresholdsNoConfigurationFixed(context, trainLog, thresholds, p1);
//}
	
System.out.println("Getting thresholds");
thresholds = getThresholdsFixedWithModel(context, trainLog, thresholds, p1,net);

testLog = getRisksWithModel(context, testLog, thresholds,net);	
testLog = priUtils.processTime(testLog);	
	
	
	return testLog;
}


public PriCaseThreshold  getThresholdsFixed(UIPluginContext context, XLog trainlog, PriCaseThreshold thresholds, Double p) {
	
	Basic basic = new Basic();
	
	//Double dev1 = 1.00;
	//Double dev2 = 2.00;
	//Double dev3 = 3.00;
	
	Double PRI5av = thresholds.PRI5av;
	Double PRI5dev = thresholds.PRI5dev;
	Double PRI7av = thresholds.PRI7av;
	Double PRI7dev = thresholds.PRI7dev;
	
	for (XTrace t : trainlog) {
		
		XAttributeMap am = t.getAttributes();
		String outcome = am.get("outcome:case_duration").toString();
				
		ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();	
		ConcurrentSkipListSet<String> activities = new ConcurrentSkipListSet<String>();
		ConcurrentSkipListSet<String> repeated_activities = new ConcurrentSkipListSet<String>();	
		ConcurrentSkipListSet<String> risky_activities = new ConcurrentSkipListSet<String>();
		
	for (XEvent e : t) 
		{
		XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
		String eventName = XConceptExtension.instance().extractName(event);
		String lifecycle = XLifecycleExtension.instance().extractTransition(event);
		String resource = XOrganizationalExtension.instance().extractResource(event);
		
		// PRI 5
		
		if(resource != null)
		{resources.add(resource);}
			
		// PRI 7
		
		if(eventName != null)
		{activities.add(eventName);}
		
		// updating current workload for PRI 9
		
		int resIndex = thresholds.getResourceIndex(resource);
		
			if(lifecycle.equalsIgnoreCase("start"))
			{
				thresholds.resources.elementAt(resIndex).current_workload+=1;
			}
			
			if(lifecycle.equalsIgnoreCase("complete"))
			{
				thresholds.resources.elementAt(resIndex).current_workload-=1;
			}
		
		// ------------------------
		
		
		if (lifecycle.equalsIgnoreCase("complete")){
									
			
			XAttributeDiscrete act_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
			long actDuration = act_duration.getValue();
			
			// PRI 1 - old
	/*		if (actDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t1f+=1;}
			}
			
			if (actDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t2f+=1;}
			}
			
			if (actDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t3f+=1;}
			}
*/			
			// PRI 1 - new
			for(int i=0; i<thresholds.stdDev.size();  i++)
			{
				if (actDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av +thresholds.stdDev.elementAt(i)*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1dev)) 					
					{
						if (outcome.equals("true"))
							thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1true.set(i, thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1true.elementAt(i)+1);
						else
							thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1false.set(i, thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1false.elementAt(i)+1);
					}
			}
			
			
			// PRI 3 - old
	/*		if(!repeated_activities.contains(eventName) && (basic.Index(t, e)+1) > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3dev)) 					
			{repeated_activities.add(eventName);
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t1f+=1;}
			}
			
			if((basic.Index(t, e)+1) > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t2f+=1;}
			}	
			
			if((basic.Index(t, e)+1) > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t3f+=1;}
			}	
*/			
			// PRI 3 - new
			
			if(!repeated_activities.contains(eventName))
				repeated_activities.add(eventName);
			
			for(int i=0; i<thresholds.stdDev.size(); i++)
			{	
				if((basic.Index(t, e)+1) > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3av + thresholds.stdDev.elementAt(i)*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3dev)) 					
				{
					if (outcome.equals("true"))
						thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3true.set(i, thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3true.elementAt(i)+1);
					else
						thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3false.set(i, thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3false.elementAt(i)+1);
				}
			
			}
						
			// PRI 4 - not changed, old and new are the same
			if(!risky_activities.contains(eventName))
			{risky_activities.add(eventName);
			if (outcome.equals("true"))					
			{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri4t+=1;}
			else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri4f+=1;};
			}
			
			
			XAttributeDiscrete subproc_duration = (XAttributeDiscrete)event.getAttributes().get("subprocess:duration");
			long subProcDuration = subproc_duration.getValue();
			// PRI 6 - old
		/*	if (subProcDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t1f+=1;}
			}
		
			if (subProcDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t2f+=1;}
			}
			
			if (subProcDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t3f+=1;}
			}
	*/		
			// PRI 6 - new
			
			for(int i=0; i<thresholds.stdDev.size(); i++)
			{
				if (subProcDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6av + thresholds.stdDev.elementAt(i)*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6dev)) 					
				{
					if (outcome.equals("true"))
						thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6true.set(i, thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6true.elementAt(i)+1);
					else
						thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6false.set(i, thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6false.elementAt(i)+1);
				}
			}
	
			
	// PRI 8 - not changed - old and new are the same
			
			//Double activityAverage = thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av;
			Double resourceActivityAverage = thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).resourceActivities.elementAt(thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).getIndex(eventName)).PRI8av;
				if ( resourceActivityAverage > -1 /*activityAverage*/ ) 					
					{
					if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).resourceActivities.elementAt(thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).getIndex(eventName)).pri8t+=1;}
					else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).resourceActivities.elementAt(thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).getIndex(eventName)).pri8f+=1;}
					}
	
		}
		
		if (lifecycle.equalsIgnoreCase("start") && t.indexOf(e)!=0){
			
			
			Date eventTime = XTimeExtension.instance().extractTimestamp(e);
			//Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.PrevPRI2(t,e));
			Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.PrevPRI2(t,e));
			long waitDuration = eventTime.getTime()-prevEventTime.getTime();
			
			// PRI 2 - old
		/*	if (waitDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t1f+=1;}
			}
			
			if (waitDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t2f+=1;}
			}
			
			if (waitDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t3f+=1;}
			}
*/			
			//PRI2 - new
			for(int i=0; i<thresholds.stdDev.size(); i++)
			{
				if (waitDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2av + thresholds.stdDev.elementAt(i)*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2dev)) 					
				{
					if (outcome.equals("true"))
						thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2true.set(i, thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2true.elementAt(i)+1);
					else
						thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2false.set(i, thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2false.elementAt(i)+1);
				}
			}
		
		}
		
		if (lifecycle.equalsIgnoreCase("start")){
			
			// PRI 9 - old
		/*	if (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload > (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9av + dev1*thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9dev)) 					
			{
				if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t1t+=1;}
				else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t1f+=1;}
			}
			
			if (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload > (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9av + dev2*thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9dev)) 					
			{
				if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t2t+=1;}
				else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t2f+=1;}
			}
			
			if (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload > (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9av + dev3*thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9dev)) 					
			{
				if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t3t+=1;}
				else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t3f+=1;}
			}
		*/	
			//PRI9 - new
			
			for(int i=0; i<thresholds.stdDev.size(); i++)
			{
				if (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload > (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9av + thresholds.stdDev.elementAt(i)*thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9dev)) 					
				{
					if (outcome.equals("true"))
						thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9true.set(i, thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9true.elementAt(i)+1);
					else
						thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9false.set(i, thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9false.elementAt(i)+1);
				}
			}
	
			
			
	}
}
	
	// ------------------------------------
	
	
	long resNum = resources.size();
	
	// PRI 5 - old
/*	if (resNum > (PRI5av+dev1*PRI5dev))					
	{
		if (outcome.equals("true")){thresholds.pri5t1t+=1;}else{thresholds.pri5t1f+=1;}
	}
	
	if (resNum > (PRI5av+dev2*PRI5dev))					
	{
		if (outcome.equals("true")){thresholds.pri5t2t+=1;}else{thresholds.pri5t2f+=1;}
	}
	
	if (resNum > (PRI5av+dev3*PRI5dev))					
	{
		if (outcome.equals("true")){thresholds.pri5t3t+=1;}else{thresholds.pri5t3f+=1;}
	}
*/	
	// PRI 5 - new
		for(int i=0; i<thresholds.stdDev.size(); i++)
		{
			if (resNum > (PRI5av+thresholds.stdDev.elementAt(i)*PRI5dev))					
			{
				if (outcome.equals("true"))
					thresholds.pri5true.set(i, thresholds.pri5true.elementAt(i)+1);
				else
					thresholds.pri5false.set(i, thresholds.pri5false.elementAt(i)+1);
			}
		}
	
	
	long actNum = activities.size();	
	
	// PRI 7 - old
/*	if (actNum > (PRI7av+dev1*PRI7dev))					
	{
		if (outcome.equals("true")){thresholds.pri7t1t+=1;}else{thresholds.pri7t1f+=1;}
	}
	
	if (actNum > (PRI7av+dev2*PRI7dev))					
	{
		if (outcome.equals("true")){thresholds.pri7t2t+=1;}else{thresholds.pri7t2f+=1;}
	}
	
	if (actNum > (PRI7av+dev3*PRI7dev))					
	{
		if (outcome.equals("true")){thresholds.pri7t3t+=1;}else{thresholds.pri7t3f+=1;}
	}
*/	
	//PRI 7 - new
	
	for(int i=0; i<thresholds.stdDev.size(); i++)
	{
		if (actNum > (PRI7av+thresholds.stdDev.elementAt(i)*PRI7dev))					
		{
			if (outcome.equals("true"))
				thresholds.pri7true.set(i, thresholds.pri7true.elementAt(i)+1);
			else
				thresholds.pri7false.set(i, thresholds.pri7false.elementAt(i)+1);
		}
	}

	
	
}		
		

//------------------------

/*	//PRI5 - old
	Double check51 = (double)thresholds.pri5t1t/(thresholds.pri5t1t+thresholds.pri5t1f);
	Double check52 = (double)thresholds.pri5t2t/(thresholds.pri5t2t+thresholds.pri5t2f);
	Double check53 = (double)thresholds.pri5t3t/(thresholds.pri5t3t+thresholds.pri5t3f);
	
	
	// PRI5
	if(thresholds.pri5t1t+thresholds.pri5t1f != 0)	
	{if (check51 >= p)
	{thresholds.PRI5threshold = thresholds.PRI5av+dev1*thresholds.PRI5dev;}
	else{if(thresholds.pri5t2t+thresholds.pri5t2f != 0)	
		if(check52 >= p)
		{thresholds.PRI5threshold = thresholds.PRI5av+dev2*thresholds.PRI5dev;}
			else{if(thresholds.pri5t3t+thresholds.pri5t3f != 0)	
				if(check53 >= p)
				{thresholds.PRI5threshold = thresholds.PRI5av+dev3*thresholds.PRI5dev;}
				}
		}
		
	}
*/	
	//PRI5 - new
	for(int i=0; i<thresholds.stdDev.size(); i++)
		thresholds.pri5fraction.set(i,(double)thresholds.pri5true.elementAt(i)/(thresholds.pri5true.elementAt(i)+thresholds.pri5false.elementAt(i)));
	
	for(int i=0; i<thresholds.stdDev.size(); i++)
	{
		if(thresholds.pri5true.elementAt(i)+thresholds.pri5false.elementAt(i) != 0 && thresholds.pri5fraction.elementAt(i) >=p)	
		{
		thresholds.PRI5threshold = thresholds.PRI5av+thresholds.stdDev.elementAt(i)*thresholds.PRI5dev;
		break;
		}
	}
	
	//------------------------
	// PRI7 - old

/*	Double check71 = (double)thresholds.pri7t1t/(thresholds.pri7t1t+thresholds.pri7t1f);
	Double check72 = (double)thresholds.pri7t2t/(thresholds.pri7t2t+thresholds.pri7t2f);
	Double check73 = (double)thresholds.pri7t3t/(thresholds.pri7t3t+thresholds.pri7t3f);

	
	if(thresholds.pri7t1t+thresholds.pri7t1f != 0)	
	{if (check71 >= p)
	{thresholds.PRI7threshold = thresholds.PRI7av+dev1*thresholds.PRI7dev;}
	else{if(thresholds.pri7t2t+thresholds.pri7t2f != 0)	
		if(check72 >= p)
		{thresholds.PRI7threshold = thresholds.PRI7av+dev2*thresholds.PRI7dev;}
			else{if(thresholds.pri7t3t+thresholds.pri7t3f != 0)	
				if(check73 >= p)
				{thresholds.PRI7threshold = thresholds.PRI7av+dev3*thresholds.PRI7dev;}
				}
		}
		
	}
*/	
	//PRI 7 - new
	
	for(int i=0; i<thresholds.stdDev.size(); i++)
		thresholds.pri7fraction.set(i,(double)thresholds.pri7true.elementAt(i)/(thresholds.pri7true.elementAt(i)+thresholds.pri7false.elementAt(i)));
	
	for(int i=0; i<thresholds.stdDev.size(); i++)
	{
		if(thresholds.pri7true.elementAt(i)+thresholds.pri7false.elementAt(i) != 0 && thresholds.pri7fraction.elementAt(i) >=p)	
		{
		thresholds.PRI7threshold = thresholds.PRI7av+thresholds.stdDev.elementAt(i)*thresholds.PRI7dev;
		break;
		}
	}


//-----------------
	
		
	for (int i=0;i<thresholds.activities.size();i++)
		
	{
		// PRI 1 - old
		
	/*	Double check11 = (double)thresholds.activities.elementAt(i).pri1t1t/(thresholds.activities.elementAt(i).pri1t1t+thresholds.activities.elementAt(i).pri1t1f);
		Double check12 = (double)thresholds.activities.elementAt(i).pri1t2t/(thresholds.activities.elementAt(i).pri1t2t+thresholds.activities.elementAt(i).pri1t2f);
		Double check13 = (double)thresholds.activities.elementAt(i).pri1t3t/(thresholds.activities.elementAt(i).pri1t3t+thresholds.activities.elementAt(i).pri1t3f);
		
		
		if(thresholds.activities.elementAt(i).pri1t1t+thresholds.activities.elementAt(i).pri1t1f != 0)	
		{if (check11 >= p)
		{thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+dev1*thresholds.activities.elementAt(i).PRI1dev;}
		else{if(thresholds.activities.elementAt(i).pri1t2t+thresholds.activities.elementAt(i).pri1t2f != 0)	
			if(check12 >= p)
			{thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+dev2*thresholds.activities.elementAt(i).PRI1dev;}
				else{if(thresholds.activities.elementAt(i).pri1t3t+thresholds.activities.elementAt(i).pri1t3f != 0)	
					if(check13 >= p)
					{thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+dev3*thresholds.activities.elementAt(i).PRI1dev;}
					}
			}
			
		}
*/		
		//PRI 1 - new
		
		for(int j=0; j<thresholds.stdDev.size(); j++)
			thresholds.activities.elementAt(i).pri1fraction.set(j, (double)thresholds.activities.elementAt(i).pri1true.elementAt(j)/(thresholds.activities.elementAt(i).pri1true.elementAt(j)+thresholds.activities.elementAt(i).pri1false.elementAt(j)));
		
		for(int j=0; j<thresholds.stdDev.size(); j++)
		{
			if(thresholds.activities.elementAt(i).pri1true.elementAt(j)+thresholds.activities.elementAt(i).pri1false.elementAt(j) != 0 && thresholds.activities.elementAt(i).pri1fraction.elementAt(j) >=p)	
			{
				thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+thresholds.stdDev.elementAt(j)*thresholds.activities.elementAt(i).PRI1dev;
			break;
			}
		}

		
		//-----------------------
		
		// PRI 2 - old
		
	/*	Double check21 = (double)thresholds.activities.elementAt(i).pri2t1t/(thresholds.activities.elementAt(i).pri2t1t+thresholds.activities.elementAt(i).pri2t1f);
		Double check22 = (double)thresholds.activities.elementAt(i).pri2t2t/(thresholds.activities.elementAt(i).pri2t2t+thresholds.activities.elementAt(i).pri2t2f);
		Double check23 = (double)thresholds.activities.elementAt(i).pri2t3t/(thresholds.activities.elementAt(i).pri2t3t+thresholds.activities.elementAt(i).pri2t3f);
	
		
				if(thresholds.activities.elementAt(i).pri2t1t+thresholds.activities.elementAt(i).pri2t1f != 0)	
				{if (check21 >= p)
				{thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+dev1*thresholds.activities.elementAt(i).PRI2dev;}
				else{if(thresholds.activities.elementAt(i).pri2t2t+thresholds.activities.elementAt(i).pri2t2f != 0)	
					if(check22 >= p)
					{thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+dev2*thresholds.activities.elementAt(i).PRI2dev;}
						else{if(thresholds.activities.elementAt(i).pri2t3t+thresholds.activities.elementAt(i).pri2t3f != 0)	
							if(check23 >= p)
							{thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+dev3*thresholds.activities.elementAt(i).PRI2dev;}
							}
					}
					
				}
	*/			
		// PRI 2 - new
		
				for(int j=0; j<thresholds.stdDev.size(); j++)
					thresholds.activities.elementAt(i).pri2fraction.set(j, (double)thresholds.activities.elementAt(i).pri2true.elementAt(j)/(thresholds.activities.elementAt(i).pri2true.elementAt(j)+thresholds.activities.elementAt(i).pri2false.elementAt(j)));
				
				for(int j=0; j<thresholds.stdDev.size(); j++)
				{
					if(thresholds.activities.elementAt(i).pri2true.elementAt(j)+thresholds.activities.elementAt(i).pri2false.elementAt(j) != 0 && thresholds.activities.elementAt(i).pri2fraction.elementAt(j) >=p)	
					{
						thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+thresholds.stdDev.elementAt(j)*thresholds.activities.elementAt(i).PRI2dev;
					break;
					}
				}

				
		//-----------------------
				
				// PRI 3 - old
				
	/*			Double check31 = (double)thresholds.activities.elementAt(i).pri3t1t/(thresholds.activities.elementAt(i).pri3t1t+thresholds.activities.elementAt(i).pri3t1f);
				Double check32 = (double)thresholds.activities.elementAt(i).pri3t2t/(thresholds.activities.elementAt(i).pri3t2t+thresholds.activities.elementAt(i).pri3t2f);
				Double check33 = (double)thresholds.activities.elementAt(i).pri3t3t/(thresholds.activities.elementAt(i).pri3t3t+thresholds.activities.elementAt(i).pri3t3f);
			
				
				if(thresholds.activities.elementAt(i).pri3t1t+thresholds.activities.elementAt(i).pri3t1f != 0)	
				{if (check31 >= p)
				{thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+dev1*thresholds.activities.elementAt(i).PRI3dev;}
				else{if(thresholds.activities.elementAt(i).pri3t2t+thresholds.activities.elementAt(i).pri3t2f != 0)	
					if(check32 >= p)
					{thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+dev2*thresholds.activities.elementAt(i).PRI3dev;}
						else{if(thresholds.activities.elementAt(i).pri3t3t+thresholds.activities.elementAt(i).pri3t3f != 0)	
							if(check33 >= p)
							{thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+dev3*thresholds.activities.elementAt(i).PRI3dev;}
							}
					}
					
				}
*/				
				
				//PRI 3 - new
				
				for(int j=0; j<thresholds.stdDev.size(); j++)
					thresholds.activities.elementAt(i).pri3fraction.set(j, (double)thresholds.activities.elementAt(i).pri3true.elementAt(j)/(thresholds.activities.elementAt(i).pri3true.elementAt(j)+thresholds.activities.elementAt(i).pri3false.elementAt(j)));
				
				for(int j=0; j<thresholds.stdDev.size(); j++)
				{
					if(thresholds.activities.elementAt(i).pri3true.elementAt(j)+thresholds.activities.elementAt(i).pri3false.elementAt(j) != 0 && thresholds.activities.elementAt(i).pri3fraction.elementAt(j) >=p)	
					{
						thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+thresholds.stdDev.elementAt(j)*thresholds.activities.elementAt(i).PRI3dev;
					break;
					}
				}

		//-----------------------
//PRI 6 - old
	/*			Double check61 = (double)thresholds.activities.elementAt(i).pri6t1t/(thresholds.activities.elementAt(i).pri6t1t+thresholds.activities.elementAt(i).pri6t1f);
				Double check62 = (double)thresholds.activities.elementAt(i).pri6t2t/(thresholds.activities.elementAt(i).pri6t2t+thresholds.activities.elementAt(i).pri6t2f);
				Double check63 = (double)thresholds.activities.elementAt(i).pri6t3t/(thresholds.activities.elementAt(i).pri6t3t+thresholds.activities.elementAt(i).pri6t3f);
			
				
				if(thresholds.activities.elementAt(i).pri6t1t+thresholds.activities.elementAt(i).pri6t1f != 0)	
				{if (check61 >= p)
				{thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+dev1*thresholds.activities.elementAt(i).PRI6dev;}
				else{if(thresholds.activities.elementAt(i).pri6t2t+thresholds.activities.elementAt(i).pri6t2f != 0)	
					if(check62 >= p)
					{thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+dev2*thresholds.activities.elementAt(i).PRI6dev;}
						else{if(thresholds.activities.elementAt(i).pri6t3t+thresholds.activities.elementAt(i).pri6t3f != 0)	
							if(check63 >= p)
							{thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+dev3*thresholds.activities.elementAt(i).PRI6dev;}
							}
					}
					
				}
*/				
//PRI 6 - new
				
				for(int j=0; j<thresholds.stdDev.size(); j++)
					thresholds.activities.elementAt(i).pri6fraction.set(j, (double)thresholds.activities.elementAt(i).pri6true.elementAt(j)/(thresholds.activities.elementAt(i).pri6true.elementAt(j)+thresholds.activities.elementAt(i).pri6false.elementAt(j)));
			
				for(int j=0; j<thresholds.stdDev.size(); j++)
				{
					if(thresholds.activities.elementAt(i).pri6true.elementAt(j)+thresholds.activities.elementAt(i).pri6false.elementAt(j) != 0 && thresholds.activities.elementAt(i).pri6fraction.elementAt(j) >=p)	
					{
						thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+thresholds.stdDev.elementAt(j)*thresholds.activities.elementAt(i).PRI6dev;
					break;
					}
				}

		//-----------------------
//PRI 4
				
				if(thresholds.activities.elementAt(i).pri4t+thresholds.activities.elementAt(i).pri4f != 0)	
				{if ((double)thresholds.activities.elementAt(i).pri4t/(thresholds.activities.elementAt(i).pri4t+thresholds.activities.elementAt(i).pri4f) >= p)
				{thresholds.activities.elementAt(i).PRI4 = true;}
					
				}
		
		
		
	}
	
for (int i=0;i<thresholds.resources.size();i++)
		
	{
		// PRI 9 - old
		
	/*	Double check91 = (double)thresholds.resources.elementAt(i).pri9t1t/(thresholds.resources.elementAt(i).pri9t1t+thresholds.resources.elementAt(i).pri9t1f);
		Double check92 = (double)thresholds.resources.elementAt(i).pri9t2t/(thresholds.resources.elementAt(i).pri9t2t+thresholds.resources.elementAt(i).pri9t2f);
		Double check93 = (double)thresholds.resources.elementAt(i).pri9t3t/(thresholds.resources.elementAt(i).pri9t3t+thresholds.resources.elementAt(i).pri9t3f);
		
		
		if(thresholds.resources.elementAt(i).pri9t1t+thresholds.resources.elementAt(i).pri9t1f != 0)	
		{if (check91 >= p)
		{thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+dev1*thresholds.resources.elementAt(i).PRI9dev;}
		else{if(thresholds.resources.elementAt(i).pri9t2t+thresholds.resources.elementAt(i).pri9t2f != 0)	
			if(check92 >= p)
			{thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+dev2*thresholds.resources.elementAt(i).PRI9dev;}
				else{if(thresholds.resources.elementAt(i).pri9t3t+thresholds.resources.elementAt(i).pri9t3f != 0)	
					if(check93 >= p)
					{thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+dev3*thresholds.resources.elementAt(i).PRI9dev;}
					}
			}
			
		}
*/		
		// PRI 9 - new
		
		for(int j=0; j<thresholds.stdDev.size(); j++)
			thresholds.resources.elementAt(i).pri9fraction.set(j, (double)thresholds.resources.elementAt(i).pri9true.elementAt(j)/(thresholds.resources.elementAt(i).pri9true.elementAt(j)+thresholds.resources.elementAt(i).pri9false.elementAt(j)));
	
		for(int j=0; j<thresholds.stdDev.size(); j++)
		{
			if(thresholds.resources.elementAt(i).pri9true.elementAt(j)+thresholds.resources.elementAt(i).pri9false.elementAt(j) != 0 && thresholds.resources.elementAt(i).pri9fraction.elementAt(j) >=p)	
			{
				thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+thresholds.stdDev.elementAt(j)*thresholds.resources.elementAt(i).PRI9dev;
			break;
			}
		}

		
		
		thresholds.resources.elementAt(i).current_workload = new Long(0);
	
		// PRI 8 - not changed - same old and new
		
		for (int j=0; j<thresholds.resources.elementAt(i).resourceActivities.size();j++)
		{
			if ((thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t+thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8f) != 0)
			{if ((double)thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t/(thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t+thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8f) > p)
				{
				thresholds.resources.elementAt(i).resourceActivities.elementAt(j).PRI8 = true;
				}
			}
		}
		
	}

	return thresholds;
					
			
}


public PriCaseThreshold  getThresholdsFixedWithModel(UIPluginContext context, XLog trainlog, PriCaseThreshold thresholds, Double p, PetrinetGraph net) {
	
	Basic basic = new Basic();
	PriUtils priUtils = new PriUtils();
	
	//Double dev1 = 1.00;
	//Double dev2 = 2.00;
	//Double dev3 = 3.00;
	
	Double PRI5av = thresholds.PRI5av;
	Double PRI5dev = thresholds.PRI5dev;
	Double PRI7av = thresholds.PRI7av;
	Double PRI7dev = thresholds.PRI7dev;
	
	for (XTrace t : trainlog) {
		
		XAttributeMap am = t.getAttributes();
		String outcome = am.get("outcome:case_duration").toString();
				
		ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();	
		ConcurrentSkipListSet<String> activities = new ConcurrentSkipListSet<String>();
		ConcurrentSkipListSet<String> repeated_activities = new ConcurrentSkipListSet<String>();	
		ConcurrentSkipListSet<String> risky_activities = new ConcurrentSkipListSet<String>();
		
	for (XEvent e : t) 
		{
		XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
		String eventName = XConceptExtension.instance().extractName(event);
		String lifecycle = XLifecycleExtension.instance().extractTransition(event);
		String resource = XOrganizationalExtension.instance().extractResource(event);
		
		// PRI 5
		
		if(resource != null)
		{resources.add(resource);}
			
		// PRI 7
		
		if(eventName != null)
		{activities.add(eventName);}
		
		// updating current workload for PRI 9
		
		int resIndex = thresholds.getResourceIndex(resource);
		
			if(lifecycle.equalsIgnoreCase("start"))
			{
				thresholds.resources.elementAt(resIndex).current_workload+=1;
			}
			
			if(lifecycle.equalsIgnoreCase("complete"))
			{
				thresholds.resources.elementAt(resIndex).current_workload-=1;
			}
		
		// ------------------------
		
		
		if (lifecycle.equalsIgnoreCase("complete")){
									
			
			XAttributeDiscrete act_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
			long actDuration = act_duration.getValue();
			
			// PRI 1 - old
	/*		if (actDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t1f+=1;}
			}
			
			if (actDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t2f+=1;}
			}
			
			if (actDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t3f+=1;}
			}
*/			
			// PRI 1 - new
			for(int i=0; i<thresholds.stdDev.size();  i++)
			{
				if (actDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av +thresholds.stdDev.elementAt(i)*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1dev)) 					
					{
						if (outcome.equals("true"))
							thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1true.set(i, thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1true.elementAt(i)+1);
						else
							thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1false.set(i, thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1false.elementAt(i)+1);
					}
			}
			
			
			// PRI 3 - old
	/*		if(!repeated_activities.contains(eventName) && (basic.Index(t, e)+1) > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3dev)) 					
			{repeated_activities.add(eventName);
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t1f+=1;}
			}
			
			if((basic.Index(t, e)+1) > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t2f+=1;}
			}	
			
			if((basic.Index(t, e)+1) > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t3f+=1;}
			}	
*/			
			// PRI 3 - new
			
			if(!repeated_activities.contains(eventName))
				repeated_activities.add(eventName);
			
			for(int i=0; i<thresholds.stdDev.size(); i++)
			{	
				if((basic.Index(t, e)+1) > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3av + thresholds.stdDev.elementAt(i)*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3dev)) 					
				{
					if (outcome.equals("true"))
						thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3true.set(i, thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3true.elementAt(i)+1);
					else
						thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3false.set(i, thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3false.elementAt(i)+1);
				}
			
			}
						
			// PRI 4 - not changed, old and new are the same
			if(!risky_activities.contains(eventName))
			{risky_activities.add(eventName);
			if (outcome.equals("true"))					
			{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri4t+=1;}
			else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri4f+=1;};
			}
			
			
			XAttributeDiscrete subproc_duration = (XAttributeDiscrete)event.getAttributes().get("subprocess:duration");
			long subProcDuration = subproc_duration.getValue();
			// PRI 6 - old
		/*	if (subProcDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t1f+=1;}
			}
		
			if (subProcDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t2f+=1;}
			}
			
			if (subProcDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t3f+=1;}
			}
	*/		
			// PRI 6 - new
			
			for(int i=0; i<thresholds.stdDev.size(); i++)
			{
				if (subProcDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6av + thresholds.stdDev.elementAt(i)*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6dev)) 					
				{
					if (outcome.equals("true"))
						thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6true.set(i, thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6true.elementAt(i)+1);
					else
						thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6false.set(i, thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6false.elementAt(i)+1);
				}
			}
	
			
	// PRI 8 - not changed - old and new are the same
			
			//Double activityAverage = thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av;
			Double resourceActivityAverage = thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).resourceActivities.elementAt(thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).getIndex(eventName)).PRI8av;
				if ( resourceActivityAverage > -1 /*activityAverage*/ ) 					
					{
					if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).resourceActivities.elementAt(thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).getIndex(eventName)).pri8t+=1;}
					else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).resourceActivities.elementAt(thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).getIndex(eventName)).pri8f+=1;}
					}
	
		}
		
		if (lifecycle.equalsIgnoreCase("start") && t.indexOf(e)!=0){
			
			
			Date eventTime = XTimeExtension.instance().extractTimestamp(e);
			//Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.PrevPRI2(t,e));
			Date prevEventTime = XTimeExtension.instance().extractTimestamp(priUtils.PrevPRI2(t,e,net));
			long waitDuration = eventTime.getTime()-prevEventTime.getTime();
			
			// PRI 2 - old
		/*	if (waitDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t1f+=1;}
			}
			
			if (waitDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t2f+=1;}
			}
			
			if (waitDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t3f+=1;}
			}
*/			
			//PRI2 - new
			for(int i=0; i<thresholds.stdDev.size(); i++)
			{
				if (waitDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2av + thresholds.stdDev.elementAt(i)*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2dev)) 					
				{
					if (outcome.equals("true"))
						thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2true.set(i, thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2true.elementAt(i)+1);
					else
						thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2false.set(i, thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2false.elementAt(i)+1);
				}
			}
		
		}
		
		if (lifecycle.equalsIgnoreCase("start")){
			
			// PRI 9 - old
		/*	if (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload > (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9av + dev1*thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9dev)) 					
			{
				if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t1t+=1;}
				else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t1f+=1;}
			}
			
			if (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload > (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9av + dev2*thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9dev)) 					
			{
				if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t2t+=1;}
				else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t2f+=1;}
			}
			
			if (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload > (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9av + dev3*thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9dev)) 					
			{
				if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t3t+=1;}
				else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t3f+=1;}
			}
		*/	
			//PRI9 - new
			
			for(int i=0; i<thresholds.stdDev.size(); i++)
			{
				if (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload > (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9av + thresholds.stdDev.elementAt(i)*thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9dev)) 					
				{
					if (outcome.equals("true"))
						thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9true.set(i, thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9true.elementAt(i)+1);
					else
						thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9false.set(i, thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9false.elementAt(i)+1);
				}
			}
	
			
			
	}
}
	
	// ------------------------------------
	
	
	long resNum = resources.size();
	
	// PRI 5 - old
/*	if (resNum > (PRI5av+dev1*PRI5dev))					
	{
		if (outcome.equals("true")){thresholds.pri5t1t+=1;}else{thresholds.pri5t1f+=1;}
	}
	
	if (resNum > (PRI5av+dev2*PRI5dev))					
	{
		if (outcome.equals("true")){thresholds.pri5t2t+=1;}else{thresholds.pri5t2f+=1;}
	}
	
	if (resNum > (PRI5av+dev3*PRI5dev))					
	{
		if (outcome.equals("true")){thresholds.pri5t3t+=1;}else{thresholds.pri5t3f+=1;}
	}
*/	
	// PRI 5 - new
		for(int i=0; i<thresholds.stdDev.size(); i++)
		{
			if (resNum > (PRI5av+thresholds.stdDev.elementAt(i)*PRI5dev))					
			{
				if (outcome.equals("true"))
					thresholds.pri5true.set(i, thresholds.pri5true.elementAt(i)+1);
				else
					thresholds.pri5false.set(i, thresholds.pri5false.elementAt(i)+1);
			}
		}
	
	
	long actNum = activities.size();	
	
	// PRI 7 - old
/*	if (actNum > (PRI7av+dev1*PRI7dev))					
	{
		if (outcome.equals("true")){thresholds.pri7t1t+=1;}else{thresholds.pri7t1f+=1;}
	}
	
	if (actNum > (PRI7av+dev2*PRI7dev))					
	{
		if (outcome.equals("true")){thresholds.pri7t2t+=1;}else{thresholds.pri7t2f+=1;}
	}
	
	if (actNum > (PRI7av+dev3*PRI7dev))					
	{
		if (outcome.equals("true")){thresholds.pri7t3t+=1;}else{thresholds.pri7t3f+=1;}
	}
*/	
	//PRI 7 - new
	
	for(int i=0; i<thresholds.stdDev.size(); i++)
	{
		if (actNum > (PRI7av+thresholds.stdDev.elementAt(i)*PRI7dev))					
		{
			if (outcome.equals("true"))
				thresholds.pri7true.set(i, thresholds.pri7true.elementAt(i)+1);
			else
				thresholds.pri7false.set(i, thresholds.pri7false.elementAt(i)+1);
		}
	}

	
	
}		
		

//------------------------

/*	//PRI5 - old
	Double check51 = (double)thresholds.pri5t1t/(thresholds.pri5t1t+thresholds.pri5t1f);
	Double check52 = (double)thresholds.pri5t2t/(thresholds.pri5t2t+thresholds.pri5t2f);
	Double check53 = (double)thresholds.pri5t3t/(thresholds.pri5t3t+thresholds.pri5t3f);
	
	
	// PRI5
	if(thresholds.pri5t1t+thresholds.pri5t1f != 0)	
	{if (check51 >= p)
	{thresholds.PRI5threshold = thresholds.PRI5av+dev1*thresholds.PRI5dev;}
	else{if(thresholds.pri5t2t+thresholds.pri5t2f != 0)	
		if(check52 >= p)
		{thresholds.PRI5threshold = thresholds.PRI5av+dev2*thresholds.PRI5dev;}
			else{if(thresholds.pri5t3t+thresholds.pri5t3f != 0)	
				if(check53 >= p)
				{thresholds.PRI5threshold = thresholds.PRI5av+dev3*thresholds.PRI5dev;}
				}
		}
		
	}
*/	
	//PRI5 - new
	for(int i=0; i<thresholds.stdDev.size(); i++)
		thresholds.pri5fraction.set(i,(double)thresholds.pri5true.elementAt(i)/(thresholds.pri5true.elementAt(i)+thresholds.pri5false.elementAt(i)));
	
	for(int i=0; i<thresholds.stdDev.size(); i++)
	{
		if(thresholds.pri5true.elementAt(i)+thresholds.pri5false.elementAt(i) != 0 && thresholds.pri5fraction.elementAt(i) >=p)	
		{
		thresholds.PRI5threshold = thresholds.PRI5av+thresholds.stdDev.elementAt(i)*thresholds.PRI5dev;
		break;
		}
	}
	
	//------------------------
	// PRI7 - old

/*	Double check71 = (double)thresholds.pri7t1t/(thresholds.pri7t1t+thresholds.pri7t1f);
	Double check72 = (double)thresholds.pri7t2t/(thresholds.pri7t2t+thresholds.pri7t2f);
	Double check73 = (double)thresholds.pri7t3t/(thresholds.pri7t3t+thresholds.pri7t3f);

	
	if(thresholds.pri7t1t+thresholds.pri7t1f != 0)	
	{if (check71 >= p)
	{thresholds.PRI7threshold = thresholds.PRI7av+dev1*thresholds.PRI7dev;}
	else{if(thresholds.pri7t2t+thresholds.pri7t2f != 0)	
		if(check72 >= p)
		{thresholds.PRI7threshold = thresholds.PRI7av+dev2*thresholds.PRI7dev;}
			else{if(thresholds.pri7t3t+thresholds.pri7t3f != 0)	
				if(check73 >= p)
				{thresholds.PRI7threshold = thresholds.PRI7av+dev3*thresholds.PRI7dev;}
				}
		}
		
	}
*/	
	//PRI 7 - new
	
	for(int i=0; i<thresholds.stdDev.size(); i++)
		thresholds.pri7fraction.set(i,(double)thresholds.pri7true.elementAt(i)/(thresholds.pri7true.elementAt(i)+thresholds.pri7false.elementAt(i)));
	
	for(int i=0; i<thresholds.stdDev.size(); i++)
	{
		if(thresholds.pri7true.elementAt(i)+thresholds.pri7false.elementAt(i) != 0 && thresholds.pri7fraction.elementAt(i) >=p)	
		{
		thresholds.PRI7threshold = thresholds.PRI7av+thresholds.stdDev.elementAt(i)*thresholds.PRI7dev;
		break;
		}
	}


//-----------------
	
		
	for (int i=0;i<thresholds.activities.size();i++)
		
	{
		// PRI 1 - old
		
	/*	Double check11 = (double)thresholds.activities.elementAt(i).pri1t1t/(thresholds.activities.elementAt(i).pri1t1t+thresholds.activities.elementAt(i).pri1t1f);
		Double check12 = (double)thresholds.activities.elementAt(i).pri1t2t/(thresholds.activities.elementAt(i).pri1t2t+thresholds.activities.elementAt(i).pri1t2f);
		Double check13 = (double)thresholds.activities.elementAt(i).pri1t3t/(thresholds.activities.elementAt(i).pri1t3t+thresholds.activities.elementAt(i).pri1t3f);
		
		
		if(thresholds.activities.elementAt(i).pri1t1t+thresholds.activities.elementAt(i).pri1t1f != 0)	
		{if (check11 >= p)
		{thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+dev1*thresholds.activities.elementAt(i).PRI1dev;}
		else{if(thresholds.activities.elementAt(i).pri1t2t+thresholds.activities.elementAt(i).pri1t2f != 0)	
			if(check12 >= p)
			{thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+dev2*thresholds.activities.elementAt(i).PRI1dev;}
				else{if(thresholds.activities.elementAt(i).pri1t3t+thresholds.activities.elementAt(i).pri1t3f != 0)	
					if(check13 >= p)
					{thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+dev3*thresholds.activities.elementAt(i).PRI1dev;}
					}
			}
			
		}
*/		
		//PRI 1 - new
		
		for(int j=0; j<thresholds.stdDev.size(); j++)
			thresholds.activities.elementAt(i).pri1fraction.set(j, (double)thresholds.activities.elementAt(i).pri1true.elementAt(j)/(thresholds.activities.elementAt(i).pri1true.elementAt(j)+thresholds.activities.elementAt(i).pri1false.elementAt(j)));
		
		for(int j=0; j<thresholds.stdDev.size(); j++)
		{
			if(thresholds.activities.elementAt(i).pri1true.elementAt(j)+thresholds.activities.elementAt(i).pri1false.elementAt(j) != 0 && thresholds.activities.elementAt(i).pri1fraction.elementAt(j) >=p)	
			{
				thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+thresholds.stdDev.elementAt(j)*thresholds.activities.elementAt(i).PRI1dev;
			break;
			}
		}

		
		//-----------------------
		
		// PRI 2 - old
		
	/*	Double check21 = (double)thresholds.activities.elementAt(i).pri2t1t/(thresholds.activities.elementAt(i).pri2t1t+thresholds.activities.elementAt(i).pri2t1f);
		Double check22 = (double)thresholds.activities.elementAt(i).pri2t2t/(thresholds.activities.elementAt(i).pri2t2t+thresholds.activities.elementAt(i).pri2t2f);
		Double check23 = (double)thresholds.activities.elementAt(i).pri2t3t/(thresholds.activities.elementAt(i).pri2t3t+thresholds.activities.elementAt(i).pri2t3f);
	
		
				if(thresholds.activities.elementAt(i).pri2t1t+thresholds.activities.elementAt(i).pri2t1f != 0)	
				{if (check21 >= p)
				{thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+dev1*thresholds.activities.elementAt(i).PRI2dev;}
				else{if(thresholds.activities.elementAt(i).pri2t2t+thresholds.activities.elementAt(i).pri2t2f != 0)	
					if(check22 >= p)
					{thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+dev2*thresholds.activities.elementAt(i).PRI2dev;}
						else{if(thresholds.activities.elementAt(i).pri2t3t+thresholds.activities.elementAt(i).pri2t3f != 0)	
							if(check23 >= p)
							{thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+dev3*thresholds.activities.elementAt(i).PRI2dev;}
							}
					}
					
				}
	*/			
		// PRI 2 - new
		
				for(int j=0; j<thresholds.stdDev.size(); j++)
					thresholds.activities.elementAt(i).pri2fraction.set(j, (double)thresholds.activities.elementAt(i).pri2true.elementAt(j)/(thresholds.activities.elementAt(i).pri2true.elementAt(j)+thresholds.activities.elementAt(i).pri2false.elementAt(j)));
				
				for(int j=0; j<thresholds.stdDev.size(); j++)
				{
					if(thresholds.activities.elementAt(i).pri2true.elementAt(j)+thresholds.activities.elementAt(i).pri2false.elementAt(j) != 0 && thresholds.activities.elementAt(i).pri2fraction.elementAt(j) >=p)	
					{
						thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+thresholds.stdDev.elementAt(j)*thresholds.activities.elementAt(i).PRI2dev;
					break;
					}
				}

				
		//-----------------------
				
				// PRI 3 - old
				
	/*			Double check31 = (double)thresholds.activities.elementAt(i).pri3t1t/(thresholds.activities.elementAt(i).pri3t1t+thresholds.activities.elementAt(i).pri3t1f);
				Double check32 = (double)thresholds.activities.elementAt(i).pri3t2t/(thresholds.activities.elementAt(i).pri3t2t+thresholds.activities.elementAt(i).pri3t2f);
				Double check33 = (double)thresholds.activities.elementAt(i).pri3t3t/(thresholds.activities.elementAt(i).pri3t3t+thresholds.activities.elementAt(i).pri3t3f);
			
				
				if(thresholds.activities.elementAt(i).pri3t1t+thresholds.activities.elementAt(i).pri3t1f != 0)	
				{if (check31 >= p)
				{thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+dev1*thresholds.activities.elementAt(i).PRI3dev;}
				else{if(thresholds.activities.elementAt(i).pri3t2t+thresholds.activities.elementAt(i).pri3t2f != 0)	
					if(check32 >= p)
					{thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+dev2*thresholds.activities.elementAt(i).PRI3dev;}
						else{if(thresholds.activities.elementAt(i).pri3t3t+thresholds.activities.elementAt(i).pri3t3f != 0)	
							if(check33 >= p)
							{thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+dev3*thresholds.activities.elementAt(i).PRI3dev;}
							}
					}
					
				}
*/				
				
				//PRI 3 - new
				
				for(int j=0; j<thresholds.stdDev.size(); j++)
					thresholds.activities.elementAt(i).pri3fraction.set(j, (double)thresholds.activities.elementAt(i).pri3true.elementAt(j)/(thresholds.activities.elementAt(i).pri3true.elementAt(j)+thresholds.activities.elementAt(i).pri3false.elementAt(j)));
				
				for(int j=0; j<thresholds.stdDev.size(); j++)
				{
					if(thresholds.activities.elementAt(i).pri3true.elementAt(j)+thresholds.activities.elementAt(i).pri3false.elementAt(j) != 0 && thresholds.activities.elementAt(i).pri3fraction.elementAt(j) >=p)	
					{
						thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+thresholds.stdDev.elementAt(j)*thresholds.activities.elementAt(i).PRI3dev;
					break;
					}
				}

		//-----------------------
//PRI 6 - old
	/*			Double check61 = (double)thresholds.activities.elementAt(i).pri6t1t/(thresholds.activities.elementAt(i).pri6t1t+thresholds.activities.elementAt(i).pri6t1f);
				Double check62 = (double)thresholds.activities.elementAt(i).pri6t2t/(thresholds.activities.elementAt(i).pri6t2t+thresholds.activities.elementAt(i).pri6t2f);
				Double check63 = (double)thresholds.activities.elementAt(i).pri6t3t/(thresholds.activities.elementAt(i).pri6t3t+thresholds.activities.elementAt(i).pri6t3f);
			
				
				if(thresholds.activities.elementAt(i).pri6t1t+thresholds.activities.elementAt(i).pri6t1f != 0)	
				{if (check61 >= p)
				{thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+dev1*thresholds.activities.elementAt(i).PRI6dev;}
				else{if(thresholds.activities.elementAt(i).pri6t2t+thresholds.activities.elementAt(i).pri6t2f != 0)	
					if(check62 >= p)
					{thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+dev2*thresholds.activities.elementAt(i).PRI6dev;}
						else{if(thresholds.activities.elementAt(i).pri6t3t+thresholds.activities.elementAt(i).pri6t3f != 0)	
							if(check63 >= p)
							{thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+dev3*thresholds.activities.elementAt(i).PRI6dev;}
							}
					}
					
				}
*/				
//PRI 6 - new
				
				for(int j=0; j<thresholds.stdDev.size(); j++)
					thresholds.activities.elementAt(i).pri6fraction.set(j, (double)thresholds.activities.elementAt(i).pri6true.elementAt(j)/(thresholds.activities.elementAt(i).pri6true.elementAt(j)+thresholds.activities.elementAt(i).pri6false.elementAt(j)));
			
				for(int j=0; j<thresholds.stdDev.size(); j++)
				{
					if(thresholds.activities.elementAt(i).pri6true.elementAt(j)+thresholds.activities.elementAt(i).pri6false.elementAt(j) != 0 && thresholds.activities.elementAt(i).pri6fraction.elementAt(j) >=p)	
					{
						thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+thresholds.stdDev.elementAt(j)*thresholds.activities.elementAt(i).PRI6dev;
					break;
					}
				}

		//-----------------------
//PRI 4
				
				if(thresholds.activities.elementAt(i).pri4t+thresholds.activities.elementAt(i).pri4f != 0)	
				{if ((double)thresholds.activities.elementAt(i).pri4t/(thresholds.activities.elementAt(i).pri4t+thresholds.activities.elementAt(i).pri4f) >= p)
				{thresholds.activities.elementAt(i).PRI4 = true;}
					
				}
		
		
		
	}
	
for (int i=0;i<thresholds.resources.size();i++)
		
	{
		// PRI 9 - old
		
	/*	Double check91 = (double)thresholds.resources.elementAt(i).pri9t1t/(thresholds.resources.elementAt(i).pri9t1t+thresholds.resources.elementAt(i).pri9t1f);
		Double check92 = (double)thresholds.resources.elementAt(i).pri9t2t/(thresholds.resources.elementAt(i).pri9t2t+thresholds.resources.elementAt(i).pri9t2f);
		Double check93 = (double)thresholds.resources.elementAt(i).pri9t3t/(thresholds.resources.elementAt(i).pri9t3t+thresholds.resources.elementAt(i).pri9t3f);
		
		
		if(thresholds.resources.elementAt(i).pri9t1t+thresholds.resources.elementAt(i).pri9t1f != 0)	
		{if (check91 >= p)
		{thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+dev1*thresholds.resources.elementAt(i).PRI9dev;}
		else{if(thresholds.resources.elementAt(i).pri9t2t+thresholds.resources.elementAt(i).pri9t2f != 0)	
			if(check92 >= p)
			{thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+dev2*thresholds.resources.elementAt(i).PRI9dev;}
				else{if(thresholds.resources.elementAt(i).pri9t3t+thresholds.resources.elementAt(i).pri9t3f != 0)	
					if(check93 >= p)
					{thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+dev3*thresholds.resources.elementAt(i).PRI9dev;}
					}
			}
			
		}
*/		
		// PRI 9 - new
		
		for(int j=0; j<thresholds.stdDev.size(); j++)
			thresholds.resources.elementAt(i).pri9fraction.set(j, (double)thresholds.resources.elementAt(i).pri9true.elementAt(j)/(thresholds.resources.elementAt(i).pri9true.elementAt(j)+thresholds.resources.elementAt(i).pri9false.elementAt(j)));
	
		for(int j=0; j<thresholds.stdDev.size(); j++)
		{
			if(thresholds.resources.elementAt(i).pri9true.elementAt(j)+thresholds.resources.elementAt(i).pri9false.elementAt(j) != 0 && thresholds.resources.elementAt(i).pri9fraction.elementAt(j) >=p)	
			{
				thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+thresholds.stdDev.elementAt(j)*thresholds.resources.elementAt(i).PRI9dev;
			break;
			}
		}

		
		
		thresholds.resources.elementAt(i).current_workload = new Long(0);
	
		// PRI 8 - not changed - same old and new
		
		for (int j=0; j<thresholds.resources.elementAt(i).resourceActivities.size();j++)
		{
			if ((thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t+thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8f) != 0)
			{if ((double)thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t/(thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t+thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8f) > p)
				{
				thresholds.resources.elementAt(i).resourceActivities.elementAt(j).PRI8 = true;
				}
			}
		}
		
	}

	return thresholds;
					
			
}


}

//////////////////////////////////////////////////PREV. VERSIONS////////////////////////////////////////////////////////////////////////////
/*
public PriCaseThreshold  getThresholdsNoConfiguration(UIPluginContext context, XLog trainlog, PriCaseThreshold thresholds, Double p1, Double p2, Double p3, Double p4, Double p5, Double p6, Double p7, Double p8, Double p9) {
	
	Basic basic = new Basic();
	
	Double dev1 = 1.00;
	Double dev2 = 2.00;
	Double dev3 = 3.00;
		
	Double PRI5av = thresholds.PRI5av;
	Double PRI5dev = thresholds.PRI5dev;
	Double PRI7av = thresholds.PRI7av;
	Double PRI7dev = thresholds.PRI7dev;
	
	for (XTrace t : trainlog) {
		
		XAttributeMap am = t.getAttributes();
		String outcome = am.get("outcome:case_duration").toString();
				
		ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();	
		ConcurrentSkipListSet<String> activities = new ConcurrentSkipListSet<String>();
		ConcurrentSkipListSet<String> repeated_activities = new ConcurrentSkipListSet<String>();	
		ConcurrentSkipListSet<String> risky_activities = new ConcurrentSkipListSet<String>();
	
																 
	for (XEvent e : t) 
		{
		XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
		String eventName = XConceptExtension.instance().extractName(event);
		String lifecycle = XLifecycleExtension.instance().extractTransition(event);
		String resource = XOrganizationalExtension.instance().extractResource(event);
		
		// PRI 5
		
		if(resource != null)
		{resources.add(resource);}
			
		// PRI 7
		
		if(eventName != null)
		{activities.add(eventName);}
		
		// updating current workload for PRI 9
		
			
		int resIndex = thresholds.getResourceIndex(resource);
		
			if(lifecycle.equalsIgnoreCase("start"))
			{
				thresholds.resources.elementAt(resIndex).current_workload+=1;
			}
			
			if(lifecycle.equalsIgnoreCase("complete"))
			{
				thresholds.resources.elementAt(resIndex).current_workload-=1;
			}
			
		
		// ------------------------
		
		
		if (lifecycle.equalsIgnoreCase("complete")){
									
			// PRI 1
			XAttributeDiscrete act_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
			long actDuration = act_duration.getValue();
			if (actDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t1f+=1;}
			}
			
			if (actDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t2f+=1;}
			}
			
			if (actDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t3f+=1;}
			}
			
			// PRI 3
			if(!repeated_activities.contains(eventName) && (basic.Index(t, e)+1) > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3dev)) 					
			{repeated_activities.add(eventName);
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t1f+=1;}
			}
			
			if((basic.Index(t, e)+1) > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t2f+=1;}
			}	
			
			if((basic.Index(t, e)+1) > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t3f+=1;}
			}	
			
						
			// PRI 4
			if(!risky_activities.contains(eventName))
			{risky_activities.add(eventName);
			if (outcome.equals("true"))					
			{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri4t+=1;}
			else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri4f+=1;};
			}
						
			// PRI 6
			XAttributeDiscrete subproc_duration = (XAttributeDiscrete)event.getAttributes().get("subprocess:duration");
			long subProcDuration = subproc_duration.getValue();
			if (subProcDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t1f+=1;}
			}
		
			if (subProcDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t2f+=1;}
			}
			
			if (subProcDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t3f+=1;}
			}
			
	// PRI 8
			
			//Double activityAverage = thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av;
			Double resourceActivityAverage = thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).resourceActivities.elementAt(thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).getIndex(eventName)).PRI8av;
				if ( resourceActivityAverage > -1 //activityAverage ) 					
					{
					if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).resourceActivities.elementAt(thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).getIndex(eventName)).pri8t+=1;}
					else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).resourceActivities.elementAt(thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).getIndex(eventName)).pri8f+=1;}
					}
	
		}
		
		if (lifecycle.equalsIgnoreCase("start") && t.indexOf(e)!=0){
			
			// PRI 2
			Date eventTime = XTimeExtension.instance().extractTimestamp(e);
			Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.PrevPRI2(t,e));
			long waitDuration = eventTime.getTime()-prevEventTime.getTime();
			if (waitDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t1f+=1;}
			}
			
			if (waitDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t2f+=1;}
			}
			
			if (waitDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t3f+=1;}
			}
		}
		
		if (lifecycle.equalsIgnoreCase("start")){
			
			// PRI 9
			if (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload > (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9av + dev1*thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9dev)) 					
			{
				if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t1t+=1;}
				else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t1f+=1;}
			}
			
			if (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload > (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9av + dev2*thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9dev)) 					
			{
				if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t2t+=1;}
				else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t2f+=1;}
			}
			
			if (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload > (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9av + dev3*thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9dev)) 					
			{
				if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t3t+=1;}
				else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t3f+=1;}
			}
			
	}
}
	
	// ------------------------------------
	
	
	// PRI 5
	long resNum = resources.size();
	if (resNum > (PRI5av+dev1*PRI5dev))					
	{
		if (outcome.equals("true")){thresholds.pri5t1t+=1;}else{thresholds.pri5t1f+=1;}
	}
	
	if (resNum > (PRI5av+dev2*PRI5dev))					
	{
		if (outcome.equals("true")){thresholds.pri5t2t+=1;}else{thresholds.pri5t2f+=1;}
	}
	
	if (resNum > (PRI5av+dev3*PRI5dev))					
	{
		if (outcome.equals("true")){thresholds.pri5t3t+=1;}else{thresholds.pri5t3f+=1;}
	}
	
	// PRI 7
	long actNum = activities.size();	
	if (actNum > (PRI7av+dev1*PRI7dev))					
	{
		if (outcome.equals("true")){thresholds.pri7t1t+=1;}else{thresholds.pri7t1f+=1;}
	}
	
	if (actNum > (PRI7av+dev2*PRI7dev))					
	{
		if (outcome.equals("true")){thresholds.pri7t2t+=1;}else{thresholds.pri7t2f+=1;}
	}
	
	if (actNum > (PRI7av+dev3*PRI7dev))					
	{
		if (outcome.equals("true")){thresholds.pri7t3t+=1;}else{thresholds.pri7t3f+=1;}
	}
	}		
		

// ------------------------

	Double check51 = (double)thresholds.pri5t1t/(thresholds.pri5t1t+thresholds.pri5t1f);
	Double check52 = (double)thresholds.pri5t2t/(thresholds.pri5t2t+thresholds.pri5t2f);
	Double check53 = (double)thresholds.pri5t3t/(thresholds.pri5t3t+thresholds.pri5t3f);
	
	
	// PRI5
	if(thresholds.pri5t1t+thresholds.pri5t1f != 0)	
	{if (check51 >= p5)
	{thresholds.PRI5threshold = thresholds.PRI5av+dev1*thresholds.PRI5dev;}
	else{if(thresholds.pri5t2t+thresholds.pri5t2f != 0)	
		if(check52 >= p5)
		{thresholds.PRI5threshold = thresholds.PRI5av+dev2*thresholds.PRI5dev;}
			else{if(thresholds.pri5t3t+thresholds.pri5t3f != 0)	
				if(check53 >= p5)
				{thresholds.PRI5threshold = thresholds.PRI5av+dev3*thresholds.PRI5dev;}
				}
		}
		
	}
	//------------------------
	// PRI7

	Double check71 = (double)thresholds.pri7t1t/(thresholds.pri7t1t+thresholds.pri7t1f);
	Double check72 = (double)thresholds.pri7t2t/(thresholds.pri7t2t+thresholds.pri7t2f);
	Double check73 = (double)thresholds.pri7t3t/(thresholds.pri7t3t+thresholds.pri7t3f);

	
	if(thresholds.pri7t1t+thresholds.pri7t1f != 0)	
	{if (check71 >= p7)
	{thresholds.PRI7threshold = thresholds.PRI7av+dev1*thresholds.PRI7dev;}
	else{if(thresholds.pri7t2t+thresholds.pri7t2f != 0)	
		if(check72 >= p7)
		{thresholds.PRI7threshold = thresholds.PRI7av+dev2*thresholds.PRI7dev;}
			else{if(thresholds.pri7t3t+thresholds.pri7t3f != 0)	
				if(check73 >= p7)
				{thresholds.PRI7threshold = thresholds.PRI7av+dev3*thresholds.PRI7dev;}
				}
		}
		
	}

// -----------------
	
		
	for (int i=0;i<thresholds.activities.size();i++)
		
	{
		// PRI 1
		
		Double check11 = (double)thresholds.activities.elementAt(i).pri1t1t/(thresholds.activities.elementAt(i).pri1t1t+thresholds.activities.elementAt(i).pri1t1f);
		Double check12 = (double)thresholds.activities.elementAt(i).pri1t2t/(thresholds.activities.elementAt(i).pri1t2t+thresholds.activities.elementAt(i).pri1t2f);
		Double check13 = (double)thresholds.activities.elementAt(i).pri1t3t/(thresholds.activities.elementAt(i).pri1t3t+thresholds.activities.elementAt(i).pri1t3f);
		
		
		if(thresholds.activities.elementAt(i).pri1t1t+thresholds.activities.elementAt(i).pri1t1f != 0)	
		{if (check11 >= p1)
		{thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+dev1*thresholds.activities.elementAt(i).PRI1dev;}
		else{if(thresholds.activities.elementAt(i).pri1t2t+thresholds.activities.elementAt(i).pri1t2f != 0)	
			if(check12 >= p1)
			{thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+dev2*thresholds.activities.elementAt(i).PRI1dev;}
				else{if(thresholds.activities.elementAt(i).pri1t3t+thresholds.activities.elementAt(i).pri1t3f != 0)	
					if(check13 >= p1)
					{thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+dev3*thresholds.activities.elementAt(i).PRI1dev;}
					}
			}
			
		}
		//-----------------------
		
		// PRI 2
		
		Double check21 = (double)thresholds.activities.elementAt(i).pri2t1t/(thresholds.activities.elementAt(i).pri2t1t+thresholds.activities.elementAt(i).pri2t1f);
		Double check22 = (double)thresholds.activities.elementAt(i).pri2t2t/(thresholds.activities.elementAt(i).pri2t2t+thresholds.activities.elementAt(i).pri2t2f);
		Double check23 = (double)thresholds.activities.elementAt(i).pri2t3t/(thresholds.activities.elementAt(i).pri2t3t+thresholds.activities.elementAt(i).pri2t3f);
	
		
				if(thresholds.activities.elementAt(i).pri2t1t+thresholds.activities.elementAt(i).pri2t1f != 0)	
				{if (check21 >= p2)
				{thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+dev1*thresholds.activities.elementAt(i).PRI2dev;}
				else{if(thresholds.activities.elementAt(i).pri2t2t+thresholds.activities.elementAt(i).pri2t2f != 0)	
					if(check22 >= p2)
					{thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+dev2*thresholds.activities.elementAt(i).PRI2dev;}
						else{if(thresholds.activities.elementAt(i).pri2t3t+thresholds.activities.elementAt(i).pri2t3f != 0)	
							if(check23 >= p2)
							{thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+dev3*thresholds.activities.elementAt(i).PRI2dev;}
							}
					}
					
				}
		//-----------------------
				
				// PRI 3
				
				Double check31 = (double)thresholds.activities.elementAt(i).pri3t1t/(thresholds.activities.elementAt(i).pri3t1t+thresholds.activities.elementAt(i).pri3t1f);
				Double check32 = (double)thresholds.activities.elementAt(i).pri3t2t/(thresholds.activities.elementAt(i).pri3t2t+thresholds.activities.elementAt(i).pri3t2f);
				Double check33 = (double)thresholds.activities.elementAt(i).pri3t3t/(thresholds.activities.elementAt(i).pri3t3t+thresholds.activities.elementAt(i).pri3t3f);
			
				
				if(thresholds.activities.elementAt(i).pri3t1t+thresholds.activities.elementAt(i).pri3t1f != 0)	
				{if (check31 >= p3)
				{thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+dev1*thresholds.activities.elementAt(i).PRI3dev;}
				else{if(thresholds.activities.elementAt(i).pri3t2t+thresholds.activities.elementAt(i).pri3t2f != 0)	
					if(check32 >= p3)
					{thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+dev2*thresholds.activities.elementAt(i).PRI3dev;}
						else{if(thresholds.activities.elementAt(i).pri3t3t+thresholds.activities.elementAt(i).pri3t3f != 0)	
							if(check33 >= p3)
							{thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+dev3*thresholds.activities.elementAt(i).PRI3dev;}
							}
					}
					
				}
		//-----------------------
// PRI 6
				Double check61 = (double)thresholds.activities.elementAt(i).pri6t1t/(thresholds.activities.elementAt(i).pri6t1t+thresholds.activities.elementAt(i).pri6t1f);
				Double check62 = (double)thresholds.activities.elementAt(i).pri6t2t/(thresholds.activities.elementAt(i).pri6t2t+thresholds.activities.elementAt(i).pri6t2f);
				Double check63 = (double)thresholds.activities.elementAt(i).pri6t3t/(thresholds.activities.elementAt(i).pri6t3t+thresholds.activities.elementAt(i).pri6t3f);
			
				
				if(thresholds.activities.elementAt(i).pri6t1t+thresholds.activities.elementAt(i).pri6t1f != 0)	
				{if (check61 >= p6)
				{thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+dev1*thresholds.activities.elementAt(i).PRI6dev;}
				else{if(thresholds.activities.elementAt(i).pri6t2t+thresholds.activities.elementAt(i).pri6t2f != 0)	
					if(check62 >= p6)
					{thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+dev2*thresholds.activities.elementAt(i).PRI6dev;}
						else{if(thresholds.activities.elementAt(i).pri6t3t+thresholds.activities.elementAt(i).pri6t3f != 0)	
							if(check63 >= p6)
							{thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+dev3*thresholds.activities.elementAt(i).PRI6dev;}
							}
					}
					
				}
		//-----------------------
// PRI 4
				
				if(thresholds.activities.elementAt(i).pri4t+thresholds.activities.elementAt(i).pri4f != 0)	
				{if ((double)thresholds.activities.elementAt(i).pri4t/(thresholds.activities.elementAt(i).pri4t+thresholds.activities.elementAt(i).pri4f) >= p4)
				{thresholds.activities.elementAt(i).PRI4 = true;}
					
				}
		
		
		
	}
	
for (int i=0;i<thresholds.resources.size();i++)
		
	{
		// PRI 9
		
		Double check91 = (double)thresholds.resources.elementAt(i).pri9t1t/(thresholds.resources.elementAt(i).pri9t1t+thresholds.resources.elementAt(i).pri9t1f);
		Double check92 = (double)thresholds.resources.elementAt(i).pri9t2t/(thresholds.resources.elementAt(i).pri9t2t+thresholds.resources.elementAt(i).pri9t2f);
		Double check93 = (double)thresholds.resources.elementAt(i).pri9t3t/(thresholds.resources.elementAt(i).pri9t3t+thresholds.resources.elementAt(i).pri9t3f);
		
		
		if(thresholds.resources.elementAt(i).pri9t1t+thresholds.resources.elementAt(i).pri9t1f != 0)	
		{if (check91 >= p9)
		{thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+dev1*thresholds.resources.elementAt(i).PRI9dev;}
		else{if(thresholds.resources.elementAt(i).pri9t2t+thresholds.resources.elementAt(i).pri9t2f != 0)	
			if(check92 >= p9)
			{thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+dev2*thresholds.resources.elementAt(i).PRI9dev;}
				else{if(thresholds.resources.elementAt(i).pri9t3t+thresholds.resources.elementAt(i).pri9t3f != 0)	
					if(check93 >= p9)
					{thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+dev3*thresholds.resources.elementAt(i).PRI9dev;}
					}
			}
			
		}
		
		thresholds.resources.elementAt(i).current_workload = new Long(0);
	
		// PRI 8
		
		for (int j=0; j<thresholds.resources.elementAt(i).resourceActivities.size();j++)
		{
			if ((thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t+thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8f) != 0)
			{if ((double)thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t/(thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t+thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8f) > p8)
				{
				thresholds.resources.elementAt(i).resourceActivities.elementAt(j).PRI8 = true;
				}
			}
		}
		
	}

	return thresholds;
					
			
}


public PriCaseThreshold  getThresholdsNoConfigurationFixed(UIPluginContext context, XLog trainlog, PriCaseThreshold thresholds, Double p) {
	
	// PRI 5
	thresholds.PRI5threshold = thresholds.PRI5av+2*thresholds.PRI5dev;
	// PRI 7
	thresholds.PRI7threshold = thresholds.PRI7av+2*thresholds.PRI7dev;
	
for (int i=0;i<thresholds.activities.size();i++)
	{
		// PRI 1
		thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+2*thresholds.activities.elementAt(i).PRI1dev;
		
		// PRI 2
		thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+2*thresholds.activities.elementAt(i).PRI2dev;

		// PRI 3
		thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+2*thresholds.activities.elementAt(i).PRI3dev;

		// PRI 6
		thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+2*thresholds.activities.elementAt(i).PRI6dev;

		// PRI 4
		if(thresholds.activities.elementAt(i).pri4t+thresholds.activities.elementAt(i).pri4f != 0)	
		{if ((double)thresholds.activities.elementAt(i).pri4t/(thresholds.activities.elementAt(i).pri4t+thresholds.activities.elementAt(i).pri4f) >= p)
		{thresholds.activities.elementAt(i).PRI4 = true;}
			
		}

	}

for (int i=0;i<thresholds.resources.size();i++)
{
	// PRI 9
	thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+2*thresholds.resources.elementAt(i).PRI9dev;
	
	// PRI 8
	for (int j=0; j<thresholds.resources.elementAt(i).resourceActivities.size();j++)
	{
		if ((thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t+thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8f) != 0)
		{if ((double)thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t/(thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t+thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8f) > p)
			{
			thresholds.resources.elementAt(i).resourceActivities.elementAt(j).PRI8 = true;
			}
		}
	}


}
	
return thresholds;	
/*	
	
	Double PRI5av = thresholds.PRI5av;
	Double PRI5dev = thresholds.PRI5dev;
	Double PRI7av = thresholds.PRI7av;
	Double PRI7dev = thresholds.PRI7dev;
	
	for (XTrace t : trainlog) {
		
		XAttributeMap am = t.getAttributes();
		String outcome = am.get("outcome:case_duration").toString();
				
		ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();	
		ConcurrentSkipListSet<String> activities = new ConcurrentSkipListSet<String>();
		ConcurrentSkipListSet<String> repeated_activities = new ConcurrentSkipListSet<String>();	
		ConcurrentSkipListSet<String> risky_activities = new ConcurrentSkipListSet<String>();
	
																 
	for (XEvent e : t) 
		{
		XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
		String eventName = XConceptExtension.instance().extractName(event);
		String lifecycle = XLifecycleExtension.instance().extractTransition(event);
		String resource = XOrganizationalExtension.instance().extractResource(event);
		
		// PRI 5
		
		if(resource != null)
		{resources.add(resource);}
			
		// PRI 7
		
		if(eventName != null)
		{activities.add(eventName);}
		
		// updating current workload for PRI 9
		
			
		int resIndex = thresholds.getResourceIndex(resource);
		
			if(lifecycle.equalsIgnoreCase("start"))
			{
				thresholds.resources.elementAt(resIndex).current_workload+=1;
			}
			
			if(lifecycle.equalsIgnoreCase("complete"))
			{
				thresholds.resources.elementAt(resIndex).current_workload-=1;
			}
			
		
		// ------------------------
		
		
		if (lifecycle.equalsIgnoreCase("complete")){
									
			// PRI 1
			XAttributeDiscrete act_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
			long actDuration = act_duration.getValue();
			if (actDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t1f+=1;}
			}
			
			if (actDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t2f+=1;}
			}
			
			if (actDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t3f+=1;}
			}
			
			// PRI 3
			if(!repeated_activities.contains(eventName) && (basic.Index(t, e)+1) > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3dev)) 					
			{repeated_activities.add(eventName);
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t1f+=1;}
			}
			
			if((basic.Index(t, e)+1) > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t2f+=1;}
			}	
			
			if((basic.Index(t, e)+1) > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t3f+=1;}
			}	
			
						
			// PRI 4
			if(!risky_activities.contains(eventName))
			{risky_activities.add(eventName);
			if (outcome.equals("true"))					
			{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri4t+=1;}
			else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri4f+=1;};
			}
						
			// PRI 6
			XAttributeDiscrete subproc_duration = (XAttributeDiscrete)event.getAttributes().get("subprocess:duration");
			long subProcDuration = subproc_duration.getValue();
			if (subProcDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t1f+=1;}
			}
		
			if (subProcDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t2f+=1;}
			}
			
			if (subProcDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t3f+=1;}
			}
			
	// PRI 8
			
			//Double activityAverage = thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av;
			Double resourceActivityAverage = thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).resourceActivities.elementAt(thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).getIndex(eventName)).PRI8av;
				if ( resourceActivityAverage > -1 //activityAverage ) 					
					{
					if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).resourceActivities.elementAt(thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).getIndex(eventName)).pri8t+=1;}
					else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).resourceActivities.elementAt(thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).getIndex(eventName)).pri8f+=1;}
					}
	
		}
		
		if (lifecycle.equalsIgnoreCase("start") && t.indexOf(e)!=0){
			
			// PRI 2
			Date eventTime = XTimeExtension.instance().extractTimestamp(e);
			Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.PrevPRI2(t,e));
			long waitDuration = eventTime.getTime()-prevEventTime.getTime();
			if (waitDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t1f+=1;}
			}
			
			if (waitDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t2f+=1;}
			}
			
			if (waitDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t3f+=1;}
			}
		}
		
		if (lifecycle.equalsIgnoreCase("start")){
			
			// PRI 9
			if (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload > (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9av + dev1*thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9dev)) 					
			{
				if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t1t+=1;}
				else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t1f+=1;}
			}
			
			if (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload > (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9av + dev2*thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9dev)) 					
			{
				if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t2t+=1;}
				else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t2f+=1;}
			}
			
			if (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload > (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9av + dev3*thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9dev)) 					
			{
				if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t3t+=1;}
				else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t3f+=1;}
			}
			
	}
}
	
	// ------------------------------------
	
	
	// PRI 5
	long resNum = resources.size();
	if (resNum > (PRI5av+dev1*PRI5dev))					
	{
		if (outcome.equals("true")){thresholds.pri5t1t+=1;}else{thresholds.pri5t1f+=1;}
	}
	
	if (resNum > (PRI5av+dev2*PRI5dev))					
	{
		if (outcome.equals("true")){thresholds.pri5t2t+=1;}else{thresholds.pri5t2f+=1;}
	}
	
	if (resNum > (PRI5av+dev3*PRI5dev))					
	{
		if (outcome.equals("true")){thresholds.pri5t3t+=1;}else{thresholds.pri5t3f+=1;}
	}
	
	// PRI 7
	long actNum = activities.size();	
	if (actNum > (PRI7av+dev1*PRI7dev))					
	{
		if (outcome.equals("true")){thresholds.pri7t1t+=1;}else{thresholds.pri7t1f+=1;}
	}
	
	if (actNum > (PRI7av+dev2*PRI7dev))					
	{
		if (outcome.equals("true")){thresholds.pri7t2t+=1;}else{thresholds.pri7t2f+=1;}
	}
	
	if (actNum > (PRI7av+dev3*PRI7dev))					
	{
		if (outcome.equals("true")){thresholds.pri7t3t+=1;}else{thresholds.pri7t3f+=1;}
	}
	}		
		

// ------------------------

	Double check51 = (double)thresholds.pri5t1t/(thresholds.pri5t1t+thresholds.pri5t1f);
	Double check52 = (double)thresholds.pri5t2t/(thresholds.pri5t2t+thresholds.pri5t2f);
	Double check53 = (double)thresholds.pri5t3t/(thresholds.pri5t3t+thresholds.pri5t3f);
	
	
	// PRI5
	if(thresholds.pri5t1t+thresholds.pri5t1f != 0)	
	{if (check51 >= p5)
	{thresholds.PRI5threshold = thresholds.PRI5av+dev1*thresholds.PRI5dev;}
	else{if(thresholds.pri5t2t+thresholds.pri5t2f != 0)	
		if(check52 >= p5)
		{thresholds.PRI5threshold = thresholds.PRI5av+dev2*thresholds.PRI5dev;}
			else{if(thresholds.pri5t3t+thresholds.pri5t3f != 0)	
				if(check53 >= p5)
				{thresholds.PRI5threshold = thresholds.PRI5av+dev3*thresholds.PRI5dev;}
				}
		}
		
	}
	//------------------------
	// PRI7

	Double check71 = (double)thresholds.pri7t1t/(thresholds.pri7t1t+thresholds.pri7t1f);
	Double check72 = (double)thresholds.pri7t2t/(thresholds.pri7t2t+thresholds.pri7t2f);
	Double check73 = (double)thresholds.pri7t3t/(thresholds.pri7t3t+thresholds.pri7t3f);

	
	if(thresholds.pri7t1t+thresholds.pri7t1f != 0)	
	{if (check71 >= p7)
	{thresholds.PRI7threshold = thresholds.PRI7av+dev1*thresholds.PRI7dev;}
	else{if(thresholds.pri7t2t+thresholds.pri7t2f != 0)	
		if(check72 >= p7)
		{thresholds.PRI7threshold = thresholds.PRI7av+dev2*thresholds.PRI7dev;}
			else{if(thresholds.pri7t3t+thresholds.pri7t3f != 0)	
				if(check73 >= p7)
				{thresholds.PRI7threshold = thresholds.PRI7av+dev3*thresholds.PRI7dev;}
				}
		}
		
	}

// -----------------
	
		
	for (int i=0;i<thresholds.activities.size();i++)
		
	{
		// PRI 1
		
		Double check11 = (double)thresholds.activities.elementAt(i).pri1t1t/(thresholds.activities.elementAt(i).pri1t1t+thresholds.activities.elementAt(i).pri1t1f);
		Double check12 = (double)thresholds.activities.elementAt(i).pri1t2t/(thresholds.activities.elementAt(i).pri1t2t+thresholds.activities.elementAt(i).pri1t2f);
		Double check13 = (double)thresholds.activities.elementAt(i).pri1t3t/(thresholds.activities.elementAt(i).pri1t3t+thresholds.activities.elementAt(i).pri1t3f);
		
		
		if(thresholds.activities.elementAt(i).pri1t1t+thresholds.activities.elementAt(i).pri1t1f != 0)	
		{if (check11 >= p1)
		{thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+dev1*thresholds.activities.elementAt(i).PRI1dev;}
		else{if(thresholds.activities.elementAt(i).pri1t2t+thresholds.activities.elementAt(i).pri1t2f != 0)	
			if(check12 >= p1)
			{thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+dev2*thresholds.activities.elementAt(i).PRI1dev;}
				else{if(thresholds.activities.elementAt(i).pri1t3t+thresholds.activities.elementAt(i).pri1t3f != 0)	
					if(check13 >= p1)
					{thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+dev3*thresholds.activities.elementAt(i).PRI1dev;}
					}
			}
			
		}
		//-----------------------
		
		// PRI 2
		
		Double check21 = (double)thresholds.activities.elementAt(i).pri2t1t/(thresholds.activities.elementAt(i).pri2t1t+thresholds.activities.elementAt(i).pri2t1f);
		Double check22 = (double)thresholds.activities.elementAt(i).pri2t2t/(thresholds.activities.elementAt(i).pri2t2t+thresholds.activities.elementAt(i).pri2t2f);
		Double check23 = (double)thresholds.activities.elementAt(i).pri2t3t/(thresholds.activities.elementAt(i).pri2t3t+thresholds.activities.elementAt(i).pri2t3f);
	
		
				if(thresholds.activities.elementAt(i).pri2t1t+thresholds.activities.elementAt(i).pri2t1f != 0)	
				{if (check21 >= p2)
				{thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+dev1*thresholds.activities.elementAt(i).PRI2dev;}
				else{if(thresholds.activities.elementAt(i).pri2t2t+thresholds.activities.elementAt(i).pri2t2f != 0)	
					if(check22 >= p2)
					{thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+dev2*thresholds.activities.elementAt(i).PRI2dev;}
						else{if(thresholds.activities.elementAt(i).pri2t3t+thresholds.activities.elementAt(i).pri2t3f != 0)	
							if(check23 >= p2)
							{thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+dev3*thresholds.activities.elementAt(i).PRI2dev;}
							}
					}
					
				}
		//-----------------------
				
				// PRI 3
				
				Double check31 = (double)thresholds.activities.elementAt(i).pri3t1t/(thresholds.activities.elementAt(i).pri3t1t+thresholds.activities.elementAt(i).pri3t1f);
				Double check32 = (double)thresholds.activities.elementAt(i).pri3t2t/(thresholds.activities.elementAt(i).pri3t2t+thresholds.activities.elementAt(i).pri3t2f);
				Double check33 = (double)thresholds.activities.elementAt(i).pri3t3t/(thresholds.activities.elementAt(i).pri3t3t+thresholds.activities.elementAt(i).pri3t3f);
			
				
				if(thresholds.activities.elementAt(i).pri3t1t+thresholds.activities.elementAt(i).pri3t1f != 0)	
				{if (check31 >= p3)
				{thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+dev1*thresholds.activities.elementAt(i).PRI3dev;}
				else{if(thresholds.activities.elementAt(i).pri3t2t+thresholds.activities.elementAt(i).pri3t2f != 0)	
					if(check32 >= p3)
					{thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+dev2*thresholds.activities.elementAt(i).PRI3dev;}
						else{if(thresholds.activities.elementAt(i).pri3t3t+thresholds.activities.elementAt(i).pri3t3f != 0)	
							if(check33 >= p3)
							{thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+dev3*thresholds.activities.elementAt(i).PRI3dev;}
							}
					}
					
				}
		//-----------------------
// PRI 6
				Double check61 = (double)thresholds.activities.elementAt(i).pri6t1t/(thresholds.activities.elementAt(i).pri6t1t+thresholds.activities.elementAt(i).pri6t1f);
				Double check62 = (double)thresholds.activities.elementAt(i).pri6t2t/(thresholds.activities.elementAt(i).pri6t2t+thresholds.activities.elementAt(i).pri6t2f);
				Double check63 = (double)thresholds.activities.elementAt(i).pri6t3t/(thresholds.activities.elementAt(i).pri6t3t+thresholds.activities.elementAt(i).pri6t3f);
			
				
				if(thresholds.activities.elementAt(i).pri6t1t+thresholds.activities.elementAt(i).pri6t1f != 0)	
				{if (check61 >= p6)
				{thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+dev1*thresholds.activities.elementAt(i).PRI6dev;}
				else{if(thresholds.activities.elementAt(i).pri6t2t+thresholds.activities.elementAt(i).pri6t2f != 0)	
					if(check62 >= p6)
					{thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+dev2*thresholds.activities.elementAt(i).PRI6dev;}
						else{if(thresholds.activities.elementAt(i).pri6t3t+thresholds.activities.elementAt(i).pri6t3f != 0)	
							if(check63 >= p6)
							{thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+dev3*thresholds.activities.elementAt(i).PRI6dev;}
							}
					}
					
				}
		//-----------------------
// PRI 4
				
				if(thresholds.activities.elementAt(i).pri4t+thresholds.activities.elementAt(i).pri4f != 0)	
				{if ((double)thresholds.activities.elementAt(i).pri4t/(thresholds.activities.elementAt(i).pri4t+thresholds.activities.elementAt(i).pri4f) >= p4)
				{thresholds.activities.elementAt(i).PRI4 = true;}
					
				}
		
		
		
	}
	
for (int i=0;i<thresholds.resources.size();i++)
		
	{
		// PRI 9
		
		Double check91 = (double)thresholds.resources.elementAt(i).pri9t1t/(thresholds.resources.elementAt(i).pri9t1t+thresholds.resources.elementAt(i).pri9t1f);
		Double check92 = (double)thresholds.resources.elementAt(i).pri9t2t/(thresholds.resources.elementAt(i).pri9t2t+thresholds.resources.elementAt(i).pri9t2f);
		Double check93 = (double)thresholds.resources.elementAt(i).pri9t3t/(thresholds.resources.elementAt(i).pri9t3t+thresholds.resources.elementAt(i).pri9t3f);
		
		
		if(thresholds.resources.elementAt(i).pri9t1t+thresholds.resources.elementAt(i).pri9t1f != 0)	
		{if (check91 >= p9)
		{thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+dev1*thresholds.resources.elementAt(i).PRI9dev;}
		else{if(thresholds.resources.elementAt(i).pri9t2t+thresholds.resources.elementAt(i).pri9t2f != 0)	
			if(check92 >= p9)
			{thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+dev2*thresholds.resources.elementAt(i).PRI9dev;}
				else{if(thresholds.resources.elementAt(i).pri9t3t+thresholds.resources.elementAt(i).pri9t3f != 0)	
					if(check93 >= p9)
					{thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+dev3*thresholds.resources.elementAt(i).PRI9dev;}
					}
			}
			
		}
		
		thresholds.resources.elementAt(i).current_workload = new Long(0);
	
		// PRI 8
		
		for (int j=0; j<thresholds.resources.elementAt(i).resourceActivities.size();j++)
		{
			if ((thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t+thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8f) != 0)
			{if ((double)thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t/(thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t+thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8f) > p8)
				{
				thresholds.resources.elementAt(i).resourceActivities.elementAt(j).PRI8 = true;
				}
			}
		}
		
	}

	
					
			
}
*/


//--------------------------------------------------PREV VERSIONS-------------------------------------------------
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*	
public XLog  getRunRisksPublished(UIPluginContext context, XLog inputlog, Date startTimeslot, Date endTimeslot, Double dist, Double allstDev, Double stDev, InputParameters ip) {
		
InOut inout = new InOut();
Basic basic = new Basic();
Stat stat = new Stat();
	
		XLog log = XFactoryRegistry.instance().currentDefault().createLog(inputlog.getAttributes());
	
		Vector <String> task_names = new Vector<String>();
		
		for (XTrace t : inputlog) {
			XTrace trace = XFactoryRegistry.instance().currentDefault().createTrace(t.getAttributes());
			log.add(trace);
						 
		for (XEvent e : t) {
				XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
				
				String eventName = XConceptExtension.instance().extractName(e);
				String lifecycle = XLifecycleExtension.instance().extractTransition(e);
				
				event.getAttributes().put("feature:act_duration",new XAttributeBooleanImpl("feature:act_duration",false));
				event.getAttributes().put("feature:ab_duration",new XAttributeContinuousImpl("feature:ab_duration",0.00));
				event.getAttributes().put("feature:wait_duration",new XAttributeBooleanImpl("feature:wait_duration",false));
				event.getAttributes().put("feature:ab_wait_duration",new XAttributeContinuousImpl("feature:ab_wait_duration",0.00));
				event.getAttributes().put("feature:odd_activity",new XAttributeBooleanImpl("feature:odd_activity",false));
				event.getAttributes().put("feature:odd_activity_duration",new XAttributeContinuousImpl("feature:odd_activity_duration",0.00));
				event.getAttributes().put("feature:act_repetition",new XAttributeBooleanImpl("feature:act_repetition",false));
				event.getAttributes().put("feature:rep_duration",new XAttributeContinuousImpl("feature:rep_duration",0.00));
				event.getAttributes().put("feature:risky_timeslot",new XAttributeBooleanImpl("feature:risky_timeslot",false));
				event.getAttributes().put("feature:inexperienced_resource",new XAttributeBooleanImpl("feature:inexperienced_resource",false));
				event.getAttributes().put("feature:ab_num_exec",new XAttributeContinuousImpl("feature:ab_num_exec",0.00));
				event.getAttributes().put("time:duration",new XAttributeDiscreteImpl("time:duration",0));
				event.getAttributes().put("subprocess:duration",new XAttributeDiscreteImpl("subprocess:duration",0));
				event.getAttributes().put("feature:subproc_duration",new XAttributeBooleanImpl("feature:subproc_duration",false));
				event.getAttributes().put("feature:subproc_ab_duration",new XAttributeContinuousImpl("feature:subproc_ab_duration",0.00));
				
	
	//---------new attributes-------------------------------------------
				
	event.getAttributes().put("PRI1:average",new XAttributeContinuousImpl("PRI1:average",0.00));
	event.getAttributes().put("PRI1:stDev",new XAttributeContinuousImpl("PRI1:stDev",0.00));
	event.getAttributes().put("PRI1:deviation",new XAttributeContinuousImpl("PRI1:deviation",0.00));
							
	event.getAttributes().put("PRI2:average",new XAttributeContinuousImpl("PRI2:average",0.00));
	event.getAttributes().put("PRI2:stDev",new XAttributeContinuousImpl("PRI2:stDev",0.00));
	event.getAttributes().put("PRI2:deviation",new XAttributeContinuousImpl("PRI2:deviation",0.00));
	
	event.getAttributes().put("PRI3:average",new XAttributeContinuousImpl("PRI3:average",0.00));
	event.getAttributes().put("PRI3:stDev",new XAttributeContinuousImpl("PRI3:stDev",0.00));
	event.getAttributes().put("PRI3:deviation",new XAttributeContinuousImpl("PRI3:deviation",0.00));
	
	event.getAttributes().put("PRI6:average",new XAttributeContinuousImpl("PRI6:average",0.00));
	event.getAttributes().put("PRI6:stDev",new XAttributeContinuousImpl("PRI6:stDev",0.00));
	event.getAttributes().put("PRI6:deviation",new XAttributeContinuousImpl("PRI6:deviation",0.00));
	
	event.getAttributes().put("PRI4:part",new XAttributeContinuousImpl("PRI4:part",0.00));
	event.getAttributes().put("PRI4:times",new XAttributeContinuousImpl("PRI4:times",0.00));
			
	// adding activity durations, choose option - start or previous event
				
				if (lifecycle.equalsIgnoreCase("complete"))
				{
					event.getAttributes().put("time:duration",new XAttributeDiscreteImpl("time:duration",basic.ActivityStartDuration(t, e)));
					event.getAttributes().put("subprocess:duration",new XAttributeDiscreteImpl("subprocess:duration",basic.SubprocessDuration(t, e)));
				}
				
				
				trace.add(event);
				
				
	//getting array of activity names
				
				boolean flag = false;
	
				for(int z=0;z<task_names.size();z++){
					if(task_names.elementAt(z).equals(eventName)){flag = true;}
				};
				if (!flag){task_names.add(eventName);}
				}
		
	//adding case duration to log
		
		XEvent first = trace.get(0);
		XEvent last = trace.get(trace.size()-1);
		Date begin = XTimeExtension.instance().extractTimestamp(first);
		Date end = XTimeExtension.instance().extractTimestamp(last);
		long duration = end.getTime()-begin.getTime();
		trace.getAttributes().put("time:duration",new XAttributeDiscreteImpl("time:duration",duration));
		trace.getAttributes().put("feature:multiple_resources",new XAttributeBooleanImpl("feature:multiple_resources",false));
		trace.getAttributes().put("feature:ab_mult_res",new XAttributeContinuousImpl("feature:ab_mult_res",0));
		trace.getAttributes().put("time:SLA",new XAttributeContinuousImpl("time:SLA",0));
		trace.getAttributes().put("time:PRI1",new XAttributeDiscreteImpl("time:PRI1",0));
		trace.getAttributes().put("time:PRI2",new XAttributeDiscreteImpl("time:PRI2",0));
		trace.getAttributes().put("time:PRI3",new XAttributeDiscreteImpl("time:PRI3",0));
		trace.getAttributes().put("time:PRI4",new XAttributeDiscreteImpl("time:PRI4",0));
		trace.getAttributes().put("time:PRI5",new XAttributeDiscreteImpl("time:PRI5",0));
		trace.getAttributes().put("time:PRI6",new XAttributeDiscreteImpl("time:PRI6",0));
		trace.getAttributes().put("set:test",new XAttributeDiscreteImpl("set:test",0));
		
		//----------------------------new attributes-----------------------------------
		trace.getAttributes().put("PRI5:average",new XAttributeContinuousImpl("PRI5:average",0));
		trace.getAttributes().put("PRI5:stDev",new XAttributeContinuousImpl("PRI5:stDev",0));
		trace.getAttributes().put("PRI5:deviation",new XAttributeContinuousImpl("PRI5:deviation",0));
		
		trace.getAttributes().put("PRI7:average",new XAttributeContinuousImpl("PRI7:average",0));
		trace.getAttributes().put("PRI7:stDev",new XAttributeContinuousImpl("PRI7:stDev",0));
		trace.getAttributes().put("PRI7:deviation",new XAttributeContinuousImpl("PRI7:deviation",0));
	
		}

//--------------------getting SLA------------------------------------------------------
		
//Double allSLA = stat.getMedianLogSLASplit(inputlog, dist, allstDev);
//Double allSLA = stat.getMedianLogSLA(inputlog, dist, allstDev);
//Double allSLA = stat.getMedianSLA(inputlog, dist, allstDev);
//Double allSLA = stat.getAverageSLA(inputlog, allstDev);
//Double allSLA = stat.getAverageLogSLA(inputlog, 2.00);

//Double allSLA = 2592000000.00;
//Double allSLA = 18000000.00;
Double allSLA = 0.0;
if(ip.calculateSLA)
{allSLA = stat.getMedianLogSLA(inputlog, dist, allstDev);}
else
{
	allSLA = ip.SLA;
}	
		
context.log("SLA: "+allSLA);
				
		
//getting number of deviations for time-based features

//Double act_stdev_threshold = stat.getMedianStDevThreshold (log, 1.483, allSLA);
//Double wait_stdev_threshold = stat.getMedianStDevThreshold (log, 1.483, allSLA);
//Double odd_threshold = stat.getNumViolated (context, log, allSLA);
//Double cycle_stdev_threshold = stat.getMedianStDevThreshold (log, 1.00, allSLA);


// annotating case outcomes

ConcurrentSkipListSet<String> bad_cases = new ConcurrentSkipListSet<String>();

bad_cases.add("1");
bad_cases.add("2");
bad_cases.add("3");
bad_cases.add("4");
bad_cases.add("5");
bad_cases.add("6");
bad_cases.add("7");
bad_cases.add("55");
bad_cases.add("57");

log = basic.addTimeOutcome (log, allSLA);	
log = basic.addQualityOutcome (log, bad_cases);	


//to add split function here

// checking risk features

XLog testLog = getTestLog25(log);
XLog trainLog = getTrainLog75(log);
//XLog testLog = getTestLog(log);
//XLog trainLog = getTrainLog(log);
				
		for(int i=0; i<task_names.size(); i++)
		{
		//log = AbnormalSubprocessDuration.getRiskyLogMedianSplit (context, log, task_names.elementAt(i), dist, stDev);
		//log = AtypicalActivity.getRiskySplit (context, log, task_names.elementAt(i), 0.05);
		//log = AbnormalActivityDuration.getRiskyLogMedianSplit (context, log, task_names.elementAt(i), dist, stDev);
		//log = AbnormalWaitDuration.getRiskyLogMedianSplit (log, task_names.elementAt(i), dist, stDev);
		//testLog = AbnormalWaitDuration.getRiskyLogAverageTwoLogs (trainLog, testLog, task_names.elementAt(i), dist, stDev);
		//log = MultipleActivityRepetitions.getRiskyMedianSplit (context, log, task_names.elementAt(i), dist, stDev);
		//log = MultipleActivityRepetitions.getRiskyAverage (context, log, task_names.elementAt(i), dist, 1.00);
		//log = AbnormalActivityDuration.getRiskyLogAverage (context, log, task_names.elementAt(i), dist, 1.50);
		//log = AbnormalWaitDuration.getRiskyLogAverage (log, task_names.elementAt(i), dist, 2.00);
		//log = MultipleActivityRepetitions.getRiskyAverage (context, log, task_names.elementAt(i), dist, 1.50);
		//testLog = AtypicalActivity.getRisky (context, testLog, task_names.elementAt(i), 0.05);
		//log = AbnormalActivityDuration.getRiskyLogMedian (context, log, task_names.elementAt(i), dist, stDev);
		//testLog = AbnormalActivityDuration.getRiskyLogAverageTwoLogs (context, trainLog, testLog, task_names.elementAt(i), dist, stDev);
		//log = AbnormalWaitDuration.getRiskyLogMedian (log, task_names.elementAt(i), dist, stDev);
		//log = MultipleActivityRepetitions.getRiskyMedian (context, log, task_names.elementAt(i), dist, stDev);
		//log = RiskyTimeslots.getRisky(log, task_names.elementAt(i), startTimeslot, endTimeslot);
		//log = InexperiencedResource.getRiskyLeftMedian (log, task_names.elementAt(i), 1.483, 1.00);	
		
		
		testLog = AbnormalSubprocessDuration.getRiskyLogMedianTwoLogs (context, trainLog, testLog, task_names.elementAt(i), dist, stDev);
		testLog = AtypicalActivity.getRiskyTwoLogs (context, trainLog, testLog, task_names.elementAt(i), 0.05);
		testLog = AbnormalActivityDuration.getRiskyLogMedianTwoLogs (context, trainLog, testLog, task_names.elementAt(i), dist, stDev);
		testLog = AbnormalWaitDuration.getRiskyLogMedianTwoLogs (trainLog, testLog, task_names.elementAt(i), dist, stDev);
		testLog = MultipleActivityRepetitions.getRiskyMedianTwoLogs (context, trainLog, testLog, task_names.elementAt(i), dist, stDev);
		
		
		testLog = AbnormalSubprocessDuration.Risky(context, trainLog, testLog, task_names.elementAt(i));
		testLog = AtypicalActivity.Risky (context, trainLog, testLog, task_names.elementAt(i), 1.00);
		testLog = AbnormalActivityDuration.Risky (context, trainLog, testLog, task_names.elementAt(i));
		testLog = AbnormalWaitDuration.Risky (trainLog, testLog, task_names.elementAt(i));
		testLog = MultipleActivityRepetitions.Risky (context, trainLog, testLog, task_names.elementAt(i));
				
		}
		//log = MultipleResourcesPerCase.getRiskyMedian (log, task_names.elementAt(0), dist, stDev);
		//log = MultipleResourcesPerCase.getRiskyMedianSplit (log, dist, stDev);
		//log = MultipleResourcesPerCase.getRiskyAverage (log, task_names.elementAt(0), dist, stDev);
		
		//testLog = MultipleResourcesPerCase.getRiskyMedianTwoLogs (trainLog, testLog, task_names.elementAt(0), dist, stDev);
		testLog = MultipleResourcesPerCase.Risky (trainLog, testLog, task_names.elementAt(0));
					
		testLog = inout.processTime(testLog);	
		testLog = inout.processQuality(testLog);
		return testLog;
		
		//log = inout.processTime(log);	
		//log = inout.processQuality(log);
		//return log;		
							
		}

*/

/*
public static XLog getTrainLog (XLog log){
	
	
	int splitIndex = 0;
	int size = log.size();
	
	if( (size % 2) == 0){splitIndex = size/2;}else{splitIndex = (size+1)/2;}	
								
		
	XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
	int i = 0;	
				
				for (XTrace t : log) 
				{
					if (i<splitIndex)
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

*/

/*
public static XLog getTestLog (XLog log){
	
	
	int splitIndex = 0;
	int size = log.size();
	
	if( (size % 2) == 0){splitIndex = size/2;}else{splitIndex = (size+1)/2;}	
								
		
	XLog copylog = XFactoryRegistry.instance().currentDefault().createLog(log.getAttributes());
	int i = 0;	
				
				for (XTrace t : log) 
				{
					if (i>=splitIndex)
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


*/

/*
public PriCaseThreshold  getThresholds(UIPluginContext context, XLog trainlog, PriCaseThreshold thresholds, Double p1, Double p2, Double p3, Double p4, Double p5, Double p6, Double p7, Double p8, Double p9) {
	
	Basic basic = new Basic();
	
	Double dev1 = 1.00;
	Double dev2 = 2.00;
	Double dev3 = 3.00;
		
	Double PRI5av = thresholds.PRI5av;
	Double PRI5dev = thresholds.PRI5dev;
	Double PRI7av = thresholds.PRI7av;
	Double PRI7dev = thresholds.PRI7dev;
	
	for (XTrace t : trainlog) {
		
		XAttributeMap am = t.getAttributes();
		String outcome = am.get("outcome:case_duration").toString();
				
		ConcurrentSkipListSet<String> resources = new ConcurrentSkipListSet<String>();	
		ConcurrentSkipListSet<String> activities = new ConcurrentSkipListSet<String>();
		//----------new----------------------
		ConcurrentSkipListSet<String> repeated_activities = new ConcurrentSkipListSet<String>();	
		ConcurrentSkipListSet<String> risky_activities = new ConcurrentSkipListSet<String>();
		//---------end of new---------------
	
																 
	for (XEvent e : t) 
		{
		XEvent event = XFactoryRegistry.instance().currentDefault().createEvent(e.getAttributes());
		String eventName = XConceptExtension.instance().extractName(event);
		String lifecycle = XLifecycleExtension.instance().extractTransition(event);
		String resource = XOrganizationalExtension.instance().extractResource(event);
		
		// PRI 5
		
		if(resource != null)
		{resources.add(resource);}
			
		// PRI 7
		
		if(eventName != null)
		{activities.add(eventName);}
		
		// updating current workload for PRI 9
		
			
		int resIndex = thresholds.getResourceIndex(resource);
		
			if(lifecycle.equalsIgnoreCase("start"))
			{
				thresholds.resources.elementAt(resIndex).current_workload+=1;
			}
			
			if(lifecycle.equalsIgnoreCase("complete"))
			{
				thresholds.resources.elementAt(resIndex).current_workload-=1;
			}
			
		
		// ------------------------
		
		
		if (lifecycle.equalsIgnoreCase("complete")){
									
			// PRI 1
			XAttributeDiscrete act_duration = (XAttributeDiscrete)event.getAttributes().get("time:duration");
			long actDuration = act_duration.getValue();
			if (actDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t1f+=1;}
			}
			
			if (actDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t2f+=1;}
			}
			
			if (actDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri1t3f+=1;}
			}
			
			// PRI 3
			if(!repeated_activities.contains(eventName) && (basic.Index(t, e)+1) > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3dev)) 					
			{repeated_activities.add(eventName);
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t1f+=1;}
			}
			
			if((basic.Index(t, e)+1) > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t2f+=1;}
			}	
			
			if((basic.Index(t, e)+1) > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI3dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri3t3f+=1;}
			}	
			
						
			// PRI 4
			if(!risky_activities.contains(eventName))
			{risky_activities.add(eventName);
			if (outcome.equals("true"))					
			{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri4t+=1;}
			else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri4f+=1;};
			}
						
			// PRI 6
			XAttributeDiscrete subproc_duration = (XAttributeDiscrete)event.getAttributes().get("subprocess:duration");
			long subProcDuration = subproc_duration.getValue();
			if (subProcDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t1f+=1;}
			}
		
			if (subProcDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t2f+=1;}
			}
			
			if (subProcDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI6dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri6t3f+=1;}
			}
			
	// PRI 8
			
			Double activityAverage = thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI1av;
			Double resourceActivityAverage = thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).resourceActivities.elementAt(thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).getIndex(eventName)).PRI8av;
				if ( resourceActivityAverage > -1 activityAverage ) 					
					{
					if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).resourceActivities.elementAt(thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).getIndex(eventName)).pri8t+=1;}
					else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).resourceActivities.elementAt(thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).getIndex(eventName)).pri8f+=1;}
					}
	
		}
		
		if (lifecycle.equalsIgnoreCase("start") && t.indexOf(e)!=0){
			
			// PRI 2
			Date eventTime = XTimeExtension.instance().extractTimestamp(e);
			Date prevEventTime = XTimeExtension.instance().extractTimestamp(basic.PrevPRI2(t,e));
			long waitDuration = eventTime.getTime()-prevEventTime.getTime();
			if (waitDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2av + dev1*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t1t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t1f+=1;}
			}
			
			if (waitDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2av + dev2*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t2t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t2f+=1;}
			}
			
			if (waitDuration > (thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2av + dev3*thresholds.activities.elementAt(thresholds.getIndex(eventName)).PRI2dev)) 					
			{
				if (outcome.equals("true")){thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t3t+=1;}
				else{thresholds.activities.elementAt(thresholds.getIndex(eventName)).pri2t3f+=1;}
			}
		}
		
		if (lifecycle.equalsIgnoreCase("start")){
			
			// PRI 9
			if (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload > (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9av + dev1*thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9dev)) 					
			{
				if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t1t+=1;}
				else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t1f+=1;}
			}
			
			if (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload > (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9av + dev2*thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9dev)) 					
			{
				if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t2t+=1;}
				else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t2f+=1;}
			}
			
			if (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).current_workload > (thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9av + dev3*thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).PRI9dev)) 					
			{
				if (outcome.equals("true")){thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t3t+=1;}
				else{thresholds.resources.elementAt(thresholds.getResourceIndex(resource)).pri9t3f+=1;}
			}
			
	}
}
	
	// ------------------------------------
	
	
	// PRI 5
	long resNum = resources.size();
	if (resNum > (PRI5av+dev1*PRI5dev))					
	{
		if (outcome.equals("true")){thresholds.pri5t1t+=1;}else{thresholds.pri5t1f+=1;}
	}
	
	if (resNum > (PRI5av+dev2*PRI5dev))					
	{
		if (outcome.equals("true")){thresholds.pri5t2t+=1;}else{thresholds.pri5t2f+=1;}
	}
	
	if (resNum > (PRI5av+dev3*PRI5dev))					
	{
		if (outcome.equals("true")){thresholds.pri5t3t+=1;}else{thresholds.pri5t3f+=1;}
	}
	
	// PRI 7
	long actNum = activities.size();	
	if (actNum > (PRI7av+dev1*PRI7dev))					
	{
		if (outcome.equals("true")){thresholds.pri7t1t+=1;}else{thresholds.pri7t1f+=1;}
	}
	
	if (actNum > (PRI7av+dev2*PRI7dev))					
	{
		if (outcome.equals("true")){thresholds.pri7t2t+=1;}else{thresholds.pri7t2f+=1;}
	}
	
	if (actNum > (PRI7av+dev3*PRI7dev))					
	{
		if (outcome.equals("true")){thresholds.pri7t3t+=1;}else{thresholds.pri7t3f+=1;}
	}
	}		
		

// ------------------------

	Double check51 = (double)thresholds.pri5t1t/(thresholds.pri5t1t+thresholds.pri5t1f);
	Double check52 = (double)thresholds.pri5t2t/(thresholds.pri5t2t+thresholds.pri5t2f);
	Double check53 = (double)thresholds.pri5t3t/(thresholds.pri5t3t+thresholds.pri5t3f);
	
	
	// PRI5
	if(thresholds.pri5t1t+thresholds.pri5t1f != 0)	
	{if (check51 >= p5)
	{thresholds.PRI5threshold = thresholds.PRI5av+dev1*thresholds.PRI5dev;}
	else{if(thresholds.pri5t2t+thresholds.pri5t2f != 0)	
		if(check52 >= p5)
		{thresholds.PRI5threshold = thresholds.PRI5av+dev2*thresholds.PRI5dev;}
			else{if(thresholds.pri5t3t+thresholds.pri5t3f != 0)	
				if(check53 >= p5)
				{thresholds.PRI5threshold = thresholds.PRI5av+dev3*thresholds.PRI5dev;}
				}
		}
		
	}
	//------------------------
	// PRI7

	Double check71 = (double)thresholds.pri7t1t/(thresholds.pri7t1t+thresholds.pri7t1f);
	Double check72 = (double)thresholds.pri7t2t/(thresholds.pri7t2t+thresholds.pri7t2f);
	Double check73 = (double)thresholds.pri7t3t/(thresholds.pri7t3t+thresholds.pri7t3f);

	
	if(thresholds.pri7t1t+thresholds.pri7t1f != 0)	
	{if (check71 >= p7)
	{thresholds.PRI7threshold = thresholds.PRI7av+dev1*thresholds.PRI7dev;}
	else{if(thresholds.pri7t2t+thresholds.pri7t2f != 0)	
		if(check72 >= p7)
		{thresholds.PRI7threshold = thresholds.PRI7av+dev2*thresholds.PRI7dev;}
			else{if(thresholds.pri7t3t+thresholds.pri7t3f != 0)	
				if(check73 >= p7)
				{thresholds.PRI7threshold = thresholds.PRI7av+dev3*thresholds.PRI7dev;}
				}
		}
		
	}

// -----------------
	
		
	for (int i=0;i<thresholds.activities.size();i++)
		
	{
		// PRI 1
		
		Double check11 = (double)thresholds.activities.elementAt(i).pri1t1t/(thresholds.activities.elementAt(i).pri1t1t+thresholds.activities.elementAt(i).pri1t1f);
		Double check12 = (double)thresholds.activities.elementAt(i).pri1t2t/(thresholds.activities.elementAt(i).pri1t2t+thresholds.activities.elementAt(i).pri1t2f);
		Double check13 = (double)thresholds.activities.elementAt(i).pri1t3t/(thresholds.activities.elementAt(i).pri1t3t+thresholds.activities.elementAt(i).pri1t3f);
		
		
		if(thresholds.activities.elementAt(i).pri1t1t+thresholds.activities.elementAt(i).pri1t1f != 0)	
		{if (check11 >= p1)
		{thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+dev1*thresholds.activities.elementAt(i).PRI1dev;}
		else{if(thresholds.activities.elementAt(i).pri1t2t+thresholds.activities.elementAt(i).pri1t2f != 0)	
			if(check12 >= p1)
			{thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+dev2*thresholds.activities.elementAt(i).PRI1dev;}
				else{if(thresholds.activities.elementAt(i).pri1t3t+thresholds.activities.elementAt(i).pri1t3f != 0)	
					if(check13 >= p1)
					{thresholds.activities.elementAt(i).PRI1threshold = thresholds.activities.elementAt(i).PRI1av+dev3*thresholds.activities.elementAt(i).PRI1dev;}
					}
			}
			
		}
		//-----------------------
		
		// PRI 2
		
		Double check21 = (double)thresholds.activities.elementAt(i).pri2t1t/(thresholds.activities.elementAt(i).pri2t1t+thresholds.activities.elementAt(i).pri2t1f);
		Double check22 = (double)thresholds.activities.elementAt(i).pri2t2t/(thresholds.activities.elementAt(i).pri2t2t+thresholds.activities.elementAt(i).pri2t2f);
		Double check23 = (double)thresholds.activities.elementAt(i).pri2t3t/(thresholds.activities.elementAt(i).pri2t3t+thresholds.activities.elementAt(i).pri2t3f);
	
		
				if(thresholds.activities.elementAt(i).pri2t1t+thresholds.activities.elementAt(i).pri2t1f != 0)	
				{if (check21 >= p2)
				{thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+dev1*thresholds.activities.elementAt(i).PRI2dev;}
				else{if(thresholds.activities.elementAt(i).pri2t2t+thresholds.activities.elementAt(i).pri2t2f != 0)	
					if(check22 >= p2)
					{thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+dev2*thresholds.activities.elementAt(i).PRI2dev;}
						else{if(thresholds.activities.elementAt(i).pri2t3t+thresholds.activities.elementAt(i).pri2t3f != 0)	
							if(check23 >= p2)
							{thresholds.activities.elementAt(i).PRI2threshold = thresholds.activities.elementAt(i).PRI2av+dev3*thresholds.activities.elementAt(i).PRI2dev;}
							}
					}
					
				}
		//-----------------------
				
				// PRI 3
				
				Double check31 = (double)thresholds.activities.elementAt(i).pri3t1t/(thresholds.activities.elementAt(i).pri3t1t+thresholds.activities.elementAt(i).pri3t1f);
				Double check32 = (double)thresholds.activities.elementAt(i).pri3t2t/(thresholds.activities.elementAt(i).pri3t2t+thresholds.activities.elementAt(i).pri3t2f);
				Double check33 = (double)thresholds.activities.elementAt(i).pri3t3t/(thresholds.activities.elementAt(i).pri3t3t+thresholds.activities.elementAt(i).pri3t3f);
			
				
				if(thresholds.activities.elementAt(i).pri3t1t+thresholds.activities.elementAt(i).pri3t1f != 0)	
				{if (check31 >= p3)
				{thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+dev1*thresholds.activities.elementAt(i).PRI3dev;}
				else{if(thresholds.activities.elementAt(i).pri3t2t+thresholds.activities.elementAt(i).pri3t2f != 0)	
					if(check32 >= p3)
					{thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+dev2*thresholds.activities.elementAt(i).PRI3dev;}
						else{if(thresholds.activities.elementAt(i).pri3t3t+thresholds.activities.elementAt(i).pri3t3f != 0)	
							if(check33 >= p3)
							{thresholds.activities.elementAt(i).PRI3threshold = thresholds.activities.elementAt(i).PRI3av+dev3*thresholds.activities.elementAt(i).PRI3dev;}
							}
					}
					
				}
		//-----------------------
// PRI 6
				Double check61 = (double)thresholds.activities.elementAt(i).pri6t1t/(thresholds.activities.elementAt(i).pri6t1t+thresholds.activities.elementAt(i).pri6t1f);
				Double check62 = (double)thresholds.activities.elementAt(i).pri6t2t/(thresholds.activities.elementAt(i).pri6t2t+thresholds.activities.elementAt(i).pri6t2f);
				Double check63 = (double)thresholds.activities.elementAt(i).pri6t3t/(thresholds.activities.elementAt(i).pri6t3t+thresholds.activities.elementAt(i).pri6t3f);
			
				
				if(thresholds.activities.elementAt(i).pri6t1t+thresholds.activities.elementAt(i).pri6t1f != 0)	
				{if (check61 >= p6)
				{thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+dev1*thresholds.activities.elementAt(i).PRI6dev;}
				else{if(thresholds.activities.elementAt(i).pri6t2t+thresholds.activities.elementAt(i).pri6t2f != 0)	
					if(check62 >= p6)
					{thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+dev2*thresholds.activities.elementAt(i).PRI6dev;}
						else{if(thresholds.activities.elementAt(i).pri6t3t+thresholds.activities.elementAt(i).pri6t3f != 0)	
							if(check63 >= p6)
							{thresholds.activities.elementAt(i).PRI6threshold = thresholds.activities.elementAt(i).PRI6av+dev3*thresholds.activities.elementAt(i).PRI6dev;}
							}
					}
					
				}
		//-----------------------
// PRI 4
				
				if(thresholds.activities.elementAt(i).pri4t+thresholds.activities.elementAt(i).pri4f != 0)	
				{if ((double)thresholds.activities.elementAt(i).pri4t/(thresholds.activities.elementAt(i).pri4t+thresholds.activities.elementAt(i).pri4f) >= p4)
				{thresholds.activities.elementAt(i).PRI4 = true;}
					
				}
		
		
		
	}
	
for (int i=0;i<thresholds.resources.size();i++)
		
	{
		// PRI 9
		
		Double check91 = (double)thresholds.resources.elementAt(i).pri9t1t/(thresholds.resources.elementAt(i).pri9t1t+thresholds.resources.elementAt(i).pri9t1f);
		Double check92 = (double)thresholds.resources.elementAt(i).pri9t2t/(thresholds.resources.elementAt(i).pri9t2t+thresholds.resources.elementAt(i).pri9t2f);
		Double check93 = (double)thresholds.resources.elementAt(i).pri9t3t/(thresholds.resources.elementAt(i).pri9t3t+thresholds.resources.elementAt(i).pri9t3f);
		
		
		if(thresholds.resources.elementAt(i).pri9t1t+thresholds.resources.elementAt(i).pri9t1f != 0)	
		{if (check91 >= p9)
		{thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+dev1*thresholds.resources.elementAt(i).PRI9dev;}
		else{if(thresholds.resources.elementAt(i).pri9t2t+thresholds.resources.elementAt(i).pri9t2f != 0)	
			if(check92 >= p9)
			{thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+dev2*thresholds.resources.elementAt(i).PRI9dev;}
				else{if(thresholds.resources.elementAt(i).pri9t3t+thresholds.resources.elementAt(i).pri9t3f != 0)	
					if(check93 >= p9)
					{thresholds.resources.elementAt(i).PRI9threshold = thresholds.resources.elementAt(i).PRI9av+dev3*thresholds.resources.elementAt(i).PRI9dev;}
					}
			}
			
		}
		
		thresholds.resources.elementAt(i).current_workload = new Long(0);
	
		// PRI 8
		
		for (int j=0; j<thresholds.resources.elementAt(i).resourceActivities.size();j++)
		{
			if ((thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t+thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8f) != 0)
			{if ((double)thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t/(thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8t+thresholds.resources.elementAt(i).resourceActivities.elementAt(j).pri8f) > p8)
				{
				thresholds.resources.elementAt(i).resourceActivities.elementAt(j).PRI8 = true;
				}
			}
		}
		
	}

	return thresholds;
					
			
}
*/