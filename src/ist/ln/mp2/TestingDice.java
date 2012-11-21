package ist.ln.mp2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class TestingDice extends LexicalTest{
	Set<String> knownWords = null;
	
	double threshold = 0.24;

	public TestingDice(Set<String> knownWords) {
		super(knownWords);
		this.knownWords = knownWords;
	}
	
	@Override
	public List<String> test(String word) {
		
		String _word = NormalizerSimple.normPunctLCaseDMarks(word);
		
		List<String> resString = new ArrayList<String>();
		
		for(String s : knownWords){
			
			Dice d = new Dice(_word, s);
			
			System.out.println("----------------------");
			System.out.println("threshold: " + threshold);
			System.out.println("word: " + _word); 
			System.out.println("s: " + s); 
			
			double res = d.checkDice();
			
			System.out.println("res: " + res);
			System.out.println("----------------------");
			
			if( 1-res <= threshold){
				resString.add(s);
			}
		}
		
		
		return resString;
	}


	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		
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
			
			try {
				line = is.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
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
		LexicalTest lt = new TestingDice(words);
		
		// Creating a console for testing
		lt.run();


	}
}
