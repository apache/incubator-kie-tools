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

package org.kie.workbench.common.dmn.api.property.dmn.types;

import java.util.Arrays;

import org.junit.Test;
import org.kie.workbench.common.dmn.api.property.dmn.QName;

import static org.junit.Assert.assertEquals;

public class BuiltInTypeTest {

    @Test
    public void testAsQName() {
        Arrays.asList(BuiltInType.values()).stream().forEach(this::assertBuiltInTypeAsQName);
    }

    private void assertBuiltInTypeAsQName(final BuiltInType bit) {
        final QName typeRef = bit.asQName();
        assertEquals(bit.getName(),
                     typeRef.getLocalPart());
        assertEquals(QName.NULL_NS_URI,
                     typeRef.getNamespaceURI());
    }
}

