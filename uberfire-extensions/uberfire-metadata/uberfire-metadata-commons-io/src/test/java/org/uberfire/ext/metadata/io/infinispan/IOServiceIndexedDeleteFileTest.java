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
import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.metadata.provider.IndexProvider;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

import static org.junit.Assert.*;
import static org.uberfire.ext.metadata.backend.infinispan.utils.AttributesUtil.toProtobufFormat;
import static org.uberfire.ext.metadata.io.KObjectUtil.toKCluster;

@RunWith(BMUnitRunner.class)
@BMUnitConfig(debug = true)
@BMScript(dir = "byteman", value = "infinispan.btm")
public class IOServiceIndexedDeleteFileTest extends BaseIndexTest {

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{getSimpleName()};
    }

    @Test
    public void testDeleteFile() {
        setupCountDown(1);
        final Path path = getBasePath(getSimpleName()).resolve("delete-me.txt");
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

        waitForCountDown(1000);

        List<String> indices = Arrays.asList(toProtobufFormat(toKCluster(path).getClusterId()));
        IndexProvider provider = this.config.getIndexProvider();

        //Check the file has been indexed
        long hits = provider.findHitsByQuery(indices,
                                             new TermQuery(new Term("delete",
                                                                    "me")));

        assertEquals(1,
                     hits);

        setupCountDown(2);

        //Delete and re-check the index
        ioService().delete(path);

        waitForCountDown(1000);

        hits = provider.findHitsByQuery(indices,
                                        new TermQuery(new Term("delete",
                                                               "me")));

        assertEquals(0,
                     hits);
    }
}
