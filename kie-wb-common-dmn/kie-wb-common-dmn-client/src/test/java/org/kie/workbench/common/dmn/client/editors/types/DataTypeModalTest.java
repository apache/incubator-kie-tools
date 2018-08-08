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

package org.kie.workbench.common.dmn.client.editors.types;

import java.util.Optional;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.v1_1.ItemDefinition;
import org.kie.workbench.common.dmn.api.property.dmn.QName;
import org.kie.workbench.common.dmn.client.editors.types.common.DataTypeFactory;
import org.kie.workbench.common.dmn.client.editors.types.common.ItemDefinitionUtils;
import org.kie.workbench.common.dmn.client.editors.types.treegrid.DataTypeTreeGrid;
import org.kie.workbench.common.dmn.client.property.dmn.QNameFieldConverter;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DataTypeModalTest {

    @Mock
    private DataTypeModal.View view;

    @Mock
    private DataTypeTreeGrid treeGrid;

    @Mock
    private ItemDefinitionUtils itemDefinitionUtils;

    @Mock
    private DataTypeFactory dataTypeFactory;

    @Mock
    private QNameFieldConverter qNameFieldConverter;

    private DataTypeModal modal;

    @Before
    public void setup() {
        modal = spy(new DataTypeModal(view, treeGrid, itemDefinitionUtils, dataTypeFactory, qNameFieldConverter));

        doNothing().when(modal).superSetup();
        doNothing().when(modal).superShow();
    }

    @Test
    public void testSetup() {
        modal.setup();
        verify(view).setup(treeGrid);
    }

    @Test
    public void testShowWhenItemDefinitionIsPresent() {

        final String selectedType = "item";
        final String selectedValue = "[][item][]";
        final QName qName = mock(QName.class);
        final ItemDefinition itemDefinition = mock(ItemDefinition.class);
        final DataType dataType = mock(DataType.class);

        when(dataTypeFactory.makeDataType(itemDefinition)).thenReturn(dataType);
        when(itemDefinitionUtils.findByName(selectedType)).thenReturn(Optional.of(itemDefinition));
        when(qNameFieldConverter.toModelValue(selectedValue)).thenReturn(qName);
        when(qName.getLocalPart()).thenReturn(selectedType);

        modal.show(selectedValue);

        verify(treeGrid).setupItems(dataType);
        verify(modal).superShow();
    }

    @Test
    public void testShowWhenItemDefinitionIsNotPresent() {

        final String selectedType = "item";
        final String selectedValue = "[][item][]";
        final QName qName = mock(QName.class);

        when(itemDefinitionUtils.findByName(selectedType)).thenReturn(Optional.empty());
        when(qNameFieldConverter.toModelValue(selectedValue)).thenReturn(qName);
        when(qName.getLocalPart()).thenReturn(selectedType);

        modal.show(selectedValue);

        verify(treeGrid, never()).setupItems(any());
        verify(modal, never()).superShow();
    }
}
