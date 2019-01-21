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
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.documentation.DefaultDiagramDocumentationView;
import org.kie.workbench.common.stunner.core.documentation.model.DocumentationOutput;
import org.kie.workbench.common.stunner.core.i18n.CoreTranslationMessages;
import org.kie.workbench.common.stunner.forms.client.event.FormFieldChanged;
import org.uberfire.client.views.pfly.icon.PatternFlyIconType;
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

    private Supplier<Boolean> isSelected;

    @Inject
    public BPMNDocumentationView(final BPMNDocumentationService documentationService,
                                 final ClientTranslationService clientTranslationService) {
        this.documentationService = documentationService;
        this.clientTranslationService = clientTranslationService;
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
        printButton.addIcon(PatternFlyIconType.PRINT.getCssName(), "pull-right");
        printButton.setClickHandler(() -> print());

        return refresh();
    }

    /**
     * Native helper method to print a div content
     * @param
     * @return
     */
    private native boolean print(HTMLElement element)/*-{
        var content = element.innerHTML;
        var printWindow = window.open('', '_blank');
        var doc = printWindow.document;

        // ready for writing
        doc.open();
        doc.write(content);
        doc.close();

        //trick part, change the media attribute to all, to load it before printing
        //otherwise it is printing before loading the style
        var links = doc.getElementsByTagName("link");
        for (var i = 0, max = links.length; i < max; i++) {
            var css = links[i];
            var media = css.attributes["media"];
            if (media && media.value === "print") {
                media.value = "all";
            }
        }

        //copy the styles from the top window
        if (window.top && window.top.location.href != document.location.href) {
            var parentStyles = window.top.document.getElementsByTagName('style');
            var docHead = doc.getElementsByTagName('head').item(0);
            for (var i = 0, max = parentStyles.length; i < max; i++) {
                if (parentStyles[i]) {
                    var copiedStyle = document.createElement('style');
                    var attrib = parentStyles[i].innerHTML;
                    copiedStyle.innerHTML = attrib;
                    docHead.appendChild(copiedStyle);
                }
            }
        }

        //printing after all resources are loaded on the new window
        printWindow.onload = function () {
            printWindow.focus();
            //trick to avoid printing before loading the styles
            setTimeout(function () {
                printWindow.print();
                printWindow.close();
            }, 10);
        };

        return true;
    }-*/;

    private void print() {
        print(documentationDiv);
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