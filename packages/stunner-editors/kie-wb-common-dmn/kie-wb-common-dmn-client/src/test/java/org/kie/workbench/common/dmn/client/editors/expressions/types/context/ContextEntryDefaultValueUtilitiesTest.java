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

package org.kie.workbench.common.dmn.client.editors.expressions.types.context;

import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.dmn.api.definition.model.Context;
import org.kie.workbench.common.dmn.api.definition.model.ContextEntry;
import org.kie.workbench.common.dmn.api.definition.model.InformationItem;

import static org.assertj.core.api.Assertions.assertThat;

public class ContextEntryDefaultValueUtilitiesTest {

    private Context context;

    @Before
    public void setup() {
        this.context = new Context();
    }

    @Test
    public void testGetNewContextEntryName() {
        final ContextEntry contextEntry1 = new ContextEntry() {{
            setVariable(new InformationItem());
        }};
        context.getContextEntry().add(contextEntry1);
        contextEntry1.getVariable().getName().setValue(ContextEntryDefaultValueUtilities.getNewContextEntryName(context));
        assertThat(contextEntry1.getVariable().getName().getValue()).isEqualTo(ContextEntryDefaultValueUtilities.PREFIX + "1");

        final ContextEntry contextEntry2 = new ContextEntry() {{
            setVariable(new InformationItem());
        }};
        context.getContextEntry().add(contextEntry2);
        contextEntry2.getVariable().getName().setValue(ContextEntryDefaultValueUtilities.getNewContextEntryName(context));
        assertThat(contextEntry2.getVariable().getName().getValue()).isEqualTo(ContextEntryDefaultValueUtilities.PREFIX + "2");
    }

    @Test
    public void testGetNewContextEntryNameWithExistingContextEntries() {
        final ContextEntry contextEntry1 = new ContextEntry() {{
            setVariable(new InformationItem());
        }};
        context.getContextEntry().add(contextEntry1);
        contextEntry1.getVariable().getName().setValue("entry");

        final ContextEntry contextEntry2 = new ContextEntry() {{
            setVariable(new InformationItem());
        }};
        context.getContextEntry().add(contextEntry2);
        contextEntry2.getVariable().getName().setValue(ContextEntryDefaultValueUtilities.getNewContextEntryName(context));
        assertThat(contextEntry2.getVariable().getName().getValue()).isEqualTo(ContextEntryDefaultValueUtilities.PREFIX + "1");
    }

    @Test
    public void testGetNewContextEntryNameWithDeletion() {
        final ContextEntry contextEntry1 = new ContextEntry() {{
            setVariable(new InformationItem());
        }};
        context.getContextEntry().add(contextEntry1);
        contextEntry1.getVariable().getName().setValue(ContextEntryDefaultValueUtilities.getNewContextEntryName(context));
        assertThat(contextEntry1.getVariable().getName().getValue()).isEqualTo(ContextEntryDefaultValueUtilities.PREFIX + "1");

        final ContextEntry contextEntry2 = new ContextEntry() {{
            setVariable(new InformationItem());
        }};
        context.getContextEntry().add(contextEntry2);
        contextEntry2.getVariable().getName().setValue(ContextEntryDefaultValueUtilities.getNewContextEntryName(context));
        assertThat(contextEntry2.getVariable().getName().getValue()).isEqualTo(ContextEntryDefaultValueUtilities.PREFIX + "2");

        context.getContextEntry().remove(contextEntry1);

        final ContextEntry contextEntry3 = new ContextEntry() {{
            setVariable(new InformationItem());
        }};
        context.getContextEntry().add(contextEntry3);
        contextEntry3.getVariable().getName().setValue(ContextEntryDefaultValueUtilities.getNewContextEntryName(context));
        assertThat(contextEntry3.getVariable().getName().getValue()).isEqualTo(ContextEntryDefaultValueUtilities.PREFIX + "3");
    }
}
