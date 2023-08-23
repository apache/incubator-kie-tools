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

package org.kie.workbench.common.dmn.client.editors.documentation.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.dmn.api.definition.model.ItemDefinition;
import org.kie.workbench.common.dmn.api.definition.model.UnaryTests;
import org.kie.workbench.common.dmn.client.graph.DMNGraphUtils;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.util.CanvasFileExport;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.uberfire.client.views.pfly.widgets.Moment;

import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationFactory_Constraints;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationFactory_ListYes;
import static org.kie.workbench.common.dmn.client.resources.i18n.DMNEditorConstants.DMNDocumentationFactory_Structure;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

public class DMNDocumentationFactory {

    static final String EMPTY = "";

    private final CanvasFileExport canvasFileExport;

    private final TranslationService translationService;

    private final DMNDocumentationDRDsFactory drdsFactory;

    private final DMNGraphUtils graphUtils;

    @Inject
    public DMNDocumentationFactory(final CanvasFileExport canvasFileExport,
                                   final TranslationService translationService,
                                   final DMNDocumentationDRDsFactory drdsFactory,
                                   final DMNGraphUtils graphUtils) {
        this.canvasFileExport = canvasFileExport;
        this.translationService = translationService;
        this.drdsFactory = drdsFactory;
        this.graphUtils = graphUtils;
    }

    public DMNDocumentation create(final Diagram diagram) {
        return DMNDocumentation.create(getNamespace(diagram),
                                       getDiagramName(diagram),
                                       getDiagramDescription(diagram),
                                       hasGraphNodes(diagram),
                                       getDataTypes(diagram),
                                       getDrds(diagram),
                                       getDiagramImage(),
                                       getCurrentDate(),
                                       getCurrentYear(),
                                       getDocumentationI18n());
    }

    protected List<DMNDocumentationDRD> getDrds(final Diagram diagram) {
        return drdsFactory.create(diagram);
    }

    protected String getNamespace(final Diagram diagram) {
        return graphUtils.getDefinitions(diagram).getNamespace().getValue();
    }

    protected boolean hasGraphNodes(final Diagram diagram) {
        return !graphUtils.getDRGElements(diagram).isEmpty();
    }

    protected String getDiagramImage() {
        return getCanvasHandler()
                .map(canvasFileExport::exportToPng)
                .orElse(EMPTY);
    }

    protected List<DMNDocumentationDataType> getDataTypes(final Diagram diagram) {

        final List<ItemDefinition> itemDefinitions = graphUtils.getDefinitions(diagram).getItemDefinition();
        final List<DMNDocumentationDataType> dataTypes = new ArrayList<>();

        makeDMNDocumentationDataTypes(itemDefinitions, dataTypes, 0);

        return dataTypes;
    }

    private void makeDMNDocumentationDataTypes(final List<ItemDefinition> itemDefinitions,
                                               final List<DMNDocumentationDataType> dataTypes,
                                               final int level) {
        itemDefinitions.forEach(itemDefinition -> makeDMNDocumentationDataType(dataTypes, itemDefinition, level));
    }

    private void makeDMNDocumentationDataType(final List<DMNDocumentationDataType> dataTypes,
                                              final ItemDefinition itemDefinition,
                                              final int level) {

        final String name = itemDefinition.getName().getValue();
        final String type = getType(itemDefinition);
        final String listLabel = getListLabel(itemDefinition);
        final String constraint = getConstraint(itemDefinition);
        final DMNDocumentationDataType dataType = DMNDocumentationDataType.create(name, type, listLabel, constraint, level);

        dataTypes.add(dataType);

        makeDMNDocumentationDataTypes(itemDefinition.getItemComponent(), dataTypes, level + 1);
    }

    private String getConstraint(final ItemDefinition itemDefinition) {
        final UnaryTests allowedValues = itemDefinition.getAllowedValues();

        if (allowedValues != null && !isEmpty(allowedValues.getText().getValue())) {
            return translationService.format(DMNDocumentationFactory_Constraints) + " " + allowedValues.getText().getValue();
        }
        return "";
    }

    private String getListLabel(final ItemDefinition itemDefinition) {
        if (itemDefinition.isIsCollection()) {
            return translationService.format(DMNDocumentationFactory_ListYes);
        }
        return "";
    }

    private String getType(final ItemDefinition itemDefinition) {
        if (itemDefinition.getTypeRef() != null) {
            return itemDefinition.getTypeRef().getLocalPart();
        }
        return translationService.format(DMNDocumentationFactory_Structure);
    }

    protected String getCurrentDate() {
        return moment().format("D MMMM YYYY");
    }

    protected String getCurrentYear() {
        return moment().format("YYYY");
    }

    protected String getDiagramName(final Diagram diagram) {
        return graphUtils
                .getDefinitions(diagram)
                .getName()
                .getValue();
    }

    protected String getDiagramDescription(final Diagram diagram) {
        return graphUtils
                .getDefinitions(diagram)
                .getDescription()
                .getValue();
    }

    private Optional<AbstractCanvasHandler> getCanvasHandler() {
        return Optional
                .ofNullable(graphUtils.getCanvasHandler())
                .filter(c -> c instanceof AbstractCanvasHandler)
                .map(c -> (AbstractCanvasHandler) c);
    }

    protected DMNDocumentationI18n getDocumentationI18n() {
        return DMNDocumentationI18n.create(translationService);
    }

    Moment moment() {
        return Moment.Builder.moment();
    }
}
