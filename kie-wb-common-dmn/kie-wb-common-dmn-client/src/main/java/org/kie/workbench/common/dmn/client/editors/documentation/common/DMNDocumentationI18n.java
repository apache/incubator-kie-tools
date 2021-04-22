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

package org.kie.workbench.common.dmn.client.editors.documentation.common;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.jboss.errai.ui.client.local.spi.TranslationService;

import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_DataTypes;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_DiagramDoesNotHaveElements;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_DmnModel;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_DmnModelDocumentation;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_DrdComponents;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_ExternalLinks;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_GeneratedBy;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_GeneratedFrom;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_GeneratedOn;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_Namespace;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_NoDRDs;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_NoDataTypes;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_NoExternalLinks;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationI18n_TableOfContents;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class DMNDocumentationI18n {

    private String generatedOn;

    private String generatedBy;

    private String generatedFrom;

    private String namespace;

    private String dmnModelDocumentation;

    private String tableOfContents;

    private String dmnModel;

    private String dataTypes;

    private String drdComponents;

    private String noDataTypes;

    private String noDRDs;

    private String diagramDoesNotHaveElements;

    private String noExternalLinks;

    private String externalLinks;

    @JsOverlay
    public static DMNDocumentationI18n create(final TranslationService translationService) {

        final DMNDocumentationI18n i18n = new DMNDocumentationI18n();

        i18n.dmnModelDocumentation = translationService.format(DMNDocumentationI18n_DmnModelDocumentation);
        i18n.generatedOn = translationService.format(DMNDocumentationI18n_GeneratedOn);
        i18n.generatedBy = translationService.format(DMNDocumentationI18n_GeneratedBy);
        i18n.generatedFrom = translationService.format(DMNDocumentationI18n_GeneratedFrom);
        i18n.namespace = translationService.format(DMNDocumentationI18n_Namespace);
        i18n.tableOfContents = translationService.format(DMNDocumentationI18n_TableOfContents);
        i18n.dmnModel = translationService.format(DMNDocumentationI18n_DmnModel);
        i18n.dataTypes = translationService.format(DMNDocumentationI18n_DataTypes);
        i18n.drdComponents = translationService.format(DMNDocumentationI18n_DrdComponents);
        i18n.noDataTypes = translationService.format(DMNDocumentationI18n_NoDataTypes);
        i18n.noDRDs = translationService.format(DMNDocumentationI18n_NoDRDs);
        i18n.diagramDoesNotHaveElements = translationService.format(DMNDocumentationI18n_DiagramDoesNotHaveElements);
        i18n.noExternalLinks = translationService.format(DMNDocumentationI18n_NoExternalLinks);
        i18n.externalLinks = translationService.format(DMNDocumentationI18n_ExternalLinks);

        return i18n;
    }

    @JsOverlay
    public final String getGeneratedOn() {
        return generatedOn;
    }

    @JsOverlay
    public final String getGeneratedBy() {
        return generatedBy;
    }

    @JsOverlay
    public final String getGeneratedFrom() {
        return generatedFrom;
    }

    @JsOverlay
    public final String getNamespace() {
        return namespace;
    }

    @JsOverlay
    public final String getDmnModelDocumentation() {
        return dmnModelDocumentation;
    }

    @JsOverlay
    public final String getTableOfContents() {
        return tableOfContents;
    }

    @JsOverlay
    public final String getDmnModel() {
        return dmnModel;
    }

    @JsOverlay
    public final String getDataTypes() {
        return dataTypes;
    }

    @JsOverlay
    public final String getDrdComponents() {
        return drdComponents;
    }

    @JsOverlay
    public final String getNoDataTypes() {
        return noDataTypes;
    }

    @JsOverlay
    public final String getNoDRDs() {
        return noDRDs;
    }

    @JsOverlay
    public final String getDiagramDoesNotHaveElements() {
        return diagramDoesNotHaveElements;
    }

    @JsOverlay
    public final String getNoExternalLinks() {
        return noExternalLinks;
    }

    @JsOverlay
    public final String getExternalLinks() {
        return externalLinks;
    }
}
