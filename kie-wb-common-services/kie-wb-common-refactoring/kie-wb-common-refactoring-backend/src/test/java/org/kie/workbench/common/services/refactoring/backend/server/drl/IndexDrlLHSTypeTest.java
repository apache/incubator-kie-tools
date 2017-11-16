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

package org.kie.workbench.common.services.refactoring.backend.server.drl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.Query;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.SingleTermQueryBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueIndexTerm.TermSearchType;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.ext.metadata.io.KObjectUtil;
import org.uberfire.java.nio.file.Path;

public class IndexDrlLHSTypeTest extends BaseIndexingTest<TestDrlFileTypeDefinition> {

    @Test
    public void testIndexDrlLHSType() throws IOException, InterruptedException {
        ioService().startBatch(ioService().getFileSystem(basePath.toUri()));
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
        ioService().endBatch();

        Thread.sleep( 12000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        List<String> index = Arrays.asList(KObjectUtil.toKCluster(basePath.getFileSystem()).getClusterId());

        //Check type extraction (with wildcards)
        {
            final Query query = new SingleTermQueryBuilder( new ValueReferenceIndexTerm( "*.Applicant", ResourceType.JAVA, TermSearchType.WILDCARD ) ).build();
            searchFor(index, query, 3, path1, path2, path3 );
        }

        //Check type extraction (without wildcards)
        {
            final Query query = new SingleTermQueryBuilder( new ValueReferenceIndexTerm( "org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Applicant", ResourceType.JAVA ) ).build();
            searchFor(index, query, 3, path1, path2, path3 );
        }

        //Check type extraction (with wildcards)
        {
            final Query query = new SingleTermQueryBuilder( new ValueReferenceIndexTerm( "*.Mortgage", ResourceType.JAVA, TermSearchType.WILDCARD ) ).build();
            searchFor(index, query, 2, path2, path3 );
        }

        //Check type extraction (without wildcards)
        {
            final Query query = new SingleTermQueryBuilder( new ValueReferenceIndexTerm( "org.kie.workbench.common.services.refactoring.backend.server.drl.classes.Mortgage", ResourceType.JAVA, TermSearchType.WILDCARD ) ).build();
            searchFor(index, query, 2, path2, path3 );
        }

    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestDrlFileIndexer();
    }

    @Override
    public Map<String, Analyzer> getAnalyzers() {
        return Collections.<String, Analyzer>emptyMap();
    }

    @Override
    protected TestDrlFileTypeDefinition getResourceTypeDefinition() {
        return new TestDrlFileTypeDefinition();
    }

    @Override
    protected String getRepositoryName() {
        return testName.getMethodName();
    }

}
