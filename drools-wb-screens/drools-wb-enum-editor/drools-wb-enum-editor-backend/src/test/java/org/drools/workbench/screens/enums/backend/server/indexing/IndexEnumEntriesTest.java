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

package org.drools.workbench.screens.enums.backend.server.indexing;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;
import org.drools.workbench.screens.enums.type.EnumResourceTypeDefinition;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.RuleAttributeNameAnalyzer;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.BasicQueryBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleAttributeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueFieldIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueTypeIndexTerm;
import org.uberfire.ext.metadata.engine.Index;
import org.uberfire.java.nio.file.Path;

public class IndexEnumEntriesTest extends BaseIndexingTest<EnumResourceTypeDefinition> {

    @Test
    public void testIndexEnumEntries() throws IOException, InterruptedException {
        //Add test files
        final Path path1 = basePath.resolve( "enum1.enumeration" );
        final String dsl1 = loadText( "enum1.enumeration" );
        ioService().write( path1,
                           dsl1 );
        final Path path2 = basePath.resolve( "enum2.enumeration" );
        final String dsl2 = loadText( "enum2.enumeration" );
        ioService().write( path2,
                           dsl2 );
        final Path path3 = basePath.resolve( "enum3.enumeration" );
        final String dsl3 = loadText( "enum3.enumeration" );
        ioService().write( path3,
                           dsl3 );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        final Index index = getConfig().getIndexManager().get( org.uberfire.ext.metadata.io.KObjectUtil.toKCluster( basePath.getFileSystem() ) );

        //Enumerations using org.drools.workbench.screens.enums.backend.server.indexing.classes.Applicant
        {
            final Query query = new BasicQueryBuilder()
                    .addTerm( new ValueTypeIndexTerm( "org.drools.workbench.screens.enums.backend.server.indexing.classes.Applicant" ) )
                    .build();
            searchFor(index, query, 2, path1, path2);
        }

        //Enumerations using org.drools.workbench.screens.enums.backend.server.indexing.classes.Mortgage
        {
            final Query query = new BasicQueryBuilder()
                    .addTerm( new ValueTypeIndexTerm( "org.drools.workbench.screens.enums.backend.server.indexing.classes.Mortgage" ) )
                    .build();
            searchFor(index, query, 1, path2);
        }

        //Enumerations using org.drools.workbench.screens.enums.backend.server.indexing.classes.Mortgage#amount
        {
            final Query query = new BasicQueryBuilder()
                    .addTerm( new ValueTypeIndexTerm( "org.drools.workbench.screens.enums.backend.server.indexing.classes.Mortgage" ) )
                    .addTerm( new ValueFieldIndexTerm( "amount" ) )
                    .build();
            searchFor(index, query, 1, path2);
        }

        //Enumerations using java.lang.Integer
        {
            final Query query = new BasicQueryBuilder()
                    .addTerm( new ValueTypeIndexTerm( "java.lang.Integer" ) )
                    .build();
            searchFor(index, query, 2, path1, path2);
        }

    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestEnumFileIndexer();
    }

    @Override
    public Map<String, Analyzer> getAnalyzers() {
        return new HashMap<String, Analyzer>() {{
            put( RuleAttributeIndexTerm.TERM,
                 new RuleAttributeNameAnalyzer() );
        }};
    }

    @Override
    protected EnumResourceTypeDefinition getResourceTypeDefinition() {
        return new EnumResourceTypeDefinition();
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }

}
