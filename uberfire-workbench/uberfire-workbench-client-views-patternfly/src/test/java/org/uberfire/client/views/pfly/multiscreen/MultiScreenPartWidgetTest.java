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

package org.uberfire.client.views.pfly.multiscreen;

import java.util.function.Consumer;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.workbench.model.PanelDefinition;
import org.uberfire.workbench.model.PartDefinition;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class MultiScreenPartWidgetTest {

    @Mock
    HTMLDivElement content;

    @Mock
    MultiScreenView multiScreenView;

    @Mock
    ManagedInstance<MultiScreenView> multiScreenViews;

    @Mock
    WorkbenchPartPresenter.View view;

    @Mock
    PartDefinition partDefinition;

    @InjectMocks
    MultiScreenPartWidget widget;

    @Before
    public void setup() {
        when(multiScreenViews.get()).thenReturn(multiScreenView);
        final WorkbenchPartPresenter presenter = mock(WorkbenchPartPresenter.class);
        doAnswer(invocationOnMock -> {
            invocationOnMock.getArgumentAt(0, Consumer.class).accept(null);
            return null;
        }).when(presenter).getMenus(any());
        when(view.getPresenter()).thenReturn(presenter);
        when(partDefinition.getParentPanel()).thenReturn(mock(PanelDefinition.class));
        when(presenter.getDefinition()).thenReturn(partDefinition);
    }

    @Test
    public void testInvalidRemovePart() {
        boolean removed = widget.remove(partDefinition);
        assertFalse(removed);
    }

    @Test
    public void testInvalidChangeTitle() {
        widget.changeTitle(partDefinition,
                           null,
                           null);
    }

    @Test
    public void testInvalidSelectPart() {
        boolean selected = widget.selectPart(partDefinition);
        assertFalse(selected);
    }

    @Test
    public void testChangeTitle() {
        String title = "newTitle";
        IsWidget titleWidget = mock(IsWidget.class);

        widget.addPart(view);

        widget.changeTitle(partDefinition,
                           title,
                           titleWidget);

        verify(multiScreenView).setTitle(title);
        verify(multiScreenView).setTitleWidget(titleWidget);
    }

    @Test
    public void testSelectPart() {
        widget.addPart(view);

        boolean selected = widget.selectPart(partDefinition);

        verify(multiScreenView, times(2)).show();
        assertTrue(selected);
    }

    @Test
    public void testAddRemovePart() {
        widget.addPart(view);

        verify(multiScreenView).setTitle(any());
        verify(multiScreenView,
               never()).setTitleWidget(any());
        verify(multiScreenView).setCloseHandler(any());
        verify(multiScreenView).disableClose();
        verify(multiScreenView).show();
        verify(content).appendChild(any());

        boolean removed = widget.remove(partDefinition);

        assertTrue(removed);
    }

    @Test
    public void testOnResize(){
        when(multiScreenView.isVisible()).thenReturn(true, false);

        widget.addPart(view);

        widget.onResize();
        widget.onResize();

        verify(multiScreenView, times(2)).isVisible();
        verify(multiScreenView).onResize();
    }
}
