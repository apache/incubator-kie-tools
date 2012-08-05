/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.bpm.console.client.process;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NamedNodeMap;
import com.google.gwt.xml.client.Node;
//import org.jboss.bpm.console.client.BpmConsoleClientFactory;
import org.drools.guvnor.client.LazyPanel;
import org.drools.guvnor.client.common.CustomizableListBox;
import org.drools.guvnor.client.util.ConsoleLog;
import org.drools.guvnor.client.util.DOMUtil;
import org.uberfire.client.annotations.WorkbenchEditor;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;

/**
 * @author Heiko.Braun <heiko.braun@jboss.com>
 */
@Dependent
@WorkbenchEditor(identifier = "InstanceDataView")
public class InstanceDataView extends SimplePanel implements LazyPanel {

    public final static String ID = InstanceDataView.class.getName();

    private CustomizableListBox<DataEntry> listBox;

    private String instanceId;

    private boolean isInitialized;

    boolean isRiftsawInstance = false;

    public InstanceDataView(/*BpmConsoleClientFactory clientFactory*/) {
        //isRiftsawInstance = clientFactory.getApplicationContext().getConfig().getProfileName().equals("BPEL Console");
    }

    public void initialize() {
        if (!isInitialized) {
            listBox =
                    new CustomizableListBox<DataEntry>(
                            new CustomizableListBox.ItemFormatter<DataEntry>() {
                                public String format(DataEntry node) {

                                    String result = "";

                                    result += node.key;

                                    result += " ";

                                    result += node.xsd;

                                    result += " ";

                                    result += node.java;

                                    result += " ";

                                    if (isRiftsawInstance) {
                                        JSONTree tree = new JSONTree(node.value);
                                        result += tree;
                                    } else {
                                        result += node.value;
                                    }

                                    return result;
                                }
                            }
                    );

            listBox.setFirstLine("Key, XSD Type, Java Type, Value");

            this.add(listBox);

            this.isInitialized = true;
        }
    }

    public boolean isInitialized() {
        return isInitialized;
    }

    public void update(String instanceId, Document xml) {
        this.instanceId = instanceId;
        parseMessage(xml);
    }

    private void parseMessage(Document messageDom) {
        try {
            // parse the XML document into a DOM
            //Document messageDom = XMLParser.parse(messageXml);

            Node dataSetNode = messageDom.getElementsByTagName("dataset").item(0);
            List<Node> dataSetNodeChildren = DOMUtil.getChildElements(dataSetNode.getChildNodes());
            List<DataEntry> results = new ArrayList<DataEntry>();

            for (Node dataNode : dataSetNodeChildren) {
                DataEntry dataEntry = new DataEntry();
                NamedNodeMap dataNodeAttributes = dataNode.getAttributes();

                Node valueNode = DOMUtil.getChildElements(dataNode.getChildNodes()).get(0); // expected to have just one child鈥�
                NamedNodeMap valueNodeAttributes = valueNode.getAttributes();

                dataEntry.key = dataNodeAttributes.getNamedItem("key").getNodeValue();
                dataEntry.java = dataNodeAttributes.getNamedItem("javaType").getNodeValue();
                dataEntry.xsd = valueNodeAttributes.getNamedItem("xsi:type").getNodeValue();

                List<Node> valueChildElements = DOMUtil.getChildElements(valueNode.getChildNodes());

                if (valueChildElements.isEmpty()
                        && valueNode.hasChildNodes()
                        && Node.TEXT_NODE == valueNode.getChildNodes().item(0).getNodeType()) {
                    dataEntry.value = valueNode.getFirstChild().getNodeValue();
                } else {
                    // complex types or empty elements
                    dataEntry.value = "n/a";
                }

                results.add(dataEntry);
            }

            bindData(results);
        } catch (Throwable e) {
            ConsoleLog.error("Failed to parse XML document", e);
        }

    }

    private void bindData(List<DataEntry> data) {
        initialize();

        listBox.clear();

        for (DataEntry d : data) {
            listBox.addItem(d);
        }
    }

    private class DataEntry {

        String key;
        String xsd;
        String java;
        String value;
    }
    
	@WorkbenchPartTitle
	public String getTitle() {
		return "InstanceDataView";
	}

	@WorkbenchPartView
	public IsWidget getView() {
		return asWidget();
	}
    
}
