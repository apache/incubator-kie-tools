/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.jboss.errai.reflections.serializers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.jboss.errai.reflections.Reflections;
import org.jboss.errai.reflections.ReflectionsException;
import org.jboss.errai.reflections.util.ConfigurationBuilder;
import org.jboss.errai.reflections.util.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * serialization of Reflections to xml
 * <p/>
 * <p>an example of produced xml:
 * <pre>
 * &#60?xml version="1.0" encoding="UTF-8"?>
 *
 * &#60Reflections>
 *  &#60org.reflections.scanners.MethodAnnotationsScanner>
 *      &#60entry>
 *          &#60key>org.reflections.TestModel$AM1&#60/key>
 *          &#60values>
 *              &#60value>org.reflections.TestModel$C4.m3()&#60/value>
 *              &#60value>org.reflections.TestModel$C4.m1(int[][], java.lang.String[][])&#60/value>
 * ...
 * </pre>
 */
public class XmlSerializer implements Serializer {

    private static final String  REFLECTIONS_TAG = "Reflections";
    private static final String  ENTRY_TAG = "entry";
    private static final String  KEY_TAG = "key";
    private static final String  VALUES_TAG = "values";
    private static final String  VALUE_TAG = "value";

  @Override
  public Reflections read(InputStream inputStream) {
    Reflections reflections = new Reflections(new ConfigurationBuilder());
    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    try {
      // to be compliant, completely disable DOCTYPE declaration:
      dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document document = dBuilder.parse(inputStream);
      NodeList indexNodeList = document.getDocumentElement().getChildNodes();
      for (int i = 0; i < indexNodeList.getLength(); i++) {
        Node item = indexNodeList.item(i);
        if (item instanceof Element) {
          Element indexElement = (Element) item;
          NodeList entryNodeList = indexElement.getElementsByTagName(ENTRY_TAG);
          for (int j = 0; j < entryNodeList.getLength(); j++) {
            Element entryElement = (Element) entryNodeList.item(j);
            Element keyElement = (Element) entryElement.getElementsByTagName(KEY_TAG).item(0);
            Element valuesElement = (Element) entryElement.getElementsByTagName(VALUES_TAG).item(0);
            NodeList valuesNodeList = valuesElement.getElementsByTagName(VALUE_TAG);
            for (int k = 0; k < valuesNodeList.getLength(); k++) {
              Element valueElement = (Element) valuesNodeList.item(k);
              String indexName = indexElement.getTagName();
              Multimap<String, String> map = reflections.getStore().getStoreMap().get(indexName);
              if (map == null) {
                reflections.getStore().getStoreMap().put(indexName, map = HashMultimap.create());
              }
              map.put(keyElement.getTextContent(), valueElement.getTextContent());
            }
          }
        }
      }
      return reflections;
    }catch(ParserConfigurationException|IOException|SAXException e){
      throw new ReflectionsException(e);
    }

  }

  @Override
  public File save(final Reflections reflections, final String filename) {
    File file = Utils.prepareFile(filename);
    try(Writer writer = new FileWriter(filename)) {
      write(reflections,writer);
    }
    catch (ParserConfigurationException|TransformerException|IOException e) {
      throw new ReflectionsException("could not save to file " + filename, e);
    }

    return file;
  }

  @Override
  public String toString(final Reflections reflections) {
    try (StringWriter writer = new StringWriter()){
      write(reflections,writer);
      return writer.toString();
    }
    catch (ParserConfigurationException|TransformerException|IOException e) {
      throw new ReflectionsException(e);
    }
  }


  private static void write(final Reflections reflections, Writer outputWriter) throws ParserConfigurationException, TransformerException {
      Document document = createDocument(reflections);
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      // to be compliant, prohibit the use of all protocols by external entities:
      transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
      transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      DOMSource source = new DOMSource(document);
      StreamResult result = new StreamResult(outputWriter);
      transformer.transform(source, result);
  }

  private static Document createDocument(final Reflections reflections) throws ParserConfigurationException {
    final Map<String, Multimap<String, String>> map = reflections.getStore().getStoreMap();
    DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    Document document = documentBuilder.newDocument();
    Element reflectionsElement = document.createElement(REFLECTIONS_TAG);
    document.appendChild(reflectionsElement);

    for (String indexName : map.keySet()) {
      Element indexElement = document.createElement(indexName);
      reflectionsElement.appendChild(indexElement);

      for (String key : map.get(indexName).keySet()) {
        Element entryElement = document.createElement(ENTRY_TAG);
        indexElement.appendChild(entryElement);
        Element keyElement = document.createElement(KEY_TAG);
        keyElement.appendChild(document.createTextNode(key));
        entryElement.appendChild(keyElement);

        Element valuesElement = document.createElement(VALUES_TAG);
        for (String value : map.get(indexName).get(key)) {
          Element valueElement = document.createElement(VALUE_TAG);
          valueElement.appendChild(document.createTextNode(value));
          valuesElement.appendChild(valueElement);
        }
        entryElement.appendChild(valuesElement);
      }
    }
    return document;
  }

}
