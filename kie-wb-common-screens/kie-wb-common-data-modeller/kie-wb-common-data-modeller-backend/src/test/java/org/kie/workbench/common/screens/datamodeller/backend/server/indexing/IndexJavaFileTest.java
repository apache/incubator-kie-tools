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
package org.kie.workbench.common.screens.datamodeller.backend.server.indexing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.junit.Test;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.JavaTypeIndexTerm;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.valueterms.ValueJavaTypeIndexTerm;
import org.kie.workbench.common.screens.javaeditor.type.JavaResourceTypeDefinition;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.RuleAttributeNameAnalyzer;
import org.kie.workbench.common.services.refactoring.backend.server.query.QueryBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleAttributeIndexTerm;
import org.uberfire.java.nio.file.Path;
import org.uberfire.commons.data.Pair;
import org.uberfire.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.metadata.engine.Index;

import static org.apache.lucene.util.Version.*;
import static org.junit.Assert.assertEquals;

public class IndexJavaFileTest extends BaseIndexingTest<JavaResourceTypeDefinition> {

    @Test
    public void testIndexJavaFiles() throws IOException, InterruptedException {
        //Don't ask, but we need to write a single file first in order for indexing to work
        final Path basePath = getDirectoryPath().resolveSibling( "someNewOtherPath" );
        ioService().write( basePath.resolve( "dummy" ),
                "<none>" );

        //Add test files
        final Path path = basePath.resolve( "Pojo1.java" );
        final String drl = loadText( "Pojo1.java" );
        ioService().write( path,
                drl );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        final Index index = getConfig().getIndexManager().get( org.uberfire.metadata.io.KObjectUtil.toKCluster( basePath.getFileSystem() ) );

        {
            final IndexSearcher searcher = ( ( LuceneIndex ) index ).nrtSearcher();
            final TopScoreDocCollector collector = TopScoreDocCollector.create( 10,
                    true );
            final Query query = new QueryBuilder().addTerm( new ValueJavaTypeIndexTerm( JavaTypeIndexTerm.JAVA_TYPE.CLASS ) ).build();

            searcher.search( query,
                    collector );
            final ScoreDoc[] hits = collector.topDocs().scoreDocs;

            assertEquals( 1,
                    hits.length );

            List<Pair<String, String>> expectedValues = initExpectedValues();

            assertContains( expectedValues, searcher.doc( hits[ 0 ].doc ) );

            ( ( LuceneIndex ) index ).nrtRelease( searcher );
        }
    }

    private void assertContains( List<Pair<String, String>> expectedValues, Document doc ) {

        List<Pair<String, String>> returnedValues = new ArrayList<Pair<String, String>>();
        for ( IndexableField field : doc.getFields() ) {
            returnedValues.add( new Pair<String, String>( field.name(), field.stringValue() ) );
        }

        //assertEquals( expectedValues.size(), returnedValues.size() );
        for ( Pair<String, String> expectedValue : expectedValues ) {
            int index = returnedValues.indexOf( expectedValue );
            if ( index < 0 ) {
                assertEquals( "Expected value is not present in Document fields.", expectedValue, null );
            } else {
                returnedValues.remove( index );
            }
        }
    }

    private List<Pair<String, String>> initFieldExpectedValues( String fieldName, String className ) {
        List<Pair<String, String>> expectedValues = new ArrayList<Pair<String, String>>();
        expectedValues.add( new Pair<String, String>( "field_name", fieldName ) );
        expectedValues.add( new Pair<String, String>( "field_type:" + fieldName, className ) );
        return expectedValues;
    }

    private List<Pair<String, String>> initExpectedValues() {

        List<Pair<String, String>> expectedValues = new ArrayList<Pair<String, String>>();

        expectedValues.add( new Pair<String, String>( "java_type", "class" ) );
        expectedValues.add( new Pair<String, String>( "java_type_name", "org.kie.workbench.common.screens.datamodeller.backend.server.indexing.Pojo1" ) );
        expectedValues.add( new Pair<String, String>( "java_type_parent", "java.util.Date" ) );
        expectedValues.add( new Pair<String, String>( "java_type_interface", "java.io.Serializable" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "java.io.Serializable" ) );

        expectedValues.addAll( initFieldExpectedValues( "o_BigDecimal", "java.math.BigDecimal" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "java.math.BigDecimal" ) );

        expectedValues.addAll( initFieldExpectedValues( "o_BigInteger", "java.math.BigInteger" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "java.math.BigInteger" ) );

        expectedValues.addAll( initFieldExpectedValues( "o_Boolean", "java.lang.Boolean" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "java.lang.Boolean" ) );

        expectedValues.addAll( initFieldExpectedValues( "o_Byte", "java.lang.Byte" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "java.lang.Byte" ) );

        expectedValues.addAll( initFieldExpectedValues( "o_Character", "java.lang.Character" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "java.lang.Character" ) );

        expectedValues.addAll( initFieldExpectedValues( "o_Date", "java.util.Date" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "java.util.Date" ) );

        expectedValues.addAll( initFieldExpectedValues( "o_Double", "java.lang.Double" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "java.lang.Double" ) );

        expectedValues.addAll( initFieldExpectedValues( "o_Float", "java.lang.Float" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "java.lang.Float" ) );

        expectedValues.addAll( initFieldExpectedValues( "o_Integer", "java.lang.Integer" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "java.lang.Integer" ) );

        expectedValues.addAll( initFieldExpectedValues( "o_Long", "java.lang.Long" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "java.lang.Long" ) );

        expectedValues.addAll( initFieldExpectedValues( "o_Short", "java.lang.Short" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "java.lang.Short" ) );

        expectedValues.addAll( initFieldExpectedValues( "o_String", "java.lang.String" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "java.lang.String" ) );

        expectedValues.addAll( initFieldExpectedValues( "p_boolean", "boolean" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "boolean" ) );

        expectedValues.addAll( initFieldExpectedValues( "p_byte", "byte" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "byte" ) );

        expectedValues.addAll( initFieldExpectedValues( "p_char", "char" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "char" ) );

        expectedValues.addAll( initFieldExpectedValues( "p_double", "double" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "double" ) );

        expectedValues.addAll( initFieldExpectedValues( "p_float", "float" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "float" ) );

        expectedValues.addAll( initFieldExpectedValues( "p_int", "int" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "int" ) );

        expectedValues.addAll( initFieldExpectedValues( "p_long", "long" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "long" ) );

        expectedValues.addAll( initFieldExpectedValues( "p_short", "short" ) );
        expectedValues.add( new Pair<String, String>( "type_name", "short" ) );

        return expectedValues;

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
}