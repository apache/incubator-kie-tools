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

package org.kie.workbench.common.dmn.client.marshaller.unmarshall;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.marshaller.common.DMNDiagramElementsUtils;
import org.kie.workbench.common.dmn.client.marshaller.included.DMNMarshallerImportsClientHelper;
import org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes.NodeEntriesFactory;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.Mock;
import org.uberfire.client.promise.Promises;
import org.uberfire.promise.SyncPromises;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNUnmarshallerTest {

    @Mock
    private FactoryManager factoryManager;

    @Mock
    private Metadata metadata;

    @Mock
    private JSITDefinitions jsitDefinitions;

    @Mock
    private DMNMarshallerImportsClientHelper dmnMarshallerImportsHelper;

    @Mock
    private NodeEntriesFactory modelToStunnerConverter;

    @Mock
    private DMNDiagramElementsUtils dmnDiagramElementsUtils;

    @Mock
    private DMNDiagramsSession dmnDiagramsSession;

    private DMNUnmarshaller dmnUnmarshaller;

    private List<JSITImport> imports;

    private Promises promises;

    @Before
    public void setup() {
        promises = new SyncPromises();
        dmnUnmarshaller = new DMNUnmarshaller(factoryManager,
                                              dmnMarshallerImportsHelper,
                                              promises,
                                              modelToStunnerConverter,
                                              dmnDiagramElementsUtils,
                                              dmnDiagramsSession);
        imports = new ArrayList<>();
        imports.add(mock(JSITImport.class));
        when(jsitDefinitions.getImport()).thenReturn(imports);
        when(dmnMarshallerImportsHelper.getImportDefinitionsAsync(eq(metadata), eq(imports))).thenReturn(promises.resolve(Collections.emptyMap()));
        when(dmnMarshallerImportsHelper.getPMMLDocumentsAsync(eq(metadata), eq(imports))).thenReturn(promises.resolve(Collections.emptyMap()));
    }

    @Test
    public void unmarshall() {
        dmnUnmarshaller.unmarshall(metadata, jsitDefinitions);
        verify(dmnMarshallerImportsHelper, times(1)).getImportDefinitionsAsync(eq(metadata), eq(imports));
        verify(dmnMarshallerImportsHelper, times(1)).getPMMLDocumentsAsync(eq(metadata), eq(imports));
    }
}
