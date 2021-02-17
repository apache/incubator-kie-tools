/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.transfer;

import java.util.Arrays;
import java.util.List;

import org.dashbuilder.external.model.ExternalComponent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.ext.layout.editor.api.PerspectiveServices;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LayoutComponentHelperTest {

    @Mock
    PerspectiveServices perspectiveServices;

    @InjectMocks
    LayoutComponentHelper layoutComponentsHelper;

    @Test
    public void testComponentId() {
        String c1 = "c1";
        String c2 = "c2";
        LayoutTemplate lt = createLayoutTemplate("lt", c1, c2);
        when(perspectiveServices.listLayoutTemplates()).thenReturn(singletonList(lt));

        List<String> components = layoutComponentsHelper.findComponentsInTemplates(p -> true);

        assertEquals(2, components.size());
        assertEquals(components, Arrays.asList(c1, c2));
    }

    public void testNoComponentId() {
        LayoutTemplate lt = createLayoutTemplate("lt");
        when(perspectiveServices.listLayoutTemplates()).thenReturn(singletonList(lt));

        List<String> components = layoutComponentsHelper.findComponentsInTemplates(p -> true);

        assertTrue(components.isEmpty());
    }

    public void testPageFilter() {
        String c1 = "c1";
        LayoutTemplate lt = createLayoutTemplate("lt", c1);
        when(perspectiveServices.listLayoutTemplates()).thenReturn(singletonList(lt));

        List<String> components = layoutComponentsHelper.findComponentsInTemplates(p -> false);

        assertTrue(components.isEmpty());
    }

    private LayoutTemplate createLayoutTemplate(String name, String... componentIds) {
        LayoutTemplate lt = new LayoutTemplate(name);
        LayoutRow lr = new LayoutRow();
        LayoutColumn lc = new LayoutColumn("");

        lr.add(lc);
        lt.addRow(lr);
        for (String componentId : componentIds) {
            LayoutComponent lComp = new LayoutComponent();
            lComp.addProperty(ExternalComponent.COMPONENT_ID_KEY, componentId);
            lc.add(lComp);
        }
        return lt;
    }

}
