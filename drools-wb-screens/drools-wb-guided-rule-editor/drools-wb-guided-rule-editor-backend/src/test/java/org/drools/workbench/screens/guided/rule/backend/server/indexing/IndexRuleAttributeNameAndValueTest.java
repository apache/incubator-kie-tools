/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.rule.backend.server.indexing;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.drools.workbench.screens.guided.rule.type.GuidedRuleDRLResourceTypeDefinition;
import org.guvnor.common.services.project.categories.Decision;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueSharedPartIndexTerm;
import org.kie.workbench.common.services.refactoring.service.PartType;
import org.uberfire.ext.metadata.io.KObjectUtil;
import org.uberfire.java.nio.file.Path;

public class IndexRuleAttributeNameAndValueTest extends BaseIndexingTest<GuidedRuleDRLResourceTypeDefinition> {

    @Test
    public void testIndexDrlRuleAttributeNameAndValues() throws IOException, InterruptedException {
        //Add test files
        final Path path = basePath.resolve( "drl1.rdrl" );
        final String drl = loadText( "drl1.rdrl" );
        ioService().write( path,
                           drl );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        List<String> index = Arrays.asList(KObjectUtil.toKCluster(basePath).getClusterId());

        {
            final BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
            ValueSharedPartIndexTerm indexTerm = new ValueSharedPartIndexTerm("*", PartType.RULEFLOW_GROUP);
            queryBuilder.add( new WildcardQuery( new Term( indexTerm.getTerm(), indexTerm.getValue() ) ),
                       BooleanClause.Occur.MUST );
            queryBuilder.add( new WildcardQuery( new Term( "shared:nonexistend", "*" ) ),
                       BooleanClause.Occur.MUST );
            searchFor(index, queryBuilder.build(), 0);

        }

        {
            // This could also just be a TermQuery..
            final BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
            ValueSharedPartIndexTerm indexTerm = new ValueSharedPartIndexTerm("myruleflowgroup", PartType.RULEFLOW_GROUP);
            queryBuilder.add( new TermQuery( new Term( indexTerm.getTerm(), indexTerm.getValue() ) ),
                       BooleanClause.Occur.MUST );
            searchFor(index, queryBuilder.build(), 1);
        }

    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestGuidedRuleDrlFileIndexer();
    }

    @Override
    protected GuidedRuleDRLResourceTypeDefinition getResourceTypeDefinition() {
        return new GuidedRuleDRLResourceTypeDefinition(new Decision());
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }

}
