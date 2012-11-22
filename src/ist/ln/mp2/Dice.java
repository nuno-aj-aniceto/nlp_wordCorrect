package ist.ln.mp2;

import java.util.ArrayList;

public class Dice {

	private String _str1 = "";
	private String _str2 = "";

	public Dice(String str1, String str2){
	
		_str1 = str1;
		_str2 = str2; 
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
		
		//System.out.println("intersection: " + intersection);
		
		return intersection.size();
	}
	
	
	public double checkDice(){
		
		double i = intersection();
		double x = _str1.length();
		double y = _str2.length();
		
		return (2*i)/(x+y);
		
	}
	
	/**public static void main(String[] args){
		
		Jaccard j = new Jaccard("saturday", "sunday");
		
		System.out.println(j.checkJaccard());
	}*/
}
