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

package org.kie.workbench.common.stunner.cm.client.shape.factory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.safehtml.shared.SafeUri;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDefinition;
import org.kie.workbench.common.stunner.cm.client.shape.ActivityShape;
import org.kie.workbench.common.stunner.cm.client.shape.DiagramShape;
import org.kie.workbench.common.stunner.cm.client.shape.NullShape;
import org.kie.workbench.common.stunner.cm.client.shape.StageShape;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementActivityShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementDiagramShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementReusableSubprocessTaskShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementSubprocessShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.CaseManagementTaskShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.NullShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.def.StageShapeDef;
import org.kie.workbench.common.stunner.cm.client.shape.view.ActivityView;
import org.kie.workbench.common.stunner.cm.client.shape.view.DiagramView;
import org.kie.workbench.common.stunner.cm.client.shape.view.NullView;
import org.kie.workbench.common.stunner.cm.client.shape.view.StageView;
import org.kie.workbench.common.stunner.cm.definition.CaseManagementDiagram;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFactory;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeDefFunctionalFactory;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.shapes.client.view.PictureShapeView;
import org.kie.workbench.common.stunner.shapes.client.view.ShapeViewFactory;

@Dependent
public class CaseManagementShapeDefFactory implements ShapeDefFactory<BPMNDefinition, CaseManagementShapeDef, Shape> {

    private final CaseManagementShapeViewFactory cmShapeViewFactory;
    private final ShapeViewFactory basicShapeViewFactory;
    private final ShapeDefFunctionalFactory<BPMNDefinition, CaseManagementShapeDef, Shape> functionalFactory;

    protected CaseManagementShapeDefFactory() {
        this(null,
             null,
             null);
    }

    @Inject
    public CaseManagementShapeDefFactory(final CaseManagementShapeViewFactory cmShapeViewFactory,
                                         final ShapeViewFactory basicShapeViewFactory,
                                         final ShapeDefFunctionalFactory<BPMNDefinition, CaseManagementShapeDef, Shape> functionalFactory) {
        this.functionalFactory = functionalFactory;
        this.basicShapeViewFactory = basicShapeViewFactory;
        this.cmShapeViewFactory = cmShapeViewFactory;
    }

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        // Register shape instance builders.
        functionalFactory
                .set(NullShapeDef.class,
                     this::newNullShape)
                .set(CaseManagementDiagramShapeDef.class,
                     this::newDiagramShape)
                .set(CaseManagementSubprocessShapeDef.class,
                     this::newStageShape)
                .set(CaseManagementTaskShapeDef.class,
                     this::newActivityShape)
                .set(CaseManagementReusableSubprocessTaskShapeDef.class,
                     this::newActivityShape);
    }

    @SuppressWarnings("unchecked")
    private Shape newNullShape(final Object instance,
                               final ShapeDef shapeDef) {
        NullShapeDef nullShapeDef = (NullShapeDef) shapeDef;
        final NullView view = cmShapeViewFactory.newNullView();
        return new NullShape(nullShapeDef,
                             view);
    }

    @SuppressWarnings("unchecked")
    private Shape newDiagramShape(final Object instance,
                                  final ShapeDef shapeDef) {
        final CaseManagementDiagram diagram = (CaseManagementDiagram) instance;
        final CaseManagementDiagramShapeDef cmShapeDef = (CaseManagementDiagramShapeDef) shapeDef;
        final double width = cmShapeDef.getWidth(diagram);
        final double height = cmShapeDef.getHeight(diagram);
        final DiagramView view = cmShapeViewFactory.newDiagramView(width,
                                                                   height);
        return new DiagramShape(cmShapeDef,
                                view);
    }

    @SuppressWarnings("unchecked")
    private Shape newStageShape(final Object instance,
                                final ShapeDef shapeDef) {
        final BPMNDefinition bpmnDefinition = (BPMNDefinition) instance;
        final StageShapeDef cmShapeDef = (StageShapeDef) shapeDef;
        final double width = cmShapeDef.getWidth(bpmnDefinition);
        final double height = cmShapeDef.getHeight(bpmnDefinition);
        final double voffset = cmShapeDef.getVOffset(bpmnDefinition);
        final StageView view = cmShapeViewFactory.newStageView(width,
                                                               height,
                                                               voffset);
        return new StageShape<>(cmShapeDef,
                                view);
    }

    @SuppressWarnings("unchecked")
    private Shape newActivityShape(final Object instance,
                                   final ShapeDef shapeDef) {
        final CaseManagementActivityShapeDef cmShapeDef = (CaseManagementActivityShapeDef) shapeDef;
        final double width = cmShapeDef.getWidth(instance);
        final double height = cmShapeDef.getHeight(instance);
        final ActivityView view = cmShapeViewFactory.newActivityView(width,
                                                                     height);
        final SafeUri iconUri = cmShapeDef.getIconUri(instance.getClass());
        final PictureShapeView iconView = basicShapeViewFactory.pictureFromUri(iconUri,
                                                                               15d,
                                                                               15d);
        return new ActivityShape(cmShapeDef,
                                 iconView,
                                 view);
    }

    @Override
    public Shape newShape(final BPMNDefinition instance,
                          final CaseManagementShapeDef shapeDef) {
        return functionalFactory.newShape(instance,
                                          shapeDef);
    }
}
