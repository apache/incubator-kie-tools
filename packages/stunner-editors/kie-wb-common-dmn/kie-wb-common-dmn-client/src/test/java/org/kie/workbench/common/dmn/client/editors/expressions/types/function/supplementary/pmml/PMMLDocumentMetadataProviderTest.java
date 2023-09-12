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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function.supplementary.pmml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.Import;
import org.kie.workbench.common.dmn.api.definition.model.ImportDMN;
import org.kie.workbench.common.dmn.api.definition.model.ImportPMML;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLModelMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLParameterMetadata;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;
import org.kie.workbench.common.dmn.client.editors.included.imports.IncludedModelsPageStateProviderImpl;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.dmn.client.service.DMNClientServicesProxy;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyListOf;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PMMLDocumentMetadataProviderTest {

    @Mock
    private DMNGraphUtils graphUtils;

    @Mock
    private DMNClientServicesProxy clientServicesProxy;

    @Mock
    private IncludedModelsPageStateProviderImpl stateProvider;

    @Mock
    private Path dmnModelPath;

    @Captor
    private ArgumentCaptor<List<PMMLIncludedModel>> pmmlIncludedModelsArgumentCaptor;

    @Captor
    private ArgumentCaptor<ServiceCallback<List<PMMLDocumentMetadata>>> callbackArgumentCaptor;

    private Definitions definitions;

    private PMMLDocumentMetadataProvider provider;

    @Before
    public void setup() {
        this.definitions = new Definitions();
        this.provider = new PMMLDocumentMetadataProvider(graphUtils,
                                                         clientServicesProxy,
                                                         stateProvider);

        final Diagram diagram = mock(Diagram.class);
        final Metadata metadata = mock(Metadata.class);
        when(stateProvider.getDiagram()).thenReturn(Optional.of(diagram));
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getPath()).thenReturn(dmnModelPath);

        when(graphUtils.getDefinitions(diagram)).thenReturn(definitions);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadPMMLIncludedDocumentsDMNModelPath() {
        provider.loadPMMLIncludedDocuments();

        verify(clientServicesProxy).loadPMMLDocumentsFromImports(eq(dmnModelPath),
                                                                 anyListOf(PMMLIncludedModel.class),
                                                                 any(ServiceCallback.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLoadPMMLIncludedDocumentsPMMLIncludedModels() {
        final Import dmn = new ImportDMN("dmn",
                                         new LocationURI("dmn-location"),
                                         DMNImportTypes.DMN.getDefaultNamespace());
        final Import pmml = new ImportPMML("pmml",
                                           new LocationURI("pmml-location"),
                                           DMNImportTypes.PMML.getDefaultNamespace());
        dmn.getName().setValue("dmn");
        pmml.getName().setValue("pmml");
        definitions.getImport().add(dmn);
        definitions.getImport().add(pmml);

        provider.loadPMMLIncludedDocuments();

        verify(clientServicesProxy).loadPMMLDocumentsFromImports(any(Path.class),
                                                                 pmmlIncludedModelsArgumentCaptor.capture(),
                                                                 any(ServiceCallback.class));

        final List<PMMLIncludedModel> actualIncludedModels = pmmlIncludedModelsArgumentCaptor.getValue();
        assertThat(actualIncludedModels).hasSize(1);

        final PMMLIncludedModel pmmlIncludedModel = actualIncludedModels.get(0);
        assertThat(pmmlIncludedModel.getModelName()).isEqualTo("pmml");
        assertThat(pmmlIncludedModel.getPath()).isEqualTo("pmml-location");
        assertThat(pmmlIncludedModel.getImportType()).isEqualTo(DMNImportTypes.PMML.getDefaultNamespace());
    }

    @Test
    public void testGetPMMLDocumentNames() {
        final List<PMMLDocumentMetadata> pmmlDocuments = new ArrayList<>();
        pmmlDocuments.add(new PMMLDocumentMetadata("path1",
                                                   "zDocument1",
                                                   DMNImportTypes.PMML.getDefaultNamespace(),
                                                   Collections.emptyList()));
        pmmlDocuments.add(new PMMLDocumentMetadata("path2",
                                                   "aDocument2",
                                                   DMNImportTypes.PMML.getDefaultNamespace(),
                                                   Collections.emptyList()));

        final ServiceCallback<List<PMMLDocumentMetadata>> callback = loadPMMLIncludedDocuments();

        callback.onSuccess(pmmlDocuments);

        final List<String> documentNames = provider.getPMMLDocumentNames();
        assertThat(documentNames).containsSequence("aDocument2", "zDocument1");
    }

    private ServiceCallback<List<PMMLDocumentMetadata>> loadPMMLIncludedDocuments() {
        provider.loadPMMLIncludedDocuments();

        verify(clientServicesProxy).loadPMMLDocumentsFromImports(any(Path.class),
                                                                 anyListOf(PMMLIncludedModel.class),
                                                                 callbackArgumentCaptor.capture());

        return callbackArgumentCaptor.getValue();
    }

    @Test
    public void testGetPMMLDocumentModelNames() {
        final List<PMMLDocumentMetadata> pmmlDocuments = new ArrayList<>();
        pmmlDocuments.add(new PMMLDocumentMetadata("path",
                                                   "document",
                                                   DMNImportTypes.PMML.getDefaultNamespace(),
                                                   asList(new PMMLModelMetadata("zModel1",
                                                                                Collections.emptySet()),
                                                          new PMMLModelMetadata("aModel2",
                                                                                Collections.emptySet()))));

        final ServiceCallback<List<PMMLDocumentMetadata>> callback = loadPMMLIncludedDocuments();

        callback.onSuccess(pmmlDocuments);

        final List<String> modelNames = provider.getPMMLDocumentModels("document");
        assertThat(modelNames).containsSequence("aModel2", "zModel1");

        assertThat(provider.getPMMLDocumentModels("unknown")).isEmpty();
    }

    @Test
    public void testGetPMMLDocumentModelParameterNames() {
        final List<PMMLDocumentMetadata> pmmlDocuments = new ArrayList<>();
        pmmlDocuments.add(new PMMLDocumentMetadata("path",
                                                   "document",
                                                   DMNImportTypes.PMML.getDefaultNamespace(),
                                                   singletonList(new PMMLModelMetadata("model",
                                                                                       Stream.of(new PMMLParameterMetadata("zParameter1"),
                                                                                                 new PMMLParameterMetadata("aParameter2"))
                                                                                               .collect(Collectors.toSet())))));

        final ServiceCallback<List<PMMLDocumentMetadata>> callback = loadPMMLIncludedDocuments();

        callback.onSuccess(pmmlDocuments);

        final List<String> modelNames = provider.getPMMLDocumentModelParameterNames("document", "model");
        assertThat(modelNames).containsSequence("aParameter2", "zParameter1");

        assertThat(provider.getPMMLDocumentModelParameterNames("unknown", "unknown")).isEmpty();
    }
}
