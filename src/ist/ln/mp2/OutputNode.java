/** 22/Nov/2012 - miniprojecto2 - ist.ln.mp2.OutputNode **/
package ist.ln.mp2;

// TODO: Auto-generated Javadoc
/**
 * The Class OutputNode.
 */
public class OutputNode {

	/** The input node. */
	private KnownWordNode inputNode;
	
	/** The Dice value. */
	private float DiceValue;	

	/** The Jaccard value. */
	private float JaccardValue;

	/** The Minimum edit distance value. */
	private float MinimumEditDistanceValue;
	
	/** The Levenshtein's distance value. */
	private float LevenshteinDistanceValue;
	
	/** The heuristic value. */
	private float heuristic;
	
	/**
	 * Instantiates a new output node.
	 *
	 * @param inputNode the input node
	 */
	public OutputNode(KnownWordNode inputNode) {
		this.inputNode = inputNode;
	}


	/**
	 * Gets the input node.
	 *
	 * @return the input node
	 */
	public KnownWordNode getInputNode() {
		return this.inputNode;
	}
	
	/**
	 * @return the DiceValue
	 */
	public float getDiceValue() {
		return this.DiceValue;
	}

	/**
	 * @return the JaccardValue
	 */
	public float getJaccardValue() {
		return this.JaccardValue;
	}

	/**
	 * @return the LevenshteinDistanceValue
	 */
	public float getLevenshteinDistanceValue() {
		return this.LevenshteinDistanceValue;
	}
	
	/**
	 * @return the minimumEditDistanceValue
	 */
	public float getMinimumEditDistanceValue() {
		return this.MinimumEditDistanceValue;
	}
	
	/**
	 * @return the heuristic
	 */
	public float getHeuristicValue() {
		return this.heuristic;
	}
	
	/**
	 * @param DiceValue the DiceValue to set
	 */
	public void setDiceValue(float DiceValue) {
		this.DiceValue = DiceValue;
	}

	/**
	 * @param JaccardValue the JaccardValue to set
	 */
	public void setJaccardValue(float JaccardValue) {
		this.JaccardValue = JaccardValue;
	}
	
	/**
	 * @param LevenshteinDistanceValue the LevenshteinDistanceValue to set
	 */
	public void setLevenshteinDistanceValue(float LevenshteinDistanceValue) {
		this.LevenshteinDistanceValue = LevenshteinDistanceValue;
	}

	/**
	 * @param MinimumEditDistanceValue the MinimumEditDistanceValue to set
	 */
	public void setMinimumEditDistanceValue(float MinimumEditDistanceValue) {
		this.MinimumEditDistanceValue = MinimumEditDistanceValue;
	}
	
	/**
	 * @param heuristicValue the heuristic to set
	 */
	public final void setHeuristicValue(float heuristicValue) {
		this.heuristic = heuristicValue;
	}
	
}
