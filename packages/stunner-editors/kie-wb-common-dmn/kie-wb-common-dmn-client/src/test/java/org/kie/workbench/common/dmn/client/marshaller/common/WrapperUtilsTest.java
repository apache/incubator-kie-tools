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

package org.kie.workbench.common.dmn.client.marshaller.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.model.DMNElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.marshaller.common.WrapperUtils.getDmnElementRef;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class WrapperUtilsTest {

    @Test
    public void testGetDmnElementRefWithNamespace() {

        final Decision drgElement = mock(Decision.class);
        final View<? extends DMNElement> view = new ViewImpl<>(drgElement, null);

        final Name drgElementName = mock(Name.class);
        final Name importName = mock(Name.class);
        final Id id = mock(Id.class);
        final org.kie.workbench.common.dmn.api.definition.model.Definitions definitions = mock(org.kie.workbench.common.dmn.api.definition.model.Definitions.class);
        final Import anImport = mock(Import.class);
        final List<Import> imports = singletonList(anImport);
        final String includedModelName = "includedModel";
        final String defaultNamespace = "://default";
        final String namespaceName = "include1";
        final String importNamespace = "://namespace";
        final Map<String, String> nsContext = new HashMap<>();

        when(importName.getValue()).thenReturn(includedModelName);

        when(anImport.getName()).thenReturn(importName);
        when(anImport.getNamespace()).thenReturn(importNamespace);

        when(id.getValue()).thenReturn("0000-1111-2222");
        when(drgElementName.getValue()).thenReturn(includedModelName + ".Decision");
        when(drgElement.getId()).thenReturn(id);
        when(drgElement.getName()).thenReturn(drgElementName);
        when(drgElement.getParent()).thenReturn(definitions);

        nsContext.put(namespaceName, importNamespace);
        when(definitions.getImport()).thenReturn(imports);
        when(definitions.getNsContext()).thenReturn(nsContext);

        final QName actual = getDmnElementRef(definitions, view, defaultNamespace);

        assertEquals(defaultNamespace, actual.getNamespaceURI());
        assertEquals("include1:0000-1111-2222", actual.getLocalPart());
        assertEquals("", actual.getPrefix());
    }

    @Test
    public void testGetDmnElementRefWithNamespaceWhenImportHasAnOddName() {

        final Decision drgElement = mock(Decision.class);
        final View<? extends DMNElement> view = new ViewImpl<>(drgElement, null);

        final Name drgElementName = mock(Name.class);
        final Name importName = mock(Name.class);
        final Id id = mock(Id.class);
        final org.kie.workbench.common.dmn.api.definition.model.Definitions definitions = mock(org.kie.workbench.common.dmn.api.definition.model.Definitions.class);
        final Import anImport = mock(Import.class);
        final List<Import> imports = singletonList(anImport);
        final String includedModelName = "d.i.v.i.";
        final String defaultNamespace = "://default";
        final String namespaceName = "include1";
        final String importNamespace = "://namespace";
        final Map<String, String> nsContext = new HashMap<>();

        when(importName.getValue()).thenReturn(includedModelName);

        when(anImport.getName()).thenReturn(importName);
        when(anImport.getNamespace()).thenReturn(importNamespace);

        when(id.getValue()).thenReturn("0000-1111-2222");
        when(drgElementName.getValue()).thenReturn(includedModelName + ".Decision");
        when(drgElement.getId()).thenReturn(id);
        when(drgElement.getName()).thenReturn(drgElementName);
        when(drgElement.getParent()).thenReturn(definitions);

        nsContext.put(namespaceName, importNamespace);
        when(definitions.getImport()).thenReturn(imports);
        when(definitions.getNsContext()).thenReturn(nsContext);

        final QName actual = getDmnElementRef(definitions, view, defaultNamespace);

        assertEquals(defaultNamespace, actual.getNamespaceURI());
        assertEquals("include1:0000-1111-2222", actual.getLocalPart());
        assertEquals("", actual.getPrefix());
    }

    @Test
    public void testGetDmnElementRefWithFakeNamespace() {

        final Decision drgElement = mock(Decision.class);
        final View<? extends DMNElement> view = new ViewImpl<>(drgElement, null);
        final String defaultNamespace = "://default";

        final Name drgElementName = mock(Name.class);
        final Id id = mock(Id.class);
        final org.kie.workbench.common.dmn.api.definition.model.Definitions definitions = mock(org.kie.workbench.common.dmn.api.definition.model.Definitions.class);

        when(id.getValue()).thenReturn("0000-1111-2222");
        when(drgElementName.getValue()).thenReturn("fakeNamespace.Decision");
        when(drgElement.getId()).thenReturn(id);
        when(drgElement.getName()).thenReturn(drgElementName);
        when(drgElement.getParent()).thenReturn(definitions);

        when(definitions.getImport()).thenReturn(emptyList());

        final QName actual = getDmnElementRef(definitions, view, defaultNamespace);

        assertEquals(defaultNamespace, actual.getNamespaceURI());
        assertEquals("0000-1111-2222", actual.getLocalPart());
        assertEquals("", actual.getPrefix());
    }

    @Test
    public void testGetDmnElementRefWithoutNamespace() {

        final Decision drgElement = mock(Decision.class);
        final View<? extends DMNElement> view = new ViewImpl<>(drgElement, null);
        final String defaultNamespace = "://default";

        final Name drgElementName = mock(Name.class);
        final Id id = mock(Id.class);
        final org.kie.workbench.common.dmn.api.definition.model.Definitions definitions = mock(org.kie.workbench.common.dmn.api.definition.model.Definitions.class);

        when(id.getValue()).thenReturn("0000-1111-2222");
        when(drgElementName.getValue()).thenReturn("Decision");
        when(drgElement.getId()).thenReturn(id);
        when(drgElement.getName()).thenReturn(drgElementName);
        when(drgElement.getParent()).thenReturn(definitions);

        when(definitions.getImport()).thenReturn(emptyList());

        final QName actual = getDmnElementRef(definitions, view, defaultNamespace);

        assertEquals(defaultNamespace, actual.getNamespaceURI());
        assertEquals("0000-1111-2222", actual.getLocalPart());
        assertEquals("", actual.getPrefix());
    }
}
