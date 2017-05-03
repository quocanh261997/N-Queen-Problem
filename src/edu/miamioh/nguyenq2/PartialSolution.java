 package edu.miamioh.nguyenq2;

import java.util.Arrays;

public class PartialSolution {
	private Queen[] queens;
	public static final int N_QUEEN = 8;
	public static final int ACCEPT = 1;
	public static final int ABANDON = 2;
	public static final int CONTINUE = 3;
	
	public PartialSolution(int size){
		queens = new Queen[size];
	}
	
	public int examine(){
		for (int i = 0; i<queens.length;i++){
			for (int j = i+1; j<queens.length;j++){
				if (queens[i].attacks(queens[j])) return ABANDON;
			}
		}
		if (queens.length == N_QUEEN) return ACCEPT;
		else return CONTINUE;
	}
	
	public PartialSolution[] extend(){
		
		//Generate a new solution for each column
		PartialSolution[] result = new PartialSolution[8];
		for (int i = 0; i<result.length;i++){
			int size = queens.length;
			
			//The new solution has one more row than this one
			result[i] = new PartialSolution(size+1);
			
			//Copy this solution into the new one
			for (int j = 0; j<size;j++){
				result[i].queens[j] = queens[j];
			}
			
			//Append the new queen into the ith column
			result[i].queens[size] = new Queen(size,i);
		}
		return result;
	}
	
	public Queen[] getQueen(){
		return this.queens;
	}
	
	public String toString(){
		return Arrays.toString(queens);
	}
	
	 
}
