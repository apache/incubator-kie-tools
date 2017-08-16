/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.editors.expressions.types.undefined;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import com.ait.lienzo.client.core.shape.Group;
import com.ait.lienzo.client.core.shape.Rectangle;
import com.ait.lienzo.shared.core.types.ColorName;
import com.ait.lienzo.shared.core.types.EventPropagationMode;
import com.google.gwt.event.shared.HandlerRegistration;
import org.kie.workbench.common.dmn.api.definition.v1_1.Expression;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinition;
import org.kie.workbench.common.dmn.client.editors.expressions.types.ExpressionEditorDefinitions;
import org.uberfire.ext.wires.core.grids.client.model.GridCell;
import org.uberfire.ext.wires.core.grids.client.widget.context.GridBodyCellRenderContext;
import org.uberfire.ext.wires.core.grids.client.widget.dom.HasDOMElementResources;
import org.uberfire.ext.wires.core.grids.client.widget.grid.renderers.columns.impl.BaseGridColumnRenderer;

public class UndefinedExpressionColumnRenderer extends BaseGridColumnRenderer<String> implements HasDOMElementResources {

    private final Group editorTypesContainer = new Group();
    private ExpressionEditorTooltip tooltip = ExpressionEditorTooltip.INSTANCE;
    private Set<HandlerRegistration> tooltipEventHandlerRegistrations = new HashSet<>();

    private Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier;
    private UndefinedExpressionGrid gridWidget;

    public UndefinedExpressionColumnRenderer(final Supplier<ExpressionEditorDefinitions> expressionEditorDefinitionsSupplier,
                                             final UndefinedExpressionGrid gridWidget) {
        this.expressionEditorDefinitionsSupplier = expressionEditorDefinitionsSupplier;
        this.gridWidget = gridWidget;

        setupTooltips();
    }

    private void setupTooltips() {
        double x = 10.0;
        for (ExpressionEditorDefinition<Expression> definition : expressionEditorDefinitionsSupplier.get()) {
            if (definition.getModelClass().isPresent()) {
                final Rectangle r = new Rectangle(10, 10);
                r.setY(10.0).setX(x).setFillColor(ColorName.AQUAMARINE).setEventPropagationMode(EventPropagationMode.NO_ANCESTORS);

                tooltipEventHandlerRegistrations.add(r.addNodeMouseEnterHandler((event) -> {
                    final double absoluteCellX = editorTypesContainer.getAbsoluteLocation().getX();
                    final double absoluteCellY = editorTypesContainer.getAbsoluteLocation().getY();
                    tooltip.show(definition,
                                 absoluteCellX,
                                 absoluteCellY,
                                 r);
                }));
                tooltipEventHandlerRegistrations.add(r.addNodeMouseExitHandler((event) -> tooltip.hide()));
                tooltipEventHandlerRegistrations.add(r.addNodeMouseClickHandler((event) -> {
                    tooltip.hide();
                    gridWidget.onExpressionTypeChanged(definition.getType());
                }));

                x = x + 15.0;
                editorTypesContainer.add(r);
            }
        }
    }

    @Override
    public Group renderCell(final GridCell<String> cell,
                            final GridBodyCellRenderContext context) {
        //Ensure Tooltip is only added to Layer once
        this.gridWidget.getLayer().remove(tooltip.asPrimitive());
        this.gridWidget.getLayer().add(tooltip.asPrimitive());

        final Group g = new Group();
        g.add(editorTypesContainer);

        return g;
    }

    @Override
    public void destroyResources() {
        tooltipEventHandlerRegistrations.stream().forEach(HandlerRegistration::removeHandler);
        tooltipEventHandlerRegistrations.clear();
        tooltip.hide();
    }
}
