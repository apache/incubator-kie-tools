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

package org.kie.workbench.common.stunner.bpmn.forms.model;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ImportsFieldDefinitionTest {

    @Test
    public void getFieldType() {
        ImportsFieldDefinition tested = new ImportsFieldDefinition();
        assertEquals(ImportsFieldDefinition.FIELD_TYPE, tested.getFieldType());
        assertEquals(ImportsFieldType.NAME, new ImportsFieldDefinition().getFieldType().getTypeName());
    }

    @Test
    public void doCopyFrom() {
        ImportsFieldDefinition tested = new ImportsFieldDefinition();
        tested.doCopyFrom(null);
        Assert.assertNotNull(tested);
    }
}