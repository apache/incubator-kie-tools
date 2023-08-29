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

package org.kie.workbench.common.dmn.client.editors.expressions.types.invocation;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.model.Binding;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;
import org.kie.workbench.common.dmn.api.definition.model.Invocation;

import static org.assertj.core.api.Assertions.assertThat;

public class InvocationDefaultValueUtilitiesTest {

    private Invocation invocation;

    @Before
    public void setup() {
        this.invocation = new Invocation();
    }

    @Test
    public void testGetNewParameterName() {
        final Binding binding1 = new Binding() {{
            setParameter(new InformationItem());
        }};
        invocation.getBinding().add(binding1);
        binding1.getParameter().getName().setValue(InvocationDefaultValueUtilities.getNewParameterName(invocation));
        assertThat(binding1.getParameter().getName().getValue()).isEqualTo(InvocationDefaultValueUtilities.PREFIX + "1");

        final Binding binding2 = new Binding() {{
            setParameter(new InformationItem());
        }};
        invocation.getBinding().add(binding2);
        binding2.getParameter().getName().setValue(InvocationDefaultValueUtilities.getNewParameterName(invocation));
        assertThat(binding2.getParameter().getName().getValue()).isEqualTo(InvocationDefaultValueUtilities.PREFIX + "2");
    }

    @Test
    public void testGetNewParameterNameWithExistingParameters() {
        final Binding binding1 = new Binding() {{
            setParameter(new InformationItem());
        }};
        invocation.getBinding().add(binding1);
        binding1.getParameter().getName().setValue("binding");

        final Binding binding2 = new Binding() {{
            setParameter(new InformationItem());
        }};
        invocation.getBinding().add(binding2);
        binding2.getParameter().getName().setValue(InvocationDefaultValueUtilities.getNewParameterName(invocation));
        assertThat(binding2.getParameter().getName().getValue()).isEqualTo(InvocationDefaultValueUtilities.PREFIX + "1");
    }

    @Test
    public void testGetNewParameterNameWithDeletion() {
        final Binding binding1 = new Binding() {{
            setParameter(new InformationItem());
        }};
        invocation.getBinding().add(binding1);
        binding1.getParameter().getName().setValue(InvocationDefaultValueUtilities.getNewParameterName(invocation));
        assertThat(binding1.getParameter().getName().getValue()).isEqualTo(InvocationDefaultValueUtilities.PREFIX + "1");

        final Binding binding2 = new Binding() {{
            setParameter(new InformationItem());
        }};
        invocation.getBinding().add(binding2);
        binding2.getParameter().getName().setValue(InvocationDefaultValueUtilities.getNewParameterName(invocation));
        assertThat(binding2.getParameter().getName().getValue()).isEqualTo(InvocationDefaultValueUtilities.PREFIX + "2");

        invocation.getBinding().remove(binding1);

        final Binding binding3 = new Binding() {{
            setParameter(new InformationItem());
        }};
        invocation.getBinding().add(binding3);
        binding3.getParameter().getName().setValue(InvocationDefaultValueUtilities.getNewParameterName(invocation));
        assertThat(binding3.getParameter().getName().getValue()).isEqualTo(InvocationDefaultValueUtilities.PREFIX + "3");
    }
}
