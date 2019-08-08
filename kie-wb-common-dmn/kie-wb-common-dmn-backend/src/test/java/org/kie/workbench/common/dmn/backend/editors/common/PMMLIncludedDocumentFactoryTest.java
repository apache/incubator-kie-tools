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

package org.kie.workbench.common.dmn.backend.editors.common;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.core.pmml.PMMLInfo;
import org.kie.dmn.core.pmml.PMMLModelInfo;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.api.editors.included.PMMLModelMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLParameterMetadata;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.NoSuchFileException;

import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PMMLIncludedDocumentFactoryTest {

    private static final String URI = "pmmlFileURI";

    private static final String NAMESPACE = DMNImportTypes.PMML.getDefaultNamespace();

    private static final String DOCUMENT_NAME = "pmmlDocument";

    private static final String MODEL_NAME = "pmmlModel";

    private static final String MODEL_CLASS_NAME = "PmmlModelClassName";

    private static final String MODEL_PACKAGE = "pmmlModelPackage";

    private static final String INPUT_FIELD_PREFIX = "input";

    private static final int INPUT_FIELDS_COUNT = 5;

    @Mock
    private IOService ioService;

    private PMMLIncludedDocumentFactory factory;

    @Before
    public void setup() {
        this.factory = spy(new PMMLIncludedDocumentFactory(ioService));
    }

    @Test
    public void testGetDocumentByPathWithKnownPath() {
        final Path path = mock(Path.class);
        final PMMLInfo<PMMLModelInfo> pmmlInfo = makePMMLInfo();

        when(path.toURI()).thenReturn(URI);
        doReturn(pmmlInfo).when(factory).loadPMMLInfo(path);

        final PMMLDocumentMetadata document = factory.getDocumentByPath(path);

        assertThat(document).isNotNull();
        assertThat(document.getPath()).isEqualTo(URI);
        assertThat(document.getImportType()).isEqualTo(NAMESPACE);
        assertThat(document.getModels()).hasSize(1);

        final PMMLModelMetadata model = document.getModels().get(0);
        assertThat(model.getName()).isEqualTo(MODEL_NAME);
        assertThat(model.getInputParameters()).hasSize(INPUT_FIELDS_COUNT);

        assertThat(model.getInputParameters()).usingElementComparator(comparing(PMMLParameterMetadata::getName, naturalOrder())).containsExactlyInAnyOrder(expectedPMMLParameterMetadata());
    }

    @Test
    public void testGetDocumentByPathWithKnownPathWithIncludedModel() {
        final Path path = mock(Path.class);
        final PMMLInfo<PMMLModelInfo> pmmlInfo = makePMMLInfo();
        final PMMLIncludedModel includedModel = makePMMLIncludedModel();

        when(path.toURI()).thenReturn(URI);
        doReturn(pmmlInfo).when(factory).loadPMMLInfo(path);

        final PMMLDocumentMetadata document = factory.getDocumentByPath(path,
                                                                        includedModel);

        assertThat(document).isNotNull();
        assertThat(document.getPath()).isEqualTo(URI);
        assertThat(document.getImportType()).isEqualTo(NAMESPACE);
        assertThat(document.getName()).isEqualTo(DOCUMENT_NAME);
        assertThat(document.getModels()).hasSize(1);

        final PMMLModelMetadata model = document.getModels().get(0);
        assertThat(model.getName()).isEqualTo(MODEL_NAME);
        assertThat(model.getInputParameters()).hasSize(INPUT_FIELDS_COUNT);

        assertThat(model.getInputParameters()).usingElementComparator(comparing(PMMLParameterMetadata::getName, naturalOrder())).containsExactlyInAnyOrder(expectedPMMLParameterMetadata());
    }

    @Test
    public void testGetDocumentByPathWithUnknownPath() {
        final Path path = mock(Path.class);

        when(path.toURI()).thenReturn(URI);
        when(ioService.newInputStream(any())).thenThrow(new NoSuchFileException());

        final PMMLDocumentMetadata document = factory.getDocumentByPath(path);

        assertThat(document).isNotNull();
        assertThat(document.getPath()).isEqualTo(URI);
        assertThat(document.getImportType()).isEqualTo(NAMESPACE);
        assertThat(document.getModels()).isEmpty();
    }

    @Test
    public void testGetDocumentByPathWithUnknownPathWithIncludedModel() {
        final Path path = mock(Path.class);
        final PMMLIncludedModel includedModel = makePMMLIncludedModel();

        when(path.toURI()).thenReturn(URI);
        when(ioService.newInputStream(any())).thenThrow(new NoSuchFileException());

        final PMMLDocumentMetadata document = factory.getDocumentByPath(path,
                                                                        includedModel);

        assertThat(document).isNotNull();
        assertThat(document.getPath()).isEqualTo(URI);
        assertThat(document.getImportType()).isEqualTo(NAMESPACE);
        assertThat(document.getName()).isEqualTo(DOCUMENT_NAME);
        assertThat(document.getModels()).isEmpty();
    }

    private PMMLInfo<PMMLModelInfo> makePMMLInfo() {
        return new PMMLInfo<>(Collections.singletonList(makePMMLModelInfo()),
                              new PMMLInfo.PMMLHeaderInfo(NAMESPACE,
                                                          Collections.emptyMap()));
    }

    private PMMLModelInfo makePMMLModelInfo() {
        return new PMMLModelInfo(MODEL_NAME,
                                 MODEL_CLASS_NAME,
                                 makePMMLModelInputFieldNames(),
                                 Collections.emptyList(),
                                 Collections.emptyList());
    }

    private Collection<String> makePMMLModelInputFieldNames() {
        return IntStream.range(0, INPUT_FIELDS_COUNT).mapToObj(i -> INPUT_FIELD_PREFIX + i).collect(Collectors.toList());
    }

    private PMMLParameterMetadata[] expectedPMMLParameterMetadata() {
        return IntStream.range(0, INPUT_FIELDS_COUNT).mapToObj(i -> new PMMLParameterMetadata(INPUT_FIELD_PREFIX + i)).collect(Collectors.toSet()).toArray(new PMMLParameterMetadata[]{});
    }

    private PMMLIncludedModel makePMMLIncludedModel() {
        return new PMMLIncludedModel(DOCUMENT_NAME,
                                     MODEL_PACKAGE,
                                     URI,
                                     NAMESPACE,
                                     1);
    }
}
