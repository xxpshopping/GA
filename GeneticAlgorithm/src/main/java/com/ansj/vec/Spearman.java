package com.ansj.vec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ansj.vec.domain.Neuron;
import com.hankcs.hanlp.dependency.nnparser.util.math;


public class Spearman {
	ArrayList<ArrayList<String>> simword = new ArrayList<ArrayList<String>>();
	ArrayList<BigDecimal> trainwordsim = new ArrayList<BigDecimal>();
	ArrayList<Integer> loss = new ArrayList<Integer>();

	Map<String, Integer> words = new HashMap<>();
	ArrayList<ArrayList<Double>> C = new ArrayList<ArrayList<Double>>();
	Map<String, Integer> twords = new HashMap<>(); // �ʵ�
	ArrayList<ArrayList<Double>> tC = new ArrayList<ArrayList<Double>>();
	Map<String, Integer> pinyins = new HashMap<>(); // pinyin map
	private Map<String, String> transMap = new HashMap<>(); // translate word
	ArrayList<ArrayList<Double>> P = new ArrayList<ArrayList<Double>>();

	int dim = 100;

	Map<String, Neuron> total_word;
	String evaluatefile;

	String filesplit;
	Boolean isOnlypinyin = true;

	public Spearman(Map<String, Neuron> total_word, String evaluatefile) {
		
		this.total_word=total_word;
		this.evaluatefile = evaluatefile;
		
		
	}

	public double computeSpearman() {
		
		readSimword(evaluatefile);
		trainWordsSimiliar();

		BigDecimal bd;
		ArrayList<WordPair> trainword = new ArrayList<WordPair>();
		ArrayList<WordPair> fileword = new ArrayList<WordPair>();

		for (int i = 0; i < trainwordsim.size(); i++) {
			bd = trainwordsim.get(i);
			double t = bd.setScale(3, BigDecimal.ROUND_HALF_UP).doubleValue();
			
			trainword.add(new WordPair(i, t));
		}

		int jk = 0;
		for (int i = 0; i < simword.size(); i++) {
			if (!loss.contains(i)) {
				fileword.add(new WordPair(jk++, Double.valueOf(simword.get(i).get(2))));
			}
		}

		Collections.sort(trainword);
		Collections.sort(fileword);

		calcuRank(trainword);
		calcuRank(fileword);
		
		double d = 0;
		for (int i = 0; i < fileword.size(); i++) {
			for (int j = 0; j < trainword.size(); j++) {
				if (fileword.get(i).id == trainword.get(j).id) {
					d = d + (fileword.get(i).rank - trainword.get(j).rank)
							* (fileword.get(i).rank - trainword.get(j).rank);
					break;
				}
			}
		}

		double size = fileword.size();
		double spearman = 1 - (6 * d) / (size * (size * size - 1));
//		System.out.println("忽略的词" + loss.size());
//		System.out.println("参与训练的词" + trainword.size());
//		System.out.println("词相似度" + (spearman * 100));
		return spearman * 100;
	}

	public void calcuRank(ArrayList<WordPair> wordpairs) {
		// ����ȼ�
		double count = 1.0;
		double sum = 1.0;

		for (int i = 0; i < wordpairs.size(); i++) {
			if (i + 1 < wordpairs.size() && wordpairs.get(i).sim == wordpairs.get(i + 1).sim) {
				count = count + 1;
				sum = sum + i + 2;
			} else {
				double t = sum / count;
				while (count > 0) {
					wordpairs.get((int) (i - count + 1)).rank = t;
					count--;
				}
				count = 1.0;
				sum = i + 2;
			}
		}

	}
	public static BigDecimal sqrt(BigDecimal a) {
		BigDecimal _2 = BigDecimal.valueOf(2.0);
		int precision = 100;
		MathContext mc = new MathContext(precision, RoundingMode.HALF_UP);
		if(a.compareTo(BigDecimal.ZERO)==0) return BigDecimal.ZERO;
		else {
			BigDecimal x = a;
			int cnt = 0;
			while(cnt<100) {
				x = (x.add(a.divide(x,mc))).divide(_2,mc);
				cnt++;
			}
			return x;
		}
	}
	

	void trainWordsSimiliar() {
		
		
		int numa = 0, numb = 0; // �ʶ��ڴʵ��к������
		
		
		String worda, wordb;
		Double[] X1a = new Double[dim]; // the first word vec
		Double[] X1b = new Double[dim]; // the second word vec
		//Double[] X2a = new Double[dim]; // the first word vec
		//Double[] X2b = new Double[dim]; // the second word vec

		for (int pairsize = 0; pairsize < simword.size(); pairsize++) {//simword.size()是evaluatefile的行数 
			
			BigDecimal theta1a=new BigDecimal(0);
			BigDecimal theta1b=new BigDecimal(0);
			//BigDecimal theta2a=new BigDecimal(0);
			//BigDecimal theta2b=new BigDecimal(0);
			
			BigDecimal s1=new BigDecimal(0);
			//BigDecimal s2=new BigDecimal(0);
			
			worda = simword.get(pairsize).get(0);
			wordb = simword.get(pairsize).get(1);

			for (int i = 0; i < dim; i++) {
				X1a[i] = 0.0;
				X1b[i] = 0.0;
				//X2a[i] = 0.0;
				//X2b[i] = 0.0;
			}

			if (total_word.get(worda) == null || total_word.get(wordb) == null) {
				loss.add(pairsize);
				continue;
			} else {	
				
				for (int i = 0; i < dim; i++) {
					X1a[i] = X1a[i] + total_word.get(worda).syn[i];
					
					X1b[i] = X1b[i] + total_word.get(wordb).syn[i];
				}
			}

			for (int j = 0; j < dim; j++) {
				BigDecimal a=new BigDecimal(X1a[j]);
				BigDecimal b=new BigDecimal(X1b[j]);
				BigDecimal a1=a.multiply(a);
				BigDecimal b1=b.multiply(b);
				BigDecimal c=a.multiply(b);
				theta1a = theta1a.add(a1) ;
				s1 = s1.add(c);
				theta1b = theta1b.add(b1);
				
				
//				a=new BigDecimal(X2a[j]);
//				b=new BigDecimal(X2b[j]);
//				a1=a.multiply(a);
//				b1=a.multiply(b);
//				c=a.multiply(b);
//				theta2a = theta2a.add(a1) ;
//				s2 = s2.add(c);
//				theta2b = theta2b.add(b1);
//				theta2a = theta2a + X2a[j] * X2a[j];
//				s2 = s2 + X2a[j] * X2b[j];
//				theta2b = theta2b + X2b[j] * X2b[j];
				
			}
			BigDecimal d=sqrt(theta1b).multiply(sqrt(theta1a));
			if(d.intValue()!=0) s1=s1.divide(d,4,RoundingMode.HALF_DOWN);
			
			//d=sqrt(theta2b).multiply(sqrt(theta2a));
			//if(d.intValue()!=0) s2=s2.divide(d);
			
			trainwordsim.add(s1);
		}
	}

	

	public void readSimword(String file) {
		try {
			InputStreamReader in = new InputStreamReader(new FileInputStream(new File(file)), "utf-8");
			BufferedReader read = new BufferedReader(in);
			String line;
			String[] split;
			while ((line = read.readLine()) != null) {
				ArrayList<String> temparray = new ArrayList<String>();
				split = line.split("\t");
				for (int i = 0; i < split.length; i++) {//split.length=3
					temparray.add(split[i]);//temparray:worda,wordb,wordsim
				}
				simword.add(temparray);

			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
