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
package org.drools.workbench.screens.guided.rule.client.editor;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.Command;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ConstraintValueChangeHandlerTest {

    @Mock
    private BaseSingleFieldConstraint constraint;

    @Mock
    private Command onChangeCommand;

    @Mock
    private ValueChangeEvent valueChangeEvent;
    private ConstraintValueChangeHandler handler;

    @Before
    public void setUp() throws Exception {
        handler = new ConstraintValueChangeHandler(constraint,
                                                   onChangeCommand);
    }

    @Test
    public void alterValueForLists() {
        doReturn("in").when(constraint).getOperator();
        doReturn("a,\\\"b,c\\\",d").when(valueChangeEvent).getValue();

        handler.onValueChange(valueChangeEvent);

        verify(onChangeCommand).execute();
        verify(constraint).setValue("a,\"b,c\",d");
    }

    @Test
    public void doNotAlterValue() {
        doReturn("==").when(constraint).getOperator();
        doReturn("\"hello\"").when(valueChangeEvent).getValue();

        handler.onValueChange(valueChangeEvent);

        verify(onChangeCommand).execute();
        verify(constraint).setValue("\"hello\"");
    }

    @Test
    public void nullCommand() {

        handler = new ConstraintValueChangeHandler(constraint,
                                                   null);

        handler.onValueChange(valueChangeEvent);

        verify(onChangeCommand, never()).execute();
    }
}