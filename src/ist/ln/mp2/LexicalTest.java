/** [miniprojecto2] - ist.ln.mp2/LexicalTest.java - 18/Nov/2012 **/
package ist.ln.mp2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;

/**
 * Abstract class that represents a LexicalTest.
 * To get a fully working test, the method List<String> test(String word) has to be implemented by extension of this class. 
 */
public abstract class LexicalTest implements Runnable {
	private Set<String> knownWords;
	
	/**
	 * Creates a new LexicalTest with the given known words set
	 * @param knownWords Set of words that know for the context of this lexical test
	 */
	public LexicalTest(Set<String> knownWords) {
		this.knownWords = knownWords;
	}
	
	/**
	 * Getter for knownWords set
	 * @return Set of words that know for the context of this lexical test
	 */
	public Set<String> getKnownWords() {
		return knownWords;
	}

	/**
	 * Method that given a word selects suggestions present on the knownWords set (see getKnownWords())
	 * @param word Word to be paired with the known words
	 * @return List of words suggested, present on knownWords set, that are associated to the given word, ordered form the strongest connection to the weakest connection 
	 */
	public abstract List<String> test(String word);
	
	/**
	 * Implementation of the Runnable interface that allows the creation of a prompt to test the LexicalTest implementations
	 */
	@Override
	public void run() {
		InputStreamReader converter = new InputStreamReader(System.in);
		BufferedReader is = new BufferedReader(converter);
		
		System.out.println("Type the words seperated by space or CTRL+C to exit: ");
		
		while(true) {
			System.out.print("> "); // println
			String line = null;
			try {
				line = is.readLine();
			} catch (IOException e) {
				System.err.println("LexicalTest: Error while retrieving the line to test!");
				e.printStackTrace();
				System.exit(-2);
			}
			
			// end of stream case
			if(line == null)
				System.exit(0);
			
			line = line.trim();
			
			if(line.isEmpty()){
				// no input was given by the user
				System.out.println("Empty line recived, type words seperated by space or CTRL+C to exit.");
			} else {
				// processing the input
				String[] words = line.split(" +");

				for(String word : words){
					List<String> results = this.test(word);

					if(results.isEmpty()){
						System.out.println("No candidates found for the word '"+word+"'");
					} else {
						System.out.println("For the word '"+word+"' found the following candidates:");
						for(String result : results){
							System.out.println(" - "+result);
						}
					}
				}
			}
		}	
	}
	
	
}
