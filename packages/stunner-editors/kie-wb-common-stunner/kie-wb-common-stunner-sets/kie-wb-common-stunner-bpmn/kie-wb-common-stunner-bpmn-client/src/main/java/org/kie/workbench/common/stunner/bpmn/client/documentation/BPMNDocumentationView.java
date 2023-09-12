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


package org.kie.workbench.common.stunner.bpmn.client.documentation;

import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import elemental2.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.stunner.bpmn.documentation.BPMNDocumentationService;
import org.kie.workbench.common.stunner.bpmn.qualifiers.BPMN;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.util.PrintHelper;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.DefaultDiagramDocumentationView;
import org.kie.workbench.common.stunner.core.documentation.model.DocumentationOutput;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.stunner.forms.client.event.FormFieldChanged;
import org.uberfire.client.views.pfly.widgets.Button;

@BPMN
@Dependent
@Templated
public class BPMNDocumentationView extends DefaultDiagramDocumentationView {

    private BPMNDocumentationService documentationService;

    @Inject
    @Named("documentationDiv")
    @DataField
    private HTMLElement documentationDiv;

    @Inject
    @DataField
    private Button printButton;

    private final ClientTranslationService clientTranslationService;

    private final PrintHelper printHelper;

    private Supplier<Boolean> isSelected;

    protected BPMNDocumentationView(final BPMNDocumentationService documentationService,
                                    final ClientTranslationService clientTranslationService,
                                    final PrintHelper printHelper,
                                    final HTMLElement documentationDiv,
                                    final Button printButton) {
        this.documentationService = documentationService;
        this.clientTranslationService = clientTranslationService;
        this.printHelper = printHelper;
        this.documentationDiv = documentationDiv;
        this.printButton = printButton;
    }

    @Inject
    public BPMNDocumentationView(final BPMNDocumentationService documentationService,
                                 final ClientTranslationService clientTranslationService,
                                 final PrintHelper printHelper) {
        this.documentationService = documentationService;
        this.clientTranslationService = clientTranslationService;
        this.printHelper = printHelper;
    }

    @Override
    public BPMNDocumentationView setIsSelected(final Supplier<Boolean> isSelected) {
        this.isSelected = isSelected;
        return this;
    }

    @Override
    public BPMNDocumentationView initialize(Diagram diagram) {
        super.initialize(diagram);

        printButton.setText(clientTranslationService.getValue(CoreTranslationMessages.PRINT));
        printButton.addIcon("pficon-print", "pull-right");
        printButton.setClickHandler(() -> print());

        return refresh();
    }

    void print() {
        printHelper.print(documentationDiv);
    }

    @Override
    public BPMNDocumentationView refresh() {
        documentationDiv.innerHTML = getDocumentationHTML();
        return this;
    }

    protected void onFormFieldChanged(@Observes FormFieldChanged formFieldChanged) {
        Optional.ofNullable(isSelected)
                .map(Supplier::get)
                .filter(Boolean.TRUE::equals)
                .map(focus -> getDiagram()
                        .map(d -> d.getGraph().getNode(formFieldChanged.getUuid()))
                ).ifPresent(focus -> refresh());
    }

    private String getDocumentationHTML() {
        return getDiagram()
                .map(documentationService::generate)
                .map(DocumentationOutput::getValue)
                .orElse("");
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}