/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.util.js;

import java.util.Map;

import org.junit.Test;
import org.kie.soup.commons.util.Maps;

import static org.junit.Assert.assertEquals;

public class JsConverterTest {

    @Test
    public void fromMap() {
        final Map<?, ?> map = new Maps.Builder<String, String>()
                .put("name", "tiago")
                .put("age", "34")
                .build();

        final KeyValue[] keyValues = JsConverter.fromMap(map);
        assertEquals(keyValues.length, 2);
        assertEquals(keyValues[0].getKey(), "name");
        assertEquals(keyValues[0].getValue(), "tiago");
        assertEquals(keyValues[1].getKey(), "age");
        assertEquals(keyValues[1].getValue(), "34");
    }
}