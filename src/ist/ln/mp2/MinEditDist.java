package ist.ln.mp2;

//import java.lang.*;
/**
 *  The Class MinEditDist.
 * @author Grupo 5
 * @author nº 56917 - Ana Santos 	<annara.snow@gmail.com>
 * @author nº 57384 - Júlio Machado <jules_informan@hotmail.com>
 * @author nº 57682 - Nuno Aniceto 	<nuno.aja@gmail.com>
 * @version 23.November.2012
 */
public class MinEditDist {
	
	private String s; /*source string*/
	private String t; /*target string*/
	private int medDist; /*Minimum Editing Distance*/
	
	/*assuming weights of one for all types of operations(will be possible to assign different weights depending on how we want it to work)*/
	private int c1 =1; /*deletion*/
	private int c2 =1; /*insertion*/
	private int c3 =1; /*substitution*/
	
	/*constructor -  takes the two words to calculate MED*/
	/*may be replaced by a default constructor later or may receive a different set of variables c1,c2,c3 for algorithm variation*/
	public MinEditDist(String word1, String word2) {
		this.s = word1;
		this.t = word2;
	}
	
	public MinEditDist(int const1, int const2, int const3){
			this.c1 = const1;
			this.c2 = const2;
			this.c3 = const3;
	}
	
	/*value getters*/
	public String getSource() {return s;}
	public String getTarget() {return t;}
	public int getMed(){return medDist;}
	
	/*function that calculates MED and returns a string with the result*/
	/*may be modified to receive the two string to compare*/
	public int computeMED(){
	
		
	int n = s.length(); /*number of columns-1*/
	int m = t.length(); /*number of rows-1*/
	
	/*if one word is not provided, return the other as MED*/
	if(n == 0){
		return m;
	} else if (m == 0) {
			return n;
		}
		
	int medMatrix [][]  = new int [m+1][n+1]; /*MED Matrix for data enclosure*/
	for(int a=0; a < n+1; a++) { //first matrix line init
		medMatrix[0][a] = a;
	}
	for(int b=0; b < m+1; b++){ //first matrix column init
		medMatrix[b][0] = b;
	}
	
	for(int i=1; i<m+1; i++){ //initializing  the rest of the matrix cells with zero values
		for(int j =1; j<n+1; j++){
			medMatrix[i][j] = 0;
		}
	}
	
	 
	
	
	/*computing MED(Levenshtein Distance) using Wagner-Fischer Algorithm */
	for(int j=1; j <= n; j++){ 
		for(int i=1; i <= m; i++){
			if(s.charAt(j-1) == t.charAt(i-1)) {
				medMatrix [i][j] = medMatrix[i-1][j-1];
			}else {
				medMatrix [i][j] = myMin(medMatrix[i-1][j]+c1, /*deletion*/
										medMatrix[i][j-1]+c2,  /*insertion*/
										medMatrix[i-1][j-1]+c3); /*substitution*/
			}
		}
	}
	
	medDist = medMatrix[m][n]; /*contains the value of MED*/
	System.out.println("distancia minima de edicao entre "+getSource()+" e "+getTarget()+" = "+getMed());
	return 0;
	}
	
	/*no min(a,b,c) exists in java, so i made one xD*/
	public int myMin(int a, int b, int c){
		if(Math.min(a,b) < Math.min(a,c)){
			return Math.min(a,b);
		}else if(Math.min(a,b) > Math.min(a,c)){
			return Math.min(a,c);
		}else if(Math.min(a,b) < Math.min(b,c)){
			return Math.min(a,b);
		}else return Math.min(b,c);
			
	}
	
	
		
	
	/*for testing and illustration purposes, there is a main here, will be removed in later versions*/
	public static void main(String[] args) {
	
	MinEditDist med = new MinEditDist("chato", "chatear"); 
	med.computeMED();
	}
}