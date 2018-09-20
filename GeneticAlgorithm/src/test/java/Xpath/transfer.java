package Xpath;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class transfer {
	
	private Map<String, String> words= new HashMap<String, String>();
	
	public transfer() {
		
	}
	
	public String getpronunce(String url,String exp) throws ParserConfigurationException, XPathExpressionException, IOException {
		String pronunce = null;
		String html = null;
		
		Connection connect = Jsoup.connect(url);
		html = connect.get().body().html();
		
		HtmlCleaner hc = new HtmlCleaner();
		TagNode tn = hc.clean(html);
		Document dom = new DomSerializer(new CleanerProperties()).createDOM(tn);
		XPath xPath = XPathFactory.newInstance().newXPath();
		Object result;
		result = xPath.evaluate(exp, dom, XPathConstants.NODESET);
		if (result instanceof NodeList) {
			NodeList nodeList = (NodeList) result;
			//System.out.println(nodeList.getLength());
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				/**
				 * Node.getTextContent() 此属性返回此节点及其后代的文本内容。
				 * Node.getFirstChild()  此节点的第一个子节点。
				 * Node.getAttributes() 包含此节点的属性的 NamedNodeMap（如果它是 Element）；否则为 null
				 * 如果想获取相应对象的相关属性，可以调用  getAttributes().getNamedItem("属性名") 方法
				 */
				pronunce=node.getTextContent();
				System.out.println(
						//node.getNodeValue() == null ? node.getFirstChild().getAttributes().getNamedItem("href") : node.getNodeValue());
						node.getTextContent());
				
			}
 
		}
		return pronunce;
	}
	public void pronuncetext(File filein,File fileout) throws XPathExpressionException, ParserConfigurationException {
		try {
			InputStreamReader input = new InputStreamReader(new FileInputStream(filein), "utf-8");
			BufferedReader read = new BufferedReader(input);
			OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(fileout), "utf-8");
			BufferedWriter write = new BufferedWriter(out);
			String line;
			String[] factors;
			String pronuce1;
			String pronuce2;
			int num = 0;
			while ((line = read.readLine()) != null) {
				
				factors = line.split(" ");
				for (int i = 0; i <factors.length; i++) {
					if(words.containsKey(factors[i]))
						continue;					
					else {
						String a = factors[i];
						if(a == null || a.length() == 0) continue;
						try{
							connect(a);
							num++;
							}
						catch (Exception e) {
							System.out.println("错误跳过hhhhhhhhhhhhhhhhhhhhhhhhhhhhh");
							if(i+1<factors.length) {
								i++;
								String a1 = factors[i];
								if(a1 == null || a1.length() == 0) continue;
								try{
									connect(a1);
									num++;
								}
								catch (Exception e1) {
									continue;
								}
								}
							else {
								break;
							}
						}
					}
					
				}
				//write.write("\r\n");
				//write.flush();

				if(num%10000==0) {						
					System.out.println("第"+num);
				}
			}
					
			for(String element : words.keySet()) {
				write.write(element);
				write.write("\t");
				if(words.get(element)==null||words.get(element).length()==0) {
					write.write("unknow");
				}
				else {
					write.write(words.get(element));
				}
					write.write("\r\n");
					write.flush();
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
	}
	
	public void connect(String a) throws XPathExpressionException, ParserConfigurationException, IOException {
		String pronuce1 = null;
		
		String url = "http://www.youdao.com/w/fr/"+a+"/#keyfrom=dict2.top";		
		String exp1 = "//*[@id=\"phrsListTab\"]/h2/span[2]";
		Connection connect = Jsoup.connect(url);
		
		
		pronuce1=getpronunce(url, exp1);
		
		
		words.put(a, pronuce1);
		
	}
	
	public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException {
		File filein =new File("E:\\文本表示语料\\franch\\frwiki");
		File fileout =new File("E:\\文本表示语料\\franch\\transfer.txt");
		transfer t =new transfer();
		t.pronuncetext(filein, fileout);
		System.out.println("complete");
 
	}

}
