/** 22/Nov/2012 - miniprojecto2 - ist.ln.mp2.Lexer **/
package ist.ln.mp2;

// note to self:
//   >> premature optimization is the root of all evil !
//      ..therefore women may be not! - or may be !?

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// TODO: Auto-generated Javadoc3
/**
 * The Class Lexer.
 */
public class Lexer extends LexicalTest {

	/** The known word nodes. */
	private HashSet<KnownWordNode> knownWordNodes;

	/** The maximum result words. (set for project as 5) */
	private static final int maximumResultWords = 5;


	/** The Jaccard's index threshold. */
	private float JaccardIndexThreshold;

	/** The Dice's coefficient threshold. */
	private float DiceCoefficientThreshold;
	
	/** The minimum edit distance threshold. */
	private int minimumEditDistanceThreshold;

	/** The Levenshtein distance threshold. */
	private int LevenshteinDistanceThreshold;
	
	/** The Jaccard's Index's weight. */
	private float JaccardIndexWeight;

	/** The Dice's Coefficient's weight. */
	private float DiceCoefficientWeight;
		
	/** The minimum edit distance's weight. */
	private float minimumEditDistanceWeight;
	
	/** The Levenshtein distance's weight. */
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
		this.minimumEditDistanceThreshold 	= 4;
		this.LevenshteinDistanceThreshold 	= 6;
		this.DiceCoefficientThreshold 		= 0.75f;
		this.JaccardIndexThreshold 			= 0.5f;

		/** weight values for balancing data **/
		this.JaccardIndexWeight 		= 1.0f;
		this.DiceCoefficientWeight 		= 1.0f;
		this.minimumEditDistanceWeight 	= 1.0f;
		this.LevenshteinDistanceWeight 	= 1.0f;
	}

	/* (non-Javadoc)
	 * @see ist.ln.mp2.LexicalTest#test(java.lang.String)
	 */
	@Override
	public List<String> test(String word) {
// 		Testes contra string "Saturday"
//		String target = "saturday";
//		System.out.print("source -- size: " + Lexer.stringToSet(word).size() + " --- ");
//		System.out.println(Lexer.stringToSet(word));
//		System.out.print("\ntarget -- size: " + Lexer.stringToSet(target).size() + " --- ");
//		System.out.println(Lexer.stringToSet(target));
//		
//		System.out.println("\nunion -- size: " + Lexer.union(Lexer.stringToSet(word), Lexer.stringToSet(target)).size() + " --- ");
//		System.out.print(Lexer.union(Lexer.stringToSet(word), Lexer.stringToSet(target)));
//		System.out.println("\nintersection -- size: " + Lexer.intersection(Lexer.stringToSet(word), Lexer.stringToSet(target)).size());
//		System.out.print(Lexer.intersection(Lexer.stringToSet(word), Lexer.stringToSet(target)) + " --- ");
//		System.out.println("\nJaccard:    " + Lexer.computeJaccard(word, target));
//		System.out.println("\nDice: " + Lexer.computeDice(word, target));
		
		String normalizedWord = NormalizerSimple.normPunctLCaseDMarks(word);
		ArrayList<String> result = new ArrayList<String>(maximumResultWords);
		ArrayList<OutputNode> evaluationTable = new ArrayList<OutputNode>();

		if(super.getKnownWords().contains(word)) {
			result.add(word);
		} else { 
			for (KnownWordNode knownNode : knownWordNodes) {
				String knownNormalizedWord = knownNode.getNormalizedString();

				/* Not being used
					//double JaccardValue = (new Jaccard(normalizedWord, knownNormalizedWord)).checkJaccard();
					//double DiceValue = (new Dice(normalizedWord, knownNormalizedWord)).checkDice();
				*/

				// compare (normalized) strings using Jaccard's Index
				float JaccardValue = computeJaccard(normalizedWord,
						knownNormalizedWord);
				if (JaccardValue < this.JaccardIndexThreshold)
					continue;

				// compare (normalized) strings using Dice's Coefficient
				float DiceValue = computeDice(normalizedWord,
						knownNormalizedWord);
				if (DiceValue < this.DiceCoefficientThreshold)
					continue;

				// compare (normalized) strings using Minimum Edit Distance
				// [Wagner-Fischer algorithm]
				int MinimumEditDistanceValue = computeMinimumEditDistance(
						normalizedWord, knownNormalizedWord);
				if (MinimumEditDistanceValue > this.minimumEditDistanceThreshold)
					continue;

				// compare (normalized) strings using Levenshtein's Distance
				int LevenshteinDistanceValue = computeLevenshteinDistance(
						normalizedWord, knownNormalizedWord);
				if (LevenshteinDistanceValue > this.LevenshteinDistanceThreshold)
					continue;

				// heuristic to sort results ------
				float heuristic;
				heuristic = JaccardIndexWeight*JaccardValue + DiceCoefficientWeight*DiceValue;
				heuristic *= (minimumEditDistanceWeight*MinimumEditDistanceValue
							+ LevenshteinDistanceWeight*LevenshteinDistanceValue);

				// output node that contains every detail (so sorting is easy) ------
				OutputNode outputNode = new OutputNode(knownNode);

				outputNode.setDiceValue(DiceValue);
				outputNode.setJaccardValue(JaccardValue);
				outputNode.setLevenshteinDistanceValue(LevenshteinDistanceValue);
				outputNode.setMinimumEditDistanceValue(MinimumEditDistanceValue);
				outputNode.setHeuristicValue(heuristic);

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
					medMatrix[i][j] = min(medMatrix[i - 1][j] + c1, // deletion
							medMatrix[i][j - 1] + c2, // insertion
							medMatrix[i - 1][j - 1] + c3); // substitution
				}
			}
		}

		// the result
		distance = medMatrix[m][n];

		// System.out.println("distancia minima de edicao entre "+sourceString+" e "+targetString+" = "+
		// distance);
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
	 * @return the jaccardIndexWeight
	 */
	public float getJaccardIndexWeight() {
		return JaccardIndexWeight;
	}

	/**
	 * @param jaccardIndexWeight the jaccardIndexWeight to set
	 */
	public void setJaccardIndexWeight(float jaccardIndexWeight) {
		JaccardIndexWeight = jaccardIndexWeight;
	}

	/**
	 * @return the diceCoefficientWeight
	 */
	public float getDiceCoefficientWeight() {
		return DiceCoefficientWeight;
	}

	/**
	 * @param diceCoefficientWeight the diceCoefficientWeight to set
	 */
	public void setDiceCoefficientWeight(float diceCoefficientWeight) {
		DiceCoefficientWeight = diceCoefficientWeight;
	}

	/**
	 * @return the minimumEditDistanceWeight
	 */
	public float getMinimumEditDistanceWeight() {
		return minimumEditDistanceWeight;
	}

	/**
	 * @param minimumEditDistanceWeight the minimumEditDistanceWeight to set
	 */
	public void setMinimumEditDistanceWeight(float minimumEditDistanceWeight) {
		this.minimumEditDistanceWeight = minimumEditDistanceWeight;
	}

	/**
	 * @return the levenshteinDistanceWeight
	 */
	public float getLevenshteinDistanceWeight() {
		return LevenshteinDistanceWeight;
	}

	/**
	 * @param levenshteinDistanceWeight the levenshteinDistanceWeight to set
	 */
	public void setLevenshteinDistanceWeight(float levenshteinDistanceWeight) {
		LevenshteinDistanceWeight = levenshteinDistanceWeight;
	}
}
