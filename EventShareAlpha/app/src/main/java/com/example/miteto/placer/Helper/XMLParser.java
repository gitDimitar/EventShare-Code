package com.example.miteto.placer.Helper;

import android.app.Activity;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Miteto on 02/04/2015.
 */
public class XMLParser
{
    String city;

    public XMLParser(String city)
    {
        this.city = city;
    }

    public String getXmlFromFile(Activity _activity)
    {
        String xml = "";

        InputStream ins = _activity.getResources().openRawResource(_activity.getResources().getIdentifier(city, "raw", _activity.getPackageName()));
        if(ins == null || ins.equals(null))
        {
            xml = "CITY NOT FOUND OR NOT SUPPORTED YET";
            return (xml);
        }

        xml = readTextFile(ins);

            /*
            try
            {
            StringBuilder sb = new StringBuilder();

            File file = new File(Uri.parse("android.resource://com.example.miteto.eventsharealpha/raw/" + placeName).getPath());
            BufferedReader br = new BufferedReader(new FileReader(file));
            String sCurrentLine;


            while ((sCurrentLine = br.readLine()) != null)
            {
                Log.i("CURRENT LINE:", sCurrentLine);
                sb.append(sCurrentLine);
            }


            xml = sb.toString();
            } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        **/

        // return XML
        return xml;
    }

    public Document getDomElement(String xml){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }
        // return DOM
        return doc;
    }

    public String getValue(Element item, String str) {
        NodeList n = item.getElementsByTagName(str);
        return this.getElementValue(n.item(0));
    }

    public final String getElementValue( Node elem ) {
        Node child;
        if( elem != null){
            if (elem.hasChildNodes()){
                for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                    if( child.getNodeType() == Node.TEXT_NODE  ){
                        return child.getNodeValue();
                    }
                }
            }
        }
        return "";
    }

    public String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }
}
