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

package org.kie.workbench.common.dmn.client.editors.documentation;

import java.util.Optional;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.DOMTokenList;
import elemental2.dom.DomGlobal;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationService;
import org.kie.workbench.common.dmn.client.editors.types.common.HiddenHelper;
import org.kie.workbench.common.stunner.core.client.util.PrintHelper;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.model.DocumentationOutput;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.documentation.model.DocumentationOutput.EMPTY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDocumentationViewTest {

    @Mock
    private HTMLDivElement documentationContent;

    @Mock
    private HTMLButtonElement printButton;

    @Mock
    private PrintHelper printHelper;

    @Mock
    private DMNDocumentationService documentationService;

    @Mock
    private Diagram diagram;

    @Mock
    private DMNDocumentationViewButtonsVisibilitySupplier buttonsVisibilitySupplier;

    @Mock
    private DOMTokenList printButtonClassList;

    @Mock
    private DOMTokenList downloadButtonClassList;

    @Mock
    private HTMLButtonElement downloadHtmlFileButton;

    @Captor
    private ArgumentCaptor<DomGlobal.SetTimeoutCallbackFn> callback;

    private DMNDocumentationView view;

    @Before
    public void setup() {
        printButton.classList = printButtonClassList;
        downloadHtmlFileButton.classList = downloadButtonClassList;
        view = spy(new DMNDocumentationView(documentationContent, printButton, downloadHtmlFileButton, printHelper, documentationService, buttonsVisibilitySupplier));
    }

    @Test
    public void testRefresh() {

        doNothing().when(view).setTimeout(any(), anyInt());
        when(buttonsVisibilitySupplier.isButtonsVisible()).thenReturn(true);

        view.refresh();

        verify(downloadButtonClassList, never()).add(HiddenHelper.HIDDEN_CSS_CLASS);
        verify(printButtonClassList, never()).add(HiddenHelper.HIDDEN_CSS_CLASS);

        verify(buttonsVisibilitySupplier).isButtonsVisible();
        verify(view).refreshDocumentationHTML();
        verify(view).refreshDocumentationHTMLAfter200ms();
    }

    @Test
    public void testRefreshWhenButtonsAreNotVisible() {

        doNothing().when(view).setTimeout(any(), anyInt());
        when(buttonsVisibilitySupplier.isButtonsVisible()).thenReturn(false);

        view.refresh();

        verify(downloadButtonClassList, never()).add(HiddenHelper.HIDDEN_CSS_CLASS);
        verify(printButtonClassList).add(HiddenHelper.HIDDEN_CSS_CLASS);
        verify(buttonsVisibilitySupplier).isButtonsVisible();
        verify(view).refreshDocumentationHTML();
        verify(view).refreshDocumentationHTMLAfter200ms();
    }

    @Test
    public void testRefreshDocumentationHTMLWhenDiagramIsPresent() {

        final String expectedHTML = "<html />";
        final DocumentationOutput output = new DocumentationOutput(expectedHTML);

        doReturn(Optional.of(diagram)).when(view).getDiagram();
        when(documentationService.generate(diagram)).thenReturn(output);

        documentationContent.innerHTML = "something";

        view.refreshDocumentationHTML();

        final String actualHTML = documentationContent.innerHTML;

        assertEquals(expectedHTML, actualHTML);
    }

    @Test
    public void testRefreshDocumentationHTMLWhenDiagramIsNotPresent() {

        final String expectedHTML = EMPTY.getValue();

        doReturn(Optional.empty()).when(view).getDiagram();

        documentationContent.innerHTML = "something";

        view.refreshDocumentationHTML();

        final String actualHTML = documentationContent.innerHTML;

        assertEquals(expectedHTML, actualHTML);
    }

    @Test
    public void testRefreshDocumentationHTMLAfter200ms() {

        doNothing().when(view).setTimeout(any(), anyInt());

        view.refreshDocumentationHTMLAfter200ms();

        verify(view).setTimeout(callback.capture(), eq(200));
        callback.getValue().onInvoke(new Object());
        verify(view).refreshDocumentationHTML();
    }

    @Test
    public void testIsEnabled() {
        assertTrue(view.isEnabled());
    }

    @Test
    public void testOnPrintButtonClick() {
        view.onPrintButtonClick(mock(ClickEvent.class));
        verify(printHelper).print(documentationContent);
    }

    @Test
    public void testOnDownloadHtmlFile() {
        final String html = "<html><body>Hi</body></html>";
        final String modelName = "model name";
        doReturn(modelName).when(view).getCurrentDocumentationModelName();
        doReturn(html).when(view).getCurrentDocumentationHTML();

        view.onDownloadHtmlFile(mock(ClickEvent.class));

        verify(view).getCurrentDocumentationHTML();
        verify(view).getCurrentDocumentationModelName();
    }
}
