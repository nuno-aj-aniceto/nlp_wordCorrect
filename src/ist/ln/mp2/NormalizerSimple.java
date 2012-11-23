//package l2f.nlp;
package ist.ln.mp2;

import java.text.Normalizer;
import java.text.Normalizer.Form;

/**
 *  The Class NormalizerSimple.
 * @author Grupo 5
 * @author nº 56917 - Ana Santos 	<annara.snow@gmail.com>
 * @author nº 57384 - Júlio Machado <jules_informan@hotmail.com>
 * @author nº 57682 - Nuno Aniceto 	<nuno.aja@gmail.com>
 * @version 23.November.2012
 */
public class NormalizerSimple {

	/**
	 * contains:
	 * 	-remove all '!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~'  = \p{Punct}+
	 *  -lowercase
	 *  -trim
	 *  -remove ¿ characters (Punct doesn't capture it)
	 *  
	 * @param words single word or sentence
	 * @return single word or sentence normalized
	 */
	public static String normPunctLCase(String words){
		return words.replaceAll("\\p{Punct}+", "").replaceAll("¿", "").toLowerCase().trim();
	}
	
	/**
	 * contains:
	 * 	-remove all !"#$%&'()*+,-./:;<=>?@[\]^_`{|}~  = \p{Punct}+
	 *  -lowercase
	 *  -trim
	 *  -remove all diacritical marks (´`~^, etc)
	 * 
	 * @param words the input String to be normalized
	 * @return a normalized String
	 */
	public static String normPunctLCaseDMarks(String words){
		return Normalizer.normalize(NormalizerSimple.normPunctLCase(words), Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
	
	/**
	 * remove all diacritical marks (´`~^, etc)
	 * @param words the input String to be normalized
	 * @return a normalized String
	 */
	public static String normDMarks(String words){
		return Normalizer.normalize(words, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}
}
