//Name(s):
//ID
//Section
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeMap;

/**
 * This class implements PageRank algorithm on simple graph structure.
 * Put your name(s), ID(s), and section here.
 *
 */
public class PageRanker {
	
	public static double log2(double x)
	{
	    return (Math.log(x) / Math.log(2) + 1e-10);
	}
	
	/**
	 * This class reads the direct graph stored in the file "inputLinkFilename" into memory.
	 * Each line in the input file should have the following format:
	 * <pid_1> <pid_2> <pid_3> .. <pid_n>
	 * 
	 * Where pid_1, pid_2, ..., pid_n are the page IDs of the page having links to page pid_1. 
	 * You can assume that a page ID is an integer.
	 */
	
	public TreeMap<Integer, ArrayList<Integer>> pageMap = new TreeMap<>();
	public TreeMap<Integer, Double> pageRank = new TreeMap<>();
	public ArrayList<Double> perplexityList = new ArrayList<Double>();
	public TreeMap<Integer, Integer> linkedNode = new TreeMap<Integer, Integer>();
	public HashSet<Integer> sinkNode;
	
	public void loadData(String inputLinkFilename){
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(inputLinkFilename));
			String line = reader.readLine();
			while (line != null) {
				String[] splited = line.split("\\s+");
				ArrayList<Integer> linkIn = new ArrayList<Integer>();
				pageMap.put(Integer.parseInt(splited[0]), linkIn);
				pageRank.put(Integer.parseInt(splited[0]), 0.00);
				for(int i=0; i<splited.length;i++) {
					if(i==0) continue;
					int page = Integer.parseInt(splited[i]);
					pageMap.get(Integer.parseInt(splited[0])).add(page); 
					if(linkedNode.get(page) == null) {
						linkedNode.put(page, 1);
					} else {
						linkedNode.replace(page, linkedNode.get(page)+1);
					}
				}
				// read next line
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		sinkNode = new HashSet<Integer>(pageMap.keySet());
		sinkNode.removeAll(linkedNode.keySet());
		
		
		//System.out.println(pageMap);
	}
	
	/**
	 * This method will be called after the graph is loaded into the memory.
	 * This method initialize the parameters for the PageRank algorithm including
	 * setting an initial weight to each page.
	 */
	public void initialize(){
		for (Integer page : pageRank.keySet()) {
			pageRank.replace(page, 1.0/pageRank.keySet().size());
		}
		//System.out.println(pageRank);
	}
	
	/**
	 * Computes the perplexity of the current state of the graph. The definition
	 * of perplexity is given in the project specs.
	 */
	public double getPerplexity() {
		double sum = 0.0;
		for(Integer page : pageRank.keySet()) {
			sum += pageRank.get(page) * log2(pageRank.get(page));
		}
		sum = Math.pow(2, (-1)*sum);
		//System.out.println(sum);
		return sum;
	}
	
	/**
	 * Returns true if the perplexity converges (hence, terminate the PageRank algorithm).
	 * Returns false otherwise (and PageRank algorithm continue to update the page scores). 
	 */
	public boolean isConverge(){
		perplexityList.add(getPerplexity());
		double changes = perplexityList.get(perplexityList.size()-1) - perplexityList.get(perplexityList.size()-2);
		if(changes < 1) {
			System.out.println("Converged!" + perplexityList.get(perplexityList.size()-1) + " " + perplexityList.get(perplexityList.size()-2));
			return true;
		} else {
			return false;
		}
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
		int i = 0;
		while(true) {
			System.out.println(i);
			if(i >= 4) {
				if(isConverge() == true) {
					break;
				}
			}
			perplexityList.add(getPerplexity());
			double sinkPR = 0.0;
			for (Integer page : sinkNode) {
				sinkPR += pageRank.get(page);
			}
			
			for (Integer page : pageRank.keySet()) {
				double newPR = (1-0.85)/pageRank.keySet().size();
				newPR += 0.85*(sinkPR/pageRank.keySet().size());
				for (Integer q : pageMap.get(page)) {
					newPR += 0.85*pageRank.get(q)/linkedNode.get(page);
				}
				
				pageRank.replace(page, newPR);
			}
			
			i++;
		}
		
		System.out.println(perplexityList);
		System.out.println(pageRank);
		
	}
	
	
	/**
	 * Return the top K page IDs, whose scores are highest.
	 */
	public Integer[] getRankedPages(int K){return null;}
	
	public static void main(String args[])
	{
	long startTime = System.currentTimeMillis();
		PageRanker pageRanker =  new PageRanker();
		pageRanker.loadData("C:\\Users\\VectorXz\\eclipse-workspace\\IR_PJ3\\p3_testcase\\test.dat");
		pageRanker.initialize();
		pageRanker.runPageRank("perplexity.out", "pr_scores.out");
		Integer[] rankedPages = pageRanker.getRankedPages(100);
	double estimatedTime = (double)(System.currentTimeMillis() - startTime)/1000.0;
		
		System.out.println("Top 100 Pages are:\n"+Arrays.toString(rankedPages));
		System.out.println("Proccessing time: "+estimatedTime+" seconds");
	}
}
