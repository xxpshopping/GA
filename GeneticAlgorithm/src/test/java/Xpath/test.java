package Xpath;
import java.io.IOException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class test {
	public static void main(String[] args) throws Exception {
		String url = "http://www.godic.net/dicts/de/"+"darüber";
		
		String exp = "//*[@id=\"exp-head\"]/div/span[1]/span[2]";
 
		String html = null;
		try {
			Connection connect = Jsoup.connect(url);
			html = connect.get().body().html();
		} catch (IOException e) {
			System.out.println("hhh");
		}
		HtmlCleaner hc = new HtmlCleaner();
		TagNode tn = hc.clean(html);
		Document dom = new DomSerializer(new CleanerProperties()).createDOM(tn);
		XPath xPath = XPathFactory.newInstance().newXPath();
		Object result;
		result = xPath.evaluate(exp, dom, XPathConstants.NODESET);
		if (result instanceof NodeList) {
			NodeList nodeList = (NodeList) result;
			System.out.println(nodeList.getLength());
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				/**
				 * Node.getTextContent() 此属性返回此节点及其后代的文本内容。
				 * Node.getFirstChild()  此节点的第一个子节点。
				 * Node.getAttributes() 包含此节点的属性的 NamedNodeMap（如果它是 Element）；否则为 null
				 * 如果想获取相应对象的相关属性，可以调用  getAttributes().getNamedItem("属性名") 方法
				 */
				System.out.println(
						//node.getNodeValue() == null ? node.getFirstChild().getAttributes().getNamedItem("href") : node.getNodeValue());
						node.getTextContent());
			}
 
		}
	}

}
