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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.reassignmentsEditor.widget;

import com.ait.lienzo.test.LienzoMockitoTestRunner;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import elemental2.dom.HTMLButtonElement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.util.ReflectionUtilsTest;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(LienzoMockitoTestRunner.class)
public class ReassignmentWidgetTest extends ReflectionUtilsTest {

    @GwtMock
    private ReassignmentWidgetViewImpl reassignmentWidgetView;

    @GwtMock
    private ClientTranslationService translationService;

    private ReassignmentWidget reassignmentWidget;

    private HTMLButtonElement saveButton, addButton;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        GwtMockito.initMocks(this);

        reassignmentWidget = spy(new ReassignmentWidget(reassignmentWidgetView, translationService));

        saveButton = spy(new HTMLButtonElement());
        addButton = spy(new HTMLButtonElement());

        doCallRealMethod().when(reassignmentWidget).getNameHeader();
        doCallRealMethod().when(reassignmentWidget).setReadOnly(any(boolean.class));
        doCallRealMethod().when(reassignmentWidgetView).setReadOnly(any(boolean.class));

        setFieldValue(reassignmentWidgetView, "saveButton", saveButton);
        setFieldValue(reassignmentWidgetView, "addButton", addButton);

        when(translationService.getValue(any(String.class))).thenReturn("Reassignment");
    }

    @Test
    public void getNameHeaderTest() {
        Assert.assertEquals("Reassignment", reassignmentWidget.getNameHeader());
    }

    @Test
    public void setReadOnlyTest() {
        reassignmentWidget.setReadOnly(false);
        boolean readOnly = getFieldValue(ReassignmentWidgetViewImpl.class, reassignmentWidgetView, "readOnly");
        Assert.assertEquals(false, readOnly);
        reassignmentWidget.save();
    }
}
