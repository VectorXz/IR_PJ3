//Name(s):
//ID
//Section
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.Map.Entry;

/**
 * This class implements PageRank algorithm on simple graph structure.
 * Put your name(s), ID(s), and section here.
 *
 */

public class PageRanker {
	
	Set<Integer> pidInLine = new HashSet<Integer>(); // P is the set of all pages; |P| = N
	Set<Integer> noOutlink = new HashSet<Integer>(); // S is pages that have no out links  hashset : ไมได้ลากไปหาตัวไหนเลย
	Map<Integer, Set<Integer>> setPages = new HashMap<Integer, Set<Integer>>();// M(p) is the set of pages that link to page p
	Map<Integer, Integer> setOutlink = new HashMap<Integer, Integer>();// L(q) is the number of out-links from page q
	
	Map<Integer, Double> PR = new HashMap<Integer, Double>();
	
	ArrayList<Double> perplexValue = new ArrayList<Double>();// keep perplexity of each iteration to write into file
	
	//List<Double> checkUnit = new ArrayList<Double>(); 
	//Integer[] checkUnit = new Integer[4]; //keep 4 perplexity value for 4 iteration
	int roundTest = 1;//count 4 loop  for assign value to checkUnit
	
	/**
	 * This class reads the direct graph stored in the file "inputLinkFilename" into memory.
	 * Each line in the input file should have the following format:
	 * <pid_1> <pid_2> <pid_3> .. <pid_n>
	 * 
	 * Where pid_1, pid_2, ..., pid_n are the page IDs of the page having links to page pid_1. 
	 * You can assume that a page ID is an integer.
	 */
	public void loadData(String inputLinkFilename){
		//System.out.println("loadData");
		String[] lines;
		try {
			lines = Files.readAllLines(new File(inputLinkFilename).toPath()).toArray(new String[0]);
			
			for(String line: lines) {
				String[] result = line.split(" ");
				//System.out.println("result "+Arrays.toString(result)+" len "+result.length);
				int [] arr = new int [result.length];	//element in each line
				Set<Integer> set = new HashSet<Integer>();
				for(int i=0; i<result.length; i++) {
						arr[i] = Integer.parseInt(result[i]);
						if(!pidInLine.contains(arr[i])) {
							pidInLine.add(arr[i]); 
							noOutlink.add(arr[i]);
						}
						
					}
				 // len > 1 : have a set of page to link to that page
				
				
					if(arr.length > 1) {
					for(int i=1; i<arr.length; i++) {
							set.add(arr[i]);
							setPages.put(arr[0], set);
							
							if(setOutlink.get(arr[i]) != null) {
								//System.out.println("-----find again");
								setOutlink.put(arr[i],setOutlink.get(arr[i])+1);
							}
							else {
								//System.out.println("=====first find====");
								setOutlink.put(arr[i], 1);
							}
							
					}
				}
				
				
				
							
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		for(Integer page: pidInLine) {
			if( setPages.get(page) != null) {
				Integer[] pageList = setPages.get(page).toArray(new Integer[setPages.get(page).size()]); 
//				Integer[] pidList = pidInLine.toArray(new Integer[pidInLine.size()]); 
//				System.out.println(Arrays.toString(pageList)+ "------ "+ pageList[1]);
				for(Integer p : pageList) {
					if(noOutlink.contains(p)) {
					noOutlink.remove(p); //remove page that have out link 
					
					}
					
					
				}
			}
			
		}
		
		
		for(Integer pageOutLink: noOutlink) {
			setOutlink.put(pageOutLink, 0);
		}
		
		//setOutlink = sortByKeys(setOutlink);
//		System.out.println("pidInLine "+pidInLine+"\n");
//		System.out.println("setPage "+setPages.toString());
//		System.out.println("setOutLink "+setOutlink);
//		System.out.println("nooutlink "+noOutlink);
		//System.out.println("pidInline size "+pidInLine.size());
	}
	
	
	
	
		
	/**
	 * This method will be called after the graph is loaded into the memory.
	 * This method initialize the parameters for the PageRank algorithm including
	 * setting an initial weight to each page.
	 */
	public void initialize(){
		
		for(Integer pageP: pidInLine) {
			PR.put(pageP, (double)1.0/pidInLine.size());
		}
	
		//System.out.println(PR); // [0.16666667, 0.16666667, 0.16666667, 0.16666667, 0.16666667, 0.16666667]
	}
	
	/**
	 * Computes the perplexity of the current state of the graph. The definition
	 * of perplexity is given in the project specs.
	 */
	public double getPerplexity(){
		double perplexity = 0.0;
		// calculate 2^(Shannon) entropy of the PageRank distribution
		for (Integer page : pidInLine) {
			perplexity += PR.get(page) * (Math.log(PR.get(page)) / Math.log(2));
		}
		perplexity = perplexity * -1;
		perplexity = Math.pow(2, perplexity);
		return perplexity;
	}
	
	/**
	 * Returns true if the perplexity converges (hence, terminate the PageRank algorithm).
	 * Returns false otherwise (and PageRank algorithm continue to update the page scores). 
	 */
	public boolean isConverge(){
		//System.out.println("--------Iteration---------");
		boolean flag = false;
		
		if(perplexValue.size() > 1) {
			//System.out.println("--after first round----");
			int first = Character.digit(String.valueOf(perplexValue.get(perplexValue.size()-2)%10).charAt(0), 10);//unit position
			int last = Character.digit(String.valueOf(perplexValue.get(perplexValue.size()-1)%10).charAt(0), 10);//unit position
			if(first == last) {
				roundTest = roundTest + 1;
				//System.out.println("equal " + roundTest);
				if(roundTest >= 4) {
					flag = true;
					//System.out.println("------converge");
				}
				else {
					flag = false;
				}
			}
			else if(first != last) {
				roundTest = 0;
				flag = false;
			}
		}
		else if(perplexValue.size() == 0 || perplexValue.size() == 1){
			//System.out.println("----first round----");
			flag = false;
		}
		
		
	
		return flag;
	}
	
	/**
	 * The main method of PageRank algorithm. 
	 * Can assume that initialize() has been called before this method is invoked.
	 * While the algorithm is being run, this method should keep track of the perplexity
	 * after each iteration. 
	 * 
	 * Once the algorithm terminates, the method generates two output files.
	 * [1]	"perplexityOutFilename" lists the perplexity after each iteration on each line. 
	 * 		The output should look something like:
	 *  	
	 *  	183811
	 *  	79669.9
	 *  	86267.7
	 *  	72260.4
	 *  	75132.4
	 *  
	 *  Where, for example,the 183811 is the perplexity after the first iteration.
	 *
	 * [2] "prOutFilename" prints out the score for each page after the algorithm terminate.
	 * 		The output should look something like:
	 * 		
	 * 		1	0.1235
	 * 		2	0.3542
	 * 		3 	0.236
	 * 		
	 * Where, for example, 0.1235 is the PageRank score of page 1.
	 * 
	 */
	public void runPageRank(String perplexityOutFilename, String prOutFilename){
		
		//System.out.println("boolean "+isConverge());
		while(!isConverge()) {
			double sinkPR = 0;
			for(Integer pageP: noOutlink) {
				sinkPR = sinkPR + PR.get(pageP);
			}
			Map<Integer, Double> newPR = new HashMap<Integer, Double>();
			for(Integer pageP: pidInLine) {
				
				newPR.put(pageP, (1.00-0.85)/pidInLine.size());//newPR(p) = (1-d)/N
				double temp1 = newPR.get(pageP); //
				temp1 = temp1 + (0.85*sinkPR/pidInLine.size()); //newPR(p) += d*sinkPR/N
				newPR.put(pageP, temp1);
//				System.out.println("====newPR "+newPR);
//				System.out.println("=====pageP "+pageP);
				if( setPages.get(pageP) != null) {
					for(Integer pageQ: setPages.get(pageP)) {
//						System.out.println("pageQ "+pageQ);
						//System.out.println("PR "+PR.get(pageQ));
						if(PR.get(pageQ) != null) {
							temp1 += (double) 0.85 * PR.get(pageQ)/setOutlink.get(pageQ);
							newPR.put(pageP, temp1);
						}
					}
				}
				
				
			}
			for(Integer pageP: pidInLine) {
				PR.put(pageP, newPR.get(pageP));
			}
			
			perplexValue.add(this.getPerplexity());
			//System.out.println("perplexValue : "+perplexValue);
			
		}
		//System.out.println("Converged!");
		//System.out.println("pageRank "+PR);
		//System.out.println("perPlexity "+perplexValue);
		//write to file.out
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(prOutFilename))) { // create file
			for (Integer pid : PR.keySet()) { // for each page id -> write pr score
				bufferedWriter.write(pid+" "+PR.get(pid)+"\n");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(perplexityOutFilename))) { // create file
			for (Double perplexity : perplexValue) { // for each iteration -> write perplexity
				bufferedWriter.write(perplexity+"\n");
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Map<Integer, Double> sortByValue(Map<Integer, Double> hashMap)
	{
		// Create a list from elements of HashMap
		List<Map.Entry<Integer, Double> > list = new ArrayList<>(hashMap.entrySet());

		// Sort the list in descending order - highest to lowest score
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// put scores from sorted list to the hashmap
		HashMap<Integer, Double> temp = new LinkedHashMap<>();
		for (Map.Entry<Integer, Double> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}
	
	
	/**
	 * Return the top K page IDs, whose scores are highest.
	 */
	public Integer[] getRankedPages(int K){
		Integer[] topk;
		Map<Integer, Double> sortedPR = sortByValue(PR);
		if(sortedPR.keySet().size() < K) {
			topk = new Integer[sortedPR.keySet().size()];
			sortedPR.keySet().toArray(topk);
		} else {
			topk = new Integer[K];
			int i = 0;
			for(Integer page : sortedPR.keySet()) {
				topk[i] = page;
				i++;
				if(i==K) {
					break;
				}
			}
		}
		return topk;
	}
	
	public static void main(String args[])
	{
		long startTime = System.currentTimeMillis();
		PageRanker pageRanker =  new PageRanker();
		pageRanker.loadData("citeseer.dat");
		pageRanker.initialize();
		pageRanker.runPageRank("perplexity.out", "pr_scores.out");
		Integer[] rankedPages = pageRanker.getRankedPages(100);
		double estimatedTime = (double)(System.currentTimeMillis() - startTime)/1000.0;
		
		System.out.println("Top 100 Pages are:\n"+Arrays.toString(rankedPages));
		System.out.println("Proccessing time: "+estimatedTime+" seconds");
	}
}
