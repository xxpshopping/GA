package com.ansj.vec.domain;

import java.util.Arrays;

public class Neuron{
  public String value;//词
  public Double[] syn = new Double[100];
  
  
  public Neuron() {
	  for(int i =0;i<100;i++) {
		   syn[i] = (double) 0;
	  }
	  
	  value = "";
}

@Override
public String toString() {
	return "Neuron [value=" + value + ", syn=" + Arrays.toString(syn) + "]";
}
  
}
