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

package org.kie.workbench.common.dmn.client.marshaller.converters;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HrefBuilderTest {

    @Test
    public void testGetHref() {

        final DRGElement drgElement = mock(DRGElement.class);
        final Name name = mock(Name.class);
        final Id id = mock(Id.class);
        final Definitions definitions = mock(Definitions.class);
        final String uuid = "0000-1111-2222";

        when(id.getValue()).thenReturn(uuid);
        when(name.getValue()).thenReturn("Decision");
        when(drgElement.getId()).thenReturn(id);
        when(drgElement.getName()).thenReturn(name);
        when(drgElement.getParent()).thenReturn(definitions);
        when(definitions.getImport()).thenReturn(Collections.emptyList());

        final String actual = HrefBuilder.getHref(drgElement);
        final String expected = "#" + uuid;

        assertEquals(expected, actual);
    }

    @Test
    public void testGetHrefForImportedDRGElement() {

        final DRGElement drgElement = mock(DRGElement.class);
        final Name drgElementName = mock(Name.class);
        final Name importName = mock(Name.class);
        final Id id = mock(Id.class);
        final Definitions definitions = mock(Definitions.class);
        final Import anImport = mock(Import.class);
        final List<Import> imports = singletonList(anImport);
        final String includedModelName = "includedModel";

        when(importName.getValue()).thenReturn(includedModelName);

        when(anImport.getName()).thenReturn(importName);
        when(anImport.getNamespace()).thenReturn("https://github.com/kiegroup/dmn/something");

        when(id.getValue()).thenReturn("0000-1111-2222");
        when(drgElementName.getValue()).thenReturn(includedModelName + ".Decision");
        when(drgElement.getId()).thenReturn(id);
        when(drgElement.getName()).thenReturn(drgElementName);
        when(drgElement.getParent()).thenReturn(definitions);

        when(definitions.getImport()).thenReturn(imports);

        final String actual = HrefBuilder.getHref(drgElement);
        final String expected = "https://github.com/kiegroup/dmn/something#0000-1111-2222";

        assertEquals(expected, actual);
    }

    @Test
    public void testGetHrefForImportedDRGElementWhenImportHasAnOddName() {

        final DRGElement drgElement = mock(DRGElement.class);
        final Name drgElementName = mock(Name.class);
        final Name importName = mock(Name.class);
        final Id id = mock(Id.class);
        final Definitions definitions = mock(Definitions.class);
        final Import anImport = mock(Import.class);
        final List<Import> imports = singletonList(anImport);
        final String includedModelName = "d.i.v.i.";

        when(importName.getValue()).thenReturn(includedModelName);

        when(anImport.getName()).thenReturn(importName);
        when(anImport.getNamespace()).thenReturn("https://github.com/kiegroup/dmn/something");

        when(id.getValue()).thenReturn("0000-1111-2222");
        when(drgElementName.getValue()).thenReturn(includedModelName + ".Decision");
        when(drgElement.getId()).thenReturn(id);
        when(drgElement.getName()).thenReturn(drgElementName);
        when(drgElement.getParent()).thenReturn(definitions);

        when(definitions.getImport()).thenReturn(imports);

        final String actual = HrefBuilder.getHref(drgElement);
        final String expected = "https://github.com/kiegroup/dmn/something#0000-1111-2222";

        assertEquals(expected, actual);
    }
}
