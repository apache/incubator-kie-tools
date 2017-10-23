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

package org.drools.workbench.screens.guided.dtable.client.wizard.column.pages;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.drools.workbench.screens.guided.dtable.client.resources.i18n.GuidedDecisionTableErraiConstants;
import org.drools.workbench.screens.guided.dtable.client.wizard.column.pages.common.DecisionTablePopoverUtils;
import org.jboss.errai.common.client.dom.Input;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(GwtMockitoTestRunner.class)
public class AdditionalInfoPageViewTest {

    public static final String HIDE_TRANSLATION = "hide translation";
    public static final String UPDATE_ENGINE_TRANSLATION = "update engine translation";
    public static final String LOGICALLY_INSERT_TRANSLATION = "logically insert translation";
    public static final String HEADER_TRANSLATION = "header translation";
    @Mock
    private Input input;

    @Mock
    private TranslationService translationService;

    @Mock
    private DecisionTablePopoverUtils popoverUtils;

    @Spy
    @InjectMocks
    private AdditionalInfoPageView view;

    @Before
    public void setUp() throws Exception {
        doReturn(HEADER_TRANSLATION)
                .when(translationService)
                .format(GuidedDecisionTableErraiConstants.AdditionalInfoPage_HeaderColumnDescription);

        doReturn(HIDE_TRANSLATION)
                .when(translationService)
                .format(GuidedDecisionTableErraiConstants.AdditionalInfoPage_HideColumnDescription);

        doReturn(UPDATE_ENGINE_TRANSLATION)
                .when(translationService)
                .format(GuidedDecisionTableErraiConstants.AdditionalInfoPage_UpdateEngineDescription);

        doReturn(LOGICALLY_INSERT_TRANSLATION)
                .when(translationService)
                .format(GuidedDecisionTableErraiConstants.AdditionalInfoPage_LogicalInsertDescription);
    }

    @Test
    public void testInitPopovers() throws Exception {
        view.initPopovers();

        verify(input, times(1)).setAttribute("type", "textbox");
        verify(input, times(4)).setAttribute("class", "form-control");
        verify(input, times(3)).setAttribute("type", "checkbox");
        verify(input, times(4)).setAttribute("data-toggle", "popover");
        verify(popoverUtils).setupAndRegisterPopover(eq(input), eq(HEADER_TRANSLATION));
        verify(popoverUtils).setupAndRegisterPopover(eq(input), eq(HIDE_TRANSLATION));
        verify(popoverUtils).setupAndRegisterPopover(eq(input), eq(UPDATE_ENGINE_TRANSLATION));
        verify(popoverUtils).setupAndRegisterPopover(eq(input), eq(LOGICALLY_INSERT_TRANSLATION));
    }
}
