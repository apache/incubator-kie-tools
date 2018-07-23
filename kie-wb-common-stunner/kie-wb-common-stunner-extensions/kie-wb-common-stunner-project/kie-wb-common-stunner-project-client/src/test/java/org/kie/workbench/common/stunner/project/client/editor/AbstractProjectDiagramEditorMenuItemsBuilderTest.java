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
package org.kie.workbench.common.stunner.project.client.editor;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwtmockito.GwtMockito;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.gwtbootstrap3.client.ui.AnchorListItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.popups.PopupUtil;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.uberfire.mvp.Command;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class AbstractProjectDiagramEditorMenuItemsBuilderTest {

    private static final String EXPORT_RAW = "export";

    @Mock
    private Command exportPNGCommand;

    @Mock
    private Command exportJPGCommand;

    @Mock
    private Command exportSVGCommand;

    @Mock
    private Command exportPDFCommand;

    @Mock
    private Command exportAsRawCommand;

    @Mock
    private ClientTranslationService translationService;

    @Mock
    private PopupUtil popupUtil;

    @Mock
    private AnchorListItem listItem;

    @Mock
    private ClickEvent clickEvent;

    @Captor
    private ArgumentCaptor<String> listItemTextCaptor;

    @Captor
    private ArgumentCaptor<String> listItemTitleCaptor;

    @Captor
    private ArgumentCaptor<ClickHandler> listItemClickHandlerCaptor;

    private AbstractProjectDiagramEditorMenuItemsBuilder menuItemsBuilder;

    @Before
    public void setup() {
        GwtMockito.useProviderForType(AnchorListItem.class, aClass -> listItem);

        this.menuItemsBuilder = new AbstractProjectDiagramEditorMenuItemsBuilder(translationService, popupUtil) {
            @Override
            protected String getExportAsRawLabel() {
                return EXPORT_RAW;
            }
        };

        when(translationService.getValue(anyString())).thenAnswer(i -> i.getArguments()[0].toString());
    }

    @Test
    public void testExportsItem() {
        menuItemsBuilder.newExportsItem(exportPNGCommand,
                                        exportJPGCommand,
                                        exportSVGCommand,
                                        exportPDFCommand,
                                        exportAsRawCommand);

        verify(listItem,
               times(5)).setText(listItemTextCaptor.capture());
        final List<String> listItemText = listItemTextCaptor.getAllValues();
        assertEquals(5,
                     listItemText.size());
        assertEquals(CoreTranslationMessages.EXPORT_PNG,
                     listItemText.get(0));
        assertEquals(CoreTranslationMessages.EXPORT_JPG,
                     listItemText.get(1));
        assertEquals(CoreTranslationMessages.EXPORT_SVG,
                     listItemText.get(2));
        assertEquals(CoreTranslationMessages.EXPORT_PDF,
                     listItemText.get(3));
        assertEquals(EXPORT_RAW,
                     listItemText.get(4));

        verify(listItem,
               times(5)).setTitle(listItemTitleCaptor.capture());
        final List<String> listItemTitle = listItemTitleCaptor.getAllValues();
        assertEquals(5,
                     listItemTitle.size());
        assertEquals(CoreTranslationMessages.EXPORT_PNG,
                     listItemTitle.get(0));
        assertEquals(CoreTranslationMessages.EXPORT_JPG,
                     listItemTitle.get(1));
        assertEquals(CoreTranslationMessages.EXPORT_SVG,
                     listItemTitle.get(2));
        assertEquals(CoreTranslationMessages.EXPORT_PDF,
                     listItemTitle.get(3));
        assertEquals(EXPORT_RAW,
                     listItemTitle.get(4));

        verify(listItem,
               times(5)).addClickHandler(listItemClickHandlerCaptor.capture());
        final List<ClickHandler> listItemClickHandler = listItemClickHandlerCaptor.getAllValues();
        assertEquals(5,
                     listItemClickHandler.size());
        listItemClickHandler.get(0).onClick(clickEvent);
        verify(exportPNGCommand).execute();
        listItemClickHandler.get(1).onClick(clickEvent);
        verify(exportJPGCommand).execute();
        listItemClickHandler.get(2).onClick(clickEvent);
        verify(exportSVGCommand).execute();
        listItemClickHandler.get(3).onClick(clickEvent);
        verify(exportPDFCommand).execute();
        listItemClickHandler.get(4).onClick(clickEvent);
        verify(exportAsRawCommand).execute();
    }
}
