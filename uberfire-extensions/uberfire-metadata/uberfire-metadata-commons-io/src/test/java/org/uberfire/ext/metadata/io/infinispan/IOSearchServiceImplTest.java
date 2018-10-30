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
import java.util.List;

import org.jboss.byteman.contrib.bmunit.BMScript;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.metadata.io.IOSearchServiceImpl;
import org.uberfire.ext.metadata.search.IOSearchService;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;

@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMScript(dir = "byteman", value = "infinispan.btm")
public class IOSearchServiceImplTest extends BaseIndexTest {

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{getSimpleName()};
    }

    @Test
    public void testFullTextSearch() throws IOException, InterruptedException {
        setupCountDown(3);

        final IOSearchServiceImpl searchIndex = new IOSearchServiceImpl(config.getSearchIndex(),
                                                                        ioService());

        final Path path1 = getBasePath(getSimpleName()).resolve("g.txt");
        ioService().write(path1,
                          "ooooo!");

        final Path path2 = getBasePath(getSimpleName()).resolve("a.txt");
        ioService().write(path2,
                          "ooooo!");

        final Path path3 = getBasePath(getSimpleName()).resolve("the.txt");
        ioService().write(path3,
                          "ooooo!");

        final Path root = path1.getRoot();

        waitForCountDown(10000);

        {
            final List<Path> result = searchIndex.fullTextSearch("g",
                                                                 new IOSearchService.NoOpFilter(),
                                                                 root);

            assertEquals(1,
                         result.size());
        }

        {
            final List<Path> result = searchIndex.fullTextSearch("a",
                                                                 new IOSearchService.NoOpFilter(),
                                                                 root);

            assertEquals(1,
                         result.size());
        }

        {
            final List<Path> result = searchIndex.fullTextSearch("the",
                                                                 new IOSearchService.NoOpFilter(),
                                                                 root);

            assertEquals(1,
                         result.size());
        }

        {
            final List<Path> result = searchIndex.fullTextSearch("",
                                                                 new IOSearchService.NoOpFilter(),
                                                                 root);

            assertEquals(0,
                         result.size());
        }

        {
            try {
                searchIndex.fullTextSearch(null,
                                           new IOSearchService.NoOpFilter(),
                                           root);
                fail();
            } catch (final IllegalArgumentException ignored) {
            }
        }
    }
}
