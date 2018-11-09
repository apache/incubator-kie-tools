/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.structure.backend.pom;

import java.util.List;
import java.util.Map;

import org.guvnor.structure.pom.DependencyType;
import org.guvnor.structure.pom.DynamicPomDependency;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class PomJsonReaderTest {

    private PomJsonReaderDefault reader;
    private final static String JSON_POM_DEPS = "DependencyTypesMapper.json";

    @Before
    public void setUp() {
        reader = new PomJsonReaderDefault("target/test-classes/",
                                          JSON_POM_DEPS);
    }

    @Test
    public void readDepsTest() {
        Map<DependencyType, List<DynamicPomDependency>> mapping = reader.readDeps();
        assertThat(mapping).isNotEmpty();
        TestUtil.testJPADep(mapping);
    }
}
