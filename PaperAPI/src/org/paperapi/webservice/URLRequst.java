package org.paperapi.webservice;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class URLRequst {
    HttpURLConnection connection = null;
    private boolean parameterEnable = false;
    private Map<String, String> parameters = new HashMap<>();

    public void sendRequest(URL url) {
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (parameterEnable == true) {
                connection.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(getParameters(parameters));
                out.flush();
                out.close();
            }
            int status = connection.getResponseCode();
            Reader streamReader = null;

            if (status > 299) {
                streamReader = new InputStreamReader(connection.getErrorStream());
            } else {
                streamReader = new InputStreamReader(connection.getInputStream());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setParameter(String parameter, String val) {
        if (parameterEnable == false) {
            parameterEnable = true;
        }
        parameters.put(parameter, val);
    }

    public Document readResponse() throws IOException {
        if (connection == null) {
            return null;
        }
        StringBuilder responseBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (connection.getInputStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                responseBuilder.append((char) c);
            }
        }
        String responseString = responseBuilder.toString();
        Document xml = convertStringToXMLDocument(responseString);
        return xml;
    }

    private String getParameters(Map<String, String> parameters) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }

    private static Document convertStringToXMLDocument(String xmlString) {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));

            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
