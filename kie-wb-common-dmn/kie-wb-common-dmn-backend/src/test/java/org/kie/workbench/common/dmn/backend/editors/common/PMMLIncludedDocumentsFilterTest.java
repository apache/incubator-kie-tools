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

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.editors.included.DMNImportTypes;
import org.kie.workbench.common.dmn.api.editors.included.PMMLDocumentMetadata;
import org.kie.workbench.common.dmn.api.editors.included.PMMLIncludedModel;
import org.kie.workbench.common.dmn.backend.common.DMNPathsHelper;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PMMLIncludedDocumentsFilterTest {

    private static final String NAME = "name";

    private static final String PACKAGE = "package";

    private static final String IMPORT_PATH = "path";

    private static final Integer MODEL_COUNT = 1;

    @Mock
    private DMNPathsHelper pathsHelper;

    @Mock
    private PMMLIncludedDocumentFactory pmmlDocumentFactory;

    @Mock
    private Path dmnModelPath;

    @Mock
    private Path includedModelPath;

    @Mock
    private PMMLDocumentMetadata pmmlDocumentMetadata;

    private List<PMMLIncludedModel> includedModels;

    private PMMLIncludedDocumentsFilter filter;

    @Before
    public void setup() {
        this.filter = new PMMLIncludedDocumentsFilter(pathsHelper,
                                                      pmmlDocumentFactory);
        this.includedModels = new ArrayList<>();

        when(pmmlDocumentFactory.getDocumentByPath(eq(includedModelPath), any(PMMLIncludedModel.class))).thenReturn(pmmlDocumentMetadata);
    }

    @Test
    public void testGetDocumentFromImportsWithNoImports() {
        assertThat(filter.getDocumentFromImports(dmnModelPath,
                                                 includedModelPath,
                                                 includedModels)).isNull();
    }

    @Test
    public void testGetDocumentFromImportsWithImportsFileIsNotImported() {
        includedModels.add(new PMMLIncludedModel(NAME,
                                                 PACKAGE,
                                                 IMPORT_PATH,
                                                 DMNImportTypes.DMN.getDefaultNamespace(),
                                                 MODEL_COUNT));

        when(pathsHelper.getRelativeURI(dmnModelPath, includedModelPath)).thenReturn("includedModelPath");

        assertThat(filter.getDocumentFromImports(dmnModelPath,
                                                 includedModelPath,
                                                 includedModels)).isNull();
    }

    @Test
    public void testGetDocumentFromImportsWithImportsFileIsImported() {
        includedModels.add(new PMMLIncludedModel(NAME,
                                                 PACKAGE,
                                                 IMPORT_PATH,
                                                 DMNImportTypes.DMN.getDefaultNamespace(),
                                                 MODEL_COUNT));

        when(pathsHelper.getRelativeURI(dmnModelPath, includedModelPath)).thenReturn(IMPORT_PATH);

        assertThat(filter.getDocumentFromImports(dmnModelPath,
                                                 includedModelPath,
                                                 includedModels)).isEqualTo(pmmlDocumentMetadata);
    }
}
