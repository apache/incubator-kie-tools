package org.kie.workbench.common.services.refactoring.backend.server.query;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.inject.Instance;

import org.apache.lucene.analysis.Analyzer;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.drl.TestDrlFileIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.drl.TestDrlFileTypeDefinition;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.RuleAttributeNameAnalyzer;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindRulesQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.IndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleAttributeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleIndexTerm;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;

import static org.apache.lucene.util.Version.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RefactoringQueryServiceImplGeneralTest extends BaseIndexingTest<TestDrlFileTypeDefinition> {

    private Instance<NamedQuery> namedQueriesProducer;

    private Set<NamedQuery> queries = new HashSet<NamedQuery>() {{
        add( new FindRulesQuery() {
            @Override
            public ResponseBuilder getResponseBuilder() {
                return new DefaultResponseBuilder( ioService() );
            }
        } );
    }};

    @Before
    public void setupNamedQueriesProducer() {
        namedQueriesProducer = mock( Instance.class );
        when( namedQueriesProducer.iterator() ).thenReturn( queries.iterator() );
    }

    @Test
    public void testGetNamedQueries() throws IOException, InterruptedException {
        final RefactoringQueryService service = new RefactoringQueryServiceImpl( getConfig(),
                                                                                 namedQueriesProducer );

        final Set<String> queryNames = service.getQueries();
        assertNotNull( queryNames );
        assertEquals( 1,
                      queryNames.size() );
        assertTrue( queryNames.contains( "FindRulesQuery" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetNonExistentNamedQueryTerms() throws IOException, InterruptedException {
        final RefactoringQueryService service = new RefactoringQueryServiceImpl( getConfig(),
                                                                                 namedQueriesProducer );
        final Set<String> queryNames = service.getQueries();
        assertNotNull( queryNames );
        assertEquals( 1,
                      queryNames.size() );
        assertTrue( queryNames.contains( "FindRulesQuery" ) );

        final Set<IndexTerm> terms = service.getTerms( "NonExistentNamedQuery" );
    }

    @Test
    public void testGetNamedQueryTerms() throws IOException, InterruptedException {
        final RefactoringQueryService service = new RefactoringQueryServiceImpl( getConfig(),
                                                                                 namedQueriesProducer );
        final Set<String> queryNames = service.getQueries();
        assertNotNull( queryNames );
        assertEquals( 1,
                      queryNames.size() );
        assertTrue( queryNames.contains( "FindRulesQuery" ) );

        final Set<IndexTerm> terms = service.getTerms( "FindRulesQuery" );
        assertNotNull( terms );
        assertEquals( 1,
                      terms.size() );
        assertTrue( terms.iterator().next() instanceof RuleIndexTerm );
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

}
