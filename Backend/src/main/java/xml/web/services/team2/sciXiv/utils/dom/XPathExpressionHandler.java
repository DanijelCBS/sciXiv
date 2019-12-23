package xml.web.services.team2.sciXiv.utils.dom;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Component
public class XPathExpressionHandler {

	private static XPathFactory xPathFactory;
	
	/*
	 * Factory initialization static-block
	 */
	static {
		xPathFactory = XPathFactory.newInstance();		
	}
	

	
	
	public NodeList findNodesByXPath(Document document, String expression) throws XPathExpressionException {
		
		XPath xPath = xPathFactory.newXPath();
		XPathExpression xPathExpression;
		xPathExpression = xPath.compile(expression);
		NodeList result = (NodeList) xPathExpression.evaluate(document, XPathConstants.NODESET);
		return result;
	}
	
	public Node findSingleNodeByXPath(Document document, String expression) throws XPathExpressionException {
		
		XPath xPath = xPathFactory.newXPath();
		XPathExpression xPathExpression;
		xPathExpression = xPath.compile(expression);
		Node result = (Node) xPathExpression.evaluate(document, XPathConstants.NODE);
		return result;
	}
	
}
