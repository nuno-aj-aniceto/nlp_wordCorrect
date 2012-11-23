package ist.ln.mp2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

/**
 *  The Class TestingDice.
 * @author Grupo 5
 * @author nº 56917 - Ana Santos 	<annara.snow@gmail.com>
 * @author nº 57384 - Júlio Machado <jules_informan@hotmail.com>
 * @author nº 57682 - Nuno Aniceto 	<nuno.aja@gmail.com>
 * @version 23.November.2012
 */
public class TestingDice extends LexicalTest{
	Set<String> knownWords = null;
	int iRes = 0;
	double _res = 0;
	
	double threshold = 0.125;

	public TestingDice(Set<String> knownWords) {
		super(knownWords);
		this.knownWords = knownWords;
	}
	
	@Override
	public List<String> test(String word) {
		
		String _word = NormalizerSimple.normPunctLCaseDMarks(word);
		
		TreeMap<Double, ArrayList<String>> mapRes = new TreeMap<Double, ArrayList<String>>(Collections.reverseOrder());
		
		for(String s : knownWords){
			
			Dice d = new Dice(_word, s);
			double res = d.checkDice();
			
			
			if( 1-res < threshold){
				
				ArrayList<String> l = new ArrayList<String>();
				
				if(mapRes.containsKey(res)){
					l = mapRes.get(res);
					l.add(s);
				}
				
				else{
					l.add(s);
					mapRes.put(res, l);
				}
				
			}
		}
		
		ArrayList<String> resString = new ArrayList<String>();
		
		for (ArrayList<String> value : mapRes.values()) {
				if(resString.size() > 5){
					return resString;
				}
				else{
					for(String s : value){
						resString.add(s);
						if(resString.size() >= 5){
							return resString;
						}
					}
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
