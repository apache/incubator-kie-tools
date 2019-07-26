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

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.event.dom.client.ClickEvent;
import elemental2.dom.HTMLButtonElement;
import elemental2.dom.HTMLDivElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.dmn.api.qualifiers.DMNEditor;
import org.kie.workbench.common.dmn.client.editors.documentation.common.DMNDocumentationService;
import org.kie.workbench.common.dmn.client.editors.documentation.common.HTMLDownloadHelper;
import org.kie.workbench.common.stunner.core.client.util.PrintHelper;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.DefaultDiagramDocumentationView;
import org.kie.workbench.common.stunner.core.documentation.DocumentationView;
import org.kie.workbench.common.stunner.core.documentation.model.DocumentationOutput;

import static org.kie.workbench.common.stunner.core.documentation.model.DocumentationOutput.EMPTY;

@DMNEditor
@Dependent
@Templated
public class DMNDocumentationView extends DefaultDiagramDocumentationView {

    static final String DOCUMENTATION_FILENAME = "Documentation";

    @DataField("documentation-panel")
    private final HTMLDivElement documentationPanel;

    @DataField("documentation-content")
    private final HTMLDivElement documentationContent;

    @DataField("print-button")
    private final HTMLButtonElement printButton;

    @DataField("download-html-file")
    private final HTMLButtonElement downloadHtmlFile;

    private final PrintHelper printHelper;

    private final DMNDocumentationService documentationService;

    private final HTMLDownloadHelper downloadHelper;

    @Inject
    public DMNDocumentationView(final HTMLDivElement documentationPanel,
                                final HTMLDivElement documentationContent,
                                final HTMLButtonElement printButton,
                                final HTMLButtonElement downloadHtmlFile,
                                final PrintHelper printHelper,
                                final DMNDocumentationService documentationService,
                                final HTMLDownloadHelper downloadHelper) {
        this.documentationPanel = documentationPanel;
        this.documentationContent = documentationContent;
        this.printButton = printButton;
        this.downloadHtmlFile = downloadHtmlFile;
        this.printHelper = printHelper;
        this.documentationService = documentationService;
        this.downloadHelper = downloadHelper;
    }

    @Override
    public DocumentationView<Diagram> refresh() {
        documentationContent.innerHTML = getDocumentationHTML();
        return this;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @EventHandler("print-button")
    public void onPrintButtonClick(final ClickEvent e) {
        printHelper.print(documentationContent);
    }

    @EventHandler("download-html-file")
    public void onDownloadHtmlFile(final ClickEvent e) {
        final String html = getCurrentDocumentationHTML();
        downloadHelper.download(getCurrentDocumentationFilename(), html);
    }

    String getCurrentDocumentationHTML() {
        return documentationContent.innerHTML;
    }

    private String getDocumentationHTML() {
        return getDiagram()
                .map(documentationService::generate)
                .map(DocumentationOutput::getValue)
                .orElse(EMPTY.getValue());
    }

    String getCurrentDocumentationFilename() {
        return getDiagram()
                .map(diagram -> documentationService.processDocumentation(diagram))
                .map(dmnDocumentation -> DOCUMENTATION_FILENAME + "-" + dmnDocumentation.getFileName())
                .orElse(DOCUMENTATION_FILENAME);
    }
}
