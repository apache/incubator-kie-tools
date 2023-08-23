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

package org.kie.workbench.common.dmn.client.editors.documentation.links;

import java.util.Optional;
import java.util.function.Consumer;

import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLElement;
import elemental2.dom.HTMLInputElement;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.api.property.dmn.DMNExternalLink;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.uberfire.client.views.pfly.widgets.JQueryProducer;
import org.uberfire.client.views.pfly.widgets.Popover;

import static org.junit.Assert.assertEquals;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_Name;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_NamePlaceholder;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_URL;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_URLPlaceholder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class NameAndUrlPopoverViewImplTest {

    @Mock
    private Div popoverElement;

    @Mock
    private Div popoverContentElement;

    @Mock
    private JQueryProducer.JQuery<Popover> jQueryPopover;

    @Mock
    private TranslationService translationService;

    @Mock
    private HTMLButtonElement cancelButton;

    @Mock
    private HTMLButtonElement okButton;

    @Mock
    private HTMLInputElement urlInput;

    @Mock
    private HTMLInputElement attachmentNameInput;

    @Mock
    private HTMLElement urlLabel;

    @Mock
    private HTMLElement attachmentName;

    @Mock
    private HTMLElement attachmentTip;

    private NameAndUrlPopoverViewImpl popover;

    @Before
    public void setup() {
        popover = spy(new NameAndUrlPopoverViewImpl(popoverElement,
                                                    popoverContentElement,
                                                    jQueryPopover,
                                                    translationService,
                                                    cancelButton,
                                                    okButton,
                                                    urlInput,
                                                    attachmentNameInput,
                                                    urlLabel,
                                                    attachmentName,
                                                    attachmentTip));
    }

    @Test
    public void testInit() {

        final String url = "url";
        final String name = "name";
        final String urlPlaceholder = "urlPlaceholder";
        final String namePlaceholder = "namePlaceholder";

        when(translationService.getTranslation(DMNDocumentationI18n_URL)).thenReturn(url);
        when(translationService.getTranslation(DMNDocumentationI18n_Name)).thenReturn(name);
        when(translationService.getTranslation(DMNDocumentationI18n_URLPlaceholder)).thenReturn(urlPlaceholder);
        when(translationService.getTranslation(DMNDocumentationI18n_NamePlaceholder)).thenReturn(namePlaceholder);

        popover.init();

        assertEquals(url, urlLabel.textContent);
        assertEquals(name, attachmentName.textContent);
        assertEquals(urlPlaceholder, urlInput.placeholder);
        assertEquals(namePlaceholder, attachmentNameInput.placeholder);
    }

    @Test
    public void testOnClickOkButton() {

        final String description = "description";
        final String url = "url";
        final ArgumentCaptor<DMNExternalLink> captor = ArgumentCaptor.forClass(DMNExternalLink.class);
        final Consumer onExternalLinkCreated = mock(Consumer.class);
        attachmentNameInput.value = description;
        urlInput.value = url;

        popover.setOnExternalLinkCreated(onExternalLinkCreated);

        popover.onClickOkButton(null);

        verify(onExternalLinkCreated).accept(captor.capture());

        final DMNExternalLink externalLink = captor.getValue();

        assertEquals(description, externalLink.getDescription());
        assertEquals(url, externalLink.getUrl());

        verify(popover).hide();
    }

    @Test
    public void testOnClickCancelButton() {

        popover.onClickCancelButton(null);
        verify(popover).hide();
    }

    @Test
    public void testShow() {

        doNothing().when(popover).superShow(any());

        popover.show(Optional.of(""));
        verify(popover).clear();
    }

    @Test
    public void testClear() {

        attachmentNameInput.value = "old";
        urlInput.value = "old value";

        popover.clear();

        assertEquals("", attachmentNameInput.value);
        assertEquals("", urlInput.value);
    }
}