/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.models.datamodel.rule.BaseSingleFieldConstraint;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.ConditionColumnPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class CalculationTypePageTest {

    @Mock
    private ConditionColumnPlugin plugin;

    @Mock
    private CalculationTypePage.View view;

    @Mock
    private SimplePanel content;

    @Mock
    private TranslationService translationService;

    @InjectMocks
    private CalculationTypePage page = spy(new CalculationTypePage(view,
                                                                   translationService));

    @BeforeClass
    public static void setupPreferences() {
        // Prevent runtime GWT.create() error at 'content = new SimplePanel()'
        GWTMockUtilities.disarm();
    }

    @Before
    public void setup() {
        when(page.plugin()).thenReturn(plugin);
    }

    @Test
    public void testGetConstraintValue() throws Exception {
        page.getConstraintValue();

        verify(plugin).constraintValue();
    }

    @Test
    public void testSetConstraintValue() throws Exception {
        page.setConstraintValue(BaseSingleFieldConstraint.TYPE_LITERAL);

        verify(plugin).setConstraintValue(BaseSingleFieldConstraint.TYPE_LITERAL);
    }

    @Test
    public void testIsCompleteWhenItIsCompleted() throws Exception {
        when(plugin.constraintValue()).thenReturn(BaseSingleFieldConstraint.TYPE_LITERAL);

        page.isComplete(Assert::assertTrue);
    }

    @Test
    public void testIsCompleteWhenItIsNotCompleted() throws Exception {
        when(plugin.constraintValue()).thenReturn(BaseSingleFieldConstraint.TYPE_UNDEFINED);

        page.isComplete(Assert::assertFalse);
    }

    @Test
    public void testGetTitle() throws Exception {
        final String errorKey = GuidedDecisionTableErraiConstants.CalculationTypePage_CalculationType;
        final String errorMessage = "Title";

        when(translationService.format(errorKey)).thenReturn(errorMessage);

        final String title = page.getTitle();

        assertEquals(errorMessage,
                     title);
    }

    @Test
    public void testPrepareView() throws Exception {
        page.prepareView();

        verify(view).init(page);
    }

    @Test
    public void testAsWidget() {
        final Widget contentWidget = page.asWidget();

        assertEquals(contentWidget,
                     content);
    }
}
