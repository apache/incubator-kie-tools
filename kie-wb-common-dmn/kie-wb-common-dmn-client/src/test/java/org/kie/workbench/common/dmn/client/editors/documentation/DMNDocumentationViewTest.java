/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.documentation;

import java.util.Optional;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwtmockito.GwtMockitoTestRunner;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationService;
import org.kie.workbench.common.stunner.core.client.util.PrintHelper;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.model.DocumentationOutput;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.kie.workbench.common.stunner.core.documentation.model.DocumentationOutput.EMPTY;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class DMNDocumentationViewTest {

    @Mock
    private HTMLDivElement documentationPanel;

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

    private DMNDocumentationView view;

    @Before
    public void setup() {
        view = spy(new DMNDocumentationView(documentationPanel, documentationContent, printButton, printHelper, documentationService));
    }

    @Test
    public void testRefreshWhenDiagramIsPresent() {

        final String expectedHTML = "<html />";
        final DocumentationOutput output = new DocumentationOutput(expectedHTML);

        doReturn(Optional.of(diagram)).when(view).getDiagram();
        when(documentationService.generate(diagram)).thenReturn(output);

        documentationContent.innerHTML = "something";

        view.refresh();

        final String actualHTML = documentationContent.innerHTML;

        assertEquals(expectedHTML, actualHTML);
    }

    @Test
    public void testRefreshWhenDiagramIsNotPresent() {

        final String expectedHTML = EMPTY.getValue();

        doReturn(Optional.empty()).when(view).getDiagram();

        documentationContent.innerHTML = "something";

        view.refresh();

        final String actualHTML = documentationContent.innerHTML;

        assertEquals(expectedHTML, actualHTML);
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
}
