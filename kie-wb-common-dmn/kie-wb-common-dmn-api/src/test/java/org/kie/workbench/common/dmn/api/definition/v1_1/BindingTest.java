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

package org.kie.workbench.common.dmn.api.definition.v1_1;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.HasVariable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BindingTest {

    private Binding binding;

    @Before
    public void setup() {
        this.binding = new Binding();
    }

    @Test
    public void testImplementsHasVariable() {
        assertTrue(binding instanceof HasVariable);
    }

    @Test
    public void testHasVariableProxyGetter() {
        assertEquals(binding.getParameter(),
                     binding.getVariable());
    }

    @Test
    public void testHasVariableProxySetter() {
        final InformationItem variable = new InformationItem();
        binding.setVariable(variable);

        assertEquals(variable,
                     binding.getParameter());
    }

    @Test
    public void testParameterSetter() {
        final InformationItem variable = new InformationItem();
        binding.setParameter(variable);

        assertEquals(variable,
                     binding.getVariable());
    }
}
