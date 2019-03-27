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
import java.util.stream.StreamSupport;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.WildcardQuery;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.metadata.backend.lucene.fields.FieldFactory;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.ext.metadata.model.KProperty;
import org.uberfire.ext.metadata.provider.IndexProvider;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;
import static org.uberfire.ext.metadata.backend.infinispan.utils.AttributesUtil.toProtobufFormat;
import static org.uberfire.ext.metadata.io.KObjectUtil.toKCluster;

@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMScript(dir = "byteman", value = "infinispan.btm")
public class IOServiceIndexedSortingTest extends BaseIndexTest {

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{getSimpleName()};
    }

    @Test
    public void testSortedFiles() {

        //Write files in reverse order so natural Lucene order would be c, b, a
        final Path base = writeFile("cFile1.txt");
        writeFile("CFile2.txt");
        writeFile("bFile.txt");
        writeFile("aFile.txt");

        List<String> indices = Arrays.asList(toProtobufFormat(toKCluster(base).getClusterId()));
        IndexProvider provider = this.config.getIndexProvider();

        {
            final Sort sort = new Sort(new SortField(FieldFactory.FILE_NAME_FIELD_SORTED,
                                                     SortField.Type.STRING));
            final Query query = new WildcardQuery(new Term("filename",
                                                           "*txt"));

            List<KObject> documents = provider.findByQuery(indices,
                                                           query,
                                                           sort,
                                                           10);

            assertEquals(4,
                         documents.size());
            assertEquals("aFile.txt",
                         getProperty(documents.get(0),
                                     "filename").getValue());
            assertEquals("bFile.txt",
                         getProperty(documents.get(1),
                                     "filename").getValue());
            assertEquals("cFile1.txt",
                         getProperty(documents.get(2),
                                     "filename").getValue());
            assertEquals("CFile2.txt",
                         getProperty(documents.get(3),
                                     "filename").getValue());
        }
    }

    private KProperty<?> getProperty(KObject document,
                                     String name) {
        return StreamSupport
                .stream(document.getProperties().spliterator(),
                        false)
                .filter(kProperty -> kProperty.getName().equals(name))
                .findAny().get();
    }

    private Path writeFile(final String fileName) {
        setupCountDown(1);
        final Path path = getBasePath(getSimpleName()).resolve(fileName);
        ioService().write(path,
                          "content",
                          Collections.<OpenOption>emptySet());
        waitForCountDown(1000);
        return path;
    }
}
