/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.impl;

import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.screens.guided.dtable.client.widget.table.model.synchronizers.ModelSynchronizer.VetoException;
import org.junit.Test;
import org.uberfire.ext.wires.core.grids.client.model.impl.BaseGridCellValue;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ModelSynchronizerTest extends BaseSynchronizerTest {

    @Test
    public void testSetCells() throws VetoException {
        modelSynchronizer.appendRow();

        uiModel.setCellValue(0,
                             1,
                             new BaseGridCellValue<String>("value"));

        assertEquals("value",
                     model.getData().get(0).get(1).getStringValue());
        assertEquals("value",
                     uiModel.getCell(0,
                                     1).getValue().getValue());
    }

    @Test
    public void testDeleteCells() throws VetoException {
        modelSynchronizer.appendRow();

        uiModel.setCellValue(0,
                             1,
                             new BaseGridCellValue<String>("value"));
        assertEquals("value",
                     model.getData().get(0).get(1).getStringValue());
        assertEquals("value",
                     uiModel.getCell(0,
                                     1).getValue().getValue());

        uiModel.deleteCell(0,
                           1);

        assertNull(model.getData().get(0).get(1).getStringValue());
        assertNull(uiModel.getCell(0,
                                   1));
    }

    @Test
    public void testInitialisationOfBooleanCellsWithDefaultValue() throws VetoException {
        setupBooleanColumn((c) -> c.setDefaultValue(new DTCellValue52(true)));

        modelSynchronizer.appendRow();

        assertTrue(model.getData().get(0).get(2).getBooleanValue());
        assertTrue((Boolean) uiModel.getCell(0,
                                             2).getValue().getValue());
    }

    @Test
    public void testInitialisationOfBooleanCellsWithNullDefaultValue() throws VetoException {
        setupBooleanColumn((c) -> c.setDefaultValue(new DTCellValue52((Boolean) null)));

        modelSynchronizer.appendRow();

        assertFalse(model.getData().get(0).get(2).getBooleanValue());
        assertFalse((Boolean) uiModel.getCell(0,
                                              2).getValue().getValue());
    }

    @Test
    public void testInitialisationOfBooleanCellsWithoutDefaultValue() throws VetoException {
        setupBooleanColumn((c) -> {/*Nothing*/ });

        modelSynchronizer.appendRow();

        assertFalse(model.getData().get(0).get(2).getBooleanValue());
        assertFalse((Boolean) uiModel.getCell(0,
                                              2).getValue().getValue());
    }

    private void setupBooleanColumn(final ParameterizedCommand<AttributeCol52> cmdInit) throws VetoException {
        final AttributeCol52 booleanColumn = new AttributeCol52();
        booleanColumn.setAttribute(Attribute.ENABLED.getAttributeName());
        cmdInit.execute(booleanColumn);
        modelSynchronizer.appendColumn(booleanColumn);
    }
}
