/*
 * Copyright 2014 JBoss, by Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.screens.datamodeller.backend.server.indexing.query;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.enterprise.inject.Instance;

import org.apache.lucene.analysis.Analyzer;
import org.junit.Test;
import org.kie.workbench.common.screens.datamodeller.backend.server.indexing.TestJavaFileIndexer;
import org.kie.workbench.common.screens.datamodeller.backend.server.query.standard.FindJavaTypeQuery;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.JavaTypeIndexTerm;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.valueterms.ValueJavaTypeIndexTerm;
import org.kie.workbench.common.screens.javaeditor.type.JavaResourceTypeDefinition;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.RuleAttributeNameAnalyzer;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQueries;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.RefactoringQueryServiceImpl;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleAttributeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.RefactoringQueryService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.paging.PageResponse;

import static org.apache.lucene.util.Version.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JavaTypeQueryValidIndexTermsTest extends BaseIndexingTest<JavaResourceTypeDefinition> {

    private Set<NamedQuery> queries = new HashSet<NamedQuery>() {{
        add( new FindJavaTypeQuery() {
            @Override
            public ResponseBuilder getResponseBuilder() {
                return new DefaultResponseBuilder( ioService() );
            }
        } );
    }};

    @Test
    public void testFindJavaTypeQueryValidIndexTerms() throws IOException, InterruptedException {
        final Instance<NamedQuery> namedQueriesProducer = mock( Instance.class );
        when( namedQueriesProducer.iterator() ).thenReturn( queries.iterator() );

        final RefactoringQueryService service = new RefactoringQueryServiceImpl( getConfig(),
                                                                                 new NamedQueries( namedQueriesProducer ) );

        //Add test files
        final Path path1 = basePath.resolve( "Pojo1.java" );
        final String javaFile1 = loadText( "../Pojo1.java" );

        /*
        final Path path2 = basePath.resolve( "Pojo2.java" );
        final String javaFile2 = loadText( "../Pojo2.java" );
        */

        ioService().write( path1,
                           javaFile1 );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        {

            HashSet<ValueIndexTerm> queryTerms = new HashSet<ValueIndexTerm>();
            queryTerms.add( new ValueJavaTypeIndexTerm( JavaTypeIndexTerm.JAVA_TYPE.CLASS ) );

            final RefactoringPageRequest request = new RefactoringPageRequest( "FindJavaTypeQuery",
                                                                               queryTerms,
                                                                               0,
                                                                               10 );

            try {
                final PageResponse<RefactoringPageRow> response = service.query( request );
                assertNotNull( response );
                assertEquals( 1,
                              response.getPageRowList().size() );
                assertResponseContains( response.getPageRowList(),
                                        path1 );

            } catch ( IllegalArgumentException e ) {
                fail();
            }
        }

    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestJavaFileIndexer();
    }

    @Override
    public Map<String, Analyzer> getAnalyzers() {
        return new HashMap<String, Analyzer>() {{
            put( RuleAttributeIndexTerm.TERM,
                 new RuleAttributeNameAnalyzer( LUCENE_40 ) );
        }};
    }

    @Override
    protected JavaResourceTypeDefinition getResourceTypeDefinition() {
        return new JavaResourceTypeDefinition();
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }

    private void assertResponseContains( final List<RefactoringPageRow> rows,
                                         final Path path ) {
        for ( RefactoringPageRow row : rows ) {
            final String rowFileName = ( (org.uberfire.backend.vfs.Path) row.getValue() ).getFileName();
            final String fileName = path.getFileName().toString();
            if ( rowFileName.endsWith( fileName ) ) {
                return;
            }
        }
        fail( "Response does not contain expected Path '" + path.toUri().toString() );
    }

}
