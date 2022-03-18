/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.artifacts;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class DataObjectEditorFieldRendererTest {

    @Mock
    private DataObjectTypeWidget widget;

    private DataObjectEditorFieldRenderer renderer;

    @Before
    public void setUp() {
        renderer = new DataObjectEditorFieldRenderer(widget);
    }

    @Test
    public void testGetName() {
        assertEquals("DataObjectEditor", renderer.getName());
    }

    @Test
    public void testSetReadonlyTrue() {
        renderer.setReadOnly(true);
        verify(widget).setReadOnly(true);
    }

    @Test
    public void testSetReadonlyFalse() {
        renderer.setReadOnly(false);
        verify(widget).setReadOnly(false);
    }
}
