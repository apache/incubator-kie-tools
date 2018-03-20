/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.forms.migration.legacy.services.impl.util;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.xerces.impl.dv.util.Base64;
import org.apache.xerces.util.XMLChar;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 */
public class XMLNode implements Serializable {

    private String objectName;

    private Properties attributes = new Properties();
    private List<XMLNode> children = new ArrayList<XMLNode>();
    private byte[] content;
    private List warnings = new ArrayList();
    private List warningArguments = new ArrayList();
    private XMLNode parent;

    private static final String INDENT_STR = "    ";

    public XMLNode(String objectName, XMLNode parent) {
        this.parent = parent;
        this.objectName = objectName;
    }

    public String getObjectName() {
        return objectName;
    }

    public Properties getAttributes() {
        return attributes;
    }

    public List<XMLNode> getChildren() {
        return children;
    }

    public List getWarnings() {
        return warnings;
    }

    public List getWarningArguments() {
        return warningArguments;
    }

    public XMLNode getParent() {
        return parent;
    }

    public Object addAttribute(String name, String value) {
        if (name != null) {
            if (value == null) {
                return attributes.remove(name);
            }
            return attributes.setProperty(name, value);
        }
        return null;
    }

    public void addChild(XMLNode node) {
        children.add(node);
    }

    public void addWarning(String warning, Object[] arguments) {
        warnings.add(warning);
        warningArguments.add(arguments);
    }

    public void addWarning(String warning, Object arguments) {
        warnings.add(warning);
        warningArguments.add(new Object[]{arguments});
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void writeXML(Writer writer, boolean blanks) throws IOException {
        writeXML(writer, blanks, false);
    }

    public void writeXML(Writer writer, boolean blanks, boolean indent) throws IOException {

        String indentStr = null;
        if (blanks) {
            writer.write("\n");
            if (indent) {
                indentStr = indentStr(indent());
                writer.write(indentStr);
            }
        }
        writer.write("<");
        writer.write(objectName);
        for (Iterator it = attributes.keySet().iterator(); it.hasNext(); ) {
            String attributeName = (String) it.next();
            String attributeValue = attributes.getProperty(attributeName, "");
            writer.write(" " + attributeName + "=\"" + escapeXml(attributeValue) + "\"");
        }
        if (children.isEmpty() && content == null) {
            writer.write("/>");
        } else {
            writer.write(">");
            for (int i = 0; i < children.size(); i++) {
                XMLNode child = children.get(i);
                child.writeXML(writer, blanks, indent);
            }
            if (content != null) {
                writer.write(Base64.encode(content));
            } else {
                if (blanks) {
                    writer.write("\n");
                    if (indent) {
                        writer.write(indentStr);
                    }
                }
            }
            writer.write("</");
            writer.write(objectName);
            writer.write(">");
        }
        writer.flush();
    }

    public void loadFromXMLNode(Node node) {
        objectName = node.getNodeName();
        NamedNodeMap attributesMap = node.getAttributes();
        if (attributesMap != null) {
            for (int i = 0; i < attributesMap.getLength(); i++) {
                Node attribute = attributesMap.item(i);
                addAttribute(attribute.getNodeName(), StringEscapeUtils.unescapeXml(attribute.getNodeValue()));
            }
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if (child.getNodeName().equals("#text")) {
                String content = child.getNodeValue();
                if (content != null && content.trim().length() > 0) {
                    setContent(Base64.decode(child.getNodeValue().trim()));
                }
            } else {
                XMLNode childNode = new XMLNode("?", this);
                childNode.loadFromXMLNode(child);
                addChild(childNode);
            }
        }
    }

    public int indent() {
        if (getParent() != null) {
            return 1 + getParent().indent();
        }
        return 0;
    }

    public static String escapeXml(String s) {
        s = StringEscapeUtils.escapeXml(s);
        StringBuffer dest = new StringBuffer();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (XMLChar.isValid(c)) {
                dest.append(c);
            }
        }
        return dest.toString();
    }

    public String indentStr(int indent) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("");
        for (int i = 0; i < indent; i++) {
            stringBuffer.append(INDENT_STR);
        }
        return stringBuffer.toString();
    }
}
