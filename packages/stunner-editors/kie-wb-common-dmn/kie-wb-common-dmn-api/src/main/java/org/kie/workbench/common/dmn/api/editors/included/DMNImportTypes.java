/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */
package org.kie.workbench.common.dmn.api.editors.included;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DMNImportTypes {

    private final String fileExtension;

    private final List<String> namespaces;

    private static final List<DMNImportTypes> importTypes = new ArrayList<>();

    private DMNImportTypes(final String fileExtension,
                           final String... namespaces) {
        this.fileExtension = fileExtension;
        this.namespaces = Arrays.asList(namespaces);
    }

    private static DMNImportTypes registerImportType(final String fileExtension,
                                                     final String... namespaces) {
        final DMNImportTypes importType = new DMNImportTypes(fileExtension, namespaces);
        importTypes.add(importType);

        return importType;
    }

    public static DMNImportTypes determineImportType(final String namespace) {
        return importTypes
                .stream()
                .filter(importType -> importType.matchesNamespace(namespace))
                .findFirst()
                .orElse(null);
    }

    private boolean matchesNamespace(final String namespace) {
        return namespaces.stream().anyMatch(ns -> Objects.equals(ns, namespace));
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getDefaultNamespace() {
        return namespaces.get(namespaces.size() - 1);
    }

    public List<String> getNamespaces() {
        return namespaces;
    }

    /**
     * DMN
     */
    public static final DMNImportTypes DMN = registerImportType("dmn",
                                                                "http://www.omg.org/spec/DMN/20180521/MODEL/");

    /**
     * PMML
     */
    public static final DMNImportTypes PMML = registerImportType("pmml",
                                                                 "http://www.dmg.org/PMML-3_0",
                                                                 "http://www.dmg.org/PMML-3_1",
                                                                 "http://www.dmg.org/PMML-3_2",
                                                                 "http://www.dmg.org/PMML-4_0",
                                                                 "http://www.dmg.org/PMML-4_1",
                                                                 "http://www.dmg.org/PMML-4_2",
                                                                 "http://www.dmg.org/PMML-4_3");
}
