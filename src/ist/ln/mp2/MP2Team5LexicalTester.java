/** [miniprojecto2] - ist.ln.mp2/MP2Team5LexicalTester.java - 20/Nov/2012 **/
package ist.ln.mp2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class MP2Team5LexicalTester.
 */
public class MP2Team5LexicalTester extends LexicalTest {

	/**
	 * Default constructor
	 * Processing of the knownWords set into other data structures, required by the algorithms in test method, should be done here
	 * @param knownWords
	 */
	public MP2Team5LexicalTester(Set<String> knownWords) {
		super(knownWords);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Compute med.
	 *
	 * @return the int
	 */
	public int computeMED(String sourceString, String targetString) {
		int c1, c2, c3;
		c1 = c2 = c3 = 1;
		int n = sourceString.length(); /*number of columns-1*/
		int m = targetString.length(); /*number of rows-1*/
		int distance = 0;

		/*if one word is not provided, return the other as MED*/
		if(n == 0){
			return m;
		} else if (m == 0) {
			return n;
		}

		int medMatrix [][]  = new int [m+1][n+1]; /*MED Matrix for data enclosure*/
		for(int a=0; a < n+1; a++) { //first matrix line init
			medMatrix[0][a] = a;
		}
		for(int b=0; b < m+1; b++){ //first matrix column init
			medMatrix[b][0] = b;
		}

		for(int i=1; i<m+1; i++){ //initializing  the rest of the matrix cells with zero values
			for(int j =1; j<n+1; j++){
				medMatrix[i][j] = 0;
			}
		}




		/*computing MED(Levenshtein Distance) using Wagner-Fischer Algorithm */
		for(int j=1; j <= n; j++){ 
			for(int i=1; i <= m; i++){
				if(sourceString.charAt(j-1) == targetString.charAt(i-1)) {
					medMatrix [i][j] = medMatrix[i-1][j-1];
				}else {
					medMatrix [i][j] = min(medMatrix[i-1][j]+c1, /*deletion*/
							medMatrix[i][j-1]+c2,  /*insertion*/
							medMatrix[i-1][j-1]+c3); /*substitution*/
				}
			}
		}

		distance = medMatrix[m][n]; /*contains the value of MED*/
		System.out.println("distancia minima de edicao entre "+sourceString+" e "+targetString+" = "+ distance);
		
		return distance;
	}
	
	/**
	 * Implementation of the test method required by LexicalTest.
	 * This implementation only returns the word if it is present on the knowWords set.
	 * @see ist.ln.mp2.LexicalTest#test(java.lang.String)
	 */
	@Override
	public List<String> test(String word) {
		ArrayList<String> result = new ArrayList<String>();
		
		/* knownWords set can be access by calling the method: 
		 * 		getKnownWords();
		 */
		
		/* a result can be added by calling the method 
		 * 		result.add(...);
		 * exemplified bellow for the word "construíram" (confidence should be a value between 0.000 and 1.000):
		 *
		 * String suggestedWord = "construiu";
		 * result.add(new LexicalTestResult(suggestedWord);
		 */
		
		if(getKnownWords().contains(word)){
			result.add(word);
		}
		
		return result;
	}
	
	/**
	 * Mininum value between three values.
	 *
	 * @param a the first value
	 * @param b the second value
	 * @param c the third value
	 * @return the mininum of {a; b; c}
	 */
	public int min(int a, int b, int c) {
		return Math.min(a, Math.min(b, c));
	}
	
	/**
	 * The main method.
	 * @param args One argument only indicating the path to the text file with the knownWords
	 * @throws IOException Exception given if there is an error opening or manipulating the knownWords file
	 */
	public static void main(String[] args) throws IOException{
		HashSet<String> words = new HashSet<String>();
		
		// We only accept one argument, any other invocations are errors
		if(args.length != 1){
			System.err.println("Expecting the path of the file with the known words. Got "+args.length+" arguments!");
			System.exit(-1);
		}
		
		// Loading the knownWords set from the text file
		File f = new File(args[0]);
		
		FileReader reader = new FileReader(f);
		BufferedReader is = new BufferedReader(reader);
		
		System.out.println("Loading known words...");
		while(true) {
			String line = null;
			
			line = is.readLine();
			
			if(line == null){
				break;
			}
			
			line = line.trim();
			if(!line.isEmpty()){
				words.add(line);
			}
		}
		is.close();
		
		// Creating the lexical test
		LexicalTest lt = new MP2BaseLexicalTest(words);
		
		// Creating a console for testing
		lt.run();
	}

}
