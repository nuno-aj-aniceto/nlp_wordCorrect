/** 22/Nov/2012 - miniprojecto2 - ist.ln.mp2.Lexer **/
package ist.ln.mp2;

// note to self:
//   >> premature optimization is the root of all evil !
//      (therefore women may not be!)

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

	/** The maximum result's words. */
	private int maximumResultWords;

	/** The minimum editing distance threshold. */
	private int minimumEditingDistanceThreshold;

	/** The Levenshtein's distance threshold. */
	private int LevenshteinDistanceThreshold;

	/** The Jaccard's coefficient threshold. */
	private double JaccardCoefficientThreshold;

	/** The Dice's coefficient threshold. */
	private double DiceCoefficientThreshold;

	/** The hybrid heuristic threshold. */
	private double hybridHeuristicThreshold;

	// should we use the F-Measure ?

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

		this.maximumResultWords = 5;

		/** filtering Thresholds **/
		this.minimumEditingDistanceThreshold = 5; // todo: test
		this.LevenshteinDistanceThreshold = 6; // todo: test
		this.DiceCoefficientThreshold = 0.5; // todo: test
		//this.JaccardCoefficientThreshold = 0.24; // todo: test
		this.JaccardCoefficientThreshold = 0.5; // todo: test
		this.hybridHeuristicThreshold = 1.0; // todo: test

		/** eigenvalues for balancing data **/
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
		
		
		//String normalizedWord = NormalizerSimple.normPunctLCaseDMarks(word);
		String normalizedWord = NormalizerSimple.normPunctLCase(word);
		ArrayList<String> result = new ArrayList<String>(
				this.maximumResultWords);
		ArrayList<OutputNode> evaluationTable = new ArrayList<OutputNode>();

		if (getKnownWords().contains(word)) {
			result.add(word);
		}

		for (KnownWordNode knownNode : knownWordNodes) {
			String knownNormalizedWord = knownNode.getNormalizedString();

			// compare (normalized) strings using Jaccard's Index
			//double JaccardValue = (new Jaccard(normalizedWord, knownNormalizedWord)).checkJaccard();
			double JaccardValue = computeJaccard(normalizedWord, knownNormalizedWord);
			//System.out.println("Jaccard [ " + normalizedWord + " + " + knownNormalizedWord + " ] == " + JaccardValue);
			if (JaccardValue < this.JaccardCoefficientThreshold)
				continue;

			// compare (normalized) strings using Dice's Coefficient
			//double DiceValue = (new Dice(normalizedWord, knownNormalizedWord)).checkDice();
			double DiceValue = computeDice(normalizedWord, knownNormalizedWord);
			if (DiceValue < this.DiceCoefficientThreshold)
				continue;

			// compare (normalized) strings using Minimum Editing Distance
			// [Wagner-Fischer algorithm]
			int MinimumEditingDistanceValue = computeMinimumEditingDistance(
					normalizedWord, knownNormalizedWord);
			if (MinimumEditingDistanceValue > this.minimumEditingDistanceThreshold)
				continue;

			// compare (normalized) strings using Levenshtein's Distance
			int LevenshteinDistanceValue = computeLevenshteinDistance(
					normalizedWord, knownNormalizedWord);
			if (LevenshteinDistanceValue > this.LevenshteinDistanceThreshold)
				continue;

			// compare (normalized) strings using a Team-Made Hybrid Heuristic
			// this is a combination of each (heuristic) value calculated before
			// Therefore this heuristic possesses an acummulated entropy of each
			// value, normalizing and combining their strengths (and weaknesses)

			double invertDiceValue, invertJaccardValue, hybridHeuristicValue;
			double hybridHeuristicValue2; // todo: test

			// invert values: can do (1/x) or (1-x)
			// 1/x give more accurate data (as it is not linear)

			// invertDiceValue = 1-DiceValue;
			// invertJaccardValue = 1-JaccardValue;

			if (DiceValue == 0)
				invertDiceValue = 0;
			else
				invertDiceValue = 1 / DiceValue;

			if (JaccardValue == 0)
				invertJaccardValue = 0;
			else
				invertJaccardValue = 1 / JaccardValue;

			// calculate Hybrid Heuristic: can use '+' or '*' for the
			// Jaccard/Dice values
			hybridHeuristicValue = (invertDiceValue * invertJaccardValue)
					* (MinimumEditingDistanceValue + LevenshteinDistanceValue);

			hybridHeuristicValue2 = (invertDiceValue + invertJaccardValue)
					* (MinimumEditingDistanceValue + LevenshteinDistanceValue);

			if (hybridHeuristicValue < this.hybridHeuristicThreshold) {
				continue;
			}

			OutputNode outputNode = new OutputNode(knownNode);
			outputNode.setDiceValue(DiceValue);
			outputNode.setJaccardValue(JaccardValue);
			outputNode.setLevenshteinDistanceValue(LevenshteinDistanceValue);
			outputNode
					.setMinimumEditingDistanceValue(MinimumEditingDistanceValue);
			outputNode.setHybridHeuristicValue(hybridHeuristicValue);

			evaluationTable.add(outputNode);
		}

		// sort the result's for their values, we want the best of them !
		final Comparator<OutputNode> OUTPUTNODE_COMPARATOR = new Comparator<OutputNode>() {
			@Override
			public int compare(OutputNode a, OutputNode b) {
				return ((int) (a.getHybridHeuristicValue() - b
						.getHybridHeuristicValue()));
			}
		};
		Collections.sort(evaluationTable, OUTPUTNODE_COMPARATOR);

		for (OutputNode outputNode : evaluationTable) {
			result.add(outputNode.getInputNode().getOriginalString());

			if (result.size() >= this.maximumResultWords)
				break;
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

	public static HashSet<Character> union(HashSet<Character> x, HashSet<Character> y) {
		HashSet<Character> t = new HashSet<Character>(x);
		t.addAll(y);
		return t;
	}

	public static HashSet<Character> intersection(HashSet<Character> x, HashSet<Character> y) {
		HashSet<Character> t = new HashSet<Character>(x);
		t.retainAll(y);
		return t;
	}

	public static HashSet<Character> stringToSet(String string) {
		HashSet<Character> set = new HashSet<Character>();
		
		for (int i = 0; i < string.length(); i++) {
			set.add(string.charAt(i));
		}
		return set;
	}

	public static float computeJaccard(String sourceString, String targetString) {
		float result;
		
		HashSet<Character> source = stringToSet(sourceString);
		HashSet<Character> target = stringToSet(targetString);
		
		HashSet<Character> intersection = intersection(source, target);
		HashSet<Character> union = union(source, target);
		
		result = ((float)intersection.size())/((float)union.size());
		
		return result;
	}

	public static float computeDice(String sourceString, String targetString) {
		float result;
		
		HashSet<Character> source = stringToSet(sourceString);
		HashSet<Character> target = stringToSet(targetString);
		
		HashSet<Character> intersection = intersection(source, target);
		
		result = ((float)2*intersection.size())/((float)(source.size()+target.size()));
		
		return result;
	}

	/**
	 * Compute minimum editing distance.
	 *
	 * @param sourceString the source string
	 * @param targetString the target string
	 * @return the int
	 */
	public static int computeMinimumEditingDistance(String sourceString,
			String targetString) {
		return computeMinimumEditingDistance(sourceString, targetString, 1, 1,
				1);
	}

	/**
	 * Compute Levenshtein's distance.
	 *
	 * @param sourceString the source string
	 * @param targetString the target string
	 * @return the int
	 */
	public static int computeLevenshteinDistance(String sourceString,
			String targetString) {
		return computeMinimumEditingDistance(sourceString, targetString, 1, 1,
				2);
	}

	/**
	 * Compute minimum editing distance.
	 *
	 * @param sourceString the source string
	 * @param targetString the target string
	 * @param c1 the c1
	 * @param c2 the c2
	 * @param c3 the c3
	 * @return the int
	 */
	public static int computeMinimumEditingDistance(String sourceString,
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

		// initial setup
		for (int a = 0; a < n + 1; a++)
			// the first line of the matrix
			medMatrix[0][a] = a;

		for (int b = 0; b < m + 1; b++)
			// the first column of the matrix
			medMatrix[b][0] = b;

		for (int i = 1; i < m + 1; i++) { // the rest of the matrix (0 value)
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
}
