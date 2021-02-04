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

import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.models.datamodel.rule.FactPattern;
import org.drools.workbench.models.datamodel.rule.SingleFieldConstraint;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BRLConditionVariableColumn;
import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.soup.project.datamodel.oracle.DataType;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class BRLConditionColumnDefinitionBuilderTest extends BaseColumnDefinitionBuilderTest {

    @Override
    protected ColumnDefinitionBuilder getBuilder() {
        return new BRLConditionColumnDefinitionBuilder(serviceCaller);
    }

    @Test
    public void checkColumnType() {
        assertEquals(BRLConditionVariableColumn.class,
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
    public void simpleBRLConditionColumn() {
        final AtomicBoolean calledBack = new AtomicBoolean(false);

        setupBRLConditionColumn();

        builder.generateDefinition(dtPresenter,
                                   model.getExpandedColumns().get(3),
                                   (String definition) -> {
                                       calledBack.set(true);
                                       assertEquals("Person( name == \"x\" )<br/>" +
                                                            "Smurf( colour == \"x\" )",
                                                    definition);
                                   });
        assertTrue(calledBack.get());
    }

    private void setupBRLConditionColumn() {
        final BRLConditionColumn brl = new BRLConditionColumn();

        final FactPattern fp1 = new FactPattern();
        fp1.setFactType("Person");
        final SingleFieldConstraint sfc1 = new SingleFieldConstraint();
        sfc1.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        sfc1.setFactType("Person");
        sfc1.setOperator("==");
        sfc1.setFieldName("name");
        sfc1.setValue("f1");
        fp1.addConstraint(sfc1);

        final FactPattern fp2 = new FactPattern();
        fp2.setFactType("Smurf");
        final SingleFieldConstraint sfc2 = new SingleFieldConstraint();
        sfc2.setConstraintValueType(BaseSingleFieldConstraint.TYPE_TEMPLATE);
        sfc2.setFactType("Smurf");
        sfc2.setOperator("==");
        sfc2.setFieldName("colour");
        sfc2.setValue("f2");
        fp2.addConstraint(sfc2);

        brl.getDefinition().add(fp1);
        brl.getDefinition().add(fp2);
        brl.getChildColumns().add(new BRLConditionVariableColumn("f1",
                                                                 DataType.TYPE_STRING));
        brl.getChildColumns().add(new BRLConditionVariableColumn("f2",
                                                                 DataType.TYPE_STRING));
        model.getConditions().add(brl);
    }
}
