/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.webapp.kogito.common.client.converters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNMarshallerKogitoUnmarshallerTest {

    @Mock
    private DMNMarshallerImportsHelperKogito dmnMarshallerImportsHelperKogitoMock;
    @Mock
    private FactoryManager factoryManagerMock;
    @Mock
    private Metadata metadataMock;
    @Mock
    private JSITDefinitions jsitDefinitionsMock;
    
    private DMNMarshallerKogitoUnmarshaller dmnMarshallerKogitoUnmarshaller;
    private List<JSITImport> imports;
    private Promises promises;

    @Before
    public void setup() {
        promises = new SyncPromises();
        dmnMarshallerKogitoUnmarshaller = new DMNMarshallerKogitoUnmarshaller(factoryManagerMock,
                                                                              dmnMarshallerImportsHelperKogitoMock,
                                                                              promises);
        imports = new ArrayList<>();
        imports.add(mock(JSITImport.class));
        when(jsitDefinitionsMock.getImport()).thenReturn(imports);
        when(dmnMarshallerImportsHelperKogitoMock.getImportDefinitionsAsync(eq(metadataMock), eq(imports))).thenReturn(promises.resolve(Collections.emptyMap()));
        when(dmnMarshallerImportsHelperKogitoMock.getPMMLDocumentsAsync(eq(metadataMock), eq(imports))).thenReturn(promises.resolve(Collections.emptyMap()));
    }

    @Test
    public void unmarshall() {
        dmnMarshallerKogitoUnmarshaller.unmarshall(metadataMock, jsitDefinitionsMock);
        verify(dmnMarshallerImportsHelperKogitoMock, times(1)).getImportDefinitionsAsync(eq(metadataMock), eq(imports));
        verify(dmnMarshallerImportsHelperKogitoMock, times(1)).getPMMLDocumentsAsync(eq(metadataMock), eq(imports));
    }
}
