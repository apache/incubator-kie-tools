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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint;

import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeConstraintTest {

    @Mock
    private DataTypeConstraint.View view;

    @Mock
    private DataTypeConstraintModal constraintModal;

    private DataTypeConstraint dataTypeConstraint;

    @Before
    public void setup() {
        dataTypeConstraint = spy(new DataTypeConstraint(view, constraintModal));
    }

    @Test
    public void testSetup() {
        dataTypeConstraint.setup();

        verify(view).init(dataTypeConstraint);
        verify(dataTypeConstraint).disableEditMode();
    }

    @Test
    public void testInit() {

        final DataTypeListItem expectedListItem = mock(DataTypeListItem.class);
        final DataType datatype = mock(DataType.class);
        final String expectedConstraint = "constraint";

        when(expectedListItem.getDataType()).thenReturn(datatype);
        when(datatype.getConstraint()).thenReturn(expectedConstraint);

        dataTypeConstraint.init(expectedListItem);

        final DataTypeListItem actualListItem = dataTypeConstraint.getListItem();
        final String actualConstraint = dataTypeConstraint.getValue();

        assertEquals(expectedListItem, actualListItem);
        assertEquals(expectedConstraint, actualConstraint);
        verify(dataTypeConstraint).refreshView();
    }

    @Test
    public void testGetElement() {

        final HTMLElement expectedElement = mock(HTMLElement.class);

        when(view.getElement()).thenReturn(expectedElement);

        final HTMLElement actualElement = dataTypeConstraint.getElement();

        assertEquals(expectedElement, actualElement);
    }

    @Test
    public void testEnableEditMode() {

        dataTypeConstraint.enableEditMode();

        assertTrue(dataTypeConstraint.isEditModeEnabled());
        verify(view).showAnchor();
        verify(view).showText();
        verify(view).hideTextLabel();
    }

    @Test
    public void testDisableEditModeWhenValueIsEmpty() {

        doReturn("").when(dataTypeConstraint).getValue();

        dataTypeConstraint.disableEditMode();

        assertFalse(dataTypeConstraint.isEditModeEnabled());
        verify(view).hideAnchor();
        verify(view).hideText();
        verify(view).hideTextLabel();
    }

    @Test
    public void testDisableEditModeWhenValueIsNotEmpty() {

        doReturn("1,2,3").when(dataTypeConstraint).getValue();

        dataTypeConstraint.disableEditMode();

        assertFalse(dataTypeConstraint.isEditModeEnabled());
        verify(view).hideAnchor();
        verify(view).showText();
        verify(view).showTextLabel();
    }

    @Test
    public void testRefreshViewWhenEditModeIsEnabled() {

        final String constraint = "1,2,3";

        doReturn(constraint).when(dataTypeConstraint).getValue();
        doReturn(true).when(dataTypeConstraint).isEditModeEnabled();

        dataTypeConstraint.refreshView();

        verify(dataTypeConstraint).enableEditMode();
    }

    @Test
    public void testRefreshViewWhenEditModeIsNotEnabled() {

        final String constraint = "1,2,3";

        doReturn(constraint).when(dataTypeConstraint).getValue();
        doReturn(false).when(dataTypeConstraint).isEditModeEnabled();

        dataTypeConstraint.refreshView();

        verify(dataTypeConstraint).disableEditMode();
    }

    @Test
    public void testOpenModal() {

        final DataTypeListItem listItem = mock(DataTypeListItem.class);
        final String constraint = "1,2,3";
        final String type = "string";
        final Consumer<String> onShowConsumer = (s) -> { /* Nothing. */ };

        doReturn(listItem).when(dataTypeConstraint).getListItem();
        doReturn(constraint).when(dataTypeConstraint).getValue();
        doReturn(onShowConsumer).when(dataTypeConstraint).getOnShowConsumer();
        when(listItem.getType()).thenReturn(type);

        dataTypeConstraint.openModal();

        constraintModal.load(type, constraint);
        constraintModal.show(onShowConsumer);
    }

    @Test
    public void testGetOnShowConsumer() {

        final String expectedConstraint = "1,2,3";

        dataTypeConstraint.getOnShowConsumer().accept(expectedConstraint);

        final String actualConstraint = dataTypeConstraint.getValue();

        assertEquals(expectedConstraint, actualConstraint);
        verify(dataTypeConstraint).refreshView();
    }
}
