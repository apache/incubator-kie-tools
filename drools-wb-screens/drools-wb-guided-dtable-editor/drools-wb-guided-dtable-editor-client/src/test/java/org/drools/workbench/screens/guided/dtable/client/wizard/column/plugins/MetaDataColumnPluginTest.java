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

import org.drools.workbench.models.guided.dtable.shared.model.MetadataCol52;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.MetaDataColumnPage;
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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MetaDataColumnPluginTest {

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private MetaDataColumnPage page;

    @Mock
    private TranslationService translationService;

    @Mock
    private EventSourceMock<WizardPageStatusChangeEvent> changeEvent;

    @InjectMocks
    private MetaDataColumnPlugin plugin = new MetaDataColumnPlugin(page,
                                                                   changeEvent,
                                                                   translationService);

    @Test
    public void testGetTitle() {
        final String errorKey = GuidedDecisionTableErraiConstants.MetaDataColumnPlugin_AddMetadataColumn;
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
    public void testGenerateColumnWhenMetaDataIsValid() throws Exception {
        final String metaData = "metaData";
        final ArgumentCaptor<MetadataCol52> colCaptor = ArgumentCaptor.forClass(MetadataCol52.class);

        plugin.setMetaData(metaData);

        final Boolean success = plugin.generateColumn();

        verify(presenter).appendColumn(colCaptor.capture());

        assertTrue(success);
        assertTrue(colCaptor.getValue().isHideColumn());
        assertEquals(metaData,
                     colCaptor.getValue().getMetadata());
    }

    @Test
    public void testSetMetaData() throws Exception {
        final String metaData = "metaData";

        plugin.setMetaData(metaData);

        assertEquals(metaData,
                     plugin.getMetaData());
        verify(changeEvent).fire(any(WizardPageStatusChangeEvent.class));
    }
}
