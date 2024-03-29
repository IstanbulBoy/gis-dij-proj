package app;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import sndlib.core.network.Network;


import sndlib.core.problem.RoutingPath.*;

/* Klasa statystyczna */
public class Stat {
	static HashMap<Integer, Long> timeForNodes = new HashMap<Integer, Long>();
	static HashMap<Integer, Integer> timeForNodesCount = new HashMap<Integer, Integer>();

	/* Funkcja dodajaca statytyke z ostatniego rozwiazywanego problemu do danych*/
	public static void addStatistics(Network network){
		Long sum;
		int count;
		/*Jesli Mapa juz zawiera element o okreslonej liczbie wezlow, to sumuj czas
		 * i liczbe przypadkow o danej liczbie wezlow*/
		if(timeForNodes.containsKey(network.nodeCount())){
			sum=timeForNodes.remove(network.nodeCount());
			sum+=Algorithm.solvingTime;
			timeForNodes.put(network.nodeCount(), sum);

			count = timeForNodesCount.remove(network.nodeCount());
			count++;
			timeForNodesCount.put(network.nodeCount(), count);
		}
		else {
			timeForNodes.put(network.nodeCount(), Algorithm.solvingTime);
			timeForNodesCount.put(network.nodeCount(), 1);

		}
		System.out.print("["+network.nodeCount()+":"+Algorithm.solvingTime+"]");
	}
	/* Funkcja generujaca i zapisujaca statystyki z rozwiazanych do tej pory problemow
	 * W argumencie moze przyjac strumien do jakiego zapisywac wyniki - jesli
	 * nie jest podany to na standardowe wyjscie - System.out*/
	public static void generateStatistics(PrintStream... printstream){
		
		PrintStream ps = printstream.length == 0 ? System.out : printstream[0];
		HashMap<Integer, Double> meanTimeForNodes = new HashMap<Integer, Double>();
		Iterator keys=timeForNodes.keySet().iterator();
		/*Petla po wszystkich liczbach wezlow dodanych do mapy*/
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
