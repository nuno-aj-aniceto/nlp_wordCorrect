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
	private double DiceValue;	

	/** The Jaccard value. */
	private double JaccardValue;

	/** The Minimum editing distance value. */
	private double MinimumEditingDistanceValue;
	
	/** The Levenshtein distance value. */
	private double LevenshteinDistanceValue;
	
	/** The hybrid heuristic value. */
	private double hybridHeuristicValue;
	
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
	public double getDiceValue() {
		return this.DiceValue;
	}

	/**
	 * @return the JaccardValue
	 */
	public double getJaccardValue() {
		return this.JaccardValue;
	}

	/**
	 * @return the LevenshteinDistanceValue
	 */
	public double getLevenshteinDistanceValue() {
		return this.LevenshteinDistanceValue;
	}
	
	/**
	 * @return the minimumEditingDistanceValue
	 */
	public double getMinimumEditingDistanceValue() {
		return this.MinimumEditingDistanceValue;
	}
	
	/**
	 * @return the hybridHeuristicValue
	 */
	public double getHybridHeuristicValue() {
		return this.hybridHeuristicValue;
	}
	
	/**
	 * @param DiceValue the DiceValue to set
	 */
	public void setDiceValue(double DiceValue) {
		this.DiceValue = DiceValue;
	}

	/**
	 * @param JaccardValue the JaccardValue to set
	 */
	public void setJaccardValue(double JaccardValue) {
		this.JaccardValue = JaccardValue;
	}
	
	/**
	 * @param LevenshteinDistanceValue the LevenshteinDistanceValue to set
	 */
	public void setLevenshteinDistanceValue(double LevenshteinDistanceValue) {
		this.LevenshteinDistanceValue = LevenshteinDistanceValue;
	}

	/**
	 * @param MinimumEditingDistanceValue the MinimumEditingDistanceValue to set
	 */
	public void setMinimumEditingDistanceValue(double MinimumEditingDistanceValue) {
		this.MinimumEditingDistanceValue = MinimumEditingDistanceValue;
	}
	
	/**
	 * @param hybridHeuristicValue the hybridHeuristicValue to set
	 */
	public final void setHybridHeuristicValue(double hybridHeuristicValue) {
		this.hybridHeuristicValue = hybridHeuristicValue;
	}
	
}
