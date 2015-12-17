/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.kie.workbench.common.services.refactoring.backend.server.query.findrules;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.inject.Instance;

import org.apache.lucene.analysis.Analyzer;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.drl.TestDrlFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.drl.TestDrlFileTypeDefinition;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.RuleAttributeNameAnalyzer;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQueries;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.RefactoringQueryServiceImpl;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.RuleNameResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindRulesByProjectQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.ProjectRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleAttributeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueProjectRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;
import org.uberfire.java.nio.file.Path;
import org.uberfire.paging.PageResponse;

import static org.apache.lucene.util.Version.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FindRulesByProjectQueryValidIndexTermsTest
        extends BaseIndexingTest<TestDrlFileTypeDefinition> {

    private static final String SOME_OTHER_PROJECT_ROOT = "some/other/projectRoot";

    private Set<NamedQuery> queries = new HashSet<NamedQuery>() {{
        add( new FindRulesByProjectQuery() {
            @Override
            public ResponseBuilder getResponseBuilder() {
                return new RuleNameResponseBuilder();
            }
        } );
    }};
    
    @Override
    protected KieProjectService getProjectService() {

        final KieProjectService mock = super.getProjectService();

        when( mock.resolveProject( any( org.uberfire.backend.vfs.Path.class ) ) )
                .thenAnswer( new Answer() {
                    @Override
                    public Object answer( InvocationOnMock invocationOnMock ) throws Throwable {
                        org.uberfire.backend.vfs.Path resource = (org.uberfire.backend.vfs.Path) invocationOnMock.getArguments()[0];
                        if ( resource.toURI().contains( TEST_PROJECT_ROOT ) ) {
                            return getKieProjectMock( TEST_PROJECT_ROOT );
                        } else if ( resource.toURI().contains( SOME_OTHER_PROJECT_ROOT ) ) {
                            return getKieProjectMock( SOME_OTHER_PROJECT_ROOT );
                        } else {
                            return null;
                        }
                    }
                } );

        return mock;
    }

    private KieProject getKieProjectMock( String testProjectRoot ) {
        final org.uberfire.backend.vfs.Path mockRoot = mock( org.uberfire.backend.vfs.Path.class );
        when( mockRoot.toURI() ).thenReturn( testProjectRoot );

        final KieProject mockProject = mock( KieProject.class );
        when( mockProject.getRootPath() ).thenReturn( mockRoot );
        return mockProject;
    }

    @Test
    public void testQueryValidIndexTerms() throws IOException, InterruptedException {
        final Instance<NamedQuery> namedQueriesProducer = mock( Instance.class );
        when( namedQueriesProducer.iterator() ).thenReturn( queries.iterator() );

        final RefactoringQueryServiceImpl service = new RefactoringQueryServiceImpl( getConfig(),
                                                                                     new NamedQueries( namedQueriesProducer ) );
        service.init();

        //Add test files
        addTestDRL( BaseIndexingTest.TEST_PROJECT_ROOT,
                    "drl1.drl" );
        addTestDRL( BaseIndexingTest.TEST_PROJECT_ROOT,
                    "drl2.drl" );
        addTestDRL( SOME_OTHER_PROJECT_ROOT,
                    "drl3.drl" );
        addTestDRL( BaseIndexingTest.TEST_PROJECT_ROOT,
                    "drl4.drl" );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        {
            final RefactoringPageRequest request = new RefactoringPageRequest( "FindRulesByProjectQuery",
                                                                               new HashSet<ValueIndexTerm>() {{
                                                                                   add( new ValueProjectRootPathIndexTerm( BaseIndexingTest.TEST_PROJECT_ROOT ) );
                                                                                   add( new ValuePackageNameIndexTerm( "" ) );
                                                                               }},
                                                                               true,
                                                                               0,
                                                                               10 );

            try {
                final PageResponse<RefactoringPageRow> response = service.query( request );
                assertNotNull( response );
                assertEquals( 1,
                              response.getPageRowList().size() );
                assertResponseContains( response.getPageRowList(),
                                        "noPackage" );

            } catch (IllegalArgumentException e) {
                fail();
            }
        }

        {
            final RefactoringPageRequest request = new RefactoringPageRequest( "FindRulesByProjectQuery",
                                                                               new HashSet<ValueIndexTerm>() {{
                                                                                   add( new ValueProjectRootPathIndexTerm( "*" ) );
                                                                                   add( new ValuePackageNameIndexTerm( "*" ) );
                                                                               }},
                                                                               true,
                                                                               0,
                                                                               10 );

            try {
                final PageResponse<RefactoringPageRow> response = service.query( request );
                assertNotNull( response );
                assertEquals( 4,
                              response.getPageRowList().size() );
                assertResponseContains( response.getPageRowList(),
                                        "myRule" );
                assertResponseContains( response.getPageRowList(),
                                        "myRule2" );
                assertResponseContains( response.getPageRowList(),
                                        "myRule3" );
                assertResponseContains( response.getPageRowList(),
                                        "noPackage" );

            } catch (IllegalArgumentException e) {
                fail();
            }
        }

        {
            final RefactoringPageRequest request = new RefactoringPageRequest( "FindRulesByProjectQuery",
                                                                               new HashSet<ValueIndexTerm>() {{
                                                                                   add( new ValueProjectRootPathIndexTerm( BaseIndexingTest.TEST_PROJECT_ROOT ) );
                                                                                   add( new ValuePackageNameIndexTerm( BaseIndexingTest.TEST_PACKAGE_NAME ) );
                                                                               }},
                                                                               false,
                                                                               0,
                                                                               10 );

            try {
                final PageResponse<RefactoringPageRow> response = service.query( request );
                assertNotNull( response );
                assertEquals( 2,
                              response.getPageRowList().size() );
                assertResponseContains( response.getPageRowList(),
                                        "myRule" );
                assertResponseContains( response.getPageRowList(),
                                        "myRule2" );

            } catch ( IllegalArgumentException e ) {
                fail();
            }
        }

        {
            final RefactoringPageRequest request = new RefactoringPageRequest( "FindRulesByProjectQuery",
                                                                               new HashSet<ValueIndexTerm>() {{
                                                                                   add( new ValueProjectRootPathIndexTerm( SOME_OTHER_PROJECT_ROOT ) );
                                                                                   add( new ValuePackageNameIndexTerm( BaseIndexingTest.TEST_PACKAGE_NAME ) );
                                                                               }},
                                                                               false,
                                                                               0,
                                                                               10 );

            try {
                final PageResponse<RefactoringPageRow> response = service.query( request );
                assertNotNull( response );
                assertEquals( 1,
                              response.getPageRowList().size() );
                assertResponseContains( response.getPageRowList(),
                                        "myRule3" );

            } catch (IllegalArgumentException e) {
                fail();
            }
        }

        {
            final RefactoringPageRequest request = new RefactoringPageRequest( "FindRulesByProjectQuery",
                                                                               new HashSet<ValueIndexTerm>() {{
                                                                                   add( new ValueProjectRootPathIndexTerm( BaseIndexingTest.TEST_PROJECT_ROOT ) );
                                                                                   add( new ValuePackageNameIndexTerm( "non-existent-package-name" ) );
                                                                               }},
                                                                               false,
                                                                               0,
                                                                               10 );

            try {
                final PageResponse<RefactoringPageRow> response = service.query( request );
                assertNotNull( response );
                assertEquals( 0,
                              response.getPageRowList().size() );

            } catch ( IllegalArgumentException e ) {
                fail();
            }
        }

        {
            final RefactoringPageRequest request = new RefactoringPageRequest( "FindRulesByProjectQuery",
                                                                               new HashSet<ValueIndexTerm>() {{
                                                                                   add( new ValueProjectRootPathIndexTerm( "non-existent-project-root" ) );
                                                                                   add( new ValuePackageNameIndexTerm( BaseIndexingTest.TEST_PACKAGE_NAME ) );
                                                                               }},
                                                                               false,
                                                                               0,
                                                                               10 );

            try {
                final PageResponse<RefactoringPageRow> response = service.query( request );
                assertNotNull( response );
                assertEquals( 0,
                              response.getPageRowList().size() );

            } catch ( IllegalArgumentException e ) {
                fail();
            }
        }

    }

    private void addTestDRL( final String projectName,
                             final String pathToFile ) throws IOException {
        final Path path = basePath.resolve( projectName + "/" + pathToFile );
        final String drl = loadText( pathToFile );
        ioService().write( path,
                           drl );
    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestDrlFileIndexer();
    }

    @Override
    public Map<String, Analyzer> getAnalyzers() {
        return new HashMap<String, Analyzer>() {{
            put( RuleAttributeIndexTerm.TERM,
                 new RuleAttributeNameAnalyzer( LUCENE_40 ) );
            put( ProjectRootPathIndexTerm.TERM,
                 new FilenameAnalyzer( LUCENE_40 ) );
        }};
    }

    @Override
    protected TestDrlFileTypeDefinition getResourceTypeDefinition() {
        return new TestDrlFileTypeDefinition();
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }

    private void assertResponseContains( final List<RefactoringPageRow> rows,
                                         final String ruleName ) {
        for ( RefactoringPageRow row : rows ) {
            final String rowRuleName = ( (String) row.getValue() );
            if ( rowRuleName.equals( ruleName ) ) {
                return;
            }
        }
        fail( "Response does not contain expected Rule Name '" + ruleName + "'." );
    }

}
