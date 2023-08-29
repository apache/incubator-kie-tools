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
package org.kie.workbench.common.dmn.client.marshaller.common;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.Definitions;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.NamedElement;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.styling.StylingSet;
import org.kie.workbench.common.dmn.client.marshaller.converters.dd.ColorUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIBounds;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIPoint;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAssociation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITContext;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionTable;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITFunctionDefinition;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInvocation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITList;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITLiteralExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITRelation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITTextAnnotation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDecisionServiceDividerLine;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDiagram;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNEdge;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNLabel;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNShape;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNStyle;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITAttachment;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentsWidthsExtension;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JSIName;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.dmn.client.marshaller.common.IdUtils.getShapeId;
import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.upperLeftBound;
import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.xOfBound;
import static org.kie.workbench.common.dmn.client.marshaller.converters.dd.PointUtils.yOfBound;
import static org.kie.workbench.common.stunner.core.util.StringUtils.isEmpty;

/**
 * Class used to holds all common <b>wrapping</b> methods
 */
public class WrapperUtils {

    public static JSITAssociation getWrappedJSITAssociation(final JSITAssociation toWrap) {
        final JSITAssociation toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        final JSIName jsiName = JSITTextAnnotation.getJSIName();
        updateJSIName(jsiName, "dmn", "association");
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITTextAnnotation getWrappedJSITTextAnnotation(final JSITTextAnnotation toWrap) {
        final JSITTextAnnotation toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        final JSIName jsiName = JSITTextAnnotation.getJSIName();
        updateJSIName(jsiName, "dmn", "textAnnotation");
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSIDMNStyle getWrappedJSIDMNStyle(final JSIDMNStyle toWrap) {
        final JSIDMNStyle toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        final JSIName jsiName = JSIDMNStyle.getJSIName();
        updateJSIName(jsiName, "dmndi", "DMNStyle");
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITDecisionTable getWrappedJSITDecisionTable(final JSITDecisionTable toWrap,
                                                                final String prefix,
                                                                final String localPart) {
        final JSITDecisionTable toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        final JSIName jsiName = JSITDecisionTable.getJSIName();
        updateJSIName(jsiName, prefix, localPart);
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITFunctionDefinition getWrappedJSITFunctionDefinition(final JSITFunctionDefinition toWrap,
                                                                          final String prefix,
                                                                          final String localPart) {
        final JSITFunctionDefinition toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        final JSIName jsiName = JSITFunctionDefinition.getJSIName();
        updateJSIName(jsiName, prefix, localPart);
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITInvocation getWrappedJSITInvocation(final JSITInvocation toWrap,
                                                          final String prefix,
                                                          final String localPart) {
        final JSITInvocation toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        final JSIName jsiName = JSITInvocation.getJSIName();
        updateJSIName(jsiName, prefix, localPart);
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITList getWrappedJSITList(final JSITList toWrap,
                                              final String prefix,
                                              final String localPart) {
        final JSITList toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        final JSIName jsiName = JSITList.getJSIName();
        updateJSIName(jsiName, prefix, localPart);
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITRelation getWrappedJSITRelation(final JSITRelation toWrap,
                                                      final String prefix,
                                                      final String localPart) {
        final JSITRelation toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        final JSIName jsiName = JSITRelation.getJSIName();
        updateJSIName(jsiName, prefix, localPart);
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITContext getWrappedJSITContext(final JSITContext toWrap,
                                                    final String prefix,
                                                    final String localPart) {
        final JSITContext toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        final JSIName jsiName = JSITContext.getJSIName();
        updateJSIName(jsiName, prefix, localPart);
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITExpression getWrappedJSITExpression(final JSITExpression toWrap,
                                                          final String prefix,
                                                          final String localPart) {
        final JSITExpression toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        final JSIName jsiName = JSITExpression.getJSIName();
        updateJSIName(jsiName, prefix, localPart);
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITLiteralExpression getWrappedJSITLiteralExpression(final JSITLiteralExpression toWrap,
                                                                        final String prefix,
                                                                        final String localPart) {
        final JSITLiteralExpression toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        final JSIName jsiName = JSITLiteralExpression.getJSIName();
        updateJSIName(jsiName, prefix, localPart);
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSIDMNEdge getWrappedJSIDMNEdge(final JSIDMNEdge toWrap) {
        final JSIDMNEdge toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        final JSIName jsiName = JSIDMNEdge.getJSIName();
        updateJSIName(jsiName, "dmndi", "DMNEdge");
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSIDMNShape getWrappedJSIDMNShape(final JSIDMNDiagram diagram,
                                                    final List<String> dmnDiagramElementIds,
                                                    final Definitions definitionsStunnerPojo,
                                                    final View<? extends DMNElement> v,
                                                    final String namespaceURI) {
        final JSIDMNShape unwrappedJSIDMNShape = stunnerToDDExt(diagram, dmnDiagramElementIds, definitionsStunnerPojo, v, namespaceURI);
        final JSIDMNShape toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(unwrappedJSIDMNShape));
        final JSIName jsiName = JSIDMNShape.getJSIName();
        updateJSIName(jsiName, "dmndi", "DMNShape");
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITAttachment getWrappedJSITAttachment(final JSITAttachment attachment) {
        final JSITAttachment toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(attachment));
        final JSIName jsiName = JSITAttachment.getJSIName();
        updateJSIName(jsiName, "kie", "attachment");
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITComponentsWidthsExtension getWrappedJSITComponentsWidthsExtension(final JSITComponentsWidthsExtension componentsWidthsExtension) {
        final JSITComponentsWidthsExtension toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(componentsWidthsExtension));
        final JSIName jsiName = JSITComponentsWidthsExtension.getJSIName();
        updateJSIName(jsiName, "kie", "ComponentsWidthsExtension");
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITDRGElement getWrappedJSITDRGElement(final JSITDRGElement toWrap,
                                                          final String prefix,
                                                          final String localPart) {
        final JSITDRGElement toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        final JSIName jsiName = JSITDRGElement.getJSIName();
        updateJSIName(jsiName, prefix, localPart);
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static void updateJSIName(final JSIName toUpdate,
                                     final String prefix,
                                     final String localPart) {
        toUpdate.setPrefix(prefix);
        toUpdate.setLocalPart(localPart);
        final String key = "{" + toUpdate.getNamespaceURI() + "}" + toUpdate.getLocalPart();
        toUpdate.setKey(key);
        final String newPrefix = !isEmpty(toUpdate.getPrefix()) ? toUpdate.getPrefix() + ":" : "";
        final String string = "{" + toUpdate.getNamespaceURI() + "}" + newPrefix + toUpdate.getLocalPart();
        toUpdate.setString(string);
    }

    private static JSIDMNShape stunnerToDDExt(final JSIDMNDiagram diagram,
                                              final List<String> dmnDiagramElementIds,
                                              final Definitions definitionsStunnerPojo,
                                              final View<? extends DMNElement> v,
                                              final String namespaceURI) {
        final JSIDMNShape result = JSIDMNShape.newInstance();
        final DMNElement definition = v.getDefinition();
        final String dmnElementId = definition.getId().getValue();
        final String shapeId = getShapeId(diagram, dmnDiagramElementIds, dmnElementId);

        result.setId(shapeId);
        result.setDmnElementRef(getDmnElementRef(definitionsStunnerPojo, v, namespaceURI));
        final JSIBounds bounds = JSIBounds.newInstance();
        result.setBounds(bounds);
        bounds.setX(xOfBound(upperLeftBound(v)));
        bounds.setY(yOfBound(upperLeftBound(v)));
        result.setDMNLabel(JSIDMNLabel.newInstance());
        // TODO {gcardosi}: HARDCODED
        result.setIsCollapsed(false);
        final JSIDMNStyle style = JSIDMNStyle.newInstance();
        if (v.getDefinition() instanceof Decision) {
            final Decision d = (Decision) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyStylingStyles(d.getStylingSet(), style);
        } else if (v.getDefinition() instanceof InputData) {
            InputData d = (InputData) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyStylingStyles(d.getStylingSet(), style);
        } else if (v.getDefinition() instanceof BusinessKnowledgeModel) {
            final BusinessKnowledgeModel d = (BusinessKnowledgeModel) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyStylingStyles(d.getStylingSet(), style);
        } else if (v.getDefinition() instanceof KnowledgeSource) {
            final KnowledgeSource d = (KnowledgeSource) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyStylingStyles(d.getStylingSet(), style);
        } else if (v.getDefinition() instanceof TextAnnotation) {
            final TextAnnotation d = (TextAnnotation) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyStylingStyles(d.getStylingSet(), style);
        } else if (v.getDefinition() instanceof DecisionService) {
            final DecisionService d = (DecisionService) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyStylingStyles(d.getStylingSet(), style);
            final JSIDMNDecisionServiceDividerLine dl = JSIDMNDecisionServiceDividerLine.newInstance();
            final JSIPoint leftPoint = JSIPoint.newInstance();
            leftPoint.setX(v.getBounds().getUpperLeft().getX());
            final double dlY = v.getBounds().getUpperLeft().getY() + d.getDividerLineY().getValue();
            leftPoint.setY(dlY);
            dl.addWaypoint(leftPoint);
            final JSIPoint rightPoint = JSIPoint.newInstance();
            rightPoint.setX(v.getBounds().getLowerRight().getX());
            rightPoint.setY(dlY);
            dl.addWaypoint(rightPoint);
            result.setDMNDecisionServiceDividerLine(dl);
        }
        result.setStyle(getWrappedJSIDMNStyle(style));

        return result;
    }

    static QName getDmnElementRef(final Definitions definitions,
                                  final View<? extends DMNElement> v,
                                  final String namespaceURI) {

        final DMNElement dmnElement = v.getDefinition();
        final String dmnElementId = dmnElement.getId().getValue();

        return getImportPrefix(definitions, dmnElement)
                .map(prefix -> new QName(namespaceURI, prefix + ":" + dmnElementId, XMLConstants.DEFAULT_NS_PREFIX))
                .orElse(new QName(namespaceURI, dmnElementId, XMLConstants.DEFAULT_NS_PREFIX));
    }

    private static Optional<String> getImportPrefix(final Definitions definitions,
                                                    final DMNElement dmnElement) {

        if (!(dmnElement instanceof NamedElement)) {
            return Optional.empty();
        }

        final NamedElement namedElement = (NamedElement) dmnElement;
        final Optional<String> name = Optional.ofNullable(namedElement.getName().getValue());

        return definitions
                .getImport()
                .stream()
                .filter(anImport -> {
                    final String importName = anImport.getName().getValue();
                    return name.map(n -> n.startsWith(importName + ".")).orElse(false);
                })
                .map(anImport -> {
                    final String importNamespace = anImport.getNamespace();
                    return getNsContextsByNamespace(definitions, importNamespace);
                })
                .findFirst();
    }

    private static String getNsContextsByNamespace(final Definitions definitions,
                                                   final String namespace) {
        return definitions
                .getNsContext()
                .entrySet()
                .stream()
                .filter(entry -> Objects.equals(entry.getValue(), namespace))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("");
    }

    private static void applyBounds(final RectangleDimensionsSet dimensionsSet,
                                    final JSIBounds bounds) {
        if (null != dimensionsSet.getWidth().getValue() &&
                null != dimensionsSet.getHeight().getValue()) {
            bounds.setWidth(dimensionsSet.getWidth().getValue());
            bounds.setHeight(dimensionsSet.getHeight().getValue());
        }
    }

    private static void applyStylingStyles(final StylingSet stylingSet,
                                           final JSIDMNStyle style) {
        if (Objects.nonNull(stylingSet.getBgColour().getValue())) {
            style.setFillColor(ColorUtils.dmnFromWB(stylingSet.getBgColour().getValue()));
        }
        if (Objects.nonNull(stylingSet.getBorderColour().getValue())) {
            style.setStrokeColor(ColorUtils.dmnFromWB(stylingSet.getBorderColour().getValue()));
        }
        if (Objects.nonNull(stylingSet.getFontColour().getValue())) {
            style.setFontColor(ColorUtils.dmnFromWB(stylingSet.getFontColour().getValue()));
        }
        if (Objects.nonNull(stylingSet.getFontFamily().getValue())) {
            style.setFontFamily(stylingSet.getFontFamily().getValue());
        }
        if (Objects.nonNull(stylingSet.getFontSize().getValue())) {
            style.setFontSize(stylingSet.getFontSize().getValue());
        }
    }
}
