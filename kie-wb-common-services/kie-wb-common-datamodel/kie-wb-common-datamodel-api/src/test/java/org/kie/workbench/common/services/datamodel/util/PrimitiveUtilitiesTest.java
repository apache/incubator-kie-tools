/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.datamodel.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PrimitiveUtilitiesTest {

    @Test
    public void testGetClassNameForPrimitiveType() {
        assertThat(PrimitiveUtilities.getClassNameForPrimitiveType(PrimitiveUtilities.BYTE)).isEqualTo(Byte.class.getName());
        assertThat(PrimitiveUtilities.getClassNameForPrimitiveType(PrimitiveUtilities.SHORT)).isEqualTo(Short.class.getName());
        assertThat(PrimitiveUtilities.getClassNameForPrimitiveType(PrimitiveUtilities.INT)).isEqualTo(Integer.class.getName());
        assertThat(PrimitiveUtilities.getClassNameForPrimitiveType(PrimitiveUtilities.LONG)).isEqualTo(Long.class.getName());
        assertThat(PrimitiveUtilities.getClassNameForPrimitiveType(PrimitiveUtilities.FLOAT)).isEqualTo(Float.class.getName());
        assertThat(PrimitiveUtilities.getClassNameForPrimitiveType(PrimitiveUtilities.DOUBLE)).isEqualTo(Double.class.getName());
        assertThat(PrimitiveUtilities.getClassNameForPrimitiveType(PrimitiveUtilities.CHAR)).isEqualTo(Character.class.getName());
        assertThat(PrimitiveUtilities.getClassNameForPrimitiveType(PrimitiveUtilities.BOOLEAN)).isEqualTo(Boolean.class.getName());

        assertThat(PrimitiveUtilities.getClassNameForPrimitiveType("")).isNull();
        assertThat(PrimitiveUtilities.getClassNameForPrimitiveType(null)).isNull();
        assertThat(PrimitiveUtilities.getClassNameForPrimitiveType("Unknown")).isNull();
    }
}
