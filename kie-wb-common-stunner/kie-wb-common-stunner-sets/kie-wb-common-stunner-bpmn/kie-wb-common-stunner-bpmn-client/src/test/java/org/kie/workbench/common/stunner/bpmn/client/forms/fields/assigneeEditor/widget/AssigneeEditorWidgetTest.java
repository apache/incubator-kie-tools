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

package org.kie.workbench.common.stunner.bpmn.client.forms.fields.assigneeEditor.widget;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.assertj.core.api.Assertions;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.i18n.StunnerBPMNConstants;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.uberfire.ext.widgets.common.client.dropdown.LiveSearchDropDown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.bpmn.forms.model.AssigneeType.USER;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AssigneeEditorWidgetTest {

    private static final int MAX = 5;

    @Mock
    private AssigneeEditorWidgetView view;

    @Mock
    private ManagedInstance<AssigneeListItem> listItems;

    @Mock
    private TranslationService translationService;

    private AssigneeEditorWidget widget;

    @Before
    public void init() {
        when(view.asWidget()).thenReturn(mock(Widget.class));

        when(listItems.get()).then((Answer<AssigneeListItem>) invocationOnMock -> new AssigneeListItem(mock(LiveSearchDropDown.class), mock(AssigneeLiveSearchService.class)));

        widget = new AssigneeEditorWidget(view, listItems, translationService);
    }

    @Test
    public void testSetEmptyValue() {
        widget.setValue("");

        verify(view).clearList();
        verify(listItems).destroyAll();
        verify(listItems, never()).get();
        verify(view, never()).add(any());
    }

    @Test
    public void testSetValue() {
        String value = "a,b,c";
        widget.setValue(value);

        verify(view).clearList();
        verify(listItems).destroyAll();
        verify(listItems, times(3)).get();
        verify(view, times(3)).add(any());

        widget.doSave();

        String newValue = widget.getValue();

        assertEquals(value, newValue);
    }

    @Test
    public void testAddAssigneesWithoutMax() {
        widget.addAssignee();
        widget.addAssignee();
        widget.addAssignee();
        widget.addAssignee();
        widget.addAssignee();
        widget.addAssignee();
        widget.addAssignee();

        verify(listItems, times(7)).get();
        verify(view, times(7)).add(any());
    }

    @Test
    public void testAddAssigneesWithMaxAndRemove() {
        widget.init(USER, MAX);

        widget.addAssignee();
        widget.addAssignee();
        widget.addAssignee();
        widget.addAssignee();
        widget.addAssignee();
        widget.addAssignee();
        widget.addAssignee();

        verify(listItems, times(5)).get();

        ArgumentCaptor<AssigneeListItem> itemsArgumentCaptor = ArgumentCaptor.forClass(AssigneeListItem.class);

        verify(view, times(5)).add(itemsArgumentCaptor.capture());

        verify(view).disableAddButton();

        List<AssigneeListItem> items = itemsArgumentCaptor.getAllValues();

        Assertions.assertThat(items)
                .isNotNull()
                .isNotEmpty()
                .hasSize(5);

        items.forEach(widget::removeAssignee);

        verify(listItems, times(5)).destroy(any());
        verify(view, times(5)).enableAddButton();
    }

    @Test
    public void testCheckDuplicateName() {
        widget.addAssignee();

        verify(listItems, times(1)).get();

        ArgumentCaptor<AssigneeListItem> itemsArgumentCaptor = ArgumentCaptor.forClass(AssigneeListItem.class);

        verify(view, times(1)).add(itemsArgumentCaptor.capture());

        List<AssigneeListItem> items = itemsArgumentCaptor.getAllValues();

        Assertions.assertThat(items)
                .isNotNull()
                .isNotEmpty()
                .hasSize(1);

        String name = "John Stark";

        items.get(0).getAssignee().setName(name);

        assertTrue(widget.isDuplicateName(name));
    }

    @Test
    public void testGeneral() {
        verify(view).init(widget);

        widget.asWidget();

        verify(view).asWidget();

        widget.getNameHeader();
        verify(translationService).getTranslation(StunnerBPMNConstants.ASSIGNEE_LABEL);


        widget.getAddLabel();
        verify(translationService).getTranslation(StunnerBPMNConstants.ASSIGNEE_NEW);

        widget.destroy();
        verify(listItems).destroyAll();
    }
}
