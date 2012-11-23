package ist.ln.mp2;

import java.util.ArrayList;

/**
 *  The Class Jaccard.
 * @author Grupo 5
 * @author nº 56917 - Ana Santos 	<annara.snow@gmail.com>
 * @author nº 57384 - Júlio Machado <jules_informan@hotmail.com>
 * @author nº 57682 - Nuno Aniceto 	<nuno.aja@gmail.com>
 * @version 23.November.2012
 */
public class Jaccard {

	private String _str1 = "";
	private String _str2 = "";

	public Jaccard(String str1, String str2){
	
		_str1 = str1;
		_str2 = str2; 
	}
	
	public int union(){
		ArrayList<String> union = new ArrayList<String>();
		int str1_len = _str1.length();
		int str2_len = _str2.length();
		
		for(int i=0; i < str1_len; i++){
			
			String s = _str1.charAt(i)+"";
			
			if(!union.contains(s)){
				union.add(s);
			}
		}
		
		for(int i=0; i < str2_len; i++){
			
			String s = _str2.charAt(i)+"";
			
			if(!union.contains(s)){
				union.add(s);
			}
		}
		
		return union.size();
	}
	
	public int intersection(){
		ArrayList<String> intersection = new ArrayList<String>();
		int str1_len = _str1.length();
		
		for(int i=0; i < str1_len; i++){
			
			String s = _str1.charAt(i)+"";
			
			if(_str2.contains(s)){
				if(!intersection.contains(s)){
					intersection.add(s);
				}
			}
		}
		
		return intersection.size();
	}
	
	
	public double checkJaccard(){
		
		double i = intersection();
		double j = union();
		return i/j;
		
	}
}
