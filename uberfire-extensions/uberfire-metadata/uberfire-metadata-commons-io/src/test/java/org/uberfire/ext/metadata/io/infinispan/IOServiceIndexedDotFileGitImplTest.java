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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.metadata.model.schema.MetaObject;
import org.uberfire.ext.metadata.provider.IndexProvider;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

import static org.junit.Assert.*;
import static org.uberfire.ext.metadata.backend.infinispan.utils.AttributesUtil.toProtobufFormat;
import static org.uberfire.ext.metadata.io.KObjectUtil.toKCluster;

@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMScript(dir = "byteman", value = "infinispan.btm")
public class IOServiceIndexedDotFileGitImplTest extends BaseIndexTest {

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{getSimpleName()};
    }

    @Test
    public void testIndexedDotFile() throws IOException, InterruptedException {
        setupCountDown(1);
        final Path path = getBasePath(getSimpleName()).resolve("dotFile.txt");
        //Write the "real path" with no attributes and hence no "dot file"
        ioService().write(path,
                          "ooooo!",
                          Collections.<OpenOption>emptySet());

        waitForCountDown(1000);
        setupCountDown(1);
        //Write an unmodified "real path" with attributes. This leads to only the "dot path" being indexed.
        ioService().write(path,
                          "ooooo!",
                          Collections.<OpenOption>emptySet(),
                          getFileAttributes());

        waitForCountDown(1000);

        final MetaObject mo = config.getMetaModelStore().getMetaObject(Path.class.getName());

        assertNotNull(mo);
        assertNotNull(mo.getProperty("name").get());
        assertEquals(1,
                     mo.getProperty("name").get().getTypes().size());
        assertTrue(mo.getProperty("name").get().getTypes().contains(String.class));

        List<String> indices = Arrays.asList(toProtobufFormat(toKCluster(path).getClusterId()));
        IndexProvider provider = this.config.getIndexProvider();

        //Check the file has been indexed
        long hits = provider.findHitsByQuery(indices,
                                             new TermQuery(new Term("name",
                                                                    "value")));

        assertEquals(1,
                     hits);
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
