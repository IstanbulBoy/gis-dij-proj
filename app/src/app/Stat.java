package app;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;


import sndlib.core.problem.RoutingPath.*;

/* Klasa statystyczna */
public class Stat {
	static HashMap<Integer, Long> timeForNodes = new HashMap<Integer, Long>();
	static HashMap<Integer, Integer> timeForNodesCount = new HashMap<Integer, Integer>();
	/* Funkcja dodaj�ca statytyk� z danej sieci do danych*/
	public static void addStatistics(){
		Long sum;
		int count;
//		if(timeForNodes.containsKey(Algorithm.network.nodeCount())){
//			sum=timeForNodes.remove(Algorithm.network.nodeCount());
//			sum+=Algorithm.solvingTime;
//			timeForNodes.put(Algorithm.network.nodeCount(), sum);
//
//			count = timeForNodesCount.remove(Algorithm.network.nodeCount());
//			count++;
//			timeForNodesCount.put(Algorithm.network.nodeCount(), count);
//		}
//		else {
//			timeForNodes.put(Algorithm.network.nodeCount(), Algorithm.solvingTime);
//			timeForNodesCount.put(Algorithm.network.nodeCount(), 1);
//
//		}
	}
	/* Funkcja generuj�ca i zapisuj�ca statystyki z rozwi�zanych do tej pory problem�w*/
	public static void generateStatistics(PrintStream... printstream){
		PrintStream ps = printstream.length == 0 ? System.out : printstream[0];
		HashMap<Integer, Double> meanTimeForNodes = new HashMap<Integer, Double>();
		Iterator keys=timeForNodes.keySet().iterator();
		while(keys.hasNext()){
			Integer key = (Integer)keys.next();
			Double meanTime = ((long)timeForNodes.get(key))/((double)(1d*timeForNodesCount.get(key)));
			meanTimeForNodes.put(key, meanTime);
			ps.println(key + "\t"+meanTime);
			System.out.println(key + "\t"+meanTime);
		}
		timeForNodes.clear();
		timeForNodesCount.clear();
		meanTimeForNodes.clear();
		
		
	}
}
