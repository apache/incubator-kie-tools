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
import org.kie.uberfire.metadata.backend.lucene.analyzer.FilenameAnalyzer;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.drl.TestDrlFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.drl.TestDrlFileTypeDefinition;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.RuleAttributeNameAnalyzer;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.RefactoringQueryServiceImpl;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindRulesByProjectQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.ProjectRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleAttributeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueProjectRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.uberfire.java.nio.file.Path;
import org.uberfire.paging.PageResponse;

import static org.apache.lucene.util.Version.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FindRulesByProjectQueryValidIndexTermsTest extends BaseIndexingTest<TestDrlFileTypeDefinition> {

    private Set<NamedQuery> queries = new HashSet<NamedQuery>() {{
        add( new FindRulesByProjectQuery() );
    }};

    @Test
    public void testQueryValidIndexTerms() throws IOException, InterruptedException {
        final Instance<NamedQuery> namedQueriesProducer = mock( Instance.class );
        when( namedQueriesProducer.iterator() ).thenReturn( queries.iterator() );

        final RefactoringQueryServiceImpl service = new RefactoringQueryServiceImpl( getConfig(),
                                                                                     namedQueriesProducer );
        service.init();

        //Add test files
        final Path path1 = basePath.resolve( "drl1.drl" );
        final String drl1 = loadText( "drl1.drl" );
        ioService().write( path1,
                           drl1 );
        final Path path2 = basePath.resolve( "drl2.drl" );
        final String drl2 = loadText( "drl2.drl" );
        ioService().write( path2,
                           drl2 );
        final Path path3 = basePath.resolve( "drl3.drl" );
        final String drl3 = loadText( "drl3.drl" );
        ioService().write( path3,
                           drl3 );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

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
                assertEquals( 3,
                              response.getPageRowList().size() );
                assertResponseContains( response.getPageRowList(),
                                        "myRule" );
                assertResponseContains( response.getPageRowList(),
                                        "myRule2" );
                assertResponseContains( response.getPageRowList(),
                                        "myRule3" );

            } catch ( IllegalArgumentException e ) {
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
                assertEquals( 3,
                              response.getPageRowList().size() );
                assertResponseContains( response.getPageRowList(),
                                        "myRule" );
                assertResponseContains( response.getPageRowList(),
                                        "myRule2" );
                assertResponseContains( response.getPageRowList(),
                                        "myRule3" );

            } catch ( IllegalArgumentException e ) {
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
