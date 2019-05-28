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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.notificationsEditor;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ReflectionUtilsTest;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.spy;

@RunWith(LienzoMockitoTestRunner.class)
public class NotificationsEditorFieldRendererTest extends ReflectionUtilsTest {

    @GwtMock
    private NotificationsEditorWidget notificationsEditorWidget;

    private NotificationsEditorFieldRenderer notificationsEditorFieldRenderer;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        GwtMockito.initMocks(this);
        notificationsEditorFieldRenderer = spy(new NotificationsEditorFieldRenderer(notificationsEditorWidget));

        doCallRealMethod().when(notificationsEditorFieldRenderer).getName();
        doCallRealMethod().when(notificationsEditorFieldRenderer).getSupportedCode();
        doCallRealMethod().when(notificationsEditorFieldRenderer).getField();
    }

    @Test
    public void getNameTest() {
        Assert.assertEquals("NotificationsEditor", notificationsEditorFieldRenderer.getName());
    }

    @Test
    public void getSupportedCodeTest() {
        Assert.assertEquals("NotificationsEditor", notificationsEditorFieldRenderer.getSupportedCode());
    }
}
