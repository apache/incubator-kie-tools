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

package org.kie.workbench.common.dmn.api.property.dmn;

import java.util.HashSet;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class QNameTest {

    private static final String VALUE = "value";

    private final static QName qName1 = new QName(VALUE);

    private final static QName qName2 = new QName(VALUE);

    @Test
    public void checkEquals() {
        assertEquals(qName1, qName2);

        assertNotEquals(qName1, FunctionDefinition.KIND_QNAME);
    }

    @Test
    public void checkHashCode() {
        assertEquals(qName1.hashCode(), qName2.hashCode());

        final HashSet<QName> qNames = new HashSet<>();

        qNames.add(qName1);

        assertTrue(qNames.contains(qName2));
    }
}
