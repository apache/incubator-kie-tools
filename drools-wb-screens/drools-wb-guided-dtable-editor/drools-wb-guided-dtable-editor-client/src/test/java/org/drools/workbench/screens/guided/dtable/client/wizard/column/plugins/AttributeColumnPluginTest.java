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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins;

import org.drools.workbench.models.datamodel.rule.Attribute;
import org.drools.workbench.models.guided.dtable.shared.model.AttributeCol52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.AttributeColumnPage;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.widgets.core.client.wizards.WizardPageStatusChangeEvent;
import org.uberfire.mocks.EventSourceMock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AttributeColumnPluginTest {

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private AttributeColumnPage page;

    @Mock
    private TranslationService translationService;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> changeEvent;

    @InjectMocks
    private AttributeColumnPlugin plugin = new AttributeColumnPlugin(page,
                                                                     changeEvent,
                                                                     translationService);

    @Test
    public void testGetTitle() {
        final String errorKey = GuidedDecisionTableErraiConstants.AttributeColumnPlugin_AddAttributeColumn;
        final String errorMessage = "Title";

        when(translationService.format(errorKey)).thenReturn(errorMessage);

        final String title = plugin.getTitle();

        assertEquals(errorMessage,
                     title);
    }

    @Test
    public void testGetPages() throws Exception {
        assertEquals(1,
                     plugin.getPages().size());
    }

    @Test
    public void testGenerateColumn() throws Exception {
        final String attributeName = Attribute.DIALECT.getAttributeName();
        final ArgumentCaptor<AttributeCol52> colCaptor = ArgumentCaptor.forClass(AttributeCol52.class);

        plugin.setAttribute(Attribute.DIALECT.getAttributeName());

        final Boolean success = plugin.generateColumn();

        verify(presenter).appendColumn(colCaptor.capture());

        assertTrue(success);
        assertEquals(attributeName,
                     colCaptor.getValue().getAttribute());
    }

    @Test
    public void testSetAttribute() throws Exception {
        final String attribute = Attribute.DIALECT.getAttributeName();

        plugin.setAttribute(attribute);

        assertEquals(attribute,
                     plugin.getAttribute());
        verify(changeEvent).fire(any(WizardPageStatusChangeEvent.class));
    }
}
