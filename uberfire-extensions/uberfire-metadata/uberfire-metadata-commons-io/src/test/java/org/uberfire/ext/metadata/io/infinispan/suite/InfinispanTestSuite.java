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

package org.uberfire.ext.metadata.io.infinispan.suite;

import org.arquillian.cube.docker.junit.rule.ContainerDslRule;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.uberfire.ext.metadata.io.infinispan.BatchIndexConcurrencyTest;
import org.uberfire.ext.metadata.io.infinispan.BatchIndexSingleThreadTest;
import org.uberfire.ext.metadata.io.infinispan.BatchIndexTest;
import org.uberfire.ext.metadata.io.infinispan.ComplexFieldsTest;
import org.uberfire.ext.metadata.io.infinispan.FullTextSearchIndexTest;
import org.uberfire.ext.metadata.io.infinispan.IOSearchServiceImplTest;
import org.uberfire.ext.metadata.io.infinispan.IOServiceIndexedDeleteFileTest;
import org.uberfire.ext.metadata.io.infinispan.IOServiceIndexedDotFileGitImplTest;
import org.uberfire.ext.metadata.io.infinispan.IOServiceIndexedDotFileGitInternalImplTest;
import org.uberfire.ext.metadata.io.infinispan.IOServiceIndexedGitImplTest;
import org.uberfire.ext.metadata.io.infinispan.IOServiceIndexedSortingTest;
import org.uberfire.ext.metadata.io.infinispan.IndexTest;
import org.uberfire.ext.metadata.io.infinispan.ReplaceIndexedObjectTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        IndexTest.class,
        BatchIndexConcurrencyTest.class,
        BatchIndexSingleThreadTest.class,
        BatchIndexTest.class,
        ComplexFieldsTest.class,
        FullTextSearchIndexTest.class,
        IOSearchServiceImplTest.class,
        IOServiceIndexedDeleteFileTest.class,
        IOServiceIndexedDotFileGitImplTest.class,
        IOServiceIndexedDotFileGitInternalImplTest.class,
        IOServiceIndexedGitImplTest.class,
        IOServiceIndexedSortingTest.class,
        ReplaceIndexedObjectTest.class
})
public class InfinispanTestSuite {

    private static InfinispanTestProperties props = InfinispanTestProperties.getInstance();

    private static String imageName = props.getImage()
            + ":"
            + props.getVersion();

//    @ClassRule
//    public static ContainerDslRule infinispan = new ContainerDslRule(imageName)
//            .withEnvironment("APP_USER",
//                             props.getUser())
//            .withEnvironment("APP_PASS",
//                             props.getPassword())
//            .withPortBinding(8080,
//                             11222);
}
