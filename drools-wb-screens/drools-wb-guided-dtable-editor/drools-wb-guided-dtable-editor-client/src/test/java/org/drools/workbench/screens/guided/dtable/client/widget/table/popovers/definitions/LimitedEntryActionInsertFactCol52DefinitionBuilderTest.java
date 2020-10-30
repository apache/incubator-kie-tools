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

import org.drools.workbench.models.guided.dtable.shared.model.BaseColumn;
import org.drools.workbench.models.guided.dtable.shared.model.DTCellValue52;
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryActionInsertFactCol52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class LimitedEntryActionInsertFactCol52DefinitionBuilderTest extends BaseColumnDefinitionBuilderTest {

    @Override
    protected ColumnDefinitionBuilder getBuilder() {
        return new LimitedEntryActionInsertFactCol52DefinitionBuilder(serviceCaller);
    }

    @Test
    public void checkColumnType() {
        assertEquals(LimitedEntryActionInsertFactCol52.class,
                     builder.getSupportedColumnType());
    }

    @Test
    public void unknownColumnTypeDoesNotTriggerBuilder() {
        final BaseColumn column = new RowNumberCol52();
        builder.generateDefinition(dtPresenter,
                                   column,
                                   (String definition) -> {
                                       fail("RowNumberCol52 should not be handled by ActionInsertFactCol52DefinitionBuilder");
                                   });
    }

    @Test
    public void simpleAction() {
        final AtomicBoolean calledBack = new AtomicBoolean(false);

        final LimitedEntryActionInsertFactCol52 aif = new LimitedEntryActionInsertFactCol52();
        aif.setFactType("Person");
        aif.setFactField("name");
        aif.setValue(new DTCellValue52("Michael"));
        model.getActionCols().add(aif);

        builder.generateDefinition(dtPresenter,
                                   aif,
                                   (String definition) -> {
                                       calledBack.set(true);
                                       assertEquals("Person fact0 = new Person();<br/>" +
                                                            "fact0.setName( \"Michael\" );<br/>" +
                                                            "insert( fact0 );",
                                                    definition);
                                   });
        assertTrue(calledBack.get());
    }
}
