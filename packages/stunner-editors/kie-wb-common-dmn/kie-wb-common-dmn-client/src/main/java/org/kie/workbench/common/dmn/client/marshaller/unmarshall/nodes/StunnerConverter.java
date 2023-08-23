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

package org.kie.workbench.common.dmn.client.marshaller.unmarshall.nodes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DRGElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.dimensions.Height;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.dimensions.Width;
import org.kie.workbench.common.dmn.api.property.dmn.DecisionServiceDividerLineY;
import org.kie.workbench.common.dmn.api.property.styling.BgColour;
import org.kie.workbench.common.dmn.api.property.styling.BorderColour;
import org.kie.workbench.common.dmn.api.property.styling.StylingSet;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNDiagramsSession;
import org.kie.workbench.common.dmn.client.marshaller.converters.BusinessKnowledgeModelConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.DecisionConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.DecisionServiceConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.InputDataConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.KnowledgeSourceConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.NodeConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.TextAnnotationConverter;
import org.kie.workbench.common.dmn.client.marshaller.converters.dd.ColorUtils;
import org.kie.workbench.common.dmn.client.marshaller.converters.dd.FontStylingSetPropertyConverter;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIPoint;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.di.JSIStyle;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITBusinessKnowledgeModel;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecision;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionService;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInputData;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITKnowledgeSource;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITTextAnnotation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDecisionServiceDividerLine;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNShape;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNStyle;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bound;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.heightOfShape;
import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.lowerRightBound;
import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.upperLeftBound;
import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.widthOfShape;
import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.xOfShape;
import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.yOfShape;

@Dependent
public class StunnerConverter {

    private BusinessKnowledgeModelConverter bkmConverter;

    private DecisionConverter decisionConverter;

    private DecisionServiceConverter decisionServiceConverter;

    private InputDataConverter inputDataConverter;

    private KnowledgeSourceConverter knowledgeSourceConverter;

    private TextAnnotationConverter textAnnotationConverter;

    @Inject
    public StunnerConverter(final FactoryManager factoryManager,
                            final DMNDiagramsSession diagramsSession) {
        this.bkmConverter = new BusinessKnowledgeModelConverter(factoryManager);
        this.decisionConverter = new DecisionConverter(factoryManager);
        this.decisionServiceConverter = new DecisionServiceConverter(factoryManager, diagramsSession);
        this.inputDataConverter = new InputDataConverter(factoryManager);
        this.knowledgeSourceConverter = new KnowledgeSourceConverter(factoryManager);
        this.textAnnotationConverter = new TextAnnotationConverter(factoryManager);
    }

    Node make(final NodeEntry nodeEntry) {

        final Node<? extends View<?>, ?> node = getConverter(nodeEntry).nodeFromDMN(nodeEntry);

        // Stunner rely on relative positioning for Edge connections, so need to cycle on DMNShape first.
        ddExtAugmentStunner(node, nodeEntry.getDmnShape());

        // Included Nodes cannot be modified
        setAllowOnlyVisualChange(node, nodeEntry.isIncluded());

        return node;
    }

    private void setAllowOnlyVisualChange(final Node node, final boolean included) {
        getDRGElement(node).ifPresent(drgElement -> {
            drgElement.setAllowOnlyVisualChange(included);
        });
    }

    private Optional<DRGElement> getDRGElement(final Node node) {
        final Object objectDefinition = DefinitionUtils.getElementDefinition(node);
        if (objectDefinition instanceof DRGElement) {
            return Optional.of((DRGElement) objectDefinition);
        } else {
            return Optional.empty();
        }
    }

    private NodeConverter<?, ?> getConverter(final NodeEntry nodeEntry) {
        final String type = nodeEntry.getDmnElement().getTYPE_NAME();

        switch (type) {
            case JSITBusinessKnowledgeModel.TYPE:
                return bkmConverter;
            case JSITDecision.TYPE:
                return decisionConverter;
            case JSITDecisionService.TYPE:
                return decisionServiceConverter;
            case JSITInputData.TYPE:
                return inputDataConverter;
            case JSITKnowledgeSource.TYPE:
                return knowledgeSourceConverter;
            case JSITTextAnnotation.TYPE:
                return textAnnotationConverter;
            default:
                throw new UnsupportedOperationException("Unsupported DRGElement type [" + type + "]");
        }
    }

    private void ddExtAugmentStunner(final Node currentNode,
                                     final JSIDMNShape shape) {

        final View content = (View) currentNode.getContent();
        final Bound ulBound = upperLeftBound(content);
        final Bound lrBound = lowerRightBound(content);
        final Object definition = content.getDefinition();

        if (definition instanceof Decision) {
            final Decision decision = (Decision) definition;
            internalAugment(shape,
                            ulBound,
                            decision.getDimensionsSet(),
                            lrBound,
                            decision.getStylingSet(),
                            (line) -> {/*NOP*/});
        } else if (definition instanceof InputData) {
            final InputData inputData = (InputData) definition;
            internalAugment(shape,
                            ulBound,
                            inputData.getDimensionsSet(),
                            lrBound,
                            inputData.getStylingSet(),
                            (line) -> {/*NOP*/});
        } else if (definition instanceof BusinessKnowledgeModel) {
            final BusinessKnowledgeModel businessKnowledgeModel = (BusinessKnowledgeModel) definition;
            internalAugment(shape,
                            ulBound,
                            businessKnowledgeModel.getDimensionsSet(),
                            lrBound,
                            businessKnowledgeModel.getStylingSet(),
                            (line) -> {/*NOP*/});
        } else if (definition instanceof KnowledgeSource) {
            final KnowledgeSource knowledgeSource = (KnowledgeSource) definition;
            internalAugment(shape,
                            ulBound,
                            knowledgeSource.getDimensionsSet(),
                            lrBound,
                            knowledgeSource.getStylingSet(),
                            (line) -> {/*NOP*/});
        } else if (definition instanceof TextAnnotation) {
            final TextAnnotation textAnnotation = (TextAnnotation) definition;
            internalAugment(shape,
                            ulBound,
                            textAnnotation.getDimensionsSet(),
                            lrBound,
                            textAnnotation.getStylingSet(),
                            (line) -> {/*NOP*/});
        } else if (definition instanceof DecisionService) {
            final DecisionService decisionService = (DecisionService) definition;
            internalAugment(shape,
                            ulBound,
                            decisionService.getDimensionsSet(),
                            lrBound,
                            decisionService.getStylingSet(),
                            (dividerLineY) -> decisionService.setDividerLineY(new DecisionServiceDividerLineY(dividerLineY - ulBound.getY())));
        }
    }

    private void internalAugment(final JSIDMNShape drgShape,
                                 final Bound ulBound,
                                 final RectangleDimensionsSet dimensionsSet,
                                 final Bound lrBound,
                                 final StylingSet stylingSet,
                                 final Consumer<Double> decisionServiceDividerLineYSetter) {

        if (Objects.nonNull(ulBound)) {
            ulBound.setX(xOfShape(drgShape));
            ulBound.setY(yOfShape(drgShape));
        }
        dimensionsSet.setWidth(new Width(widthOfShape(drgShape)));
        dimensionsSet.setHeight(new Height(heightOfShape(drgShape)));
        if (Objects.nonNull(lrBound)) {
            lrBound.setX(xOfShape(drgShape) + widthOfShape(drgShape));
            lrBound.setY(yOfShape(drgShape) + heightOfShape(drgShape));
        }

        internalAugmentStyles(drgShape,
                              stylingSet);

        if (Objects.nonNull(drgShape.getDMNDecisionServiceDividerLine())) {
            final JSIDMNDecisionServiceDividerLine divider = Js.uncheckedCast(drgShape.getDMNDecisionServiceDividerLine());
            final List<JSIPoint> dividerPoints = divider.getWaypoint();
            final JSIPoint dividerY = Js.uncheckedCast(dividerPoints.get(0));
            decisionServiceDividerLineYSetter.accept(dividerY.getY());
        }
    }

    private void internalAugmentStyles(final JSIDMNShape drgShape,
                                       final StylingSet stylingSet) {
        final JSIStyle jsiStyle = drgShape.getStyle();
        if (Objects.isNull(jsiStyle)) {
            return;
        }

        final JSIStyle drgStyle = getUnwrappedJSIStyle(jsiStyle);
        final JSIDMNStyle dmnStyleOfDrgShape = isJSIDMNStyle(drgStyle) ? getJSIDmnStyle(drgStyle) : null;
        if (Objects.nonNull(dmnStyleOfDrgShape)) {
            if (Objects.nonNull(dmnStyleOfDrgShape.getFillColor())) {
                stylingSet.setBgColour(new BgColour(ColorUtils.wbFromDMN(dmnStyleOfDrgShape.getFillColor())));
            }
            if (Objects.nonNull(dmnStyleOfDrgShape.getStrokeColor())) {
                stylingSet.setBorderColour(new BorderColour(ColorUtils.wbFromDMN(dmnStyleOfDrgShape.getStrokeColor())));
            }
        }

        final StylingSet fontStylingSet = new StylingSet();
        if (Objects.nonNull(dmnStyleOfDrgShape)) {
            mergeFontStylingSet(fontStylingSet, FontStylingSetPropertyConverter.wbFromDMN(dmnStyleOfDrgShape));
        }

        if (Objects.nonNull(drgShape.getDMNLabel())) {
            final JSIDMNShape jsiLabel = Js.uncheckedCast(drgShape.getDMNLabel());
            final JSIStyle jsiLabelStyle = jsiLabel.getStyle();
            final Object jsiLabelSharedStyle = Js.uncheckedCast(jsiLabel.getSharedStyle());
            if (Objects.nonNull(jsiLabelSharedStyle) && JSIDMNStyle.instanceOf(jsiLabelSharedStyle)) {
                mergeFontStylingSet(fontStylingSet, FontStylingSetPropertyConverter.wbFromDMN((Js.uncheckedCast(jsiLabelSharedStyle))));
            }
            if (Objects.nonNull(jsiLabelStyle) && isJSIDMNStyle(jsiLabelStyle)) {
                mergeFontStylingSet(fontStylingSet, FontStylingSetPropertyConverter.wbFromDMN(Js.uncheckedCast(jsiLabelStyle)));
            }
        }
        mergeFontStylingSet(stylingSet, fontStylingSet);
    }

    private void mergeFontStylingSet(final StylingSet stylingSet,
                                     final StylingSet additional) {
        if (Objects.nonNull(additional.getFontFamily())) {
            stylingSet.setFontFamily(additional.getFontFamily());
        }
        if (Objects.nonNull(additional.getFontSize())) {
            stylingSet.setFontSize(additional.getFontSize());
        }
        if (Objects.nonNull(additional.getFontColour())) {
            stylingSet.setFontColour(additional.getFontColour());
        }
    }

    /**
     * ########################################
     * package protected methods due to testing
     * ########################################
     */

    boolean isJSIDMNStyle(JSIStyle drgStyle) {
        return JSIDMNStyle.instanceOf(drgStyle);
    }

    JSIStyle getUnwrappedJSIStyle(JSIStyle jsiStyle) {
        return Js.uncheckedCast(JsUtils.getUnwrappedElement(jsiStyle));
    }

    JSIDMNStyle getJSIDmnStyle(JSIStyle jsiStyle) {
        return Js.uncheckedCast(jsiStyle);
    }
}
