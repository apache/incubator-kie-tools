/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.cm.client.shapes.factory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.cm.client.shapes.ActivityShape;
import org.kie.workbench.common.stunner.cm.client.shapes.DiagramShape;
import org.kie.workbench.common.stunner.cm.client.shapes.StageShape;
import org.kie.workbench.common.stunner.cm.client.shapes.view.ActivityView;
import org.kie.workbench.common.stunner.cm.client.shapes.view.DiagramView;
import org.kie.workbench.common.stunner.cm.client.shapes.view.StageView;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementBaseSubprocess;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementBaseTask;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.cm.shapes.def.CaseManagementDiagramShapeDef;
import org.kie.workbench.common.stunner.cm.shapes.def.CaseManagementSubprocessShapeDef;
import org.kie.workbench.common.stunner.cm.shapes.def.CaseManagementTaskShapeDef;
import org.kie.workbench.common.stunner.cm.shapes.factory.CaseManagementShapesFactory;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.shapes.client.factory.BasicShapesFactoryImpl;

@ApplicationScoped
public class CaseManagementShapesFactoryImpl implements CaseManagementShapesFactory<Object, AbstractCanvasHandler> {

    // Extending BasicShapesFactoryImpl brings the BasicShapesFactory interface into this classes definition. CDI
    // consequentially finds two implementations of BasicShapesFactory when initialising injection points for
    // the generated ShapeFactoryWrapper implementations which have @Default @Any qualifiers.
    private final BasicShapesFactoryImpl delegate;

    private final DefinitionManager definitionManager;

    @Inject
    public CaseManagementShapesFactoryImpl(final BasicShapesFactoryImpl delegate,
                                           final DefinitionManager definitionManager) {
        this.delegate = delegate;
        this.definitionManager = definitionManager;
    }

    @Override
    public void addShapeDef(final Class<?> clazz,
                            final ShapeDef<Object> def) {
        delegate.addShapeDef(clazz,
                             def);
    }

    @Override
    public boolean accepts(final String definitionId) {
        return delegate.accepts(definitionId);
    }

    @Override
    public String getDescription(final String definitionId) {
        return delegate.getDescription(definitionId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Shape<ShapeView> build(final Object definition,
                                  final AbstractCanvasHandler context) {
        final String id = definitionManager.adapters().forDefinition().getId(definition);
        final ShapeDef<?> proxy = delegate.getShapeDef(id);

        Shape<ShapeView> shape;

        if (isCaseManagementDiagram(proxy)) {
            final CaseManagementDiagramShapeDef diagramProxy = (CaseManagementDiagramShapeDef) proxy;
            final double width = diagramProxy.getWidth((CaseManagementDiagram) definition);
            final double height = diagramProxy.getHeight((CaseManagementDiagram) definition);
            final DiagramView view = new DiagramView(width,
                                                     height);
            shape = new DiagramShape(diagramProxy,
                                     view);
        } else if (isCaseManagementStage(proxy)) {
            final CaseManagementSubprocessShapeDef stageProxy = (CaseManagementSubprocessShapeDef) proxy;
            final double width = stageProxy.getWidth((CaseManagementBaseSubprocess) definition);
            final double height = stageProxy.getHeight((CaseManagementBaseSubprocess) definition);
            final double voffset = stageProxy.getVOffset((CaseManagementBaseSubprocess) definition);
            final StageView view = new StageView(width,
                                                 height,
                                                 voffset);
            shape = new StageShape(stageProxy,
                                   view);
        } else if (isCaseManagementActivity(proxy)) {
            final CaseManagementTaskShapeDef taskProxy = (CaseManagementTaskShapeDef) proxy;
            final double width = taskProxy.getWidth((CaseManagementBaseTask) definition);
            final double height = taskProxy.getHeight((CaseManagementBaseTask) definition);
            final ActivityView view = new ActivityView(width,
                                                       height);
            shape = new ActivityShape(taskProxy,
                                      view);
        } else {
            shape = delegate.build(definition,
                                   context);
        }
        return shape;
    }

    @Override
    public Glyph glyph(final String definitionId,
                       final double width,
                       final double height) {
        return delegate.glyph(definitionId,
                              width,
                              height);
    }

    private boolean isCaseManagementDiagram(final ShapeDef<?> proxy) {
        return proxy instanceof CaseManagementDiagramShapeDef;
    }

    private boolean isCaseManagementStage(final ShapeDef<?> proxy) {
        return proxy instanceof CaseManagementSubprocessShapeDef;
    }

    private boolean isCaseManagementActivity(final ShapeDef<?> proxy) {
        return proxy instanceof CaseManagementTaskShapeDef;
    }
}
