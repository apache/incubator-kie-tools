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


package org.kie.workbench.common.stunner.bpmn.client.forms.fields.metaDataEditor;

import javax.enterprise.event.Event;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockito;
import org.gwtbootstrap3.client.ui.Button;
import org.gwtbootstrap3.client.ui.constants.IconType;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.client.forms.fields.model.MetaDataRow;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.AttributeValueTextBox;
import org.kie.workbench.common.stunner.bpmn.client.forms.widgets.VariableNameTextBox;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.NotificationEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore("https://github.com/apache/incubator-kie-issues/issues/1431")
@RunWith(MockitoJUnitRunner.class)
public class MetaDataListItemWidgetViewImplTest {

    private static final String ATTRIBUTE_NAME = "attributeName";
    private static final String MODEL_TO_STRING = "modelToString";
    private static final String ATTRIBUTE_NEW_NAME = "attributeNewName";
    private static final String MODEL_NEW_TO_STRING = "modelNewToString";

    @GwtMock
    private DataBinder<MetaDataRow> metaDataRow;

    @GwtMock
    private VariableNameTextBox name;

    @GwtMock
    private AttributeValueTextBox value;

    @GwtMock
    private Button deleteButton;

    @GwtMock
    private KeyDownEvent keyDownEvent;

    private Event<NotificationEvent> notification = mock(EventSourceMock.class);

    @Captor
    private ArgumentCaptor<KeyDownHandler> keyDownHandlerCaptor;

    @Captor
    private ArgumentCaptor<ChangeHandler> changeHandlerCaptor;

    private MetaDataListItemWidgetViewImpl view;

    @Mock
    private MetaDataEditorWidgetView.Presenter parent;

    @Before
    public void setUp() throws Exception {
        GwtMockito.initMocks(this);
        view = mock(MetaDataListItemWidgetViewImpl.class);
        view.metaDataRow = metaDataRow;
        view.attribute = name;
        view.value = value;
        view.deleteButton = deleteButton;
        doCallRealMethod().when(view).init();
        doCallRealMethod().when(view).getModel();
        doCallRealMethod().when(view).setModel(any());
        doCallRealMethod().when(view).setParentWidget(any());
        doCallRealMethod().when(view).handleDeleteButton(any());
        doCallRealMethod().when(view).setReadOnly(anyBoolean());
        doCallRealMethod().when(view).notifyModelChanged();

        MetaDataRow row = new MetaDataRow();
        doReturn(row).when(metaDataRow).getModel();
    }

    @Test
    public void testSetProcessVarDataType() {
        MetaDataRow row = new MetaDataRow();
        row.setAttribute(ATTRIBUTE_NAME);
        row.setValue(null);

        doReturn(row).when(metaDataRow).getModel();
        view.setModel(row);
        verify(metaDataRow,
               times(1)).setModel(row);
        verify(deleteButton,
               times(1)).setIcon(IconType.TRASH);
    }

    @Test
    public void testSetModelValue() {
        assertNull(view.getModel().getValue());
        view.getModel().setValue("abc");
        assertEquals("abc",
                     view.getModel().getValue());
    }

    @Test
    public void testSetValue() {
        doCallRealMethod().when(view).setValue(anyString());
        doCallRealMethod().when(view).getValue();
        view.setValue("Value");
        when(view.getModel()).thenReturn(new MetaDataRow("myString", "Value"));

        assertEquals("Value", view.getValue());
    }

    @Test
    public void testAttributeChangeHandlerWhenDuplicate() {
        when(parent.isDuplicateAttribute(ATTRIBUTE_NEW_NAME)).thenReturn(true);
        prepareAttributeChange(ATTRIBUTE_NEW_NAME, MODEL_NEW_TO_STRING);
        verify(parent).isDuplicateAttribute(ATTRIBUTE_NEW_NAME);
        verify(parent).showErrorMessage(anyString());
        verify(name).setValue(ATTRIBUTE_NAME);
    }

    @Test
    public void testAttributeChangeHandlerWhenNotDuplicate() {
        when(parent.isDuplicateAttribute(ATTRIBUTE_NEW_NAME)).thenReturn(false);
        prepareAttributeChange(ATTRIBUTE_NEW_NAME, MODEL_NEW_TO_STRING);
        verify(parent).isDuplicateAttribute(ATTRIBUTE_NEW_NAME);
        verify(parent).notifyModelChanged();
    }

    private void prepareAttributeChange(String newName, String newToString) {
        doReturn(newName).when(name).getText();
        MetaDataRow model = mock(MetaDataRow.class);
        when(model.getAttribute()).thenReturn(ATTRIBUTE_NAME);
        when(model.toString()).thenReturn(MODEL_TO_STRING);
        doReturn(model).when(metaDataRow).getModel();
        view.setParentWidget(parent);
        view.init();
        view.setModel(model);
        when(model.toString()).thenReturn(newToString);
        verify(name).addChangeHandler(changeHandlerCaptor.capture());
        ChangeHandler handler = changeHandlerCaptor.getValue();
        handler.onChange(mock(ChangeEvent.class));
    }

    @Test
    public void testHandleDeleteButton() {
        MetaDataRow model = mock(MetaDataRow.class);
        when(view.getModel()).thenReturn(model);
        view.setParentWidget(parent);
        view.handleDeleteButton(null);
        verify(parent).removeMetaData(model);
    }

    @Test
    public void testSetReadOnlyTrue() {
        view.setReadOnly(true);
        verify(deleteButton,
               times(1)).setEnabled(false);
        verify(value,
               times(1)).setReadOnly(true);
        verify(name,
               times(1)).setEnabled(false);
    }

    @Test
    public void testSetReadOnlyFalse() {
        view.setReadOnly(false);
        verify(deleteButton,
               times(1)).setEnabled(true);
        verify(value,
               times(1)).setReadOnly(false);
        verify(name,
               times(1)).setEnabled(true);
    }

    @Test
    public void testNotifyModelChanged() {
        doCallRealMethod().when(view).getValue();
        doCallRealMethod().when(view).notifyModelChanged();
        doCallRealMethod().when(view).setParentWidget(parent);
        doNothing().when(parent).notifyModelChanged();

        view.setParentWidget(parent);

        when(view.getModel()).thenReturn(new MetaDataRow("myString", "Value"));
        view.notifyModelChanged();
        assertEquals("Value", view.getValue());

        view.notifyModelChanged();
        verify(parent,
               times(1)).notifyModelChanged();

        when(view.getModel()).thenReturn(new MetaDataRow("", ""));
        view.notifyModelChanged();
        verify(parent,
               times(2)).notifyModelChanged();

        when(view.getModel()).thenReturn(new MetaDataRow("myString2", "Value2"));
        view.notifyModelChanged();
        verify(parent,
               times(3)).notifyModelChanged();
    }
}
