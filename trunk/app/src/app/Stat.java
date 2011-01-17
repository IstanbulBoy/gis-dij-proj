//package app;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.bcel.generic.NEW;

import sndlib.core.network.*;
import sndlib.core.problem.*;
import sndlib.core.problem.RoutingPath.*;
import sndlib.core.problem.RoutingPath;
import sndlib.core.util.NetworkUtils;

public class Stat {
	static HashMap<Integer, Long> timeForNodes = new HashMap<Integer, Long>();
	static HashMap<Integer, Integer> timeForNodesCount = new HashMap<Integer, Integer>();
	public static void addStatistics(){
		Long sum;
		int count;
		if(timeForNodes.containsKey(Algorithm.network.nodeCount())){
			sum=timeForNodes.remove(Algorithm.network.nodeCount());
			sum+=Algorithm.solvingTime;
			timeForNodes.put(Algorithm.network.nodeCount(), sum);
			
			count = timeForNodesCount.remove(Algorithm.network.nodeCount());
			count++;
			timeForNodesCount.put(Algorithm.network.nodeCount(), count);
		}
	}
	
	public static void generateStatistics(PrintStream... printstream){
		PrintStream ps = printstream.length == 0 ? System.out : printstream[0];
		HashMap<Integer, Double> meanTimeForNodes = new HashMap<Integer, Double>();
		Iterator keys=timeForNodes.keySet().iterator();
		for(int i=meanTimeForNodes.size();i>0;i--){
			Integer key = (Integer)keys.next();
			Double meanTime = ((long)timeForNodes.remove(key))/((double)(1d*timeForNodesCount.remove(key)));
			meanTimeForNodes.put(key, meanTime);
			ps.println(key + "\t"+meanTime);
		}
		
		
	}
}
