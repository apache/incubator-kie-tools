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
import org.drools.workbench.models.guided.dtable.shared.model.LimitedEntryConditionCol52;
import org.drools.workbench.models.guided.dtable.shared.model.RowNumberCol52;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class LimitedEntryConditionCol52DefinitionBuilderTest extends BaseColumnDefinitionBuilderTest {

    @Override
    protected ColumnDefinitionBuilder getBuilder() {
        return new LimitedEntryConditionCol52DefinitionBuilder( serviceCaller );
    }

    @Test
    public void checkColumnType() {
        assertEquals( LimitedEntryConditionCol52.class,
                      builder.getSupportedColumnType() );
    }

    @Test
    public void unknownColumnTypeDoesNotTriggerBuilder() {
        final BaseColumn column = new RowNumberCol52();
        builder.generateDefinition( dtPresenter,
                                    column,
                                    ( String definition ) -> {
                                        fail( "RowNumberCol52 should not be handled by ConditionCol52DefinitionBuilder" );
                                    } );
    }

    @Test
    public void conditionIsNotPartOfAPattern() {
        final BaseColumn column = new LimitedEntryConditionCol52();
        builder.generateDefinition( dtPresenter,
                                    column,
                                    ( String definition ) -> {
                                        fail( "ConditionCol52 was not part of a Pattern52 and hence should not be handled by ConditionCol52DefinitionBuilder" );
                                    } );
    }

    @Test
    public void conditionIsPartOfAPattern() {
        final AtomicBoolean calledBack = new AtomicBoolean( false );

        setupLimitedEntryPatternAndCondition();

        builder.generateDefinition( dtPresenter,
                                    model.getExpandedColumns().get( 3 ),
                                    ( String definition ) -> {
                                        calledBack.set( true );
                                        assertEquals( "$p : Person( name == \"Michael\" )",
                                                      definition );
                                    } );
        assertTrue( calledBack.get() );
    }

}
