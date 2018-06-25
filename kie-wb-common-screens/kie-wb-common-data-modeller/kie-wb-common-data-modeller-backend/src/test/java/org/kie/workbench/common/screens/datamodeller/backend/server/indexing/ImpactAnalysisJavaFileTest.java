/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.screens.datamodeller.backend.server.indexing;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;

import org.apache.lucene.analysis.Analyzer;
import org.drools.core.beliefsystem.abductive.Abducible;
import org.guvnor.common.services.project.categories.Model;
import org.guvnor.structure.backend.config.Removed;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KieInternalServices;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.workbench.common.screens.javaeditor.type.JavaResourceTypeDefinition;
import org.kie.workbench.common.services.datamodeller.annotations.AnnotationValuesAnnotation;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindAllChangeImpactQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindResourceReferencesQuery;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.kie.workbench.common.services.refactoring.service.impact.QueryOperationRequest;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;

/**
 * This annotation is used by the {{@link #testReferenceQueryInfrastructure()} method.
 */
@AnnotationValuesAnnotation
public class ImpactAnalysisJavaFileTest extends BaseIndexingTest<JavaResourceTypeDefinition> {

    protected Set<NamedQuery> getQueries() {
        return new HashSet<NamedQuery>() {
            {
                add(new FindResourceReferencesQuery() {
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
            }
        };
    }

    /**
     * This field is used by the {{@link #testReferenceQueryInfrastructure()} method.
     */
    @Removed
    private JavaFileIndexer impactAnalysisTestField;

    /**
     * This method is used by the {{@link #testReferenceQueryInfrastructure()} method.
     */
    @Abducible
    private KieInternalServices impactAnalysisTestMethod(KieSession ksession, StatefulKnowledgeSession otherKsession) {
        return null;
    }

    @Before
    public void setupForThisTest() throws Exception {
        // setup
        IOService ioService = ioService();

        // Get this class location
        String fileName = this.getClass().getSimpleName() + ".java";
        String fileLoc = getLocationOfTestClass(fileName);

        // Add this class to the repository/index
        Path path = basePath.resolve(fileName);
        String javaSourceText = loadText(fileLoc);
        ioService.write(path, javaSourceText);

        // Get test java file indexer class location
        fileName = TestJavaFileIndexer.class.getSimpleName() + ".java";
        fileLoc = getLocationOfTestClass(fileName);

        // create new branch
        String randomBranchName = UUID.randomUUID().toString();
        randomBranchName = randomBranchName.substring(0, randomBranchName.indexOf("-"));

        final Path source = ioService.get(URI.create("git://master@" + getRepositoryName()));
        final Path target = ioService.get(URI.create("git://" + randomBranchName + "@" + getRepositoryName()));

        ioService.copy(source,
                       target);

        // Add this class to the repository/index
        final Path branchedBasePath = ioService.get(URI.create("git://" + randomBranchName + "@" + getRepositoryName() + "/_someDir" + seed));
        path = branchedBasePath.resolve(fileName);
        javaSourceText = loadText(fileLoc);
        ioService.write(path, javaSourceText);

        // wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index
        Thread.sleep(5000);
    }

    @Test
    public void testReferenceQueryInfrastructure() throws Exception {
        Class referencedClass = AnnotationValuesAnnotation.class;
        QueryOperationRequest queryOpRequest = QueryOperationRequest
                .references(referencedClass.getName(),
                            ResourceType.JAVA)
                .inAllModules()
                .onAllBranches();

        testQueryOperationRequest(queryOpRequest);

        queryOpRequest = QueryOperationRequest
                .references(referencedClass.getName(),
                            ResourceType.JAVA)
                .inModule(TEST_MODULE_NAME)
                .onAllBranches();

        testQueryOperationRequest(queryOpRequest);

        queryOpRequest = QueryOperationRequest
                .references(referencedClass.getName(),
                            ResourceType.JAVA)
                .inModuleRootPathURI(TEST_MODULE_ROOT)
                .onAllBranches();

        testQueryOperationRequest(queryOpRequest);

        queryOpRequest = QueryOperationRequest
                .references(referencedClass.getName(),
                            ResourceType.JAVA)
                .inAllModules()
                .onBranch("master");

        testQueryOperationRequest(queryOpRequest);
    }

    private void testQueryOperationRequest(QueryOperationRequest queryOpRequest) {
        List<RefactoringPageRow> response = service.queryToList(queryOpRequest);
        assertNotNull("Null PageResonse",
                      response);
        assertNotNull("Null PageRefactoringRow list",
                      response);
        assertEquals("Objects referencing " + AnnotationValuesAnnotation.class.getName(),
                     1,
                     response.size());

        for (RefactoringPageRow row : response) {
            org.uberfire.backend.vfs.Path rowPath = (org.uberfire.backend.vfs.Path) row.getValue();
            logger.debug(rowPath.toURI());
        }

        Object pageRowValue = response.get(0).getValue();
        assertTrue("Expected a " + org.uberfire.backend.vfs.Path.class.getName() + ", not a " + pageRowValue.getClass().getSimpleName(),
                   org.uberfire.backend.vfs.Path.class.isAssignableFrom(pageRowValue.getClass()));
        String fileName = ((org.uberfire.backend.vfs.Path) pageRowValue).getFileName();
        assertTrue("File does not end with '.java'",
                   fileName.endsWith(".java"));
        assertEquals("File name",
                     this.getClass().getSimpleName(),
                     fileName.subSequence(0,
                                          fileName.indexOf(".java")));
    }

    private String getLocationOfTestClass(String fileName) throws Exception {
        URL url = this.getClass().getProtectionDomain().getCodeSource().getLocation();
        String loc = url.toURI().toString();
        loc = loc.replace("target/test-classes/", "src/test/java/");
        String pkgName = this.getClass().getPackage().getName();
        loc = loc + pkgName.replaceAll("\\.", "/") + "/" + fileName;
        loc = loc.replaceAll("/", Matcher.quoteReplacement(File.separator));
        loc = loc.replace("file:", "");

        return loc;
    }

    @Override
    protected TestIndexer<JavaResourceTypeDefinition> getIndexer() {
        return new TestJavaFileIndexer();
    }

    @Override
    public Map<String, Analyzer> getAnalyzers() {
        return Collections.emptyMap();
    }

    @Override
    protected JavaResourceTypeDefinition getResourceTypeDefinition() {
        return new JavaResourceTypeDefinition(new Model());
    }

    @Override
    protected String getRepositoryName() {
        return testName.getMethodName();
    }
}
