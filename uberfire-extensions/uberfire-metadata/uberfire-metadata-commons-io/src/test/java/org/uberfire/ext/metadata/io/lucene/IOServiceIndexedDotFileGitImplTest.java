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

package org.uberfire.ext.metadata.io.lucene;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.metadata.model.schema.MetaObject;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

import static org.junit.Assert.*;
import static org.uberfire.ext.metadata.io.KObjectUtil.toKCluster;

@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMScript(dir = "byteman", value = "lucene.btm")
public class IOServiceIndexedDotFileGitImplTest extends BaseIndexTest {

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{this.getClass().getSimpleName()};
    }

    @Test
    public void testIndexedDotFile() throws IOException, InterruptedException {
        setupCountDown(2);
        final Path path = getBasePath(this.getClass().getSimpleName()).resolve("dotFile.txt");
        //Write the "real path" with no attributes and hence no "dot file"
        ioService().write(path,
                          "ooooo!",
                          Collections.<OpenOption>emptySet());

        //Write an unmodified "real path" with attributes. This leads to only the "dot path" being indexed.
        ioService().write(path,
                          "ooooo!",
                          Collections.<OpenOption>emptySet(),
                          getFileAttributes());

        waitForCountDown(5000);

        final MetaObject mo = config.getMetaModelStore().getMetaObject(Path.class.getName());

        assertNotNull(mo);
        assertNotNull(mo.getProperty("name").get());
        assertEquals(1,
                     mo.getProperty("name").get().getTypes().size());
        assertTrue(mo.getProperty("name").get().getTypes().contains(String.class));

        final String index = toKCluster(path).getClusterId();

        TermQuery query = new TermQuery(new Term("name",
                                                 "value"));

        long hits = config.getIndexProvider().findHitsByQuery(Arrays.asList(index),
                                                              query);

        assertEquals(1,
                     hits);

        config.dispose();
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
