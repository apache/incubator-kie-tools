/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.api.rules;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.adf.definitions.DynamicReadOnly;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.RuleViolations;
import org.kie.workbench.common.stunner.core.rule.context.GraphConnectionContext;
import org.kie.workbench.common.stunner.core.rule.ext.RuleExtension;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class NoInputNodesInImportedDecisionRuleTest {

    @Mock
    protected RuleExtension rule;

    protected NoInputNodesInImportedDecisionRule check;

    @Mock
    protected GraphConnectionContext context;

    @Mock
    protected View content;

    @Mock
    protected DynamicReadOnly definition;

    @Before
    public void setup() {
        check = spy(new NoInputNodesInImportedDecisionRule());
        final Node target = mock(Node.class);

        when(definition.isAllowOnlyVisualChange()).thenReturn(true);
        when(content.getDefinition()).thenReturn(definition);
        when(target.getContent()).thenReturn(content);

        when(context.getTarget()).thenReturn(Optional.of(target));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testMissingTargetNodeDoesNotTriggerCheck() {
        final Node source = mock(Node.class);
        when(context.getSource()).thenReturn(Optional.of(source));
        when(context.getTarget()).thenReturn(Optional.empty());

        final RuleViolations result = check.evaluate(rule,
                                                     context);
        assertNotNull(result);
        assertFalse(result.violations().iterator().hasNext());
        verify(check,
               never()).isReadOnly(any(Optional.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testEvaluate() {
        final Node source = mock(Node.class);
        final Node target = mock(Node.class);
        final View content = mock(View.class);
        final DynamicReadOnly dynamicReadonly = mock(DynamicReadOnly.class);
        when(dynamicReadonly.isAllowOnlyVisualChange()).thenReturn(true);
        when(content.getDefinition()).thenReturn(dynamicReadonly);
        when(target.getContent()).thenReturn(content);
        when(context.getSource()).thenReturn(Optional.of(source));
        when(context.getTarget()).thenReturn(Optional.of(target));

        final RuleViolations result = check.evaluate(rule,
                                                     context);
        assertNotNull(result);
        final RuleViolation violation = result.violations().iterator().next();

        assertNotNull(violation);
        assertTrue(violation.getArguments().isPresent());
        assertEquals(1,
                     violation.getArguments().get().length);
        assertEquals(NoInputNodesInImportedDecisionRule.ERROR_MESSAGE,
                     violation.getArguments().get()[0]);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testIsReadOnly() {
        final Node target = mock(Node.class);
        final View content = mock(View.class);
        final DynamicReadOnly dynamicReadonly = mock(DynamicReadOnly.class);
        when(dynamicReadonly.isAllowOnlyVisualChange()).thenReturn(true);
        when(content.getDefinition()).thenReturn(dynamicReadonly);
        when(target.getContent()).thenReturn(content);
        when(context.getTarget()).thenReturn(Optional.of(target));

        final boolean actual = check.isReadOnly(Optional.of(target));

        assertTrue(actual);

        verify(check).isReadOnly(any(Optional.class));
    }

    @Test
    public void testAcceptWhenIsExpectedClass() {
        when(rule.getId()).thenReturn(definition.getClass().getName());
        final boolean actual = check.accepts(rule, context);

        assertTrue(actual);
    }

    @Test
    public void testAcceptWhenIsNotExpectedClass() {
        when(rule.getId()).thenReturn("SomeOtherClass");
        final boolean actual = check.accepts(rule, context);

        assertFalse(actual);
    }
}