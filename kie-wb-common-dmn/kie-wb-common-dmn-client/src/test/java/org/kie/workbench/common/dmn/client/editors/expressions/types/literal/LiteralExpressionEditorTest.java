/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.literal;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.definition.HasName;
import org.kie.workbench.common.dmn.api.definition.v1_1.LiteralExpression;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LiteralExpressionEditorTest {

    @Mock
    private LiteralExpressionEditorView view;

    private LiteralExpressionEditor editor;

    @Before
    public void setup() {
        this.editor = new LiteralExpressionEditor(view);
    }

    @Test
    public void checkGetView() {
        assertEquals(view,
                     editor.getView());
    }

    @Test
    public void checkSetHasName() {
        final Optional<HasName> hasName = Optional.empty();
        editor.setHasName(hasName);
        verify(view).setHasName(eq(hasName));
    }

    @Test
    public void checkSetExpression() {
        final LiteralExpression expression = new LiteralExpression();
        editor.setExpression(expression);

        verify(view).setExpression(eq(expression));
    }
}
