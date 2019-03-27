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
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.WildcardQuery;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.commons.async.DescriptiveThreadFactory;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;
import org.uberfire.ext.metadata.io.IOServiceIndexedImpl;
import org.uberfire.ext.metadata.io.MetadataConfigBuilder;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.io.IOService;
import org.uberfire.io.attribute.DublinCoreView;
import org.uberfire.java.nio.base.version.VersionAttributeView;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

import static org.junit.Assert.*;

@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMScript(dir = "byteman", value = "infinispan.btm")
public class ComplexFieldsTest extends BaseIndexTest {

    @Override
    protected IOService ioService() {

        if (ioService == null) {
            config = new MetadataConfigBuilder("infinispan")
                    .withInMemoryMetaModelStore()
                    .useDirectoryBasedIndex()
                    .useInMemoryDirectory()
                    .usingAnalyzers(new HashMap<String, Analyzer>() {{
                        put("file",
                            new FilenameAnalyzer());
                    }})
                    .build();

            ioService = new IOServiceIndexedImpl(config.getIndexEngine(),
                                                 Executors.newCachedThreadPool(new DescriptiveThreadFactory()),
                                                 indexersFactory(),
                                                 indexerDispatcherFactory(config.getIndexEngine()),
                                                 DublinCoreView.class,
                                                 VersionAttributeView.class);
        }
        return ioService;
    }

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{"elastic_complex_fields_test"};
    }

    @Test
    public void testIndex() throws IOException, InterruptedException {
        setupCountDown(1);
        {
            final Path file = ioService().get("git://elastic_complex_fields_test/path/to/file.txt");
            ioService().write(file,
                              "some content here",
                              Collections.<OpenOption>emptySet(),
                              new FileAttribute<Object>() {
                                  @Override
                                  public String name() {
                                      return "file";
                                  }

                                  @Override
                                  public Object value() {
                                      return "default://master@file/kie";
                                  }
                              });
        }
        waitForCountDown(5000);
        setupCountDown(1);
        {
            final Path file = ioService().get("git://elastic_complex_fields_test/path/to/files.txt");
            ioService().write(file,
                              "some content here",
                              Collections.<OpenOption>emptySet(),
                              new FileAttribute<Object>() {
                                  @Override
                                  public String name() {
                                      return "file";
                                  }

                                  @Override
                                  public Object value() {
                                      return "default://master@files/kie";
                                  }
                              });
        }
        waitForCountDown(5000);

        {
            List<KObject> result = config.getIndexProvider().findByQuery(Arrays.asList("elastic_complex_fields_test__master"),
                                                                         new WildcardQuery(new Term("file",
                                                                                                    "default://master@files/kie")),
                                                                         null,
                                                                         0);

            assertEquals(1,
                         result.size());
        }

        {
            List<KObject> result = config.getIndexProvider().findByQuery(Arrays.asList("elastic_complex_fields_test__master"),
                                                                         new WildcardQuery(new Term("file",
                                                                                                    "default://master@file/kie")),
                                                                         null,
                                                                         0);

            assertEquals(1,
                         result.size());
        }

        {
            List<KObject> result = config.getIndexProvider().findByQuery(Arrays.asList("elastic_complex_fields_test__master"),
                                                                         new WildcardQuery(new Term("file",
                                                                                                    "default://master@notFound")),
                                                                         null,
                                                                         0);

            assertEquals(0,
                         result.size());
        }
    }
}
