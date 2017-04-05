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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.widget.table.GuidedDecisionTableView;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.plugins.MetaDataColumnPlugin;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(GwtMockitoTestRunner.class)
public class MetaDataColumnPageTest {

    @Mock
    private GuidedDecisionTableView.Presenter presenter;

    @Mock
    private MetaDataColumnPlugin plugin;

    @Mock
    private TranslationService translationService;

    @Mock
    private MetaDataColumnPage.View view;

    @Mock
    private SimplePanel content;

    @InjectMocks
    private MetaDataColumnPage page = new MetaDataColumnPage(view,
                                                             translationService);

    @BeforeClass
    public static void setupPreferences() {
        final Map<String, String> preferences = new HashMap<String, String>() {{
            put(ApplicationPreferences.DATE_FORMAT,
                "dd/mm/yyyy");
        }};
        ApplicationPreferences.setUp(preferences);

        // Prevent runtime GWT.create() error at 'content = new SimplePanel()'
        GWTMockUtilities.disarm();
    }

    @Test
    public void testIsCompleteWhenMetaDataIsNull() throws Exception {
        when(plugin.getMetaData()).thenReturn(null);

        page.isComplete(Assert::assertFalse);
    }

    @Test
    public void testIsCompleteWhenMetaDataIsBlank() throws Exception {
        when(plugin.getMetaData()).thenReturn("");

        page.isComplete(Assert::assertFalse);
    }

    @Test
    public void testIsCompleteWhenMetaDataIsNotNull() throws Exception {
        when(plugin.getMetaData()).thenReturn("metaData");

        page.isComplete(Assert::assertTrue);
    }

    @Test
    public void testGetMetadata() throws Exception {
        page.getMetadata();

        verify(plugin).getMetaData();
    }

    @Test
    public void testEmptyMetadataError() throws Exception {
        page.emptyMetadataError();

        verify(view).showError(any());
        verify(translationService).format(GuidedDecisionTableErraiConstants.MetaDataColumnPage_MetadataNameEmpty);
    }

    @Test
    public void testColumnNameIsAlreadyInUseError() throws Exception {
        page.columnNameIsAlreadyInUseError();

        verify(view).showError(any());
        verify(translationService).format(GuidedDecisionTableErraiConstants.MetaDataColumnPage_ThatColumnNameIsAlreadyInUsePleasePickAnother);
    }

    @Test
    public void testSetMetadata() throws Exception {
        final String metaData = "metaData";

        page.setMetadata(metaData);

        verify(plugin).setMetaData(metaData);
    }

    @Test
    public void testGetTitle() throws Exception {
        final String errorKey = GuidedDecisionTableErraiConstants.MetaDataColumnPage_AddNewMetadata;
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
