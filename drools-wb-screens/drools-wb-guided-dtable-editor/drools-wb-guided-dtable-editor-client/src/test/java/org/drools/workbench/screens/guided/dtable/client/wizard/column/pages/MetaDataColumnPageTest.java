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
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.shared.preferences.ApplicationPreferences;
import org.kie.workbench.common.services.shared.validation.ValidationService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.mocks.CallerMock;

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
    
    private Caller<ValidationService> validationServiceCaller;

    @Mock
    private ValidationService validationService;

    @Mock
    private MetaDataColumnPage.View view;

    @Mock
    private SimplePanel content;

    @InjectMocks
    private MetaDataColumnPage page = new MetaDataColumnPage(view,
                                                             translationService,
                                                             validationServiceCaller);

    private static Map<String, Boolean> validationResult;

    @BeforeClass
    public static void setupPreferences() {
        final Map<String, String> preferences = new HashMap<String, String>() {{
            put(ApplicationPreferences.DATE_FORMAT,
                "dd/mm/yyyy");
        }};
        ApplicationPreferences.setUp(preferences);

        // Prevent runtime GWT.create() error at 'content = new SimplePanel()'
        GWTMockUtilities.disarm();

        validationResult = new HashMap<>();
        validationResult.put("metaData", true);
        validationResult.put("*", false);
    }

    @Before
    public void setUp() throws Exception {
        when(translationService.format(GuidedDecisionTableErraiConstants.MetaDataColumnPage_MetadataNameEmpty))
                .thenReturn("empty");
        when(translationService.format(GuidedDecisionTableErraiConstants.MetaDataColumnPage_ThatColumnNameIsAlreadyInUsePleasePickAnother))
                .thenReturn("alreadyUsed");
        when(translationService.format(GuidedDecisionTableErraiConstants.MetaDataColumnPage_IsNotValidIdentifier))
                .thenReturn("isNotValid");

        validationServiceCaller = new CallerMock<>(validationService);
        page.validationService = validationServiceCaller;
        when(validationService.evaluateJavaIdentifiers(any())).thenReturn(validationResult);
    }

    @Test
    public void testIsCompleteWhenMetaDataIsNull() throws Exception {
        when(plugin.getMetaData()).thenReturn(null);

        page.isComplete(Assert::assertFalse);
        verify(view).showError("empty");
        verify(view, never()).hideError();
    }

    @Test
    public void testIsCompleteWhenMetaDataIsBlank() throws Exception {
        when(plugin.getMetaData()).thenReturn("");

        page.isComplete(Assert::assertFalse);
        verify(view).showError("empty");
        verify(view, never()).hideError();
    }

    @Test
    public void testIsCompleteWhenMetaDataIsNotNull() throws Exception {
        when(plugin.getMetaData()).thenReturn("metaData");
        when(presenter.isMetaDataUnique("metaData")).thenReturn(true);

        page.isComplete(Assert::assertTrue);

        verify(view, never()).showError(anyString());
        verify(view).hideError();
    }

    @Test
    public void testGetMetadata() throws Exception {
        page.getMetadata();

        verify(plugin).getMetaData();
    }

    @Test
    public void testColumnNameIsAlreadyInUseError() throws Exception {
        when(plugin.getMetaData()).thenReturn("metaData");
        when(presenter.isMetaDataUnique("metaData")).thenReturn(false);

        page.isComplete(Assert::assertFalse);
        verify(view).showError("alreadyUsed");
        verify(view, never()).hideError();
    }

    @Test
    public void testShowJustOneErrorEmptyAndNotUnique() throws Exception {
        when(plugin.getMetaData()).thenReturn("");
        when(presenter.isMetaDataUnique("")).thenReturn(false);

        page.isComplete(Assert::assertFalse);
        verify(view).showError("empty");
        verify(view, never()).showError("isNotValid");
        verify(view, never()).showError("alreadyUsed");
        verify(view, never()).hideError();
    }

    @Test
    public void testShowJustOneErrorNotEmptyNotUnique() throws Exception {
        when(plugin.getMetaData()).thenReturn("a");
        when(presenter.isMetaDataUnique("a")).thenReturn(false);

        page.isComplete(Assert::assertFalse);
        verify(view, never()).showError("empty");
        verify(view, never()).showError("isNotValid");
        verify(view).showError("alreadyUsed");
        verify(view, never()).hideError();
    }

    @Test
    public void testInvalidIdentifier() throws Exception {
        when(plugin.getMetaData()).thenReturn("*");
        when(presenter.isMetaDataUnique("*")).thenReturn(true);

        page.isComplete(Assert::assertFalse);
        
        verify(view).showError("isNotValid");
        verify(view, never()).hideError();
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
