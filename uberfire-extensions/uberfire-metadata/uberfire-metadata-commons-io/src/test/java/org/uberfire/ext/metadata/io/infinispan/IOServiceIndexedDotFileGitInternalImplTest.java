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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

import static org.junit.Assert.*;
import static org.uberfire.ext.metadata.backend.infinispan.utils.AttributesUtil.toProtobufFormat;
import static org.uberfire.ext.metadata.io.KObjectUtil.toKCluster;

@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMScript(dir = "byteman", value = "infinispan.btm")
public class IOServiceIndexedDotFileGitInternalImplTest extends BaseIndexTest {

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{this.getClass().getSimpleName()};
    }

    @Test
    public void testIndexedGitInternalDotFile() {
        setupCountDown(1);
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

        waitForCountDown(5000);

        final String index = toProtobufFormat(toKCluster(path1).getClusterId());

        TermQuery query = new TermQuery(new Term("name",
                                                 "value"));

        List<KObject> hits = config.getIndexProvider().findByQuery(Arrays.asList(index),
                                                                   query,
                                                                   10);

        assertEquals(1,
                     hits.size());

        assertEquals(hits.get(0).getKey(),
                     path2.toUri().toString());
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
