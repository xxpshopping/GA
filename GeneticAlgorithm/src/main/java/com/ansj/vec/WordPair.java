package com.ansj.vec;

import java.math.BigDecimal;

public class WordPair implements Comparable<WordPair>{
	int id;
	double rank;
	double sim;
	
	public WordPair(int id, double sim) {
		// TODO Auto-generated constructor stub
		this.id = id;
		this.sim = sim;
	}
	
	@Override
	public int compareTo(WordPair arg0) {
		// TODO Auto-generated method stub
		if(arg0.sim > this.sim) {
			return 1;
		}
		
		if(arg0.sim < this.sim) {
			return -1;
		}
		return 0;
	}

	@Override
	public String toString() {
		return "WordPair [id=" + id + ", rank=" + rank + ", sim=" + sim + "]";
	}
	
	
}
