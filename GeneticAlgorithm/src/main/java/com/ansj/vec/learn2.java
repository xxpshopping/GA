package com.ansj.vec;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.channels.NonWritableChannelException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import com.ansj.vec.domain.ListEntry;
import com.ansj.vec.domain.Neuron;


public class learn2 {
	
	int Population_size=200;//种群规模,每个词有200个个体
	int Chromosome_length= 100;//染色体长度
	double rate_crossover=0.5;//交叉率
	double rate_mutation=0.05;//变异率
	int iteration_num=20;//迭代次数
	String name11 = "";
	
	//存放文件所有词的
	private Map<String, Integer> words = new HashMap<String, Integer>();
	//存放的400维的向量
	private ArrayList<ArrayList<Double>> C = new ArrayList<ArrayList<Double>>();
	//存放文件所有词的
	private Map<String, Integer> twords = new HashMap<String, Integer>();
	//存放的400维的向量
	private ArrayList<ArrayList<Double>> tC = new ArrayList<ArrayList<Double>>();
	
	//List<Neuron> total_word = new ArrayList<>();
	//随机生成的下标200个100维
	private ArrayList<List<Integer>> random_alist = new ArrayList<List<Integer>>();
	private ArrayList<List<Integer>> new_random_alist = new ArrayList<List<Integer>>();
	private List<Integer> bestindex=new ArrayList<>();
	double bestfitness=0;
 	public learn2(){
		
	}
	/**
	 * 生成随机下标的方法
	 */
	public void create_random_alist(){
		
		for(int i = 0;i<Population_size;i++) {
			List<Integer> aList = new ArrayList<>();
			Random ran = new Random();
			while(aList.size()<100)
				{
					int n = ran.nextInt(199);
					if(!aList.contains(n)) {
						aList.add(n);
					}
				}
			aList.sort(null);	
			//System.out.println(aList);
			random_alist.add(aList);
			//aList.clear();
		}
		//System.out.println(random_alist);
	}
	
	/**
	 * 初始化得到个体的基因，也就是得到每个词的向量
	 * @param file
	 */
	public void population_initialize(File file) {
		try {
			InputStreamReader input = new InputStreamReader(new FileInputStream(file), "utf-8");
			BufferedReader read = new BufferedReader(input);
			
			String line;
			String[] factors;
			int num = 0;
			while ((line = read.readLine()) != null) {
				factors = line.split(" ");
				twords.put(factors[0], num);

				num++;
				ArrayList<Double> vec = new ArrayList<Double>();
				for (int i = 1; i <factors.length; i++) {
					vec.add(Double.valueOf(factors[i]));
				}
				tC.add(vec);

				if(num%10000==0) {						
					System.out.println("第"+num);
				}
			}
					
			
									
	    }
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		try {
//			InputStreamReader input = new InputStreamReader(new FileInputStream("ES-WS-353.txt"), "utf-8");
//			BufferedReader read = new BufferedReader(input);
//			
//			String line;			
//			String[] factors;			
//			int num = 0;
//			int i=0;
//			while ((line = read.readLine()) != null) {
//				factors = line.split("\t");
//				if(twords.containsKey(factors[0])&&!words.containsKey(factors[0])) {
//					num=twords.get(factors[0]);
//					words.put(factors[0],i);
//					C.add(tC.get(num));
//					i++;
//					System.out.println(factors[0]);
//				}
//				if(twords.containsKey(factors[1])&&!words.containsKey(factors[1])) {
//					num=twords.get(factors[1]);
//					words.put(factors[1],i);
//					C.add(tC.get(num));
//					i++;
//					System.out.println(factors[1]);
//				}
//				else {
//					continue;
//				}
//			}
//						
//									
//	    }
//		catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	/**
	 * 计算词相似度，适应度
	 * @param random_alist
	 */
	public void ComputerWordsim(Map<Integer,ListEntry> wordsim) {
		
		
		for(int i = 0;i<random_alist.size();i++) {
			Map<String, Neuron> total_word = new HashMap<String, Neuron>();
			List<Integer> index_per = random_alist.get(i);
			
			for(String name:twords.keySet()) {
				Neuron temp_word = new Neuron();
				temp_word.value = name;
				ArrayList<Double> vector_per = tC.get(twords.get(name));
				//System.out.println(index_per.size());
				for(int j=0;j<index_per.size();j++) {
					temp_word.syn[j] = vector_per.get(index_per.get(j));
				}
				total_word.put(temp_word.value, temp_word);
				//System.out.println(total_word.get(name)+":"+temp_word.syn[99]);
			}
			//计算相似度
			//System.out.println(Arrays.toString(total_word.get("the").syn));
			Spearman spearman1=new Spearman(total_word, "ES-RG-65.txt");
			Spearman spearman2=new Spearman(total_word, "ES-MC-30.txt");
			Spearman spearman3=new Spearman(total_word, "ES-WS-353.txt");
			double a1=spearman1.computeSpearman();
			double a2=spearman2.computeSpearman();
			double a3=spearman3.computeSpearman();
			
			ListEntry a=new ListEntry();
			a.fitness=(a1+a2+a3)/3;
			wordsim.put(i, a);
			if(bestfitness<wordsim.get(i).fitness) {
				bestindex=index_per;
				bestfitness=wordsim.get(i).fitness;
			}
			//total_word=null;
			System.out.println("bestfitness:  "+bestfitness);
		}
	}
	
	/**
	 * 更新种群内个体的属性值
	 * @param population_current
	 */
	public void fresh_property(Map<Integer,ListEntry> wordsim) {
		double sum=0;
		for(int i=0;i<Population_size;i++) {
			sum=sum+wordsim.get(i).fitness;
		}
		wordsim.get(0).rate_fit=wordsim.get(0).fitness/sum;
		wordsim.get(0).cumu_fit=wordsim.get(0).rate_fit;
		for(int i=1;i<random_alist.size();i++) {
			wordsim.get(i).rate_fit=wordsim.get(i).fitness/sum;
			wordsim.get(i).cumu_fit=wordsim.get(i).rate_fit+wordsim.get(i-1).cumu_fit;
		}
	}
	/**
	 * 选择方法：轮盘赌选择法
	 */
	public void seletc_prw(Map<Integer,ListEntry> wordsim) {
		Map<Integer, Integer> select=new HashMap<>();
		Random ran = new Random();
//		bestindex=random_alist.get(0);
//		double bestfitness=wordsim.get(0).fitness;
		for(int i=0;i<Population_size;i++) {
			float n = ran.nextFloat();
			if(n<wordsim.get(i).cumu_fit) {
				new_random_alist.set(i, random_alist.get(i));
			}
				
			else {
				for(int j=0;j<random_alist.size();j++) {
					if(wordsim.get(j).cumu_fit<=n&&n<=wordsim.get(j+1).cumu_fit) {
						new_random_alist.set(i, random_alist.get(j));
						break;
					}
				}
			}

		}
		
		
	}
	/**
	 * 选择方法：竞标赛选择法
	 * @param wordsim
	 */
	public void seletc_ts(Map<Integer,ListEntry> wordsim) {
		
		Random ran = new Random();
//		bestindex=random_alist.get(0);
//		double bestfitness=wordsim.get(0).fitness;
		for(int i=0;i<Population_size-1;i++) {
			int a1=ran.nextInt(Population_size);
			int a2=ran.nextInt(Population_size);
			int a3=ran.nextInt(Population_size);
			if(wordsim.get(a1).fitness>wordsim.get(a2).fitness&&wordsim.get(a1).fitness>wordsim.get(a3).fitness) {
				new_random_alist.set(i, random_alist.get(a1));
			}
			else if(wordsim.get(a2).fitness>wordsim.get(a1).fitness&&wordsim.get(a2).fitness>wordsim.get(a3).fitness) {
				new_random_alist.set(i, random_alist.get(a2));
			}
			else {
				new_random_alist.set(i, random_alist.get(a3));
			}
			
		}
		new_random_alist.set(Population_size-1, bestindex);
	}
	/**
	 * 交叉操作
	 * @param population_next_generation
	 */
	public void crossover() {
		Random ran = new Random();
		for(int i=0;i<Population_size;i++) {
			float rate_rand=ran.nextFloat();
			if(rate_rand<=rate_crossover) {
				int num1=ran.nextInt(200);
				int num2=ran.nextInt(200);
				int n=ran.nextInt(100);
				for(int j=n;j<100;j++) {
					Integer temp,temp1,temp2;
					temp1=new_random_alist.get(num1).get(j);
					temp2=new_random_alist.get(num2).get(j);
					if(!new_random_alist.get(num1).contains(temp2)&&!new_random_alist.get(num2).contains(temp1)) {
						temp=temp1;
						new_random_alist.get(num1).set(j,temp2);
					    new_random_alist.get(num2).set(j,temp);
					}
					
				}
				new_random_alist.get(i).sort(null);
			}
		}
		
		
	}
	/**
	 * 突变操作
	 * @param population_next_generation
	 */
	public void mutation() {
		Random ran = new Random();
		for(int i=0;i<Population_size;i++) {
			float rate_rand=ran.nextFloat();
			if(rate_rand<=rate_mutation) {
				int position = ran.nextInt(100);
				int n=ran.nextInt(199);
				if(!new_random_alist.get(i).contains(n)) {
					new_random_alist.get(i).set(position,n);
					new_random_alist.get(i).sort(null);
				}
				
			}
		}
	}
	
	/**
	 * 训练过程
	 * @throws IOException
	 */
	private void trainModel(File file) throws IOException {
		create_random_alist();
		new_random_alist=random_alist;
		bestindex=random_alist.get(0);
		int i=0,j=0;
		long starttime1 = System.currentTimeMillis();//起始时间
		

		population_initialize(file);
		
		
		//开始迭代
		for(i=0;i<iteration_num;i++) {
			Map<Integer,ListEntry> wordsim = new HashMap<Integer,ListEntry>();//放每组随机生成的下标对应的WordSim
			//输出当前迭代次数
			System.out.println("epoch:"+i+" ");
			//计算相似度
			ComputerWordsim(wordsim);
			//更新种群内个体的属性值
	        fresh_property(wordsim);
			//挑选优秀个体组成新的种群
			seletc_ts(wordsim);
			//对选择后的种群进行交叉操作
	        crossover();
	        //对交叉后的种群进行变异操作
	        mutation();
	        System.out.println(bestindex+"\t"+bestfitness);
	        random_alist=new_random_alist;
	        wordsim.clear();
		}
		long starttime4 = System.currentTimeMillis();
        System.out.println("耗时："+(starttime4-starttime1)/1000+"秒");
        System.out.println("最好的下标："+bestindex);
	}
	/**
	 * 保存词向量
	 * @param file
	 */
	
	public void saveModel(File file) {
		// TODO Auto-generated method stub

		try {
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file), "utf-8");
			BufferedWriter write = new BufferedWriter(out);
			
			double[] syn0=new double[100];
			
			for (String element : twords.keySet()) {
				write.write(element + " ");
				write.flush();

				// syn0 = ((WordNeuron) element.getValue()).syn0;
				
				for (int i = 0; i < 100; i++) {
					syn0[i] = tC.get(twords.get(element)).get(bestindex.get(i));
				}
				

				for (int j = 0; j < 100; j++) {
					write.write(syn0[j] + " ");
					write.flush();
				}
				write.write("\r\n");
				write.flush();
			}

			words.clear();			
			write.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		learn2 a=new learn2();
		File filein =new File("E:\\文本表示语料\\Spanish\\2fasttext.txt");
		File fileout =new File("E:\\文本表示语料\\Spanish\\result.txt");
		a.trainModel(filein);
		a.saveModel(fileout);
		
	}

}
