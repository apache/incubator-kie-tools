/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.common.services.project.utils;

public interface ModuleResourcePaths {

    public static final String SOURCE_FILENAME = "src";

    public static final String POM_PATH = "pom.xml";

    public static final String MAIN_SRC_PATH = "src/main/java";
    public static final String TEST_SRC_PATH = "src/test/java";
    public static final String MAIN_RESOURCES_PATH = "src/main/resources";
    public static final String TEST_RESOURCES_PATH = "src/test/resources";

    public static final String[] SOURCE_PATHS = {
            MAIN_SRC_PATH,
            MAIN_RESOURCES_PATH,
            TEST_SRC_PATH,
            TEST_RESOURCES_PATH
    };
}
