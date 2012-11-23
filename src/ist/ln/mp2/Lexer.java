/** 22/Nov/2012 - miniprojecto2 - ist.ln.mp2.Lexer **/
package ist.ln.mp2;

// note to self:
//   >> premature optimization is the root of all evil !
//      ..therefore women may be not! - or may be !?

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * The Class Lexer.
 */

/*
 * TODO
 * >> report
 * >> README
 */
public class Lexer extends LexicalTest {
	/** The Constant maximumResultWords. */
	private static final int maximumResultWords = 5;
	
    /** The flag for printing heuristics only. */
    private boolean verboseMode = false;

	/** The known word nodes. */
	private HashSet<KnownWordNode> knownWordNodes;

	/** The test to the Jaccard index. */
	private boolean testJaccardIndex;
	
	/** The test to the Dice coefficient. */
	private boolean testDiceCoefficient;
	
	/** The test to the minimum edit distance. */
	private boolean testMinimumEditDistance;
	
	/** The test to the Levenshtein distance. */
	private boolean testLevenshteinDistance;
	
	/** The Jaccard index threshold. */
	private float JaccardIndexThreshold;

	/** The Dice coefficient threshold. */
	private float DiceCoefficientThreshold;
	
	/** The minimum edit distance threshold. */
	private int minimumEditDistanceThreshold;

	/** The Levenshtein distance threshold. */
	private int LevenshteinDistanceThreshold;
	
	/** The Jaccard index weight. */
	private float JaccardIndexWeight;

	/** The Dice coefficient weight. */
	private float DiceCoefficientWeight;
		
	/** The minimum edit distance weight. */
	private float minimumEditDistanceWeight;
	
	/** The Levenshtein distance weight. */
	private float LevenshteinDistanceWeight;

	/**
	 * Instantiates a new lexer.
	 *
	 * @param knownWords the known words
	 */
	public Lexer(Set<String> knownWords) {
		super(knownWords);

		this.knownWordNodes = new HashSet<KnownWordNode>(knownWords.size());
		for (String word : knownWords) {
			this.knownWordNodes.add(new KnownWordNode(word));
		}

		/** filtering Thresholds **/
		this.JaccardIndexThreshold 			= 0.5f;
		this.DiceCoefficientThreshold 		= 0.75f;
		this.minimumEditDistanceThreshold 	= 8;
		this.LevenshteinDistanceThreshold 	= 9;

		/** weight values for balancing data **/
		this.JaccardIndexWeight 		= 1.0f;
		this.DiceCoefficientWeight 		= 1.0f;
		this.minimumEditDistanceWeight 	= 0.75f;
		this.LevenshteinDistanceWeight 	= 1.25f;
		
		/** booleans to enable different technique **/
		this.testJaccardIndex 		 = true;
		this.testDiceCoefficient 	 = true;
		this.testMinimumEditDistance = true;
		this.testLevenshteinDistance = true;
	}

	/* (non-Javadoc)
	 * @see ist.ln.mp2.LexicalTest#test(java.lang.String)
	 */
	@Override
	public List<String> test(String word) {
		String normalizedWord = NormalizerSimple.normPunctLCaseDMarks(word);
		ArrayList<String> result = new ArrayList<String>(maximumResultWords);
		ArrayList<OutputNode> evaluationTable = new ArrayList<OutputNode>();

		if(super.getKnownWords().contains(word)) {
			result.add(word);
		} else { 
			for (KnownWordNode knownNode : knownWordNodes) {
				String knownNormalizedWord = knownNode.getNormalizedString();

				float JaccardValue = 1, DiceValue = 1;
				int MinimumEditDistanceValue = 0;
				int LevenshteinDistanceValue = 0;
				
				if(testJaccardIndex) {
					// compare (normalized) strings using Jaccard's Index
					JaccardValue = computeJaccard(normalizedWord,
							knownNormalizedWord);
					if (JaccardValue < this.JaccardIndexThreshold)
						continue;
				}

				if(testDiceCoefficient) {
					// compare (normalized) strings using Dice's Coefficient
					DiceValue = computeDice(normalizedWord,
							knownNormalizedWord);
					if (DiceValue < this.DiceCoefficientThreshold)
						continue;
				}
				
				if(testMinimumEditDistance) {
					// compare (normalized) strings using Minimum Edit Distance
					MinimumEditDistanceValue = computeMinimumEditDistance(
							normalizedWord, knownNormalizedWord);
					if (MinimumEditDistanceValue > this.minimumEditDistanceThreshold)
						continue;
				}

				if(testLevenshteinDistance) {
					// compare (normalized) strings using Levenshtein's Distance
					LevenshteinDistanceValue = computeLevenshteinDistance(
							normalizedWord, knownNormalizedWord);
					if (LevenshteinDistanceValue > this.LevenshteinDistanceThreshold)
						continue;
				}

				// heuristic to sort results ------
                float heuristic = 0;
                
                if(testJaccardIndex)
                	heuristic += JaccardIndexWeight*JaccardValue;
                if(testDiceCoefficient)
                	heuristic += DiceCoefficientWeight*DiceValue;
                
                if(testJaccardIndex || testDiceCoefficient) {
	                if(testMinimumEditDistance && testLevenshteinDistance)
	                	heuristic /= (minimumEditDistanceWeight*MinimumEditDistanceValue
									+ LevenshteinDistanceWeight*LevenshteinDistanceValue);
	                else {
	                	if(testMinimumEditDistance)
	                		heuristic /= minimumEditDistanceWeight*MinimumEditDistanceValue;
	                	if(testLevenshteinDistance)
	                		heuristic /= LevenshteinDistanceWeight*LevenshteinDistanceValue;
	                }
                } else { // only testing with Minimum Edit Distance or Levenshtein Distance
                	if(testMinimumEditDistance && testLevenshteinDistance)
	                	heuristic = -(minimumEditDistanceWeight*MinimumEditDistanceValue
									+ LevenshteinDistanceWeight*LevenshteinDistanceValue);
	                else {
	                	if(testMinimumEditDistance)
	                		heuristic = -(minimumEditDistanceWeight*MinimumEditDistanceValue);
	                	if(testLevenshteinDistance)
	                		heuristic = -(LevenshteinDistanceWeight*LevenshteinDistanceValue);
	                }
                }
                
                if(verboseMode)
                	System.out.format("[verboseMode] h: %3.5f -- [%s]\n", heuristic, knownNormalizedWord);
				
                // output node that contains every detail (so sorting is easy) ------
				OutputNode outputNode = new OutputNode(knownNode);

				if(testJaccardIndex)
					outputNode.setJaccardValue(JaccardValue);
				if(testDiceCoefficient)
					outputNode.setDiceValue(DiceValue);
				if(testMinimumEditDistance)
					outputNode.setMinimumEditDistanceValue(MinimumEditDistanceValue);
				if(testLevenshteinDistance) 
					outputNode.setLevenshteinDistanceValue(LevenshteinDistanceValue);

				outputNode.setHeuristicValue(-heuristic*1000000);

				evaluationTable.add(outputNode);
			}

			// sort the result's for their values, we want the best of them !
			final Comparator<OutputNode> OUTPUTNODE_COMPARATOR = new Comparator<OutputNode>() {
				@Override
				public int compare(OutputNode a, OutputNode b) {
					return (int) (a.getHeuristicValue() - b
							.getHeuristicValue());
				}
			};
			Collections.sort(evaluationTable, OUTPUTNODE_COMPARATOR);

			// prepares the output
			for (OutputNode outputNode : evaluationTable) {
				result.add(outputNode.getInputNode().getOriginalString());

				if (result.size() >= maximumResultWords)
					break;
			}
		}

		return result;
	}

	/**
	 * Load config.
	 *
	 * @param pathToFile the path to file
	 */
	public void loadConfig(String pathToFile) {
		if(verboseMode)
			System.out.println("[verboseMode] loading config file: " + pathToFile);
		
		try {
            File file = new File(pathToFile);
			Scanner lineScanner = new Scanner(file);
			
			String line;
			while(lineScanner.hasNextLine()) {
				line = lineScanner.nextLine();
				line.trim();

				// ignore blank lines
				if(line.isEmpty())
					continue;

				// ignore comentaries
				if(line.length() >= 1 && line.startsWith("#")
				|| line.length() >= 2 && line.startsWith("//"))
					continue;
				
				// set verbose mode for printing heuristics
				if(line.equals("verboseMode")) {
					this.verboseMode = true;
					continue;
				}
				
				// enablers -- to choose which technique will be applied
				if(line.equalsIgnoreCase("enableJaccardTest")) {
					this.testJaccardIndex = true;
					continue;
				}
				else if(line.equalsIgnoreCase("enableDiceTest")) {
					this.testDiceCoefficient = true;
					continue;
				}
				else if(line.equalsIgnoreCase("enableMedTest")) {
					this.testMinimumEditDistance = true;
					continue;
				}
				else if(line.equalsIgnoreCase("enableLevenshteinTest")) {
					this.testLevenshteinDistance = true;
					continue;
				}
				else if(line.equalsIgnoreCase("enableAllTests")) {
					this.testJaccardIndex = true;
					this.testDiceCoefficient = true;
					this.testMinimumEditDistance = true;
					this.testLevenshteinDistance = true;
					continue;
				}
				
				// disablers -- to choose which technique will not be applied
				if(line.equalsIgnoreCase("disableJaccardTest")) {
					this.testJaccardIndex = false;
					continue;
				}
				else if(line.equalsIgnoreCase("disableDiceTest")) {
					this.testDiceCoefficient = false;
					continue;
				}
				else if(line.equalsIgnoreCase("disableMedTest")) {
					this.testMinimumEditDistance = false;
					continue;
				}
				else if(line.equalsIgnoreCase("disableLevenshteinTest")) {
					this.testLevenshteinDistance = false;
					continue;
				}
				else if(line.equalsIgnoreCase("disableAllTests")) {
					this.testJaccardIndex = false;
					this.testDiceCoefficient = false;
					this.testMinimumEditDistance = false;
					this.testLevenshteinDistance = false;
					continue;
				}

				String tokens[] = line.split("[ \t=]+");
				
				if(tokens.length != 2)
					continue;	
				
				// check for the thresholds
				if (tokens[0].equalsIgnoreCase("JaccardIndexThreshold")) {
					this.JaccardIndexThreshold = Float.parseFloat(tokens[1]);
					continue;
				} else if (tokens[0]
						.equalsIgnoreCase("DiceCoefficientThreshold")) {
					this.DiceCoefficientThreshold = Float.parseFloat(tokens[1]);
					continue;
				} else if (tokens[0]
						.equalsIgnoreCase("minimumEditDistanceThreshold")) {
					this.minimumEditDistanceThreshold = Integer.parseInt(tokens[1]);
					continue;
				} else if (tokens[0]
						.equalsIgnoreCase("LevenshteinDistanceThreshold")) {
					this.LevenshteinDistanceThreshold = Integer.parseInt(tokens[1]);
					continue;
				}

				// check for the weights
				if (tokens[0].equalsIgnoreCase("JaccardIndexWeight")) {
					this.JaccardIndexWeight = Float.parseFloat(tokens[1]);
					continue;
				} else if (tokens[0].equalsIgnoreCase("DiceCoefficientWeight")) {
					this.DiceCoefficientWeight = Float.parseFloat(tokens[1]);
					continue;
				} else if (tokens[0]
						.equalsIgnoreCase("minimumEditDistanceWeight")) {
					this.minimumEditDistanceWeight = Float.parseFloat(tokens[1]);
					continue;
				} else if (tokens[0].equalsIgnoreCase("LevenshteinDistanceWeight")) {
					this.LevenshteinDistanceWeight = Float.parseFloat(tokens[1]);
					continue;
					
				}
			}
			
			lineScanner.close();
		} catch(FileNotFoundException e) {
			System.out.println("ERROR: could not load configuration's file '" + pathToFile + "'");
			e.printStackTrace();
		}

        /** filtering Thresholds **/
		if(verboseMode) {
			System.out.println("[verboseMode ON]");
			System.out.println();
			System.out.println(">> configuration ------------------------");
			System.out.println();
			System.out.println("--- flags ---");
	        System.out.println("testJaccardIndex             = " + this.testJaccardIndex);
	        System.out.println("testDiceCoefficient          = " + this.testDiceCoefficient);
	        System.out.println("testMinimumEditDistance      = " + this.testMinimumEditDistance);
	        System.out.println("testLevenshteinDistance      = " + this.testLevenshteinDistance);
	        System.out.println("--- thresholds ---");
	        System.out.println("JaccardIndexThreshold        = " + this.JaccardIndexThreshold);
	        System.out.println("DiceCoefficientThreshold     = " + this.DiceCoefficientThreshold);
	        System.out.println("minimumEditDistanceThreshold = " + this.minimumEditDistanceThreshold);
	        System.out.println("LevenshteinDistanceThreshold = " + this.LevenshteinDistanceThreshold);
	        System.out.println("--- weights ---");
	        System.out.println("JaccardIndexWeight           = " + this.JaccardIndexWeight);
	        System.out.println("DiceCoefficientWeight        = " + this.DiceCoefficientWeight);
	        System.out.println("minimumEditDistanceWeight    = " + this.minimumEditDistanceWeight);
	        System.out.println("LevenshteinDistanceWeight    = " + this.LevenshteinDistanceWeight);
	        System.out.println();
	        System.out.println("<< --------------------------------------");
			System.out.println();
		}
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		HashSet<String> words = new HashSet<String>();

		// We only accept one argument, any other invocations are errors
		if (args.length != 1) {
			System.err
					.println("Expecting the path of the file with the known words. Got "
							+ args.length + " arguments!");
			System.exit(-1);
		}

		// Loading the knownWords set from the text file
		File f = new File(args[0]);

		FileReader reader = new FileReader(f);
		BufferedReader is = new BufferedReader(reader);

		System.out.println("Loading known words...");
		while (true) {
			String line = null;

			line = is.readLine();

			if (line == null) {
				break;
			}

			line = line.trim();
			if (!line.isEmpty()) {
				words.add(line);
			}
		}
		// this was missing on your file -- and could lead to memory leaks !
		is.close();

		// Creating the lexical test
		LexicalTest lt = new Lexer(words);
		
		((Lexer) lt).loadConfig("../resources/Lexer.conf");

		// Creating a console for testing
		lt.run();
	}

	/**
	 * Union.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the hash set
	 */
	public static HashSet<Character> union(HashSet<Character> x, HashSet<Character> y) {
		HashSet<Character> t = new HashSet<Character>(x);
		t.addAll(y);
		return t;
	}

	/**
	 * Intersection.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the hash set
	 */
	public static HashSet<Character> intersection(HashSet<Character> x, HashSet<Character> y) {
		HashSet<Character> t = new HashSet<Character>(x);
		t.retainAll(y);
		return t;
	}

	/**
	 * String to set.
	 *
	 * @param string the string
	 * @return the hash set
	 */
	public static HashSet<Character> stringToSet(String string) {
		HashSet<Character> set = new HashSet<Character>();
		
		for (int i = 0; i < string.length(); i++) {
			set.add(string.charAt(i));
		}
		return set;
	}

	/**
	 * Compute jaccard.
	 *
	 * @param sourceString the source string
	 * @param targetString the target string
	 * @return the float
	 */
	public static float computeJaccard(String sourceString, String targetString) {
		float result;
		
		HashSet<Character> source = stringToSet(sourceString);
		HashSet<Character> target = stringToSet(targetString);
		
		HashSet<Character> intersection = intersection(source, target);
		HashSet<Character> union = union(source, target);
		
		result = ((float)intersection.size())/((float)union.size());
		
		return result;
	}

	/**
	 * Compute dice.
	 *
	 * @param sourceString the source string
	 * @param targetString the target string
	 * @return the float
	 */
	public static float computeDice(String sourceString, String targetString) {
		float result;
		
		HashSet<Character> source = stringToSet(sourceString);
		HashSet<Character> target = stringToSet(targetString);
		
		HashSet<Character> intersection = intersection(source, target);
		
		result = ((float)2*intersection.size())/((float)(source.size()+target.size()));
		
		return result;
	}

	/**
	 * Compute minimum edit distance.
	 *
	 * @param sourceString the source string
	 * @param targetString the target string
	 * @return the int
	 */
	public static int computeMinimumEditDistance(String sourceString, String targetString) {
		return computeMinimumEditDistance(sourceString, targetString, 1, 1, 1);
	}

	/**
	 * Compute levenshtein distance.
	 *
	 * @param sourceString the source string
	 * @param targetString the target string
	 * @return the int
	 */
	public static int computeLevenshteinDistance(String sourceString, String targetString) {
		return computeMinimumEditDistance(sourceString, targetString, 1, 1, 2);
	}

	/**
	 * Compute minimum edit distance.
	 *
	 * @param sourceString the source string
	 * @param targetString the target string
	 * @param c1 the c1
	 * @param c2 the c2
	 * @param c3 the c3
	 * @return the int
	 */
	public static int computeMinimumEditDistance(String sourceString,
			String targetString, int c1, int c2, int c3) {
		int n = sourceString.length(); // number of columns-1
		int m = targetString.length(); // number of rows-1
		int distance = 0;

		// if one word is not provided, return the other as MED
		if (n == 0)
			return m;
		if (m == 0)
			return n;

		// MED Matrix for data enclosure
		int medMatrix[][] = new int[m + 1][n + 1];
	
		// sets the first line of the matrix
		for (int a = 0; a < n + 1; a++)
			medMatrix[0][a] = a;

		// sets the first column of the matrix
		for (int b = 0; b < m + 1; b++)
			medMatrix[b][0] = b;

		// sets the rest of the matrix (0 value)
		for (int i = 1; i < m + 1; i++) {
			for (int j = 1; j < n + 1; j++) {
				medMatrix[i][j] = 0;
			}
		}

		// computes the result using dynamic programming
		for (int j = 1; j <= n; j++) {
			for (int i = 1; i <= m; i++) {
				if (sourceString.charAt(j - 1) == targetString.charAt(i - 1)) {
					medMatrix[i][j] = medMatrix[i - 1][j - 1];
				} else {
					medMatrix[i][j] = 
							min(
									medMatrix[i - 1][j] + c1, 		// deletion
									medMatrix[i][j - 1] + c2, 		// insertion
									medMatrix[i - 1][j - 1] + c3 	// substitution
								);
				}
			}
		}

		// the result
		distance = medMatrix[m][n];
		
		return distance;
	}

	/**
	 * Min.
	 *
	 * @param a the a
	 * @param b the b
	 * @param c the c
	 * @return the int
	 */
	public static int min(int a, int b, int c) {
		return Math.min(a, Math.min(b, c));
	}

	/**
	 * Gets the known word nodes.
	 *
	 * @return the known word nodes
	 */
	public HashSet<KnownWordNode> getKnownWordNodes() {
		return knownWordNodes;
	}

	/**
	 * Sets the known word nodes.
	 *
	 * @param knownWordNodes the new known word nodes
	 */
	public void setKnownWordNodes(HashSet<KnownWordNode> knownWordNodes) {
		this.knownWordNodes = knownWordNodes;
	}

	/**
	 * Gets the maximum result words.
	 *
	 * @return the maximum result words
	 */
	public int getMaximumResultWords() {
		return maximumResultWords;
	}

	/**
	 * Gets the minimum edit distance threshold.
	 *
	 * @return the minimum edit distance threshold
	 */
	public int getMinimumEditDistanceThreshold() {
		return minimumEditDistanceThreshold;
	}

	/**
	 * Sets the minimum edit distance threshold.
	 *
	 * @param minimumEditDistanceThreshold the new minimum edit distance threshold
	 */
	public void setMinimumEditDistanceThreshold(int minimumEditDistanceThreshold) {
		this.minimumEditDistanceThreshold = minimumEditDistanceThreshold;
	}

	/**
	 * Gets the levenshtein distance threshold.
	 *
	 * @return the levenshtein distance threshold
	 */
	public int getLevenshteinDistanceThreshold() {
		return LevenshteinDistanceThreshold;
	}

	/**
	 * Sets the levenshtein distance threshold.
	 *
	 * @param levenshteinDistanceThreshold the new levenshtein distance threshold
	 */
	public void setLevenshteinDistanceThreshold(int levenshteinDistanceThreshold) {
		LevenshteinDistanceThreshold = levenshteinDistanceThreshold;
	}

	/**
	 * Gets the jaccard index threshold.
	 *
	 * @return the jaccard index threshold
	 */
	public float getJaccardIndexThreshold() {
		return JaccardIndexThreshold;
	}

	/**
	 * Sets the jaccard index threshold.
	 *
	 * @param JaccardIndexThreshold the new jaccard index threshold
	 */
	public void setJaccardIndexThreshold(float JaccardIndexThreshold) {
		this.JaccardIndexThreshold = JaccardIndexThreshold;
	}

	/**
	 * Gets the dice coefficient threshold.
	 *
	 * @return the dice coefficient threshold
	 */
	public float getDiceCoefficientThreshold() {
		return DiceCoefficientThreshold;
	}

	/**
	 * Sets the dice coefficient threshold.
	 *
	 * @param diceCoefficientThreshold the new dice coefficient threshold
	 */
	public void setDiceCoefficientThreshold(float diceCoefficientThreshold) {
		DiceCoefficientThreshold = diceCoefficientThreshold;
	}

	/**
	 * Gets the jaccard index weight.
	 *
	 * @return the jaccard index weight
	 */
	public float getJaccardIndexWeight() {
		return JaccardIndexWeight;
	}

	/**
	 * Sets the jaccard index weight.
	 *
	 * @param jaccardIndexWeight the new jaccard index weight
	 */
	public void setJaccardIndexWeight(float jaccardIndexWeight) {
		JaccardIndexWeight = jaccardIndexWeight;
	}

	/**
	 * Gets the dice coefficient weight.
	 *
	 * @return the dice coefficient weight
	 */
	public float getDiceCoefficientWeight() {
		return DiceCoefficientWeight;
	}

	/**
	 * Sets the dice coefficient weight.
	 *
	 * @param diceCoefficientWeight the new dice coefficient weight
	 */
	public void setDiceCoefficientWeight(float diceCoefficientWeight) {
		DiceCoefficientWeight = diceCoefficientWeight;
	}

	/**
	 * Gets the minimum edit distance weight.
	 *
	 * @return the minimum edit distance weight
	 */
	public float getMinimumEditDistanceWeight() {
		return minimumEditDistanceWeight;
	}

	/**
	 * Sets the minimum edit distance weight.
	 *
	 * @param minimumEditDistanceWeight the new minimum edit distance weight
	 */
	public void setMinimumEditDistanceWeight(float minimumEditDistanceWeight) {
		this.minimumEditDistanceWeight = minimumEditDistanceWeight;
	}

	/**
	 * Gets the levenshtein distance weight.
	 *
	 * @return the levenshtein distance weight
	 */
	public float getLevenshteinDistanceWeight() {
		return LevenshteinDistanceWeight;
	}

	/**
	 * Sets the levenshtein distance weight.
	 *
	 * @param levenshteinDistanceWeight the new levenshtein distance weight
	 */
	public void setLevenshteinDistanceWeight(float levenshteinDistanceWeight) {
		LevenshteinDistanceWeight = levenshteinDistanceWeight;
	}

	/**
	 * @return the testJaccardIndex
	 */
	public boolean isTestJaccardIndex() {
		return testJaccardIndex;
	}

	/**
	 * @param testJaccardIndex the testJaccardIndex to set
	 */
	public void setTestJaccardIndex(boolean testJaccardIndex) {
		this.testJaccardIndex = testJaccardIndex;
	}

	/**
	 * @return the testDiceCoefficient
	 */
	public boolean isTestDiceCoefficient() {
		return testDiceCoefficient;
	}

	/**
	 * @param testDiceCoefficient the testDiceCoefficient to set
	 */
	public void setTestDiceCoefficient(boolean testDiceCoefficient) {
		this.testDiceCoefficient = testDiceCoefficient;
	}

	/**
	 * @return the testMinimumEditDistance
	 */
	public boolean isTestMinimumEditDistance() {
		return testMinimumEditDistance;
	}

	/**
	 * @param testMinimumEditDistance the testMinimumEditDistance to set
	 */
	public void setTestMinimumEditDistance(boolean testMinimumEditDistance) {
		this.testMinimumEditDistance = testMinimumEditDistance;
	}

	/**
	 * @return the testLevenshteinDistance
	 */
	public boolean isTestLevenshteinDistance() {
		return testLevenshteinDistance;
	}

	/**
	 * @param testLevenshteinDistance the testLevenshteinDistance to set
	 */
	public void setTestLevenshteinDistance(boolean testLevenshteinDistance) {
		this.testLevenshteinDistance = testLevenshteinDistance;
	}
	
	
//		Testes contra string "Saturday"
//	String target = "saturday";
//	System.out.print("source -- size: " + Lexer.stringToSet(word).size() + " --- ");
//	System.out.println(Lexer.stringToSet(word));
//	System.out.print("\ntarget -- size: " + Lexer.stringToSet(target).size() + " --- ");
//	System.out.println(Lexer.stringToSet(target));
//	
//	System.out.println("\nunion -- size: " + Lexer.union(Lexer.stringToSet(word), Lexer.stringToSet(target)).size() + " --- ");
//	System.out.print(Lexer.union(Lexer.stringToSet(word), Lexer.stringToSet(target)));
//	System.out.println("\nintersection -- size: " + Lexer.intersection(Lexer.stringToSet(word), Lexer.stringToSet(target)).size());
//	System.out.print(Lexer.intersection(Lexer.stringToSet(word), Lexer.stringToSet(target)) + " --- ");
//	System.out.println("\nJaccard:    " + Lexer.computeJaccard(word, target));
//	System.out.println("\nDice: " + Lexer.computeDice(word, target));
}
