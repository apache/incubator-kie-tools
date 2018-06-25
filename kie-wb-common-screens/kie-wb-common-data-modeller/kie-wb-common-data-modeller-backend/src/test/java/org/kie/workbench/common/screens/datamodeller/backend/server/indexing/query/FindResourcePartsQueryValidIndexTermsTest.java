/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;
import org.guvnor.common.services.project.categories.Model;
import org.junit.Test;
import org.kie.workbench.common.screens.datamodeller.backend.server.indexing.TestJavaFileIndexer;
import org.kie.workbench.common.screens.javaeditor.type.JavaResourceTypeDefinition;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.NamedQuery;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.SingleTermQueryBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.DefaultResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.response.ResponseBuilder;
import org.kie.workbench.common.services.refactoring.backend.server.query.standard.FindResourcePartsQuery;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValuePartIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueResourceIndexTerm;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRequest;
import org.kie.workbench.common.services.refactoring.model.query.RefactoringPageRow;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.commons.data.Pair;
import org.uberfire.ext.metadata.io.KObjectUtil;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.java.nio.file.Path;
import org.uberfire.paging.PageResponse;

import static org.junit.Assert.*;

public class FindResourcePartsQueryValidIndexTermsTest extends BaseIndexingTest<JavaResourceTypeDefinition> {

    protected Set<NamedQuery> getQueries() {
        return new HashSet<NamedQuery>() {{
            add( new FindResourcePartsQuery() {
                @Override
                public ResponseBuilder getResponseBuilder() {
                    return new DefaultResponseBuilder( ioService() );
                }
            } );
        }};
    }


    @Test
    public void testIndexJavaFilesAndFindResourcePartsQuery() throws Exception {
        // setup
        ioService();

        //Add test files
        String pojo1FileName = "Pojo1.java";
        Path path = basePath.resolve( pojo1FileName );
        String javaSourceText = loadText( "../" + pojo1FileName );
        ioService().write( path, javaSourceText );

        // wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index
        Thread.sleep(5000);

        List<String> index = Arrays.asList(KObjectUtil.toKCluster(basePath.getFileSystem()).getClusterId());

        {
            final Query query = new SingleTermQueryBuilder( new ValueResourceIndexTerm( "*", ResourceType.JAVA, TermSearchType.WILDCARD ) ).build();

            List<KObject> hits = getConfig().getIndexProvider().findByQuery(index,
                                                                                query,
                                                                                10);

            assertEquals( 1, hits.size() );

            List<Pair<String, String>> expectedValues = initExpectedValues();

            KObject doc = null;
            for( KObject kObject : hits ) {
                doc = kObject;
               for( KProperty<?> property : doc.getProperties() ) {
                   String fieldVal = property.getValue().toString();
                  if( fieldVal.startsWith("git://" ) ) {
                      if( fieldVal.contains(pojo1FileName) ) {
                          break;
                      }
                  } else {
                      continue;
                  }
               }
            }

            assertContains( expectedValues, doc );

        }

        {

            HashSet<ValueIndexTerm> queryTerms = new HashSet<ValueIndexTerm>();
            queryTerms.add( new ValuePartIndexTerm(
                    "o_BigDecimal",
                    PartType.FIELD ) );

            final RefactoringPageRequest request = new RefactoringPageRequest( FindResourcePartsQuery.NAME,
                                                                               queryTerms,
                                                                               0,
                                                                               10 );

            try {
                final PageResponse<RefactoringPageRow> response = service.query( request );
                assertNotNull( "No documents found!", response  );
                assertEquals( 1,
                              response.getPageRowList().size() );

            } catch ( IllegalArgumentException e ) {
                e.printStackTrace();
                fail( "Could not execute query: " + e.getMessage());
            }
        };
    }

    private void assertContains( List<Pair<String, String>> expectedValues,
                                 KObject doc ) {

        List<Pair<String, String>> returnedValues = new ArrayList<Pair<String, String>>();
        for ( KProperty<?> field : doc.getProperties() ) {
            returnedValues.add( new Pair<>( field.getName(), field.getValue().toString() ) );
        }

        //assertEquals( expectedValues.size(), returnedValues.size() );
        for ( Pair<String, String> expectedValue : expectedValues ) {
            int index = returnedValues.indexOf( expectedValue );
            if ( index < 0 ) {
                fail( "Expected value is not in Document fields: [" + expectedValue.getK1() + " => " + expectedValue.getK2() + "]" );
            } else {
                returnedValues.remove( index );
            }
        }
    }

    private List<Pair<String, String>> initExpectedValues() {

        List<Pair<String, String>> expectedValues = new ArrayList<Pair<String, String>>();

        expectedValues.add( new Pair<String, String>( ResourceType.JAVA.toString(), "org.kie.workbench.common.screens.datamodeller.backend.server.indexing.Pojo1" ) );

        // identifying info
        String [] fieldNames = new String [] {
                "o_BigDecimal",
                "o_BigInteger",
                "o_Boolean",
                "o_Byte",
                "o_Character",
                "o_Date",
                "o_Double",
                "o_Float",
                "o_Integer",
                "o_Long",
                "o_Short",
                "o_String",
                "p_boolean",
                "p_byte",
                "p_char",
                "p_double",
                "p_float",
                "p_int",
                "p_long",
                "p_short"
        };
        for( String className : fieldNames ) {
           ValuePartIndexTerm partTerm = new ValuePartIndexTerm(className, PartType.FIELD);
           expectedValues.add( new Pair<String, String>( partTerm.getTerm(), partTerm.getValue() ) );
        }

        // references
        String [] referencedClasses = new String [] {
              "java.util.Date",
              "java.io.Serializable",
              "java.math.BigDecimal",
              "java.lang.Boolean",
              "java.lang.Byte",
              "java.lang.Character",
              "java.lang.Double",
              "java.lang.Float",
              "java.lang.Integer",
              "java.lang.Long",
              "boolean",
              "byte",
              "char",
              "double",
              "float",
              "long",
              "short"
        };

        for( String className : referencedClasses ) {
           ValueReferenceIndexTerm refTerm = new ValueReferenceIndexTerm(className, ResourceType.JAVA);
           expectedValues.add( new Pair<String, String>( refTerm.getTerm(), refTerm.getValue() ) );
        }

        return expectedValues;

    }

    @Override
    protected TestIndexer getIndexer() {
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
