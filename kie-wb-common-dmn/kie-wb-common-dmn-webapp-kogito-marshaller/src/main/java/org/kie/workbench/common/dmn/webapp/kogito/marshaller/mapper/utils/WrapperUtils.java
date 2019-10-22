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
package org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.utils;

import java.util.Objects;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import jsinterop.base.Js;
import org.kie.workbench.common.dmn.api.definition.model.BusinessKnowledgeModel;
import org.kie.workbench.common.dmn.api.definition.model.DMNElement;
import org.kie.workbench.common.dmn.api.definition.model.Decision;
import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.api.definition.model.InputData;
import org.kie.workbench.common.dmn.api.definition.model.KnowledgeSource;
import org.kie.workbench.common.dmn.api.definition.model.TextAnnotation;
import org.kie.workbench.common.dmn.api.property.background.BackgroundSet;
import org.kie.workbench.common.dmn.api.property.dimensions.RectangleDimensionsSet;
import org.kie.workbench.common.dmn.api.property.font.FontSet;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIBounds;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIColor;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dc.JSIPoint;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITAssociation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITContext;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDRGElement;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITDecisionTable;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITFunctionDefinition;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITInvocation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITList;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITLiteralExpression;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITRelation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmn12.JSITTextAnnotation;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNDecisionServiceDividerLine;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNEdge;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNLabel;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNShape;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.dmndi12.JSIDMNStyle;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.js.model.kie.JSITComponentsWidthsExtension;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JSIName;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.JsUtils;
import org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.ColorUtils;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.util.StringUtils;

import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.upperLeftBound;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.xOfBound;
import static org.kie.workbench.common.dmn.webapp.kogito.marshaller.mapper.definition.model.dd.PointUtils.yOfBound;

/**
 * Class used to holds all common <b>wrapping</b> methods
 */
public class WrapperUtils {

    public static JSITAssociation getWrappedJSITAssociation(JSITAssociation toWrap) {
        JSITAssociation toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        JSIName jsiName = JSITTextAnnotation.getJSIName();
        updateJSIName(jsiName, "dmn", "association");
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITTextAnnotation getWrappedJSITTextAnnotation(JSITTextAnnotation toWrap) {
        JSITTextAnnotation toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        JSIName jsiName = JSITTextAnnotation.getJSIName();
        updateJSIName(jsiName, "dmn", "textAnnotation");
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSIDMNStyle getWrappedJSIDMNStyle(JSIDMNStyle toWrap) {
        JSIDMNStyle toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        JSIName jsiName = JSIDMNStyle.getJSIName();
        updateJSIName(jsiName, "dmndi", "DMNStyle");
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITDecisionTable getWrappedJSITDecisionTable(JSITDecisionTable toWrap, String prefix, String localPart) {
        JSITDecisionTable toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        JSIName jsiName = JSITDecisionTable.getJSIName();
        updateJSIName(jsiName, prefix, localPart);
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITFunctionDefinition getWrappedJSITFunctionDefinition(JSITFunctionDefinition toWrap, String prefix, String localPart) {
        JSITFunctionDefinition toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        JSIName jsiName = JSITFunctionDefinition.getJSIName();
        updateJSIName(jsiName, prefix, localPart);
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITInvocation getWrappedJSITInvocation(JSITInvocation toWrap, String prefix, String localPart) {
        JSITInvocation toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        JSIName jsiName = JSITInvocation.getJSIName();
        updateJSIName(jsiName, prefix, localPart);
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITList getWrappedJSITList(JSITList toWrap, String prefix, String localPart) {
        JSITList toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        JSIName jsiName = JSITList.getJSIName();
        updateJSIName(jsiName, prefix, localPart);
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITRelation getWrappedJSITRelation(JSITRelation toWrap, String prefix, String localPart) {
        JSITRelation toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        JSIName jsiName = JSITRelation.getJSIName();
        updateJSIName(jsiName, prefix, localPart);
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITContext getWrappedJSITContext(JSITContext toWrap, String prefix, String localPart) {
        JSITContext toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        JSIName jsiName = JSITContext.getJSIName();
        updateJSIName(jsiName, prefix, localPart);
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITLiteralExpression getWrappedJSITLiteralExpression(JSITLiteralExpression toWrap, String prefix, String localPart) {
        JSITLiteralExpression toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        JSIName jsiName = JSITLiteralExpression.getJSIName();
        updateJSIName(jsiName, prefix, localPart);
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSIDMNEdge getWrappedJSIDMNEdge(JSIDMNEdge toWrap) {
        JSIDMNEdge toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        JSIName jsiName = JSIDMNEdge.getJSIName();
        updateJSIName(jsiName, "dmndi", "DMNEdge");
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSIDMNShape getWrappedJSIDMNShape(final View<? extends DMNElement> v,
                                                    final String namespaceURI) {
        JSIDMNShape unwrappedJSIDMNShape = stunnerToDDExt(v, namespaceURI);
        JSIDMNShape toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(unwrappedJSIDMNShape));
        JSIName jsiName = JSIDMNShape.getJSIName();
        updateJSIName(jsiName, "dmndi", "DMNShape");
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITComponentsWidthsExtension getWrappedJSITComponentsWidthsExtension(JSITComponentsWidthsExtension componentsWidthsExtension) {
        JSITComponentsWidthsExtension toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(componentsWidthsExtension));
        JSIName jsiName = JSITComponentsWidthsExtension.getJSIName();
        updateJSIName(jsiName, "kie", "ComponentsWidthsExtension");
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static JSITDRGElement getWrappedJSITDRGElement(JSITDRGElement toWrap, String prefix, String localPart) {
        JSITDRGElement toReturn = Js.uncheckedCast(JsUtils.getWrappedElement(toWrap));
        JSIName jsiName = JSITDRGElement.getJSIName();
        updateJSIName(jsiName, prefix, localPart);
        JsUtils.setNameOnWrapped(toReturn, jsiName);
        return toReturn;
    }

    public static void updateJSIName(JSIName toUpdate, String prefix, String localPart) {
        toUpdate.setPrefix(prefix);
        toUpdate.setLocalPart(localPart);
        String key = "{" + toUpdate.getNamespaceURI() + "}" + toUpdate.getLocalPart();
        toUpdate.setKey(key);
        prefix = !StringUtils.isEmpty(toUpdate.getPrefix()) ? toUpdate.getPrefix() + ":" : "";
        String string = "{" + toUpdate.getNamespaceURI() + "}" + prefix + toUpdate.getLocalPart();
        toUpdate.setString(string);
    }

    private static JSIDMNShape stunnerToDDExt(final View<? extends DMNElement> v,
                                              final String namespaceURI) {
        final JSIDMNShape result = new JSIDMNShape();
        result.setId("dmnshape-" + v.getDefinition().getId().getValue());
        result.setDmnElementRef(new QName(namespaceURI,
                                          v.getDefinition().getId().getValue(),
                                          XMLConstants.DEFAULT_NS_PREFIX));
        final JSIBounds bounds = new JSIBounds();
        result.setBounds(bounds);
        bounds.setX(xOfBound(upperLeftBound(v)));
        bounds.setY(yOfBound(upperLeftBound(v)));
        result.setDMNLabel(new JSIDMNLabel());
        // TODO {gcardosi}: HARDCODED
        result.setIsCollapsed(false);
        final JSIDMNStyle style = new JSIDMNStyle();
        if (v.getDefinition() instanceof Decision) {
            final Decision d = (Decision) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), style);
            applyFontStyle(d.getFontSet(), style);
        } else if (v.getDefinition() instanceof InputData) {
            InputData d = (InputData) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), style);
            applyFontStyle(d.getFontSet(), style);
        } else if (v.getDefinition() instanceof BusinessKnowledgeModel) {
            final BusinessKnowledgeModel d = (BusinessKnowledgeModel) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), style);
            applyFontStyle(d.getFontSet(), style);
        } else if (v.getDefinition() instanceof KnowledgeSource) {
            final KnowledgeSource d = (KnowledgeSource) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), style);
            applyFontStyle(d.getFontSet(), style);
        } else if (v.getDefinition() instanceof TextAnnotation) {
            final TextAnnotation d = (TextAnnotation) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), style);
            applyFontStyle(d.getFontSet(), style);
        } else if (v.getDefinition() instanceof DecisionService) {
            final DecisionService d = (DecisionService) v.getDefinition();
            applyBounds(d.getDimensionsSet(), bounds);
            applyBackgroundStyles(d.getBackgroundSet(), style);
            applyFontStyle(d.getFontSet(), style);
            final JSIDMNDecisionServiceDividerLine dl = new JSIDMNDecisionServiceDividerLine();
            final JSIPoint leftPoint = new JSIPoint();
            leftPoint.setX(v.getBounds().getUpperLeft().getX());
            final double dlY = v.getBounds().getUpperLeft().getY() + d.getDividerLineY().getValue();
            leftPoint.setY(dlY);
            dl.addWaypoint(leftPoint);
            final JSIPoint rightPoint = new JSIPoint();
            rightPoint.setX(v.getBounds().getLowerRight().getX());
            rightPoint.setY(dlY);
            dl.addWaypoint(rightPoint);
            result.setDMNDecisionServiceDividerLine(dl);
        }
        result.setStyle(getWrappedJSIDMNStyle(style));

        return result;
    }

    private static void applyFontStyle(final FontSet fontSet,
                                       final JSIDMNStyle style) {
        final JSIColor fontColor = ColorUtils.dmnFromWB(fontSet.getFontColour().getValue());
        style.setFontColor(fontColor);
        if (Objects.nonNull(fontSet.getFontFamily().getValue())) {
            style.setFontFamily(fontSet.getFontFamily().getValue());
        }
        if (Objects.nonNull(fontSet.getFontSize().getValue())) {
            style.setFontSize(fontSet.getFontSize().getValue());
        }
    }

    private static void applyBounds(final RectangleDimensionsSet dimensionsSet,
                                    final JSIBounds bounds) {
        if (null != dimensionsSet.getWidth().getValue() &&
                null != dimensionsSet.getHeight().getValue()) {
            bounds.setWidth(dimensionsSet.getWidth().getValue());
            bounds.setHeight(dimensionsSet.getHeight().getValue());
        }
    }

    private static void applyBackgroundStyles(final BackgroundSet bgset,
                                              final JSIDMNStyle style) {
        if (Objects.nonNull(bgset.getBgColour().getValue())) {
            style.setFillColor(ColorUtils.dmnFromWB(bgset.getBgColour().getValue()));
        }
        if (Objects.nonNull(bgset.getBorderColour().getValue())) {
            style.setStrokeColor(ColorUtils.dmnFromWB(bgset.getBorderColour().getValue()));
        }
    }
}
