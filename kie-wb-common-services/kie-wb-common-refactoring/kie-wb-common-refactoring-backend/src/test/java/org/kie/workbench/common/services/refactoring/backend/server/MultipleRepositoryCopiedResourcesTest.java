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

package org.kie.workbench.common.services.refactoring.backend.server;

import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;
import org.kie.workbench.common.services.shared.project.KieProjectService;

public class MultipleRepositoryCopiedResourcesTest extends MultipleRepositoryBaseIndexingTest<TestPropertiesFileTypeDefinition> {

    @Test
//    @Ignore("Copying between repositories is broken.")
    public void testIndexingCopiedResources() throws IOException, InterruptedException {
        //Add test files
        loadProperties( "file1.properties",
                        getBasePath( this.getClass().getSimpleName() + "_1" ) );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        searchFor( new TermQuery( new Term( "title", "lucene" ) ), 1);

        //Copy one of the files returned by the previous search
        ioService().copy( getBasePath( this.getClass().getSimpleName() + "_1" ).resolve( "file1.properties" ),
                          getBasePath( this.getClass().getSimpleName() + "_2" ).resolve( "file1.properties" ) );

        Thread.sleep( 5000 ); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        searchFor( new TermQuery( new Term( "title", "lucene" ) ), 2);
    }

    @Override
    protected TestIndexer getIndexer() {
        return new TestPropertiesFileIndexer();
    }

    @Override
    public Map<String, Analyzer> getAnalyzers() {
        return Collections.EMPTY_MAP;
    }

    @Override
    protected TestPropertiesFileTypeDefinition getResourceTypeDefinition() {
        return new TestPropertiesFileTypeDefinition();
    }

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{ this.getClass().getSimpleName() + "_1", this.getClass().getSimpleName() + "_2" };
    }

    @Override
    protected KieProjectService getProjectService() {
        return mock( KieProjectService.class );
    }

}
