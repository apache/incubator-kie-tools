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

package org.kie.workbench.common.dmn.backend.definition.v1_1;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefinitionsConverterTest {

    private final String NAMESPACE = "SomePreviousSetNamespace";

    @Mock
    private org.kie.dmn.model.api.Definitions apiDefinitions;

    @Mock
    private Definitions wbDefinitions;

    @Test
    public void wbFromDMN() {
        Definitions wb = DefinitionsConverter.wbFromDMN(apiDefinitions);
        String defaultNs = wb.getNsContext().get(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix());
        String namespace = wb.getNamespace().getValue();

        assertEquals(defaultNs, namespace);
    }

    @Test
    public void dmnFromWB() {
        when(wbDefinitions.getNamespace()).thenReturn(new Text());

        org.kie.dmn.model.api.Definitions dmn = DefinitionsConverter.dmnFromWB(wbDefinitions);
        String defaultNs = dmn.getNsContext().get(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix());
        String namespace = dmn.getNamespace();

        assertNotNull(defaultNs);
        assertEquals(defaultNs, namespace);

        when(wbDefinitions.getNamespace()).thenReturn(new Text(NAMESPACE));

        dmn = DefinitionsConverter.dmnFromWB(wbDefinitions);
        defaultNs = dmn.getNsContext().get(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix());
        namespace = dmn.getNamespace();

        assertNotNull(defaultNs);
        assertEquals(defaultNs, namespace);
        assertEquals(NAMESPACE, defaultNs);
    }
}