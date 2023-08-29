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


package org.eclipse.emf.ecore.xmi.resource.xml;

import com.google.gwt.xml.client.Attr;
import com.google.gwt.xml.client.Node;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.impl.BaseElementImpl;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.xmi.util.GwtDOMHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class XMLSaveTest {

    @Mock
    private XMLHelper xmlHelper;

    @Mock
    private GwtDOMHandler gwtDOMHandler;

    @Mock
    private XMLDOMHandler xmldomHandler;

    @Mock
    private XMLSave.Lookup featureTable;

    @Mock
    private Node currentNode;

    private XMLSave tested;

    @Before
    public void setUp() {
        tested = new XMLSave(xmlHelper);
        tested.toDOM = true;
        tested.currentNode = this.currentNode;
        tested.gwtDocumentHandler = this.gwtDOMHandler;
        tested.featureTable = this.featureTable;
        tested.handler = xmldomHandler;
        EStructuralFeature[] features = new EStructuralFeature[0];
        when(featureTable.getFeatures(any())).thenReturn(features);
    }

    /*
        Ensure once saving an object, if not id has been set, to set it properly.
     */
    @Test
    public void testSaveElementID() {
        BaseElementImpl obj = mock(BaseElementImpl.class);
        FormalExpression expression = mock(FormalExpression.class);
        when(currentNode.getParentNode()).thenReturn(currentNode);
        when(xmlHelper.getID(any())).thenReturn(null);
        Attr attr = mock(Attr.class);
        when(gwtDOMHandler.createAttributeNS(any(), any())).thenReturn(attr);
        tested.saveElementID(obj);
        tested.saveElementID(expression);
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        verify(obj, times(1)).setId(idCaptor.capture());
        String id = idCaptor.getValue();
        verify(attr, times(1)).setNodeValue(eq(id));
        verify(gwtDOMHandler, times(1)).setAttributeNodeNS(eq(currentNode), eq(attr));
        verify(xmldomHandler, times(1)).recordValues(eq(attr), eq(obj), eq(null), eq(obj));
    }
}
