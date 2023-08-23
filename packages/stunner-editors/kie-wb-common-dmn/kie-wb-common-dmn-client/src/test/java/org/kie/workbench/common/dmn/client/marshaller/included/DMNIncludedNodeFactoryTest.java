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

package org.kie.workbench.common.dmn.client.marshaller.included;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasVariable;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.IsInformationItem;
import org.kie.workbench.common.dmn.api.editors.included.DMNIncludedNode;
import org.kie.workbench.common.dmn.api.editors.included.IncludedModel;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@RunWith(GwtMockitoTestRunner.class)
public class DMNIncludedNodeFactoryTest {

    @Test
    public void testDrgElementWithNamespace() {

        final DRGElement drgElement = mock(DRGElement.class, withSettings().extraInterfaces(HasVariable.class));
        final IncludedModel includedModel = mock(IncludedModel.class);
        final DMNIncludedNodeFactory factory = mock(DMNIncludedNodeFactory.class);
        final Id elementId = mock(Id.class);
        final String theId = "theId";
        final String theName = "theName";
        final String tType = "tType";
        final String namespaceUri = "namespaceUri";
        final String prefix = "prefix";
        final String modelName = "Model Name";

        final Name elementName = mock(Name.class);
        final IsInformationItem informationItem = mock(IsInformationItem.class);
        final QName qName = mock(QName.class);
        final Name createdName = mock(Name.class);
        final QName typeRef = mock(QName.class);

        when(includedModel.getModelName()).thenReturn(modelName);
        when(elementId.getValue()).thenReturn(theId);
        when(elementName.getValue()).thenReturn(theName);
        when(drgElement.getName()).thenReturn(elementName);
        when(drgElement.getId()).thenReturn(elementId);
        when(((HasVariable) drgElement).getVariable()).thenReturn(informationItem);
        when(qName.getLocalPart()).thenReturn(tType);
        when(informationItem.getTypeRef()).thenReturn(qName);
        when(qName.getPrefix()).thenReturn(prefix);
        when(qName.getNamespaceURI()).thenReturn(namespaceUri);
        when(factory.createName(drgElement, modelName)).thenReturn(createdName);
        when(factory.createTypeRef(modelName, qName)).thenReturn(typeRef);
        doCallRealMethod().when(factory).drgElementWithNamespace(drgElement, includedModel);

        factory.drgElementWithNamespace(drgElement, includedModel);

        verify(drgElement).setName(createdName);
        verify(drgElement).setAllowOnlyVisualChange(true);
        verify(factory).setVariable((HasVariable) drgElement, informationItem, typeRef);
    }

    @Test
    public void testMakeDMNIncludedModel() {

        final DMNIncludedNodeFactory factory = mock(DMNIncludedNodeFactory.class);
        final String path = "path";
        final IncludedModel includedModel = mock(IncludedModel.class);
        final DRGElement drgElement = mock(DRGElement.class);
        final DRGElement elementWithNamespace = mock(DRGElement.class);
        doCallRealMethod().when(factory).makeDMNIncludeNode(path, includedModel, drgElement);
        when(factory.drgElementWithNamespace(drgElement, includedModel)).thenReturn(elementWithNamespace);

        final DMNIncludedNode includedNode = factory.makeDMNIncludeNode(path, includedModel, drgElement);

        verify(factory).drgElementWithNamespace(drgElement, includedModel);

        assertEquals(path, includedNode.getFileName());
        assertEquals(elementWithNamespace, includedNode.getDrgElement());
    }

    @Test
    public void testCreateTypeRef() {

        final DMNIncludedNodeFactory factory = new DMNIncludedNodeFactory();
        final QName qname = mock(QName.class);
        final String modelName = "modelName";
        final String namespaceUri = "uri";
        final String prefix = "prefix";
        final String localPart = "localPart";

        when(qname.getNamespaceURI()).thenReturn(namespaceUri);
        when(qname.getPrefix()).thenReturn(prefix);
        when(qname.getLocalPart()).thenReturn(localPart);

        final QName typeRef = factory.createTypeRef(modelName, qname);

        assertEquals(namespaceUri, typeRef.getNamespaceURI());
        assertEquals(modelName + "." + localPart, typeRef.getLocalPart());
        assertEquals(prefix, typeRef.getPrefix());
    }

    @Test
    public void testCreateName() {

        final DMNIncludedNodeFactory factory = new DMNIncludedNodeFactory();
        final DRGElement drgElement = mock(DRGElement.class);
        final String theName = "the name";
        final Name name = new Name(theName);
        final String modelName = "modelName";

        when(drgElement.getName()).thenReturn(name);

        final Name createdName = factory.createName(drgElement, modelName);

        assertEquals(modelName + "." + theName, createdName.getValue());
    }
}
