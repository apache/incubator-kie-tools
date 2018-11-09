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

/***
 * Mapper of the dependencies associated to each DependencyType
 */
public class DependencyTypesMapper {

    private final static String JSON_POM_DEPS = "DependencyTypesMapper.json";
    private Map<DependencyType, List<DynamicPomDependency>> mapping;

    public DependencyTypesMapper() {
        PomJsonReaderDefault jsonDepsReader = new PomJsonReaderDefault(getClass().getClassLoader().getResourceAsStream(JSON_POM_DEPS));
        mapping = jsonDepsReader.readDeps();
    }

    public Map<DependencyType, List<DynamicPomDependency>> getMapping() {
        return mapping;
    }

    public List<DynamicPomDependency> getDependencies(DependencyType key) {
        return mapping.get(key);
    }
}
