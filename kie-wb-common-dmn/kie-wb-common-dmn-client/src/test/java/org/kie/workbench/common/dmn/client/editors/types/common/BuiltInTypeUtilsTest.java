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
 */

package org.kie.workbench.common.dmn.client.editors.types.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.api.property.dmn.types.BuiltInType.BOOLEAN;

public class BuiltInTypeUtilsTest {

    @Test
    public void testIsDefaultWhenTypeIsDefault() {
        assertTrue(BuiltInTypeUtils.isDefault("string"));
    }

    @Test
    public void testIsDefaultWhenTypeIsDefaultWithAlternativeAlias() {
        assertTrue(BuiltInTypeUtils.isDefault("dayTimeDuration"));
    }

    @Test
    public void testIsNotDefaultWhenTypeIsDefaultWithAnUpperCaseCharacter() {
        assertFalse(BuiltInTypeUtils.isDefault("String"));
    }

    @Test
    public void testIsDefaultWhenTypeIsNull() {
        assertFalse(BuiltInTypeUtils.isDefault(null));
    }

    @Test
    public void testIsDefaultWhenTypeIsNotDefault() {
        assertFalse(BuiltInTypeUtils.isDefault("tAddress"));
    }

    @Test
    public void testFindBuiltInTypeByNameWhenItFinds() {
        assertTrue(BuiltInTypeUtils.findBuiltInTypeByName("boolean").isPresent());
        assertEquals(BOOLEAN, BuiltInTypeUtils.findBuiltInTypeByName("boolean").get());
    }

    @Test
    public void testFindBuiltInTypeByNameWhenItDoesNotFind() {
        assertFalse(BuiltInTypeUtils.findBuiltInTypeByName("something").isPresent());
    }
}
