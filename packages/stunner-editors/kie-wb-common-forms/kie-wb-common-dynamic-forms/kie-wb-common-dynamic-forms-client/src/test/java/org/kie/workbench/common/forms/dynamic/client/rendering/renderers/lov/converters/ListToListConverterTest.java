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


package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.converters;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ListToListConverterTest {

    private ListToListConverter converter = new ListToListConverter();

    @Test
    public void testConverter() {
        final List<String> values = new ArrayList();
        values.add("a");
        values.add("b");
        values.add("c");
        values.add("d");

        // Test convert to widget value
        Assertions.assertThat(converter.toWidgetValue(null))
                .isNotNull()
                .isEmpty();

        Assertions.assertThat(converter.toWidgetValue(new ArrayList()))
                .isNotNull()
                .isEmpty();

        Assertions.assertThat(converter.toWidgetValue(values))
                .isNotNull()
                .isNotEmpty()
                .isSameAs(values);

        // Test convert to model value
        Assertions.assertThat(converter.toModelValue(null))
                .isNull();

        Assertions.assertThat(converter.toModelValue(new ArrayList()))
                .isNotNull()
                .isEmpty();

        Assertions.assertThat(converter.toModelValue(values))
                .isNotNull()
                .isNotEmpty()
                .isSameAs(values);


    }
}
