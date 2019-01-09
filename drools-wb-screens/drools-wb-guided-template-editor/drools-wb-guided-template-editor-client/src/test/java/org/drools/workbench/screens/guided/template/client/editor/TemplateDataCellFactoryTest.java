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

package org.drools.workbench.screens.guided.template.client.editor;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.kie.workbench.common.widgets.client.datamodel.AsyncPackageDataModelOracle;
import org.mockito.Mock;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@WithClassesToStub(DateTimeFormat.class)
@RunWith(GwtMockitoTestRunner.class)
public class TemplateDataCellFactoryTest {

    @Mock
    private AsyncPackageDataModelOracle oracle;

    @Mock
    private TemplateDropDownManager dropDownManager;

    private boolean isReadOnly = false;

    @Mock
    private EventBus eventBus;

    @Mock
    private TemplateDataColumn column;

    private TemplateDataCellFactory testedFactory;

    @Before
    public void setUp() throws Exception {
        testedFactory = spy(new TemplateDataCellFactory(oracle, dropDownManager, isReadOnly, eventBus));
    }

    @Test
    public void testGetCell() throws Exception {
        final String factType = "org.kiegroup.Car";
        final String factField = "color";
        final String dataType = DataType.DataTypes.STRING.name();

        doReturn(factType).when(column).getFactType();
        doReturn(factField).when(column).getFactField();
        doReturn(dataType).when(column).getDataType();
        doReturn("==").when(column).getOperator();

        testedFactory.getCell(column);

        verify(testedFactory, never()).makeSelectionEnumCell(factType, factField, "==", dataType);
    }

    @Test
    public void testGetCellWhenEnumPresent() throws Exception {
        final String factType = "org.kiegroup.Car";
        final String factField = "color";
        final String dataType = DataType.DataTypes.STRING.name();

        doReturn(true).when(oracle).hasEnums(factType, factField);
        doReturn(factType).when(column).getFactType();
        doReturn(factField).when(column).getFactField();
        doReturn(dataType).when(column).getDataType();
        doReturn("==").when(column).getOperator();

        testedFactory.getCell(column);

        verify(testedFactory).makeSelectionEnumCell(factType, factField, "==", dataType);
    }
}
