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

package org.drools.workbench.screens.guided.rule.client.widget.attribute;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import com.google.gwtmockito.WithClassesToStub;
import org.drools.workbench.models.datamodel.rule.RuleAttribute;
import org.drools.workbench.models.datamodel.rule.RuleModel;
import org.drools.workbench.screens.guided.rule.client.editor.RuleModeller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.widgets.client.widget.LiteralTextBox;
import org.mockito.Mock;
import org.uberfire.ext.widgets.common.client.common.DatePicker;
import org.uberfire.ext.widgets.common.client.common.DirtyableHorizontalPane;
import org.uberfire.ext.widgets.common.client.common.FormStyleLayout;
import org.uberfire.ext.widgets.common.client.common.NumericIntegerTextBox;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
@WithClassesToStub({DateTimeFormat.class, DatePicker.class})
public class RuleAttributeWidgetTest {

    @Mock
    private FormStyleLayout layout;

    @Mock
    private DirtyableHorizontalPane dirtyableHorizontalPane;

    @Mock
    private RuleModeller ruleModeller;

    private RuleModel ruleModel;

    private boolean isReadOnly = false;

    private RuleAttributeWidget ruleAttributeWidget;

    @Before
    public void setUp() throws Exception {
        GwtMockito.useProviderForType(FormStyleLayout.class,
                                      aClass -> layout);
        GwtMockito.useProviderForType(DirtyableHorizontalPane.class,
                                      aClass -> dirtyableHorizontalPane);

        ruleModel = new RuleModel();
    }

    @Test
    public void testNoAttribute() {
        ruleAttributeWidget = new RuleAttributeWidget(ruleModeller,
                                                      ruleModel,
                                                      isReadOnly);

        verify(layout, never()).addAttribute(anyString(),
                                             any(IsWidget.class));
    }

    @Test
    public void testCalendarAttribute() {
        ruleModel.addAttribute(new RuleAttribute(RuleAttributeWidget.CALENDARS_ATTR, ""));

        ruleAttributeWidget = new RuleAttributeWidget(ruleModeller,
                                                      ruleModel,
                                                      isReadOnly);

        verify(layout).addAttribute(eq(RuleAttributeWidget.CALENDARS_ATTR),
                                    eq(dirtyableHorizontalPane));
        verify(dirtyableHorizontalPane).add(isA(LiteralTextBox.class));
    }

    @Test
    public void testTimerAttribute() {
        ruleModel.addAttribute(new RuleAttribute(RuleAttributeWidget.TIMER_ATTR, ""));

        ruleAttributeWidget = new RuleAttributeWidget(ruleModeller,
                                                      ruleModel,
                                                      isReadOnly);

        verify(layout).addAttribute(eq(RuleAttributeWidget.TIMER_ATTR),
                                    eq(dirtyableHorizontalPane));
        verify(dirtyableHorizontalPane).add(isA(LiteralTextBox.class));
    }

    @Test
    public void testSalienceAttribute() {
        ruleModel.addAttribute(new RuleAttribute(RuleAttributeWidget.SALIENCE_ATTR, ""));

        ruleAttributeWidget = new RuleAttributeWidget(ruleModeller,
                                                      ruleModel,
                                                      isReadOnly);

        verify(layout).addAttribute(eq(RuleAttributeWidget.SALIENCE_ATTR),
                                    eq(dirtyableHorizontalPane));
        verify(dirtyableHorizontalPane).add(isA(NumericIntegerTextBox.class));
    }

    @Test
    public void testDateEffectiveAttribute() {
        ruleModel.addAttribute(new RuleAttribute(RuleAttributeWidget.DATE_EFFECTIVE_ATTR, ""));

        ruleAttributeWidget = new RuleAttributeWidget(ruleModeller,
                                                      ruleModel,
                                                      isReadOnly);

        verify(layout).addAttribute(eq(RuleAttributeWidget.DATE_EFFECTIVE_ATTR),
                                    eq(dirtyableHorizontalPane));
        verify(dirtyableHorizontalPane).add(isA(DatePicker.class));
    }

    @Test
    public void testDateExpiresAttribute() {
        ruleModel.addAttribute(new RuleAttribute(RuleAttributeWidget.DATE_EXPIRES_ATTR, ""));

        ruleAttributeWidget = new RuleAttributeWidget(ruleModeller,
                                                      ruleModel,
                                                      isReadOnly);

        verify(layout).addAttribute(eq(RuleAttributeWidget.DATE_EXPIRES_ATTR),
                                    eq(dirtyableHorizontalPane));
        verify(dirtyableHorizontalPane).add(isA(DatePicker.class));
    }
}
