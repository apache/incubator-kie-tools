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
package org.kie.workbench.common.stunner.cm.client.shape.factory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.BaseSubprocess;
import org.kie.workbench.common.stunner.bpmn.definition.BaseTask;
import org.kie.workbench.common.stunner.cm.client.shape.ActivityShape;
import org.kie.workbench.common.stunner.cm.client.shape.DiagramShape;
import org.kie.workbench.common.stunner.cm.client.shape.NullShape;
import org.kie.workbench.common.stunner.cm.client.shape.StageShape;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementDiagramShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSubprocessShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementTaskShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.NullShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.view.ActivityView;
import org.kie.workbench.common.stunner.cm.client.shape.view.DiagramView;
import org.kie.workbench.common.stunner.cm.client.shape.view.NullView;
import org.kie.workbench.common.stunner.cm.client.shape.view.StageView;
import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.AbstractShapeDefFactory;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.Glyph;
import org.kie.workbench.common.stunner.core.client.shape.view.glyph.GlyphBuilderFactory;
import org.kie.workbench.common.stunner.core.definition.shape.GlyphDef;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;

@ApplicationScoped
public class CaseManagementShapesFactoryImpl extends
                                             AbstractShapeDefFactory<Object, ShapeView, Shape<ShapeView>, ShapeDef<Object>>
        implements CaseManagementShapesFactory<Object, AbstractCanvasHandler> {

    private final DefinitionManager definitionManager;
    private final GlyphBuilderFactory glyphBuilderFactory;

    @Inject
    public CaseManagementShapesFactoryImpl(final FactoryManager factoryManager,
                                           final DefinitionManager definitionManager,
                                           final GlyphBuilderFactory glyphBuilderFactory) {
        super(definitionManager,
              factoryManager);
        this.definitionManager = definitionManager;
        this.glyphBuilderFactory = glyphBuilderFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Shape<ShapeView> build(final Object definition,
                                  final AbstractCanvasHandler context) {
        final String id = definitionManager.adapters().forDefinition().getId(definition);
        final ShapeDef<?> proxy = getShapeDef(id);

        boolean found = false;
        Shape<? extends ShapeView> shape = null;

        if (isNullShape(proxy)) {
            found = true;
            final NullShapeDef nullProxy = (NullShapeDef) proxy;
            final NullView view = new NullView();
            shape = new NullShape(nullProxy,
                                  view);
        } else if (isCaseManagementDiagram(proxy)) {
            found = true;
            final CaseManagementDiagramShapeDef diagramProxy = (CaseManagementDiagramShapeDef) proxy;
            final double width = diagramProxy.getWidth((BPMNDiagram) definition);
            final double height = diagramProxy.getHeight((BPMNDiagram) definition);
            final DiagramView view = new DiagramView(width,
                                                     height);
            shape = new DiagramShape(diagramProxy,
                                     view);
        } else if (isCaseManagementStage(proxy)) {
            found = true;
            final CaseManagementSubprocessShapeDef stageProxy = (CaseManagementSubprocessShapeDef) proxy;
            final double width = stageProxy.getWidth((BaseSubprocess) definition);
            final double height = stageProxy.getHeight((BaseSubprocess) definition);
            final double voffset = stageProxy.getVOffset((BaseSubprocess) definition);
            final StageView view = new StageView(width,
                                                 height,
                                                 voffset);
            shape = new StageShape(stageProxy,
                                   view);
        } else if (isCaseManagementActivity(proxy)) {
            found = true;
            final CaseManagementTaskShapeDef taskProxy = (CaseManagementTaskShapeDef) proxy;
            final double width = taskProxy.getWidth((BaseTask) definition);
            final double height = taskProxy.getHeight((BaseTask) definition);
            final ActivityView view = new ActivityView(width,
                                                       height);
            shape = new ActivityShape(taskProxy,
                                      view);
        }

        if (!found) {
            throw new RuntimeException("This factory supports [" + id + "] but cannot built a shape for it.");
        }

        return (Shape<ShapeView>) shape;
    }

    private boolean isNullShape(final ShapeDef<?> proxy) {
        return proxy instanceof NullShapeDef;
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

    @Override
    @SuppressWarnings("unchecked")
    protected Glyph glyph(final Class<?> clazz,
                          final double width,
                          final double height) {
        final ShapeDef<Object> shapeDef = getShapeDef(clazz);
        final GlyphDef<Object> glyphDef = shapeDef.getGlyphDef();
        return glyphBuilderFactory
                .getBuilder(glyphDef)
                .definitionType(clazz)
                .glyphDef(glyphDef)
                .factory(this)
                .height(height)
                .width(width)
                .build();
    }
}
