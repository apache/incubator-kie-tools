/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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
 *
 */

package org.uberfire.ext.metadata.io.infinispan;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.byteman.contrib.bmunit.BMScript;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.search.ClusterSegment;
import org.uberfire.ext.metadata.search.IOSearchService;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.base.SegmentedPath;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;

@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMScript(dir = "byteman", value = "infinispan.btm")
public class IndexTest extends BaseIndexTest {

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{getSimpleName() + "_1", getSimpleName() + "_2"};
    }

    @Test
    public void testClusterSegments() throws IOException, InterruptedException {
        setupCountDown(2);
        //Add test files
        final Path path1 = getBasePath(getSimpleName() + "_1").resolve("indexedFile1.txt");
        ioService().write(path1,
                          "content1");
        final Path path2 = getBasePath(getSimpleName() + "_2").resolve("indexedFile2.txt");
        ioService().write(path2,
                          "content2");

        //Setup ClusterSegments
        final ClusterSegment cs1 = new ClusterSegment() {
            @Override
            public String getClusterId() {
                return (((FileSystemId) getBasePath(getSimpleName() + "_1").getFileSystem()).id() + "/master");
            }

            @Override
            public String[] segmentIds() {
                return new String[]{((SegmentedPath) getBasePath(getSimpleName() + "_1")).getSegmentId()};
            }
        };
        final ClusterSegment cs2 = new ClusterSegment() {
            @Override
            public String getClusterId() {
                return ((FileSystemId) getBasePath(getSimpleName() + "_2").getFileSystem()).id() + "/master";
            }

            @Override
            public String[] segmentIds() {
                return new String[]{((SegmentedPath) getBasePath(getSimpleName() + "_2")).getSegmentId()};
            }
        };

        waitForCountDown(10000);

        final Map<String, Object> attributes = new HashMap<String, Object>() {{
            put("filename",
                "*txt");
        }};

        //Attribute Search
        {
            final int hits = config.getSearchIndex().searchByAttrsHits(attributes);
            final List<KObject> results = config.getSearchIndex().searchByAttrs(attributes,
                                                                                new IOSearchService.NoOpFilter());
            assertEquals(0,
                         hits);
            assertEquals(0,
                         results.size());
        }

        {
            final int hits = config.getSearchIndex().searchByAttrsHits(attributes,
                                                                       cs1);
            final List<KObject> results = config.getSearchIndex().searchByAttrs(attributes,
                                                                                new IOSearchService.NoOpFilter(),
                                                                                cs1);
            assertEquals(1,
                         hits);
            assertEquals(1,
                         results.size());
        }

        {
            final int hits = config.getSearchIndex().searchByAttrsHits(attributes,
                                                                       cs2);
            final List<KObject> results = config.getSearchIndex().searchByAttrs(attributes,
                                                                                new IOSearchService.NoOpFilter(),
                                                                                cs2);
            assertEquals(1,
                         hits);
            assertEquals(1,
                         results.size());
        }

        {
            final int hits = config.getSearchIndex().searchByAttrsHits(attributes,
                                                                       cs1,
                                                                       cs2);
            final List<KObject> results = config.getSearchIndex().searchByAttrs(attributes,
                                                                                new IOSearchService.NoOpFilter(),
                                                                                cs1,
                                                                                cs2);
            assertEquals(2,
                         hits);
            assertEquals(2,
                         results.size());
        }

        //Full Text Search
        {
            final int hits = config.getSearchIndex().fullTextSearchHits("*indexed*");
            final List<KObject> results = config.getSearchIndex().fullTextSearch("*indexed*",
                                                                                 new IOSearchService.NoOpFilter());
            assertEquals(0,
                         hits);
            assertEquals(0,
                         results.size());
        }

        {
            final int hits = config.getSearchIndex().fullTextSearchHits("*indexed*",
                                                                        cs1);
            final List<KObject> results = config.getSearchIndex().fullTextSearch("*indexed*",
                                                                                 new IOSearchService.NoOpFilter(),
                                                                                 cs1);
            assertEquals(1,
                         hits);
            assertEquals(1,
                         results.size());
        }

        {
            final int hits = config.getSearchIndex().fullTextSearchHits("*indexed*",
                                                                        cs2);
            final List<KObject> results = config.getSearchIndex().fullTextSearch("*indexed*",
                                                                                 new IOSearchService.NoOpFilter(),
                                                                                 cs2);
            assertEquals(1,
                         hits);
            assertEquals(1,
                         results.size());
        }

        {
            final int hits = config.getSearchIndex().fullTextSearchHits("*indexed*",
                                                                        cs1,
                                                                        cs2);
            final List<KObject> results = config.getSearchIndex().fullTextSearch("*indexed*",
                                                                                 new IOSearchService.NoOpFilter(),
                                                                                 cs1,
                                                                                 cs2);
            assertEquals(2,
                         hits);
            assertEquals(2,
                         results.size());
        }
    }

    protected String getSimpleName() {
        return "indexable_" + IndexTest.this.getClass().getSimpleName();
    }
}
