/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.editor.backend.indexing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Instance;

import org.guvnor.common.services.project.categories.Form;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.editor.backend.indexing.query.FindFormDefinitionIdsQuery;
import org.kie.workbench.common.forms.editor.type.FormResourceTypeDefinition;
import org.kie.workbench.common.forms.fields.test.TestMetaDataEntryManager;
import org.kie.workbench.common.forms.model.FormModel;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FieldSerializer;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormDefinitionSerializerImpl;
import org.kie.workbench.common.forms.services.backend.serialization.impl.FormModelSerializer;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindAllChangeImpactQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindResourcesQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.java.nio.file.Path;
import org.uberfire.paging.PageResponse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FormDefinitionIndexerTest extends BaseIndexingTest<FormResourceTypeDefinition> {

    private static final long WAIT_TIME_MILLIS = 5000;

    private static final int MAX_WAIT_TIMES = 8;

    private final static String[] FORMS = {
            "none.frm",
            "PersonFull.frm",
            "PersonShort.frm",
            "test.test-taskform.frm",
            "testTask-taskform.frm"
    };

    private TestFormDefinitionIndexer indexer;

    @Override
    public void setup() throws IOException {
        FormModelVisitor visitor = mock(FormModelVisitor.class);
        FormModelVisitorProvider provider = mock(FormModelVisitorProvider.class);
        when(provider.getModelType()).thenReturn(FormModel.class);
        when(provider.getVisitor()).thenReturn(visitor);

        Instance<FormModelVisitorProvider<?>> providersInstance = mock(Instance.class);

        List<FormModelVisitorProvider<?>> providers = Arrays.asList(provider);

        when(providersInstance.iterator()).thenReturn(providers.iterator());

        indexer = spy(new TestFormDefinitionIndexer(new FormResourceTypeDefinition(new Form()),
                                                    new FormDefinitionSerializerImpl(new FieldSerializer(),
                                                                                     new FormModelSerializer(),
                                                                                     new TestMetaDataEntryManager()),
                                                    providersInstance));

        when(indexer.getProviderForModel(any())).thenReturn(provider);

        super.setup();
    }

    @Test
    public void testFormsIndexing() throws Exception {
        List<Path> pathList = new ArrayList<>();

        ioService().startBatch(ioService().getFileSystem(basePath.toUri()));
        for (String formFile : FORMS) {
            Path path = basePath.resolve(formFile);
            pathList.add(path);
            String formContent = loadText(formFile);
            ioService().write(path,
                              formContent);
        }
        ioService().endBatch();

        Path[] paths = pathList.toArray(new Path[pathList.size()]);

        {
            PageResponse<RefactoringPageRow> response = null;
            try {
                for (int i = 0; i < MAX_WAIT_TIMES; i++) {
                    Thread.sleep(WAIT_TIME_MILLIS);
                    response = getFormResources();
                    if (response != null && response.getPageRowList() != null && response.getPageRowList().size() >= paths.length) {
                        break;
                    }
                }
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }

            assertNotNull(response);

            Set<RefactoringPageRow> result = new HashSet(response.getPageRowList());

            assertEquals(paths.length,
                         result.size());
        }
        {
            final Set<ValueIndexTerm> queryTerms = new HashSet<ValueIndexTerm>() {{
                add(new ValueResourceIndexTerm("*",
                                               ResourceType.FORM,
                                               ValueIndexTerm.TermSearchType.WILDCARD));
            }};
            try {
                Set<RefactoringPageRow> response = new HashSet(service.query(
                        FindFormDefinitionIdsQuery.NAME,
                        queryTerms));
                assertNotNull(response);
                assertEquals(paths.length,
                             response.size());

                for (String expectedId : FORMS) {
                    boolean foundId = false;
                    for (RefactoringPageRow row : response) {
                        Map<String, org.uberfire.backend.vfs.Path> mapRow = (Map<String, org.uberfire.backend.vfs.Path>) row.getValue();
                        for (String rKey : mapRow.keySet()) {
                            assertTrue(Arrays.asList(FORMS).contains(rKey));
                            foundId = true;
                        }
                    }
                    if (!foundId) {
                        fail("FormDefinition with ID <" + expectedId + " not found in results for " + FindFormDefinitionIdsQuery.NAME);
                    }
                }
            } catch (IllegalArgumentException e) {
                fail("Exception thrown: " + e.getMessage());
            }
        }
    }

    private PageResponse<RefactoringPageRow> getFormResources() throws IllegalArgumentException {
        final RefactoringPageRequest request = new RefactoringPageRequest(FindResourcesQuery.NAME,
                                                                          new HashSet<ValueIndexTerm>() {{
                                                                              add(new ValueResourceIndexTerm("*",
                                                                                                             ResourceType.FORM,
                                                                                                             ValueIndexTerm.TermSearchType.WILDCARD));
                                                                          }},
                                                                          0,
                                                                          10);

        return service.query(request);
    }

    protected Set<NamedQuery> getQueries() {
        return new HashSet<NamedQuery>() {{
            add(new FindResourcesQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder(ioService());
                }
            });
            add(new FindAllChangeImpactQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder(ioService());
                }
            });
            add(new FindFormDefinitionIdsQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new FindFormDefinitionIdsQuery.FindFormDefinitionIdsResponseBuilder(ioService());
                }
            });
        }};
    }

    @Override
    protected String getRepositoryName() {
        return testName.getMethodName();
    }

    @Override
    protected TestIndexer<FormResourceTypeDefinition> getIndexer() {
        return indexer;
    }

    @Override
    protected FormResourceTypeDefinition getResourceTypeDefinition() {
        return new FormResourceTypeDefinition(new Form());
    }
}
