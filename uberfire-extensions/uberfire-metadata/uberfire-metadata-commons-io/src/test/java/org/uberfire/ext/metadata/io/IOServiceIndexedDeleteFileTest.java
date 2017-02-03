/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.ext.metadata.io;

import java.io.IOException;
import java.util.Collections;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopScoreDocCollector;
import org.junit.Test;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndex;
import org.uberfire.ext.metadata.engine.Index;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

import static org.junit.Assert.*;
import static org.uberfire.ext.metadata.io.KObjectUtil.toKCluster;

public class IOServiceIndexedDeleteFileTest extends BaseIndexTest {

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{this.getClass().getSimpleName()};
    }

    @Test
    public void testDeleteFile() throws IOException, InterruptedException {
        final Path path = getBasePath(this.getClass().getSimpleName()).resolve("delete-me.txt");
        ioService().write(path,
                          "content",
                          Collections.<OpenOption>emptySet(),
                          new FileAttribute<Object>() {
                              @Override
                              public String name() {
                                  return "delete";
                              }

                              @Override
                              public Object value() {
                                  return "me";
                              }
                          });

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        final Index index = config.getIndexManager().get(toKCluster(path.getFileSystem()));

        final IndexSearcher searcher = ((LuceneIndex) index).nrtSearcher();

        final TopScoreDocCollector collector = TopScoreDocCollector.create(10);

        //Check the file has been indexed
        searcher.search(new TermQuery(new Term("delete",
                                               "me")),
                        collector);

        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        listHitPaths(searcher,
                     hits);
        assertEquals(1,
                     hits.length);

        //Delete and re-check the index
        ioService().delete(path);

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        searcher.search(new TermQuery(new Term("delete",
                                               "me")),
                        collector);

        hits = collector.topDocs().scoreDocs;
        assertEquals(0,
                     hits.length);

        ((LuceneIndex) index).nrtRelease(searcher);
    }
}
