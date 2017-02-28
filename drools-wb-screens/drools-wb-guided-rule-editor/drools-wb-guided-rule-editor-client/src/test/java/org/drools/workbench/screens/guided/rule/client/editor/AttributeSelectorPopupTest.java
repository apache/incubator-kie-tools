/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.screens.guided.rule.client.editor;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Element;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.screens.guided.rule.client.resources.images.GuidedRuleEditorImages508;
import org.gwtbootstrap3.client.ui.Heading;
import org.gwtbootstrap3.client.ui.ListBox;
import org.gwtbootstrap3.client.ui.TextBox;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@WithClassesToStub({GuidedRuleEditorImages508.class, Heading.class})
@RunWith(GwtMockitoTestRunner.class)
public class AttributeSelectorPopupTest {

    static String[] attributes = new String[] {"attr1", "attr2", "attr3", "attr4"};
    static String[] duplicates = new String[] {"attr2", "attr3"};
    static String metadataName = "mockMetadata";

    @GwtMock
    TextBox boxMock;
    @GwtMock
    ListBox listMock;

    List<String> listMockAttributes;

    MockAttributeSelectorPopup popup;

    @Before
    public void setUp() {
        listMockAttributes = new ArrayList<>();
        popup = spy(new MockAttributeSelectorPopup());
        when(boxMock.getElement()).thenReturn(mock(Element.class));
        when(boxMock.getText()).thenReturn(metadataName);
        when(listMock.getItemCount()).thenAnswer(
            (InvocationOnMock invocation) -> listMockAttributes.size()
        );
        when(listMock.getItemText(anyInt())).thenAnswer(
            (InvocationOnMock invocation) -> listMockAttributes.get((int) invocation.getArguments()[0])
        );
        doAnswer((Answer<Void>)
            (InvocationOnMock invocation) -> {
                listMockAttributes.add((String) invocation.getArguments()[0]);
                return null;
            }
        ).when(listMock).addItem(anyString());
        doAnswer((Answer<Void>)
            (InvocationOnMock invocation) -> {
                listMockAttributes.remove((int) invocation.getArguments()[0]);
                return null;
            }
        ).when(listMock).removeItem(anyInt());
        popup.initialize(boxMock, listMock);
    }

    @Test
    public void alreadyUsedAttributesRemoved() {
        verify(popup).getAttributes();
        verify(popup).getReservedAttributes();
        verify(popup.list, times(4)).addItem(anyString());
        verify(popup.list, times(2)).removeItem(1);
    }

    @Test
    public void alreadyUsedMetadataNotAllowed() {
        popup.getMetadataHandler().onClick(null);
        verify(popup).isMetadataUnique(metadataName);
        verify(popup).metadataNotUniqueMessage(metadataName);
        verify(popup, never()).handleMetadataAddition(anyString());
    }

    @Test
    public void emptyMetadataNotAllowed() {
        when(boxMock.getText()).thenReturn("");
        popup.getMetadataHandler().onClick(null);
        verify(popup, never()).handleMetadataAddition(anyString());
    }

    @Test
    public void whitespaceMetadataNotAllowed() {
        when(boxMock.getText()).thenReturn(" \n\t ");
        popup.getMetadataHandler().onClick(null);
        verify(popup, never()).handleMetadataAddition(anyString());
    }

    private static class MockAttributeSelectorPopup extends AttributeSelectorPopup {

        @Override
        protected String[] getAttributes() {
            return attributes;
        }

        @Override
        protected String[] getReservedAttributes() {
            return duplicates;
        }

        @Override
        protected void handleAttributeAddition(String attributeName) {
            // mock, do nothing
        }

        @Override
        protected boolean isMetadataUnique(String metadataName) {
            return false;
        }

        @Override
        protected String metadataNotUniqueMessage(String metadataName) {
            return "mock message";
        }

        @Override
        protected void handleMetadataAddition(String metadataName) {
            // mock, do nothing
        }
    }
}