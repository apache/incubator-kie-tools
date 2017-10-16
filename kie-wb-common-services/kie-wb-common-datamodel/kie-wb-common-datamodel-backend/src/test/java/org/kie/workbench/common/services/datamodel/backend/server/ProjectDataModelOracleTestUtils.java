/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodel.backend.server;

import java.util.Set;

import org.kie.soup.project.datamodel.oracle.ModelField;

import static org.junit.Assert.*;

/**
 * Utility methods for DataModelOracle tests
 */
public class ProjectDataModelOracleTestUtils {

    public static void assertContains(final String string,
                                      final String[] strings) {
        for (int i = 0; i < strings.length; i++) {
            if (string.equals(strings[i])) {
                return;
            }
        }
        fail("String[] did not contain: " + string);
    }

    public static void assertContains(final String string,
                                      final Set<String> strings) {
        if (!strings.contains(string)) {
            fail("Set<String> did not contain: " + string);
        }
    }

    public static void assertContains(final String fieldName,
                                      final ModelField[] fieldDefinitions) {
        for (int i = 0; i < fieldDefinitions.length; i++) {
            if (fieldName.equals(fieldDefinitions[i].getName())) {
                return;
            }
        }
        fail("ModelField[] did not contain field: " + fieldName);
    }
}
