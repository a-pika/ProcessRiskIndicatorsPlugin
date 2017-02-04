package org.processmining.processriskindicators.inout;


public class InputParameters {

	public Double desiredPrecision;
	public Double SLA;
	public String logSplit;
	public Boolean configure;
	public Boolean calculateSLA;
	public Double atypicalActivityThreshold;
	public int devNum;
	public double step;
	
	public InputParameters()
	{
		atypicalActivityThreshold = 0.05;
		//devNum = 100;
		//step = 0.1;
		//desiredPrecision = 0.90;
		//SLA = 18000000.00;
		//calculateSLA = true;
		//logSplit = "Cluster";
		//logSplit = "Replay";
		//configure = true;
		//configure = false;
	};

}

