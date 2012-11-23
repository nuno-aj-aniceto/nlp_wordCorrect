package ist.ln.mp2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 *  The Class Dice.
 * @author Grupo 5
 * @author nº 56917 - Ana Santos 	<annara.snow@gmail.com>
 * @author nº 57384 - Júlio Machado <jules_informan@hotmail.com>
 * @author nº 57682 - Nuno Aniceto 	<nuno.aja@gmail.com>
 * @version 23.November.2012
 */
public class Dice {

	private Set<String> _str1 = new HashSet<String>();
	private Set<String> _str2 = new HashSet<String>();

	public Dice(String str1, String str2){
	
		int str1_len = str1.length();
		int str2_len = str2.length();
		
		for(int i=0; i < str1_len; i++){
			
			String c = str1.charAt(i)+"";
			
			if(!_str1.contains(c)){
				_str1.add(c);
			}
		}
	
		for(int i=0; i < str2_len; i++){
			
			String c = str2.charAt(i)+"";
			
			if(!_str2.contains(c)){
				_str2.add(c);
			}
		}
		
	}
	
	public int intersection(){
		ArrayList<String> intersection = new ArrayList<String>();
		
		for(String c : _str1){
			if(_str2.contains(c)){
				intersection.add(c);
			}
		}
		
		return intersection.size();
	}
	
	
	public double checkDice(){
		
		double i = intersection();
		double x = _str1.size();
		double y = _str2.size();
		
		return (2*i)/(x+y);
		
	}
}
