/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.ext.editor.commons.client.file.popups;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import elemental2.dom.HTMLElement;
import org.gwtbootstrap3.client.ui.Modal;
import org.gwtbootstrap3.client.ui.ModalBody;
import org.gwtbootstrap3.client.ui.ModalFooter;
import org.gwtbootstrap3.client.ui.gwt.FlowPanel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.common.popups.BaseModal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({Modal.class, RootPanel.class})
public class CommonModalBuilderTest {

    @Mock
    private BaseModal modalMock;

    private CommonModalBuilder builder;

    @Before
    public void setup() {
        builder = spy(new CommonModalBuilder());
    }

    @Test
    public void testAddBody() {

        final HTMLElement htmlElement = mock(HTMLElement.class);
        final FlowPanel flowPanel = mock(FlowPanel.class);
        final ModalBody modalBody = mock(ModalBody.class);

        doReturn(modalMock).when(builder).getModal();
        doReturn(modalBody).when(builder).makeModalBody();
        doReturn(flowPanel).when(builder).buildPanel(htmlElement, modalBody);

        builder.addBody(htmlElement);

        verify(modalMock).add(flowPanel);
    }

    @Test
    public void testAddFooter() {

        final HTMLElement htmlElement = mock(HTMLElement.class);
        final FlowPanel flowPanel = mock(FlowPanel.class);
        final ModalFooter modalFooter = mock(ModalFooter.class);

        doReturn(modalMock).when(builder).getModal();
        doReturn(modalFooter).when(builder).makeModalFooter();
        doReturn(flowPanel).when(builder).buildPanel(htmlElement, modalFooter);

        builder.addFooter(htmlElement);

        verify(modalMock).add(flowPanel);
    }

    @Test
    public void testBuild() {

        doReturn(modalMock).when(builder).getModal();

        final BaseModal modal = builder.build();

        assertEquals(modalMock, modal);
    }
}
