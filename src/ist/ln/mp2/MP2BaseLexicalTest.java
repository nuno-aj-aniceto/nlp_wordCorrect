package ist.ln.mp2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class that implements a LexicalTest by returning the given word if its on the knownWords set
 */
public class MP2BaseLexicalTest extends LexicalTest {
	/**
	 * Default constructor
	 * Processing of the knownWords set into other data structures, required by the algorithms in test method, should be done here
	 * @param knownWords
	 */
	public MP2BaseLexicalTest(Set<String> knownWords) {
		super(knownWords);
	}

	/**
	 * Implementation of the test method required by LexicalTest.
	 * This implementation only returns the word if it is present on the knowWords set.
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
	 * Example of main that reads the text file containing the knownWords, loads the set and creates a MP2BasicLexicalTest with console for testing
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
		
		// Creating the lexical test
		LexicalTest lt = new MP2BaseLexicalTest(words);
		
		// Creating a console for testing
		lt.run();
	}
}
