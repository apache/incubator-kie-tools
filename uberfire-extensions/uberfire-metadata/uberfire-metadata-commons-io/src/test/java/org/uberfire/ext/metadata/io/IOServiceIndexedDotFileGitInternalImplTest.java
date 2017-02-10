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
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

import static org.junit.Assert.*;
import static org.uberfire.ext.metadata.backend.lucene.util.KObjectUtil.toKObject;
import static org.uberfire.ext.metadata.io.KObjectUtil.toKCluster;

public class IOServiceIndexedDotFileGitInternalImplTest extends BaseIndexTest {

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{this.getClass().getSimpleName()};
    }

    @Test
    public void testIndexedGitInternalDotFile() throws IOException, InterruptedException {
        final Path path1 = getBasePath(this.getClass().getSimpleName()).resolve(".gitkeep");
        ioService().write(path1,
                          "ooooo!",
                          Collections.<OpenOption>emptySet(),
                          getFileAttributes());

        final Path path2 = getBasePath(this.getClass().getSimpleName()).resolve("afile");
        ioService().write(path2,
                          "ooooo!",
                          Collections.<OpenOption>emptySet(),
                          getFileAttributes());

        Thread.sleep(5000); //wait for events to be consumed from jgit -> (notify changes -> watcher -> index) -> lucene index

        final Index index = config.getIndexManager().get(toKCluster(path1.getFileSystem()));

        final IndexSearcher searcher = ((LuceneIndex) index).nrtSearcher();

        final TopScoreDocCollector collector = TopScoreDocCollector.create(10);

        searcher.search(new TermQuery(new Term("name",
                                               "value")),
                        collector);

        final ScoreDoc[] hits = collector.topDocs().scoreDocs;
        listHitPaths(searcher,
                     hits);

        assertEquals(1,
                     hits.length);

        final KObject ko = toKObject(searcher.doc(hits[0].doc));
        assertEquals(ko.getKey(),
                     path2.toUri().toString());

        ((LuceneIndex) index).nrtRelease(searcher);
    }

    private FileAttribute<?>[] getFileAttributes() {
        return new FileAttribute<?>[]{
                new FileAttribute<String>() {
                    @Override
                    public String name() {
                        return "name";
                    }

                    @Override
                    public String value() {
                        return "value";
                    }
                }};
    }
}
