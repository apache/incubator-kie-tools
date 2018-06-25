/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.globals.backend.server.indexing;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.lucene.search.Query;
import org.drools.workbench.screens.globals.type.GlobalResourceTypeDefinition;
import org.guvnor.common.services.project.categories.Decision;
import org.junit.Test;
import org.kie.workbench.common.services.refactoring.backend.server.BaseIndexingTest;
import org.kie.workbench.common.services.refactoring.backend.server.TestIndexer;
import org.kie.workbench.common.services.refactoring.backend.server.query.builder.SingleTermQueryBuilder;
import org.kie.workbench.common.services.refactoring.model.index.terms.valueterms.ValueReferenceIndexTerm;
import org.kie.workbench.common.services.refactoring.service.ResourceType;
import org.uberfire.ext.metadata.io.KObjectUtil;
import org.uberfire.java.nio.file.Path;

public class IndexGlobalsTest extends BaseIndexingTest<GlobalResourceTypeDefinition> {

    @Test
    public void testIndexGlobals() throws IOException, InterruptedException {
        //Add test files
        final Path path1 = basePath.resolve( "global1.gdrl" );
        final String drl1 = loadText( "global1.gdrl" );
        ioService().write( path1,
                           drl1 );
        final Path path2 = basePath.resolve( "global2.gdrl" );
        final String drl2 = loadText( "global2.gdrl" );
        ioService().write( path2,
                           drl2 );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        List<String> index = Arrays.asList(KObjectUtil.toKCluster(basePath.getFileSystem()).getClusterId());

        {
            final Query query = new SingleTermQueryBuilder( new ValueReferenceIndexTerm( "java.util.ArrayList", ResourceType.JAVA ) )
                    .build();
            searchFor(index, query, 2, path1, path2);
        }
    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestGlobalsFileIndexer();
    }

    @Override
    protected GlobalResourceTypeDefinition getResourceTypeDefinition() {
        return new GlobalResourceTypeDefinition(new Decision());
    }

    @Override
    protected String getRepositoryName() {
        return this.getClass().getSimpleName();
    }

}
