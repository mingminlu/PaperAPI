package org.paperapi.main;

import org.paperapi.webservice.URLRequst;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Sender {

  public List send() throws IOException, ParserConfigurationException, SAXException {
    List<String> pdfList = new ArrayList<>();
    URLRequst urlRequst = new URLRequst();
    URL url = new URL("http://export.arxiv.org/api/query?search_query=all:electron");
    urlRequst.sendRequest(url);
    NodeList entries = urlRequst.readResponse().getDocumentElement().getElementsByTagName("entry");
    for(int i=0;i<entries.getLength();i++){
      Node node = entries.item(i);
      NodeList nodeList = node.getChildNodes();
      for(int j=0;j<nodeList.getLength();j++){
        Node childNode = nodeList.item(j);
        if (childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals("link")) {
          NamedNodeMap attributes = childNode.getAttributes();
          if(attributes.getNamedItem("title")!=null && attributes.getNamedItem("title").getNodeValue().equals("pdf")){
            String pdfUrl = attributes.getNamedItem("href").getNodeValue();
            pdfList.add(pdfUrl);
          }
        }
      }
    }
    return pdfList;
  }
}
