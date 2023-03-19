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
package org.drools.workbench.screens.scenariosimulation.client.factories;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.user.client.DOM;
import org.drools.workbench.screens.scenariosimulation.client.domelements.ScenarioCellTextAreaDOMElement;
import org.drools.workbench.screens.scenariosimulation.client.events.ScenarioInputEvent;
import org.drools.workbench.screens.scenariosimulation.client.utils.ConstantHolder;
import org.drools.workbench.screens.scenariosimulation.client.utils.ExpressionUtils;
import org.gwtbootstrap3.client.ui.TextArea;
import org.uberfire.ext.wires.core.grids.client.widget.dom.impl.BaseDOMElement;
import org.uberfire.ext.wires.core.grids.client.widget.grid.GridWidget;
import org.uberfire.ext.wires.core.grids.client.widget.layer.GridLayer;
import org.uberfire.ext.wires.core.grids.client.widget.layer.impl.GridLienzoPanel;

import static org.drools.scenariosimulation.api.utils.ConstantsHolder.MVEL_ESCAPE_SYMBOL;

public class ScenarioExpressionCellTextAreaSingletonDOMElementFactory extends AbstractTextBoxSingletonDOMElementFactory<BaseDOMElement<String, TextArea>> {

    public ScenarioExpressionCellTextAreaSingletonDOMElementFactory(GridLienzoPanel gridPanel,
                                                                    GridLayer gridLayer,
                                                                    GridWidget gridWidget) {
        super(gridPanel, gridLayer, gridWidget);
    }

    @Override
    protected BaseDOMElement<String, TextArea> createDomElementInternal(final TextArea widget,
                                                                        final GridLayer gridLayer,
                                                                        final GridWidget gridWidget) {
        return new ScenarioCellTextAreaDOMElement(widget, gridLayer, gridWidget);
    }

    @Override
    public TextArea createWidget() {
        TextArea textArea = super.createWidget();
        DOM.sinkBitlessEvent(textArea.getElement(), ConstantHolder.INPUT);
        textArea.addHandler(scenarioInputEvent -> checkExpressionSyntax(), ScenarioInputEvent.getType());
        textArea.addFocusHandler(focusEvent -> checkExpressionSyntax());
        textArea.addBlurHandler(blurEvent -> checkEmptyExpression());
        textArea.addKeyDownHandler(this::checkEmptyExpression);
        return textArea;
    }

    protected void checkExpressionSyntax() {
        widget.setValue(ExpressionUtils.ensureExpressionSyntax(getValue()));
    }

    protected void checkEmptyExpression(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            checkEmptyExpression();
        }
    }

    protected void checkEmptyExpression() {
        if (getValue().trim().equals(MVEL_ESCAPE_SYMBOL)) {
            widget.setValue(null);
        }
    }
}
