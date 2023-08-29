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

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwtmockito.GwtMockitoTestRunner;
import jsinterop.base.JsArrayLike;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.definition.model.ImportDMN;
import org.kie.workbench.common.dmn.api.definition.model.ImportPMML;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLModelMetadata;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDefinitions;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITImport;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITItemDefinition;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class ImportConverterTest {

    private final static String LOCATION_URI = "test.uri";
    private final static String NAMESPACE = "test.namespace";
    private final static String NAME = "name";
    private final static String DESCRIPTION = "file description";
    private final static String DMN_IMPORT_TYPE = "http://www.omg.org/spec/DMN/20180521/MODEL/";
    private final static String PMML_IMPORT_TYPE = "http://www.dmg.org/PMML-4_3";

    @Mock
    private JSITImport jsitImportMock;
    @Mock
    private JSITDefinitions jsitDefinitionsMock;
    @Mock
    private JsArrayLike<JSITDRGElement> JSITDRGElementsMock;

    private PMMLDocumentMetadata pmmlDocumentMetadata;

    @Before
    public void setup() {
        when(jsitImportMock.getLocationURI()).thenReturn(LOCATION_URI);
        when(jsitImportMock.getNamespace()).thenReturn(NAMESPACE);
        when(jsitImportMock.getName()).thenReturn(NAME);
        when(jsitImportMock.getDescription()).thenReturn(DESCRIPTION);
        pmmlDocumentMetadata = new PMMLDocumentMetadata("test.pmml", DMNImportTypes.PMML.toString(), new ArrayList<>());
    }

    @Test
    public void wbFromDMN_DMNImport() {
        when(jsitImportMock.getImportType()).thenReturn(DMN_IMPORT_TYPE);
        when(jsitDefinitionsMock.getDrgElement()).thenReturn(new ArrayList<>(Arrays.asList(mock(JSITDRGElement.class), mock(JSITDRGElement.class))));
        when(jsitDefinitionsMock.getItemDefinition()).thenReturn(new ArrayList<>(Arrays.asList(mock(JSITItemDefinition.class))));
        Import resultImport = ImportConverter.wbFromDMN(jsitImportMock, jsitDefinitionsMock, null);
        assertTrue(resultImport instanceof ImportDMN);
        assertEquals(NAMESPACE, resultImport.getNamespace());
        assertEquals(LOCATION_URI, resultImport.getLocationURI().getValue());
        assertEquals(DESCRIPTION, resultImport.getDescription().getValue());
        assertEquals(NAME, resultImport.getName().getValue());
        assertEquals(DMN_IMPORT_TYPE, resultImport.getImportType());
        assertEquals(2, ((ImportDMN) resultImport).getDrgElementsCount());
        assertEquals(1, ((ImportDMN) resultImport).getItemDefinitionsCount());
    }

    @Test
    public void wbFromDMN_DMNImportNoDefinition() {
        when(jsitImportMock.getImportType()).thenReturn(DMN_IMPORT_TYPE);
        when(jsitDefinitionsMock.getDrgElement()).thenReturn(new ArrayList<>(Arrays.asList(mock(JSITDRGElement.class), mock(JSITDRGElement.class))));
        when(jsitDefinitionsMock.getItemDefinition()).thenReturn(new ArrayList<>(Arrays.asList(mock(JSITItemDefinition.class))));
        Import resultImport = ImportConverter.wbFromDMN(jsitImportMock, null, null);
        assertTrue(resultImport instanceof ImportDMN);
        assertEquals(NAMESPACE, resultImport.getNamespace());
        assertEquals(LOCATION_URI, resultImport.getLocationURI().getValue());
        assertEquals(DESCRIPTION, resultImport.getDescription().getValue());
        assertEquals(NAME, resultImport.getName().getValue());
        assertEquals(DMN_IMPORT_TYPE, resultImport.getImportType());
        assertEquals(0, ((ImportDMN) resultImport).getDrgElementsCount());
        assertEquals(0, ((ImportDMN) resultImport).getItemDefinitionsCount());
    }

    @Test
    public void wbFromDMN_PMMLImportNoModels() {
        when(jsitImportMock.getImportType()).thenReturn(PMML_IMPORT_TYPE);
        Import resultImport = ImportConverter.wbFromDMN(jsitImportMock, null, pmmlDocumentMetadata);
        assertTrue(resultImport instanceof ImportPMML);
        assertEquals(LOCATION_URI, resultImport.getLocationURI().getValue());
        assertEquals(NAME, resultImport.getNamespace());
        assertEquals(DESCRIPTION, resultImport.getDescription().getValue());
        assertEquals(NAME, resultImport.getName().getValue());
        assertEquals(PMML_IMPORT_TYPE, resultImport.getImportType());
        assertNotNull(resultImport.getId().getValue());
        assertEquals(0, ((ImportPMML) resultImport).getModelCount());
    }

    @Test
    public void wbFromDMN_PMMLImportWithModels() {
        when(jsitImportMock.getImportType()).thenReturn(PMML_IMPORT_TYPE);
        pmmlDocumentMetadata.getModels().add(new PMMLModelMetadata("modelName", null));
        Import resultImport = ImportConverter.wbFromDMN(jsitImportMock, null, pmmlDocumentMetadata);
        assertTrue(resultImport instanceof ImportPMML);
        assertEquals(LOCATION_URI, resultImport.getLocationURI().getValue());
        assertEquals(NAME, resultImport.getNamespace());
        assertEquals(DESCRIPTION, resultImport.getDescription().getValue());
        assertEquals(NAME, resultImport.getName().getValue());
        assertEquals(PMML_IMPORT_TYPE, resultImport.getImportType());
        assertNotNull(resultImport.getId().getValue());
        assertEquals(1, ((ImportPMML) resultImport).getModelCount());
    }

    @Test
    public void wbFromDMN_PMMLImportNoPmmlModelMetadata() {
        when(jsitImportMock.getImportType()).thenReturn(PMML_IMPORT_TYPE);
        pmmlDocumentMetadata.getModels().add(new PMMLModelMetadata("modelName", null));
        Import resultImport = ImportConverter.wbFromDMN(jsitImportMock, null, null);
        assertTrue(resultImport instanceof ImportPMML);
        assertEquals(LOCATION_URI, resultImport.getLocationURI().getValue());
        assertEquals(NAME, resultImport.getNamespace());
        assertEquals(DESCRIPTION, resultImport.getDescription().getValue());
        assertEquals(NAME, resultImport.getName().getValue());
        assertEquals(PMML_IMPORT_TYPE, resultImport.getImportType());
        assertNotNull(resultImport.getId().getValue());
        assertEquals(0, ((ImportPMML) resultImport).getModelCount());
    }
}
