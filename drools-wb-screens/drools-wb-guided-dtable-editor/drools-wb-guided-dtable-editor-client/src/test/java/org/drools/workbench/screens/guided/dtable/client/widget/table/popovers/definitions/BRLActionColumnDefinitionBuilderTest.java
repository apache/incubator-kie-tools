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

package org.drools.workbench.screens.guided.dtable.client.widget.table.popovers.definitions;

import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.workbench.models.datamodel.rule.ActionFieldValue;
import org.drools.workbench.models.datamodel.rule.ActionInsertFact;
import org.drools.workbench.models.datamodel.rule.FieldNatureType;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLActionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BRLActionColumnDefinitionBuilderTest extends BaseColumnDefinitionBuilderTest {

    @Override
    protected ColumnDefinitionBuilder getBuilder() {
        return new BRLActionColumnDefinitionBuilder(serviceCaller);
    }

    @Test
    public void checkColumnType() {
        assertEquals(BRLActionVariableColumn.class,
                     builder.getSupportedColumnType());
    }

    @Test
    public void unknownColumnTypeDoesNotTriggerBuilder() {
        final BaseColumn column = new RowNumberCol52();
        builder.generateDefinition(dtPresenter,
                                   column,
                                   (String definition) -> {
                                       fail("RowNumberCol52 should not be handled by ConditionCol52DefinitionBuilder");
                                   });
    }

    @Test
    public void simpleBRLActionColumn() {
        final AtomicBoolean calledBack = new AtomicBoolean(false);

        setupBRLActionColumn();

        builder.generateDefinition(dtPresenter,
                                   model.getExpandedColumns().get(3),
                                   (String definition) -> {
                                       calledBack.set(true);
                                       assertEquals("Person $a = new Person();<br/>" +
                                                            "$a.setName( \"x\" );<br/>" +
                                                            "$a.setAge( \"1\" );<br/>" +
                                                            "insert( $a );",
                                                    definition);
                                   });
        assertTrue(calledBack.get());
    }

    private void setupBRLActionColumn() {
        final BRLActionColumn brl = new BRLActionColumn();

        final ActionInsertFact ifc1 = new ActionInsertFact();
        ifc1.setFactType("Person");
        ifc1.setBoundName("$a");
        final ActionFieldValue afv1 = new ActionFieldValue();
        afv1.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv1.setField("name");
        afv1.setValue("f1");
        ifc1.addFieldValue(afv1);
        final ActionFieldValue afv2 = new ActionFieldValue();
        afv2.setNature(FieldNatureType.TYPE_TEMPLATE);
        afv2.setField("age");
        afv2.setValue("f2");
        ifc1.addFieldValue(afv2);

        brl.getDefinition().add(ifc1);
        brl.getChildColumns().add(new BRLActionVariableColumn("f1",
                                                              DataType.TYPE_STRING));
        brl.getChildColumns().add(new BRLActionVariableColumn("f2",
                                                              DataType.TYPE_NUMERIC_INTEGER));
        model.getActionCols().add(brl);

        when(dmo.getFieldType(eq("Person"),
                              eq("name"))).thenReturn(DataType.TYPE_STRING);
        when(dmo.getFieldType(eq("Person"),
                              eq("age"))).thenReturn(DataType.TYPE_NUMERIC_INTEGER);
    }
}
