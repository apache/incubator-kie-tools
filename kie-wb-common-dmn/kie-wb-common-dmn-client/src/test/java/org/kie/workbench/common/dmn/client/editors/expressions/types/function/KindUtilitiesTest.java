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

package org.kie.workbench.common.dmn.client.editors.expressions.types.function;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase.Namespace;
import org.kie.workbench.common.dmn.api.definition.v1_1.FunctionDefinition;

import static org.assertj.core.api.Assertions.assertThat;

public class KindUtilitiesTest {

    private FunctionDefinition function;

    @Before
    public void setup() {
        this.function = new FunctionDefinition();
    }

    @Test
    public void testGetKindNoneSet() {
        assertThat(KindUtilities.getKind(function)).isNull();
    }

    @Test
    public void testGetKindWhenSet() {
        function.getAdditionalAttributes().put(FunctionDefinition.KIND_QNAME,
                                               FunctionDefinition.Kind.FEEL.code());
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

    private void assertSetKind(final FunctionDefinition.Kind kind) {
        KindUtilities.setKind(function,
                              kind);

        assertThat(function.getNsContext().get(FunctionDefinition.DROOLS_PREFIX)).isEqualTo(Namespace.KIE.getUri());
        assertThat(function.getAdditionalAttributes().get(FunctionDefinition.KIND_QNAME)).isEqualTo(kind.code());
    }
}
