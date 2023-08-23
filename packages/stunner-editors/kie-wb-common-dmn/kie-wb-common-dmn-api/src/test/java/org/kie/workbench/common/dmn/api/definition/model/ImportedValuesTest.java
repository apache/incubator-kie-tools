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

package org.kie.workbench.common.dmn.api.definition.model;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.property.dmn.ExpressionLanguage;
import org.kie.workbench.common.dmn.api.property.dmn.LocationURI;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

public class ImportedValuesTest {

    private static final String NAMESPACE = "NAMESPACE";
    private static final String LOCATION_URI = "LOCATION-URI";
    private static final String IMPORT_TYPE = "IMPORT-TYPE";
    private static final String IMPORTED_ELEMENT = "IMPORTED-ELEMENT";
    private static final String EXPRESSION_LANGUAGE = "EXPRESSION-LANGUAGE";

    @Test
    public void testCopy() {
        final ImportedValues source = new ImportedValues(
                NAMESPACE,
                new LocationURI(LOCATION_URI),
                IMPORT_TYPE,
                IMPORTED_ELEMENT,
                new ExpressionLanguage(EXPRESSION_LANGUAGE)
        );

        final ImportedValues target = source.copy();

        assertNotNull(target);
        assertNotEquals(source.getId().getValue(), target.getId().getValue());
        assertEquals(NAMESPACE, target.getNamespace());
        assertEquals(LOCATION_URI, target.getLocationURI().getValue());
        assertEquals(IMPORT_TYPE, target.getImportType());
        assertEquals(IMPORTED_ELEMENT, target.getImportedElement());
        assertEquals(EXPRESSION_LANGUAGE, target.getExpressionLanguage().getValue());
    }

    @Test
    public void testExactCopy() {
        final ImportedValues source = new ImportedValues(
                NAMESPACE,
                new LocationURI(LOCATION_URI),
                IMPORT_TYPE,
                IMPORTED_ELEMENT,
                new ExpressionLanguage(EXPRESSION_LANGUAGE)
        );

        final ImportedValues target = source.exactCopy();

        assertNotNull(target);
        assertEquals(source.getId().getValue(), target.getId().getValue());
        assertEquals(NAMESPACE, target.getNamespace());
        assertEquals(LOCATION_URI, target.getLocationURI().getValue());
        assertEquals(IMPORT_TYPE, target.getImportType());
        assertEquals(IMPORTED_ELEMENT, target.getImportedElement());
        assertEquals(EXPRESSION_LANGUAGE, target.getExpressionLanguage().getValue());
    }
}
