package edu.miamioh.nguyenq2;

//Cited from the Course Book: Big Java Late Object
public class Queen {
	private int row;
	private int column;
	
	public Queen (int row, int column){
		this.row = row;
		this.column = column;
	}
	
	public boolean attacks (Queen other){
		return row == other.row || column == other.column
				|| Math.abs(row-other.row) == Math.abs(column-other.column);
	}
	
	public int getRow(){
		return this.row;
	}
	
	public int getColumn(){
		return this.column;
	}
	
	@Override
	public String toString(){
		return row+""+column;
	}
	
	public boolean equals(Queen a){
		if (a.getRow()==this.getRow() && a.getColumn() ==this.getColumn()) return true;
		return false;
	}
}
