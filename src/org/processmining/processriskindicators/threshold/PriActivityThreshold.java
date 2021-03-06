package org.processmining.processriskindicators.threshold;

import java.util.Vector;


public class PriActivityThreshold {

	public String name;
	
	public Vector<Long> actDurations = new Vector<Long>();
	public Vector<Long> waitDurations = new Vector<Long>();
	public Vector<Long> subProcDurations = new Vector<Long>();
	public Vector<Long> Repetitions = new Vector<Long>();
	public Long caseRepetitions;
	public int numExecutions;
	
	public Double PRI1av;
	public Double PRI1dev;
	public Double PRI2av;
	public Double PRI2dev;
	public Double PRI3av;
	public Double PRI3dev;
	public Double PRI6av;
	public Double PRI6dev;
	
	public Double PRI1threshold;
	public Double PRI2threshold;
	public Double PRI3threshold;
	public Double PRI6threshold;
	public Boolean PRI4;	
	
//-----------------for outcome learning---------------------------------	
public int [] pri1t;
public int [] pri1f;
public double [] pri1part;

//----
public Vector<Integer>  pri1true = new Vector<Integer>();
public Vector<Integer>  pri1false = new Vector<Integer>();
public Vector<Double>  pri1fraction = new Vector<Double>();

public Vector<Integer>  pri2true = new Vector<Integer>();
public Vector<Integer>  pri2false = new Vector<Integer>();
public Vector<Double>  pri2fraction = new Vector<Double>();

public Vector<Integer>  pri3true = new Vector<Integer>();
public Vector<Integer>  pri3false = new Vector<Integer>();
public Vector<Double>  pri3fraction = new Vector<Double>();

public Vector<Integer>  pri6true = new Vector<Integer>();
public Vector<Integer>  pri6false = new Vector<Integer>();
public Vector<Double>  pri6fraction = new Vector<Double>();
//--------

public int [] pri2t;
public int [] pri2f;
public double [] pri2part;

public int [] pri3t;
public int [] pri3f;
public double [] pri3part;

public int [] pri6t;
public int [] pri6f;
public double [] pri6part;

public int pri1t1t;
public int pri1t1f;
public int pri1t2t;
public int pri1t2f;
public int pri1t3t;
public int pri1t3f;

public int pri2t1t;
public int pri2t1f;
public int pri2t2t;
public int pri2t2f;
public int pri2t3t;
public int pri2t3f;

public int pri3t1t;
public int pri3t1f;
public int pri3t2t;
public int pri3t2f;
public int pri3t3t;
public int pri3t3f;

public int pri6t1t;
public int pri6t1f;
public int pri6t2t;
public int pri6t2f;
public int pri6t3t;
public int pri6t3f;

public int pri4t;
public int pri4f;
//------------------------------------------------------
	
public PriActivityThreshold(String n, int devNum, double step)
{name = n;

numExecutions = 0;
caseRepetitions = new Long(0);

PRI1av = 0.00;
PRI1dev = 0.00;
PRI2av = 0.00;
PRI2dev = 0.00;
PRI3av = 0.00;
PRI3dev = 0.00;
PRI6av = 0.00;
PRI6dev = 0.00;

PRI1threshold = 0.00;
PRI2threshold = 0.00;
PRI3threshold = 0.00;
PRI6threshold = 0.00;
PRI4 = false;	

pri1t = new int [devNum+1];
pri1f = new int [devNum+1];
pri1part = new double [devNum+1];

pri2t = new int [devNum+1];
pri2f = new int [devNum+1];
pri2part = new double [devNum+1];

pri3t = new int [devNum+1];
pri3f = new int [devNum+1];
pri3part = new double [devNum+1];

pri6t = new int [devNum+1];
pri6f = new int [devNum+1];
pri6part = new double [devNum+1];


for(int i=0;i<(devNum+1);i++)
{
	pri1t[i]=0;
	pri1f[i]=0;
	pri1part[i]=0.00;
	
	pri2t[i]=0;
	pri2f[i]=0;
	pri2part[i]=0.00;
	
	pri3t[i]=0;
	pri3f[i]=0;
	pri3part[i]=0.00;
	
	pri6t[i]=0;
	pri6f[i]=0;
	pri6part[i]=0.00;
}

 pri1t1t = 0;
 pri1t1f = 0;
 pri1t2t = 0;
 pri1t2f = 0;
 pri1t3t = 0;
 pri1t3f = 0;

 pri2t1t = 0;
 pri2t1f = 0;
 pri2t2t = 0;
 pri2t2f = 0;
 pri2t3t = 0;
 pri2t3f = 0;

 pri3t1t = 0;
 pri3t1f = 0;
 pri3t2t = 0;
 pri3t2f = 0;
 pri3t3t = 0;
 pri3t3f = 0;

 pri6t1t = 0;
 pri6t1f = 0;
 pri6t2t = 0;
 pri6t2f = 0;
 pri6t3t = 0;
 pri6t3f = 0;

 pri4t = 0;
 pri4f = 0;
 
 //---
 int stdDevSize = (int) (devNum/step) + 1;
 
 
 for(int i=0; i<stdDevSize; i++)
	{
		pri1true.add(0);
		pri1false.add(0);
		pri1fraction.add(0.0);
		
		pri2true.add(0);
		pri2false.add(0);
		pri2fraction.add(0.0);
		
		pri3true.add(0);
		pri3false.add(0);
		pri3fraction.add(0.0);
		
		pri6true.add(0);
		pri6false.add(0);
		pri6fraction.add(0.0);
	}
};
}

