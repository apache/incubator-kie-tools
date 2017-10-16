/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.widget.table.popovers.definitions;

import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.workbench.models.guided.dtable.shared.model.ActionSetFieldCol52;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ActionSetFieldCol52DefinitionBuilderTest extends BaseColumnDefinitionBuilderTest {

    @Override
    protected ColumnDefinitionBuilder getBuilder() {
        return new ActionSetFieldCol52DefinitionBuilder(serviceCaller);
    }

    @Test
    public void checkColumnType() {
        assertEquals(ActionSetFieldCol52.class,
                     builder.getSupportedColumnType());
    }

    @Test
    public void unknownColumnTypeDoesNotTriggerBuilder() {
        final BaseColumn column = new RowNumberCol52();
        builder.generateDefinition(dtPresenter,
                                   column,
                                   (String definition) -> {
                                       fail("RowNumberCol52 should not be handled by ActionSetFieldCol52DefinitionBuilder");
                                   });
    }

    @Test
    public void simpleActionSetField() {
        final AtomicBoolean calledBack = new AtomicBoolean(false);

        setupPatternAndCondition();

        final ActionSetFieldCol52 asf = new ActionSetFieldCol52();
        asf.setBoundName("$p");
        asf.setFactField("name");
        model.getActionCols().add(asf);

        when(dmo.getFieldType(eq("Person"),
                              eq("name"))).thenReturn(DataType.TYPE_STRING);

        builder.generateDefinition(dtPresenter,
                                   asf,
                                   (String definition) -> {
                                       calledBack.set(true);
                                       assertEquals("$p.setName( \"x\" );",
                                                    definition);
                                   });
        assertTrue(calledBack.get());
    }

    @Test
    public void simpleActionUpdateField() {
        final AtomicBoolean calledBack = new AtomicBoolean(false);

        setupPatternAndCondition();

        final ActionSetFieldCol52 asf = new ActionSetFieldCol52();
        asf.setBoundName("$p");
        asf.setFactField("name");
        asf.setUpdate(true);
        model.getActionCols().add(asf);

        when(dmo.getFieldType(eq("Person"),
                              eq("name"))).thenReturn(DataType.TYPE_STRING);

        builder.generateDefinition(dtPresenter,
                                   asf,
                                   (String definition) -> {
                                       calledBack.set(true);
                                       assertEquals("modify( $p ) {<br/>" +
                                                            "setName( \"x\" )<br/>" +
                                                            "}",
                                                    definition);
                                   });
        assertTrue(calledBack.get());
    }
}
