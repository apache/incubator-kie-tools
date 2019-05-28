/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.v1_1.DRGElement;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.property.dmn.Id;
import org.kie.workbench.common.dmn.api.property.dmn.Name;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HrefBuilderTest {

    @Test
    public void testGetHref() {

        final DRGElement drgElement = mock(DRGElement.class);
        final Id id = mock(Id.class);
        final String stringId = "someId";
        final String expected = "#" + stringId;
        when(id.getValue()).thenReturn(stringId);
        when(drgElement.getId()).thenReturn(id);

        final String actual = HrefBuilder.getHref(drgElement);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetHrefForImported() {

        final DRGElement drgElement = mock(DRGElement.class);
        final Id id = mock(Id.class);
        final String uuid = "_someUuid";
        final String importName = "USER_TYPED_NAME";
        final String uri = "https://github.com/kiegroup/dmn/something";
        final String stringId = importName + ":" + uuid;
        final Definitions definitions = mock(Definitions.class);

        final Name importModelName = mock(Name.class);

        final Map<String, String> nsContext = new HashMap<>();
        nsContext.put(importName, uri);

        when(drgElement.getParent()).thenReturn(definitions);
        when(importModelName.getValue()).thenReturn(importName);
        when(definitions.getNsContext()).thenReturn(nsContext);
        when(id.getValue()).thenReturn(stringId);
        when(drgElement.getId()).thenReturn(id);

        final String actual = HrefBuilder.getHref(drgElement);

        assertEquals(uri + "#" + uuid, actual);
    }
}