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

package org.kie.workbench.common.dmn.client.editors.types.listview.constraint;

import java.util.function.BiConsumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.model.ConstraintType;
import org.kie.workbench.common.dmn.client.editors.types.common.DataType;
import org.kie.workbench.common.dmn.client.editors.types.listview.DataTypeListItem;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.dmn.api.definition.model.ConstraintType.ENUMERATION;
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
    private ManagedInstance<DataTypeConstraintModal> constraintModalManagedInstance;

    @Mock
    private DataTypeConstraintModal constraintModal;

    private DataTypeConstraint dataTypeConstraint;

    @Before
    public void setup() {
        dataTypeConstraint = spy(new DataTypeConstraint(view, constraintModalManagedInstance));
        when(constraintModalManagedInstance.get()).thenReturn(constraintModal);
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
        final ConstraintType expectedType = mock(ConstraintType.class);

        when(expectedListItem.getDataType()).thenReturn(datatype);
        when(datatype.getConstraint()).thenReturn(expectedConstraint);
        when(datatype.getConstraintType()).thenReturn(expectedType);

        dataTypeConstraint.init(expectedListItem);

        final DataTypeListItem actualListItem = dataTypeConstraint.getListItem();
        final String actualConstraint = dataTypeConstraint.getValue();
        final ConstraintType actualType = dataTypeConstraint.getConstraintType();

        assertEquals(expectedType, actualType);
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
    }

    @Test
    public void testDisableEditMode() {

        dataTypeConstraint.disableEditMode();

        assertFalse(dataTypeConstraint.isEditModeEnabled());
        verify(view).hideAnchor();
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
        final BiConsumer<String, ConstraintType> onShowConsumer = (s, c) -> { /* Nothing. */ };

        doReturn(listItem).when(dataTypeConstraint).getListItem();
        doReturn(constraint).when(dataTypeConstraint).getValue();
        doReturn(onShowConsumer).when(dataTypeConstraint).getOnShowConsumer();
        when(listItem.getType()).thenReturn(type);

        dataTypeConstraint.openModal();

        constraintModal.load(listItem);
        constraintModal.show(onShowConsumer);
    }

    @Test
    public void testGetOnShowConsumer() {

        final String expectedConstraint = "1,2,3";
        final ConstraintType expectedConstraintType = ENUMERATION;
        dataTypeConstraint.getOnShowConsumer().accept(expectedConstraint, ENUMERATION);

        final String actualConstraint = dataTypeConstraint.getValue();
        final ConstraintType actualConstraintType = dataTypeConstraint.getConstraintType();

        assertEquals(expectedConstraint, actualConstraint);
        assertEquals(expectedConstraintType, actualConstraintType);
        verify(dataTypeConstraint).refreshView();
    }

    @Test
    public void testDisable() {

        dataTypeConstraint.disable();

        final String actualConstraint = dataTypeConstraint.getValue();
        final ConstraintType actualConstraintType = dataTypeConstraint.getConstraintType();

        assertEquals(DataTypeConstraint.NONE, actualConstraint);
        assertEquals(ConstraintType.NONE, actualConstraintType);
        verify(view).disable();
    }

    @Test
    public void testEnable() {
        dataTypeConstraint.enable();
        verify(view).enable();
    }

    @Test
    public void testGetConstraintModal() {

        assertNull(dataTypeConstraint.getConstraintModal());

        final DataTypeConstraintModal firstCallResult = dataTypeConstraint.constraintModal();
        final DataTypeConstraintModal secondCallResult = dataTypeConstraint.constraintModal();

        assertNotNull(dataTypeConstraint.getConstraintModal());
        assertSame(dataTypeConstraint.getConstraintModal(), firstCallResult);
        assertSame(dataTypeConstraint.getConstraintModal(), secondCallResult);
    }
}
