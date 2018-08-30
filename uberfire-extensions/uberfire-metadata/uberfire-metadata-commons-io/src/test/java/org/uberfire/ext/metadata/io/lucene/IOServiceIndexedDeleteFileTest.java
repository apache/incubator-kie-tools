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
import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

import static org.junit.Assert.*;
import static org.uberfire.ext.metadata.io.KObjectUtil.toKCluster;

@RunWith(BMUnitRunner.class)
@BMUnitConfig(debug = true)
@BMScript(dir = "byteman", value = "lucene.btm")
public class IOServiceIndexedDeleteFileTest extends BaseIndexTest {

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{this.getClass().getSimpleName()};
    }

    @Test
    public void testDeleteFile() throws IOException, InterruptedException {
        setupCountDown(1);
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

        waitForCountDown(5000);

        String index = toKCluster(path).getClusterId();

        //Check the file has been indexed
        TermQuery query = new TermQuery(new Term("delete",
                                                 "me"));

        long hits = config.getIndexProvider().findHitsByQuery(Arrays.asList(index),
                                                              query);

        assertEquals(1,
                     hits);

        setupCountDown(2);

        //Delete and re-check the index
        ioService().delete(path);

        waitForCountDown(5000);

        hits = config.getIndexProvider().findHitsByQuery(Arrays.asList(index),
                                                         query);

        assertEquals(0,
                     hits);
    }
}
