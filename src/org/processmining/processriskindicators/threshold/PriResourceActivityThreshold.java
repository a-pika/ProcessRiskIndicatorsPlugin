package org.processmining.processriskindicators.threshold;

public class PriResourceActivityThreshold {

	public String actName;
	
	public Long currentTotalDuration;
	public int currentNumExecutions;
	
	public Double PRI8av;
	public Boolean PRI8;	
	
//-----------------for outcome learning---------------------------------	

public int pri8t;
public int pri8f;

//------------------------------------------------------
	
public PriResourceActivityThreshold(String n)
{actName = n;

currentNumExecutions = 0;
currentTotalDuration = new Long(0);

PRI8av = 0.00;
PRI8 = false;	
pri8t = 0;
pri8f = 0;
};
}

