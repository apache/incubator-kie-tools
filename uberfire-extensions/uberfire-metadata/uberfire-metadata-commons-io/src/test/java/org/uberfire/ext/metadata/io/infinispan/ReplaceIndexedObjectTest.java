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

import org.apache.lucene.search.MatchAllDocsQuery;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

import static org.junit.Assert.*;
import static org.uberfire.ext.metadata.backend.infinispan.utils.AttributesUtil.toProtobufFormat;

@RunWith(org.jboss.byteman.contrib.bmunit.BMUnitRunner.class)
@BMScript(dir = "byteman", value = "infinispan.btm")
public class ReplaceIndexedObjectTest extends BaseIndexTest {

    @Override
    protected String[] getRepositoryNames() {
        return new String[]{getSimpleName()};
    }

    @Test
    public void testIndexSameFileTwice() {

        final Path file = ioService().get("git://" + getSimpleName() + "/path/to/file.txt");
        {
            setupCountDown(1);
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

            waitForCountDown(1000);
            setupCountDown(2);
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
                                      return "default://master@file/kies";
                                  }
                              });
            waitForCountDown(1000);
        }

        List<KObject> result = config.getIndexProvider().findByQuery(Arrays.asList(toProtobufFormat(getSimpleName()+"/master")),
                                                                     new MatchAllDocsQuery(),
                                                                     null,
                                                                     0);

        assertEquals(1,
                     result.size());
    }
}
