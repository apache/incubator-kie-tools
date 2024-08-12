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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.regexp.shared.RegExp;
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
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore("https://github.com/apache/incubator-kie-issues/issues/1431")
@RunWith(MockitoJUnitRunner.class)
public class MetaDataListItemWidgetTest {

    @GwtMock
    VariableNameTextBox name;

    @GwtMock
    AttributeValueTextBox value;

    @GwtMock
    Button deleteButton;

    @GwtMock
    DataBinder<MetaDataRow> attribute;

    //@Spy  // - cannot make Spy because of GWT error
    //@InjectMocks // - cannot InjectMocks because of GWT error
    private MetaDataListItemWidgetViewImpl widget;

    @Before
    public void initTestCase() {
        GwtMockito.initMocks(this);

        widget = GWT.create(MetaDataListItemWidgetViewImpl.class);
        MetaDataRow metaDataRow = new MetaDataRow();

        widget.attribute = name;
        widget.value = value;
        widget.deleteButton = deleteButton;
        widget.metaDataRow = attribute;

        Mockito.doCallRealMethod().when(widget).init();
        Mockito.doCallRealMethod().when(widget).setModel(any(MetaDataRow.class));
        when(widget.getModel()).thenReturn(metaDataRow);
    }

    @Test
    public void testInitWidget() {
        widget.init();
        verify(widget,
               times(1)).init();

        ArgumentCaptor<String> nameRegExpCaptor = ArgumentCaptor.forClass(String.class);
        verify(name,
               times(1)).setRegExp(nameRegExpCaptor.capture(),
                                   anyString(),
                                   anyString());
        RegExp nameRegExp = RegExp.compile(nameRegExpCaptor.getValue());
        assertEquals(false, nameRegExp.test("a 1"));
        assertEquals(false, nameRegExp.test("a@1"));
        assertEquals(true, nameRegExp.test("a1"));
        verify(name, times(1)).addChangeHandler(any(ChangeHandler.class));
        ArgumentCaptor<String> customValueRegExpCaptor = ArgumentCaptor.forClass(String.class);
    }

    @Test
    public void testSetModel() {
        widget.setModel(new MetaDataRow());
        verify(deleteButton).setIcon(IconType.TRASH);
        verify(widget, times(2)).getModel();
    }
}
