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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.model.FunctionDefinition;

import static org.assertj.core.api.Assertions.assertThat;

public class KindUtilitiesTest {

    private FunctionDefinition function;

    @Before
    public void setup() {
        this.function = new FunctionDefinition();
    }

    @Test
    public void testGetKindNoneSet() {
        assertThat(KindUtilities.getKind(function)).isEqualTo(FunctionDefinition.Kind.FEEL); // DMN v1.2 default is FEEL.
    }

    @Test
    public void testGetKindWhenSet() {
        function.setKind(FunctionDefinition.Kind.FEEL);
        assertThat(KindUtilities.getKind(function)).isEqualTo(FunctionDefinition.Kind.FEEL);
    }

    @Test
    public void testSetKindFEEL() {
        assertSetKind(FunctionDefinition.Kind.FEEL);
    }

    @Test
    public void testSetKindJAVA() {
        assertSetKind(FunctionDefinition.Kind.JAVA);
    }

    @Test
    public void testSetKindPMML() {
        assertSetKind(FunctionDefinition.Kind.PMML);
    }

    @Test
    public void testSetKindNullWithNSSet() {
        KindUtilities.setKind(function, null);

        assertThat(function.getKind()).isNull();
    }

    @Test
    public void testSetKindNullWithNSNotSet() {
        KindUtilities.setKind(function, null);

        assertThat(function.getKind()).isNull();
    }

    private void assertSetKind(final FunctionDefinition.Kind kind) {
        KindUtilities.setKind(function,
                              kind);

        assertThat(function.getKind().code()).isEqualTo(kind.code());
    }
}
